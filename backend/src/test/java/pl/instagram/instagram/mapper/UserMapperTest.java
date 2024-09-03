package pl.instagram.instagram.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.instagram.instagram.model.api.request.PersonalData;
import pl.instagram.instagram.model.api.request.UpdateUser;
import pl.instagram.instagram.model.api.response.UserHeader;
import pl.instagram.instagram.model.api.response.UserProfile;
import pl.instagram.instagram.model.entity.UserEntity;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    @InjectMocks
    private final UserMapper userMapper = new UserMapperImpl();

    @Mock
    private Base64Mapper base64Mapper;

    @Test
    void shouldConvertUpdateUserToUserEntity() {

        UpdateUser updateUser = new UpdateUser(
            "kamil",
            "Kamil",
            "Kowalski",
            "a2FtaWw="   // encoded word kamil
        );

        UserEntity user = userMapper.updateUserToUserEntity(updateUser);

        assertEquals(updateUser.nickname(), user.getNickname());
        assertEquals(updateUser.firstname(), user.getFirstname());
        assertEquals(updateUser.surname(), user.getSurname());
        assertEquals("kamil", user.getNickname());
    }

    @Test
    void shouldConvertUpdateUserToUserEntityWhenInputIsNull() {

        assertNull(userMapper.updateUserToUserEntity(null));
    }

    @Test
    void shouldConvertUserPersonalDataToUserEntity() {

        PersonalData personalData = new PersonalData(
            "kamil",
            "Kamil",
            "Kowalski"
        );

        UserEntity user = userMapper.userPersonalDataToUserEntity(personalData);

        assertEquals(personalData.firstname(), user.getFirstname());
        assertEquals(personalData.surname(), user.getSurname());
        assertEquals(personalData.nickname(), user.getNickname());
    }

    @Test
    void shouldConvertUserPersonalDataToUserEntityWhenInputIsNull() {

        assertNull(userMapper.userPersonalDataToUserEntity(null));
    }

    @Test
    void shouldConvertUserEntityToUserHeader() {

        UUID userId = UUID.randomUUID();
        byte[] avatar = ("kamil").getBytes();
        String encodedAvatar = "a2FtaWw=";

        UserEntity user = UserEntity.builder()
            .nickname("kamil")
            .firstname("Kamil")
            .surname("Kowalski")
            .avatar(avatar)
            .isVerified(true)
            .build();

        user.setId(userId);

        Mockito.when(base64Mapper.byteArrayToBase64(any())).thenReturn(encodedAvatar);

        UserHeader userHeader = userMapper.userEntityToUserHeader(user);

        assertEquals(userId.toString(), userHeader.id());
        assertEquals(user.getNickname(), userHeader.nickname());
        assertEquals(user.getFirstname(), userHeader.firstname());
        assertEquals(user.getSurname(), userHeader.surname());
        assertEquals(encodedAvatar, userHeader.avatar());   // encoded word kamil
        assertEquals(user.isVerified(), userHeader.isVerified());
        assertEquals(user.getNickname(), userHeader.nickname());

        Mockito.verify(base64Mapper).byteArrayToBase64(avatar);
    }

    @Test
    void shouldConvertUserEntityToUserHeaderWhenInputIsNull() {

        assertNull(userMapper.userEntityToUserHeader(null));
    }

    @Test
    void shouldConvertUserEntityToUserProfileInfo() {

        UUID userId = UUID.randomUUID();
        byte[] avatar = ("kamil").getBytes();
        String encodedAvatar = "a2FtaWw=";

        UserEntity user = UserEntity.builder()
            .nickname("kamil")
            .firstname("Kamil")
            .surname("Kowalski")
            .description("Opis")
            .avatar(avatar)
            .isVerified(true)
            .isPrivate(false)
            .followers(1)
            .followings(2)
            .numberOfPosts(3)
            .build();

        user.setId(userId);

        Mockito.when(base64Mapper.byteArrayToBase64(any())).thenReturn(encodedAvatar);

        UserProfile userProfile = userMapper.userEntityToUserProfileInfo(user);

        assertEquals(user.getId().toString(), userProfile.id());
        assertEquals(user.getNickname(), userProfile.nickname());
        assertEquals(user.getFirstname(), userProfile.firstname());
        assertEquals(user.getSurname(), userProfile.surname());
        assertEquals(user.getDescription(), userProfile.description());
        assertEquals(encodedAvatar, userProfile.avatar());
        assertEquals(user.isVerified(), userProfile.isVerified());
        assertEquals(user.isPrivate(), userProfile.isPrivate());
        assertEquals(user.getFollowers(), userProfile.followers());
        assertEquals(user.getFollowings(), userProfile.followings());
        assertEquals(user.getNumberOfPosts(), userProfile.numberOfPosts());
    }

    @Test
    void shouldConvertUserEntityToUserProfileInfoWhenInputIsNull() {

        assertNull(userMapper.userEntityToUserProfileInfo(null));
    }

    @Test
    void shouldConvertUserEntityListToUserHeaderList() {

        List<UUID> usersIds = List.of(UUID.randomUUID(), UUID.randomUUID());

        List<byte[]> avatars = List.of(
            ("kamil").getBytes(StandardCharsets.UTF_8),
            ("michał").getBytes(StandardCharsets.UTF_8)
        );

        List<String> encodedAvatars = List.of(
            "a2FtaWw=", "bWFyY2lu"
        );

        UserEntity user1 = UserEntity.builder()
            .nickname("kamil")
            .firstname("Kamil")
            .surname("Kowalski")
            .avatar(avatars.get(0))
            .isVerified(false)
            .build();

        user1.setId(usersIds.get(0));

        UserEntity user2 = UserEntity.builder()
            .nickname("michał")
            .firstname("Michał")
            .surname("Nowak")
            .avatar(avatars.get(1))
            .isVerified(true)
            .build();

        user2.setId(usersIds.get(1));

        List<UserEntity> users = List.of(user1, user2);

        Mockito.when(base64Mapper.byteArrayToBase64(avatars.get(0))).thenReturn(encodedAvatars.get(0));
        Mockito.when(base64Mapper.byteArrayToBase64(avatars.get(1))).thenReturn(encodedAvatars.get(1));

        List<UserHeader> convertedUsers = userMapper.userEntityListToUserHeaderList(users);

        for(int i = 0; i < users.size(); i++){

            UserEntity user = users.get(i);
            UserHeader userHeader = convertedUsers.get(i);

            assertEquals(user.getId().toString(), userHeader.id());
            assertEquals(user.getNickname(), userHeader.nickname());
            assertEquals(user.getFirstname(), userHeader.firstname());
            assertEquals(user.getSurname(), userHeader.surname());
            assertEquals(encodedAvatars.get(i), userHeader.avatar());
            assertEquals(user.isVerified(), userHeader.isVerified());
        }
    }

    @Test
    void shouldConvertUserEntityListToUserHeaderListWhenInputIsNull() {

        assertNull(userMapper.userEntityListToUserHeaderList(null));
    }
}