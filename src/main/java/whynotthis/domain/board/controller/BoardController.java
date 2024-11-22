package whynotthis.domain.board.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import whynotthis.domain.board.dto.BoardRequestDTO;
import whynotthis.domain.board.dto.BoardResponseDTO;
import whynotthis.domain.board.service.BoardService;
import whynotthis.domain.common.ApiResponse;
import whynotthis.domain.common.SecurityUtil;
import whynotthis.domain.user.dto.CustomUserDetails;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class BoardController {

    private final BoardService boardService;

    @PostMapping
    public ResponseEntity<ApiResponse<Object>> write(@RequestBody BoardRequestDTO boardRequestDTO) {
        CustomUserDetails userDetails = SecurityUtil.getEmail();
        String email = userDetails.getUsername();

        boardService.createBoard(boardRequestDTO,email);

        ApiResponse<Object> response = ApiResponse.success("게시글 작성 완료.", boardRequestDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> read(@PathVariable Long id) {
        BoardResponseDTO board = boardService.getBoard(id);
        ApiResponse<Object> response = ApiResponse.success("게시글 불러오기 성공",board);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Object>> readAll() {
        List<BoardResponseDTO> boardList = boardService.getBoardList();
        ApiResponse<Object> response = ApiResponse.success("게시물 전체 불러오기 성공",boardList);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> update(@PathVariable Long id, @RequestBody BoardRequestDTO boardRequestDTO) {
        CustomUserDetails email = SecurityUtil.getEmail();
        BoardResponseDTO updatedBoard = boardService.updateBoard(boardRequestDTO, id, email);
        ApiResponse<Object> response = ApiResponse.success("게시글이 성공적으로 수정되었습니다.", updatedBoard);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable Long boardId) {
        CustomUserDetails userDetails = SecurityUtil.getEmail();
        String email = userDetails.getUsername();

        boardService.delete(boardId,email);
        ApiResponse<Object> response = ApiResponse.success("게시글이 삭제되었습니다.");
        return ResponseEntity.ok(response);
    }
}
