package com.example.apibebakids.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Model koji predstavlja odgovor sa podacima o barkodu")
public class BarcodeResponse {

    @Schema(description = "Indikator uspešnosti odgovora", example = "true")
    private boolean responseResult;

    @Schema(description = "Poruka o grešci, ako postoji", example = "null")
    private String errorMessage;

    @Schema(description = "Broj vraćenih rezultata", example = "1")
    private int respResultCount;

    @Schema(description = "Lista podataka o barkodovima")
    private List<BarcodeData> data;

    @Schema(description = "Model koji predstavlja podatke o pojedinačnom barkodu")
    public static class BarcodeData {
        @Schema(description = "ID promene", example = "12345")
        private Integer id;

        @Schema(description = "Šifra robe", example = "12345")
        private String sifRob;

        @Schema(description = "Naziv robe", example = "Majica")
        private String nazRob;

        @Schema(description = "Šifra entiteta", example = "98765")
        private String sifEntRob;

        @Schema(description = "Barkod", example = "789456123")
        private String barKod;

        @Schema(description = "Nabavna cena", example = "100.00")
        private double nabCena;

        @Schema(description = "Maloprodajna cena", example = "150.00")
        private double malCena;

        // Getters and setters for BarcodeData
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getSifRob() {
            return sifRob;
        }

        public void setSifRob(String sifRob) {
            this.sifRob = sifRob;
        }

        public String getNazRob() {
            return nazRob;
        }

        public void setNazRob(String nazRob) {
            this.nazRob = nazRob;
        }

        public String getSifEntRob() {
            return sifEntRob;
        }

        public void setSifEntRob(String sifEntRob) {
            this.sifEntRob = sifEntRob;
        }

        public String getBarKod() {
            return barKod;
        }

        public void setBarKod(String barKod) {
            this.barKod = barKod;
        }

        public double getNabCena() {
            return nabCena;
        }

        public void setNabCena(double nabCena) {
            this.nabCena = nabCena;
        }

        public double getMalCena() {
            return malCena;
        }

        public void setMalCena(double malCena) {
            this.malCena = malCena;
        }
    }

    // Getters and setters for BarcodeResponse
    public boolean isResponseResult() {
        return responseResult;
    }

    public void setResponseResult(boolean responseResult) {
        this.responseResult = responseResult;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getRespResultCount() {
        return respResultCount;
    }

    public void setRespResultCount(int respResultCount) {
        this.respResultCount = respResultCount;
    }

    public List<BarcodeData> getData() {
        return data;
    }

    public void setData(List<BarcodeData> data) {
        this.data = data;
    }
}