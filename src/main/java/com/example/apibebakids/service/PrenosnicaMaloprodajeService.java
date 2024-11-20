package com.example.apibebakids.service;

import com.example.apibebakids.model.PrenosnicaMaloprodaje;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class PrenosnicaMaloprodajeService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AuthHeaderService authHeaderService;

    @Autowired
    private DocumentUtils documentUtils;

    public ResponseEntity<String> processPrenosnicaMaloprodaje(String storeFrom, String storeTo, String system,
                                                               String items, String pricelist, String logname,
                                                               String username, String password, String url) {
        try {
            // Validate required fields
            List<String> missingFields = documentUtils.validateRequiredFields(storeFrom, storeTo, system, items, pricelist);
            if (!missingFields.isEmpty()) {
                return documentUtils.buildErrorResponse("Missing required fields: " + String.join(", ", missingFields));
            }

            // Parse and validate items array
            JSONArray itemsArray = new JSONArray(items);
            if (itemsArray.length() == 0) {
                return documentUtils.buildErrorResponse("Items array is empty");
            }

            List<String> invalidItems = documentUtils.validateItems(itemsArray);
            if (!invalidItems.isEmpty()) {
                return documentUtils.buildErrorResponse("Invalid items: " + String.join(", ", invalidItems));
            }

            // Generate the document number
            String oznakaDokumenta = documentUtils.generateDocumentNumber("P9", system, storeFrom, "");

            // Create PrenosnicaMaloprodaje object
            PrenosnicaMaloprodaje prenosnicaMaloprodaje = constructPrenosnicaMaloprodaje(
                    system, storeFrom, storeTo, itemsArray, pricelist, oznakaDokumenta, logname);

            // Prepare SOAP request and send
            String soapRequest = convertToSoapRequest(prenosnicaMaloprodaje);

            HttpHeaders headers = authHeaderService.createHeadersWithBasicAuth(username, password);
            HttpEntity<String> requestEntity = new HttpEntity<>(soapRequest, headers);
            ResponseEntity<String> soapResponse = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

            // Check the response for error messages
            String responseBody = soapResponse.getBody();
            if (responseBody != null && responseBody.contains("errorMessage")) {
                String errorMessage = documentUtils.extractErrorMessage(responseBody);
                return documentUtils.buildErrorResponse(errorMessage);
            }

            // Success response
            return documentUtils.buildSuccessResponse(oznakaDokumenta);

        } catch (Exception e) {
            return documentUtils.buildErrorResponse("Error: " + e.getMessage());
        }
    }

    private PrenosnicaMaloprodaje constructPrenosnicaMaloprodaje(
            String system, String storeFrom, String storeTo, JSONArray itemsArray, String pricelist, String oznakaDokumenta, String logname) throws JSONException {

        PrenosnicaMaloprodaje prenosnicaMaloprodaje = new PrenosnicaMaloprodaje();
        prenosnicaMaloprodaje.setSystem(system);
        prenosnicaMaloprodaje.setObjekatIzlaza(storeFrom);
        prenosnicaMaloprodaje.setObjekatUlaza(storeTo);
        prenosnicaMaloprodaje.setPricelist(pricelist);
        prenosnicaMaloprodaje.setLogname(logname);
        prenosnicaMaloprodaje.setOznakaDokumenta(oznakaDokumenta);

        List<PrenosnicaMaloprodaje.Stavka> stavke = new ArrayList<>();
        for (int i = 0; i < itemsArray.length(); i++) {
            JSONObject itemJson = itemsArray.getJSONObject(i);
            PrenosnicaMaloprodaje.Stavka stavka = new PrenosnicaMaloprodaje.Stavka();
            stavka.setSifraRobe(itemJson.getString("sku"));
            stavka.setSifraObelezja(itemJson.getString("size"));
            stavka.setKolicina(new BigDecimal(itemJson.getString("qty")));
            stavke.add(stavka);
        }
        prenosnicaMaloprodaje.setStavke(stavke);

        return prenosnicaMaloprodaje;
    }

    private String convertToSoapRequest(PrenosnicaMaloprodaje prenosnicaMaloprodaje) {
        StringBuilder stavkeXml = new StringBuilder();

        for (PrenosnicaMaloprodaje.Stavka stavka : prenosnicaMaloprodaje.getStavke()) {
            stavkeXml.append(String.format(
                    "<stavke>" +
                            "<sifraRobe>%s</sifraRobe>" +
                            "<sifraObelezja>%s</sifraObelezja>" +
                            "<kolicina>%.2f</kolicina>" +
                            "<stopaPoreza>%s</stopaPoreza>" +
                            "</stavke>",
                    stavka.getSifraRobe(),
                    stavka.getSifraObelezja(),
                    stavka.getKolicina(),
                    "20"
            ));
        }

        return String.format(
                "<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                        "    <Body>\n" +
                        "        <dodajPrenosnicuMaloprodaje xmlns=\"http://dokumenti.servis.mis.com/\">\n" +
                        "            <prenosiMp xmlns=\"\">\n" +
                        "                <objekatIzlaza>%s</objekatIzlaza>\n" +
                        "                <objekatUlaza>%s</objekatUlaza>\n" +
                        "                <datumDokumenta>%s</datumDokumenta>\n" +
                        "                <oznakaDokumenta>%s</oznakaDokumenta>\n" +
                        "                <logname>%s</logname>\n" +
                        "                <vrstaKnjizenja>SamoIzlaz</vrstaKnjizenja>\n" +
                        "                <vrstaPrevoza>1</vrstaPrevoza>\n" +
                        "                %s\n" +
                        "            </prenosiMp>\n" +
                        "        </dodajPrenosnicuMaloprodaje>\n" +
                        "    </Body>\n" +
                        "</Envelope>",
                prenosnicaMaloprodaje.getObjekatIzlaza(),
                prenosnicaMaloprodaje.getObjekatUlaza(),
                new SimpleDateFormat("yyyy-MM-dd").format(new Date()),
                prenosnicaMaloprodaje.getOznakaDokumenta(),
                prenosnicaMaloprodaje.getLogname(),
                stavkeXml.toString()
        );
    }
}