package eCommerce.GamesRun.exception;

public record ApiError(int status, String message) {
}