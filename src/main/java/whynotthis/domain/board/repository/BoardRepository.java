package whynotthis.domain.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import whynotthis.domain.board.entity.BoardEntity;

public interface BoardRepository extends JpaRepository<BoardEntity, Long> {

}
