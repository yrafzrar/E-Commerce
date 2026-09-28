package eCommerce.GamesRun.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import eCommerce.GamesRun.domain.Anuncio;
import eCommerce.GamesRun.service.AnuncioService;

@RestController
@RequestMapping("/api/anuncios")
public class AnuncioController {

    private final AnuncioService anuncioService;

    public AnuncioController(AnuncioService anuncioService) {
        this.anuncioService = anuncioService;
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Anuncio>> listarTodos() {
        return ResponseEntity.ok(anuncioService.buscarTodos());
    }

    @GetMapping("/listar/{id}")
    public ResponseEntity<Anuncio> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(anuncioService.buscarPorId(id));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Anuncio>> buscarPorNome(@RequestParam String produto) {
        return ResponseEntity.ok(anuncioService.buscarPorNome(produto));
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<Anuncio> criar(@RequestBody(required = false) Anuncio anuncio) {
        return ResponseEntity.ok(anuncioService.salvar(anuncio));
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<Anuncio> atualizar(@PathVariable Long id, @RequestBody(required = false) Anuncio anuncio) {
        return ResponseEntity.ok(anuncioService.atualizar(id, anuncio));
    }

    @DeleteMapping("/excluir/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        anuncioService.excluir(id);
        return ResponseEntity.ok().build();
    }
}