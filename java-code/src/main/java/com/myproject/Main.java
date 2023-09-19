// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
package com.myproject;

import java.util.List;  // Add this import
import java.util.Map;
public class Main {
    public static void main(String[] args) {
        // Fetch schema and print it
        Map<String, List<String>> schema = DatabaseConnection.fetchDatabaseSchema();
        System.out.println("Fetched schema: " + schema);

        // Initialize the user interface
        UserInterface ui = new UserInterface();
        ui.run();
    }
}
