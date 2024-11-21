package whynotthis.domain.board.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import whynotthis.domain.board.entity.BoardEntity;
import whynotthis.domain.user.entity.UserEntity;

@Getter
@Setter
@Builder
public class BoardRequestDTO {
    private String boardTitle;

    private String boardContent;

    public BoardEntity toEntity(UserEntity user) {
        return new BoardEntity(user,boardTitle,boardContent);
    }
}