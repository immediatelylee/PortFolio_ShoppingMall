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
    // 상품에서 브랜드를 수정시에 brand를 조회하기 위하여
    private final BrandRepository brandRepository;


    public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList, List<MultipartFile> itemDetailImgFileList) throws Exception {

        //상품 등록
        String itemCode = generateItemCode();
        Item item = itemFormDto.toItem();
        item.setItemCode(itemCode);
        itemRepository.save(item);

        //이미지 등록
        for (int i = 0; i < itemImgFileList.size(); i++) {
            //set itemimg-item
            ItemImg itemImg = new ItemImg();
            itemImg.setItem(item);

            if (i == 0) {
                itemImg.setRepimgYn("Y");
                // set itemthumbnail-time
                ItemThumbnail itemThumbnail = new ItemThumbnail();
                itemThumbnail.setItem(item);

                itemImgService.saveItemThumbnail(itemThumbnail, itemImgFileList.get(i));
                itemImgService.saveItemImg(itemImg, itemImgFileList.get(i));
            } else {
                itemImg.setRepimgYn("N");

                itemImgService.saveItemImg(itemImg, itemImgFileList.get(i));
            }
        }
        // 상세이미지 등록
        for (int i = 0; i < itemDetailImgFileList.size(); i++) {
            // set itemdetailimg-item
            ItemDetailImg itemDetailImg = new ItemDetailImg();
            itemDetailImg.setItem(item);


            itemImgService.saveItemDetailImg(itemDetailImg, itemDetailImgFileList.get(i));

        }

        return item.getId();
    }

    @Transactional(readOnly = true)
    public Page<ItemFormDto> searchItems(ItemSearchType itemSearchType, String searchValue, String searchDateType, String sellStatus, String displayStatus, String mainCategory, String subCategory, String subSubCategory, Pageable pageable) {
        Page<ItemFormDto> items;

        // 검색어가없으면서 검색 조건이 있는경우
        if (searchValue == null || searchValue.isEmpty()) {
            ItemSearchDto itemSearchDto = new ItemSearchDto();
            itemSearchDto.setSearchDateType(searchDateType);
            itemSearchDto.setMainCategory(mainCategory);
            itemSearchDto.setSubCategory(subCategory);
            itemSearchDto.setSubSubCategory(subSubCategory);

            if ("SELL".equals(sellStatus)) {
                itemSearchDto.setSearchSellStatus(ItemSellStatus.SELL);
            } else if ("SOLD_OUT".equals(sellStatus)) {
                itemSearchDto.setSearchSellStatus(ItemSellStatus.SOLD_OUT);
            }

            if ("DISPLAY".equals(displayStatus)) {
                itemSearchDto.setSearchDisplayStatus(ItemDisplayStatus.DISPLAY);
            } else if ("NOT_DISPLAY".equals(displayStatus)) {
                itemSearchDto.setSearchDisplayStatus(ItemDisplayStatus.NOT_DISPLAY);
            }

            if (itemSearchDto.hasSearchConditions()) {
                items = itemRepository.getMainItemPage(itemSearchDto, pageable);
            } else {
                items = itemRepository.findAll(pageable).map(item -> {
                    ItemFormDto dto = ItemFormDto.of(item);
                    String thumbnailUrl = itemThumbnailRepository.findThumbnailUrlByItemId(item.getId());
                    dto.setThumbnailImgUrl(thumbnailUrl);
                    return dto;
                });
            }

        } else {
            ItemSearchDto itemSearchDto = new ItemSearchDto();
            itemSearchDto.setSearchBy(itemSearchType);
            itemSearchDto.setSearchQuery(searchValue);
            itemSearchDto.setSearchDateType(searchDateType);
            itemSearchDto.setMainCategory(mainCategory);
            itemSearchDto.setSubCategory(subCategory);
            itemSearchDto.setSubSubCategory(subSubCategory);

            if ("SELL".equals(sellStatus)) {
                itemSearchDto.setSearchSellStatus(ItemSellStatus.SELL);
            } else if ("SOLD_OUT".equals(sellStatus)) {
                itemSearchDto.setSearchSellStatus(ItemSellStatus.SOLD_OUT);
            }

            if ("DISPLAY".equals(displayStatus)) {
                itemSearchDto.setSearchDisplayStatus(ItemDisplayStatus.DISPLAY);
            } else if ("NOT_DISPLAY".equals(displayStatus)) {
                itemSearchDto.setSearchDisplayStatus(ItemDisplayStatus.NOT_DISPLAY);
            }

            items = itemRepository.getMainItemPage(itemSearchDto, pageable);
        }

        // 이미지 설정이 안 되어 있다면 여기서 설정
        Page<ItemFormDto> itemFormDtoPage = items.map(item -> {
            String thumbnailUrl = itemThumbnailRepository.findThumbnailUrlByItemId(item.getId());
            item.setThumbnailImgUrl(thumbnailUrl);
            return item;
        });

        return itemFormDtoPage;
    }

    @Transactional
    public void updateItemStatus(List<Long> itemIds, String actionType) {
        List<Item> items = itemRepository.findByIdIn(itemIds);


        if (actionType.equals("DELETE")) {
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
    public ItemFormDto getItemDtl(Long itemId) {
        //상품 정보 입력 - dto로 형변환
        Item item = itemRepository.findById(itemId)
                .orElseThrow(EntityNotFoundException::new);
        ItemFormDto itemFormDto = ItemFormDto.of(item);

        //상품이미지 정보 리스트에 입력
        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);
        List<ItemImgDto> itemImgDtoList = new ArrayList<>();
        for (ItemImg itemImg : itemImgList) {
            ItemImgDto itemImgDto = ItemImgDto.of(itemImg);
            itemImgDtoList.add(itemImgDto);
        }
        //상세이미지 정보 리스트에 입력
        List<ItemDetailImg> itemDetailImgList = itemDetailImgRepository.findByItemIdOrderByIdAsc(itemId);
        List<ItemDetailImgDto> itemDetailImgDtoList = new ArrayList<>();
        for (ItemDetailImg itemDetailImg : itemDetailImgList) {
            ItemDetailImgDto itemDetailImgDto = ItemDetailImgDto.of(itemDetailImg);
            itemDetailImgDtoList.add(itemDetailImgDto);
        }
        //썸네일 정보 리스트에 입력
        List<ItemThumbnail> itemThumbnailList = itemThumbnailRepository.findByItemIdOrderByIdAsc(itemId);
        List<ItemThumbnailDto> itemThumbnailDtoList = new ArrayList<>();
        for (ItemThumbnail itemThumbnail : itemThumbnailList) {
            ItemThumbnailDto itemThumbnailDto = ItemThumbnailDto.of(itemThumbnail);
            itemThumbnailDtoList.add(itemThumbnailDto);

        }
        //DTO에 정보 입력
        itemFormDto.setItemImgDtoList(itemImgDtoList);
        itemFormDto.setItemDetailImgDtoList(itemDetailImgDtoList);
        itemFormDto.setItemThumbnailDtoList(itemThumbnailDtoList);

        return itemFormDto;
    }

    public Long updateItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList,
                           List<MultipartFile> itemDetailImgFileList) throws Exception {
        //상품 수정
        Item item = itemRepository.findById(itemFormDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

        // Brand를 데이터베이스에서 조회
        Brand brand = brandRepository.findById(itemFormDto.getBrandId())
                .orElseThrow(() -> new EntityNotFoundException("해당 브랜드를 찾을 수 없습니다."));

        // 상품 정보 업데이트
        item.updateItem(itemFormDto,brand); // TODO: itemservice에 brand를 조회하기위해서 부득이 하게 brandRepository를 필드값으로 넣어야 했음 더 좋은 방식이 있다면 수정함.
        itemRepository.save(item);


        // 이미지 파일 리스트가 비어 있지 않은 경우에만 이미지 업데이트 수행
        if (itemImgFileList != null && !itemImgFileList.isEmpty() && !itemImgFileList.get(0).isEmpty()) {
            // 기존 이미지 정보 삭제
            List<ItemImg> itemImgs = itemImgRepository.findByItemId(itemFormDto.getId());
            for (ItemImg itemImg : itemImgs) {
                itemImgRepository.delete(itemImg);
            }

            // 기존 썸네일 이미지 정보 삭제
            List<ItemThumbnail> itemThumbnails = itemThumbnailRepository.findByItemIdOrderByIdAsc(itemFormDto.getId());
            for (ItemThumbnail itemThumbnail : itemThumbnails) {
                itemThumbnailRepository.delete(itemThumbnail);
            }

            // 새로운 이미지 저장
            for (int i = 0; i < itemImgFileList.size(); i++) {
                ItemImg itemImg = new ItemImg();
                itemImg.setItem(item);

                if (i == 0) {
                    // 첫번째 이미지 대표이미지 설정
                    itemImg.setRepimgYn("Y");

                    // 첫번째 이미지에 한해서 이미지 생성과 썸네일을 생성
                    ItemThumbnail newItemThumbnail = new ItemThumbnail();
                    newItemThumbnail.setItem(item);

                    itemImgService.saveItemThumbnail(newItemThumbnail, itemImgFileList.get(i));
                    itemImgService.saveItemImg(itemImg, itemImgFileList.get(i));
                } else {
                    itemImg.setRepimgYn("N");
                    itemImgService.saveItemImg(itemImg, itemImgFileList.get(i));
                }
            }
        }

        // 상세 이미지 파일 리스트가 비어 있지 않은 경우에만 상세 이미지 업데이트 수행
        if (itemDetailImgFileList != null && !itemDetailImgFileList.isEmpty() && !itemDetailImgFileList.get(0).isEmpty()) {
            // 기존 상세 이미지 정보 삭제
            List<ItemDetailImg> itemDetailImgs = itemDetailImgRepository.findByItemId(itemFormDto.getId());
            for (ItemDetailImg itemDetailImg : itemDetailImgs) {
                itemDetailImgRepository.delete(itemDetailImg);
            }

            // 새로운 상세 이미지 저장
            for (int i = 0; i < itemDetailImgFileList.size(); i++) {
                ItemDetailImg itemDetailImg = new ItemDetailImg();
                itemDetailImg.setItem(item);
                itemImgService.saveItemDetailImg(itemDetailImg, itemDetailImgFileList.get(i));
            }
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

            List<ItemThumbnail> itemThumbnails = itemThumbnailRepository.findByItemIdOrderByIdAsc(itemId);
            itemThumbnailRepository.deleteAll(itemThumbnails);


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

    public String findItemCodeById(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new IllegalArgumentException("해당 아이템이 없습니다. ID: " + itemId));
        return item.getItemCode();
    }
    // brand의 속한 아이템수를 계산하기 위해
    public Long countItemsByBrandId(Long brandId) {
        return itemRepository.countByBrandId(brandId);
    }

    public List<ItemWithImgDto> getItemsWithImgsBySubCategory(String subCategory) {
        return itemRepository.findItemsWithImgsBySubCategory(subCategory);
    }

    public Item getItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));
    }
}


