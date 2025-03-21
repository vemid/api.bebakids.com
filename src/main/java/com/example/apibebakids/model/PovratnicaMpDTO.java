package com.example.apibebakids.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * DTO za povratnicu maloprodaje
 */
public class PovratnicaMpDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String oznakaDokumenta;
    private Date datumDokumenta;
    private String sifraMagacina;
    private String sifraMaloprodajnogObjekta;
    private String system;
    private String logname;
    private String oznakaCenovnika;
    private String razlogVracanja;
    private String napomena;
    private VrstaKnjizenjaMp vrstaKnjizenja;
    private String smena;
    private String vrstaPovrataMP;
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

    public String getSifraMaloprodajnogObjekta() {
        return sifraMaloprodajnogObjekta;
    }

    public void setSifraMaloprodajnogObjekta(String sifraMaloprodajnogObjekta) {
        this.sifraMaloprodajnogObjekta = sifraMaloprodajnogObjekta;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getLogname() {
        return logname;
    }

    public void setLogname(String logname) {
        this.logname = logname;
    }

    public String getOznakaCenovnika() {
        return oznakaCenovnika;
    }

    public void setOznakaCenovnika(String oznakaCenovnika) {
        this.oznakaCenovnika = oznakaCenovnika;
    }

    public String getRazlogVracanja() {
        return razlogVracanja;
    }

    public void setRazlogVracanja(String razlogVracanja) {
        this.razlogVracanja = razlogVracanja;
    }

    public String getNapomena() {
        return napomena;
    }

    public void setNapomena(String napomena) {
        this.napomena = napomena;
    }

    public VrstaKnjizenjaMp getVrstaKnjizenja() {
        return vrstaKnjizenja;
    }

    public void setVrstaKnjizenja(VrstaKnjizenjaMp vrstaKnjizenja) {
        this.vrstaKnjizenja = vrstaKnjizenja;
    }

    public String getSmena() {
        return smena;
    }

    public void setSmena(String smena) {
        this.smena = smena;
    }

    public String getVrstaPovrataMP() {
        return vrstaPovrataMP;
    }

    public void setVrstaPovrataMP(String vrstaPovrataMP) {
        this.vrstaPovrataMP = vrstaPovrataMP;
    }

    public List<Stavka> getStavke() {
        return stavke;
    }

    public void setStavke(List<Stavka> stavke) {
        this.stavke = stavke;
    }

    // Inner class for items
    public static class Stavka implements Serializable {
        private static final long serialVersionUID = 1L;

        private String sifraRobe;
        private String sifraObelezja;
        private BigDecimal kolicina;
        private BigDecimal nabavnaCena;
        private BigDecimal prodajnaCena;
        private BigDecimal prodajnaCenaBezPoreza;
        private String sifraZone;

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

        public String getSifraZone() {
            return sifraZone;
        }

        public void setSifraZone(String sifraZone) {
            this.sifraZone = sifraZone;
        }
    }
}