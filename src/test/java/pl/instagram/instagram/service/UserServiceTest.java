package pl.instagram.instagram.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import pl.instagram.instagram.exception.EntityNotFoundException;
import pl.instagram.instagram.model.entity.UserEntity;
import pl.instagram.instagram.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(value = MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Test
    void shouldCheckIfExistsById(){

        //given
        UUID userId = UUID.randomUUID();

        //when
        Mockito.when(userRepository.existsById(userId)).thenReturn(true);
        boolean actual = userService.existsById(userId);

        //then
        assertTrue(actual);

        ArgumentCaptor<UUID> userIdArgumentCaptor = ArgumentCaptor.forClass(UUID.class);

        Mockito.verify(userRepository)
            .existsById(userIdArgumentCaptor.capture());
    }

    @Test
    void shouldGetUserByIdWhenUserExists() {

        //given
        UUID userId = UUID.randomUUID();

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

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(u1));

        //when
        UserEntity actual = userService.getUserById(userId);

        //then
        assertEquals(u1, actual);

        ArgumentCaptor<UUID> userIdArgumentCaptor = ArgumentCaptor.forClass(UUID.class);

        Mockito.verify(userRepository).findById(userIdArgumentCaptor.capture());

        UUID capturedUserId = userIdArgumentCaptor.getValue();
        assertEquals(userId, capturedUserId);
    }

    @Test
    void shouldNotGetUserByIdWhenUserDoesNotExist() {

        //given
        UUID userId = UUID.randomUUID();

        //when
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        //then
        assertThrows(
            EntityNotFoundException.class,
            () -> userService.getUserById(userId),
            "Nie istnieje użytkownik o takim id konta"
        );

        ArgumentCaptor<UUID> userIdArgumentCaptor = ArgumentCaptor.forClass(UUID.class);

        Mockito.verify(userRepository).findById(userIdArgumentCaptor.capture());

        UUID capturedUserId = userIdArgumentCaptor.getValue();

        assertThat(capturedUserId).isEqualTo(userId);
    }

    @Test
    void shouldGetUserByUserAccountIdWhenUserExists() {

        //given
        String accountId = "A";

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

        //when
        Mockito.when(userRepository.findByAccountId(accountId)).thenReturn(Optional.of(u1));

        UserEntity gotUser = userService.getUserByUserAccountId(accountId);

        //given
        assertThat(gotUser).isEqualTo(u1);

        ArgumentCaptor<String> userAccountIdCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(userRepository).findByAccountId(userAccountIdCaptor.capture());

        String capturedAccountId = userAccountIdCaptor.getValue();

        assertThat(capturedAccountId).isEqualTo(accountId);
    }

    @Test
    void shouldNotGetUserByUserAccountIdWhenUserDoesNotExist() {

        //given
        String accountId = "B";

        //when
        Mockito.when(userRepository.findByAccountId(accountId)).thenReturn(Optional.empty());

        //then
        assertThrows(
            EntityNotFoundException.class,
            () -> userService.getUserByUserAccountId(accountId),
            "Nie istnieje użytkownik o takim id konta"
        );

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(userRepository).findByAccountId(captor.capture());

        String gotAccountId = captor.getValue();
        assertThat(gotAccountId).isEqualTo(accountId);
    }

    @Test
    void shouldGetUsersByIdsWhenValidIds() {

        //given
        List<UserEntity> users = List.of(
            UserEntity.builder().nickname("marcin").build(),
            UserEntity.builder().nickname("kamil").build(),
            UserEntity.builder().nickname("adam").build()
        );

        List<UUID> usersIds = List.of(UUID.randomUUID(), UUID.randomUUID());

        //when
        Mockito.when(userRepository.findAllById(usersIds)).thenReturn(users.subList(0, 2));

        List<UserEntity> foundUsers = userService.getUsersByIds(usersIds);

        //then
        assertEquals(foundUsers.size(), 2);
        assertEquals(foundUsers.get(0).getNickname(), users.get(0).getNickname());
        assertEquals(foundUsers.get(1).getNickname(), users.get(1).getNickname());

        ArgumentCaptor<List<UUID>> captor = ArgumentCaptor.forClass(List.class);

        Mockito.verify(userRepository).findAllById(captor.capture());

        List<UUID> capturedIds = captor.getValue();

        assertEquals(usersIds, capturedIds);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldNotGetUsersByIdsWhenInvalidIds(List<UUID> ids) {

        //given

        //when

        //then
        assertThatThrownBy(() -> userService.getUsersByIds(ids))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Nie podano id użytkowników");
    }

    @ParameterizedTest
    @CsvSource(value = {
        "kamil kowalski, 1",
        " kamil         kowalski , 1",
    })
    void shouldSearchUsersWithFirstnameAndSurnamePhrase(String phrase, int usersCount) {

        //given

        List<UserEntity> users = List.of(
            UserEntity.builder().firstname("marcin").surname("nowak").build(),
            UserEntity.builder().firstname("kamil").surname("kowalski").build()
        );

        Pageable pageable = PageRequest.of(0, 5);

        //when
        Mockito.when(userRepository.searchByFirstnameAndSurname(phrase, pageable)).thenReturn(new PageImpl<>(users.subList(0, 1)));

        Page<UserEntity> gotUsersPage = userService.searchUsers(phrase, pageable);

        //then
        assertEquals(usersCount, gotUsersPage.getTotalElements());

        if(usersCount == 1){

            UserEntity gotUser = gotUsersPage.getContent().get(0);

            assertThat(gotUser).isEqualTo(users.get(0));
        }

        ArgumentCaptor<String> phraseCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        Mockito.verify(userRepository).searchByFirstnameAndSurname(phraseCaptor.capture(), pageableCaptor.capture());

        String capturedPhrase = phraseCaptor.getValue();
        Pageable gotPageable = pageableCaptor.getValue();

        assertEquals(phrase, capturedPhrase);
        assertEquals(pageable, gotPageable);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "kamil, 1",
            " kamil         kowalski, 1",
    })
    void shouldSearchUsersWithFirstnameOrSurnameOrNicknamePhrase(String phrase, int usersCount) {


    }

    @Test
    void shouldNotSearchUsersWithNotGivenPageable() {

        //given

        //when

        //then
        assertThrows(
            IllegalArgumentException.class,
            () -> userService.searchUsers("kamil", null),
            "Paginacja jest wymagana"
        );
    }

    @Test
    void createUser() {
    }

    @Test
    void patchUser() {
    }

    @Test
    void fillPersonalData() {
    }
}