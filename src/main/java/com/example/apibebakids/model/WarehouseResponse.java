package com.example.apibebakids.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class WarehouseResponse {
    private boolean responseResult;
    private String errorMessage;
    private int respResultCount;
    private List<WarehouseData> data;

    public static class WarehouseData {
        private String pricelist;
        private String code;
        @JsonProperty("e_mail")
        private String email;
        @JsonProperty("retail_type")
        private String retailType;
        private String name;
        private int active;
        private String type;

        // Getters and Setters
        public String getPricelist() {
            return pricelist;
        }

        public void setPricelist(String pricelist) {
            this.pricelist = pricelist;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getRetailType() {
            return retailType;
        }

        public void setRetailType(String retailType) {
            this.retailType = retailType;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getActive() {
            return active;
        }

        public void setActive(int active) {
            this.active = active;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    // Getters and Setters for WarehouseResponse
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

    public List<WarehouseData> getData() {
        return data;
    }

    public void setData(List<WarehouseData> data) {
        this.data = data;
    }
}