package pl.instagram.instagram.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.instagram.instagram.model.entity.CommentEntity;
import pl.instagram.instagram.model.entity.PostEntity;
import pl.instagram.instagram.model.entity.UserEntity;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest()
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CommentRepositoryTest {

    @Container
    @ServiceConnection
    private static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:13.0");

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @BeforeEach
    private void setUp(){

        userRepository.deleteAll();
        postRepository.deleteAll();
        commentRepository.deleteAll();
    }

    @Test
    void shouldPositivelyCheckIfExistsByIdAndAuthor_AccountId() {

        //given
        String accountId = "A";

        UserEntity author = UserEntity.builder()
            .accountId(accountId)
            .creationDatetime(LocalDateTime.now())
            .isPrivate(true)
            .isVerified(false)
            .followers(0)
            .followings(0)
            .numberOfPosts(1)
            .posts(new HashSet<>())
            .comments(new HashSet<>())
            .build();

        author = userRepository.save(author);

        PostEntity post = PostEntity.builder()
            .creationDatetime(LocalDateTime.now())
            .description("Opis postu")
            .content(("Zawartość postu").getBytes(StandardCharsets.UTF_8))
            .areDisabledComments(true)
            .areHiddenLikes(false)
            .likesCount(0)
            .commentsCount(1)
            .author(author)
            .comments(new HashSet<>())
            .build();

        post = postRepository.save(post);

        author.getPosts().add(post);

        CommentEntity comment = CommentEntity.builder()
            .content("Dobre zdjęcie")
            .creationDatetime(LocalDateTime.now())
            .likesCount(0)
            .subCommentsCount(0)
            .post(post)
            .author(author)
            .build();

        comment = commentRepository.save(comment);

        post.getComments().add(comment);
        author.getComments().add(comment);

        //when
        boolean actual = commentRepository.existsByIdAndAuthor_AccountId(comment.getId(), author.getAccountId());

        //then
        assertTrue(actual);
    }

    @ParameterizedTest
    @CsvSource(value = {
        "A, 5d497f16-ed97-4c13-9f5b-5017b1e11ebf",
        "B, null",
        "B, 5d497f16-ed97-4c13-9f5b-5017b1e11ebf"
    })
    void shouldNegativelyCheckIfExistsByIdAndAuthor_AccountId(String expectedAccountId, String expectedCommentIdStr) {

        //given
        String accountId = "A";

        UserEntity author = UserEntity.builder()
            .accountId(accountId)
            .creationDatetime(LocalDateTime.now())
            .isPrivate(true)
            .isVerified(false)
            .followers(0)
            .followings(0)
            .numberOfPosts(1)
            .posts(new HashSet<>())
            .comments(new HashSet<>())
            .build();

        author = userRepository.save(author);

        PostEntity post = PostEntity.builder()
            .creationDatetime(LocalDateTime.now())
            .description("Opis postu")
            .content(("Zawartość postu").getBytes(StandardCharsets.UTF_8))
            .areDisabledComments(true)
            .areHiddenLikes(false)
            .likesCount(0)
            .commentsCount(1)
            .author(author)
            .comments(new HashSet<>())
            .build();

        post = postRepository.save(post);

        author.getPosts().add(post);

        CommentEntity comment = CommentEntity.builder()
            .content("Dobre zdjęcie")
            .creationDatetime(LocalDateTime.now())
            .likesCount(0)
            .subCommentsCount(0)
            .post(post)
            .author(author)
            .build();

        comment = commentRepository.save(comment);

        post.getComments().add(comment);
        author.getComments().add(comment);

        UUID expectedCommentId;

        if(expectedCommentIdStr.equals("null")){
            expectedCommentId = UUID.randomUUID();
        }
        else{
            expectedCommentId = UUID.fromString(expectedCommentIdStr);
        }

        //when
        boolean actual = commentRepository.existsByIdAndAuthor_AccountId(expectedCommentId, expectedAccountId);

        //then
        assertFalse(actual);
    }

    @Test
    void shouldGetAllByPostIdAndParentCommentIdWithoutParentCommentId() {

        //given
        String accountId = "A";

        UserEntity author = UserEntity.builder()
            .accountId(accountId)
            .creationDatetime(LocalDateTime.now())
            .isPrivate(true)
            .isVerified(false)
            .followers(0)
            .followings(0)
            .numberOfPosts(1)
            .posts(new HashSet<>())
            .comments(new HashSet<>())
            .build();

        author = userRepository.save(author);

        PostEntity post = PostEntity.builder()
            .creationDatetime(LocalDateTime.now())
            .description("Opis postu")
            .content(("Zawartość postu").getBytes(StandardCharsets.UTF_8))
            .areDisabledComments(true)
            .areHiddenLikes(false)
            .likesCount(0)
            .commentsCount(2)
            .author(author)
            .comments(new HashSet<>())
            .build();

        post = postRepository.save(post);

        author.getPosts().add(post);

        CommentEntity comment = CommentEntity.builder()
            .content("Dobre zdjęcie")
            .creationDatetime(LocalDateTime.now())
            .likesCount(0)
            .subCommentsCount(1)
            .post(post)
            .author(author)
            .subComments(new HashSet<>())
            .build();

        CommentEntity subcomment = CommentEntity.builder()
            .content("Zgadzam się")
            .creationDatetime(LocalDateTime.now())
            .likesCount(0)
            .subCommentsCount(0)
            .post(post)
            .author(author)
            .parentComment(comment)
            .build();

        comment = commentRepository.save(comment);
        subcomment = commentRepository.save(subcomment);

        comment.getSubComments().add(subcomment);

        post.getComments().add(comment);
        post.getComments().add(subcomment);
        author.getComments().add(comment);
        author.getComments().add(subcomment);

        Pageable pageable = PageRequest.of(0, 5);

        //when
        Page<CommentEntity> actualPage = commentRepository.getAllByPostIdAndParentCommentId(post.getId(), null, pageable);

        //then
        assertEquals(1, actualPage.getTotalElements());
        assertEquals(comment.getId(), actualPage.getContent().get(0).getId());
    }

    @Test
    void shouldGetAllByPostIdAndParentCommentIdWithParentCommentId() {

        //given
        String accountId = "A";

        UserEntity author = UserEntity.builder()
            .accountId(accountId)
            .creationDatetime(LocalDateTime.now())
            .isPrivate(true)
            .isVerified(false)
            .followers(0)
            .followings(0)
            .numberOfPosts(1)
            .posts(new HashSet<>())
            .comments(new HashSet<>())
            .build();

        author = userRepository.save(author);

        PostEntity post = PostEntity.builder()
            .creationDatetime(LocalDateTime.now())
            .description("Opis postu")
            .content(("Zawartość postu").getBytes(StandardCharsets.UTF_8))
            .areDisabledComments(true)
            .areHiddenLikes(false)
            .likesCount(0)
            .commentsCount(2)
            .author(author)
            .comments(new HashSet<>())
            .build();

        post = postRepository.save(post);

        author.getPosts().add(post);

        CommentEntity comment = CommentEntity.builder()
            .content("Dobre zdjęcie")
            .creationDatetime(LocalDateTime.now())
            .likesCount(0)
            .subCommentsCount(1)
            .post(post)
            .author(author)
            .subComments(new HashSet<>())
            .build();

        CommentEntity subcomment = CommentEntity.builder()
            .content("Zgadzam się")
            .creationDatetime(LocalDateTime.now())
            .likesCount(0)
            .subCommentsCount(0)
            .post(post)
            .author(author)
            .parentComment(comment)
            .build();

        comment = commentRepository.save(comment);
        subcomment = commentRepository.save(subcomment);

        comment.getSubComments().add(subcomment);

        post.getComments().add(comment);
        post.getComments().add(subcomment);
        author.getComments().add(comment);
        author.getComments().add(subcomment);

        Pageable pageable = PageRequest.of(0, 5);

        //when
        Page<CommentEntity> actualPage = commentRepository.getAllByPostIdAndParentCommentId(post.getId(), comment.getId(), pageable);

        //then
        assertEquals(1, actualPage.getTotalElements());
        assertEquals(subcomment.getId(), actualPage.getContent().get(0).getId());
    }

    @Test
    void shouldGetAllByPostIdAndParentCommentIdAsEmptyPage() {

        //given
        String accountId = "A";

        UserEntity author = UserEntity.builder()
            .accountId(accountId)
            .creationDatetime(LocalDateTime.now())
            .isPrivate(true)
            .isVerified(false)
            .followers(0)
            .followings(0)
            .numberOfPosts(1)
            .posts(new HashSet<>())
            .comments(new HashSet<>())
            .build();

        author = userRepository.save(author);

        PostEntity post = PostEntity.builder()
            .creationDatetime(LocalDateTime.now())
            .description("Opis postu")
            .content(("Zawartość postu").getBytes(StandardCharsets.UTF_8))
            .areDisabledComments(true)
            .areHiddenLikes(false)
            .likesCount(0)
            .commentsCount(1)
            .author(author)
            .comments(new HashSet<>())
            .build();

        post = postRepository.save(post);

        author.getPosts().add(post);

        CommentEntity comment = CommentEntity.builder()
            .content("Dobre zdjęcie")
            .creationDatetime(LocalDateTime.now())
            .likesCount(0)
            .subCommentsCount(0)
            .post(post)
            .author(author)
            .subComments(new HashSet<>())
            .build();

        comment = commentRepository.save(comment);

        post.getComments().add(comment);
        author.getComments().add(comment);

        Pageable pageable = PageRequest.of(0, 5);

        //when
        Page<CommentEntity> actualPage = commentRepository.getAllByPostIdAndParentCommentId(post.getId(), comment.getId(), pageable);

        //then
        assertEquals(0, actualPage.getTotalElements());
    }
}