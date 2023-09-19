package com.myproject;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.google.gson.Gson;
import org.json.JSONObject; // Import this for JSON handling

public class UserInterface {
    public void run() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("Enter your natural language query:");
            String userQuery = sc.nextLine();

            try {
                // Get SQL query from GPT
                Map<String, List<String>> schemaMap = DatabaseConnection.fetchDatabaseSchema();
                Gson gson = new Gson();
                String schema_str = gson.toJson(schemaMap);
                System.out.println("Debug: Schema String to be sent: " + schema_str);
                String sqlQueryJsonString = GptRequest.request(userQuery, schema_str);

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
