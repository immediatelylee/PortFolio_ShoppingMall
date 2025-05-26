package com.shoppingmall.project_shoppingmall.repository;

import com.querydsl.core.*;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.*;
import com.shoppingmall.project_shoppingmall.constant.*;
import com.shoppingmall.project_shoppingmall.domain.*;
import com.shoppingmall.project_shoppingmall.dto.*;
import org.springframework.data.domain.*;
import org.thymeleaf.util.*;
import org.springframework.util.StringUtils;



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
        switch (searchDateType) {
            case "1d": return QItem.item.regTime.after(dateTime.minusDays(1));
            case "1w": return QItem.item.regTime.after(dateTime.minusWeeks(1));
            case "1m": return QItem.item.regTime.after(dateTime.minusMonths(1));
            case "6m": return QItem.item.regTime.after(dateTime.minusMonths(6));
            case "1y": return QItem.item.regTime.after(dateTime.minusYears(1));
            default: return null;
        }
    }


    private BooleanExpression searchByLike(ItemSearchType searchBy, String query) {
        if (searchBy == null || !StringUtils.hasText(query)) return null;

        switch (searchBy) {
            case ITEM_NM: return QItem.item.itemNm.contains(query);
            case ITEM_CODE: return QItem.item.itemCode.contains(query);
            case BRAND_NM: return QItem.item.brand.brandNm.contains(query);
            default: return null;
        }
    }



    private BooleanExpression itemNmLike(String searchQuery){
        return StringUtils.isEmpty(searchQuery) ? null : QItem.item.itemNm.like("%" + searchQuery + "%");
    }


    @Override
    public Page<ItemFormDto> getMainItemPage(ItemSearchDto dto, Pageable pageable) {

        QItem item = QItem.item;
        QItemThumbnail itemThumbnail = QItemThumbnail.itemThumbnail;

        BooleanBuilder builder = new BooleanBuilder();

        if (dto.getSearchBy() != null && StringUtils.hasText(dto.getSearchQuery())) {
            builder.and(searchByLike(dto.getSearchBy(), dto.getSearchQuery()));
        }
        if (dto.getSearchSellStatus() != null) {
            builder.and(item.itemSellStatus.eq(dto.getSearchSellStatus()));
        }
        if (dto.getSearchDisplayStatus() != null) {
            builder.and(item.itemDisplayStatus.eq(dto.getSearchDisplayStatus()));
        }
        if (StringUtils.hasText(dto.getMainCategory())) {
            builder.and(item.mainCategory.eq(dto.getMainCategory()));
        }
        if (StringUtils.hasText(dto.getSubCategory())) {
            builder.and(item.subCategory.eq(dto.getSubCategory()));
        }
        if (StringUtils.hasText(dto.getSubSubCategory())) {
            builder.and(item.subSubCategory.eq(dto.getSubSubCategory()));
        }
        if (StringUtils.hasText(dto.getSearchDateType()) && !"all".equalsIgnoreCase(dto.getSearchDateType())) {
            BooleanExpression dateCondition = regDtsAfter(dto.getSearchDateType());
            if (dateCondition != null) {
                builder.and(dateCondition);
            }
        }

        List<ItemFormDto> content = queryFactory
                .select(new QItemFormDto(
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
                ))
                .from(itemThumbnail)
                .join(itemThumbnail.item, item)
                .where(builder)
                .orderBy(item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(item.count())
                .from(itemThumbnail)
                .join(itemThumbnail.item, item)
                .where(builder)
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

