package com.example.apibebakids.model;

import com.informix.lang.Decimal;

import java.util.List;

public class StockResponse {
    private boolean responseResult;
    private String errorMessage;
    private int respResultCount;
    private List<StockItem> data;

    // Getters and setters for StockResponse
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

    public List<StockItem> getData() {
        return data;
    }

    public void setData(List<StockItem> data) {
        this.data = data;
    }

    // Nested StockItem class
    public static class StockItem {
        private String size;
        private int qty;
        private String sku;
        private String name;
        private String barcode;
        private String warehouse;
        private double price;

        // Getters and setters for StockItem
        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public int getQty() {
            return qty;
        }

        public void setQty(int qty) {
            this.qty = qty;
        }

        public String getSku() {
            return sku;
        }

        public void setSku(String sku) {
            this.sku = sku;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getBarcode() {
            return barcode;
        }

        public void setBarcode(String barcode) {
            this.barcode = barcode;
        }

        public String getWarehouse() {
            return warehouse;
        }

        public void setWarehouse(String warehouse) {
            this.warehouse = warehouse;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }
    }
}