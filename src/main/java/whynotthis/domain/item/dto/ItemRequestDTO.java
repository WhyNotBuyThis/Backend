package whynotthis.domain.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import whynotthis.domain.item.Age;
import whynotthis.domain.item.Gender;
import whynotthis.domain.item.Gift;

@Getter
@Setter
public class ItemRequestDTO {
    private Age age;

    private Gender gender;

    private Long itemPrice;

    private Gift gift;

    public ItemRequestDTO(Gender gender, Age age, Gift gift, long itemPrice) {
        this.gender = gender;
        this.age = age;
        this.gift = gift;
        this.itemPrice = itemPrice;
    }
}
