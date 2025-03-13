package com.example.apibebakids.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class PrenosnicaMpDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String oznakaDokumenta;
    private Date datumDokumenta;
    private String objekatUlaza;
    private String objekatIzlaza;
    private VrstaKnjizenjaMp vrstaKnjizenja;
    private String logname;
    private VrstaPrevozaMp vrstaPrevoza;
    private BigDecimal vrednostSaPorezom;
    private String oznakaNarudzbenice;
    private String system;
    private String oznakaCenovnika;
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

    public String getObjekatUlaza() {
        return objekatUlaza;
    }

    public void setObjekatUlaza(String objekatUlaza) {
        this.objekatUlaza = objekatUlaza;
    }

    public String getObjekatIzlaza() {
        return objekatIzlaza;
    }

    public void setObjekatIzlaza(String objekatIzlaza) {
        this.objekatIzlaza = objekatIzlaza;
    }

    public VrstaKnjizenjaMp getVrstaKnjizenja() {
        return vrstaKnjizenja;
    }

    public void setVrstaKnjizenja(VrstaKnjizenjaMp vrstaKnjizenja) {
        this.vrstaKnjizenja = vrstaKnjizenja;
    }

    public String getLogname() {
        return logname;
    }

    public void setLogname(String logname) {
        this.logname = logname;
    }

    public VrstaPrevozaMp getVrstaPrevoza() {
        return vrstaPrevoza;
    }

    public void setVrstaPrevoza(VrstaPrevozaMp vrstaPrevoza) {
        this.vrstaPrevoza = vrstaPrevoza;
    }

    public BigDecimal getVrednostSaPorezom() {
        return vrednostSaPorezom;
    }

    public void setVrednostSaPorezom(BigDecimal vrednostSaPorezom) {
        this.vrednostSaPorezom = vrednostSaPorezom;
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

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getOznakaCenovnika() {
        return oznakaCenovnika;
    }

    public void setOznakaCenovnika(String oznakaCenovnika) {
        this.oznakaCenovnika = oznakaCenovnika;
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
        private BigDecimal stopaPoreza;
        private String barkod;
        private BigDecimal brojPakovanja;

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

        public String getBarkod() {
            return barkod;
        }

        public void setBarkod(String barkod) {
            this.barkod = barkod;
        }

        public BigDecimal getBrojPakovanja() {
            return brojPakovanja;
        }

        public void setBrojPakovanja(BigDecimal brojPakovanja) {
            this.brojPakovanja = brojPakovanja;
        }
    }
}