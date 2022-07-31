package pl.sda.arppl4.spring.security.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import pl.sda.arppl4.spring.security.config.jwt.AuthenticationFilter;
import pl.sda.arppl4.spring.security.config.jwt.AuthorizationFilter;
import pl.sda.arppl4.spring.security.service.ApplicationUserService;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ApplicationUserService applicationUserService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // wyłącz csrf (cross site request forgery - edytowanie formularza i nanoszenia w nim zmian z pozycji użytkownika)
                .csrf().disable()
                // wyłącz cross origin resource sharing
                .cors().disable()
                // dlaej konfigurujemy autoryzację requestów
                .authorizeRequests()
                // przepuść każdy rewuest metodą GET na /api/test
                .antMatchers(HttpMethod.GET, "/api/test").permitAll()
                .antMatchers(HttpMethod.GET, "/api/test/authorized").hasRole("ADMIN")
                // przepuść każdy request dowolną metodą na /api/test/** ('**' oznacza podstrony)
                .antMatchers("/api/public/**").permitAll()
                // pozostałe, wszystkie requesty muszą być authenticated (zalogowane)
                .anyRequest().authenticated()
                // i...(cofamy się do konfiguracji HttpSecurity, bo przed chwilą byliśmy w 'authorizeRequests()')
                .and()
                .addFilter(new AuthenticationFilter(authenticationManager()))
                .addFilter(new AuthorizationFilter(authenticationManager()))
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                //// domyślne logowanie tokenowe (TODO: zastąpimy je)
                .httpBasic();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(bCryptPasswordEncoder);
        // password encoder
        daoAuthenticationProvider.setUserDetailsService(applicationUserService);
        // ustaw providera w managera
        auth.authenticationProvider(daoAuthenticationProvider);
    }

    /** Konfiguracja authentication providera - wskazujemy Spring Authentication Manager, gdzie jest nasz
     * authentication provider (ma być DAO).
     * Manager pochopdzi ze spring'a. Provider pochodzi ze springa, ale my wskazujemy providerowi service który
     * provider odpytuje w celu wyszukiwania użytkownika
     */

}
