import java.io.Console;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

//input helper class for terminal prompts
public class InputUtil {
    //reads the oracle password
    public static String readPassword(Scanner scanner) {
        Console console = System.console();

        if (console != null) {
            char[] value = console.readPassword("Oracle password: ");
            if (value == null) {
                return null;
            }
            return new String(value);
        }

        return readRequiredLine(scanner, "Oracle password: ");
    }

    //reads one required line
    public static String readRequiredLine(Scanner scanner, String prompt) {
        System.out.print(prompt);

        if (!scanner.hasNextLine()) {
            return null;
        }

        String value = scanner.nextLine().trim();
        if (value.isEmpty()) {
            return null;
        }

        return value;
    }

    //reads an int value
    public static Integer readInt(Scanner scanner, String prompt) {
        while (true) {
            String value = readLineAllowBlank(scanner, prompt);
            if (value == null) {
                return null;
            }

            try {
                return Integer.parseInt(value.trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number.");
            }
        }
    }

    //reads a double value
    public static Double readDouble(Scanner scanner, String prompt) {
        while (true) {
            String value = readLineAllowBlank(scanner, prompt);
            if (value == null) {
                return null;
            }

            try {
                return Double.parseDouble(value.trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

    //reads a date value
    public static Date readDate(Scanner scanner, String prompt) {
        while (true) {
            String value = readLineAllowBlank(scanner, prompt);
            if (value == null) {
                return null;
            }

            try {
                return Date.valueOf(LocalDate.parse(value.trim()));
            } catch (DateTimeParseException e) {
                System.out.println("Please use YYYY-MM-DD.");
            }
        }
    }

    //reads a timestamp value
    public static Timestamp readTimestamp(Scanner scanner, String prompt) {
        while (true) {
            String value = readLineAllowBlank(scanner, prompt);
            if (value == null) {
                return null;
            }

            try {
                return Timestamp.valueOf(LocalDateTime.parse(value.trim()));
            } catch (DateTimeParseException e) {
                System.out.println("Please use YYYY-MM-DDTHH:MM.");
            }
        }
    }

    //reads pickup date and time
    public static Timestamp readPickupTimestamp(Scanner scanner) {
        while (true) {
            String dateValue = readLineAllowBlank(
                scanner,
                "Pickup date (YYYY-MM-DD): "
            );
            if (dateValue == null) {
                return null;
            }

            String timeValue = readLineAllowBlank(
                scanner,
                "Pickup time (HH:MM): "
            );
            if (timeValue == null) {
                return null;
            }

            try {
                LocalDate date = LocalDate.parse(dateValue.trim());
                LocalTime time = LocalTime.parse(timeValue.trim());
                return Timestamp.valueOf(LocalDateTime.of(date, time));
            } catch (DateTimeParseException e) {
                System.out.println("Please use YYYY-MM-DD and HH:MM.");
            }
        }
    }

    //reads a yes or no
    public static boolean readYesNo(Scanner scanner, String prompt) {
        while (true) {
            String value = readLineAllowBlank(scanner, prompt);
            if (value == null) {
                return false;
            }

            value = value.trim().toLowerCase();
            if (value.equals("y") || value.equals("yes")) {
                return true;
            }
            if (value.equals("n") || value.equals("no")) {
                return false;
            }

            System.out.println("Please enter yes or no.");
        }
    }

    //reads one line and allows blank
    private static String readLineAllowBlank(Scanner scanner, String prompt) {
        System.out.print(prompt);

        if (!scanner.hasNextLine()) {
            return null;
        }

        return scanner.nextLine();
    }
}
