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
import pl.instagram.instagram.exception.ConflictException;
import pl.instagram.instagram.exception.EntityNotFoundException;
import pl.instagram.instagram.exception.NonLoggedException;
import pl.instagram.instagram.model.entity.UserEntity;
import pl.instagram.instagram.repository.UserRepository;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;

@ExtendWith(value = MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthService authService;

    @Test
    void shouldGetLoggedUserWithExistingAccountId(){

        //when
        String loggedUserAccountId = "A";

        UserEntity loggedUser = UserEntity.builder()
            .accountId(loggedUserAccountId)
            .build();

        //given
        Mockito.when(authService.getLoggedUserAccountId()).thenReturn(loggedUserAccountId);
        Mockito.when(userRepository.findByAccountId(loggedUserAccountId)).thenReturn(Optional.of(loggedUser));

        UserEntity gotUser = userService.getLoggedUser();

        //then
        assertThat(gotUser).isEqualTo(loggedUser);

        ArgumentCaptor<String> userAccountIdCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(userRepository).findByAccountId(userAccountIdCaptor.capture());

        String capturedAccountId = userAccountIdCaptor.getValue();

        assertThat(capturedAccountId).isEqualTo(loggedUserAccountId);
    }

    @Test
    void shouldNotGetLoggedUserWithUnloggedUser(){

        //when
        //given
        Mockito.when(authService.getLoggedUserAccountId()).thenThrow(new NonLoggedException());

        //then
        assertThrows(NonLoggedException.class, () -> userService.getLoggedUser());
    }

    @Test
    void shouldNotGetLoggedUserWithNonExistingAccountId(){

        //when
        String loggedUserAccountId = "A";

        //given
        Mockito.when(authService.getLoggedUserAccountId()).thenReturn(loggedUserAccountId);
        Mockito.when(userRepository.findByAccountId(loggedUserAccountId)).thenReturn(Optional.empty());

        //then
        assertThrows(
            EntityNotFoundException.class,
            () -> userService.getLoggedUser(),
            "Nie istnieje użytkownik o takim id konta"
        );

        ArgumentCaptor<String> accountIdCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(userRepository).findByAccountId(accountIdCaptor.capture());

        String capturedAccountId = accountIdCaptor.getValue();

        assertEquals(loggedUserAccountId, capturedAccountId);
    }

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
    void shouldSearchUsersWithFirstnameAndSurnamePhrase(String phrase, int expectedUsersCount) {

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
        assertEquals(expectedUsersCount, gotUsersPage.getTotalElements());

        if(expectedUsersCount == 1){

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
            " kaMil , 1",
    })
    void shouldSearchUsersWithFirstnameOrSurnameOrNicknamePhrase(String phrase, int expectedUsersCount) {

        //given
        List<UserEntity> users = List.of(
            UserEntity.builder().firstname("marcin").surname("nowak").build(),
            UserEntity.builder().firstname("kamil").surname("kowalski").build()
        );

        Pageable pageable = PageRequest.of(0, 5);

        //when
        Mockito.when(userRepository.searchByFirstnameOrSurnameOrNickname(phrase, pageable)).thenReturn(new PageImpl<>(users.subList(0, 1)));

        Page<UserEntity> foundUsersPage = userService.searchUsers(phrase, pageable);

        //then
        assertEquals(expectedUsersCount, foundUsersPage.getTotalElements());

        ArgumentCaptor<String> phraseCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        Mockito.verify(userRepository).searchByFirstnameOrSurnameOrNickname(phraseCaptor.capture(), pageableCaptor.capture());

        String capturedPhrase = phraseCaptor.getValue();
        Pageable gotPageable = pageableCaptor.getValue();

        assertEquals(phrase, capturedPhrase);
        assertEquals(gotPageable, pageable);
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
    void shouldCreateUserWithValidData() {

        //given
        String accountId = "A";

        UserEntity u1 = UserEntity.builder()
            .accountId("A")
            .creationDatetime(LocalDateTime.now())
            .build();

        u1.setId(UUID.randomUUID());

        //when
        Mockito.when(userRepository.existsByAccountId(accountId)).thenReturn(false);
        Mockito.when(userRepository.save(any())).thenReturn(u1);

        UUID createdUserId = userService.createUser(accountId);

        //then
        assertNotNull(createdUserId);

        ArgumentCaptor<String> accountIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);

        Mockito.verify(userRepository).existsByAccountId(accountIdCaptor.capture());
        Mockito.verify(userRepository).save(userCaptor.capture());

        String capturedAccountId = accountIdCaptor.getValue();
        UserEntity capturedUser = userCaptor.getValue();

        assertEquals(accountId, capturedAccountId);
        assertEquals(u1.getAccountId(), capturedUser.getAccountId());
        assertNotNull(capturedUser.getCreationDatetime());
    }

    @Test
    void shouldNotCreateUserWithAlreadyExistingAccountId() {

        //given
        String accountId = "A";

        //when
        Mockito.when(userRepository.existsByAccountId(accountId)).thenReturn(true);

        //then
        assertThatThrownBy(() -> userService.createUser(accountId))
            .isInstanceOf(ConflictException.class)
            .hasMessage("Istnieje już użytkownik o takim id konta");

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(userRepository).existsByAccountId(captor.capture());

        String capturedAccountId = captor.getValue();

        assertThat(capturedAccountId).isEqualTo(accountId);
    }

    @Test
    void shouldPatchUserWithAllGivenData() {

        //given
        String loggedUserAccountId = "A";

        UserEntity loggedUser = UserEntity.builder()
            .nickname("kamil")
            .firstname("Kamil")
            .surname("Dywan")
            .avatar(("kamil").getBytes(StandardCharsets.UTF_8))
            .build();

        UserEntity newData = UserEntity.builder()
            .nickname("michał")
            .firstname("Michał")
            .surname("Kowalski")
            .avatar(("michał").getBytes(StandardCharsets.UTF_8))
            .build();

        //when
        Mockito.when(authService.getLoggedUserAccountId()).thenReturn(loggedUserAccountId);
        Mockito.when(userRepository.findByAccountId(loggedUserAccountId)).thenReturn(Optional.of(loggedUser));

        Mockito.when(userRepository.existsByNicknameContainingIgnoreCase(newData.getNickname())).thenReturn(false);

        UserEntity userWithPatchedData = userService.patchUser(newData);

        //then
        assertThat(userWithPatchedData).isEqualTo(newData);

        ArgumentCaptor<String> accountIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> nicknameCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(userRepository).findByAccountId(accountIdCaptor.capture());
        Mockito.verify(userRepository).existsByNicknameContainingIgnoreCase(nicknameCaptor.capture());

        String capturedAccountId = accountIdCaptor.getValue();
        String capturedNickname = nicknameCaptor.getValue();

        assertThat(capturedAccountId).isEqualTo(loggedUserAccountId);
        assertThat(capturedNickname).isEqualTo(newData.getNickname());
    }

    @Test
    void shouldPatchUserWithNoGivenData() {

        //given
        String loggedUserAccountId = "A";

        UserEntity loggedUser = UserEntity.builder()
            .nickname("kamil")
            .firstname("Kamil")
            .surname("Dywan")
            .avatar(("kamil").getBytes(StandardCharsets.UTF_8))
            .build();

        UserEntity newData = UserEntity.builder()
            .nickname(null)
            .firstname(null)
            .surname(null)
            .avatar(null)
            .build();

        //when
        Mockito.when(authService.getLoggedUserAccountId()).thenReturn(loggedUserAccountId);
        Mockito.when(userRepository.findByAccountId(loggedUserAccountId)).thenReturn(Optional.of(loggedUser));

        UserEntity userWithPatchedData = userService.patchUser(newData);

        //then
        assertThat(userWithPatchedData).isEqualTo(loggedUser);

        ArgumentCaptor<String> accountIdCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(userRepository).findByAccountId(accountIdCaptor.capture());

        String capturedAccountId = accountIdCaptor.getValue();

        assertThat(capturedAccountId).isEqualTo(loggedUserAccountId);
    }

    @Test
    void shouldNotPatchUserWithUnloggedUser() {

        //given
        UserEntity newData = UserEntity.builder()
            .nickname("michał")
            .firstname("Michał")
            .surname("Kowalski")
            .avatar(("michał").getBytes(StandardCharsets.UTF_8))
            .build();

        //when
        Mockito.when(authService.getLoggedUserAccountId()).thenThrow(new NonLoggedException());

        //then
        assertThatThrownBy(() -> userService.patchUser(newData))
            .isInstanceOf(NonLoggedException.class);
    }

    @Test
    void shouldNotPatchUserWithNotFoundUser() {

        //given
        String loggedUserAccountId = "A";

        UserEntity newData = UserEntity.builder()
            .nickname("michał")
            .firstname("Michał")
            .surname("Kowalski")
            .avatar(("michał").getBytes(StandardCharsets.UTF_8))
            .build();

        //when
        Mockito.when(authService.getLoggedUserAccountId()).thenReturn(loggedUserAccountId);
        Mockito.when(userRepository.findByAccountId(loggedUserAccountId)).thenReturn(Optional.empty());

        //then
        assertThrows(
            EntityNotFoundException.class,
            () -> userService.patchUser(newData),
            "Nie istnieje użytkownik o takim id konta"
        );

        ArgumentCaptor<String> accountIdCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(userRepository).findByAccountId(accountIdCaptor.capture());

        String capturedAccountId = accountIdCaptor.getValue();

        assertEquals(loggedUserAccountId, capturedAccountId);
    }

    @Test
    void shouldNotPatchUserWithConflictNickname() {

        //given
        String loggedUserAccountId = "A";

        UserEntity loggedUser = UserEntity.builder()
            .nickname("kamil")
            .firstname("Kamil")
            .surname("Dywan")
            .avatar(("kamil").getBytes(StandardCharsets.UTF_8))
            .build();

        UserEntity newData = UserEntity.builder()
            .nickname("michał")
            .firstname("Michał")
            .surname("Kowalski")
            .avatar(("michał").getBytes(StandardCharsets.UTF_8))
            .build();

        //when
        Mockito.when(authService.getLoggedUserAccountId()).thenReturn(loggedUserAccountId);
        Mockito.when(userRepository.findByAccountId(loggedUserAccountId)).thenReturn(Optional.of(loggedUser));

        Mockito.when(userRepository.existsByNicknameContainingIgnoreCase(newData.getNickname())).thenReturn(true);

        //then
        assertThatThrownBy(() -> userService.patchUser(newData))
            .isInstanceOf(ConflictException.class)
            .hasMessage("Istnieje już użytkownik o takim pseudonimie");

        ArgumentCaptor<String> accountIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> nicknameCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(userRepository).findByAccountId(accountIdCaptor.capture());
        Mockito.verify(userRepository).existsByNicknameContainingIgnoreCase(nicknameCaptor.capture());

        String capturedAccountId = accountIdCaptor.getValue();
        String capturedNickname = nicknameCaptor.getValue();

        assertThat(capturedAccountId).isEqualTo(loggedUserAccountId);
        assertThat(capturedNickname).isEqualTo(newData.getNickname());
    }

    @Test
    void shouldFillPersonalDataWithValidData() {

        //given
        String loggedUserAccountId = "A";

        UserEntity loggedUser = UserEntity.builder()
            .firstname("Marcin")
            .surname("Nowak")
            .nickname("marcin")
            .build();

        UserEntity newPersonalData = UserEntity.builder()
            .firstname("Kamil")
            .surname("Kowalski")
            .nickname("kamil")
            .build();

        //when
        Mockito.when(authService.getLoggedUserAccountId()).thenReturn(loggedUserAccountId);
        Mockito.when(userRepository.findByAccountId(loggedUserAccountId)).thenReturn(Optional.of(loggedUser));

        Mockito.when(userRepository.existsByNicknameContainingIgnoreCase(newPersonalData.getNickname())).thenReturn(false);

        UserEntity userWithUpdatedData = userService.fillPersonalData(newPersonalData);

        //then
        assertThat(userWithUpdatedData).isEqualTo(newPersonalData);

        ArgumentCaptor<String> userAccountIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> nicknameCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(userRepository).findByAccountId(userAccountIdCaptor.capture());
        Mockito.verify(userRepository).existsByNicknameContainingIgnoreCase(nicknameCaptor.capture());

        String capturedAccountId = userAccountIdCaptor.getValue();
        String capturedNickname = nicknameCaptor.getValue();

        assertThat(capturedAccountId).isEqualTo(loggedUserAccountId);
        assertThat(capturedNickname).isEqualTo(newPersonalData.getNickname());
    }

    @Test
    void shouldNotFillPersonalDataWithUnloggedUser() {

        //given

        UserEntity newPersonalData = UserEntity.builder()
                .firstname("Kamil")
                .surname("Kowalski")
                .nickname("kamil")
                .build();

        //when
        Mockito.when(authService.getLoggedUserAccountId()).thenThrow(new NonLoggedException());

        //then
        assertThatThrownBy(() -> userService.fillPersonalData(newPersonalData))
            .isInstanceOf(NonLoggedException.class);
    }

    @Test
    void shouldNotFillPersonalDataWithNotExistingAccountId() {

        //given
        String loggedUserAccountId = "A";

        UserEntity newPersonalData = UserEntity.builder()
                .firstname("Kamil")
                .surname("Kowalski")
                .nickname("kamil")
                .build();

        //when
        Mockito.when(authService.getLoggedUserAccountId()).thenReturn(loggedUserAccountId);
        Mockito.when(userRepository.findByAccountId(loggedUserAccountId)).thenReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> userService.fillPersonalData(newPersonalData))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessage("Nie istnieje użytkownik o takim id konta");


        ArgumentCaptor<String> userAccountIdCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(userRepository).findByAccountId(userAccountIdCaptor.capture());

        String capturedAccountId = userAccountIdCaptor.getValue();

        assertThat(capturedAccountId).isEqualTo(loggedUserAccountId);
    }

    @Test
    void shouldNotFillPersonalDataWithConflictNickname() {

        //given
        String loggedUserAccountId = "A";

        UserEntity loggedUser = UserEntity.builder()
                .firstname("Marcin")
                .surname("Nowak")
                .nickname("marcin")
                .build();

        UserEntity newPersonalData = UserEntity.builder()
                .firstname("Kamil")
                .surname("Kowalski")
                .nickname("kamil")
                .build();

        //when
        Mockito.when(authService.getLoggedUserAccountId()).thenReturn(loggedUserAccountId);
        Mockito.when(userRepository.findByAccountId(loggedUserAccountId)).thenReturn(Optional.of(loggedUser));

        Mockito.when(userRepository.existsByNicknameContainingIgnoreCase(newPersonalData.getNickname())).thenReturn(true);

        //then
        assertThatThrownBy(() -> userService.fillPersonalData(newPersonalData))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Istnieje już użytkownik o takim pseudonimie");

        ArgumentCaptor<String> userAccountIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> nicknameCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(userRepository).findByAccountId(userAccountIdCaptor.capture());
        Mockito.verify(userRepository).existsByNicknameContainingIgnoreCase(nicknameCaptor.capture());

        String capturedAccountId = userAccountIdCaptor.getValue();
        String capturedNickname = nicknameCaptor.getValue();

        assertThat(capturedAccountId).isEqualTo(loggedUserAccountId);
        assertThat(capturedNickname).isEqualTo(newPersonalData.getNickname());
    }
}