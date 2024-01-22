package de.telekom.jsonfilter.exception;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class OperatorParsingException extends IOException {
    public OperatorParsingException(String message, JsonNode node) {
        super(
                "An error occurred during the parsing of the JSON-filter.\n" +
                "Error: " + message +
                "\nNode:\n" + node.toPrettyString());
    }
}
