package com.example.apibebakids.model;

import java.util.List;

public class DocumentCheckResponse {
    private boolean responseResult;
    private String errorMessage;
    private int respResultCount;
    private List<DocumentItem> data;

    // Getters and setters for DocumentResponse
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

    public List<DocumentItem> getData() {
        return data;
    }

    public void setData(List<DocumentItem> data) {
        this.data = data;
    }

    // Nested DocumentItem class
    public static class DocumentItem {
        private String documentType;
        private String document;
        private String barKod;
        private String nazRob;
        private String sifRob;
        private String sifEntRob;
        private double kolic;

        // Getters and setters for DocumentItem
        public String getDocumentType() {
            return documentType;
        }

        public void setDocumentType(String documentType) {
            this.documentType = documentType;
        }

        public String getDocument() {
            return document;
        }

        public void setDocument(String document) {
            this.document = document;
        }

        public String getBarKod() {
            return barKod;
        }
        public void setBarKod(String barKod) {
            this.barKod = barKod;
        }

        public String getNazRob() {
            return nazRob;
        }

        public void setNazRob(String nazRob) {
            this.nazRob = nazRob;
        }

        public String getSifRob() {
            return sifRob;
        }

        public void setSifRob(String sifRob) {
            this.sifRob = sifRob;
        }

        public String getSifEntRob() {
            return sifEntRob;
        }

        public void setSifEntRob(String sifEntRob) {
            this.sifEntRob = sifEntRob;
        }

        public double getKolic() {
            return kolic;
        }

        public void setKolic(double kolic) {
            this.kolic = kolic;
        }
    }
}
