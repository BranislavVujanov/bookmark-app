/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package example.bookmark.repository;

import example.bookmark.model.User;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author Branislav Vujanov
 */
public interface UserRepository extends CrudRepository<User, Long> {
    
    User findByUsername (String username);
    
   
}
