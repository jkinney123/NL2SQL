package com.myproject;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject; // Import this for JSON handling

public class UserInterface {
    public void run() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("Enter your natural language query:");
            String userQuery = sc.nextLine();

            try {
                // Get SQL query from GPT
                String sqlQueryJsonString = GptRequest.request(userQuery);

                // Deserialize JSON and extract the SQL query
                JSONObject json = new JSONObject(sqlQueryJsonString);
                String extractedSqlQuery = json.getString("sql_query");

                System.out.println("Generated SQL Query: " + extractedSqlQuery);

                // Execute SQL query
                DatabaseConnection.executeSQLQuery(extractedSqlQuery);

            } catch (Exception e) {
                Logger logger = Logger.getLogger(DatabaseConnection.class.getName());
                logger.log(Level.SEVERE, "An exception was thrown", e);
            }

            System.out.println("Continue? (y/n)");
            if (sc.nextLine().equals("n")) {
                break;
            }
        }
        sc.close();
    }
}
