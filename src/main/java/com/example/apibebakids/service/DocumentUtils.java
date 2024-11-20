package com.example.apibebakids.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class DocumentUtils {

    @Autowired
    @Qualifier("jdbcTemplateBebaKids")
    private JdbcTemplate jdbcTemplateBebaKids;

    @Autowired
    @Qualifier("jdbcTemplateWatch")
    private JdbcTemplate jdbcTemplateWatch;

    @Autowired
    @Qualifier("jdbcTemplateGeox")
    private JdbcTemplate jdbcTemplateGeox;

    @Qualifier("jdbcTemplateBebaKidsBih")
    @Autowired
    private JdbcTemplate jdbcTemplateBebaKidsBih;

    public List<String> validateRequiredFields(String storeFrom, String storeTo, String system, String items, String pricelist) {
        List<String> missingFields = new ArrayList<>();
        if (storeFrom == null || storeFrom.isEmpty()) missingFields.add("storeFrom");
        if (storeTo == null || storeTo.isEmpty()) missingFields.add("storeTo");
        if (system == null || system.isEmpty()) missingFields.add("system");
        if (items == null || items.isEmpty()) missingFields.add("items");
        if (pricelist == null || pricelist.isEmpty()) missingFields.add("pricelist");
        return missingFields;
    }

    public List<String> validateItems(JSONArray itemsArray) throws JSONException {
        List<String> invalidItems = new ArrayList<>();
        for (int i = 0; i < itemsArray.length(); i++) {
            JSONObject item = itemsArray.getJSONObject(i);
            if (!item.has("sku") || !item.has("size") || !item.has("qty")) {
                invalidItems.add("Item " + (i + 1) + " is missing required fields");
            } else {
                try {
                    new BigDecimal(item.getString("qty"));
                } catch (NumberFormatException e) {
                    invalidItems.add("Item " + (i + 1) + " has invalid quantity");
                }
            }
        }
        return invalidItems;
    }

    public String generateDocumentNumber(String documentType, String system, String store, String note) {
        String currentDate = new SimpleDateFormat("dd.MM.yyyy").format(new Date());

        String sql = "EXECUTE PROCEDURE get_document_number(?, ?, ?,?)";

        JdbcTemplate jdbcTemplate = getJdbcTemplate(system);

        String documentNumber = jdbcTemplate.queryForObject(sql, new Object[]{documentType, store, currentDate, note}, String.class);

        return documentNumber != null ? documentNumber.trim() : null;
    }

    private JdbcTemplate getJdbcTemplate(String system) {
        switch (system.toLowerCase()) {
            case "bebakids":
                return jdbcTemplateBebaKids;
            case "watch":
                return jdbcTemplateWatch;
            case "geox":
                return jdbcTemplateGeox;
            case "bebakidsbih":
                return jdbcTemplateBebaKidsBih;
            default:
                throw new IllegalArgumentException("Invalid system: " + system);
        }
    }

    public String extractErrorMessage(String responseBody) {
        String errorTagStart = "<errorMessage>";
        String errorTagEnd = "</errorMessage>";

        if (responseBody.contains(errorTagStart) && responseBody.contains(errorTagEnd)) {
            int startIndex = responseBody.indexOf(errorTagStart) + errorTagStart.length();
            int endIndex = responseBody.indexOf(errorTagEnd);
            return responseBody.substring(startIndex, endIndex).trim();
        }

        return "Unknown error occurred.";
    }

    public ResponseEntity<String> buildErrorResponse(String errorMessage) {
        JSONObject responseJson = new JSONObject();
        responseJson.put("responseResult", false);
        responseJson.put("errorMessage", errorMessage);
        responseJson.put("respResultCount", 0);
        responseJson.put("data", new JSONArray());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseJson.toString());
    }

    public ResponseEntity<String> buildSuccessResponse(String documentNumber) {
        JSONObject responseJson = new JSONObject();
        responseJson.put("responseResult", true);
        responseJson.put("errorMessage", JSONObject.NULL);
        responseJson.put("respResultCount", 65);
        JSONArray dataArray = new JSONArray();
        JSONObject messageObject = new JSONObject();
        messageObject.put("message", "Successfully created document: " + documentNumber);
        dataArray.put(messageObject);
        responseJson.put("data", dataArray);
        return ResponseEntity.ok(responseJson.toString());
    }
}