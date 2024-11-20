package com.example.apibebakids.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Request to save inventory items")
public class PopisRequest {

    @Schema(description = "System name", example = "bebakids")
    private String system;

    @Schema(description = "Retail store where the inventory is being processed", example = "Store1")
    private String retailStore;

    @Schema(description = "Note when parsing data ", example = "Shelf number XXX")
    private String note;

    @Schema(description = "List of items to be saved in the inventory")
    private List<PopisItem> items;

    // Getters and Setters
    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getRetailStore() {
        return retailStore;
    }

    public void setRetailStore(String retailStore) {
        this.retailStore = retailStore;
    }

    public List<PopisItem> getItems() {
        return items;
    }

    public void setItems(List<PopisItem> items) {
        this.items = items;
    }
}
