# This is a sample Python script.

# Press Shift+F10 to execute it or replace it with your code.
# Press Double Shift to search everywhere for classes, files, tool windows, actions, and settings.
from flask import Flask, request, jsonify
import openai
import os
import logging
from dotenv import load_dotenv
import json
logging.basicConfig(level=logging.DEBUG)

load_dotenv()
app = Flask(__name__)
openai.api_key = os.environ.get("OPENAI_API_KEY")  # Fallback to hardcoded ke


@app.route('/convert', methods=['POST'])
def convert():
    print("Incoming request data: ", request.data.decode('utf-8'))
    try:
        data = request.json
        print("Parsed JSON data: ", data)
        if not data or 'query' not in data:
            return jsonify({"error": "Bad Request: 'query' key is required in JSON payload"}), 400

        query = data['query']
        schema_str = data.get('schema_str', '')  # Fetch the schema string if provided, else set to empty string
        print("Parsed schema_str: ", schema_str)

        prompt = f"You are a chatbot that connects to a user's SQL database."
        if schema_str:
            prompt += f" The database has the following schema: {schema_str}."

        prompt += f"\nTranslate the following user input into an SQL query: {query}"

        # Translate the natural language query to SQL query using GPT
        response = openai.Completion.create(
            engine="text-davinci-002",  # Adjust the engine here
            prompt=prompt,
            max_tokens=100
        )

        sql_query = response.choices[0].text.strip()
        sql_query = sql_query.replace("FROM ", "FROM parties_db.")
        print(f"Generated SQL Query: {sql_query}")
        return jsonify({"sql_query": sql_query})

    except Exception as e:
        exception_details = {
            "error_message": str(e),
            "error_type": str(type(e)),
            # add any other details you think would be useful
        }
        logging.error(f"An error occurred: {e}")
        return jsonify({"error": f"An error occurred: {str(e)}"}), 500


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)
