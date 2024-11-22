//package whynotthis.domain.item.service;
//
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//import whynotthis.domain.item.Age;
//import whynotthis.domain.item.Gender;
//import whynotthis.domain.item.dto.ItemDTO;
//import whynotthis.domain.item.entity.ItemEntity;
//
//import java.util.List;
//
//
//@SpringBootTest
//@Transactional
//class ItemServiceTest {
//
//    @Autowired
//    private ItemService itemService;
//
//    @Autowired
//    private ItemRepositoryImpl itemRepositoryImpl;
//
//    @BeforeEach
//    void setup() {
//        // 테스트용 데이터 삽입
//        itemRepositoryImpl.save(new ItemEntity("Teen Fashion Sneakers", 10000L, "", Age.EARLY_TEENS, Gender.FEMALE));
//        itemRepositoryImpl.save(new ItemEntity("Cool Graphic T-Shirt", 15000L, "", Age.MID_TEENS, Gender.MALE));
//        itemRepositoryImpl.save(new ItemEntity("Trendy Backpack", 20000L, "", Age.LATE_TEENS, Gender.FEMALE));
//        itemRepositoryImpl.save(new ItemEntity("Casual Wrist Watch", 25000L, "", Age.EARLY_TWENTIES, Gender.UNISEX));
//        itemRepositoryImpl.save(new ItemEntity("Leather Wallet", 30000L, "", Age.MID_TWENTIES, Gender.MALE));
//        itemRepositoryImpl.save(new ItemEntity("Stylish Sunglasses", 35000L, "", Age.LATE_TWENTIES, Gender.FEMALE));
//    }
//
//    @Test
//    @DisplayName("조건에 맞는 아이템을 가져오기")
//    void serarchItemByPrice() {
//        // Given
//        ItemDTO itemDTO = new ItemDTO(Gender.FEMALE, Age.EARLY_TEENS, 20000L);
//        // When
//        List<ItemEntity> result = itemService.searchItem(itemDTO);
//        // Then
//        Assertions.assertEquals(1,result.size(),"검색 결과는 1개");
//
//    }
//}