package eCommerce.GamesRun.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiError> handleUploadLimit(MaxUploadSizeExceededException exception) {
        return ResponseEntity.status(413).body(new ApiError(413, "A imagem deve ter até 5 MB."));
    }

	@ExceptionHandler(ApiException.class)
	public ResponseEntity<ApiError> handleApiException(ApiException exception) {
		ApiError error = new ApiError(exception.getStatus().value(), exception.getMessage());
		return ResponseEntity.status(exception.getStatus()).body(error);
	}

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataConflict(DataIntegrityViolationException exception) {
        return ResponseEntity.status(409).body(new ApiError(409,
                "Dados em conflito. Confira se nome, nick ou CPF já estão cadastrados e se os itens informados existem."));
    }
}
