package pl.instagram.instagram.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import pl.instagram.instagram.exception.EntityNotFoundException;
import pl.instagram.instagram.exception.UserIsNotResourceAuthorException;
import pl.instagram.instagram.mapper.CommentMapper;
import pl.instagram.instagram.model.domain.CommentEntityForLoggedUser;
import pl.instagram.instagram.model.entity.CommentEntity;
import pl.instagram.instagram.model.entity.PostEntity;
import pl.instagram.instagram.model.entity.UserEntity;
import pl.instagram.instagram.repository.CommentRepository;
import pl.instagram.instagram.repository.PostRepository;
import pl.instagram.instagram.repository.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserService userService;

    @Mock
    private PostService postService;

    @Mock
    private AuthService authService;

    @Mock
    private CommentMapper commentMapper;

    @Test
    void shouldGetById() {

        //given
        UUID commentId = UUID.randomUUID();

        CommentEntity comment = CommentEntity.builder()
            .content("Dobre zdjęcie")
            .build();

        comment.setId(commentId);

        //when
        Mockito.when(commentRepository.findById(any())).thenReturn(Optional.of(comment));

        CommentEntity gotComment = commentService.getById(commentId);

        //then
        assertEquals(comment, gotComment);

        Mockito.verify(commentRepository).findById(commentId);
    }

    @Test
    void shouldNotGetByIdWhenCommentDoesNotExist() {

        //given
        UUID commentId = UUID.randomUUID();

        //when
        Mockito.when(commentRepository.findById(any())).thenReturn(Optional.empty());

        //then
        assertThrows(
            EntityNotFoundException.class,
            () -> commentService.getById(commentId),
            "Nie istnieje komentarz o takim id"
        );

        Mockito.verify(commentRepository).findById(commentId);
    }

    @Test
    void shouldGetPostCommentsPageWhenParentCommentIdIsNull() {

        //given
        UUID postId = UUID.randomUUID();

        CommentEntity comment1 = CommentEntity.builder()
            .content("Dobre zdjęcie")
            .build();

        CommentEntity comment2 = CommentEntity.builder()
            .content("Dobry opis")
            .build();

        Page<CommentEntity> commentsPage = new PageImpl<>(List.of(comment1, comment2));

        Pageable pageable = PageRequest.of(0, 5);

        //when
        Mockito.when(postService.existsById(any())).thenReturn(true);
        Mockito.when(commentRepository.getAllByPostIdAndParentCommentId(any(), any(), any())).thenReturn(commentsPage);
        Mockito.when(authService.isUserLogged()).thenReturn(false);

        Page<CommentEntityForLoggedUser> gotCommentsPage = commentService.getPostCommentsPage(postId, null, pageable);

        //then
        assertEquals(commentsPage.getTotalElements(), gotCommentsPage.getTotalElements());
        assertEquals(commentsPage.getContent().get(0).getContent(), gotCommentsPage.getContent().get(0).getContent());
        assertEquals(commentsPage.getContent().get(1).getContent(), gotCommentsPage.getContent().get(1).getContent());

        Mockito.verify(postService).existsById(postId);
        Mockito.verify(commentRepository).getAllByPostIdAndParentCommentId(eq(postId), eq(null), any());
        Mockito.verify(authService).isUserLogged();
    }

    @Test
    void shouldGetPostCommentsPageWhenParentCommentIdIsNotNull() {

        //given
        UUID postId = UUID.randomUUID();
        UUID parentCommentId = UUID.randomUUID();

        CommentEntity comment1 = CommentEntity.builder()
            .content("Dobre zdjęcie")
            .build();

        CommentEntity comment2 = CommentEntity.builder()
            .content("Dobry opis")
            .build();

        Page<CommentEntity> commentsPage = new PageImpl<>(List.of(comment1, comment2));

        Pageable pageable = PageRequest.of(0, 5);

        //when
        Mockito.when(postService.existsById(any())).thenReturn(true);
        Mockito.when(commentRepository.existsById(any())).thenReturn(true);
        Mockito.when(commentRepository.getAllByPostIdAndParentCommentId(any(), any(), any())).thenReturn(commentsPage);
        Mockito.when(authService.isUserLogged()).thenReturn(false);

        Page<CommentEntityForLoggedUser> gotCommentsPage = commentService.getPostCommentsPage(postId, parentCommentId, pageable);

        //then
        assertEquals(commentsPage.getTotalElements(), gotCommentsPage.getTotalElements());
        assertEquals(commentsPage.getContent().get(0).getContent(), gotCommentsPage.getContent().get(0).getContent());
        assertEquals(commentsPage.getContent().get(1).getContent(), gotCommentsPage.getContent().get(1).getContent());

        Mockito.verify(postService).existsById(postId);
        Mockito.verify(commentRepository).existsById(parentCommentId);
        Mockito.verify(commentRepository).getAllByPostIdAndParentCommentId(eq(postId), eq(parentCommentId), any());
        Mockito.verify(authService).isUserLogged();
    }

    @Test
    void shouldNotGetPostCommentsPageWhenPaginationIsNull() {

        //given
        UUID postId = UUID.randomUUID();

        //when
        //then
        assertThrows(
            IllegalArgumentException.class,
            () -> commentService.getPostCommentsPage(postId, null, null),
            "Paginacja jest wymagana"
        );
    }

    @Test
    void shouldNotGetPostCommentsPageWhenPostIsNotFound() {

        //given
        UUID postId = UUID.randomUUID();

        Pageable pageable = PageRequest.of(0, 5);

        //when
        Mockito.when(postService.existsById(any())).thenReturn(false);

        //then
        assertThrows(
            EntityNotFoundException.class,
            () -> commentService.getPostCommentsPage(postId, null, pageable),
            "Nie istnieje post o takim id"
        );

        Mockito.verify(postService).existsById(postId);
    }

    @Test
    void shouldNotGetPostCommentsPageWhenParentCommentWasNotFound() {

        //given
        UUID postId = UUID.randomUUID();
        UUID parentCommentId = UUID.randomUUID();

        Pageable pageable = PageRequest.of(0, 5);

        //when
        Mockito.when(postService.existsById(any())).thenReturn(true);
        Mockito.when(commentRepository.existsById(any())).thenReturn(false);

        //then
        assertThrows(
            EntityNotFoundException.class,
            () -> commentService.getPostCommentsPage(postId, parentCommentId, pageable),
            "Nie istnieje nadrzędny komentarz o takim id"
        );

        Mockito.verify(postService).existsById(postId);
        Mockito.verify(commentRepository).existsById(parentCommentId);
    }

    @Test
    void shouldCreateCommentWhenParentCommentIdIsNull() {

        //given
        UUID postId = UUID.randomUUID();
        Optional<UUID> parentCommentIdOpt = Optional.empty();
        String content = "Dobre zdjęcie";

        PostEntity post = PostEntity.builder()
            .comments(new HashSet<>())
            .commentsCount(0)
            .build();

        UserEntity loggedUser = UserEntity.builder()
            .comments(new HashSet<>())
            .build();

        CommentEntity createdComment = CommentEntity.builder()
            .content(content)
            .author(loggedUser)
            .post(post)
            .build();

        //when
        Mockito.when(postService.getPostById(any())).thenReturn(post);
        Mockito.when(userService.getLoggedUser()).thenReturn(loggedUser);
        Mockito.when(commentRepository.save(any())).thenReturn(createdComment);

        CommentEntity gotComment = commentService.createComment(postId, parentCommentIdOpt, content);

        //then
        assertEquals(createdComment.getContent(), gotComment.getContent());
        assertEquals(createdComment.getAuthor(), gotComment.getAuthor());
        assertEquals(createdComment.getPost(), gotComment.getPost());

        assertEquals(1, loggedUser.getComments().size());
        assertEquals(1, post.getComments().size());
        assertEquals(1, post.getCommentsCount());

        Mockito.verify(postService).getPostById(postId);
        Mockito.verify(userService).getLoggedUser();
        Mockito.verify(commentRepository).save(any());
    }

    @Test
    void shouldCreateCommentWhenParentCommentIdIsNotNull() {

        //given
        UUID postId = UUID.randomUUID();
        Optional<UUID> parentCommentIdOpt = Optional.of(UUID.randomUUID());
        String content = "Zgadzam się";

        PostEntity post = PostEntity.builder()
            .comments(new HashSet<>())
            .commentsCount(0)
            .build();

        UserEntity loggedUser = UserEntity.builder()
            .comments(new HashSet<>())
            .build();

        CommentEntity createdComment = CommentEntity.builder()
            .content(content)
            .author(loggedUser)
            .post(post)
            .build();

        CommentEntity parentComment = CommentEntity.builder()
            .content("Dobre zdjęcie")
            .subComments(new HashSet<>())
            .subCommentsCount(0)
            .build();

        //when
        Mockito.when(postService.getPostById(any())).thenReturn(post);
        Mockito.when(userService.getLoggedUser()).thenReturn(loggedUser);
        Mockito.when(commentRepository.findById(any())).thenReturn(Optional.of(parentComment));
        Mockito.when(commentRepository.save(any())).thenReturn(createdComment);

        CommentEntity gotComment = commentService.createComment(postId, parentCommentIdOpt, content);

        //then
        assertEquals(createdComment.getContent(), gotComment.getContent());
        assertEquals(createdComment.getAuthor(), gotComment.getAuthor());
        assertEquals(createdComment.getPost(), gotComment.getPost());

        assertEquals(1, parentComment.getSubCommentsCount());
        assertEquals(1, parentComment.getSubComments().size());

        assertEquals(1, loggedUser.getComments().size());
        assertEquals(1, post.getComments().size());
        assertEquals(1, post.getCommentsCount());

        Mockito.verify(postService).getPostById(postId);
        Mockito.verify(userService).getLoggedUser();
        Mockito.verify(commentRepository).findById(parentCommentIdOpt.get());
        Mockito.verify(commentRepository).save(any());
    }

    @Test
    void shouldNotCreateCommentWhenPostIsNotFound() {

        //given
        UUID postId = UUID.randomUUID();
        Optional<UUID> parentCommentIdOpt = Optional.empty();
        String content = "Dobre zdjęcie";

        //when
        Mockito.when(postService.getPostById(any())).thenThrow(EntityNotFoundException.class);

        //then
        assertThrows(
            EntityNotFoundException.class,
            () -> commentService.createComment(postId, parentCommentIdOpt, content)
        );

        Mockito.verify(postService).getPostById(postId);
    }

    @Test
    void shouldNotCreateCommentWhenAuthorIsNotFound() {

        //given
        UUID postId = UUID.randomUUID();
        Optional<UUID> parentCommentIdOpt = Optional.empty();
        String content = "Dobre zdjęcie";

        //when
        Mockito.when(postService.getPostById(any())).thenReturn(new PostEntity());
        Mockito.when(userService.getLoggedUser()).thenThrow(EntityNotFoundException.class);

        //then
        assertThrows(
            EntityNotFoundException.class,
            () -> commentService.createComment(postId, parentCommentIdOpt, content)
        );

        Mockito.verify(postService).getPostById(postId);
        Mockito.verify(userService).getLoggedUser();
    }

    @Test
    void shouldNotCreateCommentWhenParentCommentDoesNotExist() {

        //given
        UUID postId = UUID.randomUUID();
        Optional<UUID> parentCommentIdOpt = Optional.of(UUID.randomUUID());
        String content = "Dobre zdjęcie";

        //when
        Mockito.when(postService.getPostById(any())).thenReturn(new PostEntity());
        Mockito.when(userService.getLoggedUser()).thenReturn(new UserEntity());
        Mockito.when(commentRepository.findById(parentCommentIdOpt.get())).thenReturn(Optional.empty());

        //then
        assertThrows(
            EntityNotFoundException.class,
            () -> commentService.createComment(postId, parentCommentIdOpt, content),
            "Nie istnieje nadrzędny komentarz o takim id"
        );

        Mockito.verify(postService).getPostById(postId);
        Mockito.verify(userService).getLoggedUser();
        Mockito.verify(commentRepository).findById(parentCommentIdOpt.get());
    }

    @Test
    void shouldUpdateComment() {

        //given
        UUID commentId = UUID.randomUUID();
        String oldContent = "Dobry posty";

        String accountId = "A";

        UserEntity author = new UserEntity();
        author.setAccountId(accountId);

        CommentEntity oldComment = new CommentEntity();
        oldComment.setId(commentId);
        oldComment.setContent(oldContent);
        oldComment.setAuthor(author);

        String newContent = "Dobre zdjęcie";

        //when
        Mockito.when(commentRepository.findById(any())).thenReturn(Optional.of(oldComment));
        Mockito.doNothing().when(authService).checkLoggedUserResourceAuthorship(anyString());

        CommentEntity gotComment = commentService.updateComment(commentId, newContent);

        //then
        assertEquals(newContent, gotComment.getContent());

        Mockito.verify(commentRepository).findById(commentId);
        Mockito.verify(authService).checkLoggedUserResourceAuthorship(oldComment.getAuthor().getAccountId());
    }

    @Test
    void shouldNotUpdateCommentWhenCommentWasNotFound() {

        //given
        UUID commentId = UUID.randomUUID();
        String newContent = "Dobre zdjęcie";

        //when
        Mockito.when(commentRepository.findById(any())).thenReturn(Optional.empty());

        //then
        assertThrows(
            EntityNotFoundException.class,
            () -> commentService.updateComment(commentId, newContent),
            "Nie istnieje komentarz o takim id"
        );

        Mockito.verify(commentRepository).findById(commentId);
    }

    @Test
    void shouldNotUpdateCommentWhenLoggedUserIsNotAnAuthor() {

        //given
        UUID commentId = UUID.randomUUID();
        String newContent = "Dobre zdjęcie";

        String authorAccountId = "A";

        UserEntity author = UserEntity.builder()
            .accountId(authorAccountId)
            .build();

        CommentEntity comment = CommentEntity.builder()
            .author(author)
            .build();

        //when
        Mockito.when(commentRepository.findById(any())).thenReturn(Optional.of(comment));
        Mockito.doThrow(UserIsNotResourceAuthorException.class).when(authService).checkLoggedUserResourceAuthorship(any());

        //then
        assertThrows(
            UserIsNotResourceAuthorException.class,
            () -> commentService.updateComment(commentId, newContent)
        );

        Mockito.verify(commentRepository).findById(commentId);
        Mockito.verify(authService).checkLoggedUserResourceAuthorship(authorAccountId);
    }

    @Test
    void shouldDeleteCommentWhenParentCommentIdIsNull() {

        //given
        UUID commentId = UUID.randomUUID();

        UserEntity author = UserEntity.builder()
            .comments(new HashSet<>())
            .build();

        PostEntity post = PostEntity.builder()
            .commentsCount(1)
            .comments(new HashSet<>())
            .build();

        CommentEntity comment = CommentEntity.builder()
            .author(author)
            .post(post)
            .build();

        author.getComments().add(comment);
        post.getComments().add(comment);

        //when
        Mockito.when(commentRepository.findById(any())).thenReturn(Optional.of(comment));
        Mockito.doNothing().when(authService).checkLoggedUserResourceAuthorship(any(), any());

        commentService.deleteComment(commentId);

        //then
        assertEquals(0, author.getComments().size());
        assertEquals(0, post.getCommentsCount());
        assertEquals(0, post.getComments().size());

        Mockito.verify(commentRepository).findById(commentId);
        Mockito.verify(authService).checkLoggedUserResourceAuthorship(eq(commentId), any());
        Mockito.verify(commentRepository).deleteById(commentId);
    }

    @Test
    void shouldDeleteCommentWhenParentCommentIdIsNotNull() {

        //given
        UUID commentId = UUID.randomUUID();

        UserEntity author = UserEntity.builder()
            .comments(new HashSet<>())
            .build();

        PostEntity post = PostEntity.builder()
            .commentsCount(2)
            .comments(new HashSet<>())
            .build();

        CommentEntity parentComment = CommentEntity.builder()
            .subCommentsCount(1)
            .subComments(new HashSet<>())
            .build();

        CommentEntity comment = CommentEntity.builder()
            .author(author)
            .post(post)
            .parentComment(parentComment)
            .build();

        author.getComments().add(parentComment);
        author.getComments().add(comment);
        post.getComments().add(parentComment);
        post.getComments().add(comment);
        parentComment.getSubComments().add(comment);

        //when
        Mockito.when(commentRepository.findById(any())).thenReturn(Optional.of(comment));
        Mockito.doNothing().when(authService).checkLoggedUserResourceAuthorship(any(), any());

        commentService.deleteComment(commentId);

        //then
        assertEquals(1, author.getComments().size());
        assertEquals(1, post.getCommentsCount());
        assertEquals(1, post.getComments().size());

        assertEquals(0, parentComment.getSubCommentsCount());
        assertEquals(0, parentComment.getSubComments().size());

        Mockito.verify(commentRepository).findById(commentId);
        Mockito.verify(authService).checkLoggedUserResourceAuthorship(eq(commentId), any());
        Mockito.verify(commentRepository).deleteById(commentId);
    }

    @Test
    void shouldNotDeleteCommentWhenCommentWasNotFound() {

        //given
        UUID commentId = UUID.randomUUID();

        //when
        Mockito.when(commentRepository.findById(any())).thenReturn(Optional.empty());

        //then
        assertThrows(
            EntityNotFoundException.class,
            () -> commentService.deleteComment(commentId),
            "Nie istnieje komentarz o takim id"
        );

        Mockito.verify(commentRepository).findById(commentId);
    }

    @Test
    void shouldNotDeleteCommentWhenLoggedUserIsNotAnAuthor() {

        //given
        UUID commentId = UUID.randomUUID();

        UserEntity author = UserEntity.builder()
            .comments(new HashSet<>())
            .build();

        PostEntity post = PostEntity.builder()
            .commentsCount(2)
            .comments(new HashSet<>())
            .build();

        CommentEntity comment = CommentEntity.builder()
            .author(author)
            .post(post)
            .build();

        //when
        Mockito.when(commentRepository.findById(any())).thenReturn(Optional.of(comment));
        Mockito.doThrow(UserIsNotResourceAuthorException.class).when(authService).checkLoggedUserResourceAuthorship(any(), any());

        //then
        assertThrows(
            UserIsNotResourceAuthorException.class,
            () -> commentService.deleteComment(commentId)
        );

        Mockito.verify(commentRepository).findById(commentId);
        Mockito.verify(authService).checkLoggedUserResourceAuthorship(eq(commentId), any());
    }
}