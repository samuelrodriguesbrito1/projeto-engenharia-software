package fornecedores;

import java.util.List;

record Resultado(String mensagem, boolean sucesso, List<EntidadeDominio> entidades) {
    static Resultado erro(String mensagem) {
        return new Resultado(mensagem, false, List.of());
    }

    static Resultado sucesso(String mensagem, List<? extends EntidadeDominio> entidades) {
        return new Resultado(mensagem, true, List.copyOf(entidades));
    }
}
