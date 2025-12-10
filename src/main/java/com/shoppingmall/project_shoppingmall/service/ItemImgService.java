package com.shoppingmall.project_shoppingmall.service;

import com.shoppingmall.project_shoppingmall.domain.ItemDetailImg;
import com.shoppingmall.project_shoppingmall.domain.ItemImg;
import com.shoppingmall.project_shoppingmall.domain.ItemThumbnail;
import com.shoppingmall.project_shoppingmall.repository.ItemDetailImgRepository;
import com.shoppingmall.project_shoppingmall.repository.ItemImgRepository;
import com.shoppingmall.project_shoppingmall.repository.ItemThumbnailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemImgService {

    @Value("${uploadPath}")
    private String uploadPath;

    private final ItemImgRepository itemImgRepository;
    private final ItemDetailImgRepository itemDetailImgRepository;
    private final ItemThumbnailRepository itemThumbnailRepository;

    private final FileService fileService;

    /**
     * itemImgLocation에 설정된 URI 형식의 문자열을 올바른 Path 객체로 변환
     */
    private Path getItemImgDirectory(String subDir) {
        try {
            URI uri = new URI(uploadPath);
            Path basePath = Paths.get(uri).resolve(subDir);
            Files.createDirectories(basePath);  // 디렉토리 자동 생성
            return basePath;
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("uploadPath 값의 URI 형식이 올바르지 않습니다: " + uploadPath, e);
        }
    }

    public void saveItemImg(ItemImg itemImg, MultipartFile itemImgFile) throws Exception {
        String oriImgName = itemImgFile.getOriginalFilename();
        String imgName = "";
        String imgUrl = "";

        if (!StringUtils.isEmpty(oriImgName)) {
            Path basePath = getItemImgDirectory("itemimg");
            imgName = fileService.uploadFile(basePath.toString(), oriImgName, itemImgFile.getBytes());

            // URL 수정
            imgUrl = "/uploaded_images/itemimg/" + imgName;
        }

        itemImg.updateItemImg(oriImgName, imgName, imgUrl);
        itemImgRepository.save(itemImg);
    }

    public void saveItemThumbnail(ItemThumbnail itemThumbnail, MultipartFile itemImgFile) throws Exception {
        String oriImgName = itemImgFile.getOriginalFilename();
        String imgName = "";
        String imgUrl = "";

        if (!StringUtils.isEmpty(oriImgName)) {
            Path basePath = getItemImgDirectory("itemthumbnail");
            imgName = fileService.createThumbnail(basePath.toString(), oriImgName, itemImgFile.getBytes());

            // URL 수정
            imgUrl = "/uploaded_images/itemthumbnail/" + imgName;
        }

        itemThumbnail.updateItemThumbnail(oriImgName, imgName, imgUrl);
        itemThumbnailRepository.save(itemThumbnail);
    }

    public void saveItemDetailImg(ItemDetailImg itemDetailImg, MultipartFile itemDetailFile) throws Exception {
        String oriImgName = itemDetailFile.getOriginalFilename();
        String imgName = "";
        String imgUrl = "";

        if (!StringUtils.isEmpty(oriImgName)) {
            Path basePath = getItemImgDirectory("itemdetailimg");
            imgName = fileService.uploadFile(basePath.toString(), oriImgName, itemDetailFile.getBytes());

            // URL 수정
            imgUrl = "/uploaded_images/itemdetailimg/" + imgName;
        }

        itemDetailImg.updateItemDetailImg(oriImgName, imgName, imgUrl);
        itemDetailImgRepository.save(itemDetailImg);
    }

    public String getRepImageUrl(Long itemId) {
        ItemImg repImg = itemImgRepository
                .findByItemIdAndRepimgYn(itemId, "Y")
                .orElse(null);

        if (repImg == null) {
            return "/images/default-product.jpg"; // ✅ 기본 이미지 경로
        }

        return repImg.getImgUrl();
    }

    public ItemImg getByItemId(Long itemId){
        return itemImgRepository.findFirstByItemIdOrderByIdAsc(itemId);
    }

}