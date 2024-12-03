package whynotthis.domain.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import whynotthis.domain.item.dto.ItemRequestDTO;
import whynotthis.domain.item.dto.ItemResponseDTO;
import whynotthis.domain.item.service.ItemService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping("/recommend")
    public ResponseEntity<List<ItemResponseDTO>> filterItems(@RequestBody ItemRequestDTO itemRequestDTO) {
        System.out.println("Request received in controller: " + itemRequestDTO);
        List<ItemResponseDTO> response = itemService.filterItem(itemRequestDTO);
        return ResponseEntity.ok(response);
    }
}
