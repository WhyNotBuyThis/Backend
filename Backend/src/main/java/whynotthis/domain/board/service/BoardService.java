package whynotthis.domain.board.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import whynotthis.domain.board.dto.BoardRequestDTO;
import whynotthis.domain.board.dto.BoardResponseDTO;
import whynotthis.domain.board.entity.BoardEntity;
import whynotthis.domain.board.repository.BoardRepository;
import whynotthis.domain.exception.GeneralException;
import whynotthis.domain.jwt.ErrorCode;
import whynotthis.domain.user.dto.CustomUserDetails;
import whynotthis.domain.user.entity.UserEntity;
import whynotthis.domain.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    public void createBoard(BoardRequestDTO boardRequestDTO, String email) {
        String title = boardRequestDTO.getBoardTitle();
        String content = boardRequestDTO.getBoardContent();

        if(title == null) {
            throw new GeneralException(ErrorCode.EMPTY_TITLE);
        }

        if(content == null) {
            throw new GeneralException(ErrorCode.EMPTY_CONTENT);
        }

        UserEntity user = userRepository.findByUserEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));

        BoardEntity boardEntity = boardRequestDTO.toEntity(user);
        boardRepository.save(boardEntity);
    }

    public BoardResponseDTO getBoard(Long boardId) {
        BoardEntity board = boardRepository.findById(boardId)
                .orElseThrow(() -> new GeneralException(ErrorCode.NOT_FIND_BOARD));

        return BoardResponseDTO.builder()
                .boardEntity(board)
                .build();
    }

    public List<BoardResponseDTO> getBoardList() {
        List<BoardEntity> boards = boardRepository.findAll();
        return boards.stream()
                .map(b -> BoardResponseDTO.builder()
                        .boardEntity(b)
                        .build())
                .toList();
    }


    public BoardResponseDTO updateBoard(BoardRequestDTO boardRequestDTO, Long boardId, CustomUserDetails userDetails) {
        String email = userDetails.getUsername();
        BoardEntity board = boardRepository.findById(boardId)
                .orElseThrow(() -> new GeneralException(ErrorCode.NOT_FIND_BOARD));
        if(!board.getUser().getUserEmail().equals(email)) {
            throw new GeneralException(ErrorCode.UNAUTHORIZED_ACCESS);
        }
        board.update(boardRequestDTO);
        return BoardResponseDTO.builder()
                .boardEntity(board)
                .build();
    }

    public void delete(Long boardId,String email) {
        BoardEntity board = boardRepository.findById(boardId)
                .orElseThrow(() -> new GeneralException(ErrorCode.NOT_FIND_BOARD));

        if(!board.getUser().getUserEmail().equals(email)) {
            throw new GeneralException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        boardRepository.deleteById(boardId);
    }
}
