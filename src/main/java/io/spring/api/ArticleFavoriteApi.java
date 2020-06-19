package io.spring.api;

import io.spring.api.exception.ResourceNotFoundException;
import io.spring.application.ArticleQueryService;
import io.spring.application.data.ArticleData;
import io.spring.core.article.Article;
import io.spring.core.article.ArticleRepository;
import io.spring.core.favorite.ArticleFavorite;
import io.spring.core.favorite.ArticleFavoriteRepository;
import io.spring.core.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(path = "articles/{slug}/favorite")
public class ArticleFavoriteApi {
    private ArticleFavoriteRepository articleFavoriteRepository;
    private ArticleRepository articleRepository;
    private ArticleQueryService articleQueryService;

    @Autowired
    public ArticleFavoriteApi(ArticleFavoriteRepository articleFavoriteRepository,
                              ArticleRepository articleRepository,
                              ArticleQueryService articleQueryService) {
        this.articleFavoriteRepository = articleFavoriteRepository;
        this.articleRepository = articleRepository;
        this.articleQueryService = articleQueryService;
    }

    @PostMapping
    public ResponseEntity favoriteArticle(@PathVariable("slug") String slug,
                                          @AuthenticationPrincipal User user) {
        Article article = getArticle(slug);
        ArticleFavorite articleFavorite = new ArticleFavorite(article.getId(), user.getId());
        articleFavoriteRepository.save(articleFavorite);
        ArticleData articleData = articleQueryService.findBySlug(slug, user).orElse(null);
        return responseArticleData(articleData);
    }

    @DeleteMapping
    public ResponseEntity unfavoriteArticle(@PathVariable("slug") String slug,
                                            @AuthenticationPrincipal User user) {
        Article article = getArticle(slug);
        articleFavoriteRepository.find(article.getId(), user.getId()).ifPresent(favorite -> {
            articleFavoriteRepository.remove(favorite);
        });
        ArticleData articleData = articleQueryService.findBySlug(slug, user).orElse(null);
        return responseArticleData(articleData);
    }

    private ResponseEntity<HashMap<String, Object>> responseArticleData(final ArticleData articleData) {
        HashMap<String, Object> mapArticleData = new HashMap<String, Object>();
        mapArticleData.put("article", articleData);
        return ResponseEntity.ok(mapArticleData);
    }

    private Article getArticle(String slug) {
        return articleRepository.findBySlug(slug).map(article -> article)
                .orElseThrow(ResourceNotFoundException::new);
    }
}
