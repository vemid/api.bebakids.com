package com.example.apibebakids.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Represents a retail shipping document")
public class OtpremnicaUMaloprodaju {

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

    @Schema(description = "Warehouse code")
    private String sifraMagacina;

    @Schema(description = "Retail store code")
    private String sifraObjektaMaloprodaje;

    @Schema(description = "List of items in the shipment")
    private List<StavkaOtpremnice> stavke;

    @Schema(description = "Cancellation flag")
    private String storno;

    @Schema(description = "Type of bookkeeping entry")
    private String vrstaKnjizenja;

    // Getters and Setters

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

    public String getSifraMagacina() {
        return sifraMagacina;
    }

    public void setSifraMagacina(String sifraMagacina) {
        this.sifraMagacina = sifraMagacina;
    }

    public String getSifraObjektaMaloprodaje() {
        return sifraObjektaMaloprodaje;
    }

    public void setSifraObjektaMaloprodaje(String sifraObjektaMaloprodaje) {
        this.sifraObjektaMaloprodaje = sifraObjektaMaloprodaje;
    }

    public List<StavkaOtpremnice> getStavke() {
        return stavke;
    }

    public void setStavke(List<StavkaOtpremnice> stavke) {
        this.stavke = stavke;
    }

    public String getStorno() {
        return storno;
    }

    public void setStorno(String storno) {
        this.storno = storno;
    }

    public String getVrstaKnjizenja() {
        return vrstaKnjizenja;
    }

    public void setVrstaKnjizenja(String vrstaKnjizenja) {
        this.vrstaKnjizenja = vrstaKnjizenja;
    }

    // Nested class for items (stavke)
    @Schema(description = "Represents an item in the shipment")
    public static class StavkaOtpremnice {

        @Schema(description = "Action rebate rate")
        private BigDecimal akcijskaStopaRabata;

        @Schema(description = "Number of packages")
        private BigDecimal brojPakovanja;

        @Schema(description = "Inventory price")
        private BigDecimal cenaZalihe;

        @Schema(description = "Excise duty amount")
        private BigDecimal iznosAkcize;

        @Schema(description = "Tax amount")
        private BigDecimal iznosTakse;

        @Schema(description = "Quantity")
        private BigDecimal kolicina;

        @Schema(description = "Second quantity field")
        private BigDecimal kolicina1;

        @Schema(description = "Retail margin")
        private BigDecimal maloprodajnaMarza;

        @Schema(description = "Notes for the item")
        private String napomena;

        @Schema(description = "Basic price")
        private BigDecimal osnovnaCena;

        @Schema(description = "Special rebate rate")
        private BigDecimal posebnaStopaRabata;

        @Schema(description = "Selling price")
        private BigDecimal prodajnaCena;

        @Schema(description = "Selling price excluding tax")
        private BigDecimal prodajnaCenaBezPoreza;

        @Schema(description = "Selling price with rebate")
        private BigDecimal prodajnaCenaSaRabatom;

        @Schema(description = "Product SKU")
        private String sifraRobe;

        @Schema(description = "Product attribute code")
        private String sifraObelezja;

        @Schema(description = "Section code")
        private String sifraOdeljka;

        @Schema(description = "Package code")
        private String sifraPakovanja;

        @Schema(description = "Tax group code")
        private String sifraTarifneGrupePoreza;

        @Schema(description = "Excise group code")
        private String sifraTarifneGrupeTakse;

        @Schema(description = "Warehouse zone code")
        private String sifraZoneMagacina;

        @Schema(description = "VAT rate")
        private BigDecimal stopaPDV;

        @Schema(description = "Rebate rate")
        private BigDecimal stopaRabata;

        // Getters and Setters

        public BigDecimal getAkcijskaStopaRabata() {
            return akcijskaStopaRabata;
        }

        public void setAkcijskaStopaRabata(BigDecimal akcijskaStopaRabata) {
            this.akcijskaStopaRabata = akcijskaStopaRabata;
        }

        public BigDecimal getBrojPakovanja() {
            return brojPakovanja;
        }

        public void setBrojPakovanja(BigDecimal brojPakovanja) {
            this.brojPakovanja = brojPakovanja;
        }

        public BigDecimal getCenaZalihe() {
            return cenaZalihe;
        }

        public void setCenaZalihe(BigDecimal cenaZalihe) {
            this.cenaZalihe = cenaZalihe;
        }

        public BigDecimal getIznosAkcize() {
            return iznosAkcize;
        }

        public void setIznosAkcize(BigDecimal iznosAkcize) {
            this.iznosAkcize = iznosAkcize;
        }

        public BigDecimal getIznosTakse() {
            return iznosTakse;
        }

        public void setIznosTakse(BigDecimal iznosTakse) {
            this.iznosTakse = iznosTakse;
        }

        public BigDecimal getKolicina() {
            return kolicina;
        }

        public void setKolicina(BigDecimal kolicina) {
            this.kolicina = kolicina;
        }

        public BigDecimal getKolicina1() {
            return kolicina1;
        }

        public void setKolicina1(BigDecimal kolicina1) {
            this.kolicina1 = kolicina1;
        }

        public BigDecimal getMaloprodajnaMarza() {
            return maloprodajnaMarza;
        }

        public void setMaloprodajnaMarza(BigDecimal maloprodajnaMarza) {
            this.maloprodajnaMarza = maloprodajnaMarza;
        }

        public String getNapomena() {
            return napomena;
        }

        public void setNapomena(String napomena) {
            this.napomena = napomena;
        }

        public BigDecimal getOsnovnaCena() {
            return osnovnaCena;
        }

        public void setOsnovnaCena(BigDecimal osnovnaCena) {
            this.osnovnaCena = osnovnaCena;
        }

        public BigDecimal getPosebnaStopaRabata() {
            return posebnaStopaRabata;
        }

        public void setPosebnaStopaRabata(BigDecimal posebnaStopaRabata) {
            this.posebnaStopaRabata = posebnaStopaRabata;
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

        public BigDecimal getProdajnaCenaSaRabatom() {
            return prodajnaCenaSaRabatom;
        }

        public void setProdajnaCenaSaRabatom(BigDecimal prodajnaCenaSaRabatom) {
            this.prodajnaCenaSaRabatom = prodajnaCenaSaRabatom;
        }

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

        public String getSifraOdeljka() {
            return sifraOdeljka;
        }

        public void setSifraOdeljka(String sifraOdeljka) {
            this.sifraOdeljka = sifraOdeljka;
        }

        public String getSifraPakovanja() {
            return sifraPakovanja;
        }

        public void setSifraPakovanja(String sifraPakovanja) {
            this.sifraPakovanja = sifraPakovanja;
        }

        public String getSifraTarifneGrupePoreza() {
            return sifraTarifneGrupePoreza;
        }

        public void setSifraTarifneGrupePoreza(String sifraTarifneGrupePoreza) {
            this.sifraTarifneGrupePoreza = sifraTarifneGrupePoreza;
        }

        public String getSifraTarifneGrupeTakse() {
            return sifraTarifneGrupeTakse;
        }

        public void setSifraTarifneGrupeTakse(String sifraTarifneGrupeTakse) {
            this.sifraTarifneGrupeTakse = sifraTarifneGrupeTakse;
        }

        public String getSifraZoneMagacina() {
            return sifraZoneMagacina;
        }

        public void setSifraZoneMagacina(String sifraZoneMagacina) {
            this.sifraZoneMagacina = sifraZoneMagacina;
        }

        public BigDecimal getStopaPDV() {
            return stopaPDV;
        }

        public void setStopaPDV(BigDecimal stopaPDV) {
            this.stopaPDV = stopaPDV;
        }

        public BigDecimal getStopaRabata() {
            return stopaRabata;
        }

        public void setStopaRabata(BigDecimal stopaRabata) {
            this.stopaRabata = stopaRabata;
        }
    }
}
