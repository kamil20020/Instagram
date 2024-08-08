package pl.instagram.instagram.controller;

import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.instagram.instagram.exception.EntityNotFoundException;
import pl.instagram.instagram.model.api.response.BasicPostLikeData;
import pl.instagram.instagram.model.api.response.UserHeader;

import java.security.Principal;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostLikeController {

    @GetMapping("/{id}/likes")
    ResponseEntity getPostLikesPage(@PathVariable("id") String postIdStr, Pageable pageable) {

        if(pageable == null){
            pageable = Pageable.unpaged();
        }

        UUID postId;

        try {
            postId = UUID.fromString(postIdStr);
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Podano niepoprawne id posta");
        }

        Page<PostLikeEntity> foundPostLikesPage;

        try {
            foundPostLikesPage = postLikeService.getPostLikes(postId, pageable);
        }
        catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

        Page<UserHeader> likedByPage = foundPostLikesPage.map(l ->
                userMapper.userEntityToBasicUserData(l.getUserEntity())
        );

        return ResponseEntity.ok(likedByPage);
    }

    @PostMapping("/{id}/likes")
    ResponseEntity createPostLike(@PathVariable("id") String postIdStr, Principal principal){

        String userAccountId = principal.getName();

        UUID postId;

        try {
            postId = UUID.fromString(postIdStr);
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Podano niepoprawne id posta");
        }

        PostLikeEntity createdLike;

        try{
            UUID foundLoggedUserId = userService.getUserIdByUserAccountId(userAccountId);
            createdLike = postLikeService.addLike(postId, foundLoggedUserId);
        }
        catch(EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        catch(EntityExistsException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }

        UserHeader likeAuthorBasicData = userMapper.userEntityToBasicUserData(createdLike.getUserEntity());
        BasicPostLikeData basicPostLikeData = BasicPostLikeData.builder()
                .id(createdLike.getId().toString())
                .user(likeAuthorBasicData)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(basicPostLikeData);
    }

    @DeleteMapping("/likes/{id}")
    ResponseEntity deletePostLike(@PathVariable("id") String postLikeIdStr){

        UUID postLikeId;

        try{
            postLikeId = UUID.fromString(postLikeIdStr);
        }
        catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Podano niewłaściwy identyfikator like posta");
        }

        try{
            postLikeService.removeLike(postLikeId);
        }
        catch(EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

        return ResponseEntity.noContent().build();
    }
}
