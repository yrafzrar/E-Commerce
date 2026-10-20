package eCommerce.GamesRun;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
class StudioIntegrationTests {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;

    @Test
    void serveStudioESeusArquivosLocais() throws Exception {
        for (String path : new String[]{"/", "/index.html"}) {
            mvc.perform(get(path)).andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                    .andExpect(content().string(containsString("signupIdade")))
                    .andExpect(content().string(containsString("script.js")));
        }
        mvc.perform(get("/style.css")).andExpect(status().isOk())
                .andExpect(content().string(containsString("studio-hero")));
        mvc.perform(get("/script.js")).andExpect(status().isOk())
                .andExpect(content().string(containsString("/api/usuarios/login")));
        mvc.perform(get("/config.js")).andExpect(status().isOk())
                .andExpect(content().string(containsString("demoOnly: false")));
        mvc.perform(get("/assets/ps5.svg")).andExpect(status().isOk());
    }

    @Test
    void cadastroAceitaIdadeLoginNaoExpoeSenhaERecusaDuplicado() throws Exception {
        String conta = """
                {"nome":"Studio Auth","nick":"studio.auth@teste.com","cpf":"52998224725","idade":18,"senha":"studio123"}
                """;
        mvc.perform(post("/api/usuarios/cadastrar").contentType(MediaType.APPLICATION_JSON).content(conta))
                .andExpect(status().isOk()).andExpect(jsonPath("$.idade").value(18))
                .andExpect(jsonPath("$.senha").doesNotExist());
        mvc.perform(post("/api/usuarios/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"nick\":\"studio.auth@teste.com\",\"senha\":\"errada\"}"))
                .andExpect(status().isUnauthorized());
        mvc.perform(post("/api/usuarios/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"nick\":\"studio.auth@teste.com\",\"senha\":\"studio123\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.senha").doesNotExist());
        mvc.perform(post("/api/usuarios/cadastrar").contentType(MediaType.APPLICATION_JSON).content(conta))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.message").exists());
    }

    @Test
    void registraAsTresFormasSemConfirmarPagamento() throws Exception {
        var cadastro = mvc.perform(post("/api/usuarios/cadastrar").contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Studio Pagamentos\",\"nick\":\"studio.pag@teste.com\",\"cpf\":\"11144477735\",\"idade\":20,\"senha\":\"studio123\"}"))
                .andExpect(status().isOk()).andReturn();
        long comprador = mapper.readTree(cadastro.getResponse().getContentAsString()).get("id").asLong();
        for (String forma : new String[]{"pix", "cartao", "boleto"}) {
            var sessao = (org.springframework.mock.web.MockHttpSession) cadastro.getRequest().getSession(false);
            String pedido = """
                    {"compradorId":%d,"status":"AGUARDANDO_PAGAMENTO","valorTotal":125.0,"formaPagamento":"%s","enderecoEntrega":"Rua Teste, 123 - Centro - Sao Paulo/SP","itensResumo":"[]"}
                    """.formatted(comprador, forma);
            mvc.perform(post("/api/pedidos/cadastrar").session(sessao).contentType(MediaType.APPLICATION_JSON).content(pedido))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.formaPagamento").value(forma))
                    .andExpect(jsonPath("$.status").value("AGUARDANDO_PAGAMENTO"));
        }
    }
}
