package pl.instagram.instagram.service;

import jakarta.persistence.EntityExistsException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.instagram.instagram.exception.EntityNotFoundException;
import pl.instagram.instagram.model.entity.PostEntity;
import pl.instagram.instagram.model.entity.UserEntity;
import pl.instagram.instagram.repository.PostRepository;
import pl.instagram.instagram.repository.UserRepository;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostService postService;
    private final UserService userService;
    private final UserRepository userRepository;

    public Page<UserEntity> getPostLikes(UUID postId, Pageable pageable) throws EntityNotFoundException {

        if(!postService.existsById(postId)){
            throw new EntityNotFoundException("Nie istnieje post o takim id");
        }

        pageable = PageRequest.of(
            pageable.getPageNumber(), pageable.getPageSize(), Sort.by("creationDatetime").ascending()
        );

        return userRepository.findByLikedPostsId(postId, pageable);
    }

    @Transactional
    public UserEntity addLike(UUID postId, UUID authorId) throws EntityNotFoundException, EntityExistsException {

        PostEntity post = postService.getPostById(postId);
        UserEntity author = userService.getUserById(authorId);

        if(userRepository.existsByIdAndLikedPostsId(authorId, postId)){
            throw new EntityExistsException("Istnieje już polubienie posta o takich id posta i użytkownika");
        }

        post.setLikesCount(post.getLikesCount() + 1);
        author.getLikedPosts().add(post);

        return author;
    }

    @Transactional
    public void removeLike(UUID postId, UUID authorId) throws EntityNotFoundException {

        PostEntity post = postService.getPostById(postId);
        UserEntity author = userService.getUserById(authorId);

        if(!userRepository.existsByIdAndLikedPostsId(authorId, postId)){
            throw new EntityNotFoundException("Nie istnieje polubienie posta o takich id użytkownika i posta");
        }

        post.setLikesCount(post.getLikesCount() - 1);
        author.getLikedPosts().remove(post);
    }
}
