package springboot2.handler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import springboot2.exception.BadRequestException;
import springboot2.exception.BadRequestExceptionDetails;
import springboot2.exception.ExceptionDetails;
import springboot2.exception.ValidationExceptionDetails;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler{
	
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
	
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
			MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatus status, WebRequest request) {
		List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
		
		String fields = fieldErrors.stream().map(FieldError::getField).collect(Collectors.joining(", "));
		String fieldsMessage = fieldErrors.stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(", "));
		
		return new ResponseEntity<>(
				ValidationExceptionDetails.builder()
				.title("Bad Resquest Exception, Invalid Fields")
				.status(HttpStatus.BAD_REQUEST.value())
				.details("Check the field(s) error")
				.developerMessage(exception.getClass().getName())
				.timestamp(LocalDateTime.now())
				.fields(fields)
				.fieldsMessage(fieldsMessage)
				.build(), HttpStatus.BAD_REQUEST);
	}
	
	@Override
	protected ResponseEntity<Object> handleExceptionInternal(
			Exception exception, @Nullable Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
		return new ResponseEntity<>(
				ExceptionDetails.builder()
				.title(exception.getCause().getMessage())
				.status(status.value())
				.details(exception.getMessage())
				.developerMessage(exception.getClass().getName())
				.timestamp(LocalDateTime.now())
				.build(), headers, status);
	}

}
