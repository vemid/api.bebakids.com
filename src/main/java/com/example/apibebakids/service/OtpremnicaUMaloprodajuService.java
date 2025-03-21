package com.example.apibebakids.service;

import com.example.apibebakids.model.OtpremnicaMpResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Adapter servis koji povezuje stari kontroler sa novom implementacijom za otpremnice maloprodaje
 */
@Service
public class OtpremnicaUMaloprodajuService {

    @Autowired
    private OtpremnicaMpService otpremnicaMpService;

    @Autowired
    private DocumentUtils documentUtils;

    /**
     * Procesira zahtev za otpremnicu maloprodaje u starom formatu i prosleđuje ga novoj implementaciji
     */
    public ResponseEntity<String> processOtpremnicaUMaloprodaju(
            String storeCode, String retailStoreCode, String note, String system, String items,
            String pricelist, String logname, String username, String password, String url) {

        try {
            // Konverzija stavki iz starog formata u novi
            JSONArray originalItems = new JSONArray(items);
            JSONArray newItems = new JSONArray();

            for (int i = 0; i < originalItems.length(); i++) {
                JSONObject oldItem = originalItems.getJSONObject(i);
                JSONObject newItem = new JSONObject();

                // Mapiranje iz starog u novi format
                newItem.put("sku", oldItem.getString("sku"));
                newItem.put("size", oldItem.getString("size"));
                newItem.put("qty", oldItem.getString("qty"));

                // Opciona polja
                if (oldItem.has("zoneMagacina")) {
                    newItem.put("zoneMagacina", oldItem.getString("zoneMagacina"));
                }

                if (oldItem.has("sifraPakovanja")) {
                    newItem.put("sifraPakovanja", oldItem.getString("sifraPakovanja"));
                }

                if (oldItem.has("brojPakovanja")) {
                    newItem.put("brojPakovanja", oldItem.getString("brojPakovanja"));
                }

                if (oldItem.has("stopaPDV")) {
                    newItem.put("stopaPDV", oldItem.getString("stopaPDV"));
                }

                newItems.put(newItem);
            }

            // Poziv nove implementacije
            ResponseEntity<OtpremnicaMpResponse> mpResponse = otpremnicaMpService.dodajOtpremnicu(
                    storeCode, retailStoreCode, system, newItems.toString(),
                    pricelist, logname, note);

            // Konverzija odgovora u format koji očekuju postojeći klijenti
            if (mpResponse.getBody() != null && mpResponse.getBody().isResponseResult()) {
                String oznakaDokumenta = "";
                if (mpResponse.getBody().getOtpremnice() != null && !mpResponse.getBody().getOtpremnice().isEmpty()) {
                    oznakaDokumenta = mpResponse.getBody().getOtpremnice().get(0).getOznakaDokumenta();
                }

                return documentUtils.buildSuccessResponse(oznakaDokumenta);
            } else {
                String errorMessage = mpResponse.getBody() != null ?
                        mpResponse.getBody().getErrorMessage() : "Greška prilikom kreiranja otpremnice";
                return documentUtils.buildErrorResponse(errorMessage);
            }

        } catch (Exception e) {
            return documentUtils.buildErrorResponse("Greška: " + e.getMessage());
        }
    }
}