package com.example.apibebakids.service;

import com.example.apibebakids.model.PovratnicaMpResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Adapter servis koji povezuje stari kontroler sa novom implementacijom za povratnice maloprodaje
 */
@Service
public class PovratnicaMaloprodajeService {

    @Autowired
    private PovratnicaMpService povratnicaMpService;

    @Autowired
    private DocumentUtils documentUtils;

    /**
     * Procesira zahtev za povratnicu maloprodaje u starom formatu i prosleđuje ga novoj implementaciji
     */
    public ResponseEntity<String> processPovratnicaMaloprodaje(
            String warehouseCode, String retailStoreCode, String system, String items,
            String pricelist, String logname, String reasonForReturn, String username, String password, String url) {

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
                if (oldItem.has("zone")) {
                    newItem.put("zone", oldItem.getString("zone"));
                }

                newItems.put(newItem);
            }

            // Poziv nove implementacije
            ResponseEntity<PovratnicaMpResponse> mpResponse = povratnicaMpService.dodajPovratnicu(
                    warehouseCode, retailStoreCode, system, newItems.toString(),
                    pricelist, logname, reasonForReturn);

            // Konverzija odgovora u format koji očekuju postojeći klijenti
            if (mpResponse.getBody() != null && mpResponse.getBody().isResponseResult()) {
                String oznakaDokumenta = "";
                if (mpResponse.getBody().getPovratnice() != null && !mpResponse.getBody().getPovratnice().isEmpty()) {
                    oznakaDokumenta = mpResponse.getBody().getPovratnice().get(0).getOznakaDokumenta();
                }

                return documentUtils.buildSuccessResponse(oznakaDokumenta);
            } else {
                String errorMessage = mpResponse.getBody() != null ?
                        mpResponse.getBody().getErrorMessage() : "Greška prilikom kreiranja povratnice";
                return documentUtils.buildErrorResponse(errorMessage);
            }

        } catch (Exception e) {
            return documentUtils.buildErrorResponse("Greška: " + e.getMessage());
        }
    }
}