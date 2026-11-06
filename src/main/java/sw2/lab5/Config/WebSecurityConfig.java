package sw2.lab5.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.formLogin()
                .loginPage("/loginForm")
                .loginProcessingUrl("/processlogin")
                .defaultSuccessUrl("/redirectByRol", true);

        http.logout().logoutSuccessUrl("/")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true);

        http.authorizeRequests().antMatchers("/user/list").hasAuthority("admin");
        http.authorizeRequests().antMatchers("/post/delete").hasAnyAuthority("admin", "user");
        http.authorizeRequests().antMatchers("/user", "/user/edit").hasAnyAuthority("user", "admin");
        http.authorizeRequests().antMatchers("/post/create", "/post/edit").hasAnyAuthority("user", "admin");
        http.authorizeRequests().anyRequest().permitAll();

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(DataSource dataSource) {
        JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);
        manager.setUsersByUsernameQuery("SELECT email, password, active from user where email = ?");
        manager.setAuthoritiesByUsernameQuery("select u.email, r.role_name from user u " +
                "inner join role r on u.id_role = r.id_role " +
                "where u.email = ? and u.active = 1");
        return manager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
