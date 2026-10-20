package eCommerce.GamesRun;

import eCommerce.GamesRun.domain.Anuncio;
import eCommerce.GamesRun.domain.Categoria;
import eCommerce.GamesRun.repository.AnuncioRepository;
import eCommerce.GamesRun.repository.CategoriaRepository;
import eCommerce.GamesRun.repository.FavoritoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FavoritosTests {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired AnuncioRepository anuncios;
    @Autowired CategoriaRepository categorias;
    @Autowired FavoritoRepository favoritos;

    record Conta(long id, String nick, MockHttpSession session) {}
    Conta conta() throws Exception {
        String token=UUID.randomUUID().toString(),nick=token+"@example.com";
        var result=mvc.perform(post("/api/usuarios/cadastrar").contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Favoritos "+token+"\",\"nick\":\""+nick+"\",\"cpf\":\""+token+"\",\"senha\":\"senha12345\",\"idade\":18}"))
                .andExpect(status().isOk()).andReturn();
        return new Conta(mapper.readTree(result.getResponse().getContentAsString()).get("id").asLong(),nick,
                (MockHttpSession)result.getRequest().getSession(false));
    }
    long anuncio(Conta owner) {
        Categoria categoria=new Categoria();categoria.setNome("Favorito "+UUID.randomUUID());
        Anuncio a=new Anuncio();a.setProduto("Produto favorito");a.setPreco(100);a.setVendedorId(owner.id());
        a.setCategoriaId(categorias.save(categoria).getId());
        return anuncios.save(a).getId();
    }
    void lista(Conta user,long... ids) throws Exception {
        String json=java.util.Arrays.toString(ids);
        mvc.perform(get("/api/favoritos").session(user.session()))
                .andExpect(status().isOk()).andExpect(content().json(json));
    }
    @Test void exigeLoginParaLerAdicionarERemover() throws Exception {
        mvc.perform(get("/api/favoritos")).andExpect(status().isUnauthorized());
        mvc.perform(put("/api/favoritos/1")).andExpect(status().isUnauthorized());
        mvc.perform(delete("/api/favoritos/1")).andExpect(status().isUnauthorized());
    }
    @Test void favoritosSaoIsoladosMesmoComUsuarioIdForjadoEOperacoesRepetidas() throws Exception {
        Conta a=conta(),b=conta();long id=anuncio(a);
        for(int i=0;i<2;i++)mvc.perform(put("/api/favoritos/{id}",id).session(a.session()))
                .andExpect(status().isNoContent());
        assertEquals(1,favoritos.findByUsuarioIdOrderByAnuncioIdAsc(a.id()).size());
        lista(a,id);lista(b);
        mvc.perform(get("/api/favoritos").session(b.session()).param("usuarioId",String.valueOf(a.id())))
                .andExpect(status().isOk()).andExpect(content().json("[]"));
        mvc.perform(delete("/api/favoritos/{id}",id).session(b.session()).param("usuarioId",String.valueOf(a.id())))
                .andExpect(status().isNoContent());
        lista(a,id);
        mvc.perform(put("/api/favoritos/{id}",id).session(b.session())).andExpect(status().isNoContent());
        for(int i=0;i<2;i++)mvc.perform(delete("/api/favoritos/{id}",id).session(a.session())).andExpect(status().isNoContent());
        lista(a);lista(b,id);
    }
    @Test void mantemFavoritosNoBancoAposLogoutENovoLogin() throws Exception {
        Conta a=conta();long id=anuncio(a);
        mvc.perform(put("/api/favoritos/{id}",id).session(a.session())).andExpect(status().isNoContent());
        mvc.perform(post("/api/usuarios/logout").session(a.session())).andExpect(status().isNoContent());
        assertEquals(id,favoritos.findByUsuarioIdOrderByAnuncioIdAsc(a.id()).get(0).getAnuncioId());
        mvc.perform(get("/api/favoritos")).andExpect(status().isUnauthorized());
        var result=mvc.perform(post("/api/usuarios/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"nick\":\""+a.nick()+"\",\"senha\":\"senha12345\"}"))
                .andExpect(status().isOk()).andReturn();
        lista(new Conta(a.id(),a.nick(),(MockHttpSession)result.getRequest().getSession(false)),id);
    }
    @Test void rejeitaAnuncioInexistenteELimpaFavoritosQuandoVendedorExclui() throws Exception {
        Conta a=conta(),b=conta();long id=anuncio(a);
        mvc.perform(put("/api/favoritos/9223372036854775807").session(a.session())).andExpect(status().isNotFound());
        for(Conta user:new Conta[]{a,b})mvc.perform(put("/api/favoritos/{id}",id).session(user.session()))
                .andExpect(status().isNoContent());
        mvc.perform(delete("/api/anuncios/excluir/{id}",id).session(a.session())).andExpect(status().isOk());
        lista(a);lista(b);assertFalse(anuncios.existsById(id));
    }
}
