package pl.instagram.instagram.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.instagram.instagram.mapper.UUIDMapper;
import pl.instagram.instagram.mapper.UserMapper;
import pl.instagram.instagram.model.api.response.PostLikesResponse;
import pl.instagram.instagram.model.api.response.UserHeader;
import pl.instagram.instagram.model.domain.PostLikes;
import pl.instagram.instagram.model.entity.UserEntity;
import pl.instagram.instagram.service.PostLikeService;

import java.util.UUID;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostLikeController {

    private final PostLikeService postLikeService;

    private final UserMapper userMapper;
    private final UUIDMapper uuidMapper;

    private static final String POST_MAPPER_MESSAGE = "post";

    @GetMapping("/{id}/likes")
    ResponseEntity<PostLikesResponse> getPostLikesPage(@PathVariable("id") String postIdStr, Pageable pageable) {

        UUID postId = uuidMapper.strToUUID(postIdStr, POST_MAPPER_MESSAGE);

        PostLikes foundPostLikesDetails = postLikeService.getPostLikes(postId, pageable);
        Page<UserHeader> foundPostLikesHeadersPage = foundPostLikesDetails.postLikes()
            .map(userMapper::userEntityToUserHeader);
        PostLikesResponse foundPostsLikesResponse = new PostLikesResponse(
            postId.toString(), foundPostLikesHeadersPage, foundPostLikesDetails.didLoggedUserLikePost()
        );
        return ResponseEntity.ok(foundPostsLikesResponse);
    }

    @PostMapping("/{id}/likes")
    ResponseEntity<UserHeader> createPostLike(@PathVariable("id") String postIdStr){

        UUID postId = uuidMapper.strToUUID(postIdStr, POST_MAPPER_MESSAGE);

        UserEntity likeAuthor = postLikeService.addLike(postId);
        UserHeader likeAuthorHeader = userMapper.userEntityToUserHeader(likeAuthor);

        return ResponseEntity.status(HttpStatus.CREATED).body(likeAuthorHeader);
    }

    @DeleteMapping("/likes/{id}")
    ResponseEntity<Void> deletePostLike(@PathVariable("id") String postLikeIdStr){

        UUID postLikeId = uuidMapper.strToUUID(postLikeIdStr, POST_MAPPER_MESSAGE);

        postLikeService.removeLike(postLikeId);

        return ResponseEntity.noContent().build();
    }
}
