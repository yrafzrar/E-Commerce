# GamesRun Studio consolidado

O pacote completo inclui `GamesRun/pom.xml`, os dois Maven Wrappers, `.mvn/wrapper/maven-wrapper.properties`, todas as classes Java e testes, `application.properties`, HTML, CSS, JavaScript e imagens locais. Não precisa aplicar patches anteriores. A configuração de banco e o visual Studio foram preservados.

## O que está integrado

- Login e criação de conta usando a API e sessão HTTP. A conta de exemplo é criada com o perfil `mock`.
- Fotos locais de anúncios: seleção de JPG/PNG de até 5 MB, prévia, upload autenticado, validação do conteúdo e armazenamento permanente fora de `target/`. O uso de link continua disponível.
- Publicação de anúncios: o listener usa o formulário de `event.target`, e a API associa o vendedor à conta autenticada.
- Favoritos no banco, separados por usuário. A interface limpa a lista ao sair e recarrega apenas a lista da conta ao entrar. O coração fica vermelho depois de salvar; sem login, a ação abre a entrada de conta. A lista antiga compartilhada do navegador é descartada por não identificar o dono dos itens.
- Exclusão de anúncios pelo criador, com confirmação na interface e validação no servidor. Outro vendedor não pode excluir ou transferir o anúncio. A exclusão remove referências de carrinhos e favoritos; o histórico textual dos pedidos é preservado.
- Carrinho de visitante, conciliação com a conta no login, quantidades, checkout e histórico de pedidos.
- Simulação de Pix, cartão e boleto. O Pix gera QR e código aleatórios correspondentes, com cópia e regeneração; o cartão recebe dados de teste; o boleto tem código de barras e linha fictícios. A confirmação grava `PAGAMENTO_SIMULADO`, sem cobrança. A tela usa nomes normais de checkout e um aviso discreto de ambiente de teste no rodapé; o histórico mostra “Pagamento confirmado”.

Cartão de teste: **4242 4242 4242 4242** ou **5555 5555 5555 4444**, nome fictício, validade futura como **12/30**, CVV **123**. Número, nome e CVV ficam apenas no formulário e não são enviados ao backend. Novos códigos de Pix e boleto começam com `GAMESRUN-` e não são pagamentos bancários válidos.

A conta de demonstração é `demo@gamesrun.local`, senha `GamesRun123!`, salvo se você alterou essa senha no banco antigo. O perfil `mock` preserva a conta existente e não duplica a carga após a primeira execução desta versão.

## Rodar e verificar

Siga [README.md](README.md) para iniciar, importar na IDE ou copiar o banco antigo. O comando de compilação limpa e testes é, dentro de `GamesRun`:

```powershell
.\mvnw.cmd clean verify
```

Node é opcional, usado somente nos testes de lógica do frontend:

```powershell
node frontend-tests/studio.test.cjs
```

O roteiro `frontend-tests/studio-live.cjs` exige um servidor com **banco novo em memória**, sem o perfil `mock`. Em um terminal na pasta `GamesRun`:

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--server.port=18081 --spring.datasource.url=jdbc:h2:mem:studio-smoke --spring.jpa.hibernate.ddl-auto=create-drop --spring.h2.console.enabled=false"
```

Em outro terminal na mesma pasta, execute `node frontend-tests/studio-live.cjs`. Reinicie o banco em memória antes de repetir. Não use esse roteiro no banco com seus dados.

## Banco e limites existentes

O H2 usa `GamesRun/data/gamesrun.mv.db`, porta 8081 e `ddl-auto=update`. Os testes Java usam H2 em memória separado. Fotos são salvas em `GamesRun/uploads/anuncios`; preserve essa pasta junto com `GamesRun/data` ao trocar de instalação. `application.properties` passou a incluir limites de upload de 5 MB por arquivo e 6 MB por requisição, sem alterar a URL do banco ou a porta. Não foram incluídos bancos pessoais, fotos enviadas durante testes, caches compilados ou configurações antigas de IDE.

O frete demonstrativo continua em R$ 25. A escolha de pagamento é uma simulação acadêmica; não há integração com um banco ou gateway. As dicas do chat continuam sendo uma demonstração local por palavras-chave. As rotas legadas e o armazenamento de senhas mantêm os limites do backend acadêmico; este pacote não transforma o projeto em um sistema de pagamentos de produção.

## Entrega

O código não foi enviado por commit ou push. A compilação, os testes e a inicialização foram verificados no ambiente Java 17 desta entrega. Veja [VALIDACAO.md](VALIDACAO.md) para os resultados. O iniciador Windows foi incluído, mas não houve execução em Windows ou inspeção visual automatizada em navegador.
