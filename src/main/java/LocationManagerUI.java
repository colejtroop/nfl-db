import java.util.List;
import java.util.Scanner;

//location manager screen logic
public class LocationManagerUI {
    //runs the location manager ui
    public static void run(Scanner scanner, DB db) {
        System.out.println();
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("  First Down - Location Manager");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        List<DB.Location> locations = db.getLocations();
        if (locations.isEmpty()) {
            System.out.println("No locations were found.");
            return;
        }

        System.out.println();
        System.out.println("Location manager");
        for (DB.Location location : locations) {
            System.out.println(location.id + " - " + location.city
                + " - " + location.address);
        }

        Integer locationId = InputUtil.readInt(scanner, "Location id: ");
        if (locationId == null) {
            return;
        }

        List<DB.OrderSummary> orders = db.getOrdersForLocation(locationId);
        if (orders.isEmpty()) {
            System.out.println("No orders were found for that location.");
            return;
        }

        System.out.println();
        System.out.println("Orders for location " + locationId);
        for (DB.OrderSummary order : orders) {
            System.out.print(order.orderId + " - " + order.customerName
                + " - " + order.orderType
                + " - " + order.orderTs);

            if ("ONLINE".equals(order.orderType)) {
                System.out.print(" - pickup " + order.pickupTs);
            } else {
                System.out.print(" - table " + order.tableNum);
            }

            System.out.println();
        }

        Integer orderId = InputUtil.readInt(
            scanner,
            "Order id to view items (0 to go back): "
        );
        if (orderId == null || orderId == 0) {
            return;
        }

        List<DB.OrderDetail> details = db.getOrderDetails(orderId);
        if (details.isEmpty()) {
            System.out.println("No order items were found.");
            return;
        }

        System.out.println();
        System.out.println("Order details");
        for (DB.OrderDetail detail : details) {
            String itemLabel = detail.itemId > 0
                ? String.valueOf(detail.itemId)
                : "(deleted)";

            System.out.printf("%s - %s - qty %d - $%.2f - tax $%.2f%n",
                itemLabel,
                detail.itemName,
                detail.quantity,
                detail.unitPrice,
                detail.taxAmount);
        }
    }
}
