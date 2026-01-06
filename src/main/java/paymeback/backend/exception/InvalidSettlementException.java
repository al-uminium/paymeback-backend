package paymeback.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class InvalidSettlementException extends RuntimeException {
  public InvalidSettlementException(String message) {
    super(message);
  }
}
