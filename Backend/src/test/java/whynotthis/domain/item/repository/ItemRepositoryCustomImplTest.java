package whynotthis.domain.item.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import whynotthis.domain.item.Age;
import whynotthis.domain.item.Category;
import whynotthis.domain.item.Gender;
import whynotthis.domain.item.Gift;
import whynotthis.domain.item.entity.ItemEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class ItemRepositoryCustomImplTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private EntityManager em;

    @BeforeEach
    @Transactional
    void setUp() {
        // Mock 데이터 삽입
        itemRepository.save(new ItemEntity("Gift1", Gender.MALE, Age.MID_TEENS, Gift.BIRTHDAY, 10000L, Category.LIFESTYLE));
        itemRepository.save(new ItemEntity("Gift2", Gender.MALE, Age.MID_TEENS, Gift.BIRTHDAY, 15000L, Category.FASHION_BEAUTY));
        itemRepository.save(new ItemEntity("Gift3", Gender.MALE, Age.MID_TEENS, Gift.BIRTHDAY, 20000L, Category.HEALTH_WELLNESS));
        itemRepository.save(new ItemEntity("Gift4", Gender.MALE, Age.MID_TEENS, Gift.BIRTHDAY, 25000L, Category.FOOD_DRINKS));
        itemRepository.save(new ItemEntity("Gift5", Gender.MALE, Age.MID_TEENS, Gift.BIRTHDAY, 30000L, Category.DIGITAL_TECH));
        em.flush(); // 영속성 컨텍스트를 데이터베이스에 반영
        em.clear(); // 영속성 컨텍스트 초기화
    }

    @Test
    void testFilterItems() {
        // Given
        Gender gender = Gender.MALE;
        Age age = Age.MID_TEENS;
        Gift gift = Gift.BIRTHDAY;
        Long minPrice = 10000L;
        Long maxPrice = 30000L;

        // When
        List<ItemEntity> result = itemRepository.filterItems(gender, age, gift, minPrice, maxPrice);

        // 데이터베이스에 저장된 모든 아이템 출력
        List<ItemEntity> allItems = itemRepository.findAll();
        System.out.println("All items in database: " + allItems);

        // Then
        assertThat(result).hasSize(3); // 결과가 3개인지 확인
        assertThat(result).allMatch(item -> item.getGift() == gift); // 모든 상품이 동일한 선물 목적을 가지고 있는지 확인
        assertThat(result).allMatch(item -> item.getGender() == gender); // 모든 상품의 성별이 일치하는지 확인
        assertThat(result).allMatch(item -> item.getAge() == age); // 모든 상품의 나이가 일치하는지 확인
        assertThat(result).allMatch(item -> item.getItemPrice() >= minPrice && item.getItemPrice() <= maxPrice); // 가격 조건 확인
    }
}