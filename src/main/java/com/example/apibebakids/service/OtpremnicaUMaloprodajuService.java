package com.example.apibebakids.service;

import com.example.apibebakids.model.OtpremnicaUMaloprodaju;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OtpremnicaUMaloprodajuService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AuthHeaderService authHeaderService;

    @Autowired
    private DocumentUtils documentUtils;

    @Autowired
    private PriceCheckService priceCheckService;

    public ResponseEntity<String> processOtpremnicaUMaloprodaju(String storeCode, String retailStoreCode, String note,
                                                                String system, String items, String pricelist, String logname,
                                                                String username, String password, String url) {
        try {
            JSONArray itemsArray = new JSONArray(items);
            if (itemsArray.length() == 0) {
                return documentUtils.buildErrorResponse("Items array is empty");
            }

            // Validate items
            List<String> invalidItems = documentUtils.validateItems(itemsArray);
            if (!invalidItems.isEmpty()) {
                return documentUtils.buildErrorResponse("Invalid items: " + String.join(", ", invalidItems));
            }

            // Generate the document number
            String oznakaDokumenta = documentUtils.generateDocumentNumber("OM", system, storeCode, note);

            // Create OtpremnicaUMaloprodaju object
            OtpremnicaUMaloprodaju otpremnicaUMaloprodaju = constructOtpremnicaUMaloprodaju(
                    system, storeCode, retailStoreCode, note, itemsArray, pricelist, oznakaDokumenta, logname);

            // Prepare SOAP request and send
            String soapRequest = convertToSoapRequest(otpremnicaUMaloprodaju, system, storeCode, pricelist);

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

    private OtpremnicaUMaloprodaju constructOtpremnicaUMaloprodaju(
            String system, String storeCode, String retailStoreCode, String note, JSONArray itemsArray, String pricelist, String oznakaDokumenta, String logname) throws JSONException {

        OtpremnicaUMaloprodaju otpremnicaUMaloprodaju = new OtpremnicaUMaloprodaju();
        otpremnicaUMaloprodaju.setDatumDokumenta(LocalDateTime.now());
        otpremnicaUMaloprodaju.setLogname(logname);
        otpremnicaUMaloprodaju.setNapomena(note);
        otpremnicaUMaloprodaju.setOznakaDokumenta(oznakaDokumenta);
        otpremnicaUMaloprodaju.setSifraMagacina(storeCode);
        otpremnicaUMaloprodaju.setSifraObjektaMaloprodaje(retailStoreCode);
        otpremnicaUMaloprodaju.setOznakaCenovnika(pricelist);

        List<OtpremnicaUMaloprodaju.StavkaOtpremnice> stavke = new ArrayList<>();
        for (int i = 0; i < itemsArray.length(); i++) {
            JSONObject itemJson = itemsArray.getJSONObject(i);
            OtpremnicaUMaloprodaju.StavkaOtpremnice stavka = new OtpremnicaUMaloprodaju.StavkaOtpremnice();
            stavka.setSifraRobe(itemJson.getString("sku"));
            stavka.setSifraObelezja(itemJson.getString("size"));
            stavka.setKolicina(new BigDecimal(itemJson.getString("qty")));

            // Fetch prices for this item
            double[] prices = priceCheckService.checkPrices(system, storeCode, itemJson.getString("sku"), pricelist);
            stavka.setCenaZalihe(BigDecimal.valueOf(prices[0]));
            stavka.setProdajnaCenaBezPoreza(BigDecimal.valueOf(prices[1]));
            stavka.setOsnovnaCena(BigDecimal.valueOf(prices[2]));
            stavka.setProdajnaCena(BigDecimal.valueOf(prices[2]));
            stavka.setProdajnaCenaSaRabatom(BigDecimal.valueOf(prices[2]));

            stavke.add(stavka);
        }
        otpremnicaUMaloprodaju.setStavke(stavke);

        return otpremnicaUMaloprodaju;
    }

    private String convertToSoapRequest(OtpremnicaUMaloprodaju otpremnicaUMaloprodaju, String system, String storeCode, String pricelist) {
        StringBuilder stavkeXml = new StringBuilder();

        for (OtpremnicaUMaloprodaju.StavkaOtpremnice stavka : otpremnicaUMaloprodaju.getStavke()) {
            // Fetch the prices again to ensure we have the most up-to-date prices
            double[] prices = priceCheckService.checkPrices(system, storeCode, stavka.getSifraRobe(), pricelist);

            stavkeXml.append(String.format(
                    "<stavke>" +
                            "<akcijskaStopaRabata>0</akcijskaStopaRabata>" +
                            "<brojPakovanja>%.2f</brojPakovanja>" +
                            "<cenaZalihe>%.2f</cenaZalihe>" +
                            "<kolicina>%.2f</kolicina>" +
                            "<maloprodajnaMarza>0</maloprodajnaMarza>" +
                            "<osnovnaCena>%.2f</osnovnaCena>" +
                            "<posebnaStopaRabata>0</posebnaStopaRabata>" +
                            "<prodajnaCena>%.2f</prodajnaCena>" +
                            "<prodajnaCenaBezPoreza>%.2f</prodajnaCenaBezPoreza>" +
                            "<prodajnaCenaSaRabatom>%.2f</prodajnaCenaSaRabatom>" +
                            "<sifraObelezja>%s</sifraObelezja>" +
                            "<sifraRobe>%s</sifraRobe>" +
                            "<sifraTarifneGrupePoreza>100</sifraTarifneGrupePoreza>" +
                            "<stopaPDV>20</stopaPDV>" +
                            "<stopaRabata>0</stopaRabata>" +
                            "</stavke>",
                    stavka.getKolicina(),
                    prices[0],
                    stavka.getKolicina(),
                    prices[2],
                    prices[2],
                    prices[1],
                    prices[2],
                    stavka.getSifraObelezja(),
                    stavka.getSifraRobe()
            ));
        }

        return String.format(
                "<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                        "    <Body>\n" +
                        "        <dodajOtpremnicuUMaloprodaju xmlns=\"http://sifarnici.servis.mis.com/\">\n" +
                        "            <otpremnicaUMaloprodaju xmlns=\"\">\n" +
                        "                <datumDokumenta>%s</datumDokumenta>\n" +
                        "                <logname>%s</logname>\n" +
                        "                <napomena>%s</napomena>\n" +
                        "                <oznakaCenovnika>%s</oznakaCenovnika>\n" +
                        "                <oznakaDokumenta>%s</oznakaDokumenta>\n" +
                        "                <sifraMagacina>%s</sifraMagacina>\n" +
                        "                <sifraObjektaMaloprodaje>%s</sifraObjektaMaloprodaje>\n" +
                        "                %s\n" +
                        "                <storno>N</storno>\n" +
                        "                <vrstaKnjizenja>2</vrstaKnjizenja>\n" +
                        "            </otpremnicaUMaloprodaju>\n" +
                        "        </dodajOtpremnicuUMaloprodaju>\n" +
                        "    </Body>\n" +
                        "</Envelope>",
                otpremnicaUMaloprodaju.getDatumDokumenta(),
                otpremnicaUMaloprodaju.getLogname(),
                otpremnicaUMaloprodaju.getNapomena(),
                otpremnicaUMaloprodaju.getOznakaCenovnika(),
                otpremnicaUMaloprodaju.getOznakaDokumenta(),
                otpremnicaUMaloprodaju.getSifraMagacina(),
                otpremnicaUMaloprodaju.getSifraObjektaMaloprodaje(),
                stavkeXml.toString()
        );
    }
}