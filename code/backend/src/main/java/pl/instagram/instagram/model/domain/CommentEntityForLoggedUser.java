package pl.instagram.instagram.model.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.instagram.instagram.model.entity.CommentEntity;
import pl.instagram.instagram.model.entity.CommentLikeEntity;
import pl.instagram.instagram.model.entity.PostEntity;
import pl.instagram.instagram.model.entity.UserEntity;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentEntityForLoggedUser {
    
    private UUID id;
    private String content;
    private LocalDateTime creationDatetime;
    private Integer likesCount;
    private Integer subCommentsCount;
    private Set<CommentEntity> subComments;
    private CommentEntity parentComment;
    private PostEntity post;
    private UserEntity author;
    private CommentEntity comment;
    private Set<CommentLikeEntity> commentLikes;
    private boolean didLoggedUserLikeComment;
}
