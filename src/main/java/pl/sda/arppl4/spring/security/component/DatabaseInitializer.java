package pl.sda.arppl4.spring.security.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import pl.sda.arppl4.spring.security.config.InitialUsersConfiguration;
import pl.sda.arppl4.spring.security.model.ApplicationUser;
import pl.sda.arppl4.spring.security.model.ApplicationUserRole;
import pl.sda.arppl4.spring.security.repository.ApplicationUserRepository;
import pl.sda.arppl4.spring.security.repository.ApplicationUserRoleRepository;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseInitializer {
    private final ApplicationUserRepository applicationUserRepository;
    private final ApplicationUserRoleRepository applicationUserRoleRepository;

    // inicjalna konfiguracja bazy danych (związana z użytkownikami)
    private final InitialUsersConfiguration initialUserConfiguration;

    // Encryptor/Encoder
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @EventListener(ContextRefreshedEvent.class)
    public void initializeDatabase() {
        createRoles();
        createUsers();
    }

    private void createUsers() {
        for (InitialUsersConfiguration.InitialUserInfo userInfo : initialUserConfiguration.getUsers()) {
            createUserIfNotExists(userInfo);
        }
    }

    private void createUserIfNotExists(InitialUsersConfiguration.InitialUserInfo userInfo) {
        if (!applicationUserRepository.existsByUsername(userInfo.getUsername())) {

//            List<ApplicationUserRole> applicationUserRoleList = new ArrayList<>();
//            for (String role : userInfo.getRoles()) {
//                Optional<ApplicationUserRole> applicationUserRoleOptional = applicationUserRoleRepository.findByName(role);
//                if (applicationUserRoleOptional.isPresent()){
//                    applicationUserRoleList.add(applicationUserRoleOptional.get());
//                }
//            }

            // To samo co wyżej, może też napisać za pomocą streamów:

            Set<ApplicationUserRole> applicationUserRoleSet = userInfo.getRoles().stream()
                    .map(applicationUserRoleRepository::findByName)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());


            ApplicationUser applicationUser = ApplicationUser.builder()
                    .username(userInfo.getUsername())
                    .password(bCryptPasswordEncoder.encode(userInfo.getPassword()))  // wtrzykujemy beana dotyczącego szyfrowania hasła
                    .roles(applicationUserRoleSet)
                    .accountNonExpired(true)
                    .accountNonLocked(true)
                    .credentialsNonExpired(true)
                    .enabled(true)
                    .build();

            applicationUserRepository.save(applicationUser);

        }
    }

    private void createRoles() {
        for (String roleName : initialUserConfiguration.getRoles()) {
            createRoleIfNotExists(roleName);
        }
    }

    private void createRoleIfNotExists(String roleName) {
        if(!applicationUserRoleRepository.existsByName(roleName)) {
            ApplicationUserRole role = new ApplicationUserRole();
            role.setName(roleName);

            applicationUserRoleRepository.save(role);
        }
    }
}
