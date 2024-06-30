package com.shoppingmall.project_shoppingmall.service;

import com.shoppingmall.project_shoppingmall.config.*;
import com.shoppingmall.project_shoppingmall.constant.*;
import com.shoppingmall.project_shoppingmall.domain.*;
import com.shoppingmall.project_shoppingmall.dto.*;
import com.shoppingmall.project_shoppingmall.repository.*;
import groovy.util.logging.*;
import org.junit.jupiter.api.*;
import org.modelmapper.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import org.springframework.context.annotation.*;
import org.springframework.mock.web.*;
import org.springframework.test.context.*;
import org.springframework.ui.*;
import org.springframework.web.multipart.*;

import java.util.*;



@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
@Slf4j
public class MapperTest {
    @Autowired
    ItemRepository itemRepository;

    @Autowired
    BrandRepository brandRepository;

    List<MultipartFile> createMultipartFiles() throws Exception{

        List<MultipartFile> multipartFileList = new ArrayList<>();

        for(int i=0;i<5;i++){
            String path = "C:/shop/item/";
            String imageName = "image" + i + ".jpg";
            MockMultipartFile multipartFile =
                    new MockMultipartFile(path, imageName, "image/jpg", new byte[]{1,2,3,4});
            multipartFileList.add(multipartFile);
        }

        return multipartFileList;
    }

    List<MultipartFile> createMultipartFiles2() throws Exception{

        List<MultipartFile> multipartFileList2 = new ArrayList<>();

        for(int i=0;i<5;i++){
            String path = "C:/shop/item/";
            String imageName = "image" + i + "D"+ ".jpg";
            MockMultipartFile multipartFile =
                    new MockMultipartFile(path, imageName, "image/jpg", new byte[]{1,2,3,4});
            multipartFileList2.add(multipartFile);
        }

        return multipartFileList2;
    }


    //
    @DisplayName("ModelMapper Domain <-> Dto 변환 로그를 보기위한 테스트 항상 True가 출력된다.")
    @Test
    public void modelMapper1() throws Exception{
        // itemDTO을 생성하는 과정.  현재 등록된 아이템이 없기 떄문
        ModelMapper modelMapper = new ModelMapper();
        ItemFormDto itemFormDto = new ItemFormDto();
        BrandFormDto brandFormDto = new BrandFormDto();
        itemFormDto.setItemNm("테스트상품");
        itemFormDto.setItemSellStatus(ItemSellStatus.SELL);
        itemFormDto.setItemDetail("테스트 상품 입니다.");
        itemFormDto.setPrice(1000);
        itemFormDto.setStockNumber(100);
        List<MultipartFile> multipartFileList = createMultipartFiles();
        List<MultipartFile> multipartDetailFileList = createMultipartFiles2();
        itemFormDto.setBrandId(1L);
        itemFormDto.setMainCategory("지갑");


        Item item = modelMapper.map(itemFormDto,Item.class);
        ItemFormDto itemFormDto1 = modelMapper.map(item,ItemFormDto.class);
        // brandId가 정상적으로 매핑된다.
        System.out.println("ItemFormDto -> Item");
        System.out.println((item.toString()));
        System.out.println("Item->ItemFormDto");
        System.out.println((itemFormDto1.toString()));
    }



}
