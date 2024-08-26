package pl.instagram.instagram.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import pl.instagram.instagram.exception.EntityNotFoundException;
import pl.instagram.instagram.exception.UserIsNotResourceAuthorException;
import pl.instagram.instagram.model.domain.PatchPostData;
import pl.instagram.instagram.model.entity.CommentEntity;
import pl.instagram.instagram.model.entity.PostEntity;
import pl.instagram.instagram.model.entity.UserEntity;
import pl.instagram.instagram.repository.PostRepository;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserService userService;

    @Mock
    private AuthService authService;

    @Test
    void shouldPositivelyCheckIfExistsById() {

        //given
        UUID postId = UUID.randomUUID();
        PostEntity postEntity = new PostEntity();
        postEntity.setId(postId);

        //when
        Mockito.when(postRepository.existsById(any())).thenReturn(true);

        boolean actual = postService.existsById(postId);

        //then
        assertTrue(actual);

        Mockito.verify(postRepository).existsById(postId);
    }

    @Test
    void shouldNegativelyCheckIfExistsById() {

        //given
        UUID postId = UUID.randomUUID();

        //when
        Mockito.when(postRepository.existsById(any())).thenReturn(false);

        boolean actual = postService.existsById(postId);

        //then
        assertFalse(actual);

        Mockito.verify(postRepository).existsById(postId);
    }

    @Test
    void shouldGetPostById() {

        //given
        UUID postId = UUID.randomUUID();

        PostEntity post = PostEntity.builder()
            .creationDatetime(LocalDateTime.now())
            .description("Opis postu")
            .content(("Zawartosc postu").getBytes(StandardCharsets.UTF_8))
            .build();

        post.setId(postId);

        //when
        Mockito.when(postRepository.findById(postId)).thenReturn(
            Optional.of(post)
        );

        PostEntity gotPost = postService.getPostById(postId);

        //then
        assertEquals(post, gotPost);

        Mockito.verify(postRepository).findById(postId);
    }

    @Test
    void shouldNotGetPostByIdWhenPostDoesNotExist() {

        //given
        UUID postId = UUID.randomUUID();

        //when
        Mockito.when(postRepository.findById(postId)).thenThrow(new EntityNotFoundException("Nie istnieje post o takim id"));

        //then
        assertThrows(
            EntityNotFoundException.class,
            () -> postService.getPostById(postId),
            "Nie istnieje post o takim id"
        );

        Mockito.verify(postRepository).findById(postId);
    }

    @Test
    void shouldGetUserPostsPage() {

        //given
        UUID authorId = UUID.randomUUID();

        PostEntity p1 = PostEntity.builder()
            .creationDatetime(LocalDateTime.now())
            .content(("Zawartosc postu").getBytes(StandardCharsets.UTF_8))
            .description("Opis postu 1")
            .build();

        PostEntity p2 = PostEntity.builder()
            .creationDatetime(LocalDateTime.now())
            .content(("Zawartosc postu").getBytes(StandardCharsets.UTF_8))
            .description("Opis postu 1")
            .build();

        Page<PostEntity> postsPage = new PageImpl<>(List.of(p1, p2));

        Pageable pageable = PageRequest.of(0, 5);

        //when
        Mockito.when(userService.existsById(any())).thenReturn(true);
        Mockito.when(postRepository.findAllByAuthorId(any(), any())).thenReturn(postsPage);

        Page<PostEntity> gotPostsPage = postService.getUserPostsPage(authorId, pageable);
        List<PostEntity> gotPosts = gotPostsPage.getContent();

        //then
        assertEquals(postsPage.getTotalElements(), gotPostsPage.getTotalElements());
        assertEquals(p1.getId(), gotPosts.get(0).getId());
        assertEquals(p2.getId(), gotPosts.get(1).getId());

        Mockito.verify(userService).existsById(authorId);
        Mockito.verify(postRepository).findAllByAuthorId(eq(authorId), any());
    }

    @Test
    void shouldNotGetUserPostsPageWithoutPagination() {

        //given
        UUID authorId = UUID.randomUUID();

        //when
        //then
        assertThrows(
            IllegalArgumentException.class,
            () -> postService.getUserPostsPage(authorId, null),
            "Paginacja jest wymagana"
        );
    }

    @Test
    void shouldNotGetUserPostsPageWhenUserDoesNotExist() {

        //given
        UUID authorId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 5);

        //when
        Mockito.when(userService.existsById(authorId)).thenReturn(false);

        //then
        assertThrows(
            EntityNotFoundException.class,
            () -> postService.getUserPostsPage(authorId, pageable),
            "Nie istnieje użytkownik o takim id"
        );

        Mockito.verify(userService).existsById(authorId);
    }

    @Test
    void shouldCreatePost() {

        //then
        PostEntity post = PostEntity.builder()
            .content(("Zawartosc postu").getBytes(StandardCharsets.UTF_8))
            .description("Opis postu 1")
            .areHiddenLikes(true)
            .areDisabledComments(false)
            .build();

        UserEntity loggedUser = UserEntity.builder()
            .posts(new HashSet<>())
            .numberOfPosts(0)
            .build();

        //when
        Mockito.when(userService.getLoggedUser()).thenReturn(loggedUser);

        postService.createPost(post);

        //then
        Mockito.verify(userService).getLoggedUser();

        ArgumentCaptor<PostEntity> postCaptor = ArgumentCaptor.forClass(PostEntity.class);

        Mockito.verify(postRepository).save(postCaptor.capture());

        PostEntity gotPost = postCaptor.getValue();

        assertEquals(post.getContent(), gotPost.getContent());
        assertEquals(post.getDescription(), gotPost.getDescription());
        assertEquals(post.isAreHiddenLikes(), gotPost.isAreHiddenLikes());
        assertEquals(post.isAreDisabledComments(), gotPost.isAreDisabledComments());
        assertNotNull(gotPost.getCreationDatetime());
        assertNotNull(gotPost.getAuthor());

        assertEquals(1, loggedUser.getPosts().size());
        assertEquals(1, loggedUser.getNumberOfPosts());
    }

    @Test
    void shouldNotCreatePostWithUnloggedUser() {

        //given

        //when
        Mockito.when(userService.getLoggedUser()).thenThrow(new EntityNotFoundException("Nie istnieje użytkownik o takim id"));

        //then
        assertThrows(
            EntityNotFoundException.class,
            () -> userService.getLoggedUser(),
            "Nie istnieje użytkownik o takim id"
        );

        Mockito.verify(userService).getLoggedUser();
    }

    @Test
    void shouldPatchPostById() {

        //given
        UserEntity author = UserEntity.builder()
            .accountId("A")
            .build();

        UUID postId = UUID.randomUUID();

        PostEntity post = PostEntity.builder()
            .description("Opis postu")
            .areDisabledComments(true)
            .areHiddenLikes(false)
            .author(author)
            .build();

        PatchPostData updatePost = new PatchPostData(
            "Opis postu1",
            false,
            true
        );

        //when
        Mockito.when(postRepository.findById(any())).thenReturn(
            Optional.of(post)
        );

        PostEntity gotPost = postService.patchPostById(postId, updatePost);

        //then
        assertEquals(updatePost.description(), gotPost.getDescription());
        assertEquals(updatePost.areDisabledComments(), gotPost.isAreDisabledComments());
        assertEquals(updatePost.areHiddenLikes(), gotPost.isAreHiddenLikes());

        Mockito.verify(postRepository).findById(postId);
        Mockito.verify(authService).checkLoggedUserResourceAuthorship(any());
    }

    @Test
    void shouldPatchPostByIdAndSkipWhenFieldsAreNull() {

        //given
        UserEntity author = UserEntity.builder()
            .accountId("A")
            .build();

        UUID postId = UUID.randomUUID();

        PostEntity post = PostEntity.builder()
            .description("Opis postu")
            .areDisabledComments(true)
            .areHiddenLikes(false)
            .author(author)
            .build();

        PatchPostData updatePost = new PatchPostData(
            "Opis postu1",
            false,
            true
        );

        ///when
        Mockito.when(postRepository.findById(any())).thenReturn(
            Optional.of(post)
        );

        PostEntity gotPost = postService.patchPostById(postId, updatePost);

        //then
        Mockito.verify(postRepository).findById(postId);

        assertEquals(post.getDescription(), gotPost.getDescription());
        assertEquals(post.isAreHiddenLikes(), gotPost.isAreHiddenLikes());
        assertEquals(post.isAreDisabledComments(), gotPost.isAreDisabledComments());

        Mockito.verify(postRepository).findById(postId);
    }

    @Test
    void shouldNotPatchPostByIdAndSkipWhenPostWasNotFound() {

        //given
        UUID postId = UUID.randomUUID();

        PatchPostData updatePost = new PatchPostData(
            "Opis postu1",
            false,
            true
        );

        //when
        Mockito.when(postRepository.findById(any())).thenReturn(Optional.empty());

        //then
        assertThrows(
            EntityNotFoundException.class,
            () -> postService.patchPostById(postId, updatePost)
        );

        Mockito.verify(postRepository).findById(postId);
    }

    @Test
    void shouldNotPatchPostByIdAndSkipWhenUserIsNotPostsAuthor() {

        //given
        UserEntity author = UserEntity.builder()
            .accountId("A")
            .build();

        UUID postId = UUID.randomUUID();

        PostEntity post = PostEntity.builder()
            .author(author)
            .build();

        PatchPostData updatePost = new PatchPostData(
            "Opis postu1",
            false,
            true
        );

        //when
        Mockito.when(postRepository.findById(any())).thenReturn(
            Optional.of(post)
        );
        Mockito.doThrow(UserIsNotResourceAuthorException.class).when(authService).checkLoggedUserResourceAuthorship(any());

        //then
        assertThrows(
            UserIsNotResourceAuthorException.class,
            () -> postService.patchPostById(postId, updatePost)
        );

        Mockito.verify(postRepository).findById(postId);
        Mockito.verify(authService).checkLoggedUserResourceAuthorship(any());
    }

    @Test
    void shouldDeletePostById() {

        //given
        UUID postId = UUID.randomUUID();

        UserEntity author = UserEntity.builder()
            .posts(new HashSet<>())
            .numberOfPosts(1)
            .build();

        PostEntity post = PostEntity.builder()
            .author(author)
            .build();

        author.getPosts().add(post);

        //when
        Mockito.when(postRepository.findById(any())).thenReturn(Optional.of(post));

        postService.deletePostById(postId);

        //then
        assertEquals(0, author.getNumberOfPosts());
        assertEquals(0, author.getPosts().size());

        Mockito.verify(postRepository).findById(postId);
        Mockito.verify(authService).checkLoggedUserResourceAuthorship(eq(postId), any());
        Mockito.verify(postRepository).deleteById(postId);
    }

    @Test
    void shouldNotDeletePostByIdWhenPostDoesNotExist() {

        //given
        UUID postId = UUID.randomUUID();

        //when
        Mockito.when(postRepository.findById(any())).thenReturn(Optional.empty());

        //then
        assertThrows(
            EntityNotFoundException.class,
            () -> postService.deletePostById(postId),
            "Nie istnieje post o takim id"
        );

        Mockito.verify(postRepository).findById(postId);
    }

    @Test
    void shouldNotDeletePostByIdWhenUserIsNotPostsAuthor() {

        //given
        UUID postId = UUID.randomUUID();

        UserEntity author = new UserEntity();

        PostEntity post = PostEntity.builder()
            .author(author)
            .build();

        //when
        Mockito.when(postRepository.findById(any())).thenReturn(Optional.of(post));
        Mockito.doThrow(UserIsNotResourceAuthorException.class).when(authService).checkLoggedUserResourceAuthorship(any(), any());

        //then
        assertThrows(
            UserIsNotResourceAuthorException.class,
            () -> postService.deletePostById(postId)
        );

        Mockito.verify(postRepository).findById(postId);
        Mockito.verify(authService).checkLoggedUserResourceAuthorship(eq(postId), any());
    }
}