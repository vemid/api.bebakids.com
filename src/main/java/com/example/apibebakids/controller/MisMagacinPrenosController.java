package com.example.apibebakids.controller;

import com.example.apibebakids.model.MisMagacinPrenos;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import com.example.apibebakids.service.SoapResponseParserService;
import com.example.apibebakids.service.AuthHeaderService;
import com.example.apibebakids.service.PriceCheckService;


@RestController
@RequestMapping("/api")
public class MisMagacinPrenosController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SoapResponseParserService soapResponseParserService;

    @Autowired
    private AuthHeaderService authHeaderService;

    @Autowired
    private PriceCheckService priceCheckService;

    @Value("${soap.service.urlWATCH}")
    private String soapServiceUrlWatch;

    @Value("${soap.service.urlBK}")
    private String soapServiceUrlBK;

    @Value("${soap.service.urlCF}")
    private String soapServiceUrlCF;

    @Value("${soap.service.urlBKBIH}")
    private String soapServiceUrlBKBIH;

    // Basic Authentication kredencijali
    @Value("${soap.service.username}")
    private String username;

    @Value("${soap.service.password}")
    private String password;

    private String getUrl(String system) {
        switch (system.toLowerCase()) {
            case "bebakids":
                return soapServiceUrlBK;
            case "watch":
                return soapServiceUrlWatch;
            case "geox":
                return soapServiceUrlCF;
            case "bebakidsbih":
                return soapServiceUrlBKBIH;
            default:
                throw new IllegalArgumentException("Invalid system: " + system);
        }
    }

    @PostMapping("/createMisMagacinPrenos")
    @Tag(name = "Document API", description = "API za kreiranje dokumenata")
    public ResponseEntity<?> dodajPrenos(@RequestBody MisMagacinPrenos misMagacinPrenos) {
        String system = misMagacinPrenos.getSystem();
        try {

            if (system.equals("bebakids") || system.equals("watch") || system.equals("geox") || system.equals("bebakidsbih")) {
                String soapRequest = convertToSoapRequest(misMagacinPrenos);
                String url = getUrl(system);

                // Use AuthHeaderService to create headers with Basic Authentication
                HttpHeaders headers = authHeaderService.createHeadersWithBasicAuth(username, password);
                headers.setContentType(MediaType.TEXT_XML);

                HttpEntity<String> requestEntity = new HttpEntity<>(soapRequest, headers);

                ResponseEntity<String> soapResponse = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

                return soapResponseParserService.parseSoapResponse(soapResponse.getBody());
            }
            else {
                return ResponseEntity.status(500).body("System is not defined");
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    private String convertToSoapRequest(MisMagacinPrenos misMagacinPrenos) {
        // Kreiranje SOAP XML zahteva na osnovu JSON objekta
        String stavkeXml = "";
        for (MisMagacinPrenos.MisMagacinPrenosStavka stavka : misMagacinPrenos.getStavke()) {

//            Double nabavnaCena = priceCheckService.checkPrice(prenosRequest.getSifraMagacinaIzlaza(), stavka.getSifraRobe());
//
//            // Ako nema cene, postavi na 0 ili neku drugu vrednost
//            if (nabavnaCena == null) {
//                nabavnaCena = 0.0;
//            }

            stavkeXml += String.format("<stavke>" +
                                "<kolicina>%d</kolicina>" +
                                "<sifraRobe>%s</sifraRobe>" +
                                "<sifraObelezja>%s</sifraObelezja>" +
                                "<sifraObelezjaU>%s</sifraObelezjaU>" +
                                "<zonaMagacina>DEF_ZON</zonaMagacina>" +
                                "<zonaMagacinaU>DEF_ZON</zonaMagacinaU>" +
//                            "<nabavnaCena>%.2f</nabavnaCena>" +
                            "</stavke>",
                    stavka.getKolicina(), stavka.getSifraRobe(),stavka.getVelicinaRobe(),stavka.getVelicinaRobe());
        }

        return String.format(
                "<Envelope xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                        "    <Body>\n" +
                        "        <dodajPrenos xmlns=\"http://dokumenti.servis.mis.com/\">\n" +
                        "            <prenosi xmlns=\"\">\n" +
                        "                <datumDokumenta>%s</datumDokumenta>\n" +
                        "                <logname>%s</logname>\n" +
                        "                <oznakaDokumenta>%s</oznakaDokumenta>\n" +
                        "                <napomena>%s</napomena>\n" +
                        "                <sifraMagacinaIzlaza>%s</sifraMagacinaIzlaza>\n" +
                        "                <sifraMagacinaUlaza>%s</sifraMagacinaUlaza>\n" +
                        "                %s\n" + // Stavke se ovde umetnu
                        "                <storno>%s</storno>\n" +
                        "                <vrstaKnjizenja>%s</vrstaKnjizenja>\n" +
                        "                <vrstaPrenosa>%s</vrstaPrenosa>\n" +
                        "            </prenosi>\n" +
                        "        </dodajPrenos>\n" +
                        "    </Body>\n" +
                        "</Envelope>",
                misMagacinPrenos.getDatumDokumenta(),
                misMagacinPrenos.getLogname(),
                misMagacinPrenos.getOznakaDokumenta(),
                misMagacinPrenos.getNapomena(),
                misMagacinPrenos.getSifraMagacinaIzlaza(),
                misMagacinPrenos.getSifraMagacinaUlaza(),
                stavkeXml,
                misMagacinPrenos.getStorno(),
                misMagacinPrenos.getVrstaKnjizenja(),
                misMagacinPrenos.getVrstaPrenosa()
        );
    }
}
