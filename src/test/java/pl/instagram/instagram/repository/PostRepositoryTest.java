package pl.instagram.instagram.repository;

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
import pl.instagram.instagram.model.entity.PostEntity;
import pl.instagram.instagram.model.entity.UserEntity;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PostRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:13.0");

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldPositivelyCheckIfPostExistsByPostIdAndAuthorAccountId() {

        //given
        String accountId = "A";

        UserEntity author = UserEntity.builder()
            .accountId(accountId)
            .creationDatetime(LocalDateTime.now())
            .isVerified(true)
            .isPrivate(false)
            .followers(1)
            .followings(2)
            .numberOfPosts(1)
            .posts(new HashSet<>())
            .build();

        author = userRepository.save(author);

        PostEntity post = PostEntity.builder()
            .creationDatetime(LocalDateTime.now())
            .description("Opis posta")
            .content(("Zawartosc posta").getBytes(StandardCharsets.UTF_8))
            .areHiddenLikes(true)
            .areDisabledComments(false)
            .likesCount(1)
            .commentsCount(2)
            .author(author)
            .build();

        post = postRepository.save(post);

        author.getPosts().add(post);

        //then
        assertTrue(postRepository.existsByIdAndAuthorAccountId(post.getId(), accountId));
    }

    @ParameterizedTest
    @CsvSource(value = {
        "A, 02e97563-ec39-4c6a-88b6-73a53d73b05f",
        "B, null",
        "B, 02e97563-ec39-4c6a-88b6-73a53d73b05f"
    })
    void shouldNegativelyCheckIfPostExistsByPostIdAndAuthorAccountId(String expectedAccountId, String expectedPostIdStr) {

        String accountId = "A";

        UserEntity author = UserEntity.builder()
            .accountId(accountId)
            .creationDatetime(LocalDateTime.now())
            .isVerified(true)
            .isPrivate(false)
            .followers(1)
            .followings(2)
            .numberOfPosts(1)
            .posts(new HashSet<>())
            .build();

        author = userRepository.save(author);

        PostEntity post = PostEntity.builder()
            .creationDatetime(LocalDateTime.now())
            .description("Opis posta")
            .content(("Zawartosc posta").getBytes(StandardCharsets.UTF_8))
            .areHiddenLikes(true)
            .areDisabledComments(false)
            .likesCount(1)
            .commentsCount(2)
            .author(author)
            .build();

        post = postRepository.save(post);

        author.getPosts().add(post);

        UUID expectedPostId;

        if(expectedPostIdStr.equals("null")){
            expectedPostId = UUID.randomUUID();
        }
        else{
            expectedPostId = UUID.fromString(expectedPostIdStr);
        }

        assertFalse(postRepository.existsByIdAndAuthorAccountId(expectedPostId, expectedAccountId));
    }

    @Test
    void shouldFindAllByAuthorId() {

        //given
        UserEntity author = UserEntity.builder()
            .accountId("A")
            .creationDatetime(LocalDateTime.now())
            .isVerified(true)
            .isPrivate(false)
            .followers(1)
            .followings(2)
            .numberOfPosts(2)
            .posts(new HashSet<>())
            .build();

        author = userRepository.save(author);

        PostEntity post1 = PostEntity.builder()
            .creationDatetime(LocalDateTime.now())
            .description("Opis posta")
            .content(("Zawartosc posta").getBytes(StandardCharsets.UTF_8))
            .areHiddenLikes(true)
            .areDisabledComments(false)
            .likesCount(1)
            .commentsCount(2)
            .author(author)
            .build();

        PostEntity post2 = PostEntity.builder()
            .creationDatetime(LocalDateTime.now())
            .description("Opis posta1")
            .content(("Zawartosc posta1").getBytes(StandardCharsets.UTF_8))
            .areHiddenLikes(false)
            .areDisabledComments(true)
            .likesCount(2)
            .commentsCount(1)
            .author(author)
            .build();

        List<PostEntity> createdPosts = postRepository.saveAll(
            List.of(post1, post2)
        );

        post1 = createdPosts.get(0);
        post2 = createdPosts.get(1);

        author.getPosts().add(post1);
        author.getPosts().add(post2);

        Pageable pageable = PageRequest.of(0, 5);

        //when
        Page<PostEntity> authorPostsPage = postRepository.findAllByAuthorId(author.getId(), pageable);
        List<PostEntity> gotPosts = authorPostsPage.getContent();

        //then
        assertEquals(createdPosts.size(), authorPostsPage.getTotalElements());

        PostEntity finalPost1 = post1;
        PostEntity finalPost2 = post2;

        assertEquals(createdPosts.size(), gotPosts.stream()
            .filter(post -> post.getId().equals(finalPost1.getId()) || post.getId().equals(finalPost2.getId()))
            .count()
        );
    }
}