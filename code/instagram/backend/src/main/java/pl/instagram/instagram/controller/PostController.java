package pl.instagram.instagram.controller;

import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.instagram.instagram.exception.EntityNotFoundException;
import pl.instagram.instagram.mapper.ByteArrayMapper;
import pl.instagram.instagram.mapper.DateTimeMapper;
import pl.instagram.instagram.mapper.PostMapper;
import pl.instagram.instagram.mapper.UserMapper;
import pl.instagram.instagram.model.api.request.CreateComment;
import pl.instagram.instagram.model.api.request.CreatePost;
import pl.instagram.instagram.model.api.request.UpdateComment;
import pl.instagram.instagram.model.api.response.BasicPostLikeData;
import pl.instagram.instagram.model.api.response.BasicUserData;
import pl.instagram.instagram.model.api.response.CommentData;
import pl.instagram.instagram.model.api.response.PostDetails;
import pl.instagram.instagram.model.entity.CommentEntity;
import pl.instagram.instagram.model.entity.PostEntity;
import pl.instagram.instagram.model.entity.PostLike;
import pl.instagram.instagram.model.entity.UserEntity;
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
    private final UserService userService;
    private final PostLikeService postLikeService;
    private final CommentService commentService;
    private final ByteArrayMapper byteArrayMapper = ByteArrayMapper.INSTANCE;
    private final UserMapper userMapper = UserMapper.INSTANCE;
    private final PostMapper postMapper = PostMapper.INSTANCE;
    private final DateTimeMapper dateTimeMapper = DateTimeMapper.INSTANCE;

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

        PostDetails postDetails = postMapper.postEntityToPostDetails(foundPost);

        return ResponseEntity.ok(postDetails);
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

    @GetMapping("/{id}/comments")
    ResponseEntity getPostCommentsPage(@PathVariable("id") String postIdStr, Pageable pageable){

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

        Page<CommentEntity> foundCommentsPage;

        try{
            foundCommentsPage = commentService.getPostCommentsPage(postId, pageable);
        }
        catch(EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

        Page<CommentData> convertedCommentsPage = foundCommentsPage.map(c ->
            CommentData.builder()
                .id(c.getId().toString())
                .content(c.getContent())
                .userData(userMapper.userEntityToBasicUserData(c.getUserEntity()))
                .creationDatetime(dateTimeMapper.map(c.getCreationDatetime()))
                .build()
        );

        return ResponseEntity.ok(convertedCommentsPage);
    }

    @PostMapping
    ResponseEntity createPost(@RequestBody CreatePost createPost, Principal principal){

        String userAccountId = principal.getName();

        PostEntity toCreatePost = PostEntity.builder()
            .areDisabledComments(createPost.isAreDisabledComments())
            .areHiddenLikes(createPost.isAreHiddenLikes())
            .description(createPost.getDescription())
            .img(byteArrayMapper.base64ToByteArray(createPost.getImg()))
        .build();

        PostEntity createdPost;

        try{
            UUID foundLoggedUserId = userService.getUserIdByUserAccountId(userAccountId);
            createdPost = postService.createPost(foundLoggedUserId, toCreatePost);
        }
        catch(EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

    @PostMapping("/{id}/likes")
    ResponseEntity likePost(@PathVariable("id") String postIdStr, Principal principal){

        String userAccountId = principal.getName();

        UUID postId;

        try {
            postId = UUID.fromString(postIdStr);
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Podano niepoprawne id posta");
        }

        PostLike createdLike;

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

        BasicUserData likeAuthorBasicData = userMapper.userEntityToBasicUserData(createdLike.getUserEntity());
        BasicPostLikeData basicPostLikeData = BasicPostLikeData.builder()
            .id(createdLike.getId().toString())
            .user(likeAuthorBasicData)
            .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(basicPostLikeData);
    }

    @PostMapping("/{id}/comments")
    ResponseEntity createPostComment(@PathVariable("id") String postIdStr, @RequestBody CreateComment createComment, Principal principal){

        String userAccountId = principal.getName();

        UUID postId;

        try {
            postId = UUID.fromString(postIdStr);
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Podano niepoprawne id posta");
        }

        try{
            UUID foundLoggedUserId = userService.getUserIdByUserAccountId(userAccountId);
            commentService.createComment(postId, foundLoggedUserId, createComment.getParentCommentId(), createComment.getContent());
        }
        catch(EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
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

    @PutMapping("/comments/{id}")
    ResponseEntity updateComment(@PathVariable("id") String commentIdStr, @RequestBody UpdateComment updateComment){

        UUID commentId;

        try{
            commentId = UUID.fromString(commentIdStr);
        }
        catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Podano niewłaściwy identyfikator komentarza");
        }

        try{
            commentService.updateComment(commentId, updateComment.getContent());
        }
        catch(EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

        return ResponseEntity.noContent().build();
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

    @DeleteMapping("/likes/{id}")
    ResponseEntity removeLike(@PathVariable("id") String postLikeIdStr){

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

    @DeleteMapping("/comments/{id}")
    ResponseEntity removeComment(@PathVariable("id") String commentIdStr){

        UUID commentId;

        try{
            commentId = UUID.fromString(commentIdStr);
        }
        catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Podano niewłaściwy identyfikator komentarza");
        }

        try{
            commentService.deleteComment(commentId);
        }
        catch(EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

        return ResponseEntity.noContent().build();
    }
}