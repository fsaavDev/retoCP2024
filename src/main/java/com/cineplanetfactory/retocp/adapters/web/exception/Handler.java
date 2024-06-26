package com.cineplanetfactory.retocp.adapters.web.exception;

import com.cineplanetfactory.retocp.domain.response.RetoCpApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class Handler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RetoCpApiResponse<String>> handleGenericExceptions(Exception e, WebRequest req){
        log.error("Ocurrio un error: {}",e.getMessage());
        RetoCpApiResponse<String> res = new RetoCpApiResponse<>(e.getMessage());
        return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ModelNotFoundException.class)
    public ResponseEntity<RetoCpApiResponse<String>> handleModelNotFoundException(ModelNotFoundException e, WebRequest req){
        log.error("Ocurrio un error de entidad no encontrada: {}",e.getMessage());
        RetoCpApiResponse<String> res = new RetoCpApiResponse<>(e.getMessage());
        return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<RetoCpApiResponse<String>> handleUnauthorizedException(UnauthorizedException e, WebRequest req){
        log.error("Ocurrio un error de autorizacion: {}",e.getMessage());
        RetoCpApiResponse<String> res = new RetoCpApiResponse<>(e.getMessage());
        return new ResponseEntity<>(res, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<RetoCpApiResponse<List<String>>> handleSQLExceptions(DataIntegrityViolationException e, WebRequest req){
        log.error("Ocurrio un error de tipo SQL: {}",e.getMessage());
        List<String> errList = e.getMostSpecificCause().getMessage().lines().map(String::strip).collect(Collectors.toList());
        RetoCpApiResponse<List<String>> res = new RetoCpApiResponse<>(errList);
        return new ResponseEntity<>(res, HttpStatus.CONFLICT);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException e, HttpHeaders headers, HttpStatus status, WebRequest req) {
        log.error("Ocurrio un error de tipo No Handler Found: {}",e.getMessage());
        RetoCpApiResponse<String> res = new RetoCpApiResponse<>(e.getMessage());
        return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpHeaders headers, HttpStatus status, WebRequest req) {
        log.error("Ocurrio un error con los argumentos del request: {}",e.getMessage());
        Map<String,String> errList = new HashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(err-> errList.put(err.getField(),err.getDefaultMessage()));
        RetoCpApiResponse<Map<String,String>> res = new RetoCpApiResponse<>(errList);
        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException e, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error("Ocurrio un error con los parametros del request: {}",e.getMessage());
        RetoCpApiResponse<String> res = new RetoCpApiResponse<>(e.getMessage());
        return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
    }
}