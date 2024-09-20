package pl.instagram.instagram.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.instagram.instagram.model.api.response.CommentData;
import pl.instagram.instagram.model.api.response.UserHeader;
import pl.instagram.instagram.model.entity.CommentEntity;
import pl.instagram.instagram.model.entity.UserEntity;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class CommentMapperTest {

    @InjectMocks
    private final CommentMapper commentMapper = new CommentMapperImpl();

    @Mock
    private DateTimeMapper dateTimeMapper;

    @Mock
    private UserMapper userMapper;

    @Test
    void shouldConvertCommentEntityToCommentData() {

        //given
        OffsetDateTime offsetDateTime = OffsetDateTime.now();
        LocalDateTime localDateTime = offsetDateTime.toLocalDateTime();

        UserEntity author = UserEntity.builder()
            .nickname("kamil")
            .firstname("Kamil")
            .surname("Kowalski")
            .avatar(("Zdjęcie").getBytes(StandardCharsets.UTF_8))
            .isVerified(true)
            .build();

        author.setId(UUID.randomUUID());

        CommentEntity comment = CommentEntity.builder()
            .author(author)
            .content("Dobre zdjęcie")
            .creationDatetime(localDateTime)
            .subCommentsCount(1)
            .likesCount(2)
            .build();

        comment.setId(UUID.randomUUID());

        UserHeader userHeader = new UserHeader(
            author.getId().toString(),
            author.getNickname(),
            author.getFirstname(),
            author.getSurname(),
            "WmRqxJljaWU=",
            author.isVerified()
        );

        //when
        Mockito.when(userMapper.userEntityToUserHeader(any())).thenReturn(userHeader);
        Mockito.when(dateTimeMapper.map(any())).thenReturn(offsetDateTime);

        CommentData gotCommentData = commentMapper.commentEntityToCommentData(comment);

        //then
        assertEquals(comment.getId().toString(), gotCommentData.id());
        assertEquals(comment.getContent(), gotCommentData.content());
        assertEquals(comment.getCreationDatetime(), gotCommentData.creationDatetime().toLocalDateTime());
        assertEquals(comment.getSubCommentsCount(), gotCommentData.subCommentsCount());
        assertEquals(comment.getLikesCount(), gotCommentData.likesCount());
        assertNotNull(gotCommentData.author());

        Mockito.verify(userMapper).userEntityToUserHeader(author);
        Mockito.verify(dateTimeMapper).map(localDateTime);
    }

    @Test
    void shouldConvertCommentEntityToCommentDataWhenInputIsNull() {

        //given
        //when
        //then
        assertNull(commentMapper.commentEntityToCommentData(null));
    }
}