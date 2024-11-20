package com.example.apibebakids.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Represents a return document from retail to warehouse")
public class PovratnicaMaloprodaje {

    @Schema(description = "Date and time of the document creation")
    private LocalDateTime datumDokumenta;

    @Schema(description = "Username of the person creating the document")
    private String logname;

    @Schema(description = "Additional notes for the document")
    private String napomena;

    @Schema(description = "Pricelist identifier")
    private String oznakaCenovnika;

    @Schema(description = "Document identifier")
    private String oznakaDokumenta;

    @Schema(description = "Reason for return")
    private String razlogVracanja;

    @Schema(description = "Warehouse code")
    private String sifraMagacina;

    @Schema(description = "Retail store code")
    private String sifraMaloprodajnogObjekta;

    @Schema(description = "List of items in the return document")
    private List<Stavka> stavke;

    @Schema(description = "Return posting type for the warehouse")
    private String vrstaKnjizenjaPovrataMagacin;

    // Getters and setters

    @Schema(description = "Represents an item in the return document")
    public static class Stavka {
        @Schema(description = "Quantity of the item")
        private BigDecimal kolicina;

        @Schema(description = "Product attribute code")
        private String sifraObelezja;

        @Schema(description = "Product SKU")
        private String sifraRobe;

        @Schema(description = "Warehouse zone code")
        private String sifraZone;

        // Getters and setters for all fields

        public BigDecimal getKolicina() {
            return kolicina;
        }

        public void setKolicina(BigDecimal kolicina) {
            this.kolicina = kolicina;
        }

        public String getSifraObelezja() {
            return sifraObelezja;
        }

        public void setSifraObelezja(String sifraObelezja) {
            this.sifraObelezja = sifraObelezja;
        }

        public String getSifraRobe() {
            return sifraRobe;
        }

        public void setSifraRobe(String sifraRobe) {
            this.sifraRobe = sifraRobe;
        }

        public String getSifraZone() {
            return sifraZone;
        }

        public void setSifraZone(String sifraZone) {
            this.sifraZone = sifraZone;
        }
    }

    public LocalDateTime getDatumDokumenta() {
        return datumDokumenta;
    }

    public void setDatumDokumenta(LocalDateTime datumDokumenta) {
        this.datumDokumenta = datumDokumenta;
    }

    public String getLogname() {
        return logname;
    }

    public void setLogname(String logname) {
        this.logname = logname;
    }

    public String getNapomena() {
        return napomena;
    }

    public void setNapomena(String napomena) {
        this.napomena = napomena;
    }

    public String getOznakaCenovnika() {
        return oznakaCenovnika;
    }

    public void setOznakaCenovnika(String oznakaCenovnika) {
        this.oznakaCenovnika = oznakaCenovnika;
    }

    public String getOznakaDokumenta() {
        return oznakaDokumenta;
    }

    public void setOznakaDokumenta(String oznakaDokumenta) {
        this.oznakaDokumenta = oznakaDokumenta;
    }

    public String getRazlogVracanja() {
        return razlogVracanja;
    }

    public void setRazlogVracanja(String razlogVracanja) {
        this.razlogVracanja = razlogVracanja;
    }

    public String getSifraMagacina() {
        return sifraMagacina;
    }

    public void setSifraMagacina(String sifraMagacina) {
        this.sifraMagacina = sifraMagacina;
    }

    public String getSifraMaloprodajnogObjekta() {
        return sifraMaloprodajnogObjekta;
    }

    public void setSifraMaloprodajnogObjekta(String sifraMaloprodajnogObjekta) {
        this.sifraMaloprodajnogObjekta = sifraMaloprodajnogObjekta;
    }

    public List<Stavka> getStavke() {
        return stavke;
    }

    public void setStavke(List<Stavka> stavke) {
        this.stavke = stavke;
    }

    public String getVrstaKnjizenjaPovrataMagacin() {
        return vrstaKnjizenjaPovrataMagacin;
    }

    public void setVrstaKnjizenjaPovrataMagacin(String vrstaKnjizenjaPovrataMagacin) {
        this.vrstaKnjizenjaPovrataMagacin = vrstaKnjizenjaPovrataMagacin;
    }
}
