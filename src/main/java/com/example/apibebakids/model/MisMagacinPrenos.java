package com.example.apibebakids.model;

import java.util.List;

public class MisMagacinPrenos {

    private String system;
    private String datumDokumenta;
    private String logname;
    private String oznakaDokumenta;
    private String napomena;
    private String sifraMagacinaIzlaza;
    private String sifraMagacinaUlaza;
    private List<MisMagacinPrenosStavka> stavke;
    private String storno;
    private String vrstaKnjizenja;
    private String vrstaPrenosa;

    // Getteri i setteri za sva polja

    public static class MisMagacinPrenosStavka {
        private int kolicina;
        private String sifraRobe;
        private String velicinaRobe;

        // Getteri i setteri za stavke
        public int getKolicina() {
            return kolicina;
        }

        public void setKolicina(int kolicina) {
            this.kolicina = kolicina;
        }

        public String getSifraRobe() {
            return sifraRobe;
        }

        public void setSifraRobe(String sifraRobe) {
            this.sifraRobe = sifraRobe;
        }

        public String getVelicinaRobe() {
            return velicinaRobe;
        }

        public void setVelicinaRobe(String velicinaRobe) {
            this.velicinaRobe = velicinaRobe;
        }
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    // Getteri i setteri za PrenosRequest
    public String getDatumDokumenta() {
        return datumDokumenta;
    }

    public void setDatumDokumenta(String datumDokumenta) {
        this.datumDokumenta = datumDokumenta;
    }

    public String getLogname() {
        return logname;
    }

    public void setLogname(String logname) {
        this.logname = logname;
    }

    public String getOznakaDokumenta() {
        return oznakaDokumenta;
    }

    public void setOznakaDokumenta(String oznakaDokumenta) {
        this.oznakaDokumenta = oznakaDokumenta;
    }

    public String getNapomena() {
        return napomena;
    }

    public void setNapomena(String napomena) {
        this.napomena = napomena;
    }

    public String getSifraMagacinaIzlaza() {
        return sifraMagacinaIzlaza;
    }

    public void setSifraMagacinaIzlaza(String sifraMagacinaIzlaza) {
        this.sifraMagacinaIzlaza = sifraMagacinaIzlaza;
    }

    public String getSifraMagacinaUlaza() {
        return sifraMagacinaUlaza;
    }

    public void setSifraMagacinaUlaza(String sifraMagacinaUlaza) {
        this.sifraMagacinaUlaza = sifraMagacinaUlaza;
    }

    public List<MisMagacinPrenosStavka> getStavke() {
        return stavke;
    }

    public void setStavke(List<MisMagacinPrenosStavka> stavke) {
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

    public String getVrstaPrenosa() {
        return vrstaPrenosa;
    }

    public void setVrstaPrenosa(String vrstaPrenosa) {
        this.vrstaPrenosa = vrstaPrenosa;
    }
}
