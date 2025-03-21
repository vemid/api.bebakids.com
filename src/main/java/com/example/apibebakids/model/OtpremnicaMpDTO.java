package com.example.apibebakids.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * DTO za otpremnicu maloprodaje
 */
public class OtpremnicaMpDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String oznakaDokumenta;
    private Date datumDokumenta;
    private String sifraMagacina;
    private String sifraObjektaMaloprodaje;
    private String storno;
    private String logname;
    private String napomena;
    private String oznakaCenovnika;
    private String system;
    private String vrstaKnjizenja;
    private List<Stavka> stavke;

    // Getters and Setters
    public String getOznakaDokumenta() {
        return oznakaDokumenta;
    }

    public void setOznakaDokumenta(String oznakaDokumenta) {
        this.oznakaDokumenta = oznakaDokumenta;
    }

    public Date getDatumDokumenta() {
        return datumDokumenta;
    }

    public void setDatumDokumenta(Date datumDokumenta) {
        this.datumDokumenta = datumDokumenta;
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

    public String getStorno() {
        return storno;
    }

    public void setStorno(String storno) {
        this.storno = storno;
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

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getVrstaKnjizenja() {
        return vrstaKnjizenja;
    }

    public void setVrstaKnjizenja(String vrstaKnjizenja) {
        this.vrstaKnjizenja = vrstaKnjizenja;
    }

    public List<Stavka> getStavke() {
        if (stavke == null) {
            stavke = new ArrayList<>();
        }
        return stavke;
    }

    public void setStavke(List<Stavka> stavke) {
        this.stavke = stavke;
    }

    // Inner class for stavke
    public static class Stavka implements Serializable {
        private static final long serialVersionUID = 1L;

        private String sifraRobe;
        private String sifraObelezja;
        private BigDecimal kolicina;
        private BigDecimal cenaZalihe;
        private BigDecimal osnovnaCena;
        private BigDecimal prodajnaCena;
        private BigDecimal prodajnaCenaBezPoreza;
        private BigDecimal maloprodajnaMarza;
        private String sifraTarifneGrupePoreza;
        private BigDecimal stopaPDV;
        private String sifraTarifneGrupeTakse;
        private BigDecimal iznosAkcize;
        private BigDecimal iznosTakse;
        private String sifraPakovanja;
        private BigDecimal brojPakovanja;
        private BigDecimal stopaRabata;
        private BigDecimal prodajnaCenaSaRabatom;
        private BigDecimal posebnaStopaRabata;
        private BigDecimal akcijskaStopaRabata;
        private String napomena;
        private String sifraOdeljka;
        private String sifraZoneMagacina;

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

        public BigDecimal getCenaZalihe() {
            return cenaZalihe;
        }

        public void setCenaZalihe(BigDecimal cenaZalihe) {
            this.cenaZalihe = cenaZalihe;
        }

        public BigDecimal getOsnovnaCena() {
            return osnovnaCena;
        }

        public void setOsnovnaCena(BigDecimal osnovnaCena) {
            this.osnovnaCena = osnovnaCena;
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

        public BigDecimal getMaloprodajnaMarza() {
            return maloprodajnaMarza;
        }

        public void setMaloprodajnaMarza(BigDecimal maloprodajnaMarza) {
            this.maloprodajnaMarza = maloprodajnaMarza;
        }

        public String getSifraTarifneGrupePoreza() {
            return sifraTarifneGrupePoreza;
        }

        public void setSifraTarifneGrupePoreza(String sifraTarifneGrupePoreza) {
            this.sifraTarifneGrupePoreza = sifraTarifneGrupePoreza;
        }

        public BigDecimal getStopaPDV() {
            return stopaPDV;
        }

        public void setStopaPDV(BigDecimal stopaPDV) {
            this.stopaPDV = stopaPDV;
        }

        public String getSifraTarifneGrupeTakse() {
            return sifraTarifneGrupeTakse;
        }

        public void setSifraTarifneGrupeTakse(String sifraTarifneGrupeTakse) {
            this.sifraTarifneGrupeTakse = sifraTarifneGrupeTakse;
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

        public String getSifraPakovanja() {
            return sifraPakovanja;
        }

        public void setSifraPakovanja(String sifraPakovanja) {
            this.sifraPakovanja = sifraPakovanja;
        }

        public BigDecimal getBrojPakovanja() {
            return brojPakovanja;
        }

        public void setBrojPakovanja(BigDecimal brojPakovanja) {
            this.brojPakovanja = brojPakovanja;
        }

        public BigDecimal getStopaRabata() {
            return stopaRabata;
        }

        public void setStopaRabata(BigDecimal stopaRabata) {
            this.stopaRabata = stopaRabata;
        }

        public BigDecimal getProdajnaCenaSaRabatom() {
            return prodajnaCenaSaRabatom;
        }

        public void setProdajnaCenaSaRabatom(BigDecimal prodajnaCenaSaRabatom) {
            this.prodajnaCenaSaRabatom = prodajnaCenaSaRabatom;
        }

        public BigDecimal getPosebnaStopaRabata() {
            return posebnaStopaRabata;
        }

        public void setPosebnaStopaRabata(BigDecimal posebnaStopaRabata) {
            this.posebnaStopaRabata = posebnaStopaRabata;
        }

        public BigDecimal getAkcijskaStopaRabata() {
            return akcijskaStopaRabata;
        }

        public void setAkcijskaStopaRabata(BigDecimal akcijskaStopaRabata) {
            this.akcijskaStopaRabata = akcijskaStopaRabata;
        }

        public String getNapomena() {
            return napomena;
        }

        public void setNapomena(String napomena) {
            this.napomena = napomena;
        }

        public String getSifraOdeljka() {
            return sifraOdeljka;
        }

        public void setSifraOdeljka(String sifraOdeljka) {
            this.sifraOdeljka = sifraOdeljka;
        }

        public String getSifraZoneMagacina() {
            return sifraZoneMagacina;
        }

        public void setSifraZoneMagacina(String sifraZoneMagacina) {
            this.sifraZoneMagacina = sifraZoneMagacina;
        }
    }
}