/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package example.bookmark.service;

import example.bookmark.exception.BadRequestException;
import example.bookmark.exception.DuplicateResourceException;
import example.bookmark.exception.ForbiddenOperationException;
import example.bookmark.exception.ResourceNotFoundException;
import example.bookmark.model.Bookmark;
import example.bookmark.model.Role;
import example.bookmark.model.User;
import example.bookmark.repository.BookmarkRepository;
import example.bookmark.repository.UserRepository;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 *
 * @author Branislav Vujanov
 */
@Service
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    

    public BookmarkService(BookmarkRepository repository, UserRepository userRepository) {
        this.bookmarkRepository = repository;
        this.userRepository = userRepository;
    }

    
    private boolean isModerator(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(Role.ROLE_MODERATOR.name()));
    }
    
    private void validateBookmark(Bookmark bookmark) {
        if (bookmark.getTitle() == null || bookmark.getTitle().isBlank()) {
            throw new BadRequestException("Title must not be blank");
        }
        if (bookmark.getUrl() == null || bookmark.getUrl().isBlank()) {
            throw new BadRequestException("URL must not be blank");
        }
    }
    
    private void ensureNoDuplicate(Long userId, String url) {
        if (bookmarkRepository.existsByUserIdAndUrl(userId, url)) {
            throw new DuplicateResourceException("Bookmark already exists for this user");
        }
    }
    
    
    
    public Bookmark findById(Long id, Authentication authentication) {
   
        if (isModerator(authentication)) {
            return bookmarkRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Bookmark not found"));
        }

        User user = userRepository.findByUsername(authentication.getName());

        return bookmarkRepository.findByIdAndUserId(id, user.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Bookmark not found"));
    }
    
    public Bookmark save(Bookmark bookmark, Authentication authentication) {
        
        validateBookmark(bookmark);

        if (isModerator(authentication)) {
            throw new ForbiddenOperationException("Moderators cannot create bookmarks");
        }
        
        User user = userRepository.findByUsername(authentication.getName());
        ensureNoDuplicate(user.getId(), bookmark.getUrl());
        
        bookmark.setUserId(user.getId());
        bookmark.setCreatedAt(LocalDateTime.now());
        
        return bookmarkRepository.save(bookmark);
    }
    
    public Page<Bookmark> findAll(Pageable pageable, Authentication authentication) {

        if (isModerator(authentication)) {
            return bookmarkRepository.findAll(pageable); 
        }

        User user = userRepository.findByUsername(authentication.getName());
        
        return bookmarkRepository.findByUserId(user.getId(), pageable);
    }
    
    public void deleteBookmark (Long id, Authentication authentication) {
        
        if (isModerator(authentication)) {
            if (!bookmarkRepository.existsById(id)) {
                throw new ResourceNotFoundException("Bookmark not found");
            }
            bookmarkRepository.deleteById(id);
            return;
        }
        
        User user = userRepository.findByUsername(authentication.getName());

        boolean bookmarkExists = bookmarkRepository.existsByIdAndUserId(id, user.getId());
        if (!bookmarkExists) {
            throw new ResourceNotFoundException("Bookmark not found");
        }
        
        bookmarkRepository.deleteById(id);
    }
  
    public void putBookmark(Long requestedId, Bookmark bookmarkUpdate, Authentication authentication) {
        
        validateBookmark(bookmarkUpdate);

        if (isModerator(authentication)) {
            throw new ForbiddenOperationException("Moderators cannot update bookmarks");
        }

        User user = userRepository.findByUsername(authentication.getName());

        Bookmark existingBookmark = bookmarkRepository
                .findByIdAndUserId(requestedId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Bookmark not found"));
        
        //In case user wishes to update his Bookmark URL, check for duplicate URL 
        if (!existingBookmark.getUrl().equals(bookmarkUpdate.getUrl())){
            ensureNoDuplicate(user.getId(), bookmarkUpdate.getUrl());
        }
        // update only allowed fields
        existingBookmark.setTitle(bookmarkUpdate.getTitle());
        existingBookmark.setUrl(bookmarkUpdate.getUrl());
        existingBookmark.setDescription(bookmarkUpdate.getDescription());

        bookmarkRepository.save(existingBookmark);
    }
}
 