package com.shoppingmall.project_shoppingmall.repository;

import com.querydsl.core.*;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.*;
import com.shoppingmall.project_shoppingmall.constant.*;
import com.shoppingmall.project_shoppingmall.domain.*;
import com.shoppingmall.project_shoppingmall.dto.*;
import org.springframework.data.domain.*;
import org.thymeleaf.util.*;



import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

public class ItemRepositoryCustomImpl implements ItemRepositoryCustom {

    private JPAQueryFactory queryFactory;

    public ItemRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Long countAllItems() {
        QItem item = QItem.item;
        return queryFactory
                .select(item.count())
                .from(item)
                .fetchOne();
    }

    @Override
    public Long countItemsBySellStatus(ItemSellStatus sellStatus) {
        QItem item = QItem.item;
        return queryFactory
                .select(item.count())
                .from(item)
                .where(item.itemSellStatus.eq(sellStatus))
                .fetchOne();
    }

    @Override
    public Long countItemsByDisplayStatus(ItemDisplayStatus displayStatus) {
        QItem item = QItem.item;
        return queryFactory
                .select(item.count())
                .from(item)
                .where(item.itemDisplayStatus.eq(displayStatus))
                .fetchOne();
    }

    @Override
    public List<Item> findItemsBySellStatus(ItemSellStatus sellStatus) {
        QItem item = QItem.item;
        return queryFactory
                .selectFrom(item)
                .where(item.itemSellStatus.eq(sellStatus))
                .fetch();
    }


    @Override
    public List<Item> findItemsByDisplayStatus(ItemDisplayStatus displayStatus) {
        QItem item = QItem.item;
        return queryFactory
                .selectFrom(item)
                .where(item.itemDisplayStatus.eq(displayStatus))
                .fetch();
    }

    public static BooleanExpression mainCategoryEq(String mainCategory) {
        if (!org.springframework.util.StringUtils.hasText(mainCategory)) {
            return null;
        }
        return QItem.item.mainCategory.eq(mainCategory);
    }

    public static BooleanExpression subCategoryEq(String subCategory) {
        if (!org.springframework.util.StringUtils.hasText(subCategory)) {
            return null;
        }
        return QItem.item.subCategory.eq(subCategory);
    }

    public static BooleanExpression subSubCategoryEq(String subSubCategory) {
        if (!org.springframework.util.StringUtils.hasText(subSubCategory)) {
            return null;
        }
        return QItem.item.subSubCategory.eq(subSubCategory);
    }


    private BooleanExpression searchSellStatusEq(ItemSellStatus searchSellStatus) {
        return searchSellStatus == null ? null : QItem.item.itemSellStatus.eq(searchSellStatus);
    }

    private BooleanExpression searchDisplayStatusEq(ItemDisplayStatus searchDisplayStatus) {
        QItem item = QItem.item;
        return searchDisplayStatus != null ? item.itemDisplayStatus.eq(searchDisplayStatus) : null;
    }

    private BooleanExpression regDtsAfter(String searchDateType) {

        LocalDateTime dateTime = LocalDateTime.now();

        if (StringUtils.equals("all", searchDateType) || searchDateType == null) {
            return null;
        } else if (StringUtils.equals("1d", searchDateType)) {
            dateTime = dateTime.minusDays(1);
        } else if (StringUtils.equals("1w", searchDateType)) {
            dateTime = dateTime.minusWeeks(1);
        } else if (StringUtils.equals("1m", searchDateType)) {
            dateTime = dateTime.minusMonths(1);
        } else if (StringUtils.equals("6m", searchDateType)) {
            dateTime = dateTime.minusMonths(6);
        } else if (StringUtils.equals("1y", searchDateType)) {
            dateTime = dateTime.minusYears(1);
        }
        return QItem.item.regTime.after(dateTime);
    }

    private BooleanExpression searchByLike(String searchBy, String searchQuery) {

        if (StringUtils.equals("ITEM_NM", searchBy)) {
            return QItem.item.itemNm.like("%" + searchQuery + "%");
        } else if (StringUtils.equals("itemCode", searchBy)) {
            return QItem.item.itemCode.like("%" + searchQuery + "%");
        } else if (StringUtils.equals("brand_nm", searchBy)) {
            return QItem.item.brand.brandNm.like("%" + searchQuery + "%");
        }

        return null;
    }


    private BooleanExpression itemNmLike(String searchQuery){
        return StringUtils.isEmpty(searchQuery) ? null : QItem.item.itemNm.like("%" + searchQuery + "%");
    }

    @Override
    public Page<ItemFormDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {

        System.out.println("searchBy: " + itemSearchDto.getSearchBy());
        System.out.println("searchQuery: " + itemSearchDto.getSearchQuery());

        QItem item = QItem.item;
        QItemThumbnail itemThumbnail = QItemThumbnail.itemThumbnail;

        List<ItemFormDto> content = queryFactory
                .select(
                        new QItemFormDto(
                                item.id,
                                item.itemCode,
                                item.itemNm,
                                item.itemDisplayStatus,
                                item.itemSellStatus,
                                itemThumbnail.imgUrl,
                                item.price,
                                item.mainCategory,
                                item.subCategory,
                                item.subSubCategory
                                )
                )
                .from(itemThumbnail)
                .join(itemThumbnail.item, item)
                .where(
                        searchByLike(itemSearchDto.getSearchBy().toString(), itemSearchDto.getSearchQuery()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchDisplayStatusEq(itemSearchDto.getSearchDisplayStatus()),
                        regDtsAfter(itemSearchDto.getSearchDateType()),
                        mainCategoryEq(itemSearchDto.getMainCategory()),
                        subCategoryEq(itemSearchDto.getSubCategory()),
                        subSubCategoryEq(itemSearchDto.getSubSubCategory())
                        )
                .orderBy(item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        //itemNmLike(itemSearchDto.getSearchQuery()), where에서 제거하고 searchByLike(itemSearchDto.get~) 으로 바꿈

        Long total = queryFactory
                .select(item.count())
                .from(itemThumbnail)
                .join(itemThumbnail.item, item)
                .where(searchByLike(itemSearchDto.getSearchBy().toString().toLowerCase(), itemSearchDto.getSearchQuery())
                )
                .fetchOne();
        return new PageImpl<>(content, pageable, total);
    }

//  여기도 where을 itemNmLikeike(itemSearchDto.getSearchQuery()) 에서 searchByLike로 바꿈


    @Override
    public List<ItemWithImgDto> findItemsWithImgsBySubCategory(String subCategory) {
        QItem item = QItem.item;
        QItemImg itemImg = QItemImg.itemImg;

        return queryFactory
                .select(new QItemWithImgDto(
                        item.id,
                        item.itemNm,
                        item.price,
                        item.itemDetail,
                        item.itemSellStatus.stringValue(),
                        item.itemDisplayStatus.stringValue(),
                        item.mainCategory,
                        item.subCategory,
                        item.subSubCategory,
                        item.brand.brandNm,
                        itemImg.imgUrl
                ))
                .from(item)
                .leftJoin(itemImg).on(itemImg.item.eq(item))
                .where(item.subCategory.eq(subCategory),
                        (item.itemDisplayStatus.eq(ItemDisplayStatus.DISPLAY)), // 진열 상태 조건 추가
                        itemImg.repimgYn.eq("Y")) // 대표 이미지 조건
                .fetch();
    }
}

