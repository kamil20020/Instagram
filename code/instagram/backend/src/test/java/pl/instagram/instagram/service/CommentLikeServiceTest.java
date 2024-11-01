package pl.instagram.instagram.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.instagram.instagram.exception.ConflictException;
import pl.instagram.instagram.exception.EntityNotFoundException;
import pl.instagram.instagram.model.entity.CommentEntity;
import pl.instagram.instagram.model.entity.CommentLikeEntity;
import pl.instagram.instagram.model.entity.UserEntity;
import pl.instagram.instagram.repository.CommentLikeRepository;
import pl.instagram.instagram.repository.CommentRepository;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
public class CommentLikeServiceTest {

    @InjectMocks
    private CommentLikeService commentLikeService;

    @Mock
    private CommentRepository commentRepository;
    
    @Mock
    private CommentLikeRepository commentLikeRepository;

    @Mock
    private CommentService commentService;
    
    @Mock
    private UserService userService;

    @Test
    void shouldGetById(){

        //given
        CommentLikeEntity.CommentLikeEntityId commentLikeId = new CommentLikeEntity.CommentLikeEntityId(null, null);
        CommentLikeEntity commentLike = new CommentLikeEntity();

        //when
        Mockito.when(commentLikeRepository.findById(any())).thenReturn(Optional.of(commentLike));

        CommentLikeEntity gotCommentLike = commentLikeService.getById(commentLikeId);

        //then
        assertEquals(commentLike, gotCommentLike);

        Mockito.verify(commentLikeRepository).findById(commentLikeId);
    }

    @Test
    public void shouldNotGetByNotFoundId(){

        //given
        CommentLikeEntity.CommentLikeEntityId commentLikeEntityId = null;

        //when
        Mockito.when(commentLikeRepository.findById(any())).thenThrow(EntityNotFoundException.class);

        //then
        assertThrows(EntityNotFoundException.class, () -> commentLikeRepository.findById(commentLikeEntityId));
    }

    @Test
    public void shouldDeleteCommentLike(){

        //given
        UUID commentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        UserEntity user = UserEntity.builder()
            .id(userId)
            .likedComments(new HashSet<>())
            .build();

        CommentEntity comment = CommentEntity.builder()
            .id(commentId)
            .commentLikes(new HashSet<>())
            .likesCount(1)
            .build();

        CommentLikeEntity.CommentLikeEntityId commentLikeId = new CommentLikeEntity.CommentLikeEntityId(userId, commentId);
        CommentLikeEntity commentLike = new CommentLikeEntity(user, comment);

        //when
        Mockito.when(commentService.getById(any())).thenReturn(comment);
        Mockito.when(userService.getLoggedUser()).thenReturn(user);
        Mockito.when(commentLikeRepository.findById(any())).thenReturn(Optional.of(commentLike));

        commentLikeService.deleteCommentLike(commentId);

        //then
        assertEquals(0, user.getLikedComments().size());
        assertEquals(Integer.valueOf(0), comment.getLikesCount());
        assertEquals(0, comment.getCommentLikes().size());

        Mockito.verify(userService).getLoggedUser();
        Mockito.verify(commentLikeRepository).deleteById(commentLikeId);
    }

    @Test
    public void shouldNotDeleteLikeWithNotExistingComment(){

        //given
        UUID commentId = UUID.randomUUID();

        //when
        Mockito.when(commentService.getById(any())).thenThrow(EntityNotFoundException.class);

        //then
        assertThrows(EntityNotFoundException.class, () -> commentLikeService.deleteCommentLike(commentId));

        Mockito.verify(commentService).getById(commentId);
    }

    @Test
    public void shouldCreateComment(){

        //given
        UUID commentId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String accountId = "A";

        UserEntity user = UserEntity.builder()
            .id(userId)
            .accountId(accountId)
            .likedComments(new HashSet<>())
            .build();

        CommentEntity comment = CommentEntity.builder()
            .id(commentId)
            .likesCount(0)
            .commentLikes(new HashSet<>())
            .build();

        CommentLikeEntity commentLike = new CommentLikeEntity(user, comment);

        //when
        Mockito.when(commentService.getById(any())).thenReturn(comment);
        Mockito.when(userService.getLoggedUser()).thenReturn(user);
        Mockito.when(commentLikeRepository.existsByAuthorAccountIdAndCommentId(any(), any())).thenReturn(false);
        Mockito.when(commentLikeRepository.save(any())).thenReturn(commentLike);

        UserEntity commentLikeUser = commentLikeService.createCommentLike(commentId);

        //then
        assertEquals(user, commentLikeUser);
        assertEquals(1, user.getLikedComments().size());
        assertEquals(1, comment.getCommentLikes().size());
        assertEquals(Integer.valueOf(1), comment.getLikesCount());

        Mockito.verify(commentService).getById(commentId);
        Mockito.verify(userService).getLoggedUser();
        Mockito.verify(commentLikeRepository).existsByAuthorAccountIdAndCommentId(accountId, commentId);
        Mockito.verify(commentLikeRepository).save(commentLike);
    }

    @Test
    public void shouldNotCreateSameLike(){

        //given
        UUID commentId = UUID.randomUUID();
        String accountId = "A";
        UUID userId = UUID.randomUUID();

        CommentEntity comment = CommentEntity.builder()
            .id(commentId)
            .build();

        UserEntity user = UserEntity.builder()
            .id(userId)
            .accountId(accountId)
            .build();

        //when
        Mockito.when(commentService.getById(any())).thenReturn(comment);
        Mockito.when(userService.getLoggedUser()).thenReturn(user);
        Mockito.when(commentLikeRepository.existsByAuthorAccountIdAndCommentId(any(), any())).thenReturn(true);

        //then
        assertThrows(ConflictException.class, () -> commentLikeService.createCommentLike(commentId));

        Mockito.verify(commentService).getById(commentId);
        Mockito.verify(userService).getLoggedUser();
        Mockito.verify(commentLikeRepository).existsByAuthorAccountIdAndCommentId(accountId, commentId);
    }
}
