package whynotthis.domain.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import whynotthis.domain.item.entity.ItemEntity;

public interface ItemRepository extends JpaRepository<ItemEntity, Long>, ItemRepositoryCustom {
}
