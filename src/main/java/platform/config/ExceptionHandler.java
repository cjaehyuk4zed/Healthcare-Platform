package platform.config;

import com.google.zxing.WriterException;
import jakarta.servlet.ServletException;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.io.FileNotFoundException;
import java.io.IOException;

@Slf4j
@ControllerAdvice
public class ExceptionHandler {

    private final String prefix = "ExceptionHandler : ";

    private void logError(Exception e){
        log.error(prefix + "Exception caused by : {}", e.getCause());
        log.error(prefix + "{} : {}", e.getClass(), e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> accessDeniedException(AccessDeniedException e){
        logError(e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> badRequestException(BadRequestException e){
        logError(e);
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> dataIntegrityViolationException(DataIntegrityViolationException e){
        logError(e);
        return ResponseEntity.badRequest().body("Foreign Key constraint violation : Invalid IDs");
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<String> fileNotFoundException(FileNotFoundException e){
        logError(e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(HttpMessageConversionException.class)
    public ResponseEntity<String> httpMessageConversionException(HttpMessageConversionException e){
        logError(e);
        return ResponseEntity.internalServerError().body(e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> illegalArgumentException(IllegalArgumentException e){
        logError(e);
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(IOException.class)
    public ResponseEntity<String> ioException(IOException e){
        logError(e);
        return ResponseEntity.internalServerError().body(e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(ServletException.class)
    public ResponseEntity<String> servletException(ServletException e){
        logError(e);
        return ResponseEntity.internalServerError().body(e.getMessage());
    }

    // Exception thrown by the ZXing QRCode library
    @org.springframework.web.bind.annotation.ExceptionHandler(WriterException.class)
    public ResponseEntity<String> writerException(WriterException e){
        logError(e);
        return ResponseEntity.internalServerError().body(e.getMessage());
    }

}
