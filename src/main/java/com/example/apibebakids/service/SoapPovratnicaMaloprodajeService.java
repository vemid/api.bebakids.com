package com.example.apibebakids.service;

import com.example.apibebakids.model.PovratnicaMaloprodaje;
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
public class SoapPovratnicaMaloprodajeService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AuthHeaderService authHeaderService;

    @Autowired
    private DocumentUtils documentUtils;

    public ResponseEntity<String> processPovratnicaMaloprodaje(String warehouseCode, String retailwarehouseCode, String system,
                                                               String items, String pricelist, String logname, String reasonForReturn,
                                                               String username, String password, String url) {
        try {
            // Validate required fields
            List<String> missingFields = documentUtils.validateRequiredFields(warehouseCode, retailwarehouseCode, system, items, pricelist);
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
            String oznakaDokumenta = documentUtils.generateDocumentNumber("VN", system, warehouseCode, "");

            // Create PovratnicaMaloprodaje object
            PovratnicaMaloprodaje povratnicaMaloprodaje = constructPovratnicaMaloprodaje(
                    system, warehouseCode, retailwarehouseCode, itemsArray, pricelist, oznakaDokumenta, logname, reasonForReturn);

            // Prepare SOAP request and send
            String soapRequest = convertToSoapRequest(povratnicaMaloprodaje);

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

    private PovratnicaMaloprodaje constructPovratnicaMaloprodaje(
            String system, String warehouseCode, String retailwarehouseCode, JSONArray itemsArray, String pricelist,
            String oznakaDokumenta, String logname, String reasonForReturn) throws JSONException {

        PovratnicaMaloprodaje povratnicaMaloprodaje = new PovratnicaMaloprodaje();
        povratnicaMaloprodaje.setSifraMagacina(warehouseCode);
        povratnicaMaloprodaje.setSifraMaloprodajnogObjekta(retailwarehouseCode);
        povratnicaMaloprodaje.setOznakaCenovnika(pricelist);
        povratnicaMaloprodaje.setLogname(logname);
        povratnicaMaloprodaje.setOznakaDokumenta(oznakaDokumenta);
        povratnicaMaloprodaje.setRazlogVracanja(reasonForReturn);

        List<PovratnicaMaloprodaje.Stavka> stavke = new ArrayList<>();
        for (int i = 0; i < itemsArray.length(); i++) {
            JSONObject itemJson = itemsArray.getJSONObject(i);
            PovratnicaMaloprodaje.Stavka stavka = new PovratnicaMaloprodaje.Stavka();
            stavka.setSifraRobe(itemJson.getString("sku"));
            stavka.setSifraObelezja(itemJson.getString("size"));
            stavka.setKolicina(new BigDecimal(itemJson.getString("qty")));
            stavka.setSifraZone(itemJson.optString("zone", ""));
            stavke.add(stavka);
        }
        povratnicaMaloprodaje.setStavke(stavke);

        return povratnicaMaloprodaje;
    }

    private String convertToSoapRequest(PovratnicaMaloprodaje povratnicaMaloprodaje) {
        StringBuilder stavkeXml = new StringBuilder();

        for (PovratnicaMaloprodaje.Stavka stavka : povratnicaMaloprodaje.getStavke()) {
            stavkeXml.append(String.format(
                    "<stavke>" +
                            "<sifraRobe>%s</sifraRobe>" +
                            "<sifraObelezja>%s</sifraObelezja>" +
                            "<kolicina>%.2f</kolicina>" +
                            "<sifraZone>%s</sifraZone>" +
                            "</stavke>",
                    stavka.getSifraRobe(),
                    stavka.getSifraObelezja(),
                    stavka.getKolicina(),
                    stavka.getSifraZone()
            ));
        }

        return String.format(
                "<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                        "    <Body>\n" +
                        "        <dodajNalogZaPovratIzMaloprodajeUMagacin xmlns=\"http://dokumenti.servis.mis.com/\">\n" +
                        "            <povrati xmlns=\"\">\n" +
                        "                <datumDokumenta>%s</datumDokumenta>\n" +
                        "                <logname>%s</logname>\n" +
                        "                <napomena>%s</napomena>\n" +
                        "                <oznakaCenovnika>%s</oznakaCenovnika>\n" +
                        "                <oznakaDokumenta>%s</oznakaDokumenta>\n" +
                        "                <razlogVracanja>%s</razlogVracanja>\n" +
                        "                <sifraMagacina>%s</sifraMagacina>\n" +
                        "                <sifraMaloprodajnogObjekta>%s</sifraMaloprodajnogObjekta>\n" +
                        "                %s\n" +
                        "                <vrstaKnjizenjaPovrataMagacin>SamoIzlaz</vrstaKnjizenjaPovrataMagacin>\n" +
                        "            </povrati>\n" +
                        "        </dodajNalogZaPovratIzMaloprodajeUMagacin>\n" +
                        "    </Body>\n" +
                        "</Envelope>",
                new SimpleDateFormat("yyyy-MM-dd").format(new Date()), // Format the current date
                povratnicaMaloprodaje.getLogname(),
                povratnicaMaloprodaje.getNapomena(),
                povratnicaMaloprodaje.getOznakaCenovnika(),
                povratnicaMaloprodaje.getOznakaDokumenta(),
                povratnicaMaloprodaje.getRazlogVracanja(),
                povratnicaMaloprodaje.getSifraMagacina(),
                povratnicaMaloprodaje.getSifraMaloprodajnogObjekta(),
                stavkeXml.toString()
        );
    }
}
