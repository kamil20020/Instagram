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
@Table(name="USERS")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="user_id", insertable = false, updatable = false)
    private UUID Id;

    @Column(name = "user_account_id", nullable = false, unique = true)
    private UUID userAccountId;

    @Column(name = "nickname", unique = true)
    private String nickname;

    @Column(name = "firstname")
    private String firstname;

    @Column(name = "surname")
    private String surname;

    @Column(name = "tel")
    private String tel;

    @Column(name = "avatar")
    private byte[] avatar;

    @Column(name = "creation_datetime", nullable = false)
    private LocalDateTime creationDatetime;

    @Column(name = "description")
    private String description;

    @Column(name = "is_verified", nullable = false)
    private boolean isVerified = false;

    @Column(name = "is_private", nullable = false)
    private boolean isPrivate = false;

    @Column(name = "followers", nullable = false)
    private Integer followers = 0;

    @Column(name = "followings", nullable = false)
    private Integer followings = 0;

    @Column(name = "number_of_posts", nullable = false)
    private Integer numberOfPosts = 0;

    @JsonIgnore
    @OneToMany(mappedBy = "userEntity", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<PostEntity> postEntityList;

    @JsonIgnore
    @OneToMany(mappedBy = "userEntity", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<PostLike> postLikes;

    @JsonIgnore
    @OneToMany(mappedBy = "userEntity", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<CommentEntity> commentEntityList;
}
