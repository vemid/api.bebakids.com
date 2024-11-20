package com.example.apibebakids.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class EmailRequestDTO {

    @NotNull(message = "fromEmail is required")
    private String fromEmail;

    @NotNull(message = "system is required")
    private String system;

    @NotNull(message = "documentNumber is required")
    private String documentNumber;

    @NotNull(message = "documentType is required")
    private String documentType;

    @NotEmpty(message = "items cannot be empty")
    private List<Item> items;

    // Getters and setters

    public String getFromEmail() {
        return fromEmail;
    }

    public void setFromEmail(String fromEmail) {
        this.fromEmail = fromEmail;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public static class Item {

        @NotNull(message = "SKU is required")
        private String sku;

        @NotNull(message = "Size is required")
        private String size;

        @NotNull(message = "Name is required")
        private String name;

        @NotNull(message = "Invoiced Quantity is required")
        private Integer invoicedQty;

        @NotNull(message = "Scanned Quantity is required")
        private Integer scannedQty;

        // Getters and setters
        public String getSku() {
            return sku;
        }

        public void setSku(String sku) {
            this.sku = sku;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getInvoicedQty() {
            return invoicedQty;
        }

        public void setInvoicedQty(Integer invoicedQty) {
            this.invoicedQty = invoicedQty;
        }

        public Integer getScannedQty() {
            return scannedQty;
        }

        public void setScannedQty(Integer scannedQty) {
            this.scannedQty = scannedQty;
        }
    }
}
