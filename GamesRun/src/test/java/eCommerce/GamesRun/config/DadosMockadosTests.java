package eCommerce.GamesRun.config;

import eCommerce.GamesRun.domain.Anuncio;
import eCommerce.GamesRun.repository.AnuncioRepository;
import eCommerce.GamesRun.repository.CategoriaRepository;
import eCommerce.GamesRun.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:mock-tests;DB_CLOSE_DELAY=-1")
@AutoConfigureMockMvc
@ActiveProfiles({"test", "mock"})
@Transactional
class DadosMockadosTests {
    @Autowired DadosMockados inicializador;
    @Autowired UsuarioRepository usuarios;
    @Autowired CategoriaRepository categorias;
    @Autowired AnuncioRepository anuncios;
    @Autowired MockMvc mvc;

    @Test
    void gravaContaEOitoAnunciosVinculadosEPermiteLogin() throws Exception {
        var usuario = usuarios.findByNick(DadosMockados.LOGIN).orElseThrow();
        assertEquals(8, anuncios.count());
        assertEquals(5, categorias.count());
        assertTrue(anuncios.findAll().stream().allMatch(item ->
                usuario.getId().equals(item.getVendedorId()) && item.getImagemUrl().startsWith("assets/")));
        mvc.perform(post("/api/usuarios/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nick\":\"demo@gamesrun.local\",\"senha\":\"GamesRun123!\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(usuario.getId()))
                .andExpect(jsonPath("$.senha").doesNotExist());
    }

    @Test
    void repetirNaoDuplicaNemApagaDadosOuRedefineSenha() {
        var usuario = usuarios.findByNick(DadosMockados.LOGIN).orElseThrow();
        usuario.setSenha("SenhaAlterada123!");
        usuarios.save(usuario);
        Anuncio proprio = new Anuncio();
        proprio.setProduto("Anúncio cadastrado pelo usuário");
        proprio.setDescricao("Este registro precisa ser preservado.");
        proprio.setPreco(150);
        proprio.setCategoriaId(categorias.findAll().get(0).getId());
        proprio.setVendedorId(usuario.getId());
        anuncios.save(proprio);
        inicializador.run();
        inicializador.run();
        assertEquals(1, usuarios.count());
        assertEquals(9, anuncios.count());
        assertEquals(5, categorias.count());
        assertEquals("SenhaAlterada123!", usuarios.findByNick(DadosMockados.LOGIN).orElseThrow().getSenha());
        assertTrue(anuncios.findById(proprio.getId()).isPresent());
    }
    @Test
    void exemplosExcluidosNaoSaoRecriados() {
        var anuncio = anuncios.findAll().get(0);
        anuncios.delete(anuncio);
        anuncios.flush();
        inicializador.run();
        assertEquals(7, anuncios.count());
    }
}
