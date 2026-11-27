export const resultado = `
<header>Cadastro de Fornecedores</header>
<main class="resultado-main">
    <section class="resultado-filtros" aria-label="Filtros de fornecedores">
        <form>
            <div class="resultado-filtro-principal">
                <label for="company">Razão Social:</label>
                <input type="text" id="company" name="company" maxlength="100">
            </div>

            <div class="resultado-status">
                <label for="status">Status:</label>
                <select id="status" name="status">
                    <option value="">Todos</option>
                    <option value="ATIVO">Ativo</option>
                    <option value="INATIVO">Inativo</option>
                </select>
            </div>

            <button class="search-button" type="submit">Buscar</button>
        </form>

        <nav class="resultado-acoes" aria-label="Ações de fornecedor">
            <button type="button" data-acao="buscar">Mais filtros</button>
            <button type="button" data-acao="novo" disabled>Novo</button>
            <button type="button" data-acao="visualizar" disabled>Visualizar</button>
            <button type="button" data-acao="alterar" disabled>Alterar</button>
            <button type="button" data-acao="inativar" disabled>Inativar</button>
            <button type="button" data-acao="ativar" disabled>Ativar</button>
        </nav>
    </section>

    <section class="resultado-tabela" aria-label="Fornecedores encontrados">
        <table>
            <thead>
                <tr>
                    <th scope="col">Selecionar</th>
                    <th scope="col">Código</th>
                    <th scope="col">Razão Social</th>
                    <th scope="col">Nome Fantasia</th>
                    <th scope="col">CNPJ</th>
                    <th scope="col">Cidade/UF</th>
                    <th scope="col">Status</th>
                </tr>
            </thead>
            <tbody></tbody>
        </table>
    </section>

    <section class="resultado-usuario">
        <fieldset>
            <legend>Usuário</legend>
            <p data-usuario></p>
        </fieldset>
    </section>
</main>
<footer></footer>
`;
