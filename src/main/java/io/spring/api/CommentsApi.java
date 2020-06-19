package io.spring.api;

import com.fasterxml.jackson.annotation.JsonRootName;
import io.spring.api.exception.InvalidRequestException;
import io.spring.api.exception.NoAuthorizationException;
import io.spring.api.exception.ResourceNotFoundException;
import io.spring.application.CommentQueryService;
import io.spring.application.data.CommentData;
import io.spring.core.article.Article;
import io.spring.core.article.ArticleRepository;
import io.spring.core.comment.Comment;
import io.spring.core.comment.CommentRepository;
import io.spring.core.service.AuthorizationService;
import io.spring.core.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/articles/{slug}/comments")
public class CommentsApi {
    private ArticleRepository articleRepository;
    private CommentRepository commentRepository;
    private CommentQueryService commentQueryService;

    @Autowired
    public CommentsApi(ArticleRepository articleRepository,
                       CommentRepository commentRepository,
                       CommentQueryService commentQueryService) {
        this.articleRepository = articleRepository;
        this.commentRepository = commentRepository;
        this.commentQueryService = commentQueryService;
    }

    @PostMapping
    public ResponseEntity<?> createComment(@PathVariable("slug") String slug,
                                                     @AuthenticationPrincipal User user,
                                                     @Valid @RequestBody NewCommentParam newCommentParam,
                                                     BindingResult bindingResult) {
        Article article = findArticle(slug);
        if (bindingResult.hasErrors()) {
            throw new InvalidRequestException(bindingResult);
        }
        Comment comment = new Comment(newCommentParam.getBody(), user.getId(), article.getId());
        commentRepository.save(comment);
        return ResponseEntity.status(201).body(commentResponse(commentQueryService.findById(comment.getId(), user).get()));
    }

    @GetMapping
    public ResponseEntity getComments(@PathVariable("slug") String slug,
                                      @AuthenticationPrincipal User user) {
        Article article = findArticle(slug);
        List<CommentData> comments = commentQueryService.findByArticleId(article.getId(), user);
        Map<String,Object> mapComments = new HashMap<String, Object>();
        mapComments.put("comments", comments);
        return ResponseEntity.ok(mapComments);
    }

    @RequestMapping(path = "{id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteComment(@PathVariable("slug") String slug,
                                        @PathVariable("id") String commentId,
                                        @AuthenticationPrincipal User user) {
        Article article = findArticle(slug);
        return commentRepository.findById(article.getId(), commentId).map(comment -> {
            if (!AuthorizationService.canWriteComment(user, article, comment)) {
                throw new NoAuthorizationException();
            }
            commentRepository.remove(comment);
            return ResponseEntity.noContent().build();
        }).orElseThrow(ResourceNotFoundException::new);
    }

    private Article findArticle(String slug) {
        return articleRepository.findBySlug(slug).map(article -> article).orElseThrow(ResourceNotFoundException::new);
    }

    private Map<String, Object> commentResponse(CommentData commentData) {
        Map<String,Object> mapComment = new HashMap<String, Object>();
        mapComment.put("comment", commentData);
        return  mapComment;
    }
}

@Getter
@NoArgsConstructor
@JsonRootName("comment")
class NewCommentParam {
    @NotBlank(message = "can't be empty")
    private String body;
}
