package com.shoppingmall.project_shoppingmall.controller;

import com.shoppingmall.project_shoppingmall.constant.*;
import com.shoppingmall.project_shoppingmall.domain.*;
import com.shoppingmall.project_shoppingmall.dto.*;
import com.shoppingmall.project_shoppingmall.service.*;
import lombok.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.data.domain.*;
import org.springframework.data.web.*;
import org.springframework.stereotype.*;
import org.springframework.ui.*;
import org.springframework.validation.*;
import org.springframework.web.bind.annotation.*;


import javax.servlet.*;
import javax.servlet.http.*;
import javax.validation.*;
import java.util.*;
import java.util.stream.*;

@RequiredArgsConstructor
@Controller
public class BrandController {

    private final BrandService brandService;
    private final PaginationService paginationService;

    @ModelAttribute("brandStatus")
    public BrandStatus[] brandStatuses(){
        return BrandStatus.values();
    }

    @GetMapping("/admin/brand/brandadd")
    public String brandForm(@ModelAttribute("brandFormDto") BrandFormDto brandFormDto){

        return "brand/brandAdd";
    }

    @PostMapping("/admin/brand/brandadd")
    public String savebrand(@Valid BrandFormDto brandFormDto ,
                            BindingResult bindingResult, Model model){
        if(bindingResult.hasErrors()){

            return "brand/brandForm";
        }

        try {
            brandService.saveBrand(brandFormDto);
            model.addAttribute("message", "브랜드가 등록되었습니다.");
            return "redirect:/admin/brand/management";

        } catch (Exception e){
            model.addAttribute("errorMessage", "브렌드 등록에 실패했습니다.");
            return "brand/brandForm";
        }

    }

    @GetMapping(value = "/admin/brand/management")
    public String brandmanage(@RequestParam(required = false) BrandSearchType brandSearchType,
                              @RequestParam(required = false) String searchValue,
                              @RequestParam(required = false) String searchStatus,
                              @PageableDefault(page = 0,size = 10 ,sort = "brandCode" ,direction = Sort.Direction.DESC) Pageable pageable,
                              Model model){

        Page<Brand> searchBrands = brandService.searchBrands(brandSearchType,searchValue,searchStatus,pageable).map(BrandFormDto::toBrand);
        List<Integer> barNumbers = paginationService.getPaginationBarNumbers(pageable.getPageNumber(),searchBrands.getTotalPages());



        model.addAttribute("paginationBarNumbers", barNumbers);
        model.addAttribute("BrandSearchType",brandSearchType.values());
        model.addAttribute("IdsTransferDto",new IdsTransferDto());  // retrun list
        model.addAttribute("searchBrands",searchBrands);

        return "brand/brandForm";
    }

    @GetMapping(value = "/admin/brand/management/{brandId}")
    public String getBrand(@PathVariable Long brandId,Model model){
        BrandFormDto brandFormDto = brandService.getBrand(brandId);
        model.addAttribute("brandFormDto",brandFormDto);


    return "brand/brandAdd";
    }

    @PostMapping(value = "/admin/brand/management/{brandId}")
    public String updateBrand(@Valid BrandFormDto brandFormDto,BindingResult bindingResult,Model model) {
        if(bindingResult.hasErrors()){
            return "brand/brandForm";
        }
        try {
            brandService.updateBrand(brandFormDto);
        } catch (Exception e){
            model.addAttribute("errorMessage","브랜드 수정중 에러가 발생하였습니다.");
            return "brand/brandForm";
        }
        return "redirect:/admin/brand/management";
    }


    @PostMapping("/admin/brand/deleteBrands")
    public String deleteBrands(IdsTransferDto idsTransferDto){

        List<Long> brandIds = idsTransferDto.getSelectedIds();

        // 브랜드 삭제
        brandService.deleteBrands(brandIds);

        // 브랜드 관리 페이지로 리다이렉션
        return "redirect:/admin/brand/management";

    }


}
