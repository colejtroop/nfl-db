import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

//customer order screen logic
public class CustomerOrderUI {
    //runs the customer order ui
    public static void run(Scanner scanner, DB db) {
        System.out.println();
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("  First Down - Customer Order");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        List<DB.Location> locations = db.getLocations();
        if (locations.isEmpty()) {
            System.out.println("No locations were found.");
            return;
        }

        showLocations(locations);
        Integer locationId = InputUtil.readInt(scanner, "Location id: ");
        DB.Location location = findLocation(locations, locationId);
        if (location == null) {
            System.out.println("Order canceled.");
            return;
        }

        List<DB.MenuItem> menu = db.getMenuForLocation(location.id);
        if (menu.isEmpty()) {
            System.out.println("No menu items were found.");
            return;
        }

        showMenu(menu);

        List<DB.Customer> customers = db.getCustomers();
        if (customers.isEmpty()) {
            System.out.println("No customers were found.");
            return;
        }

        showCustomers(customers);
        Integer customerId = InputUtil.readInt(scanner, "Customer id: ");
        DB.Customer customer = findCustomer(customers, customerId);
        if (customer == null) {
            System.out.println("Order canceled.");
            return;
        }

        int cardId = chooseCard(scanner, db, customer.id);
        if (cardId < 1) {
            System.out.println("Order canceled.");
            return;
        }

        System.out.println();
        System.out.println("1. Online order");
        System.out.println("2. In-person order");

        Integer orderType = InputUtil.readInt(scanner, "Order type: ");
        if (orderType == null || (orderType != 1 && orderType != 2)) {
            System.out.println("Order canceled.");
            return;
        }

        Timestamp pickupTs = null;
        Integer tableNum = null;

        if (orderType == 1) {
            pickupTs = InputUtil.readPickupTimestamp(scanner);
            if (pickupTs == null) {
                System.out.println("Order canceled.");
                return;
            }
        } else {
            tableNum = InputUtil.readInt(scanner, "Table number (1-24): ");
            if (tableNum == null || tableNum < 1 || tableNum > 24) {
                System.out.println("Table number must be between 1 and 24.");
                return;
            }
        }

        showMenu(menu);
        List<DB.OrderLine> lines = readOrderLines(scanner, menu);
        if (lines.isEmpty()) {
            System.out.println("Order canceled. No items were added.");
            return;
        }

        printOrderSummary(lines, location, customer);
        boolean confirmed = InputUtil.readYesNo(
            scanner,
            "Place this order? (yes/no): "
        );
        if (!confirmed) {
            System.out.println("Order canceled.");
            return;
        }

        int orderId = db.placeOrder(
            location.id,
            customer.id,
            cardId,
            orderType == 1,
            pickupTs,
            tableNum,
            lines
        );

        if (orderId > 0) {
            System.out.println("Order saved with id " + orderId + ".");
        }
    }

    //prints the locations
    private static void showLocations(List<DB.Location> locations) {
        System.out.println();
        System.out.println("Locations");

        for (DB.Location location : locations) {
            System.out.printf("%d - %s - %s - tax %.2f%%%n",
                location.id,
                location.city,
                location.address,
                location.taxRate);
        }
    }

    //prints the menu
    private static void showMenu(List<DB.MenuItem> menu) {
        System.out.println();
        System.out.println("Menu");

        for (DB.MenuItem item : menu) {
            System.out.printf("%d - %s - %s - $%.2f%n",
                item.id,
                item.name,
                item.category,
                item.price);
        }
    }

    //prints the customers
    private static void showCustomers(List<DB.Customer> customers) {
        System.out.println();
        System.out.println("Customers");

        for (DB.Customer customer : customers) {
            System.out.println(customer.id + " - " + customer.name
                + " - " + customer.phone);
        }
    }

    //lets the user pick a card
    private static int chooseCard(Scanner scanner, DB db, int customerId) {
        List<DB.Card> cards = db.getCardsForCustomer(customerId);

        System.out.println();
        System.out.println("Saved cards");
        for (DB.Card card : cards) {
            System.out.println(card.id + " - " + card.type
                + " - " + card.number
                + " - exp " + card.expDate);
        }
        System.out.println("0 - Enter a new card");

        Integer cardChoice = InputUtil.readInt(scanner, "Card id: ");
        if (cardChoice == null) {
            return -1;
        }

        if (cardChoice == 0) {
            return addNewCard(scanner, db, customerId);
        }

        for (DB.Card card : cards) {
            if (card.id == cardChoice) {
                return card.id;
            }
        }

        System.out.println("That card was not found.");
        return -1;
    }

    //adds a new card
    private static int addNewCard(Scanner scanner, DB db, int customerId) {
        String cardNumber = InputUtil.readRequiredLine(
            scanner,
            "Card number: "
        );
        if (cardNumber == null) {
            return -1;
        }

        Date expDate = InputUtil.readDate(scanner, "Expiry date (YYYY-MM-DD): ");
        if (expDate == null) {
            return -1;
        }

        String cardType = InputUtil.readRequiredLine(scanner, "Card type: ");
        if (cardType == null) {
            return -1;
        }

        Integer cvc = InputUtil.readInt(scanner, "CVC: ");
        if (cvc == null || cvc < 0) {
            return -1;
        }

        return db.addCardForCustomer(
            customerId,
            cardNumber,
            expDate,
            cardType,
            cvc
        );
    }

    //reads the order lines
    private static List<DB.OrderLine> readOrderLines(
        Scanner scanner,
        List<DB.MenuItem> menu
    ) {
        List<DB.OrderLine> lines = new ArrayList<>();

        System.out.println();
        System.out.println("Enter menu items");

        while (true) {
            Integer itemId = InputUtil.readInt(
                scanner,
                "Menu item id (0 to finish): "
            );
            if (itemId == null) {
                return new ArrayList<>();
            }

            if (itemId == 0) {
                return lines;
            }

            DB.MenuItem item = findMenuItem(menu, itemId);

            if (item == null) {
                System.out.println("That menu item was not found.");
                continue;
            }

            Integer quantity = InputUtil.readInt(scanner, "Quantity: ");
            if (quantity == null || quantity <= 0) {
                System.out.println("Quantity must be at least 1.");
                continue;
            }

            lines.add(new DB.OrderLine(
                item.id,
                item.name,
                quantity,
                item.price
            ));
        }
    }

    //prints the order summary
    private static void printOrderSummary(
        List<DB.OrderLine> lines,
        DB.Location location,
        DB.Customer customer
    ) {
        double subtotal = 0.0;

        System.out.println();
        System.out.println("Order summary");
        System.out.println("Location: " + location.city);
        System.out.println("Customer: " + customer.name);

        for (DB.OrderLine line : lines) {
            double lineTotal = line.quantity * line.unitPrice;
            subtotal += lineTotal;

            System.out.printf("%s x%d - $%.2f%n",
                line.itemName,
                line.quantity,
                lineTotal);
        }

        System.out.printf("Subtotal: $%.2f%n", subtotal);
    }

    //finds one location by id
    private static DB.Location findLocation(
        List<DB.Location> locations,
        Integer locationId
    ) {
        if (locationId == null) {
            return null;
        }

        for (DB.Location location : locations) {
            if (location.id == locationId) {
                return location;
            }
        }

        return null;
    }

    //finds one customer by id
    private static DB.Customer findCustomer(
        List<DB.Customer> customers,
        Integer customerId
    ) {
        if (customerId == null) {
            return null;
        }

        for (DB.Customer customer : customers) {
            if (customer.id == customerId) {
                return customer;
            }
        }

        return null;
    }

    //finds one menu item by id
    private static DB.MenuItem findMenuItem(
        List<DB.MenuItem> menu,
        int itemId
    ) {
        for (DB.MenuItem item : menu) {
            if (item.id == itemId) {
                return item;
            }
        }

        return null;
    }
}
