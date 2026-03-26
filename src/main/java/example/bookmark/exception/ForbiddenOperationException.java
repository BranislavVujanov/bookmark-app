/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package example.bookmark.exception;

/**
 *
 * @author Branislav Vujanov
 */
public class ForbiddenOperationException extends RuntimeException {
   
    public ForbiddenOperationException(String message) {
        super(message);
    }
}
