package eCommerce.GamesRun;

import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.Decoder;
import eCommerce.GamesRun.domain.Anuncio;
import eCommerce.GamesRun.domain.Carrinho;
import eCommerce.GamesRun.domain.Categoria;
import eCommerce.GamesRun.domain.Pedido;
import eCommerce.GamesRun.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdicoesStudioTests {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired CategoriaRepository categorias;
    @Autowired AnuncioRepository anuncios;
    @Autowired CarrinhoRepository carrinhos;
    @Autowired PedidoRepository pedidos;

    record Conta(long id, MockHttpSession session) {}
    Conta conta() throws Exception {
        String token=UUID.randomUUID().toString();
        MvcResult result=mvc.perform(post("/api/usuarios/cadastrar").contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Teste "+token+"\",\"nick\":\""+token+"@example.com\",\"cpf\":\""+token+"\",\"senha\":\"senha12345\",\"idade\":18}"))
                .andExpect(status().isOk()).andReturn();
        return new Conta(mapper.readTree(result.getResponse().getContentAsString()).get("id").asLong(),(MockHttpSession)result.getRequest().getSession(false));
    }
    long categoria() {
        Categoria c=new Categoria();c.setNome("Teste "+UUID.randomUUID());return categorias.save(c).getId();
    }
    long anuncio(Conta owner) throws Exception {
        var result=mvc.perform(post("/api/anuncios/cadastrar").session(owner.session()).contentType(MediaType.APPLICATION_JSON)
                .content("{\"produto\":\"Produto de teste\",\"preco\":100,\"categoriaId\":"+categoria()+",\"vendedorId\":999999}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.vendedorId").value(owner.id())).andReturn();
        return mapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }
    long pedido(Conta buyer,String method) throws Exception {
        var result=mvc.perform(post("/api/pedidos/cadastrar").session(buyer.session()).contentType(MediaType.APPLICATION_JSON)
                .content("{\"compradorId\":999999,\"status\":\"PAGO\",\"valorTotal\":125,\"formaPagamento\":\""+method+"\",\"itensResumo\":\"[]\",\"enderecoEntrega\":\"Rua Teste, 123\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.compradorId").value(buyer.id()))
                .andExpect(jsonPath("$.status").value("AGUARDANDO_PAGAMENTO")).andReturn();
        return mapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }
    JsonNode abrir(Conta buyer,long id) throws Exception {
        return mapper.readTree(mvc.perform(post("/api/pedidos/{id}/simulacao",id).session(buyer.session()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.simulado").value(true)).andReturn().getResponse().getContentAsString());
    }

    @Test void exclusaoRecusaAnonimoEOutroVendedorMesmoComIdForjado() throws Exception {
        Conta owner=conta(),other=conta();long id=anuncio(owner);
        mvc.perform(delete("/api/anuncios/excluir/{id}",id)).andExpect(status().isUnauthorized());
        mvc.perform(delete("/api/anuncios/excluir/{id}",id).session(other.session()).param("vendedorId",String.valueOf(owner.id())))
                .andExpect(status().isForbidden());
        assertTrue(anuncios.existsById(id));
    }
    @Test void vendedorExcluiDoBancoELimpaReferenciasSemApagarHistorico() throws Exception {
        Conta owner=conta(),buyer=conta();long id=anuncio(owner);
        Carrinho item=new Carrinho();item.setUsuarioId(buyer.id());item.setAnuncioId(id);item.setQuantidade(1);carrinhos.save(item);
        Pedido order=new Pedido();order.setCompradorId(buyer.id());order.setStatus("AGUARDANDO_PAGAMENTO");order.setItensResumo("[{\"nome\":\"Produto de teste\",\"quantidade\":1}]");order=pedidos.save(order);
        mvc.perform(delete("/api/anuncios/excluir/{id}",id).session(owner.session())).andExpect(status().isOk());
        assertFalse(anuncios.existsById(id));assertTrue(carrinhos.findByUsuarioId(buyer.id()).isEmpty());assertTrue(pedidos.existsById(order.getId()));
    }
    @Test void naoPermiteTransferirPropriedadeOuSobrescreverAnuncioComPost() throws Exception {
        Conta owner=conta(),other=conta();long id=anuncio(owner);long category=categoria();
        String body="{\"id\":"+id+",\"produto\":\"Produto atualizado\",\"preco\":150,\"categoriaId\":"+category+",\"vendedorId\":"+other.id()+"}";
        mvc.perform(put("/api/anuncios/atualizar/{id}",id).session(other.session()).contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isForbidden());
        mvc.perform(put("/api/anuncios/atualizar/{id}",id).session(owner.session()).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk()).andExpect(jsonPath("$.vendedorId").value(owner.id()));
        var posted=mvc.perform(post("/api/anuncios/cadastrar").session(other.session()).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk()).andReturn();
        assertNotEquals(id,mapper.readTree(posted.getResponse().getContentAsString()).get("id").asLong());
        assertEquals(owner.id(),anuncios.findById(id).orElseThrow().getVendedorId());
        mvc.perform(put("/api/usuarios/atualizar/{id}",owner.id()).session(other.session()).contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isForbidden());
    }
    @Test void pixGeraQrLegivelAleatorioECopiaIgualAoConteudoDoQr() throws Exception {
        Conta buyer=conta();long id=pedido(buyer,"pix");
        JsonNode first=abrir(buyer,id),second=abrir(buyer,id);
        assertNotEquals(first.get("codigo").asText(),second.get("codigo").asText());
        assertTrue(second.get("codigo").asText().startsWith("GAMESRUN-PIX-"));
        String svg=second.get("qrSvg").asText();var dimensions=Pattern.compile("viewBox=\"0 0 (\\d+) (\\d+)\"").matcher(svg);assertTrue(dimensions.find());
        int width=Integer.parseInt(dimensions.group(1));BitMatrix matrix=new BitMatrix(width-8);
        var cells=Pattern.compile("M(\\d+),(\\d+)h(\\d+)v1h-\\d+z").matcher(svg);
        while(cells.find()) {int x=Integer.parseInt(cells.group(1)),y=Integer.parseInt(cells.group(2)),length=Integer.parseInt(cells.group(3));for(int i=0;i<length;i++)if(x+i>=4&&x+i<width-4&&y>=4&&y<width-4)matrix.set(x+i-4,y-4);}
        assertEquals(second.get("codigo").asText(),new Decoder().decode(matrix).getText());
        mvc.perform(post("/api/pedidos/{id}/simulacao/confirmar",id).session(buyer.session()).contentType(MediaType.APPLICATION_JSON)
                .content("{\"codigo\":\""+first.get("codigo").asText()+"\"}")).andExpect(status().isConflict());
    }
    @Test void confirmaTresMetodosComoSimuladosERepetirNaoDuplica() throws Exception {
        Conta buyer=conta();
        for(String method:new String[]{"pix","cartao","boleto"}){
            long id=pedido(buyer,method);JsonNode simulation=abrir(buyer,id);String codigo=simulation.get("codigo").asText();
            if(method.equals("boleto")){assertTrue(codigo.startsWith("GAMESRUN-BOLETO-"));assertTrue(simulation.get("barrasSvg").asText().contains("<svg"));}
            String body="{\"codigo\":\""+codigo+"\"}";
            var result=mvc.perform(post("/api/pedidos/{id}/simulacao/confirmar",id).session(buyer.session()).contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("PAGAMENTO_SIMULADO")).andExpect(jsonPath("$.pagamentoSimuladoEm").isNotEmpty()).andReturn();
            String date=mapper.readTree(result.getResponse().getContentAsString()).get("pagamentoSimuladoEm").asText();
            mvc.perform(post("/api/pedidos/{id}/simulacao/confirmar",id).session(buyer.session()).contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.pagamentoSimuladoEm").value(date));
        }
    }
    @Test void outroCompradorNaoPodeAbrirOuConfirmarPagamento() throws Exception {
        Conta buyer=conta(),other=conta();long id=pedido(buyer,"pix");String code=abrir(buyer,id).get("codigo").asText();
        mvc.perform(post("/api/pedidos/{id}/simulacao",id)).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/pedidos/{id}/simulacao",id).session(other.session())).andExpect(status().isForbidden());
        mvc.perform(post("/api/pedidos/{id}/simulacao/confirmar",id).session(other.session()).contentType(MediaType.APPLICATION_JSON)
                .content("{\"codigo\":\""+code+"\"}")).andExpect(status().isForbidden());
    }
    @Test void logoutInvalidaSessao() throws Exception {
        Conta user=conta();mvc.perform(get("/api/usuarios/sessao").session(user.session())).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(user.id()));
        mvc.perform(post("/api/usuarios/logout").session(user.session())).andExpect(status().isNoContent());
        mvc.perform(get("/api/usuarios/sessao")).andExpect(status().isUnauthorized());assertTrue(user.session().isInvalid());
    }
}
