import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

//custom menu screen logic
public class CustomMenuUI {
    //runs the custom menu ui
    public static void run(Scanner scanner, DB db) {
        System.out.println();
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("  First Down - Custom Menu");
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("1. Create custom item");
        System.out.println("2. Delete custom item");

        Integer choice = InputUtil.readInt(scanner, "Option: ");
        if (choice == null) {
            return;
        }

        if (choice == 2) {
            deleteCustomItem(scanner, db);
            return;
        }

        if (choice != 1) {
            System.out.println("Custom menu item canceled.");
            return;
        }

        String itemName = InputUtil.readRequiredLine(
            scanner,
            "Custom item name: "
        );
        if (itemName == null) {
            System.out.println("Custom item canceled.");
            return;
        }

        showCategories(db.getAllMenuItems());
        String category = InputUtil.readRequiredLine(
            scanner,
            "Category (or Other): "
        );
        if (category == null) {
            System.out.println("Custom item canceled.");
            return;
        }

        if (category.equalsIgnoreCase("other")) {
            category = InputUtil.readRequiredLine(
                scanner,
                "Enter custom category: "
            );
            if (category == null) {
                System.out.println("Custom item canceled.");
                return;
            }
        }

        List<DB.InventoryItem> inventoryItems = db.getInventoryItems();
        if (inventoryItems.isEmpty()) {
            System.out.println("No inventory items were found.");
            return;
        }

        System.out.println();
        System.out.println("Inventory items");
        for (DB.InventoryItem item : inventoryItems) {
            System.out.printf("%d - %s - cost $%.2f%n",
                item.id,
                item.name,
                item.cost);
        }

        List<DB.ComponentLine> components = new ArrayList<>();
        while (true) {
            Integer invId = InputUtil.readInt(
                scanner,
                "Inventory id (0 to stop): "
            );
            if (invId == null) {
                System.out.println("Custom item canceled.");
                return;
            }

            if (invId == 0) {
                break;
            }

            Double qty = InputUtil.readDouble(scanner, "Quantity: ");
            if (qty == null || qty <= 0) {
                System.out.println("Quantity must be positive.");
                continue;
            }

            components.add(new DB.ComponentLine(invId, qty));
        }

        List<DB.SubitemLine> subitems = new ArrayList<>();
        boolean wantsSubitems = InputUtil.readYesNo(
            scanner,
            "Add existing menu items as subitems? (yes/no): "
        );

        if (wantsSubitems) {
            List<DB.MenuItem> menuItems = db.getAllMenuItems();

            System.out.println();
            System.out.println("Menu items");
            for (DB.MenuItem item : menuItems) {
                System.out.printf("%d - %s - $%.2f%n",
                    item.id,
                    item.name,
                    item.price);
            }

            while (true) {
                Integer childId = InputUtil.readInt(
                    scanner,
                    "Child item id (0 to stop): "
                );
                if (childId == null) {
                    System.out.println("Custom item canceled.");
                    return;
                }

                if (childId == 0) {
                    break;
                }

                Double qty = InputUtil.readDouble(scanner, "Quantity: ");
                if (qty == null || qty <= 0) {
                    System.out.println("Quantity must be positive.");
                    continue;
                }

                subitems.add(new DB.SubitemLine(childId, qty));
            }
        }

        double suggestedPrice = db.getSuggestedCustomItemPrice(
            components,
            subitems
        );
        if (suggestedPrice <= 0) {
            System.out.println("Custom item canceled.");
            return;
        }

        System.out.printf("Suggested price (3x cost): $%.2f%n", suggestedPrice);
        Double finalPrice = InputUtil.readDouble(
            scanner,
            "Final price: "
        );
        if (finalPrice == null || finalPrice <= 0) {
            System.out.println("Custom item canceled.");
            return;
        }

        int itemId = db.createCustomMenuItem(
            itemName,
            category,
            finalPrice,
            components,
            subitems
        );

        if (itemId > 0) {
            System.out.println("Custom item saved with id " + itemId + ".");
        }
    }

    //deletes one custom item
    private static void deleteCustomItem(Scanner scanner, DB db) {
        List<DB.MenuItem> customItems = db.getCustomMenuItems();
        if (customItems.isEmpty()) {
            System.out.println("No custom items were found.");
            return;
        }

        System.out.println();
        System.out.println("Custom items");
        for (DB.MenuItem item : customItems) {
            System.out.printf("%d - %s - %s%n",
                item.id,
                item.name,
                item.category);
        }

        Integer itemId = InputUtil.readInt(
            scanner,
            "Custom item id to delete (0 to cancel): "
        );
        if (itemId == null || itemId == 0) {
            System.out.println("Delete canceled.");
            return;
        }

        if (itemId < 16) {
            System.out.println("Only custom items with id 16 or higher can be deleted.");
            return;
        }

        boolean confirmed = InputUtil.readYesNo(
            scanner,
            "Delete this custom item? (yes/no): "
        );
        if (!confirmed) {
            System.out.println("Delete canceled.");
            return;
        }

        if (db.deleteCustomMenuItem(itemId)) {
            System.out.println("Custom item deleted.");
        }
    }

    //prints existing categories
    private static void showCategories(List<DB.MenuItem> menuItems) {
        Set<String> categories = new LinkedHashSet<>();

        for (DB.MenuItem item : menuItems) {
            if (item.category != null && !item.category.isBlank()) {
                categories.add(item.category);
            }
        }

        if (categories.isEmpty()) {
            return;
        }

        System.out.println();
        System.out.println("Existing categories");
        for (String category : categories) {
            System.out.println("- " + category);
        }
        System.out.println("- Other");
    }
}
