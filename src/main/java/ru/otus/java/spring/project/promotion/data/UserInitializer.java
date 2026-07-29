package ru.otus.java.spring.project.promotion.data;

import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import ru.otus.java.spring.project.promotion.domains.promotions.User;
import ru.otus.java.spring.project.promotion.security.AppUserDetailsService;

@Component
@AllArgsConstructor
public class UserInitializer implements ApplicationRunner {

    private final AppUserDetailsService userDetailsService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        User user = new User();
        user.setUsername("user");
        user.setPassword("password");
        user.addAuthority("ROLE_USER");
        userDetailsService.save(user);

        User editor = new User();
        editor.setUsername("manager");
        editor.setPassword("password");
        editor.addAuthority("ROLE_MANAGER");
        userDetailsService.save(editor);
    }
}
