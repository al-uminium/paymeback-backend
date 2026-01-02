package paymeback.backend.controller;

// Controller used for testing random stuff xd

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import paymeback.backend.service.UtilService;

@RestController
@RequestMapping("/test")
public class TestController {

  private final UtilService utilService = new UtilService();

  @GetMapping("/token")
  public String generateToken() {
    return utilService.generateLinkToken(26);
  }
}
