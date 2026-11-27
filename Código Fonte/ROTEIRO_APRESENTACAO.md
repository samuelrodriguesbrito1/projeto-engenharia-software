# Roteiro da apresentação — Cadastro de Fornecedores

**Samuel Rodrigues Brito e Eduardo França da Silva Filho**

## Antes de gravar

Abra o sistema já autenticado, o PostgreSQL conectado ao mesmo banco e a IDE com os arquivos indicados abaixo. Em um banco de demonstração, cadastre **Aurora** e deixe-a **inativa**. Cadastre **Horizonte** somente durante a gravação. Anote os códigos que o sistema gerar.

| Dados | Aurora, antes do vídeo | Horizonte, durante o vídeo |
| --- | --- | --- |
| Razão social / fantasia | Distribuidora Aurora Ltda / Aurora Livros | Livraria Horizonte Ltda / Horizonte Livros |
| CNPJ | `22.333.444/0001-81` | `11.222.333/0001-81` |
| E-mail | `aurora@example.com` | `horizonte@example.com` |
| Telefone | Comercial, `19`, `34567890` | Comercial, `11`, `34567890` |
| Endereço | Rua das Palmeiras, 21, Centro, 13010-000, Campinas/SP, Brasil | Rua das Flores, 42, Centro, 01001-000, São Paulo/SP, Brasil |

Deixe o complemento em branco nos dois cadastros. Para inativar a Aurora, use **Financeiro** e a justificativa **Preparação da demonstração**.

## 1. Slide inicial — Samuel

Comece o vídeo com o título **Pedido × realizado**, o nome da dupla e esta tabela.

| Pedido | Status | O que falta e por quê |
| --- | --- | --- |
| Consulta, inclusão, visualização, alteração, inativação e ativação | Feito | — |
| RN0081, RN0082 e RN0083 · Campos obrigatórios, endereço e CNPJ único | Feito | — |
| RN0084 e RN0085 · Motivos de inativação e ativação | Feito | — |
| RN0086 · Fornecedor ativo na entrada em estoque | Parcial | A visão lista só os ativos. Falta aplicar a regra na entrada em estoque, pois esse módulo está fora do projeto. |
| RNF0011 · Consulta em até 1 segundo | Parcial | A medição foi local. Falta testar com volume e carga representativos. |
| RNF0012, RNF0013 e RNF0081 · Log, domínios e código único | Feito | — |
| E1, E2, E3 e E4 · Campos vazios, CNPJ repetido, busca vazia e motivo incompleto | Feito | — |
| Facade, Command, Strategy, DAO, View Helper e Front Controller | Feito | — |
| Diagrama de classes | Parcial | O detalhe da conexão precisa refletir o uso do `JdbcTemplate`. |
| Diagramas de sequência | Parcial | O fluxo de escrita precisa mostrar que a confirmação ocorre depois do log. |

**Diga:** “As seis operações estão implementadas. O slide mostra o que ficou parcial. Agora vamos demonstrar o cadastro.”

Confiram os nomes dos padrões com a lista da disciplina antes de montar o slide. A tabela descreve os que aparecem no código.

## 2. Demonstração

### Samuel — Buscar e cadastrar

1. **Busca isolada.** Mostre **Novo** desabilitado. Busque apenas por razão social **Distribuidora Aurora**. Mostre o resultado.
2. **Busca combinada.** Clique em **Mais filtros**. Mantenha a razão social e acrescente **Campinas**, **SP** e **Inativo**. Busque e mostre a Aurora.
3. **Busca vazia e botão Novo.** Volte a **Mais filtros**, limpe os filtros e busque só pelo CNPJ da **Horizonte**. Mostre a mensagem de que nenhum fornecedor foi encontrado e **Novo** habilitado. Digite **São Paulo** no campo **Cidade**, mostre **Novo** desabilitado e busque novamente.
4. **Campos obrigatórios e inclusão.** Clique em **Novo**. Mostre o CNPJ já preenchido. Clique em **Salvar** com os outros campos em branco e mostre o erro. Preencha os dados da Horizonte, salve e mostre o código gerado e o status **Ativo**.

**Passe a fala:** “A Horizonte foi cadastrada. Eduardo vai testar as exceções e mudar o cadastro.”

### Eduardo — Testar, alterar e mostrar o banco

5. **CNPJ repetido.** Abra **Novo** a partir do resultado da Horizonte. Preencha os campos obrigatórios com os mesmos dados e salve. Mostre o código da Horizonte no erro. Troque somente o CNPJ pelo da **Aurora inativa**, salve de novo e mostre o código dela no erro. Clique em **Cancelar**.
6. **Visualizar e alterar.** Selecione a Horizonte e clique em **Visualizar**. Mostre o status e os campos desabilitados. Volte, selecione-a de novo, clique em **Alterar** e troque o nome fantasia para **Horizonte Livros edição**. Salve e mostre o mesmo código e status.
7. **Inativar.** Selecione a Horizonte e clique em **Inativar**. Escolha **Financeiro** e confirme com a justificativa em branco. Mostre o erro. Digite **Pausa no fornecimento**, confirme e mostre **Inativo**.
8. **Ativar.** Selecione a Horizonte e clique em **Ativar**. Digite **Fornecimento regularizado** e confirme sem categoria. Mostre o erro. Escolha **Regularização**, confirme e mostre **Ativo**.
9. **Banco e log.** Execute as consultas no fim do roteiro. Mostre o cadastro e as quatro linhas do log, uma por operação concluída.

**Passe a fala:** “Samuel vai mostrar como a Fachada organiza essas operações.”

## 3. Código da Fachada — Samuel

Abra `Fachada.java`. Mostre os cinco métodos públicos, as listas `regrasAntes` e `regrasDepois` e o método `executar`.

**Diga:** “A Fachada oferece salvar, alterar, inativar, ativar e consultar. Visualizar usa a consulta pelo código. O Controller escolhe o Command, e o Command chama a Fachada. Uma consulta segue direto para o DAO. Em uma escrita, a Fachada roda as regras de antes, chama o DAO e depois roda a regra de log. A gravação e o log são confirmados juntos. Se uma validação falha, a Fachada devolve `Resultado.erro`. O Controller responde com HTTP 400 e a tela mostra a mensagem.”

## 4. Código do DAO — Eduardo

Abra `FornecedorDAO.java`. Mostre `consultar`, `adicionarFiltro` e `salvar`. Em seguida mostre `GerarLog.java` e `FornecedorLogDAO.java`.

**Diga:** “Cada filtro preenchido acrescenta uma condição com `?`. Com vários filtros, as condições são ligadas por AND. Para salvar, o DAO gera o código e insere fornecedor, telefone e endereço. A Fachada controla a transação com `TransactionTemplate`. A estratégia `GerarLog` monta a auditoria e o `FornecedorLogDAO` grava os dados anteriores e posteriores, o usuário, o horário e o motivo quando houver.”

## 5. Nova regra hipotética — Samuel

Abra `IStrategy.java` e aponte `regrasAntes` em `Fachada.java`.

**Diga:** “Para validar os dígitos verificadores, eu criaria `ValidarDigitosVerificadoresCnpj` como uma Strategy. Ela usaria o CNPJ já normalizado e devolveria uma mensagem se encontrasse erro. Eu a registraria em `SALVAR` e `ALTERAR`, depois da validação do formato e antes da verificação de duplicidade. Como a Fachada já executa estratégias pelo mesmo contrato, Controller, Commands, View Helper, DAOs e telas continuariam com o código atual. Esta regra é apenas o exemplo da apresentação.”

## Consultas SQL para o passo 9

Troque `F0002` pelo código real da Horizonte.

```sql
SELECT f.codigo, f.nome_fantasia, f.status, t.ddd, t.numero AS telefone,
       e.logradouro, e.cidade
FROM fornecedor f
JOIN telefone t ON t.fornecedor_codigo = f.codigo
JOIN endereco e ON e.fornecedor_codigo = f.codigo
WHERE f.codigo = 'F0002';

SELECT operacao, data_hora, usuario, justificativa,
       COALESCE(ci.nome, ca.nome) AS categoria,
       dados_antes::jsonb ->> 'fantasyName' AS nome_antes,
       dados_depois::jsonb ->> 'fantasyName' AS nome_depois,
       dados_antes::jsonb ->> 'status' AS status_antes,
       dados_depois::jsonb ->> 'status' AS status_depois
FROM fornecedor_log l
LEFT JOIN dominio ci ON ci.id = l.categoria_inativacao_id
LEFT JOIN dominio ca ON ca.id = l.categoria_ativacao_id
WHERE l.fornecedor_codigo = 'F0002'
ORDER BY l.id;
```

## Entrega

Gravem os cinco blocos na ordem, com os dois falando e com a tela, o código e o áudio legíveis. Gravem a demonstração inteira, inclusive os erros esperados. Testem o acesso ao vídeo no YouTube não listado ou no Drive. Coloquem `link-video.txt` e as pastas **Código Fonte**, **Front-end** e **Diagramas Fornecedor**, incluindo `schema.sql` e `data.sql`, no ZIP `Samuel_Rodrigues_Brito_Eduardo_Franca_da_Silva_Filho.zip`.
