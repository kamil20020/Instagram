package pl.instagram.instagram.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name="POSTS")
public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="post_id", insertable = false, updatable = false)
    private UUID Id;

    @Column(name = "creation_datetime", nullable = false)
    private LocalDateTime creationDatetime;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "img")
    private byte[] img;

    @Column(name = "are_hidden_likes", nullable = false)
    private boolean areHiddenLikes = false;

    @Column(name = "are_disabled_comments", nullable = false)
    private boolean areDisabledComments = false;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

    @JsonIgnore
    @OneToMany(mappedBy = "postEntity", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<PostLike> postLikes;

    @JsonIgnore
    @OneToMany(mappedBy = "postEntity", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<CommentEntity> commentEntityList;
}
