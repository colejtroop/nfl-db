import java.util.Scanner;

//main class runs the whole program
public class Main {
    private static final String DEFAULT_JDBC_URL =
        "jdbc:oracle:thin:@//rocordb01.cse.lehigh.edu:1522/cse241pdb";

    //starts the app
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String user = InputUtil.readRequiredLine(
            scanner,
            "Oracle username: "
        );
        if (user == null) {
            System.out.println("No input provided. Exiting.");
            return;
        }

        String password = InputUtil.readPassword(scanner);
        if (password == null) {
            System.out.println("No input provided. Exiting.");
            return;
        }

        try (DB db = new DB(DEFAULT_JDBC_URL, user, password)) {
            if (!db.isConnected()) {
                return;
            }

            runMainMenu(scanner, db);
        } catch (Exception e) {
            System.out.println("Could not start the program.");
            System.out.println(e.getMessage());
        }
    }

    //shows the main menu
    private static void runMainMenu(Scanner scanner, DB db) {
        boolean done = false;

        while (!done) {
            System.out.println();
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println("  Welcome to First Down!");
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println("1. Customer order");
            System.out.println("2. Management reports");
            System.out.println("3. Location manager");
            System.out.println("4. Custom menu item");
            System.out.println("0. Exit");

            Integer choice = InputUtil.readInt(scanner, "Choose an option: ");
            if (choice == null) {
                System.out.println("Exiting.");
                return;
            }

            if (choice == 1) {
                CustomerOrderUI.run(scanner, db);
            } else if (choice == 2) {
                ReportUI.run(scanner, db);
            } else if (choice == 3) {
                LocationManagerUI.run(scanner, db);
            } else if (choice == 4) {
                CustomMenuUI.run(scanner, db);
            } else if (choice == 0) {
                done = true;
            } else {
                System.out.println("Please choose 0, 1, 2, 3, or 4.");
            }
        }
    }
}
