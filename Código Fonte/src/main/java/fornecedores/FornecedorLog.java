package fornecedores;

import java.time.LocalDateTime;

final class FornecedorLog extends EntidadeDominio {
    Long id;
    OperacaoLog operacao;
    LocalDateTime dataHora;
    String usuario;
    String dadosAntes;
    String dadosDepois;
    String justificativa;
    Fornecedor fornecedor;
    CategoriaInativacao categoriaInativacao;
    CategoriaAtivacao categoriaAtivacao;
}
