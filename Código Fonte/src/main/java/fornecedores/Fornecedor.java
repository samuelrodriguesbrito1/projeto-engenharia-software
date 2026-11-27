package fornecedores;

class Fornecedor extends EntidadeDominio {
    String codigo;
    String razaoSocial;
    String nomeFantasia;
    String cnpj;
    String email;
    StatusFornecedor status;
    String usuario;
    Fornecedor estadoAnterior;
    Telefone telefone;
    Endereco endereco;
    MotivoStatus motivoStatus;
}
