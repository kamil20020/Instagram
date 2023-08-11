package pl.instagram.instagram.controller;

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
import pl.instagram.instagram.model.api.request.CreatePost;
import pl.instagram.instagram.model.entity.PostEntity;
import pl.instagram.instagram.service.PostService;

import java.security.Principal;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final ByteArrayMapper byteArrayMapper = ByteArrayMapper.INSTANCE;

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
}