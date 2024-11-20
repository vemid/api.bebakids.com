package com.example.apibebakids.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Represents a shipment order document (Nalog Za Otpremu)")
public class NalogZaOtpremu {

    @Schema(description = "Action discount value")
    private BigDecimal akcijskiRabatVrednost;

    @Schema(description = "Number of pallets")
    private BigDecimal brojPaleta;

    @Schema(description = "Number of installments")
    private int brojRata;

    @Schema(description = "Transportation cost")
    private BigDecimal cenaPrevoza;

    @Schema(description = "Document creation date")
    private LocalDateTime datumDokumenta;

    @Schema(description = "Shipment date")
    private LocalDateTime datumOtpreme;

    @Schema(description = "Delivery performance date")
    private LocalDateTime dpo;

    @Schema(description = "Free percentage")
    private int gratisPer;

    @Schema(description = "Cash discount amount")
    private BigDecimal iznosKasaSkonto;

    @Schema(description = "Handling fee")
    private BigDecimal iznosManipulativnihTroskova;

    @Schema(description = "Cash discount percentage")
    private BigDecimal kasaSkonto;

    @Schema(description = "AHP creation")
    private String kreirajAhp;

    @Schema(description = "Username of the person creating the document")
    private String logname;

    @Schema(description = "Profit margin")
    private BigDecimal marza;

    @Schema(description = "Delivery place")
    private String mestoIsporuke;

    @Schema(description = "Pricelist identifier")
    private String oznakaCenovnika;

    @Schema(description = "Document identifier")
    private String oznakaDokumenta;

    @Schema(description = "Invoice identifier")
    private String oznakaRacuna;

    @Schema(description = "Realized flag")
    private String realizovano;

    @Schema(description = "Delivery deadline in days")
    private int rokIsporukeUDanima;

    @Schema(description = "Warehouse code")
    private String sifraMagacina;

    @Schema(description = "Payment method code")
    private String sifraNacinaPlacanja;

    @Schema(description = "Organizational unit code")
    private String sifraOrganizacioneJednice;

    @Schema(description = "Ordering organization unit code")
    private String sifraOrganizacioneJedniceNarucioca;

    @Schema(description = "Partner code")
    private String sifraPartnera;

    @Schema(description = "Partner user code")
    private String sifraPartneraKorisnika;

    @Schema(description = "Invoice code")
    private String sifraRacuna;

    @Schema(description = "Status of the document")
    private String status;

    @Schema(description = "List of items in the shipment order")
    private List<Stavka> stavke;

    @Schema(description = "Handling fee rate")
    private BigDecimal stopaManipulativnihTroskova;

    @Schema(description = "Storno flag")
    private String storno;

    @Schema(description = "Order type")
    private String tipNaloga;

    @Schema(description = "Payment currency date")
    private LocalDateTime valutaPlacanja;

    @Schema(description = "Value of the order")
    private BigDecimal vrednost;

    @Schema(description = "Billing type")
    private String vrstaFakturisanja;

    @Schema(description = "Declaration type")
    private String vrstaIzjave;

    @Schema(description = "Transportation type")
    private String vrstaPrevoza;

    @Schema(description = "Collaborator code")
    private String sifraSaradnika;

    @Schema(description = "Additional notes")
    private String napomena;

    // Getters and setters

    @Schema(description = "Represents an item in the shipment order")
    public static class Stavka {

        @Schema(description = "Discount rate for the item")
        private BigDecimal akcijskaStopaRabata;

        @Schema(description = "Number of packages")
        private BigDecimal brojPakovanja;

        @Schema(description = "Gross amount")
        private BigDecimal bruto;

        @Schema(description = "Price with discount")
        private BigDecimal cenaSaRabatom;

        @Schema(description = "Foreign currency price")
        private BigDecimal deviznaCena;

        @Schema(description = "Additional discount")
        private BigDecimal dodatniRabat;

        @Schema(description = "Excise tax amount")
        private BigDecimal iznosAkcize;

        @Schema(description = "Handling cost amount")
        private BigDecimal iznosManipulativnihTroskova;

        @Schema(description = "Tax amount")
        private BigDecimal iznosTakse;

        @Schema(description = "Quantity")
        private BigDecimal kolicina;

        @Schema(description = "Base price")
        private BigDecimal osnovnaCena;

        @Schema(description = "Special discount rate")
        private BigDecimal posebnaStopaRabata;

        @Schema(description = "Selling price")
        private BigDecimal prodajnaCena;

        @Schema(description = "Discount without excise")
        private BigDecimal rabatBezAkciza;

        @Schema(description = "Attribute code")
        private String sifraObelezja;

        @Schema(description = "Product SKU")
        private String sifraRobe;

        @Schema(description = "Warehouse zone code")
        private String sifraZoneMagacina;

        @Schema(description = "Handling cost rate")
        private BigDecimal stopaManipulativnihTroskova;

        @Schema(description = "Tax rate")
        private BigDecimal stopaPoreza;

        @Schema(description = "Discount rate")
        private BigDecimal stopaRabata;

        @Schema(description = "Requested quantity")
        private BigDecimal zahtevanaKolicina;

        // Getters and setters for all fields

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

        public BigDecimal getBruto() {
            return bruto;
        }

        public void setBruto(BigDecimal bruto) {
            this.bruto = bruto;
        }

        public BigDecimal getCenaSaRabatom() {
            return cenaSaRabatom;
        }

        public void setCenaSaRabatom(BigDecimal cenaSaRabatom) {
            this.cenaSaRabatom = cenaSaRabatom;
        }

        public BigDecimal getDeviznaCena() {
            return deviznaCena;
        }

        public void setDeviznaCena(BigDecimal deviznaCena) {
            this.deviznaCena = deviznaCena;
        }

        public BigDecimal getDodatniRabat() {
            return dodatniRabat;
        }

        public void setDodatniRabat(BigDecimal dodatniRabat) {
            this.dodatniRabat = dodatniRabat;
        }

        public BigDecimal getIznosAkcize() {
            return iznosAkcize;
        }

        public void setIznosAkcize(BigDecimal iznosAkcize) {
            this.iznosAkcize = iznosAkcize;
        }

        public BigDecimal getIznosManipulativnihTroskova() {
            return iznosManipulativnihTroskova;
        }

        public void setIznosManipulativnihTroskova(BigDecimal iznosManipulativnihTroskova) {
            this.iznosManipulativnihTroskova = iznosManipulativnihTroskova;
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

        public BigDecimal getRabatBezAkciza() {
            return rabatBezAkciza;
        }

        public void setRabatBezAkciza(BigDecimal rabatBezAkciza) {
            this.rabatBezAkciza = rabatBezAkciza;
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

        public String getSifraZoneMagacina() {
            return sifraZoneMagacina;
        }

        public void setSifraZoneMagacina(String sifraZoneMagacina) {
            this.sifraZoneMagacina = sifraZoneMagacina;
        }

        public BigDecimal getStopaManipulativnihTroskova() {
            return stopaManipulativnihTroskova;
        }

        public void setStopaManipulativnihTroskova(BigDecimal stopaManipulativnihTroskova) {
            this.stopaManipulativnihTroskova = stopaManipulativnihTroskova;
        }

        public BigDecimal getStopaPoreza() {
            return stopaPoreza;
        }

        public void setStopaPoreza(BigDecimal stopaPoreza) {
            this.stopaPoreza = stopaPoreza;
        }

        public BigDecimal getStopaRabata() {
            return stopaRabata;
        }

        public void setStopaRabata(BigDecimal stopaRabata) {
            this.stopaRabata = stopaRabata;
        }

        public BigDecimal getZahtevanaKolicina() {
            return zahtevanaKolicina;
        }

        public void setZahtevanaKolicina(BigDecimal zahtevanaKolicina) {
            this.zahtevanaKolicina = zahtevanaKolicina;
        }
    }

    // Getters and setters for main class fields

    public BigDecimal getAkcijskiRabatVrednost() {
        return akcijskiRabatVrednost;
    }

    public void setAkcijskiRabatVrednost(BigDecimal akcijskiRabatVrednost) {
        this.akcijskiRabatVrednost = akcijskiRabatVrednost;
    }

    public BigDecimal getBrojPaleta() {
        return brojPaleta;
    }

    public void setBrojPaleta(BigDecimal brojPaleta) {
        this.brojPaleta = brojPaleta;
    }

    public int getBrojRata() {
        return brojRata;
    }

    public void setBrojRata(int brojRata) {
        this.brojRata = brojRata;
    }

    public BigDecimal getCenaPrevoza() {
        return cenaPrevoza;
    }

    public void setCenaPrevoza(BigDecimal cenaPrevoza) {
        this.cenaPrevoza = cenaPrevoza;
    }

    public LocalDateTime getDatumDokumenta() {
        return datumDokumenta;
    }

    public void setDatumDokumenta(LocalDateTime datumDokumenta) {
        this.datumDokumenta = datumDokumenta;
    }

    public LocalDateTime getDatumOtpreme() {
        return datumOtpreme;
    }

    public void setDatumOtpreme(LocalDateTime datumOtpreme) {
        this.datumOtpreme = datumOtpreme;
    }

    public LocalDateTime getDpo() {
        return dpo;
    }

    public void setDpo(LocalDateTime dpo) {
        this.dpo = dpo;
    }

    public int getGratisPer() {
        return gratisPer;
    }

    public void setGratisPer(int gratisPer) {
        this.gratisPer = gratisPer;
    }

    public BigDecimal getIznosKasaSkonto() {
        return iznosKasaSkonto;
    }

    public void setIznosKasaSkonto(BigDecimal iznosKasaSkonto) {
        this.iznosKasaSkonto = iznosKasaSkonto;
    }

    public BigDecimal getIznosManipulativnihTroskova() {
        return iznosManipulativnihTroskova;
    }

    public void setIznosManipulativnihTroskova(BigDecimal iznosManipulativnihTroskova) {
        this.iznosManipulativnihTroskova = iznosManipulativnihTroskova;
    }

    public BigDecimal getKasaSkonto() {
        return kasaSkonto;
    }

    public void setKasaSkonto(BigDecimal kasaSkonto) {
        this.kasaSkonto = kasaSkonto;
    }

    public String getKreirajAhp() {
        return kreirajAhp;
    }

    public void setKreirajAhp(String kreirajAhp) {
        this.kreirajAhp = kreirajAhp;
    }

    public String getLogname() {
        return logname;
    }

    public void setLogname(String logname) {
        this.logname = logname;
    }

    public BigDecimal getMarza() {
        return marza;
    }

    public void setMarza(BigDecimal marza) {
        this.marza = marza;
    }

    public String getMestoIsporuke() {
        return mestoIsporuke;
    }

    public void setMestoIsporuke(String mestoIsporuke) {
        this.mestoIsporuke = mestoIsporuke;
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

    public String getOznakaRacuna() {
        return oznakaRacuna;
    }

    public void setOznakaRacuna(String oznakaRacuna) {
        this.oznakaRacuna = oznakaRacuna;
    }

    public String getRealizovano() {
        return realizovano;
    }

    public void setRealizovano(String realizovano) {
        this.realizovano = realizovano;
    }

    public int getRokIsporukeUDanima() {
        return rokIsporukeUDanima;
    }

    public void setRokIsporukeUDanima(int rokIsporukeUDanima) {
        this.rokIsporukeUDanima = rokIsporukeUDanima;
    }

    public String getSifraMagacina() {
        return sifraMagacina;
    }

    public void setSifraMagacina(String sifraMagacina) {
        this.sifraMagacina = sifraMagacina;
    }

    public String getSifraNacinaPlacanja() {
        return sifraNacinaPlacanja;
    }

    public void setSifraNacinaPlacanja(String sifraNacinaPlacanja) {
        this.sifraNacinaPlacanja = sifraNacinaPlacanja;
    }

    public String getSifraOrganizacioneJednice() {
        return sifraOrganizacioneJednice;
    }

    public void setSifraOrganizacioneJednice(String sifraOrganizacioneJednice) {
        this.sifraOrganizacioneJednice = sifraOrganizacioneJednice;
    }

    public String getSifraOrganizacioneJedniceNarucioca() {
        return sifraOrganizacioneJedniceNarucioca;
    }

    public void setSifraOrganizacioneJedniceNarucioca(String sifraOrganizacioneJedniceNarucioca) {
        this.sifraOrganizacioneJedniceNarucioca = sifraOrganizacioneJedniceNarucioca;
    }

    public String getSifraPartnera() {
        return sifraPartnera;
    }

    public void setSifraPartnera(String sifraPartnera) {
        this.sifraPartnera = sifraPartnera;
    }

    public String getSifraPartneraKorisnika() {
        return sifraPartneraKorisnika;
    }

    public void setSifraPartneraKorisnika(String sifraPartneraKorisnika) {
        this.sifraPartneraKorisnika = sifraPartneraKorisnika;
    }

    public String getSifraRacuna() {
        return sifraRacuna;
    }

    public void setSifraRacuna(String sifraRacuna) {
        this.sifraRacuna = sifraRacuna;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Stavka> getStavke() {
        return stavke;
    }

    public void setStavke(List<Stavka> stavke) {
        this.stavke = stavke;
    }

    public BigDecimal getStopaManipulativnihTroskova() {
        return stopaManipulativnihTroskova;
    }

    public void setStopaManipulativnihTroskova(BigDecimal stopaManipulativnihTroskova) {
        this.stopaManipulativnihTroskova = stopaManipulativnihTroskova;
    }

    public String getStorno() {
        return storno;
    }

    public void setStorno(String storno) {
        this.storno = storno;
    }

    public String getTipNaloga() {
        return tipNaloga;
    }

    public void setTipNaloga(String tipNaloga) {
        this.tipNaloga = tipNaloga;
    }

    public LocalDateTime getValutaPlacanja() {
        return valutaPlacanja;
    }

    public void setValutaPlacanja(LocalDateTime valutaPlacanja) {
        this.valutaPlacanja = valutaPlacanja;
    }

    public BigDecimal getVrednost() {
        return vrednost;
    }

    public void setVrednost(BigDecimal vrednost) {
        this.vrednost = vrednost;
    }

    public String getVrstaFakturisanja() {
        return vrstaFakturisanja;
    }

    public void setVrstaFakturisanja(String vrstaFakturisanja) {
        this.vrstaFakturisanja = vrstaFakturisanja;
    }

    public String getVrstaIzjave() {
        return vrstaIzjave;
    }

    public void setVrstaIzjave(String vrstaIzjave) {
        this.vrstaIzjave = vrstaIzjave;
    }

    public String getVrstaPrevoza() {
        return vrstaPrevoza;
    }

    public void setVrstaPrevoza(String vrstaPrevoza) {
        this.vrstaPrevoza = vrstaPrevoza;
    }

    public String getSifraSaradnika() {
        return sifraSaradnika;
    }

    public void setSifraSaradnika(String sifraSaradnika) {
        this.sifraSaradnika = sifraSaradnika;
    }

    public String getNapomena() {
        return napomena;
    }

    public void setNapomena(String napomena) {
        this.napomena = napomena;
    }
}
