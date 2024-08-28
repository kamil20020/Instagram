package pl.instagram.instagram.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.io.Serializable;
import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "FOLLOWERS")
public class FollowerEntity {

    @Embeddable
    @Data
    @NoArgsConstructor
    public static class FollowerEntityId implements Serializable {
        private UUID followedId;
        private UUID followerId;

        public FollowerEntityId(UUID followedId, UUID followerId){
            this.followedId = followedId;
            this.followerId = followerId;
        }
    }

    @EmbeddedId
    private FollowerEntityId id = new FollowerEntityId();

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @MapsId(value = "followedId")
    @JoinColumn(name = "followed_id", nullable = false, updatable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserEntity followed;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @MapsId(value = "followerId")
    @JoinColumn(name = "follower_id", nullable = false, updatable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UserEntity follower;

    public FollowerEntity(UserEntity followed, UserEntity follower){
        this.followed = followed;
        this.follower = follower;
        this.id = new FollowerEntityId(followed.getId(), follower.getId());
    }
}
