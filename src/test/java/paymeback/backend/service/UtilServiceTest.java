package paymeback.backend.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class UtilServiceTest {
  @Test
  @DisplayName("Link token returns valid token")
  void returnValidToken() {
    UtilService utilService = new UtilService();
    String token = utilService.generateLinkToken(30);
    assertTrue(token.matches("[a-zA-Z]{30}"));
  }

  @Test
  @DisplayName("Link token generates 30 characters")
  void returnTokenOf30length() {
    UtilService utilService = new UtilService();
    String token = utilService.generateLinkToken(30);
    assertEquals(30, token.length());
  }
}
