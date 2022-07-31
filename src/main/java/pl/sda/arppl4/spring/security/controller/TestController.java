package pl.sda.arppl4.spring.security.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/api/test")
public class TestController {

    // dostęp dla każdego       /api/test
    @GetMapping("")
    public String test() {
        return "test";
    }

    // dostęp dla zalogowanego :   /api/test/authorized
    @GetMapping("/authorized")
    public String testAuthorized(Authentication principal) {
        log.info("Po filtracji: " + principal);
        return "testAuthorized";
    }

}
