import java.util.Scanner;

//report screen logic
public class ReportUI {
    //runs the reports ui
    public static void run(Scanner scanner, DB db) {
        boolean done = false;

        while (!done) {
            System.out.println();
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println("  First Down - Reports");
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println("1. Sales by location");
            System.out.println("2. Top selling menu items");
            System.out.println("3. Customer points and order history");
            System.out.println("0. Back");

            Integer choice = InputUtil.readInt(scanner, "Choose a report: ");
            if (choice == null) {
                return;
            }

            if (choice == 1) {
                db.printSalesByLocation();
            } else if (choice == 2) {
                db.printTopSellingItems();
            } else if (choice == 3) {
                db.printCustomerHistory();
            } else if (choice == 0) {
                done = true;
            } else {
                System.out.println("Please choose 0, 1, 2, or 3.");
            }
        }
    }
}
