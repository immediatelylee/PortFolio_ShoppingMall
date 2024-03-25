package com.shoppingmall.project_shoppingmall.constant;

public enum BrandStatus {
    True("사용함"),False("사용안함");

    private final String description;
    BrandStatus(String description) {
        this.description=description;
    }

    public String getDescription(){
        return description;
    }
}
