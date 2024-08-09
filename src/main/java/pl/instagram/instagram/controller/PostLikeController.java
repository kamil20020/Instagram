package pl.instagram.instagram.controller;

import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.instagram.instagram.exception.EntityNotFoundException;
import pl.instagram.instagram.mapper.UUIDMapper;
import pl.instagram.instagram.mapper.UserMapper;
import pl.instagram.instagram.model.api.response.BasicPostLikeData;
import pl.instagram.instagram.model.api.response.UserHeader;
import pl.instagram.instagram.model.entity.UserEntity;
import pl.instagram.instagram.service.PostLikeService;

import java.security.Principal;
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
    ResponseEntity<Page<UserHeader>> getPostLikesPage(@PathVariable("id") String postIdStr, Pageable pageable) {

        UUID postId = uuidMapper.strToUUID(postIdStr, POST_MAPPER_MESSAGE);

        Page<UserEntity> foundPostLikesUsers = postLikeService.getPostLikes(postId, pageable);
        Page<UserHeader> foundPostsLikesUsersHeaders = foundPostLikesUsers.map(userMapper::userEntityToUserHeader);

        return ResponseEntity.ok(foundPostsLikesUsersHeaders);
    }

    @PostMapping("/{id}/likes")
    ResponseEntity<UserHeader> createPostLike(@PathVariable("id") String postIdStr, Principal principal){

        String loggedUserAccountId = principal.getName();

        UUID postId = uuidMapper.strToUUID(postIdStr, POST_MAPPER_MESSAGE);

        UserEntity likeAuthor = postLikeService.addLike(postId, loggedUserAccountId);
        UserHeader likeAuthorHeader = userMapper.userEntityToUserHeader(likeAuthor);

        return ResponseEntity.status(HttpStatus.CREATED).body(likeAuthorHeader);
    }

    @DeleteMapping("/likes/{id}")
    ResponseEntity<Void> deletePostLike(@PathVariable("id") String postLikeIdStr, Principal principal){

        String loggedUserAccountId = principal.getName();

        UUID postLikeId = uuidMapper.strToUUID(postLikeIdStr, POST_MAPPER_MESSAGE);

        postLikeService.removeLike(postLikeId, loggedUserAccountId);

        return ResponseEntity.noContent().build();
    }
}
