package com.myproject;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;
import org.json.JSONObject;

public class GptRequest {

    private static final Logger logger = Logger.getLogger(GptRequest.class.getName()); // <-- Add this line

    public static String request(String query, String schema_str) throws Exception {
        logger.info("Fetched schema_str: " + schema_str);  // <-- This should now work
        URI uri = new URI("http://127.0.0.1:5000/convert");
        URL url = uri.toURL();

        // Open connection
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");

        // Set headers and properties
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);

        // Prepare the JSON payload
        JSONObject jsonPayload = new JSONObject();
        jsonPayload.put("query", query);
        jsonPayload.put("schema_str", schema_str);
        String jsonInputString = jsonPayload.toString();

        // Log the JSON payload
        logger.info("Sending JSON: " + jsonInputString);  // <-- This should also work

        try (OutputStream os = con.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String responseLine;
        while ((responseLine = br.readLine()) != null) {
            response.append(responseLine.trim());
        }

        // Log the server's response
        logger.info("Received response: " + response);

        return response.toString();
    }
}
