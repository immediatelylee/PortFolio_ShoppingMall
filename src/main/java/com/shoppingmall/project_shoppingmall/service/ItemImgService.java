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

    public void saveItemImg(ItemImg itemImg, MultipartFile itemImgFile) throws Exception {
        String oriImgName = itemImgFile.getOriginalFilename();
        String imgName = "";
        String imgUrl = "";

        //파일 업로드
        if (!StringUtils.isEmpty(oriImgName)) {
            imgName = fileService.uploadFile(itemImgLocation, oriImgName,
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
            imgName = fileService.createThumbnail(itemImgLocation, oriImgName,
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
            imgName = fileService.uploadFile(itemImgLocation, oriImgName,
                    itemDetailFile.getBytes());
            imgUrl = "/images/item/" + imgName;
        }
        itemDetailImg.updateItemDetailImg(oriImgName,imgName,imgUrl);
        itemDetailImgRepository.save(itemDetailImg);


    }

    //상품 수정
    public void updateItemImg(Long itemImgId, MultipartFile itemImgFile) throws Exception {
        if (!itemImgFile.isEmpty()) {
            ItemImg savedItemImg = itemImgRepository.findById(itemImgId)
                    .orElseThrow(EntityNotFoundException::new);

            //기존 이미지 파일 삭제
            if (!StringUtils.isEmpty(savedItemImg.getImgName())) {
                fileService.deleteFile(itemImgLocation + "/" +
                        savedItemImg.getImgName());
            }



            String oriImgName = itemImgFile.getOriginalFilename();
            String imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes());
            String imgUrl = "/images/item/" + imgName;
            savedItemImg.updateItemImg(oriImgName, imgName, imgUrl);

            itemImgRepository.save(savedItemImg);// 변경사항 저장
        }

    }

    public void updateItemDetailImg(Long itemDetailImgId, MultipartFile itemDetailImgFile) throws Exception {
        if (!itemDetailImgFile.isEmpty()) {
            ItemDetailImg savedItemDetailImg = itemDetailImgRepository.findById(itemDetailImgId)
                    .orElseThrow(EntityNotFoundException::new);

            //기존 이미지 파일 삭제
            if (!StringUtils.isEmpty(savedItemDetailImg.getImgName())) {
                fileService.deleteFile(itemImgLocation + "/" +
                        savedItemDetailImg.getImgName());
            }

            String oriImgName = itemDetailImgFile.getOriginalFilename();
            String imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemDetailImgFile.getBytes());
            String imgUrl = "/images/item/" + imgName;
            savedItemDetailImg.updateItemDetailImg(oriImgName, imgName, imgUrl);

            itemDetailImgRepository.save(savedItemDetailImg);
        }

    }
    public void updateItemThumbnail(Long itemThumbnailId, MultipartFile itemThumbnailFile) throws Exception {
        if (!itemThumbnailFile.isEmpty()) {
            ItemThumbnail savedItemThumbnail = itemThumbnailRepository.findById(itemThumbnailId)
                    .orElseThrow(EntityNotFoundException::new);

            //기존 이미지 파일 삭제
            if (!StringUtils.isEmpty(savedItemThumbnail.getImgName())) {
                fileService.deleteFile(itemImgLocation + "/" +
                        savedItemThumbnail.getImgName());
            }

            String oriImgName = itemThumbnailFile.getOriginalFilename();
            String imgName = fileService.createThumbnail(itemImgLocation, oriImgName, itemThumbnailFile.getBytes());
            String imgUrl = "/images/item/" + imgName;
            savedItemThumbnail.updateItemThumbnail(oriImgName, imgName, imgUrl);

            itemThumbnailRepository.save(savedItemThumbnail);
        }

    }
    public ItemImg getByItemId(Long itemId){
        return itemImgRepository.findFirstByItemIdOrderByIdAsc(itemId);
    }

}