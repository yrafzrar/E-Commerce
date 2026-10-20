package eCommerce.GamesRun.controller;

import eCommerce.GamesRun.security.SessaoUsuario;
import eCommerce.GamesRun.service.FavoritoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/favoritos")
public class FavoritoController {
    private final FavoritoService favoritos;
    public FavoritoController(FavoritoService favoritos) { this.favoritos = favoritos; }

    @GetMapping
    public List<Long> listar(HttpServletRequest request) {
        return favoritos.listar(SessaoUsuario.exigir(request));
    }
    @PutMapping("/{anuncioId}")
    public ResponseEntity<Void> adicionar(@PathVariable Long anuncioId, HttpServletRequest request) {
        favoritos.adicionar(SessaoUsuario.exigir(request), anuncioId);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/{anuncioId}")
    public ResponseEntity<Void> remover(@PathVariable Long anuncioId, HttpServletRequest request) {
        favoritos.remover(SessaoUsuario.exigir(request), anuncioId);
        return ResponseEntity.noContent().build();
    }
}
