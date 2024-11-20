package com.example.apibebakids.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Represents a retail transfer document")
public class PrenosnicaMaloprodaje {

    @Schema(description = "System identifier (bebakids, watch, or geox)")
    private String system;

    @Schema(description = "Date and time of the document creation")
    private LocalDateTime datumDokumenta;

    @Schema(description = "Username of the person creating the document")
    private String logname;

    @Schema(description = "Code of the source store")
    private String objekatIzlaza;

    @Schema(description = "Code of the destination store")
    private String objekatUlaza;

    @Schema(description = "Document identifier")
    private String oznakaDokumenta;

    @Schema(description = "Order number associated with the transfer")
    private String oznakaNarudzbenice;

    @Schema(description = "List of items in the transfer")
    private List<Stavka> stavke;

    @Schema(description = "Total value of the transfer including tax")
    private BigDecimal vrednostSaPorezom;

    @Schema(description = "Type of bookkeeping entry")
    private String vrstaKnjizenja;

    @Schema(description = "Type of transfer")
    private String vrstaPrevoza;


    @Schema(description = "Pricelist identifier")
    private String pricelist;


    // Getters and Setters

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
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

    public String getObjekatIzlaza() {
        return objekatIzlaza;
    }

    public void setObjekatIzlaza(String objekatIzlaza) {
        this.objekatIzlaza = objekatIzlaza;
    }

    public String getObjekatUlaza() {
        return objekatUlaza;
    }

    public void setObjekatUlaza(String objekatUlaza) {
        this.objekatUlaza = objekatUlaza;
    }

    public String getOznakaDokumenta() {
        return oznakaDokumenta;
    }

    public void setOznakaDokumenta(String oznakaDokumenta) {
        this.oznakaDokumenta = oznakaDokumenta;
    }

    public String getOznakaNarudzbenice() {
        return oznakaNarudzbenice;
    }

    public void setOznakaNarudzbenice(String oznakaNarudzbenice) {
        this.oznakaNarudzbenice = oznakaNarudzbenice;
    }

    public List<Stavka> getStavke() {
        return stavke;
    }

    public void setStavke(List<Stavka> stavke) {
        this.stavke = stavke;
    }

    public BigDecimal getVrednostSaPorezom() {
        return vrednostSaPorezom;
    }

    public void setVrednostSaPorezom(BigDecimal vrednostSaPorezom) {
        this.vrednostSaPorezom = vrednostSaPorezom;
    }

    public String getVrstaKnjizenja() {
        return vrstaKnjizenja;
    }

    public void setVrstaKnjizenja(String vrstaKnjizenja) {
        this.vrstaKnjizenja = vrstaKnjizenja;
    }

    public String getVrstaPrevoza() {
        return vrstaPrevoza;
    }

    public void setVrstaPrevoza(String vrstaPrevoza) {
        this.vrstaPrevoza = vrstaPrevoza;
    }


    public String getPricelist() {
        return pricelist;
    }

    public void setPricelist(String pricelist) {
        this.pricelist = pricelist;
    }

    // Nested Stavka class with Getters and Setters
    @Schema(description = "Represents an item in the transfer document")
    public static class Stavka {

        @Schema(description = "Product SKU")
        private String sifraRobe;

        @Schema(description = "Product size or attribute")
        private String sifraObelezja;

        @Schema(description = "Quantity of the item")
        private BigDecimal kolicina;

        @Schema(description = "Purchase price of the item")
        private BigDecimal nabavnaCena;

        @Schema(description = "Selling price of the item")
        private BigDecimal prodajnaCena;

        @Schema(description = "Selling price without tax")
        private BigDecimal prodajnaCenaBezPoreza;

        @Schema(description = "Tax rate for the item")
        private BigDecimal stopaPoreza;

        // Getters and Setters

        public String getSifraRobe() {
            return sifraRobe;
        }

        public void setSifraRobe(String sifraRobe) {
            this.sifraRobe = sifraRobe;
        }

        public String getSifraObelezja() {
            return sifraObelezja;
        }

        public void setSifraObelezja(String sifraObelezja) {
            this.sifraObelezja = sifraObelezja;
        }

        public BigDecimal getKolicina() {
            return kolicina;
        }

        public void setKolicina(BigDecimal kolicina) {
            this.kolicina = kolicina;
        }

        public BigDecimal getNabavnaCena() {
            return nabavnaCena;
        }

        public void setNabavnaCena(BigDecimal nabavnaCena) {
            this.nabavnaCena = nabavnaCena;
        }

        public BigDecimal getProdajnaCena() {
            return prodajnaCena;
        }

        public void setProdajnaCena(BigDecimal prodajnaCena) {
            this.prodajnaCena = prodajnaCena;
        }

        public BigDecimal getProdajnaCenaBezPoreza() {
            return prodajnaCenaBezPoreza;
        }

        public void setProdajnaCenaBezPoreza(BigDecimal prodajnaCenaBezPoreza) {
            this.prodajnaCenaBezPoreza = prodajnaCenaBezPoreza;
        }

        public BigDecimal getStopaPoreza() {
            return stopaPoreza;
        }

        public void setStopaPoreza(BigDecimal stopaPoreza) {
            this.stopaPoreza = stopaPoreza;
        }
    }
}
