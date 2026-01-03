package paymeback.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MembersNotFoundException extends RuntimeException{
  public MembersNotFoundException(String message) {
    super(message);
  }
}
