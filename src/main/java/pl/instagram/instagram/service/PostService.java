package pl.instagram.instagram.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.instagram.instagram.model.entity.PostEntity;

import java.util.UUID;

public interface PostService {

    PostEntity getPostById(UUID postId);
    Page<PostEntity> getUserPostsPage(UUID userId, Pageable pageable);
    PostEntity createPost(UUID userId, PostEntity postEntity);
    void updatePostById(UUID postId, PostEntity postEntity);
    void deletePostById(UUID postId);
}
