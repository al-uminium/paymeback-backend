package paymeback.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class CannotDeleteWithActiveDebtException extends RuntimeException{
  public CannotDeleteWithActiveDebtException(String message) {
    super(message);
  }
}
