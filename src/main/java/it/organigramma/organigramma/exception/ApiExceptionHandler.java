package it.organigramma.organigramma.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(RoleNotAllowedException.class)
    public ResponseEntity<?> roleNotAllowed(RoleNotAllowedException ex) {
        return ResponseEntity.badRequest().body(Map.of(
                "error", "ROLE_NOT_ALLOWED",
                "message", ex.getMessage()
        ));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> illegalState(IllegalStateException ex) {
        return ResponseEntity.badRequest().body(Map.of(
                "error", "INVALID_OPERATION",
                "message", ex.getMessage()
        ));
    }

    @ExceptionHandler(java.util.NoSuchElementException.class)
    public ResponseEntity<?> notFound(java.util.NoSuchElementException ex) {
        return ResponseEntity.status(404).body(Map.of(
                "error", "NOT_FOUND",
                "message", "Risorsa non trovata"
        ));
    }

    @ExceptionHandler(UnitNotDeletableException.class)
    public ResponseEntity<?> unitNotDeletable(UnitNotDeletableException ex) {
        return ResponseEntity.status(409).body(Map.of(
                "error", "UNIT_NOT_DELETABLE",
                "message", ex.getMessage()
        ));
    }

}
