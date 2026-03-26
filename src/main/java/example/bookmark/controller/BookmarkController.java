/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package example.bookmark.controller;

import example.bookmark.model.Bookmark;
import example.bookmark.service.BookmarkService;
import java.net.URI;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

/**
 *
 * @author Branislav Vujanov
 */

@RestController
@RequestMapping("/bookmarks")
public class BookmarkController {
    
    
    private final BookmarkService service;
    
    
    public BookmarkController(BookmarkService service) {
        this.service = service;
    }
    
    
    
    @GetMapping("/{id}")
    public ResponseEntity<Bookmark> findById(@PathVariable Long id, Authentication authentication) {

        Bookmark bookmark = service.findById(id, authentication);

        return ResponseEntity.ok(bookmark);
    }

    
    @PostMapping
    public ResponseEntity<Void> createBookmark(@RequestBody Bookmark newBookmark, UriComponentsBuilder ucb, 
                                                                                    Authentication authentication) {
        
            Bookmark savedBookmark = service.save(newBookmark, authentication);

            URI locationOfSavedBookmark = ucb
                    .path("/bookmarks/{id}")
                    .buildAndExpand(savedBookmark.getId())
                    .toUri();

            return ResponseEntity.created(locationOfSavedBookmark).build();   
    }
    
    @GetMapping
    public ResponseEntity<List<Bookmark>> findAll(Pageable pageable, Authentication authentication) {
        
        Page<Bookmark> page = service.findAll(pageable, authentication);
        
        return ResponseEntity.ok(page.getContent());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBookmark(@PathVariable Long id, Authentication authentication) {
        
            service.deleteBookmark(id, authentication);
            
            return ResponseEntity.noContent().build();

    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateBookmark(@PathVariable Long id, @RequestBody Bookmark bookmarkUpdate,
            Authentication authentication) {

            service.putBookmark(id, bookmarkUpdate, authentication);
            
            return ResponseEntity.noContent().build();
    }
    
}
