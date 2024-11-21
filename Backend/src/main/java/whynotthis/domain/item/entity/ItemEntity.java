package whynotthis.domain.item.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import whynotthis.domain.item.Age;
import whynotthis.domain.item.Category;
import whynotthis.domain.item.Gender;
import whynotthis.domain.item.Gift;
import whynotthis.domain.item.dto.ItemResponseDTO;
import whynotthis.global.BaseEntity;

@Entity
@Getter
@ToString
@NoArgsConstructor
public class ItemEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String itemName;

    @Column(nullable = false)
    private Long itemPrice;

    private String itemImage;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Age age;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gift gift;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;

    public ItemEntity(String itemName, Gender gender, Age age, Gift gift, Long itemPrice, Category category) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.age = age;
        this.gender = gender;
        this.gift = gift;
        this.category = category;
    }

    public ItemResponseDTO toDTO() {
        return new ItemResponseDTO(
                this.itemName,
                this.itemPrice,
                this.itemImage,
                this.category
        );
    }
}
