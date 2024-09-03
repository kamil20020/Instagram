package pl.instagram.instagram.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "COMMENTS_LIKES")
public class CommentLikeEntity {

    @Embeddable
    @Data
    @NoArgsConstructor
    public static class CommentLikeEntityId implements Serializable {

        private UUID authorId;
        private UUID commentId;

        public CommentLikeEntityId(UUID authorId, UUID commentId){
            this.authorId = authorId;
            this.commentId = commentId;
        }
    }

    @EmbeddedId
    private CommentLikeEntityId id = new CommentLikeEntityId();

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @MapsId(value = "authorId")
    @JoinColumn(name = "author_id", nullable = false, updatable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserEntity author;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @MapsId(value = "commentId")
    @JoinColumn(name = "comment_id", nullable = false, updatable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private CommentEntity comment;

    public CommentLikeEntity(UserEntity author, CommentEntity comment){
        this.author = author;
        this.comment = comment;
        this.id = new CommentLikeEntityId(author.getId(), comment.getId());
    }

}
