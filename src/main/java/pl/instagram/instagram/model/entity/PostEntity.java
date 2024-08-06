package pl.instagram.instagram.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name="POSTS",
    indexes = {
        @Index(name = "ix_posts_author_id", columnList = "author_id"),
        @Index(name = "ix_posts_creation_datetime", columnList = "creation_datetime")
    }
)
public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="post_id", updatable = false)
    private UUID Id;

    @CreationTimestamp
    @Column(name = "creation_datetime", nullable = false)
    private LocalDateTime creationDatetime;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "content")
    private byte[] content;

    @Column(name = "are_hidden_likes", nullable = false)
    private boolean areHiddenLikes = false;

    @Column(name = "are_disabled_comments", nullable = false)
    private boolean areDisabledComments = false;

    @Column(name = "likes_count", nullable = false)
    private Integer likesCount = 0;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "author_id", nullable = false, updatable = false)
    private UserEntity author;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "POSTS_LIKES",
        joinColumns = @JoinColumn(name = "post_id", referencedColumnName = "post_id", nullable = false, updatable = false),
        inverseJoinColumns = @JoinColumn(name = "author_id", referencedColumnName = "user_id", nullable = false, updatable = false)
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<UserEntity> likes;

    @JsonIgnore
    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<CommentEntity> comments;
}
