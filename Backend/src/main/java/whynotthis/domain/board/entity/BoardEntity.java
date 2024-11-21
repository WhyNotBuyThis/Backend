package whynotthis.domain.board.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import whynotthis.domain.board.dto.BoardRequestDTO;
import whynotthis.domain.user.entity.UserEntity;
import whynotthis.global.BaseEntity;

@Entity
@Getter
@NoArgsConstructor
public class BoardEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;

    private String boardTitle;

    private String boardContent;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    public BoardEntity(UserEntity user,String boardTitle,String boardContent) {
        this.user = user;
        this.boardTitle = boardTitle;
        this.boardContent = boardContent;
    }

    public void update(BoardRequestDTO boardRequestDTO) {
        this.boardTitle = boardRequestDTO.getBoardTitle();
        this.boardContent = boardRequestDTO.getBoardContent();
    }
}
