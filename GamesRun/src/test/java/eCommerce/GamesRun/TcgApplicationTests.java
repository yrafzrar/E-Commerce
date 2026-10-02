package eCommerce.GamesRun;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import eCommerce.GamesRun.repository.ProdutoRepository;
import eCommerce.GamesRun.repository.AnuncioRepository;
import eCommerce.GamesRun.repository.CarrinhoRepository;
import eCommerce.GamesRun.repository.PedidoRepository;
import eCommerce.GamesRun.repository.CategoriaRepository;
import eCommerce.GamesRun.repository.UsuarioRepository;
import tools.jackson.databind.ObjectMapper;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
class GamesRunApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ProdutoRepository produtoRepository;

	@Autowired
	private AnuncioRepository anuncioRepository;

	@Autowired
	private CarrinhoRepository carrinhoRepository;

	@Autowired
	private PedidoRepository pedidoRepository;

	@Autowired
	private CategoriaRepository categoriaRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@BeforeEach
	void limparProdutos() {
		produtoRepository.deleteAll();
		pedidoRepository.deleteAll();
		carrinhoRepository.deleteAll();
		anuncioRepository.deleteAll();
		categoriaRepository.deleteAll();
		usuarioRepository.deleteAll();
	}

	@Test
	void contextLoads() {
	}

	@Test
	void deveCriarConsultarAtualizarEExcluirProduto() throws Exception {
		MvcResult resultadoCriacao = mockMvc.perform(post("/api/produtos/cadastrar")
					.contentType(MediaType.APPLICATION_JSON)
					.content("""
							{"nome":"Controle sem fio","descricao":"Controle para console","preco":199.90,"estoque":4}
							"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.nome").value("Controle sem fio"))
				.andReturn();

		long id = objectMapper.readTree(resultadoCriacao.getResponse().getContentAsString())
				.get("id").asLong();

		mockMvc.perform(get("/api/produtos/listar/{id}", id))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.estoque").value(4));

		mockMvc.perform(put("/api/produtos/atualizar/{id}", id)
					.contentType(MediaType.APPLICATION_JSON)
					.content("""
							{"nome":"Controle atualizado","descricao":"Controle sem fio","preco":179.90,"estoque":2}
							"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.nome").value("Controle atualizado"));

		mockMvc.perform(delete("/api/produtos/excluir/{id}", id))
				.andExpect(status().isOk());

		mockMvc.perform(get("/api/produtos/listar/{id}", id))
				.andExpect(status().isNotFound());
	}

	@Test
	void cadastrosNulosRetornamErroComMensagemEspecifica() throws Exception {
		verificarCadastroNulo("/api/anuncios/cadastrar", "Anuncio nao pode ser nulo");
		verificarCadastroNulo("/api/avaliacoes/cadastrar", "Avaliacao nao pode ser nula");
		verificarCadastroNulo("/api/carrinhos/cadastrar", "Carrinho nao pode ser nulo");
		verificarCadastroNulo("/api/categorias/cadastrar", "Categoria nao pode ser nula");
		verificarCadastroNulo("/api/pedidos/cadastrar", "Pedido nao pode ser nulo");
		verificarCadastroNulo("/api/produtos/cadastrar", "Produto nao pode ser nulo");
		verificarCadastroNulo("/api/usuarios/cadastrar", "Usuario nao pode ser nulo");
	}

	@Test
	void fluxoDeContaAnuncioCarrinhoEPedido() throws Exception {
		MvcResult resultadoUsuario = mockMvc.perform(post("/api/usuarios/cadastrar")
					.contentType(MediaType.APPLICATION_JSON)
					.content("""
							{"nome":"Jogador Teste","nick":"jogador@teste.com","senha":"senha123","cpf":"00123456789"}
							"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.senha").doesNotExist())
				.andReturn();
		long usuarioId = objectMapper.readTree(resultadoUsuario.getResponse().getContentAsString()).get("id").asLong();
		var categoria = new eCommerce.GamesRun.domain.Categoria();
		categoria.setNome("Consoles");
		categoria.setDescricao("Jogos e consoles");
		long categoriaId = categoriaRepository.save(categoria).getId();

		mockMvc.perform(post("/api/usuarios/login")
					.contentType(MediaType.APPLICATION_JSON)
					.content("""
							{"nick":"jogador@teste.com","senha":"senha123"}
							"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(usuarioId));

		MvcResult resultadoAnuncio = mockMvc.perform(post("/api/anuncios/cadastrar")
					.contentType(MediaType.APPLICATION_JSON)
					.content("""
							{"produto":"Controle retrô","descricao":"Em ótimo estado","preco":150.0,"categoriaId":%d,"vendedorId":%d,"imagemUrl":"https://example.com/controle.jpg","estadoConservacao":"Usado"}
							""".formatted(categoriaId, usuarioId)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.imagemUrl").value("https://example.com/controle.jpg"))
				.andReturn();
		long anuncioId = objectMapper.readTree(resultadoAnuncio.getResponse().getContentAsString()).get("id").asLong();

		mockMvc.perform(get("/api/anuncios/buscar").param("produto", "controle"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].produto").value("Controle retrô"));

		mockMvc.perform(post("/api/carrinhos/usuario/{usuarioId}/itens", usuarioId)
					.contentType(MediaType.APPLICATION_JSON)
					.content("""
							{"anuncioId":%d,"quantidade":1}
							""".formatted(anuncioId)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.quantidade").value(1));

		mockMvc.perform(put("/api/carrinhos/usuario/{usuarioId}/itens/{anuncioId}", usuarioId, anuncioId)
					.param("quantidade", "2"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.quantidade").value(2));

		mockMvc.perform(post("/api/pedidos/cadastrar")
					.contentType(MediaType.APPLICATION_JSON)
					.content("""
							{"compradorId":%d,"status":"AGUARDANDO_PAGAMENTO","dataCriacao":"2026-09-28T10:00:00Z","valorTotal":325.0,"formaPagamento":"pix","enderecoEntrega":"Rua Teste, 1","itensResumo":"[{\\"nome\\":\\"Controle retrô\\",\\"preco\\":150.0,\\"quantidade\\":2}]"}
							""".formatted(usuarioId)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("AGUARDANDO_PAGAMENTO"));

		mockMvc.perform(get("/api/pedidos/comprador/{compradorId}", usuarioId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].itensResumo").exists());
	}

	@Test
	void buscasInexistentesRetornam404ComMensagemDoRecurso() throws Exception {
		verificarNaoEncontrado("/api/anuncios/listar/99999999", "Anuncio nao encontrado");
		verificarNaoEncontrado("/api/avaliacoes/listar/99999999", "Avaliacao nao encontrada");
		verificarNaoEncontrado("/api/carrinhos/listar/99999999", "Carrinho nao encontrado");
		verificarNaoEncontrado("/api/categorias/listar/99999999", "Categoria nao encontrada");
		verificarNaoEncontrado("/api/pedidos/listar/99999999", "Pedido nao encontrado");
		verificarNaoEncontrado("/api/produtos/listar/99999999", "Produto nao encontrado");
		verificarNaoEncontrado("/api/usuarios/listar/99999999", "Usuario nao encontrado");
	}

	private void verificarCadastroNulo(String rota, String mensagem) throws Exception {
		mockMvc.perform(post(rota)
					.contentType(MediaType.APPLICATION_JSON)
					.content("null"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.message").value(mensagem));
	}

	private void verificarNaoEncontrado(String rota, String mensagem) throws Exception {
		mockMvc.perform(get(rota))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.message").value(mensagem));
	}

}
