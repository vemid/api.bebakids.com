package com.example.apibebakids.service;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class SoapResponseParserService {

    // Public method to parse SOAP response and return the appropriate JSON response
    public ResponseEntity<?> parseSoapResponse(String soapResponse) {
        try {
            // Parse the SOAP XML response using DocumentBuilder
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(soapResponse.getBytes()));

            // Extract the responseResult and errorMessage
            NodeList responseResultNode = document.getElementsByTagName("responseResult");
            NodeList errorMessageNode = document.getElementsByTagName("errorMessage");

            String responseResult = responseResultNode.item(0).getTextContent();

            // If responseResult is false, return 500 with the errorMessage
            if ("false".equalsIgnoreCase(responseResult)) {
                String errorMessage = errorMessageNode.item(0).getTextContent();
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("errorMessage", errorMessage);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
            }

            // If responseResult is true, return 200 with success message
            if ("true".equalsIgnoreCase(responseResult)) {
                Map<String, Object> successResponse = new HashMap<>();
                successResponse.put("message", "Operation successful");
                return ResponseEntity.ok(successResponse);
            }

            // Default fallback if neither true nor false was found
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected response format");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error parsing SOAP response: " + e.getMessage());
        }
    }
}
