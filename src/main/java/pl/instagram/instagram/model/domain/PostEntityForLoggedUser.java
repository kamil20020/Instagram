package pl.instagram.instagram.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import pl.instagram.instagram.model.entity.CommentEntity;
import pl.instagram.instagram.model.entity.PostEntity;
import pl.instagram.instagram.model.entity.UserEntity;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostEntityForLoggedUser {

    private UUID Id;
    private LocalDateTime creationDatetime;
    private String description;
    private byte[] content;
    private boolean areHiddenLikes;
    private boolean areDisabledComments;
    private Integer likesCount;
    private Integer commentsCount;
    private UserEntity author;
    private Set<CommentEntity> comments;
    private PostEntity post;
    private boolean didLoggedUserLikePost;
}
