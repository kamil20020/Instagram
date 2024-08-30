package pl.instagram.instagram.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.instagram.instagram.exception.ConflictException;
import pl.instagram.instagram.exception.EntityNotFoundException;
import pl.instagram.instagram.mapper.CommentMapper;
import pl.instagram.instagram.model.domain.CommentEntityForLoggedUser;
import pl.instagram.instagram.model.entity.CommentEntity;
import pl.instagram.instagram.model.entity.PostEntity;
import pl.instagram.instagram.model.entity.UserEntity;
import pl.instagram.instagram.repository.CommentRepository;
import pl.instagram.instagram.repository.UserRepository;

import java.util.UUID;

@Lazy
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentLikeService {

    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    private final AuthService authService;
    private final CommentService commentService;
    private final UserService userService;

    private final CommentMapper commentMapper;

    public Page<UserEntity> getCommentLikesPage(UUID commentId, Pageable pageable) throws IllegalArgumentException, EntityNotFoundException {

        if(pageable == null){
            throw new IllegalArgumentException("Paginacja jest wymagana");
        }

        if(!commentRepository.existsById(commentId)){
            throw new EntityNotFoundException("Nie istnieje komentarz o danym id");
        }

        return userRepository.findByLikedCommentsId(commentId, pageable);
    }

    @Transactional
    public UserEntity createCommentLike(UUID commentId) throws EntityNotFoundException, ConflictException {

        CommentEntity foundComment = commentService.getById(commentId);

        String loggedAccountId = authService.getLoggedUserAccountId();

        if(userRepository.existsByAccountIdAndLikedCommentsId(loggedAccountId, commentId)){
            throw new ConflictException("Użytkownik polubił już dany komentarz");
        }

        UserEntity loggedUser = userService.getLoggedUser();

        loggedUser.getLikedComments().add(foundComment);

        foundComment.setLikesCount(foundComment.getLikesCount() + 1);

        return loggedUser;
    }

    @Transactional
    public void deleteCommentLike(UUID commentId) throws EntityNotFoundException {

        CommentEntity foundComment = commentService.getById(commentId);

        String loggedAccountId = authService.getLoggedUserAccountId();

        if(!userRepository.existsByAccountIdAndLikedCommentsId(loggedAccountId, commentId)){
            throw new EntityNotFoundException("Użytkownik nie polubił otrzymanego komentarza");
        }

        UserEntity loggedUser = userService.getLoggedUser();

        loggedUser.getLikedComments().remove(foundComment);

        foundComment.setLikesCount(foundComment.getLikesCount() - 1);
    }
}
