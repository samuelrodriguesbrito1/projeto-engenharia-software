package fornecedores;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
record Conexao(JdbcTemplate jdbc) {
}
