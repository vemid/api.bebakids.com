package com.example.apibebakids.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class PrenosnicaUtils {

    /**
     * Generiše broj dokumenta
     */
    public String generateDocumentNumber(String prefix, String system, String objectCode, String suffix) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dateStr = sdf.format(new Date());

        // Format: PREFIX-SYSTEM-OBJECTCODE-YYYYMMDD-SUFFIX-RANDOM
        StringBuilder sb = new StringBuilder();
        sb.append(prefix).append("-");
        sb.append(system.toUpperCase()).append("-");
        sb.append(objectCode).append("-");
        sb.append(dateStr);

        if (suffix != null && !suffix.isEmpty()) {
            sb.append("-").append(suffix);
        }

        // Dodaj random broj za jedinstvenost
        sb.append("-").append(System.currentTimeMillis() % 10000);

        return sb.toString();
    }

    /**
     * Validira obavezna polja
     */
    public List<String> validateRequiredFields(String... fields) {
        List<String> missingFields = new ArrayList<>();
        for (int i = 0; i < fields.length; i++) {
            if (fields[i] == null || fields[i].isEmpty()) {
                missingFields.add("field" + (i + 1));
            }
        }
        return missingFields;
    }

    /**
     * Validira stavke u JSON nizu
     */
    public List<String> validateItems(JSONArray itemsArray) {
        List<String> invalidItems = new ArrayList<>();

        for (int i = 0; i < itemsArray.length(); i++) {
            JSONObject itemJson = itemsArray.getJSONObject(i);

            // Proveri obavezna polja
            if (!itemJson.has("sifraRobe") || itemJson.getString("sifraRobe").isEmpty()) {
                invalidItems.add("Stavka " + (i + 1) + ": nedostaje šifra robe");
            }

            if (!itemJson.has("sifraObelezja") || itemJson.getString("sifraObelezja").isEmpty()) {
                invalidItems.add("Stavka " + (i + 1) + ": nedostaje šifra obeležja");
            }

            if (!itemJson.has("kolicina") || itemJson.getString("kolicina").isEmpty()) {
                invalidItems.add("Stavka " + (i + 1) + ": nedostaje količina");
            } else {
                // Proveri da li je količina broj
                try {
                    Double.parseDouble(itemJson.getString("kolicina"));
                } catch (NumberFormatException e) {
                    invalidItems.add("Stavka " + (i + 1) + ": količina nije validan broj");
                }
            }
        }

        return invalidItems;
    }

    /**
     * Ekstrahuje poruku o grešci iz SOAP odgovora
     */
    public String extractErrorMessage(String soapResponse) {
        // Jednostavna implementacija koja traži tag sa greškom
        String errorTag = "<errorMessage>";
        String errorEndTag = "</errorMessage>";

        int startIndex = soapResponse.indexOf(errorTag);
        if (startIndex != -1) {
            startIndex += errorTag.length();
            int endIndex = soapResponse.indexOf(errorEndTag, startIndex);
            if (endIndex != -1) {
                return soapResponse.substring(startIndex, endIndex);
            }
        }

        return "Nepoznata greška u SOAP odgovoru";
    }

    /**
     * Kreira odgovor sa greškom
     */
    public ResponseEntity<String> buildErrorResponse(String errorMessage) {
        JSONObject response = new JSONObject();
        response.put("success", false);
        response.put("error", errorMessage);

        return ResponseEntity.badRequest().body(response.toString());
    }

    /**
     * Kreira uspešan odgovor
     */
    public ResponseEntity<String> buildSuccessResponse(String documentNumber) {
        JSONObject response = new JSONObject();
        response.put("success", true);
        response.put("documentNumber", documentNumber);

        return ResponseEntity.ok(response.toString());
    }
}