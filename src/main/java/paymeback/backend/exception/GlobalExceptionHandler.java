package paymeback.backend.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiError> handleBadRequest(IllegalArgumentException ex) {
    return ResponseEntity.badRequest().body(new ApiError(400, ex.getMessage()));
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ApiError> handleInvalidParams(ConstraintViolationException ex) {
    return ResponseEntity.badRequest().body(new ApiError(400, ex.getMessage()));
  }

  @ExceptionHandler(GroupNotFoundException.class)
  public ResponseEntity<ApiError> handleGroupNotFound(GroupNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ApiError(204, ex.getMessage()));
  }

  @ExceptionHandler(MemberNotFoundException.class)
  public ResponseEntity<ApiError> handleMemberNotFound(MemberNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(404, ex.getMessage()));
  }

  @ExceptionHandler(ExpenseNotFoundException.class)
  public ResponseEntity<ApiError> handleExpenseNotFound(ExpenseNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(404, ex.getMessage()));
  }

  @ExceptionHandler(CannotDeleteWithActiveDebtException.class)
  public ResponseEntity<ApiError> handleCannotDeleteWithActiveDebt(CannotDeleteWithActiveDebtException ex) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiError(403, ex.getMessage()));
  }

  @ExceptionHandler(InvalidSettlementException.class)
  public ResponseEntity<ApiError> handleInvalidSettlement(InvalidSettlementException ex) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiError(403, ex.getMessage()));
  }
}
