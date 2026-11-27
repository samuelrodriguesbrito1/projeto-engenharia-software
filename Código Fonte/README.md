# Cadastro de fornecedores

Projeto de faculdade baseado no diagrama de classes, nos seis diagramas de sequência e no MER. A interface em HTML, CSS e JavaScript é servida pelo mesmo aplicativo Java que atende a API.

## Pastas do projeto

- **Código Fonte** contém o backend Java, os scripts do PostgreSQL e os comandos do Maven.
- **Front-end** fica ao lado de **Código Fonte** e contém a interface HTML, CSS e JavaScript.
- **Diagramas Fornecedor** contém o diagrama de classes, os seis diagramas de sequência e o MER.

Execute os comandos de compilação e execução dentro de **Código Fonte**. Mantenha **Front-end** no mesmo nível para que o Maven inclua a interface no JAR.

## Configurar o PostgreSQL

No PostgreSQL local, crie um usuário e um banco exclusivo para a aplicação. Por exemplo, abra o `psql` com a conta de administração do seu PostgreSQL:

```powershell
psql -h 127.0.0.1 -U postgres -d postgres
```

No `psql`, execute os comandos abaixo. O comando `\password` pede a senha sem colocá-la no histórico SQL.

```sql
CREATE ROLE cadastro_fornecedores LOGIN;
\password cadastro_fornecedores
CREATE DATABASE fornecedores OWNER cadastro_fornecedores;
\q
```

Na pasta do projeto, configure a conexão e o login do administrador no PowerShell:

```powershell
$env:DB_URL = 'jdbc:postgresql://127.0.0.1:5432/fornecedores'
$env:DB_USUARIO = 'cadastro_fornecedores'
$env:DB_SENHA = Read-Host 'Senha do usuário do PostgreSQL' -MaskInput
$env:ADMIN_USUARIO = 'administrador'
$env:ADMIN_SENHA = Read-Host 'Escolha a senha do administrador da aplicação' -MaskInput
```

O exemplo usa PowerShell 7 para ocultar as senhas durante a digitação. Na primeira inicialização, a aplicação cria as tabelas e carrega os cadastros de domínio pelo `schema.sql` e pelo `data.sql`.

## Executar

Se o JAR já estiver na pasta `target`, entre na pasta deste README e rode com Java 17 ou superior:

```powershell
java -jar target/cadastro-fornecedores-1.1.0.jar
```

Abra [Cadastro de fornecedores](http://127.0.0.1:8080) e entre com `ADMIN_USUARIO` e `ADMIN_SENHA`. Use `Ctrl+C` no terminal para encerrar. Se a versão anterior ainda estiver rodando na porta 8080, encerre-a ou acrescente `--server.port=8081` ao comando e abra a porta 8081.

### Compilar pelo código-fonte

Use um **JDK 17 ou superior**. O Maven Wrapper baixa o Maven e as dependências na primeira execução, que requer internet.

```powershell
.\mvnw.cmd package
```

No Linux ou macOS, use `sh mvnw package`. O JAR inclui o servidor web, o driver do PostgreSQL e a interface.

## Como explicar na apresentação

Java implementa as classes do diagrama. O Spring Boot recebe as requisições HTTP e o Spring Security verifica a sessão do administrador. JDBC executa o SQL dos DAOs no PostgreSQL.

| Classe no diagrama | O que faz no projeto |
| --- | --- |
| Telas | Os formulários e a tabela ficam em `../Front-end/`. O JavaScript envia a operação e os dados. |
| `Controller` | Recebe `POST /api/fornecedor` e escolhe o comando. |
| `FornecedorViewHelper` | Transforma os campos do formulário em um `Fornecedor` e o `Resultado` em JSON. |
| `Comandos` e `Fachada` | Cada comando encaminha uma operação. A fachada coordena as regras e a gravação. |
| `IStrategy` | Cada regra tem uma tarefa, como validar o formato do CNPJ, consultar a duplicidade ou definir o status. |
| `FornecedorDAO` e `FornecedorLogDAO` | Executam o SQL para consultar e gravar fornecedores e gravar a auditoria. |

Uma forma de explicar o fluxo do botão **Salvar**:

> O formulário manda os dados para o Controller, que usa o ViewHelper para montar um Fornecedor e chama o SalvarCommand. Na Fachada, as regras validam os dados e definem o status inicial. O DAO grava fornecedor, telefone e endereço e a aplicação registra o log. A gravação e o log ficam na mesma transação. O resultado volta à tela como JSON.

## Roteiro rápido de demonstração

Para a gravação em dupla, siga o [roteiro da apresentação](ROTEIRO_APRESENTACAO.md).

1. Pesquise o CNPJ `11.222.333/0001-81`. Clique em **Novo** quando aparecer a mensagem de que a busca não encontrou fornecedores. O CNPJ pesquisado já estará no formulário. Use razão social `Livros Exemplo Ltda`, nome fantasia `Livros Exemplo`, e-mail `contato@example.com`, telefone `11 3456-7890` e endereço `Rua das Flores, 42, Centro, 01001-000, São Paulo/SP`.
2. Selecione o registro na tabela e clique em **Visualizar**. Depois abra **Alterar**, edite o nome fantasia e salve.
3. Clique em **Inativar**, selecione uma categoria e informe uma justificativa. Na consulta, filtre por **Inativo** e confira o resultado.
4. Selecione o mesmo fornecedor e clique em **Ativar**. Informe a categoria e a justificativa e volte ao filtro **Todos** para vê-lo ativo.

Os registros de auditoria ficam na tabela `fornecedor_log`, com a operação, o horário, o usuário autenticado e os dados de antes e depois da mudança. Ativação e inativação registram a respectiva categoria e justificativa.

Para integrar com a entrada em estoque (RN0086), consulte a visão `fornecedores_ativos_estoque` pelo código do fornecedor. A aplicação cobre o módulo de fornecedores e oferece essa seleção para o módulo de estoque.
