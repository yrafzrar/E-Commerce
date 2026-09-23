#🎮 GamesRun
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
Um dos principais diferenciais da GamesRun é o sistema de segurança presente no chat.

As mensagens podem ser analisadas por uma IA, que procura identificar possíveis tentativas de golpe.

Quando uma mensagem suspeita é identificada, o sistema pode:

🔎 Classificar a mensagem;

⚠️ Alertar o usuário;

🚫 Bloquear mensagens que incentivem negociações fora da plataforma;

👮 Registrar o caso para supervisão;

📋 Adicionar o usuário à lista de supervisão quando necessário.

Exemplo
Vendedor:
"Me chama no WhatsApp para fecharmos por fora da plataforma."

        ↓

      IA analisa

        ↓

Mensagem identificada como suspeita

        ↓

🚫 Mensagem bloqueada
⚠️ Usuário recebe orientação
📋 Caso registrado para supervisão
⚙️ Funcionalidades
👤 Usuários
Criar conta;

Fazer login;

Atualizar dados pessoais;

Cadastrar endereços;

Gerenciar conta.

🛒 Produtos
Criar anúncios;

Editar anúncios;

Remover anúncios;

Pesquisar produtos;

Visualizar detalhes;

Adicionar imagens;

Definir preço e quantidade;

Informar condição do produto.

🛍️ Carrinho
Adicionar produtos;

Alterar quantidade;

Remover produtos;

Visualizar carrinho.

💳 Compras
Realizar pedidos;

Registrar pedidos;

Realizar pagamento;

Acompanhar status da compra.

🚚 Entregas
Escolher tipo de entrega;

Entrega em domicílio;

Retirada em ponto de coleta;

Acompanhar entrega;

Consultar código de rastreio.

💬 Chat
Comunicação entre comprador e vendedor;

Análise das mensagens;

Identificação de mensagens suspeitas;

Bloqueio de mensagens de negociação externa;

Registro de possíveis golpes.

⭐ Avaliações
Avaliar vendedores;

Avaliar compradores;

Registrar nota de 1 a 5;

Adicionar comentários.

🗄️ Banco de dados
A GamesRun utiliza um banco de dados relacional.

Principais tabelas:

usuarios
enderecos
categorias
anuncios
imagens_anuncio
carrinhos
itens_carrinho
pedidos
itens_pedido
pagamentos
entregas
chats
mensagens
supervisoes
avaliacoes
Principais relacionamentos
USUARIO
   │
   ├── ENDEREÇOS
   ├── ANÚNCIOS
   ├── CARRINHO
   ├── PEDIDOS
   ├── CHATS
   ├── SUPERVISÕES
   └── AVALIAÇÕES

ANÚNCIO
   │
   ├── IMAGENS
   ├── CATEGORIA
   ├── ITENS DO CARRINHO
   └── CHATS

PEDIDO
   │
   ├── ITENS
   ├── PAGAMENTO
   └── ENTREGA

CHAT
   │
   └── MENSAGENS
          │
          └── SUPERVISÕES
🏗️ Arquitetura
O projeto utiliza uma arquitetura organizada em camadas:

src/
│
├── controllers/
│   ├── AuthController
│   ├── UsuarioController
│   ├── EnderecoController
│   ├── AnuncioController
│   ├── CategoriaController
│   ├── CarrinhoController
│   ├── PedidoController
│   ├── PagamentoController
│   ├── EntregaController
│   ├── ChatController
│   ├── MensagemController
│   ├── AvaliacaoController
│   └── SupervisaoController
│
├── services/
│   ├── AuthService
│   ├── AnuncioService
│   ├── PedidoService
│   ├── PagamentoService
│   ├── EntregaService
│   ├── ChatService
│   ├── MensagemService
│   └── IAService
│
├── models/
│   ├── Usuario
│   ├── Endereco
│   ├── Categoria
│   ├── Anuncio
│   ├── Carrinho
│   ├── Pedido
│   ├── Pagamento
│   ├── Entrega
│   ├── Chat
│   ├── Mensagem
│   ├── Avaliacao
│   └── Supervisao
│
└── repositories/
    └── acesso aos dados
🤖 Fluxo da IA
A análise de mensagens não fica diretamente dentro do controller.

MensagemController
        ↓
MensagemService
        ↓
    IAService
        ↓
Análise da mensagem
        ↓
 ┌──────┴──────┐
 ↓             ↓
Normal       Suspeita
 ↓             ↓
Envia       Bloqueia/
mensagem    alerta
                ↓
          Supervisão
Essa separação facilita a manutenção, evolução e organização do sistema.

🔐 Segurança
A plataforma possui requisitos voltados à proteção dos usuários e dos dados.

Entre eles:

Senhas armazenadas de forma protegida;

Dados pessoais protegidos;

CPF, e-mail e nickname únicos;

Controle de usuários bloqueados ou suspensos;

Proteção contra negociações fora da plataforma;

Análise de mensagens suspeitas;

Registro de casos para supervisão;

Confidencialidade das comunicações.

📋 Requisitos funcionais
Código	Requisito
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
RNF01: Segurança dos usuários e produtos;

RNF02: Armazenamento seguro de dados pessoais;

RNF03: Proteção das senhas;

RNF04: Redução de golpes e negociações externas;

RNF05: Interface simples e intuitiva;

RNF06: Disponibilidade adequada;

RNF07: Tempo de resposta adequado;

RNF08: Suporte a múltiplos usuários simultâneos;

RNF09: Integridade e consistência do banco de dados;

RNF10: Arquitetura preparada para manutenção e evolução;

RNF11: Compatibilidade com dispositivos e navegadores;

RNF12: Confidencialidade das comunicações;

RNF13: Análise de mensagens sem comprometer a privacidade;

RNF14: Integração com serviços de entrega de terceiros.

🌐 Principais páginas
A plataforma será composta inicialmente por:

Home
 ├── Produtos
 ├── Categorias
 └── Pesquisa

Produto
 ├── Informações
 ├── Imagens
 ├── Preço
 └── Comprar

Carrinho
 ├── Produtos
 ├── Quantidades
 └── Total

Compra
 ├── Endereço
 ├── Forma de entrega
 ├── Pagamento
 └── Confirmação

Sign Up
 └── Cadastro

Sign In
 └── Login

Chat
 ├── Mensagens
 └── Sistema de segurança
🔄 Fluxo de uma compra
Usuário pesquisa produto
          ↓
Visualiza anúncio
          ↓
Adiciona ao carrinho
          ↓
Realiza a compra
          ↓
Escolhe entrega
          ↓
Realiza pagamento
          ↓
Pedido registrado
          ↓
Produto enviado
          ↓
Acompanhamento da entrega
          ↓
Produto recebido
          ↓
Avaliação
🚚 Fluxo de venda entre usuários
Vendedor cria anúncio
          ↓
Comprador realiza compra
          ↓
Vendedor envia produto
          ↓
Centro de distribuição GamesRun
          ↓
Verificação do produto
          ↓
Produto enviado ao comprador
          ↓
Comprador recebe
          ↓
Avaliação
🎯 Objetivo
O objetivo da GamesRun é desenvolver uma plataforma de compra e venda de produtos gamers que combine:

Marketplace + Segurança + Inteligência Artificial

A proposta busca tornar as negociações mais organizadas e reduzir riscos relacionados a golpes e negociações realizadas fora da plataforma.

👨‍💻 Status do projeto
🚧 Em desenvolvimento

O projeto está sendo desenvolvido como uma aplicação acadêmica, com foco em:

Desenvolvimento web;

Banco de dados;

APIs;

Arquitetura de software;

Segurança;

Inteligência Artificial;

Experiência do usuário.

📁 Organização do projeto
GamesRun/
│
├── src/
├── database/
├── docs/
├── tests/
├── README.md
└── ...
📜 Licença
Projeto desenvolvido para fins acadêmicos e educacionais.