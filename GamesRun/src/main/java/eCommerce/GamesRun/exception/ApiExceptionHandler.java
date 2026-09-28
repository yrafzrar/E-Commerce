package eCommerce.GamesRun.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler(ApiException.class)
	public ResponseEntity<ApiError> handleApiException(ApiException exception) {
		ApiError error = new ApiError(exception.getStatus().value(), exception.getMessage());
		return ResponseEntity.status(exception.getStatus()).body(error);
	}
}