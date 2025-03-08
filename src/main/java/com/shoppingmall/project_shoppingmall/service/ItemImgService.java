package com.shoppingmall.project_shoppingmall.service;

import com.shoppingmall.project_shoppingmall.domain.*;
import com.shoppingmall.project_shoppingmall.repository.*;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;
import javax.persistence.EntityNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemImgService {

    @Value("${itemImgLocation}")
    private String itemImgLocation;

    private final ItemImgRepository itemImgRepository;
    private final ItemDetailImgRepository itemDetailImgRepository;
    private final ItemThumbnailRepository itemThumbnailRepository;

    private final FileService fileService;

    /**
     * itemImgLocation에 설정된 URI 형식의 문자열을 올바른 Path 객체로 변환
     */
    private Path getItemImgDirectory() {
        try {
            URI uri = new URI(itemImgLocation);
            return Paths.get(uri);
        } catch (URISyntaxException e) {
            throw new RuntimeException("itemImgLocation 값의 URI 형식이 올바르지 않습니다: " + itemImgLocation, e);
        }
    }

    public void saveItemImg(ItemImg itemImg, MultipartFile itemImgFile) throws Exception {
        String oriImgName = itemImgFile.getOriginalFilename();
        String imgName = "";
        String imgUrl = "";

        //파일 업로드
        if (!StringUtils.isEmpty(oriImgName)) {
            String basePath = getItemImgDirectory().toString();
            imgName = fileService.uploadFile(basePath, oriImgName,
                    itemImgFile.getBytes());
            imgUrl = "/images/item/" + imgName;
        }

        //상품 이미지 정보 저장
        itemImg.updateItemImg(oriImgName, imgName, imgUrl);
        itemImgRepository.save(itemImg);
    }
    public void saveItemThumbnail(ItemThumbnail itemThumbnail,MultipartFile itemImgFile) throws Exception {
        String oriImgName = itemImgFile.getOriginalFilename();
        String imgName = "";
        String imgUrl = "";

        //파일 업로드
        if (!StringUtils.isEmpty(oriImgName)) {
            String basePath = getItemImgDirectory().toString();
            imgName = fileService.createThumbnail(basePath, oriImgName,
                    itemImgFile.getBytes());
            imgUrl = "/images/item/" + imgName;
        }
        itemThumbnail.updateItemThumbnail(oriImgName,imgName,imgUrl);
        itemThumbnailRepository.save(itemThumbnail);
    }



    public void saveItemDetailImg(ItemDetailImg itemDetailImg , MultipartFile itemDetailFile) throws Exception {
        String oriImgName = itemDetailFile.getOriginalFilename();
        String imgName = "";
        String imgUrl = "";

        //파일 업로드
        if (!StringUtils.isEmpty(oriImgName)) {
            String basePath = getItemImgDirectory().toString();
            imgName = fileService.uploadFile(basePath, oriImgName,
                    itemDetailFile.getBytes());
            imgUrl = "/images/item/" + imgName;
        }
        itemDetailImg.updateItemDetailImg(oriImgName,imgName,imgUrl);
        itemDetailImgRepository.save(itemDetailImg);


    }

    // TODO: 하위 메소드들이 사용되고 있지 않는데 상품수정에서 문제가 없는지 확인하고 제거
    // 상품 수정: 이미지 업데이트
    public void updateItemImg(Long itemImgId, MultipartFile itemImgFile) throws Exception {
        if (!itemImgFile.isEmpty()) {
            ItemImg savedItemImg = itemImgRepository.findById(itemImgId)
                    .orElseThrow(EntityNotFoundException::new);

            // 올바른 basePath를 구함
            String basePath = getItemImgDirectory().toString();

            // 기존 이미지 파일 삭제
            if (!StringUtils.isEmpty(savedItemImg.getImgName())) {
                fileService.deleteFile(basePath + "/" + savedItemImg.getImgName());
            }

            String oriImgName = itemImgFile.getOriginalFilename();
            String imgName = fileService.uploadFile(basePath, oriImgName, itemImgFile.getBytes());
            String imgUrl = "/images/item/" + imgName;
            savedItemImg.updateItemImg(oriImgName, imgName, imgUrl);

            itemImgRepository.save(savedItemImg); // 변경사항 저장
        }
    }

    public void updateItemDetailImg(Long itemDetailImgId, MultipartFile itemDetailImgFile) throws Exception {
        if (!itemDetailImgFile.isEmpty()) {
            ItemDetailImg savedItemDetailImg = itemDetailImgRepository.findById(itemDetailImgId)
                    .orElseThrow(EntityNotFoundException::new);

            // 올바른 basePath를 구함
            String basePath = getItemImgDirectory().toString();

            // 기존 이미지 파일 삭제
            if (!StringUtils.isEmpty(savedItemDetailImg.getImgName())) {
                fileService.deleteFile(basePath + "/" + savedItemDetailImg.getImgName());
            }

            String oriImgName = itemDetailImgFile.getOriginalFilename();
            String imgName = fileService.uploadFile(basePath, oriImgName, itemDetailImgFile.getBytes());
            String imgUrl = "/images/item/" + imgName;
            savedItemDetailImg.updateItemDetailImg(oriImgName, imgName, imgUrl);

            itemDetailImgRepository.save(savedItemDetailImg);
        }
    }

    public void updateItemThumbnail(Long itemThumbnailId, MultipartFile itemThumbnailFile) throws Exception {
        if (!itemThumbnailFile.isEmpty()) {
            ItemThumbnail savedItemThumbnail = itemThumbnailRepository.findById(itemThumbnailId)
                    .orElseThrow(EntityNotFoundException::new);

            // 올바른 basePath를 구함
            String basePath = getItemImgDirectory().toString();

            // 기존 이미지 파일 삭제
            if (!StringUtils.isEmpty(savedItemThumbnail.getImgName())) {
                fileService.deleteFile(basePath + "/" + savedItemThumbnail.getImgName());
            }

            String oriImgName = itemThumbnailFile.getOriginalFilename();
            String imgName = fileService.createThumbnail(basePath, oriImgName, itemThumbnailFile.getBytes());
            String imgUrl = "/images/item/" + imgName;
            savedItemThumbnail.updateItemThumbnail(oriImgName, imgName, imgUrl);

            itemThumbnailRepository.save(savedItemThumbnail);
        }
    }
    public ItemImg getByItemId(Long itemId){
        return itemImgRepository.findFirstByItemIdOrderByIdAsc(itemId);
    }

}