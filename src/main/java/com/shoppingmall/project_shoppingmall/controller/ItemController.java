package com.shoppingmall.project_shoppingmall.controller;

import com.shoppingmall.project_shoppingmall.constant.ItemCategory;
import com.shoppingmall.project_shoppingmall.constant.ItemSearchType;
import com.shoppingmall.project_shoppingmall.domain.Brand;
import com.shoppingmall.project_shoppingmall.domain.OptionSet;
import com.shoppingmall.project_shoppingmall.dto.IdsTransferDto;
import com.shoppingmall.project_shoppingmall.dto.ItemFormDto;
import com.shoppingmall.project_shoppingmall.dto.ItemSearchDto;
import com.shoppingmall.project_shoppingmall.dto.ItemWithImgDto;
import com.shoppingmall.project_shoppingmall.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Controller
public class ItemController {
    private final ItemService itemService;
    private final BrandService brandService;
    private final PaginationService paginationService;
    private final WishlistService wishlistService;
    private final OptionSetService optionSetService;


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
    public String ItemManagement(@ModelAttribute ItemSearchDto searchDto,
                                 @PageableDefault(page = 0,size = 10 ,sort = "id" ,direction = Sort.Direction.DESC) Pageable pageable,
                                 Model model){

        Page<ItemFormDto> searchItems = itemService.searchItems(searchDto, pageable);
        List<Integer> barNumbers = paginationService.getPaginationBarNumbers(pageable.getPageNumber(),searchItems.getTotalPages());


        // 카운트 데이터 조회
        Long totalItemCount = itemService.getTotalItemCount();
        Long onSaleItemCount = itemService.getItemsOnSaleCount();
        Long soldOutItemCount = itemService.getItemsSoldOutCount();
        Long displayedItemCount = itemService.getItemsDisplayedCount();
        Long notDisplayedItemCount = itemService.getItemsNotDisplayedCount();


        model.addAttribute("ItemSearchType",ItemSearchType.values());
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

        return "item/itemManagement";
    }


    @GetMapping(value = "/item/infant")
    public String itemInfant(@PageableDefault(page = 0,size = 10 ,sort = "id" ,direction = Sort.Direction.DESC) Pageable pageable, Model model, Principal principal){



        // 추후에 수정해야함(각 코너마다의 count로 바꿔야함)
        Long onSaleItemCount = itemService.getItemsOnSaleCount();
        List<ItemWithImgDto> items = itemService.getItemsWithImgsBySubCategory("패션의류/잡화","아동");

        List<Long> wishlistItemIds = new ArrayList<>(); // 사용자의 위시리스트 상품 ID 리스트

        if (principal != null) { // 로그인된 사용자인 경우
            String memberEmail = principal.getName();
            wishlistItemIds = wishlistService.getWishlistItemIds(memberEmail);

        }

        model.addAttribute("items", items);
        model.addAttribute("wishlistItemIds", wishlistItemIds);
        model.addAttribute("onSaleItemCount", onSaleItemCount);

        System.out.println("============test");
        System.out.println(items);

        return "item/itemList";
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

        // 1) 모든 OptionSet 조회
        List<OptionSet> setList = optionSetService.getAllOptionSets();
        model.addAttribute("optionSets", setList);

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

            return "redirect:/admin/item/management";
        }

        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null){
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값 입니다.");
            System.out.println("error2");
            return "redirect:/admin/item/management";
        }

        try {
            itemService.saveItem(itemFormDto, itemImgFileList,itemDetailImgFileList);
        } catch (Exception e){
            model.addAttribute("errorMessage", "상품 등록 중 에러가 발생하였습니다.");
            System.out.println("error3");
            return "redirect:/admin/item/management";
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
                             @RequestParam(value = "itemImgFile" , required = false) List<MultipartFile> itemImgFileList,
                             @RequestParam(value = "itemDetailImgFile", required = false) List<MultipartFile> itemDetailImgFileList,
                             Model model){
        if(bindingResult.hasErrors()){
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
            System.out.println("상품수정중 에러 ");
            return "item/itemManagement";
        }

        return "redirect:/admin/item/management";
    }

    @GetMapping(value = "/item/{itemId}")
    public String itemDetail(Model model,@PathVariable("itemId") Long itemId){
        ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
        model.addAttribute("item", itemFormDto);
        return "item/itemDetail";
    }
//TODO: 테스트 후에는 다시 합칠것
    @GetMapping(value = "/item_mobile/{itemId}")
    public String mobile_itemDetail(Model model,@PathVariable("itemId") Long itemId){
        ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
        model.addAttribute("item", itemFormDto);
        return "mobile/mobile_detail";
    }

}

