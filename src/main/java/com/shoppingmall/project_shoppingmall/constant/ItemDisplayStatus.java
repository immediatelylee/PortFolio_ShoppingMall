package com.shoppingmall.project_shoppingmall.constant;

public enum ItemDisplayStatus {
    DISPLAY("사용함"),NOT_DISPLAY("사용안함");

    private final String description;
    ItemDisplayStatus(String description) {
        this.description=description;
    }

    public String getDescription(){
        return description;
    }
}
