> Documento histórico. Esta versão completa já incorpora estas alterações. Para instalar, siga o README.md da pasta principal; não aplique os ZIPs antigos.

# Correção de anúncios e dados de teste

Esta atualização altera somente o tratamento do envio dos formulários e acrescenta uma carga opcional de conta/anúncios fictícios no banco. HTML, CSS, aparência, pagamentos e as demais regras foram mantidos.

## Aplicar no projeto que você já está usando

1. Pare o servidor no terminal com **Ctrl+C**.
2. Extraia `GamesRun-correcao-anuncios.zip` **dentro da pasta `GamesRun-Studio`**, a que contém `README.md`, `STUDIO.md` e a subpasta `GamesRun`.
3. Aceite substituir os arquivos existentes. Este pacote não contém `data/`, `application.properties`, HTML ou CSS; não substitui seu banco.
4. No terminal, entre na subpasta `GamesRun`, onde está `mvnw.cmd`.
5. Execute:

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=mock"
```

6. Quando a inicialização terminar, abra `http://localhost:8081/` e use **Ctrl+F5** para carregar o JavaScript corrigido.

A carga grava diretamente no H2 configurado no seu projeto. São registros reais da aplicação com conteúdo fictício, e não exemplos de catálogo mantidos apenas no navegador. A gravação acontece no seu computador ao executar o comando acima; o pacote não pretende acessar remotamente o seu banco.

## Conta criada na primeira execução do perfil mock

| Campo | Valor |
| --- | --- |
| E-mail / nick | `demo@gamesrun.local` |
| Senha | `GamesRun123!` |
| Nome | `Jogador Demo GamesRun` |
| Idade | 18 |

Os oito anúncios ficam vinculados a essa conta. Se ela já existir, será preservada, incluindo qualquer senha que você tenha alterado. A carga não duplica os anúncios existentes com os mesmos títulos nessa conta nem apaga registros seus.

São incluídos Nintendo 3DS XL, PS5 Slim, Game Boy Classic, Nintendo 64, Xbox Series X, DualSense, cartucho retrô e console portátil, com preços ilustrativos e imagens locais.

Depois de gravados, os dados permanecem no H2 em arquivo. Nos próximos usos, você pode voltar ao comando normal:

```powershell
.\mvnw.cmd spring-boot:run
```

## Falha corrigida

Os eventos de envio são recebidos por um listener em `document`. Nesse contexto, `event.currentTarget` é a página, enquanto `event.target` é o formulário enviado. O código usava `currentTarget` para montar `FormData` e chamar `reset()`, o que impedia a publicação e também afetava autenticação/cadastro e o mesmo envio de checkout. Agora esses trechos usam o formulário correto de `event.target`; as outras regras desses fluxos não foram alteradas.

A regressão foi reproduzida antes da correção: a autenticação terminava com `form.reset is not a function` e o envio do anúncio tentava criar `FormData` a partir de `document`.

Para testar um novo anúncio na interface, entre na conta acima e preencha os campos. Caso precise de uma URL de imagem para o teste, você pode usar:

```text
http://localhost:8081/assets/controller.svg
```

## Arquivos da atualização

- `GamesRun/src/main/resources/static/script.js`: correção do formulário de origem do submit.
- `GamesRun/src/main/java/eCommerce/GamesRun/config/DadosMockados.java`: carga opcional no perfil `mock`.
- Testes associados em `GamesRun/frontend-tests/` e `GamesRun/src/test/java/eCommerce/GamesRun/config/`.
- Este guia.

Nenhuma configuração de banco, arquivo de estilo ou página HTML foi alterada. Não foi feito commit ou push.
