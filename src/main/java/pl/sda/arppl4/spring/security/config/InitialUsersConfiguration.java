package pl.sda.arppl4.spring.security.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "default")
public class InitialUsersConfiguration {

    private List<String> roles;
    private List<InitialUserInfo> users;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InitialUserInfo {
        private String username;
        private String password;
        private List<String> roles;
    }

}
