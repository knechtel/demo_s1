package io.spring.api;

import io.spring.application.TagsQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "tags")
public class TagsApi {
    private TagsQueryService tagsQueryService;

    @Autowired
    public TagsApi(TagsQueryService tagsQueryService) {
        this.tagsQueryService = tagsQueryService;
    }

    @GetMapping
    public ResponseEntity<Map<String,Object>> getTags() {
        Map<String,Object> mapTags = new HashMap<>();
        mapTags.put("tags", tagsQueryService.allTags());
        return ResponseEntity.ok(mapTags);
    }
}
