package pl.instagram.instagram.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name="COMMENTS",
    indexes = {
        @Index(name = "ix_comments_parent_comment_id", columnList = "parent_comment_id"),
        @Index(name = "ix_comments_creation_datetime", columnList = "creation_datetime")
    }
)
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "comment_id")
    private UUID id;

    @Column(name = "content", nullable = false)
    private String content;

    @CreationTimestamp
    @Column(name = "creation_datetime", nullable = false)
    private LocalDateTime creationDatetime;

    @Column(name = "likes_count", nullable = false)
    private Integer likesCount = 0;

    @Column(name = "sub_comments_count", nullable = false)
    private Integer subCommentsCount = 0;

    @JsonIgnore
    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<CommentEntity> subComments;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id", updatable = false)
    @EqualsAndHashCode.Exclude
    private CommentEntity parentComment;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false, updatable = false)
    @EqualsAndHashCode.Exclude
    private PostEntity post;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "author_id", nullable = false, updatable = false)
    @EqualsAndHashCode.Exclude
    private UserEntity author;

    @JsonIgnore
    @OneToMany(mappedBy = "comment", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<CommentLikeEntity> commentLikes;
}
