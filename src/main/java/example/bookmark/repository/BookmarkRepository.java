/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package example.bookmark.repository;

import example.bookmark.model.Bookmark;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 *
 * @author Branislav Vujanov
 */
public interface BookmarkRepository extends CrudRepository<Bookmark, Long>, PagingAndSortingRepository<Bookmark, Long> {

    boolean existsByUserIdAndUrl(Long userId, String url);

    Optional<Bookmark> findByIdAndUserId(Long id, Long userId);

    Page<Bookmark> findByUserId(Long userId, Pageable pageable);
    
    boolean existsByIdAndUserId(Long id, Long userId);
}
    

