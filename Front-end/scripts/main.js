import { search } from "./search.js";
import { resultado } from "./resultado.js";
import { cadastro } from "./cadastro.js";
import { inativar } from "./inativar.js";

const main = document.getElementById("main");
const mensagem = document.getElementById("mensagem");
let filtros = {};
let mensagemPendente = "";
let catalogos;
let usuario;
let buscaConcluida = false;
let buscaSemResultados = false;
let resultadosRecentes;

async function requisicao(url, opcoes) {
    let resposta;
    try {
        resposta = await fetch(url, opcoes);
    } catch {
        throw new Error("Não foi possível conectar ao servidor Java.");
    }
    if (resposta.status >= 500) {
        throw new Error("O servidor encontrou um erro. Tente novamente.");
    }
    if (resposta.redirected && new URL(resposta.url).pathname === "/login") {
        window.location.assign("/login");
        throw new Error("Entre novamente para continuar.");
    }
    if (!resposta.headers.get("content-type")?.includes("application/json")) {
        throw new Error("Não foi possível concluir a operação. Atualize a página e tente novamente.");
    }
    const dados = await resposta.json();
    if (!resposta.ok || dados.sucesso === false) {
        throw new Error(dados.mensagem || "Não foi possível concluir a operação.");
    }
    return dados;
}

function carregarUsuario() {
    usuario ??= requisicao("/api/usuario").catch(erro => {
        usuario = undefined;
        throw erro;
    });
    return usuario;
}

async function operar(operacao, campos = {}) {
    const { csrfHeader, csrfToken } = await carregarUsuario();
    return requisicao("/api/fornecedor", {
        method: "POST",
        headers: { [csrfHeader]: csrfToken },
        body: new URLSearchParams({ ...campos, operacao })
    });
}

async function carregarCatalogos() {
    catalogos ??= await requisicao("/api/dominios");
    return catalogos;
}

function exibirMensagem(texto, erro = false) {
    mensagem.textContent = texto;
    mensagem.hidden = !texto;
    mensagem.classList.toggle("mensagem-erro", erro);
}

function irPara(tela, codigo = "") {
    window.location.hash = codigo ? `#/${tela}/${encodeURIComponent(codigo)}` : `#/${tela}`;
}

function preencherFormulario(form, dados) {
    for (const [chave, valor] of Object.entries(dados)) {
        const campo = form.elements.namedItem(chave);
        if (campo) {
            campo.value = valor ?? "";
        }
    }
}

function preencherSelect(select, opcoes, vazio) {
    select.replaceChildren();
    if (vazio !== undefined) {
        select.add(new Option(vazio, ""));
    }
    for (const { chave, nome } of opcoes) {
        select.add(new Option(nome, chave));
    }
}

function campos(form) {
    return Object.fromEntries(new FormData(form));
}

function configurarConsulta(tela, prepararFiltros, exibirResultado) {
    const form = tela.querySelector("form");
    const novo = tela.querySelector('[data-acao="novo"]');
    const buscar = form.querySelector('button[type="submit"]');
    const avisoVazio = tela.querySelector("[data-resultado-vazio]");

    function invalidarBusca() {
        buscaConcluida = false;
        buscaSemResultados = false;
        novo.disabled = true;
        if (avisoVazio) avisoVazio.hidden = true;
    }

    async function consultar() {
        const dadosFormulario = campos(form);
        invalidarBusca();
        buscar.disabled = true;
        try {
            const parametros = prepararFiltros(dadosFormulario);
            const resposta = await operar("CONSULTAR", parametros);
            if (!tela.isConnected || JSON.stringify(dadosFormulario) !== JSON.stringify(campos(form))) return;
            filtros = parametros;
            buscaConcluida = true;
            buscaSemResultados = resposta.entidades.length === 0;
            novo.disabled = false;
            exibirResultado(resposta.entidades);
        } catch (erro) {
            if (tela.isConnected) exibirMensagem(erro.message, true);
        } finally {
            buscar.disabled = false;
        }
    }

    novo.onclick = () => irPara("cadastro");
    form.addEventListener("input", invalidarBusca);
    form.addEventListener("submit", evento => {
        evento.preventDefault();
        if (buscar.disabled) return;
        exibirMensagem("");
        consultar();
    });
    novo.disabled = !buscaConcluida;
    buscar.disabled = false;
    return consultar;
}

async function configurarBusca(tela) {
    const form = tela.querySelector("form");
    const opcoes = await carregarCatalogos();
    preencherSelect(form.elements.state, opcoes.state, "Todos");
    preencherFormulario(form, filtros);
    const avisoVazio = tela.querySelector("[data-resultado-vazio]");
    avisoVazio.hidden = !buscaSemResultados;
    configurarConsulta(tela, dados => dados, fornecedores => {
        if (!fornecedores.length) {
            avisoVazio.hidden = false;
            return;
        }
        resultadosRecentes = fornecedores;
        irPara("resultado");
    });
}

function formatarCnpj(cnpj) {
    return cnpj.replace(/^(.{2})(.{3})(.{3})(.{4})(.{2})$/, "$1.$2.$3/$4-$5");
}

function preencherTabela(tela, fornecedores) {
    const corpo = tela.querySelector("tbody");
    for (const fornecedor of fornecedores) {
        const linha = corpo.insertRow();
        const selecionar = document.createElement("input");
        selecionar.type = "radio";
        selecionar.name = "fornecedorSelecionado";
        selecionar.setAttribute("aria-label", `Selecionar ${fornecedor.code}`);
        linha.insertCell().append(selecionar);
        selecionar.addEventListener("change", () => {
            corpo.querySelector(".selecionado")?.classList.remove("selecionado");
            linha.classList.add("selecionado");
            for (const acao of ["visualizar", "alterar", "inativar", "ativar"]) {
                const botao = tela.querySelector(`[data-acao="${acao}"]`);
                botao.disabled = (acao === "inativar" && fornecedor.status !== "ATIVO")
                    || (acao === "ativar" && fornecedor.status !== "INATIVO");
                botao.onclick = () => irPara(acao === "alterar" ? "cadastro" : acao, fornecedor.code);
            }
        });

        const valores = [
            fornecedor.code, fornecedor.company, fornecedor.fantasyName, formatarCnpj(fornecedor.cnpj),
            `${fornecedor.city}/${fornecedor.state}`, fornecedor.status === "ATIVO" ? "Ativo" : "Inativo"
        ];
        for (const valor of valores) {
            linha.insertCell().textContent = valor;
        }
    }
}

async function configurarResultado(tela) {
    const form = tela.querySelector("form");
    preencherFormulario(form, filtros);
    tela.querySelector('[data-acao="buscar"]').onclick = () => {
        buscaConcluida = false;
        buscaSemResultados = false;
        irPara("buscar");
    };
    const consultar = configurarConsulta(tela, dados => ({ ...filtros, ...dados }), fornecedores => {
        if (!fornecedores.length) {
            mensagemPendente = mensagem.textContent;
            irPara("buscar");
            return;
        }
        tela.querySelector("tbody").replaceChildren();
        for (const acao of ["visualizar", "alterar", "inativar", "ativar"]) {
            tela.querySelector(`[data-acao="${acao}"]`).disabled = true;
        }
        preencherTabela(tela, fornecedores);
    });
    if (resultadosRecentes) {
        preencherTabela(tela, resultadosRecentes);
        resultadosRecentes = undefined;
    } else {
        await consultar();
    }
}

async function buscarFornecedor(codigo) {
    const resposta = await operar("CONSULTAR", { code: codigo });
    const fornecedor = resposta.entidades[0];
    if (!fornecedor) {
        throw new Error("Fornecedor não encontrado.");
    }
    return fornecedor;
}

function configurarEnvio(form, operacao, codigo) {
    const enviar = form.querySelector('button[type="submit"]');
    // A resposta do servidor lista todos os campos pendentes de uma vez.
    form.noValidate = true;
    enviar.disabled = false;
    form.addEventListener("submit", async evento => {
        evento.preventDefault();
        if (enviar.disabled) return;
        enviar.disabled = true;
        exibirMensagem("");
        try {
            const resposta = await operar(operacao, { ...campos(form), code: codigo });
            if (form.isConnected) {
                mensagemPendente = resposta.mensagem;
                irPara("resultado");
            }
        } catch (erro) {
            if (form.isConnected) exibirMensagem(erro.message, true);
        } finally {
            enviar.disabled = false;
        }
    });
}

async function configurarCadastro(tela, codigo, visualizar) {
    const form = tela.querySelector("form");
    const voltar = form.querySelector('button[type="button"]');
    voltar.onclick = () => irPara("resultado");
    if (visualizar) {
        voltar.textContent = "Voltar";
        form.querySelector('button[type="submit"]').remove();
        for (const fieldset of form.querySelectorAll("fieldset")) fieldset.disabled = true;
    }
    const titulo = visualizar ? "Visualizar Fornecedor" : codigo ? "Alterar Fornecedor" : "Novo Fornecedor";
    tela.querySelector("header").textContent = `Cadastro de Fornecedores  |  ${titulo}`;

    const [opcoes, fornecedor] = await Promise.all([carregarCatalogos(), codigo ? buscarFornecedor(codigo) : null]);
    preencherSelect(form.elements.phoneType, opcoes.phoneType);
    preencherSelect(form.elements.streetType, opcoes.streetType);
    preencherSelect(form.elements.state, opcoes.state, "Selecione");
    preencherSelect(form.elements.country, opcoes.country);

    if (fornecedor) {
        preencherFormulario(form, { ...fornecedor, cnpj: formatarCnpj(fornecedor.cnpj),
            status: fornecedor.status === "ATIVO" ? "Ativo" : "Inativo" });
        if (visualizar) tela.querySelector("[data-status]").hidden = false;
    } else {
        form.elements.cnpj.value = filtros.cnpj || "";
    }
    if (!visualizar) {
        configurarEnvio(form, codigo ? "ALTERAR" : "SALVAR", codigo);
    }
}

async function configurarStatus(tela, codigo, ativar) {
    const form = tela.querySelector("form");
    form.querySelector('button[type="button"]').onclick = () => irPara("resultado");
    const titulo = ativar ? "Ativar Fornecedor" : "Inativar Fornecedor";
    tela.querySelector("header").textContent = `Cadastro de Fornecedores  |  ${titulo}`;

    const [opcoes, fornecedor] = await Promise.all([carregarCatalogos(), buscarFornecedor(codigo)]);
    tela.querySelector(".inativar-fornecedor strong").textContent =
        `Fornecedor: ${fornecedor.code} — ${fornecedor.company}`;
    if (fornecedor.status !== (ativar ? "INATIVO" : "ATIVO")) {
        throw new Error(ativar ? "O fornecedor já está ativo." : "O fornecedor já está inativo.");
    }
    preencherSelect(form.elements.category, ativar ? opcoes.activation : opcoes.inactivation, "Selecione");
    configurarEnvio(form, ativar ? "ATIVAR" : "INATIVAR", codigo);
}

async function renderRoute() {
    const [rota, codigo = ""] = window.location.hash.slice(2).split("/");
    const telas = { buscar: search, resultado, cadastro, visualizar: cadastro, inativar, ativar: inativar };
    const tela = document.createElement("div");
    tela.innerHTML = Object.hasOwn(telas, rota) ? telas[rota] : telas.buscar;
    main.replaceChildren(tela);
    exibirMensagem(mensagemPendente);
    mensagemPendente = "";

    try {
        const codigoFornecedor = decodeURIComponent(codigo);
        const operador = await carregarUsuario();
        if (!tela.isConnected) return;
        tela.querySelector("[data-usuario]").textContent = `${operador.nome} / ${operador.perfil}`;
        switch (rota) {
            case "resultado":
                await configurarResultado(tela);
                break;
            case "cadastro":
            case "visualizar":
                if (rota === "visualizar" && !codigoFornecedor) {
                    irPara("resultado");
                    return;
                }
                if (!codigoFornecedor && !buscaConcluida) {
                    irPara("buscar");
                    return;
                }
                await configurarCadastro(tela, codigoFornecedor, rota === "visualizar");
                break;
            case "inativar":
            case "ativar":
                if (!codigoFornecedor) {
                    irPara("resultado");
                    return;
                }
                await configurarStatus(tela, codigoFornecedor, rota === "ativar");
                break;
            default:
                await configurarBusca(tela);
        }
    } catch (erro) {
        if (tela.isConnected) exibirMensagem(erro.message, true);
    }
}

window.addEventListener("hashchange", renderRoute);
renderRoute();
