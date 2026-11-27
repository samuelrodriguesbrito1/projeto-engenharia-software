package fornecedores;

import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
class Controller {
    private final Map<Operacao, ICommand> commands;
    private final Map<String, IViewHelper> viewHelpers;
    private final DominioViewHelper dominios;

    Controller(IFachada fachada, FornecedorViewHelper fornecedores, DominioViewHelper dominios) {
        this.dominios = dominios;
        commands = Map.of(
                Operacao.SALVAR, new SalvarCommand(fachada),
                Operacao.ALTERAR, new AlterarCommand(fachada),
                Operacao.INATIVAR, new InativarCommand(fachada),
                Operacao.ATIVAR, new AtivarCommand(fachada),
                Operacao.CONSULTAR, new ConsultarCommand(fachada));
        viewHelpers = Map.of("fornecedor", fornecedores, "dominio", dominios);
    }

    @PostMapping(value = "/api/fornecedor", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> handle(@RequestParam Map<String, String> parametros, Authentication autenticacao) {
        IViewHelper viewHelper = viewHelpers.get("fornecedor");
        Resultado resultado;
        Operacao operacao;
        try {
            operacao = Operacao.valueOf(parametros.getOrDefault("operacao", ""));
        } catch (IllegalArgumentException erro) {
            return ResponseEntity.badRequest().body(viewHelper.getView(Resultado.erro("Operação inválida.")));
        }

        if (autenticacao.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals(operacao.permissao()))) {
            return ResponseEntity.status(403).body(viewHelper.getView(Resultado.erro("Operação sem permissão.")));
        }

        try {
            Fornecedor fornecedor = (Fornecedor) viewHelper.getEntidade(parametros);
            fornecedor.usuario = autenticacao.getName();
            resultado = commands.get(operacao).executar(fornecedor);
        } catch (IllegalArgumentException erro) {
            resultado = Resultado.erro(erro.getMessage());
        }
        return ResponseEntity.status(resultado.sucesso() ? 200 : 400).body(viewHelper.getView(resultado));
    }

    @GetMapping(value = "/api/dominios", produces = MediaType.APPLICATION_JSON_VALUE)
    public String catalogos() {
        return dominios.catalogos();
    }

    @GetMapping(value = "/api/usuario", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> usuario(Authentication autenticacao, CsrfToken csrf) {
        return Map.of("nome", autenticacao.getName(), "perfil", "Administrador",
                "csrfToken", csrf.getToken(), "csrfHeader", csrf.getHeaderName());
    }
}
