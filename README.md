🎮 GamesRun
GamesRun é uma plataforma de compra e venda de produtos relacionados ao universo gamer, como jogos, consoles e acessórios.

O projeto tem como principal diferencial a segurança nas negociações, utilizando mecanismos de análise de mensagens para identificar possíveis tentativas de golpe e impedir negociações realizadas fora da plataforma.

📌 Sobre o projeto
A GamesRun permite que usuários anunciem produtos e realizem compras pela própria plataforma.

Existem duas possibilidades para o vendedor:

🏢 Vender o produto diretamente para a GamesRun;
👤 Vender o produto para outro usuário.

Nas vendas entre usuários, o produto passa por um centro de distribuição da GamesRun, onde pode ser verificado antes de ser enviado ao comprador.

O comprador pode escolher entre:

🚚 Entrega em domicílio;
📦 Retirada em ponto de coleta.

🛡️ Sistema de segurança
Um dos principais diferenciais da GamesRun é o sistema de segurança presente no chat, as mensagens podem ser analisadas por uma IA, que procura identificar possíveis tentativas de golpe.

Quando uma mensagem suspeita é identificada, o sistema pode:
  🔎 Classificar a mensagem
  ⚠️ Alertar o usuário
  🚫 Bloquear mensagens que incentivem negociações fora da plataforma
  👮 Registrar o caso para supervisão
  📋 Adicionar o usuário à lista de supervisão quando necessário

🗄️ Banco de dados
Por padrão, o ambiente local usa H2 em arquivo (`GamesRun/data/gamesrun.mv.db`), mantendo os dados entre reinicializações. O console do H2 fica disponível em `http://localhost:8081/h2-console`, com JDBC URL `jdbc:h2:file:./data/gamesrun`, usuário `sa` e senha vazia.

Os testes usam um banco H2 em memória e não alteram os dados persistidos do ambiente local.

▶️ Executar localmente
Na pasta `GamesRun`, execute `sh mvnw spring-boot:run`. A página ficará disponível em `http://localhost:8081` e os endpoints em `/api`.

📋 Requisitos funcionais
  RF01	Criar conta
  RF02	Fazer login
  RF03	Gerenciar dados pessoais
  RF04	Cadastrar anúncios
  RF05	Pesquisar produtos
  RF06	Visualizar detalhes do produto
  RF07	Adicionar produtos ao carrinho
  RF08	Realizar compra
  RF09	Escolher forma de entrega
  RF10	Escolher modalidade de venda
  RF11	Registrar pedidos
  RF12	Utilizar chat
  RF13	Analisar mensagens
  RF14	Orientar usuário em possíveis golpes
  RF15	Bloquear negociação fora da plataforma
  RF16	Registrar usuários para supervisão
  RF17	Acompanhar compra e entrega
  RF18	Disponibilizar páginas principais
  RF19	Avaliar vendedores e compradores

📋 Requisitos não funcionais
  RNF01: Segurança dos usuários e produtos
  RNF02: Armazenamento seguro de dados pessoais
  RNF03: Proteção das senhas
  RNF04: Redução de golpes e negociações externas
  RNF05: Interface simples e intuitiva
  RNF06: Disponibilidade adequada
  RNF07: Tempo de resposta adequado
  RNF08: Suporte a múltiplos usuários simultâneos
  RNF09: Integridade e consistência do banco de dados
  RNF10: Arquitetura preparada para manutenção e evolução
  RNF11: Compatibilidade com dispositivos e navegadores
  RNF12: Confidencialidade das comunicações
  RNF13: Análise de mensagens sem comprometer a privacidade
  RNF14: Integração com serviços de entrega de terceiros

🎯 Objetivo
O objetivo da GamesRun é desenvolver uma plataforma de compra e venda de produtos gamers que combine:

Marketplace + Segurança + Inteligência Artificial

A proposta busca tornar as negociações mais organizadas e reduzir riscos relacionados a golpes e negociações realizadas fora da plataforma.

👨‍💻 Status do projeto
🚧 Em desenvolvimento

O projeto está sendo desenvolvido como uma aplicação acadêmica, com foco em:
  - Desenvolvimento web
  - Banco de dados
  - APIs
  - Arquitetura de software
  - Segurança
  - Inteligência Artificial
  - Experiência do usuário

📜 Licença
Projeto desenvolvido para fins acadêmicos e educacionais.
