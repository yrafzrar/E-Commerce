package eCommerce.GamesRun;

import eCommerce.GamesRun.security.SessaoUsuario;
import eCommerce.GamesRun.service.ImagemService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ImagemTests {
    static final Path PASTA = pastaTemporaria();
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @DynamicPropertySource static void propriedades(DynamicPropertyRegistry registry) {
        registry.add("gamesrun.upload-dir", () -> PASTA.toString());
        registry.add("spring.datasource.url", () -> "jdbc:h2:mem:imagens-tests;DB_CLOSE_DELAY=-1");
    }
    static Path pastaTemporaria() {
        try { return Files.createTempDirectory("gamesrun-imagens-test-"); }
        catch (Exception e) { throw new IllegalStateException(e); }
    }
    @AfterAll static void limpar() throws Exception {
        try(var arquivos=Files.walk(PASTA)) {
            for(Path arquivo:arquivos.sorted(Comparator.reverseOrder()).toList()) Files.deleteIfExists(arquivo);
        }
    }
    MockHttpSession sessao() {
        MockHttpSession s=new MockHttpSession();s.setAttribute(SessaoUsuario.CHAVE,1L);return s;
    }
    byte[] imagem(String formato) throws Exception {
        var imagem=new BufferedImage(8,6,BufferedImage.TYPE_INT_RGB);imagem.setRGB(0,0,0xff5500);
        var bytes=new ByteArrayOutputStream();assertTrue(ImageIO.write(imagem,formato,bytes));return bytes.toByteArray();
    }
    @Test void uploadExigeLogin() throws Exception {
        mvc.perform(multipart("/api/imagens").file(new MockMultipartFile("arquivo","foto.png","image/png",imagem("png"))))
                .andExpect(status().isUnauthorized());
    }
    @Test void pngEJpgSaoSalvosComNomeNovoEServidosComoImagem() throws Exception {
        for(String formato:new String[]{"png","jpeg"}) {
            var result=mvc.perform(multipart("/api/imagens").file(new MockMultipartFile("arquivo","../../nome-local.exe","image/"+formato,imagem(formato)))
                    .session(sessao())).andExpect(status().isOk()).andReturn();
            String url=mapper.readTree(result.getResponse().getContentAsString()).get("imagemUrl").asText();
            assertTrue(url.matches("/uploads/anuncios/[0-9a-f-]{36}\\.(png|jpg)"));assertFalse(url.contains("nome-local"));
            var response=mvc.perform(get(url)).andExpect(status().isOk())
                    .andExpect(header().string("X-Content-Type-Options","nosniff"))
                    .andExpect(content().contentType(formato.equals("png")?"image/png":"image/jpeg")).andReturn().getResponse();
            var decoded=ImageIO.read(new ByteArrayInputStream(response.getContentAsByteArray()));assertNotNull(decoded);
            assertEquals(8,decoded.getWidth());assertEquals(6,decoded.getHeight());
            assertTrue(Files.isRegularFile(new ImagemService(PASTA.toString()).localizar(url.substring(url.lastIndexOf('/')+1))));
        }
    }
    @Test void rejeitaImagemVaziaArquivoDisfarcadoELimiteDeTamanho() throws Exception {
        for(byte[] dados:new byte[][]{new byte[0],"<svg onload='alert(1)'/>".getBytes(),"texto que nao e imagem".getBytes()})
            mvc.perform(multipart("/api/imagens").file(new MockMultipartFile("arquivo","foto.png","image/png",dados)).session(sessao()))
                    .andExpect(status().isBadRequest());
        mvc.perform(multipart("/api/imagens").file(new MockMultipartFile("arquivo","foto.png","image/png",new byte[(int)ImagemService.LIMITE+1])).session(sessao()))
                .andExpect(status().isPayloadTooLarge()).andExpect(jsonPath("$.message").value("A imagem deve ter até 5 MB."));
    }
    @Test void nomesArbitrariosENaoExistentesNaoExpoemArquivos() throws Exception {
        mvc.perform(get("/uploads/anuncios/application.properties")).andExpect(status().isNotFound());
        mvc.perform(get("/uploads/anuncios/00000000-0000-0000-0000-000000000000.png")).andExpect(status().isNotFound());
    }
}
