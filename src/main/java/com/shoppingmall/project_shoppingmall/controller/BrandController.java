package com.shoppingmall.project_shoppingmall.controller;

import com.shoppingmall.project_shoppingmall.constant.*;
import com.shoppingmall.project_shoppingmall.domain.*;
import com.shoppingmall.project_shoppingmall.dto.*;
import com.shoppingmall.project_shoppingmall.service.*;
import lombok.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.ui.*;
import org.springframework.validation.*;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.*;
import javax.validation.*;
import java.util.*;
import java.util.stream.*;

@RequiredArgsConstructor
@Controller
public class BrandController {

    private final BrandService brandService;

    @ModelAttribute("brandStatus")
    public BrandStatus[] brandStatuses(){
        return BrandStatus.values();
    }

    @ModelAttribute("brands")
    public List<Brand> brandList() {
        return brandService.findAll();
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
//        return "brand/brandForm";
    }

    @GetMapping(value = "/admin/brand/management")
    public String brandmanage(Model model){
        model.addAttribute("brandFormDto",new BrandFormDto());
        model.addAttribute("IdsTransferDto",new IdsTransferDto());  // retrun list
        return "brand/brandForm";
    }

    @PostMapping("/admin/brand/deleteBrands")
    public String deleteBrands(IdsTransferDto idsTransferDto){

        List<Long> brandIds = idsTransferDto.getSelectedBrandIds();

        // 브랜드 삭제
        brandService.deleteBrands(brandIds);

        // 브랜드 관리 페이지로 리다이렉션
        return "redirect:/admin/brand/management";

    }


}
