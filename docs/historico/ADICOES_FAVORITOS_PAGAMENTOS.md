> Documento histórico. Esta versão completa já incorpora estas alterações. Para instalar, siga o README.md da pasta principal; não aplique os ZIPs antigos.

# Favoritos, exclusão do vendedor e pagamentos simulados

Este pacote é uma atualização para o GamesRun Studio com a correção de anúncios já aplicada. Não inclui nem substitui o arquivo do banco. A página inicial, o catálogo e os demais componentes mantêm o modelo Studio; os estilos novos foram acrescentados ao final do CSS.

## Aplicar

1. Pare o servidor com **Ctrl+C**.
2. Extraia `GamesRun-favoritos-exclusao-pagamentos.zip` dentro de **`GamesRun-Studio`**, a pasta que contém a subpasta `GamesRun`. Aceite substituir os arquivos.
3. Na subpasta `GamesRun`, inicie como antes:

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=mock"
```

Você também pode iniciar sem o perfil `mock` se os exemplos já estiverem no banco. A primeira compilação baixa a biblioteca ZXing usada para gerar os códigos de teste.

4. Abra `http://localhost:8081/`, pressione **Ctrl+F5** e **entre novamente na conta**. A nova autorização usa uma sessão no servidor; o login anterior guardado apenas no navegador não basta.

A conta de demonstração continua sendo `demo@gamesrun.local`, senha `GamesRun123!`, salvo se você tiver alterado a senha no banco. Não foi feito commit ou push.

## Favoritos

O coração fica preenchido de vermelho quando o produto é favoritado e volta ao contorno original quando é removido. Isso vale tanto para o card quanto para os detalhes do produto. Os favoritos agora são gravados na tabela `favorito` do banco, vinculados ao usuário autenticado. A lista é carregada no login e ao recarregar a página. Ao sair da conta, a lista e o contador exibidos são limpos, sem apagar os favoritos salvos daquela conta. Outra conta recebe somente a própria lista. Favoritar sem login abre a entrada de conta.

A antiga lista compartilhada do navegador é descartada porque não registrava quem salvou cada item. Depois de atualizar, marque novamente os produtos que deseja manter nos favoritos. Não há importação automática para evitar atribuir a uma conta os favoritos de outra pessoa.

`GET /api/favoritos`, `PUT /api/favoritos/{anuncioId}` e `DELETE /api/favoritos/{anuncioId}` usam exclusivamente a sessão do servidor para identificar a conta. Um `usuarioId` enviado pelo navegador não escolhe o dono da lista. A chave composta impede duplicar o mesmo favorito por conta. Requisições que terminem depois do logout não repõem os corações da conta anterior.

## Exclusão do anúncio

O vendedor encontra **Excluir anúncio** nos detalhes dos próprios produtos e na seção **Meus anúncios**, abaixo do formulário de publicação. Há uma confirmação antes da exclusão.

A API verifica o ID da conta na sessão de login. Um visitante recebe HTTP 401 e outro vendedor recebe HTTP 403, inclusive tentando forjar `vendedorId` na requisição. Criação e atualização de anúncios também usam o proprietário autenticado; não permitem transferir a propriedade ou sobrescrever outro anúncio enviando um ID no corpo. Alterações de conta exigem que a sessão seja da própria conta, para não permitir obter a identidade de outro vendedor por essa rota.

Ao excluir, o anúncio sai do banco e suas referências são removidas dos carrinhos e favoritos de todas as contas para não violar as chaves estrangeiras. Pedidos já registrados preservam o histórico textual dos itens. A carga `mock` grava um marcador no banco para que os exemplos excluídos não sejam recriados no próximo início.

## Pagamentos: somente simulação

Registrar o pedido leva à tela do pagamento escolhido. Um pedido pendente no histórico também tem a opção **Simular pagamento**.

| Método | Experiência |
| --- | --- |
| Pix | QR code legível, código aleatório correspondente, botão para copiar, opção de gerar outro e confirmação manual do pagamento fictício. |
| Cartão | Nome, número, validade e CVV de teste, validação local e confirmação da compra fictícia. |
| Boleto | Valor ilustrativo, vencimento, código de barras e linha fictícia aleatória, botão para copiar e confirmação manual. |

Para cartão, use **4242 4242 4242 4242** ou **5555 5555 5555 4444**, um nome fictício, validade futura como **12/30** e CVV de teste como **123**. A interface recusa outros números para evitar o uso de um cartão real.

Os campos de cartão ficam somente no formulário temporário. Não são enviados à API nem salvos em localStorage, sessionStorage ou banco. A confirmação envia apenas o identificador aleatório da simulação. Ao concluir, o formulário é removido da tela.

Os códigos começam com `SIMULACAO-GAMESRUN-`; não são Pix ou boletos bancários válidos. O QR é um QR real do código fictício e o código de barras do boleto é ilustrativo, sem valor de pagamento. Nenhum banco, emissor ou gateway é acionado e nenhum dinheiro é movimentado.

A confirmação grava **`PAGAMENTO_SIMULADO`** no pedido, com data da simulação, e mostra um comprovante claramente fictício. Repetir a confirmação não gera outro pedido nem muda a data já confirmada. Gerar outro código invalida o anterior. Somente o comprador autenticado pode abrir ou confirmar o pagamento do seu pedido.

## Alterações técnicas

- CSS: estado vermelho dos favoritos e estilos das novas seções.
- JavaScript: ações de exclusão, sessão, tela e confirmação dos três pagamentos simulados.
- Sessão HTTP: criada no cadastro/login e invalidada no logout.
- Backend: autorização de propriedade dos anúncios, remoção transacional e endpoints de simulação.
- Favoritos: tabela por usuário e anúncio; leitura e alterações autenticadas, sem armazenamento da lista no navegador.
- Banco: novas colunas para código/data da simulação e controle da carga de exemplos. A atualização é feita pelo `ddl-auto=update` já usado no projeto. Não foi alterado o `application.properties`.
- Maven: `com.google.zxing:core:3.5.3` para codificar QR e código de barras, sem serviços externos.

## Verificação

Passaram **24 testes Java**, incluindo tentativa de exclusão por outro vendedor, IDs forjados, limpeza de referências, preservação do histórico, autorização do comprador, regeneração/invalidação de códigos, confirmação idempotente e logout, isolamento de favoritos entre contas, permanência após novo login, operações repetidas e limpeza após excluir um anúncio. Um teste decodifica o QR gerado e confere o conteúdo contra o código exibido.

Passaram **25 verificações do frontend** com DOM e API simulados, incluindo vermelho dos favoritos, botão somente para o dono, recusa de cartão fora dos exemplos e ausência de dados de cartão na requisição, limpeza de favoritos no logout, falhas da API e respostas atrasadas após sair da conta.

O roteiro com HTTP real realizou **50 chamadas do frontend** contra o Spring Boot e H2 em memória, completando Pix, cartão e boleto como simulados e excluindo um anúncio com a conta proprietária. O mesmo roteiro usou duas contas para conferir favoritos independentes e a recuperação dos favoritos da primeira conta após novo login. Não houve inspeção visual automatizada em navegador nesta sessão.

Para repetir, na pasta `GamesRun`:

```powershell
.\mvnw.cmd test
node frontend-tests/studio.test.cjs
```

O roteiro `frontend-tests/studio-live.cjs` usa por padrão um servidor de teste na porta 18081 com banco **novo em memória**, sem o perfil `mock`, conforme as orientações de testes anteriores. Não use esse roteiro na sua base com dados reais.
