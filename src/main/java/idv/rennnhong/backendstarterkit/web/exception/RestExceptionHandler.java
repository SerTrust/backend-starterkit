package idv.rennnhong.backendstarterkit.web.exception;

import idv.rennnhong.common.response.ErrorMessages;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NonUniqueResultException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
//@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFound(
        EntityNotFoundException ex) {
        return new ResponseEntity<Object>(ErrorMessages.RESOURCE_NOT_FOUND.toObject(),
            HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(javassist.NotFoundException.class)
    protected ResponseEntity<Object> handleNotFound(
        javassist.NotFoundException ex) {
        return new ResponseEntity<Object>(ErrorMessages.RESOURCE_NOT_FOUND.toObject(),
            HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    protected ResponseEntity<Object> handleEmptyResultDataAccessException(
        EmptyResultDataAccessException ex) {
        return new ResponseEntity<Object>(ErrorMessages.RELATION_DATA_NOT_EXIST.toObject(),
            HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NullPointerException.class)
    protected ResponseEntity<Object> handleNullPointerException(
        NullPointerException ex) {
        return new ResponseEntity<Object>(ErrorMessages.NULL_POINTER_EXCEPTION.toObject(),
            HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NonUniqueResultException.class)
    protected ResponseEntity<Object> handleNonUniqueResultException(
        NonUniqueResultException ex) {
        return new ResponseEntity<Object>(ErrorMessages.RECORD_ALREADY_EXISTS.toObject(),
            HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleServerException(Exception exception,
                                                           HttpServletRequest request, HttpServletResponse response) {
        return new ResponseEntity<Object>(ErrorMessages.INTERNAL_SERVER_ERROR.toObject(),
            HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    protected ResponseEntity<Object> handleMaxUploadSizeException(MaxUploadSizeExceededException exception,
                                                                  HttpServletRequest request,
                                                                  HttpServletResponse response) {
        return new ResponseEntity<Object>(ErrorMessages.REQUEST_MAX_UPLOAD_SIZE_EXCEED.toObject(),
            HttpStatus.BAD_REQUEST);
    }
}
