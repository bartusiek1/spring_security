package pl.sda.arppl4.spring.security.config.jwt;

public interface SecurityConstants {
    String APPLICATION_NAME = "sPRINGsECURITY";
    String APPLICATION_KEY = "ArppL4SeCrEt";
    String HEADER_AUTH = "Authorization";       // Standard
    String HEADER_AUTH_BEARER = "Bearer:";       // Standard
    String HEADER_EXPIRATION = "Expires_at";
    String HEADER_ROLES = "App_roles";
    String ROLES_SEPARATOR = ",";
}
