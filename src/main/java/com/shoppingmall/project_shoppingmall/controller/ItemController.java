package com.shoppingmall.project_shoppingmall.controller;

import com.shoppingmall.project_shoppingmall.constant.*;
import com.shoppingmall.project_shoppingmall.domain.*;
import com.shoppingmall.project_shoppingmall.dto.*;
import com.shoppingmall.project_shoppingmall.service.*;
import lombok.*;
import lombok.experimental.*;
import org.springframework.data.domain.*;
import org.springframework.data.web.*;
import org.springframework.http.*;
import org.springframework.stereotype.*;
import org.springframework.ui.*;
import org.springframework.validation.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.*;
import org.springframework.web.servlet.mvc.support.*;

import javax.persistence.*;
import javax.validation.*;
import java.io.*;
import java.util.*;
import java.util.stream.*;

@RequiredArgsConstructor
@Controller
public class ItemController {
    private final ItemService itemService;
    private final BrandService brandService;
    private final PaginationService paginationService;

    @ModelAttribute("ItemCategory")
    public ItemCategory[] itemCategories(){
        return ItemCategory.values();
    }




    @PostMapping("/admin/item/multiaction")
    public String updateMultipleItems(@Valid IdsTransferDto idsTransferDto,
                                      RedirectAttributes redirectAttributes) {

        List<Long> itemIds = idsTransferDto.getSelectedIds();
        String actionType = idsTransferDto.getDataActionType();
        itemService.updateItemStatus(itemIds, actionType);
        redirectAttributes.addFlashAttribute("message", "상품 상태가 업데이트되었습니다.");
        return "redirect:/admin/item/management";
    }


    @GetMapping(value = "/admin/item/management")
    public String ItemManagement(@RequestParam(required = false) ItemSearchType itemSearchType,
                                 @RequestParam(required = false) String searchValue,
                                 @RequestParam(required = false) String searchDateType,
                                 @RequestParam(required = false) String sellStatus,
                                 @RequestParam(required = false) String displayStatus,
                                 @RequestParam(required = false) String mainCategory,
                                 @RequestParam(required = false) String subCategory,
                                 @RequestParam(required = false) String subSubCategory,
                                 @PageableDefault(page = 0,size = 10 ,sort = "id" ,direction = Sort.Direction.DESC) Pageable pageable,
                                 Model model){

        Page<ItemFormDto> searchItems = itemService.searchItems(itemSearchType,searchValue,searchDateType,sellStatus,displayStatus,mainCategory,subCategory,subSubCategory,pageable);
        List<Integer> barNumbers = paginationService.getPaginationBarNumbers(pageable.getPageNumber(),searchItems.getTotalPages());


        // 카운트 데이터 조회
        Long totalItemCount = itemService.getTotalItemCount();
        Long onSaleItemCount = itemService.getItemsOnSaleCount();
        Long soldOutItemCount = itemService.getItemsSoldOutCount();
        Long displayedItemCount = itemService.getItemsDisplayedCount();
        Long notDisplayedItemCount = itemService.getItemsNotDisplayedCount();


        model.addAttribute("ItemSearchType",itemSearchType.values());
        model.addAttribute("paginationBarNumbers", barNumbers);
        model.addAttribute("searchItems",searchItems);
        model.addAttribute("totalItemCount", totalItemCount);
        model.addAttribute("onSaleItemCount", onSaleItemCount);
        model.addAttribute("soldOutItemCount", soldOutItemCount);
        model.addAttribute("displayedItemCount", displayedItemCount);
        model.addAttribute("notDisplayedItemCount", notDisplayedItemCount);
        model.addAttribute("IdsTransferDto",new IdsTransferDto());

        model.addAttribute("categories", Arrays.stream(ItemCategory.values())
                .filter(category -> category.getParentItemCategory().isPresent() &&
                        category.getParentItemCategory().get() == ItemCategory.ROOT)
                .collect(Collectors.toList()));
        System.out.println("===============================================");
        System.out.println(Arrays.stream(ItemCategory.values())
                .filter(category -> category.getParentItemCategory().isPresent() &&
                        category.getParentItemCategory().get() == ItemCategory.ROOT)
                .collect(Collectors.toList()));


        return "item/itemManagement";
    }

    @GetMapping(value = "/admin/item/itemadd")
    public String itemAdd(Model model){
        List<ItemCategory> depth1 = itemService.getCategoryBydepth(1L);
        List<ItemCategory> depth2 = itemService.getCategoryBydepth(2L);
        List<ItemCategory> depth3 = itemService.getCategoryBydepth(3L);

        List<Brand> brands = brandService.findAll();


        model.addAttribute("depth1",depth1);
        model.addAttribute("depth2",depth2);
        model.addAttribute("depth3",depth3);
        model.addAttribute("itemFormDto", new ItemFormDto());

        model.addAttribute("brands",brands);
        System.out.println(depth1);
        System.out.println(depth2);
        System.out.println(depth3);

        return "item/itemAdd";
    }



    @PostMapping(value = "/admin/item/new")
    public String itemNew(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                          Model model, @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList,
                          @RequestParam("itemDetailImgFile") List<MultipartFile> itemDetailImgFileList
                          ){

        if(bindingResult.hasErrors()){
            System.out.println("error1");
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for(FieldError error : fieldErrors) {
                System.out.println("Field: " + error.getField());
                System.out.println("Message: " + error.getDefaultMessage());
            }

            return "item/itemManagement";
        }

        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null){
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값 입니다.");
            System.out.println("error2");
            return "item/itemManagement";
        }

        try {
            itemService.saveItem(itemFormDto, itemImgFileList,itemDetailImgFileList);
        } catch (Exception e){
            model.addAttribute("errorMessage", "상품 등록 중 에러가 발생하였습니다.");
            System.out.println("error3");
            return "item/itemManagement";
        }

        return "redirect:/admin/item/management";
    }

    @GetMapping(value = "/admin/item/management/{itemId}")
    public String itemDtl(@PathVariable("itemId") Long itemId, Model model){

        try {
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId);

            List<ItemCategory> depth1 = itemService.getCategoryBydepth(1L);
            List<ItemCategory> depth2 = itemService.getCategoryBydepth(2L);
            List<ItemCategory> depth3 = itemService.getCategoryBydepth(3L);

            List<Brand> brands = brandService.findAll();

            model.addAttribute("itemFormDto", itemFormDto);
            model.addAttribute("depth1",depth1);
            model.addAttribute("depth2",depth2);
            model.addAttribute("depth3",depth3);


            model.addAttribute("brands",brands);
            System.out.println(depth1);

        } catch(EntityNotFoundException e){
            model.addAttribute("errorMessage", "존재하지 않는 상품 입니다.");

            return "item/itemManagement";
        }

        return "item/itemAdd";
    }

    @PostMapping(value = "/admin/item/management/{itemId}")
    public String itemUpdate(@PathVariable("itemId") Long itemId, @Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                             @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList,
                             @RequestParam("itemDetailImgFile") List<MultipartFile> itemDetailImgFileList,
                             Model model){
        if(bindingResult.hasErrors()){
            return "item/itemManagement";
        }

        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null){
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값 입니다.");
            return "item/itemManagement";
        }

        try {
            // URL 패스의 itemId를 itemFormDto에 설정
            itemFormDto.setId(itemId);
            itemFormDto.setItemCode(itemService.findItemCodeById(itemId));

            // itemImgFileList의 첫 번째 이미지를 썸네일로 사용
            List<MultipartFile> itemThumbnailFileList = new ArrayList<>();
            itemThumbnailFileList.add(itemImgFileList.get(0));

            itemService.updateItem(itemFormDto, itemImgFileList,itemDetailImgFileList);
        } catch (Exception e){
            model.addAttribute("errorMessage", "상품 수정 중 에러가 발생하였습니다.");
            return "item/itemManagement";
        }

        return "redirect:/admin/item/management";
    }

//    @GetMapping(value = {"/admin/items", "/admin/items/{page}"})
//    public String itemManage(ItemSearchDto itemSearchDto, @PathVariable("page") Optional<Integer> page, Model model){
//
//        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 3);
//        Page<Item> items = itemService.getAdminItemPage(itemSearchDto, pageable);
//
//        model.addAttribute("items", items);
//        model.addAttribute("itemSearchDto", itemSearchDto);
//        model.addAttribute("maxPage", 5);
//
//        return "item/itemMng";
//    }

//    @GetMapping(value = "/item/{itemId}")
//    public String itemDtl(Model model, @PathVariable("itemId") Long itemId){
//        ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
//        model.addAttribute("item", itemFormDto);
//        return "item/itemDtl";
//    }
    @GetMapping(value = "/item/{itemId}")
    public String itemDtl(Model model, @PathVariable("itemId") Long itemId){
        ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
        model.addAttribute("item", itemFormDto);
        return "item/new_itemDtl";
    }
}

