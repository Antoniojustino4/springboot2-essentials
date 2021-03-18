package springboot2.handler;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import springboot2.exception.BadRequestException;
import springboot2.exception.BadRequestExceptionDetails;

@ControllerAdvice
public class RestExceptionHandler {
	
	@ExceptionHandler(BadRequestException.class)
	private ResponseEntity<BadRequestExceptionDetails> handlerBadRequestException(BadRequestException bre){
		return new ResponseEntity<>(
				BadRequestExceptionDetails.builder()
				.title("Bad Resquest Exception, Check the Documentation")
				.status(HttpStatus.BAD_REQUEST.value())
				.details(bre.getMessage())
				.developerMessage(bre.getClass().getName())
				.timestamp(LocalDateTime.now())
				.build(), HttpStatus.BAD_REQUEST);
	}

}
