package eCommerce.GamesRun.controller;

import eCommerce.GamesRun.security.SessaoUsuario;
import eCommerce.GamesRun.service.ImagemService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.concurrent.TimeUnit;

@RestController
public class ImagemController {
    private final ImagemService imagens;
    public ImagemController(ImagemService imagens) { this.imagens = imagens; }

    @PostMapping(value = "/api/imagens", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImagemEnviada> enviar(@RequestParam("arquivo") MultipartFile arquivo,
                                                HttpServletRequest request) {
        SessaoUsuario.exigir(request);
        return ResponseEntity.ok(new ImagemEnviada(imagens.salvar(arquivo)));
    }

    @GetMapping("/uploads/anuncios/{nome}")
    public ResponseEntity<Resource> imagem(@PathVariable String nome) {
        var arquivo = imagens.localizar(nome);
        return ResponseEntity.ok().header("X-Content-Type-Options", "nosniff")
                .cacheControl(CacheControl.maxAge(365, TimeUnit.DAYS).cachePublic().immutable())
                .contentType(nome.endsWith(".png") ? MediaType.IMAGE_PNG : MediaType.IMAGE_JPEG)
                .body(new FileSystemResource(arquivo));
    }

    public record ImagemEnviada(String imagemUrl) {}
}
