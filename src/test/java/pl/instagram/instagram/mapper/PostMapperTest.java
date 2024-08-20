package pl.instagram.instagram.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.instagram.instagram.model.api.request.CreatePost;
import pl.instagram.instagram.model.api.response.PostDetails;
import pl.instagram.instagram.model.api.response.PostHeader;
import pl.instagram.instagram.model.api.response.UserHeader;
import pl.instagram.instagram.model.entity.PostEntity;
import pl.instagram.instagram.model.entity.UserEntity;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class PostMapperTest {

    @InjectMocks
    private final PostMapper postMapper = new PostMapperImpl();

    @Mock
    private Base64Mapper base64Mapper;

    @Mock
    private DateTimeMapper dateTimeMapper;

    @Mock
    private UserMapper userMapper;

    @Test
    void shouldConvertCreatePostToPostEntity() {

        //given
        String encodedContent = "WmF3YXJ0b3NjIHBvc3R1";

        CreatePost createPost = new CreatePost(
            "Opis postu",
            encodedContent, // base64 encoded Zawartosc postu
            true,
            false
        );

        byte[] content = ("Zawartosc postu").getBytes(StandardCharsets.UTF_8);

        //when
        Mockito.when(base64Mapper.base64ToByteArray(anyString())).thenReturn(content);

        PostEntity gotPost = postMapper.createPostToPostEntity(createPost);

        //then
        assertEquals(createPost.description(), gotPost.getDescription());
        assertEquals(content, gotPost.getContent());
        assertEquals(createPost.areHiddenLikes(), gotPost.isAreHiddenLikes());
        assertEquals(createPost.areDisabledComments(), gotPost.isAreDisabledComments());

        Mockito.verify(base64Mapper).base64ToByteArray(encodedContent);
    }

    @Test
    void shouldConvertCreatePostToPostEntityWhenInputIsNull() {

        assertNull(postMapper.createPostToPostEntity(null));
    }

    @Test
    void shouldConvertPostEntityToPostDetails() {

        //given
        OffsetDateTime offsetDateTime = OffsetDateTime.now();
        LocalDateTime localDateTime = offsetDateTime.toLocalDateTime();

        UUID postId = UUID.randomUUID();
        byte[] content = ("Zawartosc postu").getBytes(StandardCharsets.UTF_8);
        String encodedContent = "WmF3YXJ0b3NjIHBvc3R1";

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

        UserHeader userHeader = new UserHeader(
            userId.toString(),
            user.getNickname(),
            user.getFirstname(),
            user.getSurname(),
            encodedAvatar,
            user.isVerified()
        );

        PostEntity post = PostEntity.builder()
            .creationDatetime(localDateTime)
            .description("Opis")
            .content(content)
            .areHiddenLikes(true)
            .areDisabledComments(false)
            .author(user)
            .likesCount(1)
            .commentsCount(2)
            .build();

        post.setId(postId);

        //when
        Mockito.when(dateTimeMapper.map(any())).thenReturn(offsetDateTime);
        Mockito.when(base64Mapper.byteArrayToBase64(any())).thenReturn(encodedContent);
        Mockito.when(userMapper.userEntityToUserHeader(any())).thenReturn(userHeader);

        PostDetails gotPostDetails = postMapper.postEntityToPostDetails(post);

        //then
        assertEquals(post.getId().toString(), gotPostDetails.id());
        assertEquals(offsetDateTime, gotPostDetails.creationDatetime());
        assertEquals(post.getDescription(), gotPostDetails.description());
        assertEquals(encodedContent, gotPostDetails.content());
        assertEquals(post.isAreHiddenLikes(), gotPostDetails.areHiddenLikes());
        assertEquals(post.isAreDisabledComments(), gotPostDetails.areDisabledComments());
        assertEquals(userHeader, gotPostDetails.author());
        assertEquals(post.getLikesCount(), gotPostDetails.likesCount());
        assertEquals(post.getCommentsCount(), gotPostDetails.commentsCount());

        Mockito.verify(dateTimeMapper).map(localDateTime);
        Mockito.verify(base64Mapper).byteArrayToBase64(content);
        Mockito.verify(userMapper).userEntityToUserHeader(user);
    }

    @Test
    void shouldConvertPostEntityToPostDetailsWhenInputIsNull() {

        assertNull(postMapper.postEntityToPostDetails(null));
    }

    @Test
    void shouldConvertPostEntityToPostHeader() {

        //given
        UUID postId = UUID.randomUUID();
        byte[] content = ("Zawartosc postu").getBytes(StandardCharsets.UTF_8);
        String encodedContent = "WmF3YXJ0b3NjIHBvc3R1";

        PostEntity post = PostEntity.builder()
            .content(content)
            .areHiddenLikes(true)
            .likesCount(1)
            .commentsCount(2)
            .build();

        post.setId(postId);

        //when
        Mockito.when(base64Mapper.byteArrayToBase64(any())).thenReturn(encodedContent);

        //then
        PostHeader gotPostHeader = postMapper.postEntityToPostHeader(post);

        assertEquals(post.getId().toString(), gotPostHeader.id());
        assertEquals(encodedContent, gotPostHeader.content());
        assertEquals(post.isAreHiddenLikes(), gotPostHeader.areHiddenLikes());
        assertEquals(post.getLikesCount(), gotPostHeader.likesCount());
        assertEquals(post.getCommentsCount(), gotPostHeader.commentsCount());

        Mockito.verify(base64Mapper).byteArrayToBase64(content);
    }

    @Test
    void shouldConvertPostEntityToPostHeaderWhenInputIsNull() {

        assertNull(postMapper.postEntityToPostHeader(null));
    }
}