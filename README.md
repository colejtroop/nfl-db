# First Down

First Down is a Java command-line application for managing restaurant orders and menus across multiple locations. It uses JDBC to connect to Oracle Database and provides separate workflows for customer orders, location managers, menu customization, and reporting.

## Features

- Place pickup or in-person orders with location-specific sales tax and customer loyalty points.
- Build custom menu items from inventory components and existing menu items.
- Browse orders by location and inspect quantities, prices, and tax for each line item.
- Report sales by location, top-selling menu items, and customer order history.

## Build and run

Compile from the repository root with JDK 17 or later. Compilation requires only the JDK.

```sh
mkdir -p build/classes
javac --release 17 -d build/classes src/main/java/*.java
```

Running the app requires the Oracle JDBC driver (`ojdbc11.jar`), valid credentials, and access to an Oracle database with the First Down schema. The original course database is the default. Set `FIRST_DOWN_JDBC_URL` to use another compatible instance. The application prompts for your Oracle username and password.

| Platform | Run command (assuming the driver JAR is in the repository root) |
| --- | --- |
| macOS / Linux | `java -cp "build/classes:ojdbc11.jar" Main` |
| Windows PowerShell | `java -cp "build/classes;ojdbc11.jar" Main` |

The schema and seed data are required separately. GitHub Actions checks compilation without a database connection.

## Source layout

All Java sources are in `src/main/java/`. `Main.java` handles the connection prompt and top-level menu. The `*UI.java` classes implement the console workflows, and `InputUtil.java` handles input parsing and validation. `DB.java` contains the prepared SQL statements and coordinates transactions for multi-step writes.
