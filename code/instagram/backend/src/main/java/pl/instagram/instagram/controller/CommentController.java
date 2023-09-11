package pl.instagram.instagram.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.instagram.instagram.exception.EntityNotFoundException;
import pl.instagram.instagram.model.api.request.UpdateComment;
import pl.instagram.instagram.service.CommentService;

import java.util.UUID;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    @PutMapping("/{id}")
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
