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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import eCommerce.GamesRun.repository.ProdutoRepository;
import tools.jackson.databind.ObjectMapper;

@AutoConfigureMockMvc
@SpringBootTest
class GamesRunApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ProdutoRepository produtoRepository;

	@BeforeEach
	void limparProdutos() {
		produtoRepository.deleteAll();
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
		verificarCadastroNulo("/api/anuncios", "Anuncio nao pode ser nulo");
		verificarCadastroNulo("/api/avaliacoes", "Avaliacao nao pode ser nula");
		verificarCadastroNulo("/api/carrinhos", "Carrinho nao pode ser nulo");
		verificarCadastroNulo("/api/categorias", "Categoria nao pode ser nula");
		verificarCadastroNulo("/api/pedidos", "Pedido nao pode ser nulo");
		verificarCadastroNulo("/api/produtos/cadastrar", "Produto nao pode ser nulo");
		verificarCadastroNulo("/api/usuarios", "Usuario nao pode ser nulo");
	}

	@Test
	void buscasInexistentesRetornam404ComMensagemDoRecurso() throws Exception {
		verificarNaoEncontrado("/api/anuncios/99999999", "Anuncio nao encontrado");
		verificarNaoEncontrado("/api/avaliacoes/99999999", "Avaliacao nao encontrada");
		verificarNaoEncontrado("/api/carrinhos/99999999", "Carrinho nao encontrado");
		verificarNaoEncontrado("/api/categorias/99999999", "Categoria nao encontrada");
		verificarNaoEncontrado("/api/pedidos/99999999", "Pedido nao encontrado");
		verificarNaoEncontrado("/api/produtos/listar/99999999", "Produto nao encontrado");
		verificarNaoEncontrado("/api/usuarios/99999999", "Usuario nao encontrado");
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
