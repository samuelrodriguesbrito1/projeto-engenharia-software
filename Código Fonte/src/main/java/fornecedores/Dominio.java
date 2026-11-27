package fornecedores;

abstract class Dominio extends EntidadeDominio {
    final Integer id;
    final String chave;
    final String nome;

    Dominio(Integer id, String chave, String nome) {
        this.id = id;
        this.chave = chave;
        this.nome = nome;
    }
}

final class TipoTelefone extends Dominio {
    TipoTelefone(Integer id, String chave, String nome) { super(id, chave, nome); }
}

final class TipoLogradouro extends Dominio {
    TipoLogradouro(Integer id, String chave, String nome) { super(id, chave, nome); }
}

final class Pais extends Dominio {
    Pais(Integer id, String chave, String nome) { super(id, chave, nome); }
}

final class CategoriaInativacao extends Dominio {
    CategoriaInativacao(Integer id, String chave, String nome) { super(id, chave, nome); }
}

final class CategoriaAtivacao extends Dominio {
    CategoriaAtivacao(Integer id, String chave, String nome) { super(id, chave, nome); }
}
