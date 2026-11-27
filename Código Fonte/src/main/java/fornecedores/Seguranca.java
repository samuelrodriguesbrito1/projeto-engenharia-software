package fornecedores;

import java.util.Arrays;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
class Seguranca {
    @Bean
    SecurityFilterChain acesso(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(regras -> regras
                        .requestMatchers("/styles/**", "/scripts/**").permitAll()
                        .anyRequest().hasRole("ADMINISTRADOR"))
                .formLogin(login -> login.permitAll().defaultSuccessUrl("/", true))
                .build();
    }

    @Bean
    UserDetailsService administrador(@Value("${cadastro.admin.usuario}") String usuario,
                                      @Value("${cadastro.admin.senha}") String senha) {
        if (usuario.isBlank() || usuario.length() > 100 || senha.isBlank()) {
            throw new IllegalArgumentException("Configure ADMIN_USUARIO (até 100 caracteres) e ADMIN_SENHA.");
        }
        String[] autoridades = Stream.concat(Stream.of("ROLE_ADMINISTRADOR"),
                Arrays.stream(Operacao.values()).map(Operacao::permissao)).toArray(String[]::new);
        return new InMemoryUserDetailsManager(User.withUsername(usuario)
                .password("{bcrypt}" + new BCryptPasswordEncoder().encode(senha))
                .authorities(autoridades)
                .build());
    }

}
