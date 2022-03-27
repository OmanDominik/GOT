package internetowy_przewodnik_got.security;

import internetowy_przewodnik_got.model.EmployeePTTK;
import internetowy_przewodnik_got.model.repositories.EmployeePTTKRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final EmployeePTTKRepo employeePTTKRepo;

    @Autowired
    public SecurityConfig(EmployeePTTKRepo employeePTTKRepo) {
        this.employeePTTKRepo = employeePTTKRepo;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic().and().authorizeRequests()
                .antMatchers("/index.html", "/trails.html", "/route-generator.html").permitAll()
                .antMatchers("/admin-panel.html").hasRole("WORKER")
                .and()
                .formLogin().permitAll()
                .and()
                .logout().logoutSuccessUrl("/index.html")
                .and()
                .csrf().disable();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        var employeePTTKList = employeePTTKRepo.findAll();
        List<UserDetails> userDetailsList = new ArrayList<>();
        for (EmployeePTTK employee: employeePTTKList) {
            UserDetails userDetail = User.withDefaultPasswordEncoder()
                    .username(employee.getEmail())
                    .password(employee.getPassword())
                    .roles("WORKER")
                    .build();
            userDetailsList.add(userDetail);
        }

        return new InMemoryUserDetailsManager(userDetailsList);
    }
}
