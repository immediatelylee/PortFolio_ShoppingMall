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
    private final OptionCombinationRepository optionCombinationRepository;


    public Long saveItem(ItemFormDto itemFormDto,
                         List<MultipartFile> itemImgFileList,
                         List<MultipartFile> itemDetailImgFileList
                         ) throws Exception {
        System.out.println(itemFormDto);
        // 1) Item 엔티티 생성 & 필드 세팅
        Item item = itemFormDto.toItem();
        // 기존에 있었던 itemCode 생성 로직
        String itemCode = generateItemCode();
        item.setItemCode(itemCode);

        // 2) 옵션 처리 분기
        if (itemFormDto.getDisplayType() == OptionDisplayType.COMBINED) {
            // 조합 일체형
            handleCombinedOptions(item, itemFormDto.getCombinationList());
            item.setDisplayType(OptionDisplayType.COMBINED);
        }
        else if (itemFormDto.getDisplayType() == OptionDisplayType.SEPARATED) {
            // 분리 선택형
            // 예) OptionSet을 직접 연결 or UsedOption 생성
            handleSeparatedOptions(item, itemFormDto);
            item.setDisplayType(OptionDisplayType.SEPARATED);
        }
        else {
            // 옵션 미사용 / NONE
            // item.setDisplayType(OptionDisplayType.NONE); // 필요 시
        }

        // 3) Item DB 저장
        itemRepository.save(item);

        // 4) 이미지 등록
        // 대표 이미지 + 일반 이미지
        if (itemImgFileList != null && !itemImgFileList.isEmpty()) {
            for (int i = 0; i < itemImgFileList.size(); i++) {
                ItemImg itemImg = new ItemImg();
                itemImg.setItem(item);

                if (i == 0) {
                    // 대표 이미지
                    itemImg.setRepimgYn("Y");

                    // 썸네일도 같이
                    ItemThumbnail itemThumbnail = new ItemThumbnail();
                    itemThumbnail.setItem(item);

                    itemImgService.saveItemThumbnail(itemThumbnail, itemImgFileList.get(i));
                    itemImgService.saveItemImg(itemImg, itemImgFileList.get(i));
                } else {
                    // 일반 이미지
                    itemImg.setRepimgYn("N");
                    itemImgService.saveItemImg(itemImg, itemImgFileList.get(i));
                }
            }
        }

        // 5) 상세 이미지 등록
        if (itemDetailImgFileList != null && !itemDetailImgFileList.isEmpty()) {
            for (int i = 0; i < itemDetailImgFileList.size(); i++) {
                ItemDetailImg itemDetailImg = new ItemDetailImg();
                itemDetailImg.setItem(item);

                itemImgService.saveItemDetailImg(itemDetailImg, itemDetailImgFileList.get(i));
            }
        }

        // 5) 옵션 조합 저장 (옵션재고를 사용하는 경우)
        if (itemFormDto.getCombinationList() != null &&
                !itemFormDto.getCombinationList().isEmpty()) {

            List<String> combinationList = itemFormDto.getCombinationList();
            // stockUses: 각 옵션 조합에 대해 재고 사용 여부 ("T" 또는 "F")
            List<String> stockUses = itemFormDto.getStockOption_use();
            // stocks: 각 옵션 조합별 재고 수량 (정수)
            List<Integer> stocks = itemFormDto.getItemOption_stock();

            List<OptionCombination> combinationEntities = new ArrayList<>();
            for (int i = 0; i < combinationList.size(); i++) {
                OptionCombination oc = new OptionCombination();
                oc.setCombination(combinationList.get(i));

                // 재고 사용 여부를 체크 (대소문자 구분 없이 "T"이면 true)
                boolean useStock = "T".equalsIgnoreCase(stockUses.get(i));
                // 사용하면 해당 재고값, 그렇지 않으면 0 할당
                if (useStock) {
                    oc.setUse_stock(true);
                    oc.setStock(stocks.get(i));
                } else {
                    oc.setUse_stock(false);
                    oc.setStock(0);
                }
                oc.setItem(item);  // 연관관계 설정
                combinationEntities.add(oc);
            }
            optionCombinationRepository.saveAll(combinationEntities);
        }

        return item.getId();
    }

    @Transactional(readOnly = true)
    public Page<ItemFormDto> searchItems(ItemSearchDto itemSearchDto, Pageable pageable) {

        // 조건 여부 상관없이 항상 DTO를 전달하여 검색
        Page<ItemFormDto> items = itemRepository.getMainItemPage(itemSearchDto, pageable);

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



    /**
     * [조합 일체형] 옵션 처리
     * - combinationList: ["블랙-S","블랙-M","화이트-S"] 등
     */
    private void handleCombinedOptions(Item item, List<String> combinationList) {
        // 조합 목록을 반복하며 OptionCombination 생성
        if (combinationList != null) {
            for (String combo : combinationList) {
                OptionCombination oc = new OptionCombination();
                oc.setCombination(combo);
                oc.setStock(0); // 필요하면 재고 필드 활용

                // item.addOptionCombination(oc);
                // 연관관계 편의 메서드
                item.addOptionCombination(oc);
            }
        }
    }

    /**
     * [분리 선택형] 옵션 처리
     * - 예) itemFormDto로부터 optionSetId를 가져올 수도 있고,
     *       color/size 필드를 직접 사용해 UsedOption을 만든다든지
     */
    private void handleSeparatedOptions(Item item, ItemFormDto itemFormDto) {
        // 예: OptionSet을 미리 만들어놓았다면
        // Long optionSetId = itemFormDto.getOptionSetId(); (필드 추가 필요)
        // OptionSet optionSet = optionSetService.getOptionSetById(optionSetId);
        // item.setOptionSet(optionSet);

        // or color/size 등 직접 저장
        // if (itemFormDto.getColor() != null && itemFormDto.getSize() != null) {
        //     UsedOption usedOptColor = new UsedOption("색상", itemFormDto.getColor());
        //     usedOptColor.setItem(item);
        //     UsedOption usedOptSize = new UsedOption("사이즈", String.valueOf(itemFormDto.getSize()));
        //     usedOptSize.setItem(item);
        //     // usedOptionRepository.save(...); cascade 설정에 따라 다름
        // }

        // etc...
    }
}


