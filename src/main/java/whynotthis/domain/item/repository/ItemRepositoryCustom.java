package whynotthis.domain.item.repository;

import whynotthis.domain.item.Age;
import whynotthis.domain.item.Gender;
import whynotthis.domain.item.Gift;
import whynotthis.domain.item.entity.ItemEntity;

import java.util.List;

public interface ItemRepositoryCustom {
    List<ItemEntity> filterItems(Gender gender, Age age, Gift gift, Long minPrice, Long maxPrice);
}
