import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

//db class handles database conection and queries
public class DB implements AutoCloseable { 
    private Connection conn;

    //opens the db conection
    public DB(String url, String user, String password) {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            conn = DriverManager.getConnection(url, user, password);
            conn.setAutoCommit(true);
            System.out.println("Connected to Oracle.");
        } catch (ClassNotFoundException e) {
            System.out.println("Oracle JDBC driver was not found.");
            System.out.println("Add ojdbc11.jar to the Java runtime classpath.");
        } catch (SQLException e) {
            System.out.println("Could not connect to Oracle.");
            System.out.println(e.getMessage());
        }
    }

    //checks if the db is connected
    public boolean isConnected() {
        return conn != null;
    }

    //gets all store locations
    public List<Location> getLocations() {
        List<Location> locations = new ArrayList<>();
        String sql =
            "select loc_id, addr, city, sales_tax_rate " +
            "from location order by loc_id";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Location location = new Location();
                location.id = rs.getInt("loc_id");
                location.address = rs.getString("addr");
                location.city = rs.getString("city");
                location.taxRate = rs.getDouble("sales_tax_rate");
                locations.add(location);
            }
        } catch (SQLException e) {
            printError("Could not load locations.", e);
        }

        return locations;
    }

    //gets menu items for one location
    public List<MenuItem> getMenuForLocation(int locId) {
        List<MenuItem> items = new ArrayList<>();
        String sql =
            "select m.item_id, m.item_nm, m.mtype, " +
            "coalesce(mp.location_price, m.suggested_price) as price " +
            "from menuitem m " +
            "left join menu_price mp " +
            "on m.item_id = mp.menu_item_id " +
            "and mp.location_id = ? " +
            "where upper(nvl(m.mtype, '')) <> 'DELETED' " +
            "order by m.item_id";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, locId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    MenuItem item = new MenuItem();
                    item.id = rs.getInt("item_id");
                    item.name = rs.getString("item_nm");
                    item.category = rs.getString("mtype");
                    item.price = rs.getDouble("price");
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            printError("Could not load menu items.", e);
        }

        return items;
    }

    //gets all menu items
    public List<MenuItem> getAllMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        String sql =
            "select item_id, item_nm, mtype, suggested_price " +
            "from menuitem " +
            "where upper(nvl(mtype, '')) <> 'DELETED' " +
            "order by item_id";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                MenuItem item = new MenuItem();
                item.id = rs.getInt("item_id");
                item.name = rs.getString("item_nm");
                item.category = rs.getString("mtype");
                item.price = rs.getDouble("suggested_price");
                items.add(item);
            }
        } catch (SQLException e) {
            printError("Could not load menu items.", e);
        }

        return items;
    }

    //gets all customers
    public List<Customer> getCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql =
            "select cust_id, cust_nm, phone " +
            "from customer order by cust_id";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Customer customer = new Customer();
                customer.id = rs.getInt("cust_id");
                customer.name = rs.getString("cust_nm");
                customer.phone = rs.getString("phone");
                customers.add(customer);
            }
        } catch (SQLException e) {
            printError("Could not load customers.", e);
        }

        return customers;
    }

    //gets cards for one customer
    public List<Card> getCardsForCustomer(int custId) {
        List<Card> cards = new ArrayList<>();
        String sql =
            "select card_id, cnum, ctype, exp_dt " +
            "from paymentcard where cust_id = ? order by card_id";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, custId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Card card = new Card();
                    card.id = rs.getInt("card_id");
                    card.number = rs.getString("cnum");
                    card.type = rs.getString("ctype");
                    card.expDate = String.valueOf(rs.getDate("exp_dt"));
                    cards.add(card);
                }
            }
        } catch (SQLException e) {
            printError("Could not load payment cards.", e);
        }

        return cards;
    }

    //gets all inventory items
    public List<InventoryItem> getInventoryItems() {
        List<InventoryItem> items = new ArrayList<>();
        String sql =
            "select inv_id, inv_nm, inv_cost " +
            "from inventoryitem order by inv_id";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                InventoryItem item = new InventoryItem();
                item.id = rs.getInt("inv_id");
                item.name = rs.getString("inv_nm");
                item.cost = rs.getDouble("inv_cost");
                items.add(item);
            }
        } catch (SQLException e) {
            printError("Could not load inventory items.", e);
        }

        return items;
    }

    //adds a new saved card
    public int addCardForCustomer(
        int custId,
        String cardNumber,
        Date expDate,
        String cardType,
        int cvc
    ) {
        if (conn == null) {
            return -1;
        }

        try {
            conn.setAutoCommit(false);
            lockPaycardTable();

            int cardId = nextPaycardId();
            String sql =
                "insert into paymentcard " +
                "(card_id, cnum, exp_dt, ctype, cvc, cust_id) " +
                "values (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, cardId);
                stmt.setString(2, cardNumber);
                stmt.setDate(3, expDate);
                stmt.setString(4, cardType);
                stmt.setInt(5, cvc);
                stmt.setInt(6, custId);
                stmt.executeUpdate();
            }

            conn.commit();
            conn.setAutoCommit(true);
            return cardId;
        } catch (SQLException e) {
            rollbackQuietly();
            printError("Could not save the new card.", e);
            return -1;
        }
    }

    //places a full order in one transaction
    public int placeOrder(
        int locId,
        int custId,
        int cardId,
        boolean online,
        Timestamp pickupTs,
        Integer tableNum,
        List<OrderLine> lines
    ) {
        if (conn == null) {
            return -1;
        }

        try {
            conn.setAutoCommit(false);
            lockOrderTables();

            Integer acctId = findAccountId(custId);
            if (acctId == null) {
                System.out.println("No account was found for that customer.");
                rollbackQuietly();
                return -1;
            }

            if (!cardBelongsToCustomer(cardId, custId)) {
                System.out.println("That card does not belong to the customer.");
                rollbackQuietly();
                return -1;
            }

            double taxRate = getLocationTaxRate(locId);
            int orderId = nextOrderId();

            insertOrder(orderId, custId, acctId, cardId, locId);

            if (online) {
                insertOnlineOrder(orderId, pickupTs);
            } else {
                insertInPersonOrder(orderId, tableNum);
            }

            double subtotal = 0.0;
            int orderItemId = nextOrderItemId();

            for (OrderLine line : lines) {
                double taxAmount = line.quantity
                    * line.unitPrice
                    * taxRate / 100.0;
                taxAmount = roundMoney(taxAmount);
                subtotal += line.quantity * line.unitPrice;

                insertOrderItem(orderItemId, orderId, line, taxAmount);
                orderItemId++;
            }

            addPoints(acctId, subtotal);

            conn.commit();
            conn.setAutoCommit(true);
            return orderId;
        } catch (SQLException e) {
            rollbackQuietly();
            printError("Could not save the order.", e);
            return -1;
        }
    }

    //calcualtes the suggested custom item price
    public double getSuggestedCustomItemPrice(
        List<ComponentLine> components,
        List<SubitemLine> subitems
    ) {
        if (conn == null) {
            return -1.0;
        }

        try {
            double suggestedPrice =
                computeComponentCost(components) + computeSubitemCost(subitems);
            suggestedPrice = suggestedPrice * 3.0;
            return roundMoney(suggestedPrice);
        } catch (SQLException e) {
            printError("Could not calculate the suggested price.", e);
            return -1.0;
        }
    }

    //creates a custom menu item
    public int createCustomMenuItem(
        String itemName,
        String category,
        double finalPrice,
        List<ComponentLine> components,
        List<SubitemLine> subitems
    ) {
        if (conn == null) {
            return -1;
        }

        if (components.isEmpty() && subitems.isEmpty()) {
            System.out.println("A custom item needs at least one component.");
            return -1;
        }

        try {
            conn.setAutoCommit(false);
            lockMenuTables();

            int itemId = nextMenuItemId();

            String insertMenu =
                "insert into menuitem " +
                "(item_id, item_nm, suggested_price, mtype) " +
                "values (?, ?, ?, ?)";

            try (PreparedStatement stmt = conn.prepareStatement(insertMenu)) {
                stmt.setInt(1, itemId);
                stmt.setString(2, itemName);
                stmt.setDouble(3, roundMoney(finalPrice));
                stmt.setString(4, category);
                stmt.executeUpdate();
            }

            insertMenuComponents(itemId, components);
            insertMenuSubitems(itemId, subitems);

            conn.commit();
            conn.setAutoCommit(true);
            return itemId;
        } catch (SQLException e) {
            rollbackQuietly();
            printError("Could not create the custom item.", e);
            return -1;
        }
    }

    //gets custom menu items
    public List<MenuItem> getCustomMenuItems() {
        List<MenuItem> items = new ArrayList<>();
        String sql =
            "select item_id, item_nm, mtype, suggested_price " +
            "from menuitem " +
            "where item_id >= 16 " +
            "and upper(nvl(mtype, '')) <> 'DELETED' " +
            "order by item_id";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                MenuItem item = new MenuItem();
                item.id = rs.getInt("item_id");
                item.name = rs.getString("item_nm");
                item.category = rs.getString("mtype");
                item.price = rs.getDouble("suggested_price");
                items.add(item);
            }
        } catch (SQLException e) {
            printError("Could not load custom menu items.", e);
        }

        return items;
    }

    //deletes a custom menu item
    public boolean deleteCustomMenuItem(int itemId) {
        if (conn == null) {
            return false;
        }

        if (itemId < 16) {
            System.out.println("Only custom items can be deleted.");
            return false;
        }

        try {
            conn.setAutoCommit(false);
            lockMenuTables();
            softDeleteMenuItem(itemId);

            conn.commit();
            conn.setAutoCommit(true);
            return true;
        } catch (SQLException e) {
            rollbackQuietly();
            printError("Could not delete the custom item.", e);
            return false;
        }
    }

    //prints sales by location
    public void printSalesByLocation() {
        String sql =
            "select l.loc_id, l.city, count(distinct o.order_id) as orders, " +
            "nvl(sum(oi.quantity * oi.unit_price + oi.tax_amount), 0) " +
            "as total_sales " +
            "from location l " +
            "left join orders o on l.loc_id = o.location_id " +
            "left join orderitem oi on o.order_id = oi.order_id " +
            "group by l.loc_id, l.city " +
            "order by l.loc_id";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            System.out.println();
            System.out.println("Sales by location");

            while (rs.next()) {
                System.out.printf("%d - %s - orders: %d - $%.2f%n",
                    rs.getInt("loc_id"),
                    rs.getString("city"),
                    rs.getInt("orders"),
                    rs.getDouble("total_sales"));
            }
        } catch (SQLException e) {
            printError("Could not run the sales report.", e);
        }
    }

    //prints top selling items
    public void printTopSellingItems() {
        String sql =
            "select oi.menu_item_id as item_id, " +
            "coalesce(m.item_nm, '(item does not exist anymore)') as item_nm, " +
            "nvl(sum(oi.quantity), 0) as total_qty " +
            "from orderitem oi " +
            "left join menuitem m on oi.menu_item_id = m.item_id " +
            "group by oi.menu_item_id, coalesce(m.item_nm, '(item does not exist anymore)') " +
            "order by total_qty desc, oi.menu_item_id";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            System.out.println();
            System.out.println("Top selling menu items");

            while (rs.next()) {
                System.out.printf("%d - %s - %d sold%n",
                    rs.getInt("item_id"),
                    rs.getString("item_nm"),
                    rs.getInt("total_qty"));
            }
        } catch (SQLException e) {
            printError("Could not run the top items report.", e);
        }
    }

    //prints customer points and order counts
    public void printCustomerHistory() {
        String sql =
            "select c.cust_id, c.cust_nm, nvl(a.pts, 0) as points, " +
            "count(distinct o.order_id) as order_count " +
            "from customer c " +
            "left join account a on c.cust_id = a.cust_id " +
            "left join orders o on c.cust_id = o.customer_id " +
            "group by c.cust_id, c.cust_nm, a.pts " +
            "order by c.cust_id";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            System.out.println();
            System.out.println("Customer points and order history");

            while (rs.next()) {
                System.out.printf("%d - %s - points: %d - orders: %d%n",
                    rs.getInt("cust_id"),
                    rs.getString("cust_nm"),
                    rs.getInt("points"),
                    rs.getInt("order_count"));
            }
        } catch (SQLException e) {
            printError("Could not run the customer report.", e);
        }
    }

    //gets orders for one location
    public List<OrderSummary> getOrdersForLocation(int locId) {
        List<OrderSummary> orders = new ArrayList<>();
        String sql =
            "select o.order_id, o.order_timestamp, c.cust_nm, " +
            "oo.pickup_timestamp, io.table_number " +
            "from orders o " +
            "join customer c on o.customer_id = c.cust_id " +
            "left join onlineorder oo on o.order_id = oo.order_id " +
            "left join inpersonorder io on o.order_id = io.order_id " +
            "where o.location_id = ? " +
            "order by o.order_id";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, locId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    OrderSummary order = new OrderSummary();
                    order.orderId = rs.getInt("order_id");
                    order.orderTs = rs.getTimestamp("order_timestamp");
                    order.customerName = rs.getString("cust_nm");
                    order.pickupTs = rs.getTimestamp("pickup_timestamp");

                    int tableNum = rs.getInt("table_number");
                    if (rs.wasNull()) {
                        order.tableNum = null;
                        order.orderType = "ONLINE";
                    } else {
                        order.tableNum = tableNum;
                        order.orderType = "IN_PERSON";
                    }

                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            printError("Could not load local orders.", e);
        }

        return orders;
    }

    //gets the items for one order
    public List<OrderDetail> getOrderDetails(int orderId) {
        List<OrderDetail> details = new ArrayList<>();
        String sql =
            "select oi.menu_item_id, " +
            "coalesce(m.item_nm, '(item does not exist anymore)') as item_nm, oi.quantity, " +
            "oi.unit_price, oi.tax_amount " +
            "from orderitem oi " +
            "left join menuitem m on oi.menu_item_id = m.item_id " +
            "where oi.order_id = ? " +
            "order by oi.order_item_id";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    OrderDetail detail = new OrderDetail();
                    detail.itemId = rs.getInt("menu_item_id");
                    if (rs.wasNull()) {
                        detail.itemId = -1;
                    }
                    detail.itemName = rs.getString("item_nm");
                    detail.quantity = rs.getInt("quantity");
                    detail.unitPrice = rs.getDouble("unit_price");
                    detail.taxAmount = rs.getDouble("tax_amount");
                    details.add(detail);
                }
            }
        } catch (SQLException e) {
            printError("Could not load order details.", e);
        }

        return details;
    }

    //finds the account for a customer
    private Integer findAccountId(int custId) throws SQLException {
        String sql =
            "select acct_id from account where cust_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, custId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("acct_id");
                }
            }
        }

        return null;
    }

    //checks that the card matches the customer
    private boolean cardBelongsToCustomer(int cardId, int custId)
        throws SQLException {
        String sql =
            "select count(*) from paymentcard " +
            "where card_id = ? and cust_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cardId);
            stmt.setInt(2, custId);

            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1) == 1;
            }
        }
    }

    //gets the tax rate for a location
    private double getLocationTaxRate(int locId) throws SQLException {
        String sql =
            "select sales_tax_rate from location where loc_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, locId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("sales_tax_rate");
                }
            }
        }

        return 0.0;
    }

    //gets the next order id
    private int nextOrderId() throws SQLException {
        return nextIdFromSql(
            "select nvl(max(order_id), 0) + 1 as next_id from orders"
        );
    }

    //gets the next order item id
    private int nextOrderItemId() throws SQLException {
        return nextIdFromSql(
            "select nvl(max(order_item_id), 0) + 1 as next_id from orderitem"
        );
    }

    //gets the next paycard id
    private int nextPaycardId() throws SQLException {
        return nextIdFromSql(
            "select nvl(max(card_id), 0) + 1 as next_id " +
            "from paymentcard"
        );
    }

    //gets the next menu item id
    private int nextMenuItemId() throws SQLException {
        return nextIdFromSql(
            "select nvl(max(item_id), 0) + 1 as next_id from menuitem"
        );
    }

    //runs a max id query
    private int nextIdFromSql(String sql) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            rs.next();
            return rs.getInt("next_id");
        }
    }

    //locks order tables for updates
    private void lockOrderTables() throws SQLException {
        executeLock("lock table orders in exclusive mode");
        executeLock("lock table orderitem in exclusive mode");
        executeLock("lock table onlineorder in exclusive mode");
        executeLock("lock table inpersonorder in exclusive mode");
    }

    //locks menu tables for updates
    private void lockMenuTables() throws SQLException {
        executeLock("lock table menuitem in exclusive mode");
        executeLock("lock table menu_component in exclusive mode");
        executeLock("lock table menu_subitem in exclusive mode");
    }

    //locks the paycard table
    private void lockPaycardTable() throws SQLException {
        executeLock("lock table paymentcard in exclusive mode");
    }

    //runs one lock statement
    private void executeLock(String sql) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.execute();
        }
    }

    //inserts the main order row
    private void insertOrder(
        int orderId,
        int custId,
        int acctId,
        int cardId,
        int locId
    ) throws SQLException {
        String sql =
            "insert into orders " +
            "(order_id, order_timestamp, customer_id, " +
            "account_id, card_id, location_id) " +
            "values (?, current_timestamp, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            stmt.setInt(2, custId);
            stmt.setInt(3, acctId);
            stmt.setInt(4, cardId);
            stmt.setInt(5, locId);
            stmt.executeUpdate();
        }
    }

    //inserts the online order row
    private void insertOnlineOrder(int orderId, Timestamp pickupTs)
        throws SQLException {
        String sql =
            "insert into onlineorder (order_id, pickup_timestamp) " +
            "values (?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            stmt.setTimestamp(2, pickupTs);
            stmt.executeUpdate();
        }
    }

    //inserts the in person order row
    private void insertInPersonOrder(int orderId, Integer tableNum)
        throws SQLException {
        String sql =
            "insert into inpersonorder (order_id, table_number) " +
            "values (?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            stmt.setInt(2, tableNum);
            stmt.executeUpdate();
        }
    }

    //inserts one order item row
    private void insertOrderItem(
        int orderItemId,
        int orderId,
        OrderLine line,
        double taxAmount
    ) throws SQLException {
        String sql =
            "insert into orderitem " +
            "(order_item_id, order_id, menu_item_id, " +
            "quantity, unit_price, tax_amount) " +
            "values (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderItemId);
            stmt.setInt(2, orderId);
            stmt.setInt(3, line.itemId);
            stmt.setInt(4, line.quantity);
            stmt.setDouble(5, line.unitPrice);
            stmt.setDouble(6, taxAmount);
            stmt.executeUpdate();
        }
    }

    //adds account points
    private void addPoints(int acctId, double subtotal) throws SQLException {
        int pointsToAdd = (int) Math.round(subtotal);
        String sql =
            "update account set pts = nvl(pts, 0) + ? where acct_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, pointsToAdd);
            stmt.setInt(2, acctId);
            stmt.executeUpdate();
        }
    }

    //adds up inventory component cost
    private double computeComponentCost(List<ComponentLine> components)
        throws SQLException {
        double total = 0.0;
        String sql =
            "select inv_cost from inventoryitem where inv_id = ?";

        for (ComponentLine component : components) {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, component.invId);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        total += rs.getDouble("inv_cost")
                            * component.quantity;
                    }
                }
            }
        }

        return total;
    }

    //adds up subitem cost
    private double computeSubitemCost(List<SubitemLine> subitems)
        throws SQLException {
        double total = 0.0;
        String sql =
            "select suggested_price from menuitem where item_id = ?";

        for (SubitemLine subitem : subitems) {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, subitem.childItemId);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        total += rs.getDouble("suggested_price")
                            * subitem.quantity;
                    }
                }
            }
        }

        return total;
    }

    //inserts menu component rows
    private void insertMenuComponents(
        int itemId,
        List<ComponentLine> components
    ) throws SQLException {
        String sql =
            "insert into menu_component " +
            "(menu_item_id, inventory_id, quantity) " +
            "values (?, ?, ?)";

        for (ComponentLine component : components) {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, itemId);
                stmt.setInt(2, component.invId);
                stmt.setDouble(3, component.quantity);
                stmt.executeUpdate();
            }
        }
    }

    //inserts menu subitem rows
    private void insertMenuSubitems(
        int itemId,
        List<SubitemLine> subitems
    ) throws SQLException {
        String sql =
            "insert into menu_subitem " +
            "(parent_menu_item_id, child_menu_item_id, quantity) " +
            "values (?, ?, ?)";

        for (SubitemLine subitem : subitems) {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, itemId);
                stmt.setInt(2, subitem.childItemId);
                stmt.setDouble(3, subitem.quantity);
                stmt.executeUpdate();
            }
        }
    }

    //marks a custom item as deleted
    private void softDeleteMenuItem(int itemId) throws SQLException {
        String sql =
            "update menuitem " +
            "set item_nm = '(item does not exist anymore)', " +
            "mtype = 'DELETED' " +
            "where item_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, itemId);
            stmt.executeUpdate();
        }
    }

    //rolls back on error
    private void rollbackQuietly() {
        if (conn == null) {
            return;
        }

        try {
            conn.rollback();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            printError("Rollback failed.", e);
        }
    }

    //rounds money to cents
    private double roundMoney(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    //prints a db error
    private void printError(String message, SQLException e) {
        System.out.println(message);
        System.out.println(e.getMessage());
    }

    @Override
    //closes the db conection
    public void close() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.out.println("Could not close the connection.");
                System.out.println(e.getMessage());
            }
        }
    }


    //class declarations for data objects
    public static class Location {
        int id;
        String address;
        String city;
        double taxRate;
    }

    public static class MenuItem {
        int id;
        String name;
        String category;
        double price;
    }

    public static class Customer {
        int id;
        String name;
        String phone;
    }

    public static class Card {
        int id;
        String number;
        String type;
        String expDate;
    }

    public static class InventoryItem {
        int id;
        String name;
        double cost;
    }

    public static class OrderSummary {
        int orderId;
        Timestamp orderTs;
        String customerName;
        String orderType;
        Timestamp pickupTs;
        Integer tableNum;
    }

    public static class OrderDetail {
        int itemId;
        String itemName;
        int quantity;
        double unitPrice;
        double taxAmount;
    }

    public static class OrderLine {
        int itemId;
        String itemName;
        int quantity;
        double unitPrice;

        //stores one order line
        public OrderLine(
            int itemId,
            String itemName,
            int quantity,
            double unitPrice
        ) {
            this.itemId = itemId;
            this.itemName = itemName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }
    }

    public static class ComponentLine {
        int invId;
        double quantity;

        //stores one component line
        public ComponentLine(int invId, double quantity) {
            this.invId = invId;
            this.quantity = quantity;
        }
    }

    public static class SubitemLine {
        int childItemId;
        double quantity;

        //stores one subitem line
        public SubitemLine(int childItemId, double quantity) {
            this.childItemId = childItemId;
            this.quantity = quantity;
        }
    }
}
