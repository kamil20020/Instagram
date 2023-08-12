package pl.instagram.instagram.controller;

import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.instagram.instagram.exception.EntityNotFoundException;
import pl.instagram.instagram.mapper.ByteArrayMapper;
import pl.instagram.instagram.mapper.UserMapper;
import pl.instagram.instagram.model.api.request.CreatePost;
import pl.instagram.instagram.model.api.request.CreatePostLike;
import pl.instagram.instagram.model.api.response.BasicPostLikeData;
import pl.instagram.instagram.model.api.response.BasicUserData;
import pl.instagram.instagram.model.entity.PostEntity;
import pl.instagram.instagram.model.entity.PostLike;
import pl.instagram.instagram.service.PostLikeService;
import pl.instagram.instagram.service.PostService;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final PostLikeService postLikeService;
    private final ByteArrayMapper byteArrayMapper = ByteArrayMapper.INSTANCE;
    private final UserMapper userMapper = UserMapper.INSTANCE;

    @GetMapping("/{id}")
    ResponseEntity getPostById(@PathVariable("id") String postIdStr) {

        UUID postId;

        try {
            postId = UUID.fromString(postIdStr);
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Podano niepoprawne id posta");
        }

        PostEntity foundPost;

        try {
            foundPost = postService.getPostById(postId);
        }
        catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

        return ResponseEntity.ok(foundPost);
    }

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

        Page<PostLike> foundPostLikesPage;

        try {
            foundPostLikesPage = postLikeService.getPostLikes(postId, pageable);
        }
        catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

        Page<BasicUserData> likedByPage = foundPostLikesPage.map(l ->
            userMapper.userEntityToBasicUserData(l.getUserEntity())
        );

        return ResponseEntity.ok(likedByPage);
    }

    @PostMapping
    ResponseEntity createPost(@RequestBody CreatePost createPost){

        UUID userId;

        try{
            userId = UUID.fromString(createPost.getUserId());
        }
        catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Podano niewłaściwe id użytkownika");
        }

        PostEntity toCreatePost = PostEntity.builder()
            .areDisabledComments(createPost.isAreDisabledComments())
            .areHiddenLikes(createPost.isAreHiddenLikes())
            .description(createPost.getDescription())
            .img(byteArrayMapper.base64ToByteArray(createPost.getImg()))
        .build();

        PostEntity createdPost;

        try{
            createdPost = postService.createPost(userId, toCreatePost);
        }
        catch(EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

    @PostMapping("/{id}/likes")
    ResponseEntity likePost(@PathVariable("id") String postIdStr, @RequestBody CreatePostLike createPostLike){

        UUID postId;

        try {
            postId = UUID.fromString(postIdStr);
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Podano niepoprawne id posta");
        }

        PostLike createdLike;

        try{
            createdLike = postLikeService.addLike(postId, createPostLike.getUserId());
        }
        catch(EntityExistsException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }

        BasicUserData likeAuthorBasicData = userMapper.userEntityToBasicUserData(createdLike.getUserEntity());
        BasicPostLikeData basicPostLikeData = BasicPostLikeData.builder()
            .id(createdLike.getId().toString())
            .user(likeAuthorBasicData)
            .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(basicPostLikeData);
    }

    @PatchMapping("/{id}")
    ResponseEntity updatePostById(@PathVariable("id") String postIdStr, @RequestBody PostEntity postEntity){

        UUID postId;

        try{
            postId = UUID.fromString(postIdStr);
        }
        catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Podano niewłaściwy identyfikator posta");
        }

        try{
            postService.updatePostById(postId, postEntity);
        }
        catch(EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{id}")
    ResponseEntity deletePostById(@PathVariable("id") String postIdStr){

        UUID postId;

        try{
            postId = UUID.fromString(postIdStr);
        }
        catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Podano niewłaściwy identyfikator posta");
        }

        try{
            postService.deletePostById(postId);
        }
        catch(EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/like/{id}")
    ResponseEntity likePost(@PathVariable("id") String postLikeIdStr){

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