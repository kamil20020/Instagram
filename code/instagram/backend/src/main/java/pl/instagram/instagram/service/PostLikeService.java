package pl.instagram.instagram.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.instagram.instagram.model.entity.PostLike;

import java.util.List;
import java.util.UUID;

public interface PostLikeService {

    Page<PostLike> getPostLikes(UUID postId, Pageable pageable);
    PostLike addLike(UUID postId, UUID userId);
    void removeLike(UUID likeId);
}
