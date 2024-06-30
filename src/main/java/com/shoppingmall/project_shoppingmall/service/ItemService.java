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
import java.util.stream.*;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemImgService itemImgService;
    private final ItemImgRepository itemImgRepository;
    private final ItemDetailImgRepository itemDetailImgRepository;
    private final ItemThumbnailRepository itemThumbnailRepository;

//    public Map<String, Long> getProductCounts() {
//        Map<String, Long> counts = new HashMap<>();
//        counts.put("all", itemRepository.count());
//        counts.put("sellingTrue", itemRepository.countByStatus("T"));
//        counts.put("sellingFalse", itemRepository.countByStatus("F"));
//        counts.put("displayTrue", itemRepository.countByDisplayStatus("T"));
//        counts.put("displayFalse", itemRepository.countByDisplayStatus("F"));
//        return counts;
//    }


    public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList,List<MultipartFile> itemDetailImgFileList) throws Exception{

        //상품 등록
        String itemCode = generateItemCode();
        Item item = itemFormDto.toItem();
        item.setItemCode(itemCode);
        itemRepository.save(item);

        //이미지 등록
        for(int i=0;i<itemImgFileList.size();i++){
            ItemImg itemImg = new ItemImg();
            itemImg.setItem(item);

            if(i == 0) {
                itemImg.setRepimgYn("Y");
                ItemThumbnail itemThumbnail = new ItemThumbnail();
                itemThumbnail.setItem(item);
                itemImgService.saveItemThumbnail(itemThumbnail, itemImgFileList.get(i));
                itemImgService.saveItemImg(itemImg, itemImgFileList.get(i));
            }else {
                itemImg.setRepimgYn("N");

                itemImgService.saveItemImg(itemImg, itemImgFileList.get(i));
            }
        }
        // 상세이미지 분할
        for(int i=0;i<itemDetailImgFileList.size();i++){
            ItemDetailImg itemDetailImg = new ItemDetailImg();
            itemDetailImg.setItem(item);


            itemImgService.saveItemDetailImg(itemDetailImg, itemDetailImgFileList.get(i));

        }

        return item.getId();
    }

    @Transactional(readOnly = true)
    public Page<ItemFormDto> searchItems(ItemSearchType itemSearchType,String searchValue,String searchDateType ,String sellStatus ,String displayStatus,Pageable pageable) {
        Page<Item> items;
        if (searchValue == null || searchValue.isEmpty()) {
            if ("SELL".equals(sellStatus)) {
                items = itemRepository.findBySellStatus(ItemSellStatus.SELL,pageable);
            } else if ("SOLD_OUT".equals(sellStatus)) {
                items = itemRepository.findBySellStatus(ItemSellStatus.SOLD_OUT,pageable);
            } else if ("DISPLAY".equals(displayStatus)) {
                items = itemRepository.findByDisplayStatus(ItemDisplayStatus.DISPLAY,pageable);
            } else if ("NOT_DISPLAY".equals(displayStatus)) {
                items = itemRepository.findByDisplayStatus(ItemDisplayStatus.NOT_DISPLAY,pageable);
            } else {
                items = itemRepository.findAll(pageable);
            }
            // Item 객체 리스트를 ItemFormDto 객체 리스트로 변환
            List<ItemFormDto> itemFormDtos = items.stream()
                    .map(item -> ItemFormDto.of(item))
                    .collect(Collectors.toList());
            // 각 ItemFormDto에 대해 ThumbnailUrl 설정
            itemFormDtos.forEach(itemFormDto -> {
                String thumbnailUrl = itemThumbnailRepository.findThumbnailUrlByItemId(itemFormDto.getId());
                itemFormDto.setThumbnailImgUrl(thumbnailUrl);
            });
            return new PageImpl<>(itemFormDtos, pageable, items.getTotalElements());
        }


        else{
            ItemSearchDto itemSearchDto = new ItemSearchDto();
            itemSearchDto.setSearchBy((itemSearchType));

            itemSearchDto.setSearchQuery(searchValue);
            itemSearchDto.setSearchDateType(searchDateType);


            if ("A".equals(sellStatus)){
            } else if ("T".equals(sellStatus)){
                itemSearchDto.setSearchSellStatus((ItemSellStatus.SELL));
            } else if ("F".equals(sellStatus)){
//                (sellStatus == "F") 를 예상함.
                itemSearchDto.setSearchSellStatus((ItemSellStatus.SOLD_OUT));
            } else {
                System.out.println("==========sellStatus error ===============");
                return null;}


            if ("A".equals(displayStatus)){
            } else if("T".equals(displayStatus)){
                itemSearchDto.setSearchDisplayStatus((ItemDisplayStatus.DISPLAY));
            }else if("F".equals(displayStatus)){
                itemSearchDto.setSearchDisplayStatus((ItemDisplayStatus.NOT_DISPLAY));
            }
            else{
                System.out.println("==========displayStatus error ===============");
                return null;
            }


            return itemRepository.getMainItemPage(itemSearchDto, pageable);
//            return null; // 임시
            }



        }

//        return items.map(item -> {
//            List<ItemImg> itemImgs = itemImgRepository.findByItemIdOrderByIdAsc(item.getId());
//            List<ItemImgDto> itemImgDtoList = itemImgs.stream()
//                    .map(ItemImgDto::of)
//                    .collect(Collectors.toList());
//            ItemFormDto itemFormDto = ItemFormDto.of(item);
//            itemFormDto.setItemImgDtoList(itemImgDtoList);
//            return itemFormDto;
//        });

//    }


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
// 아이템 관리 header부분의 count

    public Long getTotalItemCount() {
        return itemRepository.countAllItems();
    }

    public Long getItemsOnSaleCount() {
        return itemRepository.countItemsBySellStatus(ItemSellStatus.SELL);
    }

    public Long getItemsSoldOutCount() {
        return itemRepository.countItemsBySellStatus(ItemSellStatus.SOLD_OUT);
    }

    public Long getItemsDisplayedCount() {
        return itemRepository.countItemsByDisplayStatus(ItemDisplayStatus.DISPLAY);
    }

    public Long getItemsNotDisplayedCount() {
        return itemRepository.countItemsByDisplayStatus(ItemDisplayStatus.NOT_DISPLAY);
    }

    public List<Item> getItemsOnSale() {
        return itemRepository.findItemsBySellStatus(ItemSellStatus.SELL);
    }

    public List<Item> getItemsSoldOut() {
        return itemRepository.findItemsBySellStatus(ItemSellStatus.SOLD_OUT);
    }

    public List<Item> getItemsDisplayed() {
        return itemRepository.findItemsByDisplayStatus(ItemDisplayStatus.DISPLAY);
    }

    public List<Item> getItemsNotDisplayed() {
        return itemRepository.findItemsByDisplayStatus(ItemDisplayStatus.NOT_DISPLAY);
    }

    private String generateItemCode() {
        // 2. 상품 코드 생성 로직
        //I + 7자리 숫자
        return "I" + String.format("%07d", itemRepository.count() + 1);
    }

}


