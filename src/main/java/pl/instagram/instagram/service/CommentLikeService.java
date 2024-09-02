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
import pl.instagram.instagram.model.entity.CommentLikeEntity;
import pl.instagram.instagram.model.entity.PostEntity;
import pl.instagram.instagram.model.entity.UserEntity;
import pl.instagram.instagram.repository.CommentLikeRepository;
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
    private final CommentLikeRepository commentLikeRepository;

    private final AuthService authService;
    private final CommentService commentService;
    private final UserService userService;

    private final CommentMapper commentMapper;

    private CommentLikeEntity getById(CommentLikeEntity.CommentLikeEntityId id){

        return commentLikeRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Nie istnieje polubienie komentarza o takim id"));
    }

    public Page<UserEntity> getCommentLikesPage(UUID commentId, Pageable pageable) throws IllegalArgumentException, EntityNotFoundException {

        if(pageable == null){
            throw new IllegalArgumentException("Paginacja jest wymagana");
        }

        if(!commentRepository.existsById(commentId)){
            throw new EntityNotFoundException("Nie istnieje komentarz o danym id");
        }

        Page<CommentLikeEntity> gotCommentLikesPage = commentLikeRepository.findByCommentId(commentId, pageable);

        return gotCommentLikesPage.map(like -> like.getAuthor());
    }

    @Transactional
    public UserEntity createCommentLike(UUID commentId) throws EntityNotFoundException, ConflictException {

        UserEntity loggedUser = userService.getLoggedUser();
        CommentEntity foundComment = commentService.getById(commentId);

        if(commentLikeRepository.existsByAuthorAccountIdAndCommentId(loggedUser.getAccountId(), commentId)){
            throw new ConflictException("Użytkownik polubił już dany komentarz");
        }

        CommentLikeEntity toCreateCommentLike = new CommentLikeEntity(loggedUser, foundComment);
        CommentLikeEntity createdCommentLike = commentLikeRepository.save(toCreateCommentLike);

        loggedUser.getLikedComments().add(createdCommentLike);

        foundComment.getCommentLikes().add(createdCommentLike);
        foundComment.setLikesCount(foundComment.getLikesCount() + 1);

        return loggedUser;
    }

    @Transactional
    public void deleteCommentLike(UUID commentId) throws EntityNotFoundException {

        CommentEntity foundComment = commentService.getById(commentId);
        UserEntity loggedUser = userService.getLoggedUser();

        CommentLikeEntity.CommentLikeEntityId commentLikeId = new CommentLikeEntity.CommentLikeEntityId(loggedUser.getId(), commentId);
        CommentLikeEntity foundCommentLike = getById(commentLikeId);

        loggedUser.getLikedComments().remove(foundCommentLike);

        foundComment.getCommentLikes().remove(foundCommentLike);
        foundComment.setLikesCount(foundComment.getLikesCount() - 1);

        commentLikeRepository.deleteById(commentLikeId);
    }
}
