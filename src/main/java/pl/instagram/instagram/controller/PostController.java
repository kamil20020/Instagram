package pl.instagram.instagram.controller;

import jakarta.persistence.EntityExistsException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.instagram.instagram.exception.EntityNotFoundException;
import pl.instagram.instagram.mapper.*;
import pl.instagram.instagram.model.api.request.CreateComment;
import pl.instagram.instagram.model.api.request.CreatePost;
import pl.instagram.instagram.model.api.response.*;
import pl.instagram.instagram.model.entity.CommentEntity;
import pl.instagram.instagram.model.entity.PostEntity;
import pl.instagram.instagram.service.CommentService;
import pl.instagram.instagram.service.PostLikeService;
import pl.instagram.instagram.service.PostService;
import pl.instagram.instagram.service.UserService;

import java.security.Principal;
import java.util.UUID;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final UUIDMapper uuidMapper;
    private final PostMapper postMapper;

    private static final String POST_MAPPER_MESSAGE = "posta";
    private static final String USER_MAPPER_MESSAGE = "użytkownika";

    @GetMapping("/{id}")
    ResponseEntity<PostDetails> getPostById(@PathVariable("id") String postIdStr) {

        UUID postId = uuidMapper.strToUUID(postIdStr, POST_MAPPER_MESSAGE);

        PostEntity foundPost = postService.getPostById(postId);
        PostDetails postDetails = postMapper.postEntityToPostDetails(foundPost);

        return ResponseEntity.ok(postDetails);
    }

    @GetMapping("/{id}/posts")
    ResponseEntity<Page<PostHeader>> getUserPostsHeadersPage(@PathVariable("id") String userIdStr, Pageable pageable){

        UUID userId = uuidMapper.strToUUID(userIdStr, USER_MAPPER_MESSAGE);

        Page<PostEntity> foundPostsPage = postService.getUserPostsPage(userId, pageable);
        Page<PostHeader> foundPostsHeaders = foundPostsPage.map(postMapper::postEntityToPostHeader);

        return ResponseEntity.ok(foundPostsHeaders);
    }

    @PostMapping
    ResponseEntity<PostDetails> createPost(@Valid @RequestBody CreatePost createPost, Principal principal){

        String loggedUserAccountId = principal.getName();
        PostEntity toCreatePost = postMapper.createPostToPostEntity(createPost);

        PostEntity createdPost = postService.createPost(loggedUserAccountId, toCreatePost);
        PostDetails postDetails = postMapper.postEntityToPostDetails(createdPost);

        return ResponseEntity.status(HttpStatus.CREATED).body(postDetails);
    }

    @PatchMapping("/{id}")
    ResponseEntity<PostDetails> patchPostById(@PathVariable("id") String postIdStr, @RequestBody PostEntity postEntity) {

        UUID postId = uuidMapper.strToUUID(postIdStr, POST_MAPPER_MESSAGE);

        PostEntity changedPost = postService.patchPostById(postId, postEntity);
        PostDetails changedPostDetails = postMapper.postEntityToPostDetails(changedPost);

        return ResponseEntity.ok(changedPostDetails);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deletePostById(@PathVariable("id") String postIdStr){

        UUID postId = uuidMapper.strToUUID(postIdStr, POST_MAPPER_MESSAGE);

        postService.deletePostById(postId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}