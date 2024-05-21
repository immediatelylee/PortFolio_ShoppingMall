package com.shoppingmall.project_shoppingmall.controller;

import com.shoppingmall.project_shoppingmall.constant.*;
import com.shoppingmall.project_shoppingmall.domain.*;
import com.shoppingmall.project_shoppingmall.dto.*;
import com.shoppingmall.project_shoppingmall.service.*;
import lombok.*;
import lombok.experimental.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;
import org.springframework.ui.*;
import org.springframework.validation.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.*;

import javax.persistence.*;
import javax.validation.*;
import java.util.*;
import java.util.stream.*;

@RequiredArgsConstructor
@Controller
public class ItemController {
    private final ItemService itemService;
    private final BrandService brandService;

    @ModelAttribute("ItemCategory")
    public ItemCategory[] itemCategories(){
        return ItemCategory.values();
    }

    @GetMapping(value = "/admin/item/management")
    public String test(Model model){
        for (ItemCategory category : ItemCategory.values()) {
            System.out.println((category.getTitle()+ " " + category.name() + " " +category.getDepth() ));

            if (category.getParentItemCategory().isPresent()) {

                if (category.getParentItemCategory().get().equals(ItemCategory.ROOT)) {
                    System.out.println("parent ROOT");
                    System.out.println("================================");

                    for (ItemCategory childcate : category.getChildCategories()) {
                        System.out.println("==child==");
                        System.out.println(childcate.getTitle());
                    }
                } else  {
                    continue;
                }
            }
        }

        return "item/itemManagement";
    }

    @GetMapping(value = "/admin/item/itemadd")
    public String test1(Model model){
        List<ItemCategory> depth1 = itemService.getCategoryBydepth(1L);
        List<ItemCategory> depth2 = itemService.getCategoryBydepth(2L);
        List<ItemCategory> depth3 = itemService.getCategoryBydepth(3L);

        model.addAttribute("depth1",depth1);
        model.addAttribute("depth2",depth2);
        model.addAttribute("depth3",depth3);

        return "item/itemAdd";
    }


    @GetMapping(value = "/admin/item/new")
    public String itemForm(Model model){
        model.addAttribute("itemFormDto", new ItemFormDto());
        model.addAttribute("brandFormDto",new BrandFormDto());
        return "item/itemForm";
    }

    @PostMapping(value = "/admin/item/new")
    public String itemNew(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                          Model model, @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList){

        if(bindingResult.hasErrors()){
            return "item/itemForm";
        }

        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null){
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값 입니다.");
            return "item/itemForm";
        }

        try {
            itemService.saveItem(itemFormDto, itemImgFileList);
        } catch (Exception e){
            model.addAttribute("errorMessage", "상품 등록 중 에러가 발생하였습니다.");
            return "item/itemForm";
        }

        return "redirect:/";
    }

    @GetMapping(value = "/admin/item/{itemId}")
    public String itemDtl(@PathVariable("itemId") Long itemId, Model model){

        try {
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
//            BrandFormDto brandFormDto = brandService.getBrand(brandId); // 수정이 필요함.
            model.addAttribute("itemFormDto", itemFormDto);
//            model.addAttribute("brandFormDto",brandFormDto);
        } catch(EntityNotFoundException e){
            model.addAttribute("errorMessage", "존재하지 않는 상품 입니다.");
            model.addAttribute("itemFormDto", new ItemFormDto());
            model.addAttribute("brandFormDto",new BrandFormDto());
            return "item/itemForm";
        }

        return "item/itemForm";
    }

    @PostMapping(value = "/admin/item/{itemId}")
    public String itemUpdate(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                             @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList, Model model){
        if(bindingResult.hasErrors()){
            return "item/itemForm";
        }

        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null){
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값 입니다.");
            return "item/itemForm";
        }

        try {
            itemService.updateItem(itemFormDto, itemImgFileList);
        } catch (Exception e){
            model.addAttribute("errorMessage", "상품 수정 중 에러가 발생하였습니다.");
            return "item/itemForm";
        }

        return "redirect:/";
    }

    @GetMapping(value = {"/admin/items", "/admin/items/{page}"})
    public String itemManage(ItemSearchDto itemSearchDto, @PathVariable("page") Optional<Integer> page, Model model){

        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 3);
        Page<Item> items = itemService.getAdminItemPage(itemSearchDto, pageable);

        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage", 5);

        return "item/itemMng";
    }

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

