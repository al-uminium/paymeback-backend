package paymeback.backend.exception;

public record ApiError(
    int status,
    String message
) {}
