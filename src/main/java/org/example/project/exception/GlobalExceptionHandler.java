package org.example.project.exception;

import lombok.RequiredArgsConstructor;
import org.example.project.extra.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;


@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
  private static final Logger logger= LoggerFactory.getLogger(GlobalExceptionHandler.class);

 @ExceptionHandler({
         NotFoundException.class,
         ForbiddenException.class,
         Exception.class
 })

    public ResponseEntity<?> handleException(Exception exception, WebRequest webRequest){
       logger.error(exception.getMessage(),exception);
     HttpStatus httpStatus = determineHttpStatus(exception);
     return ResponseEntity.status(httpStatus)
             .body(new ApiResponse(exception.getMessage(),false,null));
 }

 private HttpStatus determineHttpStatus(Exception exception){
     if (exception instanceof NotFoundException)return HttpStatus.NOT_FOUND;
     if (exception instanceof ForbiddenException)return HttpStatus.FORBIDDEN;
     if (exception instanceof AlreadyExistException)return HttpStatus.BAD_REQUEST;
     return HttpStatus.INTERNAL_SERVER_ERROR;
 }




}
