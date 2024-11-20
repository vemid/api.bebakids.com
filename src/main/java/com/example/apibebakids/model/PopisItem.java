package com.example.apibebakids.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Represents a single item in the inventory")
public class PopisItem {

    @Schema(description = "SKU of the item", example = "12345")
    private String sku;

    @Schema(description = "Size of the item", example = "L")
    private String size;

    @Schema(description = "Quantity of the item", example = "10")
    private int qty;

    // Getters and Setters
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

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }
}
