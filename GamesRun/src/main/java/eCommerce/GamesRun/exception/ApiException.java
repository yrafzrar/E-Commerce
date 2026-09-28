package eCommerce.GamesRun.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApiException extends RuntimeException {

	private final HttpStatus status;

	public ApiException(HttpStatus status, String message) {
		super(message);
		this.status = status;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public ResponseEntity<Object> toResponseEntity() {
		return ResponseEntity.status(status)
				.body(new ApiError(status.value(), getMessage()));
	}
}