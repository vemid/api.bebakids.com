package com.example.apibebakids.service;

import com.example.apibebakids.model.NalogZaOtpremu;
import com.example.apibebakids.model.PriceInfoDTO;
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
public class NalogZaOtpremuService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AuthHeaderService authHeaderService;

    @Autowired
    private DocumentUtils documentUtils;

    @Autowired
    private PriceCheckService priceCheckService;

    public ResponseEntity<String> processNalogZaOtpremu(String warehouseCode, String system, String items, String pricelist,
                                                        String logname, String destination, String note, String username,
                                                        String password, String url) {
        try {
            // Parse and validate items
            JSONArray itemsArray = new JSONArray(items);
            if (itemsArray.length() == 0) {
                return documentUtils.buildErrorResponse("Items array is empty");
            }

            List<String> invalidItems = documentUtils.validateItems(itemsArray);
            if (!invalidItems.isEmpty()) {
                return documentUtils.buildErrorResponse("Invalid items: " + String.join(", ", invalidItems));
            }

            // Generate document number
            String oznakaDokumenta = documentUtils.generateDocumentNumber("NI", system, warehouseCode, note);

            // Create NalogZaOtpremu object
            NalogZaOtpremu nalogZaOtpremu = constructNalogZaOtpremu(
                    warehouseCode, system, itemsArray, pricelist, logname, destination, note, oznakaDokumenta);

            // Prepare SOAP request and send
            String soapRequest = convertToSoapRequest(nalogZaOtpremu, system, warehouseCode, pricelist,destination);

            HttpHeaders headers = authHeaderService.createHeadersWithBasicAuth(username, password);
            HttpEntity<String> requestEntity = new HttpEntity<>(soapRequest, headers);
            ResponseEntity<String> soapResponse = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

            // Check response for error messages
            String responseBody = soapResponse.getBody();
            if (responseBody != null && responseBody.contains("errorMessage")) {
                String errorMessage = documentUtils.extractErrorMessage(responseBody);
                return documentUtils.buildErrorResponse(errorMessage);
            }

            // Return success response
            return documentUtils.buildSuccessResponse(oznakaDokumenta);

        } catch (Exception e) {
            return documentUtils.buildErrorResponse("Error: " + e.getMessage());
        }
    }

    private NalogZaOtpremu constructNalogZaOtpremu(
            String warehouseCode, String system, JSONArray itemsArray,
            String pricelist,String logname, String destination, String note,String oznakaDokumenta) throws JSONException {

        NalogZaOtpremu nalogZaOtpremu = new NalogZaOtpremu();
        nalogZaOtpremu.setDatumDokumenta(LocalDateTime.now());
        nalogZaOtpremu.setDatumOtpreme(LocalDateTime.now());
        nalogZaOtpremu.setDpo(LocalDateTime.now());
        nalogZaOtpremu.setValutaPlacanja(LocalDateTime.now());
        nalogZaOtpremu.setLogname(logname);
        nalogZaOtpremu.setNapomena(note);
        nalogZaOtpremu.setOznakaDokumenta(oznakaDokumenta);
        nalogZaOtpremu.setSifraMagacina(warehouseCode);
        nalogZaOtpremu.setMestoIsporuke(destination);
        nalogZaOtpremu.setOznakaCenovnika(pricelist);
//        nalogZaOtpremu.setSifraOrganizacioneJednice("organization_code"); // example of dynamic field
//        nalogZaOtpremu.setSifraPartnera("partner_code"); // dynamic field for partner
//        nalogZaOtpremu.setSifraPartneraKorisnika("partner_user_code"); // dynamic

        List<NalogZaOtpremu.Stavka> stavke = new ArrayList<>();
        for (int i = 0; i < itemsArray.length(); i++) {
            JSONObject itemJson = itemsArray.getJSONObject(i);
            NalogZaOtpremu.Stavka stavka = new NalogZaOtpremu.Stavka();
            stavka.setSifraRobe(itemJson.getString("sku"));
            stavka.setSifraObelezja(itemJson.getString("size"));
            stavka.setKolicina(new BigDecimal(itemJson.getString("qty")));


            // Other static fields (set to 0 or fixed values)
            stavka.setAkcijskaStopaRabata(BigDecimal.ZERO);
            stavka.setBrojPakovanja(BigDecimal.ZERO);
            stavka.setBruto(BigDecimal.ZERO);
            stavka.setIznosAkcize(BigDecimal.ZERO);
            stavka.setIznosManipulativnihTroskova(BigDecimal.ZERO);
            stavka.setIznosTakse(BigDecimal.ZERO);
            stavka.setPosebnaStopaRabata(BigDecimal.ZERO);
            stavka.setStopaRabata(BigDecimal.ZERO);

            stavke.add(stavka);
        }
        nalogZaOtpremu.setStavke(stavke);

        return nalogZaOtpremu;
    }

    private String convertToSoapRequest(NalogZaOtpremu nalogZaOtpremu, String system, String warehouse, String pricelist,String destincation) {
        StringBuilder stavkeXml = new StringBuilder();



        for (NalogZaOtpremu.Stavka stavka : nalogZaOtpremu.getStavke()) {

            PriceInfoDTO priceInfo = priceCheckService.checkPrices(system, warehouse, stavka.getSifraRobe(), pricelist);

            stavkeXml.append(String.format(
                    "<stavke>" +
                            "<akcijskaStopaRabata>0</akcijskaStopaRabata>" +
                            "<brojPakovanja>0</brojPakovanja>" +
                            "<bruto>0</bruto>" +
                            "<cenaSaRabatom>%.2f</cenaSaRabatom>" +
                            "<deviznaCena>%.2f</deviznaCena>" +
                            "<dodatniRabat>0</dodatniRabat>" +
                            "<iznosAkcize>0</iznosAkcize>" +
                            "<iznosManipulativnihTroskova>0</iznosManipulativnihTroskova>" +
                            "<iznosTakse>0</iznosTakse>" +
                            "<kolicina>%.2f</kolicina>" +
                            "<osnovnaCena>%.2f</osnovnaCena>" +
                            "<posebnaStopaRabata>0</posebnaStopaRabata>" +
                            "<prodajnaCena>%.2f</prodajnaCena>" +
                            "<rabatBezAkciza>0</rabatBezAkciza>" +
                            "<sifraObelezja>%s</sifraObelezja>" +
                            "<sifraRobe>%s</sifraRobe>" +
                            "<stopaManipulativnihTroskova>0</stopaManipulativnihTroskova>" +
                            "<stopaPoreza>20</stopaPoreza>" +
                            "<stopaRabata>0</stopaRabata>" +
                            "<zahtevanaKolicina>%.2f</zahtevanaKolicina>" +
                            "</stavke>",
                    priceInfo.getVp(),             // cenaSaRabatom (prices[3] je bio vp)
                    priceInfo.getVp(),             // deviznaCena
                    stavka.getKolicina(),
                    priceInfo.getVp(),             // osnovnaCena
                    priceInfo.getMpBezPdv(),       // prices[1]        // prodajnaCena (same as osnovnaCena here)
                    stavka.getSifraObelezja(),
                    stavka.getSifraRobe(),
                    stavka.getKolicina()
            ));
        }

        String  partner = priceCheckService.getFransizeData(system, destincation);


        return String.format(
                "<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                        "    <Body>\n" +
                        "        <dodajNalogZaIzdavanje xmlns=\"http://dokumenti.servis.mis.com/\">\n" +
                        "            <dokumenti xmlns=\"\">\n" +
                        "                <akcijskiRabatVrednost>0</akcijskiRabatVrednost>\n" +
                        "                <brojRata>0</brojRata>\n" +
                        "                <cenaPrevoza>0</cenaPrevoza>\n" +
                        "                <datumDokumenta>%s</datumDokumenta>\n" +
                        "                <datumOtpreme>%s</datumOtpreme>\n" +
                        "                <dpo>%s</dpo>\n" +
                        "                <valutaPlacanja>%s</valutaPlacanja>\n" +
                        "                <logname>%s</logname>\n" +
                        "                <napomena>%s</napomena>\n" +
                        "                <oznakaCenovnika>%s</oznakaCenovnika>\n" +
                        "                <oznakaDokumenta>%s</oznakaDokumenta>\n" +
                        "                <realizovano>N</realizovano>\n" +
                        "                <sifraNacinaPlacanja>1</sifraNacinaPlacanja>\n" +
                        "                <sifraOrganizacioneJednice>01</sifraOrganizacioneJednice>\n" +
                        "                <sifraMagacina>%s</sifraMagacina>\n" +
                        "                <status>0</status>\n" +
                        "                <stopaManipulativnihTroskova>0</stopaManipulativnihTroskova>\n" +
                        "                <iznosManipulativnihTroskova>0</iznosManipulativnihTroskova>\n" +
                        "                <iznosKasaSkonto>0</iznosKasaSkonto>\n" +
                        "                <kasaSkonto>0</kasaSkonto>\n" +
                        "                <marza>0</marza>\n" +
                        "                %s\n" +
                        "                <storno>N</storno>\n" +
                        "                <vrstaFakturisanja>1</vrstaFakturisanja>\n" +
                        "                <vrstaIzjave>2</vrstaIzjave>\n" +
                        "                <sifraRacuna>RB</sifraRacuna>\n" +
                        "                <sifraPartnera>%s</sifraPartnera>\n" +
                        "                <sifraPartneraKorisnika>%s</sifraPartneraKorisnika>\n" +
                        "                <tipNaloga>1</tipNaloga>\n" +
                        "                <vrstaPrevoza>1</vrstaPrevoza>\n" +
                        "            </dokumenti>\n" +
                        "        </dodajNalogZaIzdavanje>\n" +
                        "    </Body>\n" +
                        "</Envelope>",
                nalogZaOtpremu.getDatumDokumenta(),
                nalogZaOtpremu.getDatumOtpreme(),
                nalogZaOtpremu.getDpo(),
                nalogZaOtpremu.getValutaPlacanja(),
                nalogZaOtpremu.getLogname(),
                nalogZaOtpremu.getNapomena(),
                nalogZaOtpremu.getOznakaCenovnika(),
                nalogZaOtpremu.getOznakaDokumenta(),
                nalogZaOtpremu.getSifraMagacina(),
                stavkeXml.toString(),
                partner,
                partner
        );
    }
}
