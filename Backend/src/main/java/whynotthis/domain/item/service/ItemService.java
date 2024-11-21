package whynotthis.domain.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import whynotthis.domain.item.dto.ItemRequestDTO;
import whynotthis.domain.item.dto.ItemResponseDTO;
import whynotthis.domain.item.entity.ItemEntity;
import whynotthis.domain.item.repository.ItemRepositoryCustomImpl;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepositoryCustomImpl itemRepositoryCustomImpl;

    public List<ItemResponseDTO> filterItem(ItemRequestDTO itemRequestDTO) {
        Long minPrice = Math.round(itemRequestDTO.getItemPrice() * 0.9);
        Long maxPrice = Math.round(itemRequestDTO.getItemPrice() * 1.1);
        List<ItemEntity> items = itemRepositoryCustomImpl.filterItems(
                itemRequestDTO.getGender(),
                itemRequestDTO.getAge(),
                itemRequestDTO.getGift(),
                minPrice, maxPrice);

        return items.stream()
                .map(ItemEntity::toDTO)
                .toList();
    }
}
