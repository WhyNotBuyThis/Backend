package whynotthis.domain.board.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import whynotthis.domain.board.entity.BoardEntity;

import java.time.LocalDateTime;

@Getter
@Setter
public class BoardResponseDTO {
    private Long boardId;

    private String boardTitle;

    private String boardContent;

    private String userEmail;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;

    @Builder
    public BoardResponseDTO(BoardEntity boardEntity) {
        this.boardId = boardEntity.getBoardId();
        this.boardTitle = boardEntity.getBoardTitle();
        this.createAt = boardEntity.getCreateAt();
        this.userEmail = boardEntity.getUser().getUserEmail();
        this.updateAt = boardEntity.getUser().getUpdateAt();
        this.boardContent = boardEntity.getBoardContent();
    }
}
