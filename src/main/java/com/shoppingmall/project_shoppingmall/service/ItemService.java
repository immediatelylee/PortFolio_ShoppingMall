package com.shoppingmall.project_shoppingmall.service;

import com.shoppingmall.project_shoppingmall.constant.*;
import com.shoppingmall.project_shoppingmall.domain.*;
import com.shoppingmall.project_shoppingmall.dto.*;
import com.shoppingmall.project_shoppingmall.repository.*;
import lombok.*;
import org.modelmapper.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import org.springframework.web.multipart.*;

import javax.persistence.*;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemImgService itemImgService;
    private final ItemImgRepository itemImgRepository;

    public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList,List<MultipartFile> itemDetailImgFileList) throws Exception{

        //상품 등록
        Item item = itemFormDto.toItem();
        itemRepository.save(item);

        //이미지 등록
        for(int i=0;i<itemImgFileList.size();i++){
            ItemImg itemImg = new ItemImg();
            itemImg.setItem(item);

            if(i == 0)
                itemImg.setRepimgYn("Y");
            else
                itemImg.setRepimgYn("N");

            itemImgService.saveItemImg(itemImg, itemImgFileList.get(i));
        }
        // 상세이미지 분할
        for(int i=0;i<itemDetailImgFileList.size();i++){
            ItemDetailImg itemDetailImg = new ItemDetailImg();
            itemDetailImg.setItem(item);


            itemImgService.saveItemDetailImg(itemDetailImg, itemDetailImgFileList.get(i));

        }

        return item.getId();
    }

    @Transactional
    public void updateItemStatus(List<Long> itemIds, String actionType) {
        List<Item> items = itemRepository.findByIdIn(itemIds);


        if (actionType.equals("DELETE")){
            deleteItems(itemIds);
            return;

        }

            for (Item item : items) {
                switch (actionType) {
                    case "DS":
                        item.setItemDisplayStatus(ItemDisplayStatus.DISPLAY);
                        break;
                    case "DN":
                        item.setItemDisplayStatus(ItemDisplayStatus.NOT_DISPLAY);
                        break;
                    case "SS":
                        item.setItemSellStatus(ItemSellStatus.SELL);
                        break;
                    case "SN":
                        item.setItemSellStatus(ItemSellStatus.SOLD_OUT);
                        break;

                }
            }
            // 삭제하지 않은 아이템만 저장
            if (!items.isEmpty()) {
                itemRepository.saveAll(items);
            }
        }



// 상품 수정
    @Transactional(readOnly = true)
    public ItemFormDto getItemDtl(Long itemId){
        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);
        List<ItemImgDto> itemImgDtoList = new ArrayList<>();
        for (ItemImg itemImg : itemImgList) {
            ItemImgDto itemImgDto = ItemImgDto.of(itemImg);
            itemImgDtoList.add(itemImgDto);
        }

        Item item = itemRepository.findById(itemId)
                .orElseThrow(EntityNotFoundException::new);
        ItemFormDto itemFormDto = ItemFormDto.of(item);
        itemFormDto.setItemImgDtoList(itemImgDtoList);
        return itemFormDto;
    }
    public Long updateItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception{
        //상품 수정
        Item item = itemRepository.findById(itemFormDto.getId())
                .orElseThrow(EntityNotFoundException::new);
        item.updateItem(itemFormDto);
        List<Long> itemImgIds = itemFormDto.getItemImgIds();

        //이미지 등록
        for(int i=0;i<itemImgFileList.size();i++){
            itemImgService.updateItemImg(itemImgIds.get(i),
                    itemImgFileList.get(i));
        }

        return item.getId();
    }

    // 상품관리 탭의 기능 진열함,진열안함,판매함,판매안함,삭제 에 관련 기능.

    public void deleteItems(List<Long> itemIds) {
        for (Long itemId : itemIds) {
            List<ItemImg> itemImgs = itemImgRepository.findByItemId(itemId);
            itemImgRepository.deleteAll(itemImgs);

            List<ItemDetailImg> itemDetailImgs = itemDetailImgRepository.findByItemId(itemId);
            itemDetailImgRepository.deleteAll(itemDetailImgs);

            itemRepository.deleteById(itemId);
        }
    }

    public void displayItems(List<Long> itemIds) {
        List<Item> items = itemRepository.findByIdIn(itemIds);
        for (Item item : items) {
            item.setItemDisplayStatus(ItemDisplayStatus.DISPLAY);
        }
        itemRepository.saveAll(items);
    }

    public void hideItems(List<Long> itemIds) {
        List<Item> items = itemRepository.findByIdIn(itemIds);
        for (Item item : items) {
            item.setItemDisplayStatus(ItemDisplayStatus.NOT_DISPLAY);
        }
        itemRepository.saveAll(items);
    }

    public void sellItems(List<Long> itemIds) {
        List<Item> items = itemRepository.findByIdIn(itemIds);
        for (Item item : items) {
            item.setItemSellStatus(ItemSellStatus.SELL);
        }
        itemRepository.saveAll(items);
    }

    public void stopSellingItems(List<Long> itemIds) {
        List<Item> items = itemRepository.findByIdIn(itemIds);
        for (Item item : items) {
            item.setItemSellStatus(ItemSellStatus.SOLD_OUT);
        }
        itemRepository.saveAll(items);
    }




    @Transactional(readOnly = true)
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable){
        return itemRepository.getAdminItemPage(itemSearchDto, pageable);
    }

    @Transactional(readOnly = true)
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable){
        return itemRepository.getMainItemPage(itemSearchDto, pageable);
    }
    @Transactional(readOnly = true)
    public List<ItemCategory> getCategoryBydepth(Long depth){
            List<ItemCategory> categoriesWithDepth = new ArrayList<>();
            for (ItemCategory category : ItemCategory.values()) {
                if (category.getDepth().equals(depth)) {
                    categoriesWithDepth.add(category);
                }
            }
        return categoriesWithDepth;
    }
}


