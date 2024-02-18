package com.shoppingmall.project_shoppingmall.service;

import com.shoppingmall.project_shoppingmall.domain.*;
import com.shoppingmall.project_shoppingmall.repository.*;
import lombok.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import org.springframework.web.multipart.*;
import org.thymeleaf.util.*;

import javax.persistence.*;

@Service
@RequiredArgsConstructor
@Transactional
public class BrandImgService {

    @Value("${brandImgLocation}")
    private String brandImgLocation;

    private final BrandImgRepository brandImgRepository;

    private final FileService fileService;

    public void saveBrandImg(BrandImg brandImg, MultipartFile brandImgFile) throws Exception {
        String oriImgName = brandImgFile.getOriginalFilename();
        String imgName = "";
        String imgUrl = "";

        //파일 업로드
        if (!StringUtils.isEmpty(oriImgName)) {
            imgName = fileService.uploadFile(brandImgLocation, oriImgName,
                    brandImgFile.getBytes());
            imgUrl = "/images/brand/" + imgName;
        }

        //상품 이미지 정보 저장
        brandImg.updateBrandImg(oriImgName, imgName, imgUrl);
        brandImgRepository.save(brandImg);
    }

    //상품 수정
    public void updateBrandImg(Long brandImgId, MultipartFile brandImgFile) throws Exception {
        if (!brandImgFile.isEmpty()) {
            BrandImg savedbrandImg = brandImgRepository.findById(brandImgId)
                    .orElseThrow(EntityNotFoundException::new);

            //기존 이미지 파일 삭제
            if (!StringUtils.isEmpty(savedbrandImg.getImgName())) {
                fileService.deleteFile(brandImgLocation + "/" +
                        savedbrandImg.getImgName());
            }

            String oriImgName = brandImgFile.getOriginalFilename();
            String imgName = fileService.uploadFile(brandImgLocation, oriImgName, brandImgFile.getBytes());
            String imgUrl = "/images/brand/" + imgName;
            savedbrandImg.updateBrandImg(oriImgName, imgName, imgUrl);
        }

    }
}
