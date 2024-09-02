package pl.instagram.instagram.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.context.annotation.Lazy;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name="USERS",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_users_account_id", columnNames = "account_id"),
        @UniqueConstraint(name = "uq_users_nickname", columnNames = "nickname")
    }
)
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="user_id")
    private UUID id;

    @Column(name = "account_id", nullable = false, length = 50)
    private String accountId;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "firstname", length = 50)
    private String firstname;

    @Column(name = "surname", length = 50)
    private String surname;

    @Column(name = "tel", length = 15)
    private String tel;

    @Column(name = "avatar")
    private byte[] avatar;

    @CreationTimestamp
    @Column(name = "creation_datetime", nullable = false)
    private LocalDateTime creationDatetime;

    @Column(name = "description")
    private String description;

    @Column(name = "is_verified", nullable = false)
    private boolean isVerified = false;

    @Column(name = "is_private", nullable = false)
    private boolean isPrivate = false;

    @Column(name = "followers_count", nullable = false)
    private Integer followers = 0;

    @Column(name = "followings_count", nullable = false)
    private Integer followings = 0;

    @Column(name = "posts_count", nullable = false)
    private Integer numberOfPosts = 0;

    @JsonIgnore
    @OneToMany(mappedBy = "author", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<PostEntity> posts = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "author", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<CommentEntity> comments = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "author", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<PostLikeEntity> likedPosts = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "author", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<CommentLikeEntity> likedComments = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "followed", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<FollowerEntity> followedUsers = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "follower", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<FollowerEntity> followersUsers = new HashSet<>();
}
