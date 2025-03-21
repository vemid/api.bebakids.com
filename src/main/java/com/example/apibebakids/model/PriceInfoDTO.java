package com.example.apibebakids.model;

public class PriceInfoDTO {
    private double cenZal;
    private double mpBezPdv;
    private double mpSaPdv;
    private double vp;
    private double tarifnaGrupa;
    private double stopaPoreza;

    public PriceInfoDTO(double cenZal, double mpBezPdv, double mpSaPdv, double vp,
                     double tarifnaGrupa, double stopaPoreza) {
        this.cenZal = cenZal;
        this.mpBezPdv = mpBezPdv;
        this.mpSaPdv = mpSaPdv;
        this.vp = vp;
        this.tarifnaGrupa = tarifnaGrupa;
        this.stopaPoreza = stopaPoreza;
    }

    // Getteri
    public double getCenZal() { return cenZal; }
    public double getMpBezPdv() { return mpBezPdv; }
    public double getMpSaPdv() { return mpSaPdv; }
    public double getVp() { return vp; }
    public double getTarifnaGrupa() { return tarifnaGrupa; }
    public double getStopaPoreza() { return stopaPoreza; }

    @Override
    public String toString() {
        return "PriceInfo{" +
                "cenZal=" + cenZal +
                ", mpBezPdv=" + mpBezPdv +
                ", mpSaPdv=" + mpSaPdv +
                ", vp=" + vp +
                ", tarifnaGrupa=" + tarifnaGrupa +
                ", stopaPoreza=" + stopaPoreza +
                '}';
    }
}