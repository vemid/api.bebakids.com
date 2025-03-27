package com.example.apibebakids.service;

import com.example.apibebakids.model.PrenosnicaMpDTO;
import com.example.apibebakids.model.PrenosnicaMpResponse;
import com.example.apibebakids.model.VrstaKnjizenjaMp;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Adapter servis koji prihvata zahteve za PrenosnicaMaloprodaje u starom formatu
 * i prosleđuje ih novoj implementaciji.
 */
@Service
public class PrenosnicaMaloprodajeService {

    @Autowired
    private PrenosnicaMpService prenosnicaMpService;

    @Autowired
    private DocumentUtils documentUtils;

    /**
     * Procesira zahtev za kreiranje prenosnice maloprodaje u starom formatu
     * i prosleđuje ga novoj implementaciji.
     */
    public ResponseEntity<String> processPrenosnicaMaloprodaje(
            String storeFrom, String storeTo, String system, String items,
            String pricelist, String logname, String username, String password, String url) {

        try {
            // Konvertuj parametre iz starog formata u novi
            JSONArray originalItems = new JSONArray(items);
            JSONArray newItems = new JSONArray();

            for (int i = 0; i < originalItems.length(); i++) {
                JSONObject oldItem = originalItems.getJSONObject(i);
                JSONObject newItem = new JSONObject();

                // Mapiranje iz starog u novi format
                newItem.put("sifraRobe", oldItem.getString("sku"));
                newItem.put("sifraObelezja", oldItem.getString("size"));
                newItem.put("kolicina", oldItem.getString("qty"));

                // Opciona polja
                if (oldItem.has("stopaPoreza")) {
                    newItem.put("stopaPoreza", oldItem.getString("stopaPoreza"));
                }

                if (oldItem.has("zoneMagacina")) {
                    newItem.put("zoneMagacina", oldItem.getString("zoneMagacina"));
                }

                if (oldItem.has("sifraPakovanja")) {
                    newItem.put("sifraPakovanja", oldItem.getString("sifraPakovanja"));
                }

                if (oldItem.has("brojPakovanja")) {
                    newItem.put("brojPakovanja", oldItem.getString("brojPakovanja"));
                }

                newItems.put(newItem);
            }

            // Podrazumevano vrsta knjiženja je IZLAZ_SA_PRODAJOM (2)
            Integer vrstaKnjizenja = 2;

            // Pozovi novu implementaciju
            ResponseEntity<PrenosnicaMpResponse> mpResponse = prenosnicaMpService.dodajPrenosnicu(
                    storeFrom, storeTo, system, newItems.toString(),
                    pricelist, logname, vrstaKnjizenja, null);

            // Konverzija odgovora u format koji očekuju postojeći klijenti
            if (mpResponse.getBody() != null && mpResponse.getBody().isResponseResult()) {
                String oznakaDokumenta = "";
                if (mpResponse.getBody().getPrenosnice() != null && !mpResponse.getBody().getPrenosnice().isEmpty()) {
                    oznakaDokumenta = mpResponse.getBody().getPrenosnice().get(0).getOznakaDokumenta();
                }

                return documentUtils.buildSuccessResponse(oznakaDokumenta);
            } else {
                String errorMessage = mpResponse.getBody() != null ?
                        mpResponse.getBody().getErrorMessage() : "Greška prilikom kreiranja prenosnice";
                return documentUtils.buildErrorResponse(errorMessage);
            }

        } catch (Exception e) {
            return documentUtils.buildErrorResponse("Greška: " + e.getMessage());
        }
    }
}