package pl.instagram.instagram.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import pl.instagram.instagram.model.entity.CommentEntity;
import pl.instagram.instagram.model.entity.FollowerEntity;
import pl.instagram.instagram.model.entity.PostEntity;
import pl.instagram.instagram.model.entity.UserEntity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntityForLoggedUser {

    private UUID Id;
    private String accountId;
    private String nickname;
    private String firstname;
    private String surname;
    private String tel;
    private byte[] avatar;
    private LocalDateTime creationDatetime;
    private String description;
    private boolean isVerified;
    private boolean isPrivate;
    private Integer followers;
    private Integer followings;
    private Integer numberOfPost;
    private Set<PostEntity> posts;
    private Set<PostEntity> likedPosts;
    private Set<FollowerEntity> followedUsers;
    private Set<FollowerEntity> followersUsers;
    private Set<CommentEntity> likedComments;
    private Set<CommentEntity> comments;
    private boolean didLoggedUserFollow;
}