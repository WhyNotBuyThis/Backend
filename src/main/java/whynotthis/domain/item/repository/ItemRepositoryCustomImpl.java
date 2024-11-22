package whynotthis.domain.item.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import whynotthis.domain.item.Age;
import whynotthis.domain.item.Category;
import whynotthis.domain.item.Gender;
import whynotthis.domain.item.Gift;
import whynotthis.domain.item.entity.ItemEntity;
import whynotthis.domain.item.entity.QItemEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryCustomImpl implements ItemRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    // 생성자 주입
    public ItemRepositoryCustomImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<ItemEntity> filterItems(Gender gender, Age age, Gift gift, Long minPrice, Long maxPrice) {
        QItemEntity item = QItemEntity.itemEntity;

        // 조건에 맞는 아이템들이 존재하는 카테고리들을 조회
        List<Category> categories = queryFactory.selectDistinct(item.category)
                .from(item)
                .where(
                        item.gift.eq(gift),
                        item.gender.eq(gender),
                        item.age.eq(age),
                        item.itemPrice.between(minPrice, maxPrice)
                )
                .fetch();

        // 카테고리가 없는 경우 빈 결과 반환
        if (categories.isEmpty()) {
            return Collections.emptyList();
        }

        // 카테고리를 랜덤하게 섞어서 최대 3개 선택
        Collections.shuffle(categories);
        List<Category> selectedCategories = categories.stream()
                .limit(3)
                .toList();

        List<ItemEntity> result = new ArrayList<>();

        // 선택한 각 카테고리에서 조건에 맞는 아이템을 하나씩 가져옴
        for (Category category : selectedCategories) {
            ItemEntity singleItem = queryFactory.selectFrom(item)
                    .where(
                            item.gift.eq(gift),
                            item.gender.eq(gender),
                            item.age.eq(age),
                            item.itemPrice.between(minPrice, maxPrice),
                            item.category.eq(category)
                    )
                    .fetchFirst(); // 첫 번째 아이템만 가져옴

            if (singleItem != null) {
                result.add(singleItem);
            }
        }

        return result;
    }
}
