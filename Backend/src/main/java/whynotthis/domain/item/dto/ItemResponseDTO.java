package whynotthis.domain.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import whynotthis.domain.item.Category;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ItemResponseDTO {
    private String itemName;

    private Long itemPrice;

    private String itemImage;

    private Category category;

}
