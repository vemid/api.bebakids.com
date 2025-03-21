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

    /**
     * Procesira zahtev za kreiranje prenosnice maloprodaje u starom formatu
     * i prosleđuje ga novoj implementaciji.
     */
    public ResponseEntity<?> processPrenosnicaMaloprodaje(
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

                // Ako postoje dodatna polja, možemo ih dodati
                if (oldItem.has("stopaPoreza")) {
                    newItem.put("stopaPoreza", oldItem.getString("stopaPoreza"));
                }

                newItems.put(newItem);
            }

            // Podrazumevano vrsta knjiženja je UlazIzlaz (3)
            Integer vrstaKnjizenja = 2;

            // Pozovi novu implementaciju
            return prenosnicaMpService.dodajPrenosnicu(
                    storeFrom, storeTo, system, newItems.toString(),
                    pricelist, logname, vrstaKnjizenja, null);

        } catch (Exception e) {
            // Kreiranje odgovora u formatu koji očekuju postojeći klijenti
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("success", false);
            errorResponse.put("error", "Greška: " + e.getMessage());

            return ResponseEntity.status(500).body(errorResponse.toString());
        }
    }
}