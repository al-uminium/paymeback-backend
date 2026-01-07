package paymeback.backend.service;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Currency;
import java.util.Random;
import java.util.Set;

@Service
public class UtilService {

  public String generateLinkToken(int length) {
    StringBuilder sb = new StringBuilder();
    String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    String linkToken = "";
    Random r = new Random();
    for (int i = 0; i < length; i++) {
      linkToken = String.valueOf(sb.append(characters.charAt(r.nextInt(characters.length()))));
    }

    return linkToken;
  }
}
