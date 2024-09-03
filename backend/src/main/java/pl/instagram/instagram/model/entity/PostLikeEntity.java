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
@Table(name = "POSTS_LIKES")
public class PostLikeEntity {

    @Embeddable
    @Data
    @NoArgsConstructor
    public static class PostLikeEntityId implements Serializable {
        private UUID authorId;
        private UUID postId;

        public PostLikeEntityId(UUID authorId, UUID postId){
            this.authorId = authorId;
            this.postId = postId;
        }
    }

    @EmbeddedId
    private PostLikeEntity.PostLikeEntityId id = new PostLikeEntity.PostLikeEntityId();

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @MapsId(value = "authorId")
    @JoinColumn(name = "author_id", nullable = false, updatable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserEntity author;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @MapsId(value = "postId")
    @JoinColumn(name = "post_id", nullable = false, updatable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private PostEntity post;

    public PostLikeEntity(UserEntity author, PostEntity post){
        this.author = author;
        this.post = post;
        id = new PostLikeEntityId(author.getId(), post.getId());
    }
}
