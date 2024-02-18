package com.shoppingmall.project_shoppingmall.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name="brand_img")
@Getter @Setter
public class BrandImg extends BaseEntity{

    @Id
    @Column(name="brand_img_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imgName; //이미지 파일명

    private String oriImgName; //원본 이미지 파일명

    private String imgUrl; //이미지 조회 경로

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="brand_id")
    private Brand brand;

    public void updateBrandImg(String oriImgName, String imgName, String imgUrl){
        this.oriImgName = oriImgName;
        this.imgName = imgName;
        this.imgUrl = imgUrl;
    }
}
