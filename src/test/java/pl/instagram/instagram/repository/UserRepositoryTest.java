package pl.instagram.instagram.repository;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.ParameterizedTest;
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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:13.0");

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    @BeforeEach
    public void setUp(){
        userRepository.deleteAll();
    }

    @ParameterizedTest
    @CsvSource(value = {
        "A, true",
        "B, false"
    })
    void checkIfExistsByAccountId(String accountId, boolean expected) {

        //given
        UserEntity u1 = UserEntity.builder()
            .accountId(accountId)
            .nickname("kamil")
            .firstname("Kamil")
            .surname("Kowalski")
            .tel("+48111222333")
            .description("Opis")
            .creationDatetime(LocalDateTime.now())
            .followings(0)
            .followers(0)
            .numberOfPosts(0)
            .build();

        userRepository.save(u1);

        //when
        boolean actual = userRepository.existsByAccountId("A");

        //then
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @CsvSource(value = {
        "kamil, true",
        "Kamil, true",
        "kaMil, true",
        "kamil1, false"
    })
    void checkIfExistsByNickname(String nickname, boolean expected) {

        //given
        UserEntity u1 = UserEntity.builder()
            .accountId("A")
            .nickname("kamil")
            .firstname("Kamil")
            .surname("Kowalski")
            .tel("+48111222333")
            .description("Opis")
            .creationDatetime(LocalDateTime.now())
            .followings(0)
            .followers(0)
            .numberOfPosts(0)
            .build();

        userRepository.save(u1);

        //when
        boolean actual = userRepository.existsByNicknameContainingIgnoreCase(nickname);

        //then
        assertEquals(expected, actual);
    }

    @Test
    void checkIfExistsByIdAndLikedPostsIdWhenBothExist() {

        //given
        UserEntity u1 = UserEntity.builder()
            .accountId("A")
            .nickname("kamil")
            .firstname("Kamil")
            .surname("Kowalski")
            .tel("+48111222333")
            .description("Opis")
            .creationDatetime(LocalDateTime.now())
            .followings(0)
            .followers(0)
            .numberOfPosts(0)
            .posts(new HashSet<>())
            .likedPosts(new HashSet<>())
            .build();

        u1 = userRepository.save(u1);

        PostEntity p1 = PostEntity.builder()
            .creationDatetime(LocalDateTime.now())
            .description("Opis")
            .content(("Zawartość").getBytes(StandardCharsets.UTF_8))
            .likesCount(1)
            .commentsCount(0)
            .author(u1)
            .build();

        p1 = postRepository.save(p1);

        u1.getPosts().add(p1);
        u1.getLikedPosts().add(p1);

        //when
        boolean actual = userRepository.existsByIdAndLikedPostsId(u1.getId(), p1.getId());

        //then
        assertThat(actual).isTrue();
    }

    @Test
    void checkIfExistsByIdAndLikedPostsIdWhenUserDoesNotExist() {

        //given
        UserEntity u1 = UserEntity.builder()
                .accountId("A")
                .nickname("kamil")
                .firstname("Kamil")
                .surname("Kowalski")
                .tel("+48111222333")
                .description("Opis")
                .creationDatetime(LocalDateTime.now())
                .followings(0)
                .followers(0)
                .numberOfPosts(0)
                .posts(new HashSet<>())
                .likedPosts(new HashSet<>())
                .build();

        u1 = userRepository.save(u1);

        PostEntity p1 = PostEntity.builder()
                .creationDatetime(LocalDateTime.now())
                .description("Opis")
                .content(("Zawartość").getBytes(StandardCharsets.UTF_8))
                .likesCount(1)
                .commentsCount(0)
                .author(u1)
                .build();

        p1 = postRepository.save(p1);

        u1.getPosts().add(p1);
        u1.getLikedPosts().add(p1);

        //when
        boolean actual = userRepository.existsByIdAndLikedPostsId(UUID.randomUUID(), p1.getId());

        //then
        assertThat(actual).isFalse();
    }

    @Test
    void checkIfExistsByIdAndLikedPostsIdWhePostDoesNotExist() {

        //given
        UserEntity u1 = UserEntity.builder()
                .accountId("A")
                .nickname("kamil")
                .firstname("Kamil")
                .surname("Kowalski")
                .tel("+48111222333")
                .description("Opis")
                .creationDatetime(LocalDateTime.now())
                .followings(0)
                .followers(0)
                .numberOfPosts(0)
                .posts(new HashSet<>())
                .likedPosts(new HashSet<>())
                .build();

        u1 = userRepository.save(u1);

        PostEntity p1 = PostEntity.builder()
                .creationDatetime(LocalDateTime.now())
                .description("Opis")
                .content(("Zawartość").getBytes(StandardCharsets.UTF_8))
                .likesCount(1)
                .commentsCount(0)
                .author(u1)
                .build();

        p1 = postRepository.save(p1);

        u1.getPosts().add(p1);
        u1.getLikedPosts().add(p1);

        //when
        boolean actual = userRepository.existsByIdAndLikedPostsId(u1.getId(),UUID.randomUUID());

        //then
        assertThat(actual).isFalse();
    }

    @Test
    void shouldFindByAccountIdWhenExists() {

        //given
        UserEntity u1 = UserEntity.builder()
            .accountId("A")
            .nickname("kamil")
            .firstname("Kamil")
            .surname("Kowalski")
            .tel("+48111222333")
            .description("Opis")
            .creationDatetime(LocalDateTime.now())
            .followings(0)
            .followers(0)
            .numberOfPosts(0)
            .build();

        u1 = userRepository.save(u1);

        //when
        Optional<UserEntity> foundUserOpt = userRepository.findByAccountId("A");

        //then
        assertTrue(foundUserOpt.isPresent());
        assertEquals(foundUserOpt.get(), u1);
    }

    @Test
    void shouldNotFindByAccountIdWhenDoesNotExist() {

        //given
        
        //when
        Optional<UserEntity> foundUserOpt = userRepository.findByAccountId("B");

        //then
        assertTrue(foundUserOpt.isEmpty());
    }

    @Test
    void shouldFindByLikedPostsIdWhenPostLikeAndPageableExist() {

        //given
        UserEntity u1 = UserEntity.builder()
            .accountId("A")
            .nickname("kamil")
            .firstname("Kamil")
            .surname("Kowalski")
            .tel("+48111222333")
            .description("Opis")
            .creationDatetime(LocalDateTime.now())
            .followings(0)
            .followers(0)
            .numberOfPosts(0)
            .posts(new HashSet<>())
            .likedPosts(new HashSet<>())
            .build();

        UserEntity u2 = UserEntity.builder()
            .accountId("B")
            .nickname("adam")
            .firstname("Adam")
            .surname("Nowak")
            .tel("+48444555666")
            .description("Opis2")
            .creationDatetime(LocalDateTime.now())
            .followings(0)
            .followers(0)
            .numberOfPosts(0)
            .posts(new HashSet<>())
            .likedPosts(new HashSet<>())
            .build();

        List<UserEntity> expectedUsers = userRepository.saveAll(List.of(u1, u2));
        u1 = expectedUsers.get(0);

        PostEntity p1 = PostEntity.builder()
            .creationDatetime(LocalDateTime.now())
            .description("Opis")
            .content(("Zawartość").getBytes(StandardCharsets.UTF_8))
            .likesCount(1)
            .commentsCount(0)
            .author(u1)
            .build();

        p1 = postRepository.save(p1);

        u1.getPosts().add(p1);
        u1.getLikedPosts().add(p1);

        Pageable pageable = PageRequest.of(0, 5);

        //when
        Page<UserEntity> foundPostLikesPage = userRepository.findByLikedPostsId(p1.getId(), pageable);
        List<UserEntity> foundPostLikes = foundPostLikesPage.getContent();

        //then
        assertEquals(foundPostLikesPage.getTotalElements(), 1);
        assertEquals(foundPostLikes.get(0), u1);
    }

    @Test
    void shouldNotFindByLikedPostsIdWhenPostLikeDoesNotExist() {

        //given
        UserEntity u1 = UserEntity.builder()
            .accountId("A")
            .nickname("kamil")
            .firstname("Kamil")
            .surname("Kowalski")
            .tel("+48111222333")
            .description("Opis")
            .creationDatetime(LocalDateTime.now())
            .followings(0)
            .followers(0)
            .numberOfPosts(0)
            .posts(new HashSet<>())
            .likedPosts(new HashSet<>())
            .build();

        UserEntity u2 = UserEntity.builder()
            .accountId("B")
            .nickname("adam")
            .firstname("Adam")
            .surname("Nowak")
            .tel("+48444555666")
            .description("Opis2")
            .creationDatetime(LocalDateTime.now())
            .followings(0)
            .followers(0)
            .numberOfPosts(0)
            .posts(new HashSet<>())
            .likedPosts(new HashSet<>())
            .build();

        userRepository.saveAll(List.of(u1, u2));

        PostEntity p1 = PostEntity.builder()
                .creationDatetime(LocalDateTime.now())
                .description("Opis")
                .content(("Zawartość").getBytes(StandardCharsets.UTF_8))
                .likesCount(1)
                .commentsCount(0)
                .author(u1)
                .build();

        p1 = postRepository.save(p1);

        Pageable pageable = PageRequest.of(0, 5);

        //when
        Page<UserEntity> foundPostLikesPage = userRepository.findByLikedPostsId(p1.getId(), pageable);

        //then
        assertEquals(foundPostLikesPage.getTotalElements(), 0);
    }

    @ParameterizedTest
    @CsvSource(value = {
        "kamil kowalski, 1",
        "kowalski    Kamil, 1",
        "kaMil         kowaLski, 1",
        " wals    mi , 1",
        "kamil kamil, 0",
        " kowalski, 0"
    })
    public void shouldSearchByFirstnameAndSurname(String phrase, int expectedCount){

        //given
        UserEntity u1 = UserEntity.builder()
            .accountId("A")
            .nickname("kamil")
            .firstname("Kamil")
            .surname("Kowalski")
            .tel("+48111222333")
            .description("Opis")
            .creationDatetime(LocalDateTime.now())
            .followings(0)
            .followers(0)
            .numberOfPosts(0)
            .posts(new HashSet<>())
            .likedPosts(new HashSet<>())
            .build();

        UserEntity u2 = UserEntity.builder()
            .accountId("B")
            .nickname("adam")
            .firstname("Adam")
            .surname("Nowak")
            .tel("+48444555666")
            .description("Opis2")
            .creationDatetime(LocalDateTime.now())
            .followings(0)
            .followers(0)
            .numberOfPosts(0)
            .posts(new HashSet<>())
            .likedPosts(new HashSet<>())
            .build();

        u1 = userRepository.saveAll(List.of(u1, u2)).get(0);

        Pageable pageable = PageRequest.of(0, 5);

        //when
        Page<UserEntity> foundUsersPage = userRepository.searchByFirstnameAndSurname(phrase, pageable);

        //then
        assertThat(foundUsersPage.getTotalElements()).isEqualTo(expectedCount);

        if(expectedCount == 1){

            UserEntity foundUser = foundUsersPage.getContent().get(0);

            assertThat(foundUser).isEqualTo(u1);
        }
    }

    @ParameterizedTest
    @CsvSource(value = {
        "kamil, 1",
        "  kAmil  , 1",
        "kam, 1",
        "kamill, 0",
        "kamil  kamil, 0"
    })
    public void shouldSearchByFirstnameOrSurnameOrNickname(String phrase, int expectedCount){

        //given
        UserEntity u1 = UserEntity.builder()
            .accountId("A")
            .nickname("kamil")
            .firstname("Kamil")
            .surname("Kowalski")
            .tel("+48111222333")
            .description("Opis")
            .creationDatetime(LocalDateTime.now())
            .followings(0)
            .followers(0)
            .numberOfPosts(0)
            .posts(new HashSet<>())
            .likedPosts(new HashSet<>())
            .build();

        UserEntity u2 = UserEntity.builder()
            .accountId("B")
            .nickname("adam")
            .firstname("Adam")
            .surname("Nowak")
            .tel("+48444555666")
            .description("Opis2")
            .creationDatetime(LocalDateTime.now())
            .followings(0)
            .followers(0)
            .numberOfPosts(0)
            .posts(new HashSet<>())
            .likedPosts(new HashSet<>())
            .build();

        u1 = userRepository.saveAll(List.of(u1, u2)).get(0);

        Pageable pageable = PageRequest.of(0, 5);

        //when
        Page<UserEntity> gotUsersPage = userRepository.searchByFirstnameOrSurnameOrNickname(phrase, pageable);

        //then
        assertThat(gotUsersPage.getTotalElements()).isEqualTo(expectedCount);

        if(expectedCount == 1){

            UserEntity gotUser = gotUsersPage.getContent().get(0);

            assertEquals(u1, gotUser);
        }
    }
}