# GamesRun Studio — projeto completo atualizado

Este pacote reúne o backend Java, o frontend Studio e todas as atualizações anteriores, incluindo envio de fotos locais e a nova apresentação do pagamento. Não é um patch e não exige os ZIPs anteriores.

## Iniciar no Windows

1. Pare o servidor anterior com **Ctrl+C**.
2. Extraia **GamesRun-Studio-completo-atualizado.zip** em **uma pasta nova**. Não misture com arquivos compilados ou configurações de IDE da pasta antiga.
3. Instale/configure um **JDK 17 ou superior**. É necessário acesso à internet no primeiro uso para o Maven baixar dependências.
4. Na pasta `GamesRun-Studio`, execute **iniciar.cmd**. Também pode abrir PowerShell nessa pasta e digitar `.\iniciar.cmd`.
5. Aguarde a mensagem `Started GamesRun`, abra **http://localhost:8081/** e pressione **Ctrl+F5**. Faça login novamente.

O iniciador entra na subpasta certa e faz uma compilação limpa antes de iniciar. `clean` remove somente arquivos compilados de `target/`; o banco em `data/` é mantido.

**Conta de exemplo:** `demo@gamesrun.local` · **Senha:** `GamesRun123!`.
O perfil `mock` cria essa conta e oito anúncios fictícios no seu primeiro uso. Não recria os anúncios excluídos nas execuções seguintes.

## Preservar os dados do projeto anterior

Com o servidor antigo e o novo **parados**, faça uma cópia de segurança da pasta antiga `GamesRun/data`. Depois copie essa pasta para `GamesRun-Studio/GamesRun/data` no projeto novo **antes de iniciá-lo**. A configuração de H2 em arquivo foi preservada. Não copie `target/`, `.idea/` ou `.vscode/` da versão antiga.

Se já tiver enviado fotos em uma versão com upload, copie também `GamesRun/uploads` para a mesma pasta no projeto novo; o banco guarda o endereço das imagens e os arquivos ficam nessa pasta.

Se não copiar o banco anterior, o projeto cria um banco novo com a conta e os anúncios de exemplo. Este ZIP não inclui o banco pessoal do seu computador.

## Foto do produto e pagamento

Ao criar o anúncio, clique em **Escolher arquivo** no campo **Foto do produto**. Aceita **JPG/PNG até 5 MB**, com prévia antes de publicar. É possível remover/trocar o arquivo ou continuar usando um link. O upload exige login; o arquivo fica em `GamesRun/uploads/anuncios` e seu endereço é gravado no anúncio. As imagens continuam disponíveis após reiniciar ou executar `clean`. O limite de dimensões é de 20 megapixels.

O checkout agora usa títulos e botões como **Pagamento**, **Dados do cartão**, **Confirmar compra** e **Confirmar pagamento**, sem os grandes avisos de simulação. Um rodapé discreto identifica o ambiente de teste. Os pagamentos continuam sem cobrança real. No cartão, o botão **Usar dados de exemplo** preenche os campos para você concluir o fluxo.

## Abrir na IDE

Abra **GamesRun/pom.xml como projeto Maven**, configure o JDK 17 ou superior e aguarde a importação das dependências. Não adicione arquivos avulsos das atualizações anteriores. O código principal está em `GamesRun/src/main/java`; a página está em `GamesRun/src/main/resources/static`.

Para executar manualmente, abra PowerShell **dentro de GamesRun**, onde estão `pom.xml` e `mvnw.cmd`:

```powershell
.\mvnw.cmd clean spring-boot:run "-Dspring-boot.run.profiles=mock"
```

Linux/macOS, na pasta principal: `sh iniciar.sh`.

Os detalhes das funcionalidades e dos testes estão em [STUDIO.md](STUDIO.md). Os documentos em `docs/historico` registram as etapas anteriores; suas instruções de aplicar patches foram substituídas por este pacote completo.

---

# GamesRun 
GamesRun é uma plataforma de compra e venda de produtos relacionados ao universo gamer, como jogos, consoles e acessórios.

O projeto tem como principal diferencial a segurança nas negociações, utilizando mecanismos de análise de mensagens para identificar possíveis tentativas de golpe e impedir negociações realizadas fora da plataforma.

 **Sobre o projeto**
 
A GamesRun permite que usuários anunciem produtos e realizem compras pela própria plataforma.

Existem duas possibilidades para o vendedor:

- Vender o produto diretamente para a GamesRun;
- Vender o produto para outro usuário.

Nas vendas entre usuários, o produto passa por um centro de distribuição da GamesRun, onde pode ser verificado antes de ser enviado ao comprador.

O comprador pode escolher entre:

- Entrega em domicílio;
- Retirada em ponto de coleta.

# Sistema de segurança
Um dos principais diferenciais da GamesRun é o sistema de segurança presente no chat, as mensagens podem ser analisadas por uma IA, que procura identificar possíveis tentativas de golpe.

Quando uma mensagem suspeita é identificada, o sistema pode:
  - Classificar a mensagem
  - Alertar o usuário
  - Bloquear mensagens que incentivem negociações fora da plataforma
  - Registrar o caso para supervisão
  - Adicionar o usuário à lista de supervisão quando necessário

# Banco de dados
Por padrão, o ambiente local usa H2 em arquivo (`GamesRun/data/gamesrun.mv.db`), mantendo os dados entre reinicializações. O console do H2 fica disponível em `http://localhost:8081/h2-console`, com JDBC URL `jdbc:h2:file:./data/gamesrun`, usuário `sa` e senha vazia.

Os testes usam um banco H2 em memória e não alteram os dados persistidos do ambiente local.

**Executar localmente**
Na pasta `GamesRun`, execute `sh mvnw spring-boot:run`. A página ficará disponível em `http://localhost:8081` e os endpoints em `/api`.

# Requisitos funcionais
  - RF01	Criar conta
  - RF02	Fazer login
  - RF03	Gerenciar dados pessoais
  - RF04	Cadastrar anúncios
  - RF05	Pesquisar produtos
  - RF06	Visualizar detalhes do produto
  - RF07	Adicionar produtos ao carrinho
  - RF08	Realizar compra
  - RF09	Escolher forma de entrega
  - RF10	Escolher modalidade de venda
  - RF11	Registrar pedidos
  - RF12	Utilizar chat
  - RF13	Analisar mensagens
  - RF14	Orientar usuário em possíveis golpes
  - RF15	Bloquear negociação fora da plataforma
  - RF16	Registrar usuários para supervisão
  - RF17	Acompanhar compra e entrega
  - RF18	Disponibilizar páginas principais
  - RF19	Avaliar vendedores e compradores

# Requisitos não funcionais
  - RNF01: Segurança dos usuários e produtos
  - RNF02: Armazenamento seguro de dados pessoais
  - RNF03: Proteção das senhas
  - RNF04: Redução de golpes e negociações externas
  - RNF05: Interface simples e intuitiva
  - RNF06: Disponibilidade adequada
  - RNF07: Tempo de resposta adequado
  - RNF08: Suporte a múltiplos usuários simultâneos
  - RNF09: Integridade e consistência do banco de dados
  - RNF10: Arquitetura preparada para manutenção e evolução
  - RNF11: Compatibilidade com dispositivos e navegadores
  - RNF12: Confidencialidade das comunicações
  - RNF13: Análise de mensagens sem comprometer a privacidade
  - RNF14: Integração com serviços de entrega de terceiros

# Objetivo
O objetivo da GamesRun é desenvolver uma plataforma de compra e venda de produtos gamers que combine:

Marketplace + Segurança + Inteligência Artificial

A proposta busca tornar as negociações mais organizadas e reduzir riscos relacionados a golpes e negociações realizadas fora da plataforma.

# Status do projeto
Em desenvolvimento

O projeto está sendo desenvolvido como uma aplicação acadêmica, com foco em:
  - Desenvolvimento web
  - Banco de dados
  - APIs
  - Arquitetura de software
  - Segurança
  - Inteligência Artificial
  - Experiência do usuário

 **Licença**

Projeto desenvolvido para fins acadêmicos e educacionais.
