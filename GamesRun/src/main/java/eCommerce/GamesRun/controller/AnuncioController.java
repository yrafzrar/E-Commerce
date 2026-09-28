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

import eCommerce.GamesRun.domain.Anuncio;
import eCommerce.GamesRun.exception.ApiException;
import eCommerce.GamesRun.service.AnuncioService;

@RestController
@RequestMapping("/api/anuncios")
public class AnuncioController {

    private final AnuncioService anuncioService;

    public AnuncioController(AnuncioService anuncioService) {
        this.anuncioService = anuncioService;
    }

    @GetMapping
    public ResponseEntity<Object> listarTodos() {
        return ResponseEntity.ok(anuncioService.buscarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> buscarPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(anuncioService.buscarPorId(id));
        } catch (ApiException exception) {
            return exception.toResponseEntity();
        }
    }

    @GetMapping("/busca")
    public ResponseEntity<Object> buscarPorNome(@RequestParam String produto) {
        return ResponseEntity.ok(anuncioService.buscarPorNome(produto));
    }

    @PostMapping
    public ResponseEntity<Object> criar(@RequestBody(required = false) Anuncio anuncio) {
        try {
            return ResponseEntity.ok(anuncioService.salvar(anuncio));
        } catch (ApiException exception) {
            return exception.toResponseEntity();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> atualizar(@PathVariable Long id, @RequestBody(required = false) Anuncio anuncio) {
        try {
            return ResponseEntity.ok(anuncioService.atualizar(id, anuncio));
        } catch (ApiException exception) {
            return exception.toResponseEntity();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> excluir(@PathVariable Long id) {
        try {
            anuncioService.excluir(id);
            return ResponseEntity.ok(null);
        } catch (ApiException exception) {
            return exception.toResponseEntity();
        }
    }
}