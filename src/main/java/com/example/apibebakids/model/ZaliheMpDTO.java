package com.example.apibebakids.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class ZaliheMpDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String sifraRobe;
    private String sifraObjekataMaloprodaje;
    private BigDecimal kolicina;
    private BigDecimal prosecnaNabavnaCena;
    private BigDecimal veleprodajnaCena;
    private BigDecimal maloprodajnaCena;
    private BigDecimal maloprodajnaCenaBezPoreza;

    public String getSifraRobe() {
        return sifraRobe;
    }

    public void setSifraRobe(String sifraRobe) {
        this.sifraRobe = sifraRobe;
    }

    public String getSifraObjekataMaloprodaje() {
        return sifraObjekataMaloprodaje;
    }

    public void setSifraObjekataMaloprodaje(String sifraObjekataMaloprodaje) {
        this.sifraObjekataMaloprodaje = sifraObjekataMaloprodaje;
    }

    public BigDecimal getKolicina() {
        return kolicina;
    }

    public void setKolicina(BigDecimal kolicina) {
        this.kolicina = kolicina;
    }

    public BigDecimal getProsecnaNabavnaCena() {
        return prosecnaNabavnaCena;
    }

    public void setProsecnaNabavnaCena(BigDecimal prosecnaNabavnaCena) {
        this.prosecnaNabavnaCena = prosecnaNabavnaCena;
    }

    public BigDecimal getVeleprodajnaCena() {
        return veleprodajnaCena;
    }

    public void setVeleprodajnaCena(BigDecimal veleprodajnaCena) {
        this.veleprodajnaCena = veleprodajnaCena;
    }

    public BigDecimal getMaloprodajnaCena() {
        return maloprodajnaCena;
    }

    public void setMaloprodajnaCena(BigDecimal maloprodajnaCena) {
        this.maloprodajnaCena = maloprodajnaCena;
    }

    public BigDecimal getMaloprodajnaCenaBezPoreza() {
        return maloprodajnaCenaBezPoreza;
    }

    public void setMaloprodajnaCenaBezPoreza(BigDecimal maloprodajnaCenaBezPoreza) {
        this.maloprodajnaCenaBezPoreza = maloprodajnaCenaBezPoreza;
    }
}