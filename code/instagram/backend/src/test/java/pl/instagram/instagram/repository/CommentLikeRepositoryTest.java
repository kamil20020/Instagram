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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.instagram.instagram.model.entity.CommentEntity;
import pl.instagram.instagram.model.entity.CommentLikeEntity;
import pl.instagram.instagram.model.entity.PostEntity;
import pl.instagram.instagram.model.entity.UserEntity;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.UUID;

import static org.junit.Assert.*;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CommentLikeRepositoryTest {

    @Container
    @ServiceConnection
    private static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:13.0");

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentLikeRepository commentLikeRepository;

    @BeforeEach
    private void setUp(){

        userRepository.deleteAll();
        postRepository.deleteAll();
        commentRepository.deleteAll();
        commentLikeRepository.deleteAll();
    }

    @Test
    public void shouldPositivelyCheckIfExistsByAuthorAccountIdAndCommentId(){

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
            .likedComments(new HashSet<>())
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

        CommentEntity comment = CommentEntity.builder()
            .content("Dobre zdjęcie")
            .creationDatetime(LocalDateTime.now())
            .likesCount(1)
            .subCommentsCount(0)
            .post(post)
            .author(author)
            .build();

        comment = commentRepository.save(comment);

        CommentLikeEntity.CommentLikeEntityId commentLikeEntityId = new CommentLikeEntity.CommentLikeEntityId(author.getId(), comment.getId());

        CommentLikeEntity commentLike = CommentLikeEntity.builder()
            .id(commentLikeEntityId)
            .author(author)
            .comment(comment)
            .build();

        commentLike = commentLikeRepository.save(commentLike);

        //when
        boolean actual = commentLikeRepository.existsByAuthorAccountIdAndCommentId(accountId, comment.getId());

        //then
        assertTrue(actual);
    }

    @ParameterizedTest
    @CsvSource(value = {
        "A, e2828a12-1e87-4eaf-b943-056d9ee9894b",
        "B, null",
        "B, e2828a12-1e87-4eaf-b943-056d9ee9894b"
    })
    public void shouldNegativelyCheckIfExistsByAccountIdAndCommentId(String accountId, String commentIdStr){

        //given
        UUID commentId;

        UserEntity author = UserEntity.builder()
            .accountId("A")
            .creationDatetime(LocalDateTime.now())
            .isPrivate(true)
            .isVerified(false)
            .followers(0)
            .followings(0)
            .numberOfPosts(1)
            .posts(new HashSet<>())
            .comments(new HashSet<>())
            .likedComments(new HashSet<>())
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

        CommentEntity comment = CommentEntity.builder()
            .content("Dobre zdjęcie")
            .creationDatetime(LocalDateTime.now())
            .likesCount(1)
            .subCommentsCount(0)
            .post(post)
            .author(author)
            .build();

        comment = commentRepository.save(comment);


        if(!commentIdStr.equals("null")){
            commentId = UUID.fromString(commentIdStr);
        }
        else{
            commentId = comment.getId();
        }

        CommentLikeEntity.CommentLikeEntityId commentLikeEntityId = new CommentLikeEntity.CommentLikeEntityId(author.getId(), comment.getId());

        CommentLikeEntity commentLike = CommentLikeEntity.builder()
            .id(commentLikeEntityId)
            .author(author)
            .comment(comment)
            .build();

        commentLike = commentLikeRepository.save(commentLike);

        //when
        boolean actual = commentLikeRepository.existsByAuthorAccountIdAndCommentId(accountId, commentId);

        //then
        assertFalse(actual);
    }

    @Test
    public void shouldFindByCommentId(){

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
            .likedComments(new HashSet<>())
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

        CommentEntity comment = CommentEntity.builder()
            .content("Dobre zdjęcie")
            .creationDatetime(LocalDateTime.now())
            .likesCount(1)
            .subCommentsCount(0)
            .post(post)
            .author(author)
            .build();

        comment = commentRepository.save(comment);

        CommentLikeEntity.CommentLikeEntityId commentLikeEntityId = new CommentLikeEntity.CommentLikeEntityId(author.getId(), comment.getId());

        CommentLikeEntity commentLike = CommentLikeEntity.builder()
            .id(commentLikeEntityId)
            .author(author)
            .comment(comment)
            .build();

        commentLike = commentLikeRepository.save(commentLike);

        Pageable pageable = PageRequest.of(0, 5);

        //when
        Page<CommentLikeEntity> gotLikes = commentLikeRepository.findByCommentId(comment.getId(), pageable);

        //then
        assertEquals(1, gotLikes.getTotalElements());
        assertEquals(commentLike, gotLikes.getContent().get(0));
    }
}
