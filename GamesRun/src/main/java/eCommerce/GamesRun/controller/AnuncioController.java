package eCommerce.GamesRun.controller;

import eCommerce.GamesRun.domain.Anuncio;
import eCommerce.GamesRun.service.AnuncioService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/anuncios")
public class AnuncioController {

    private final AnuncioService anuncioService;

    public AnuncioController(AnuncioService anuncioService) {
        this.anuncioService = anuncioService;
    }

    @GetMapping
    public List<Anuncio> listarTodos() {
        return anuncioService.buscarTodos();
    }

    @GetMapping("/{id}")
    public Anuncio buscarPorId(@PathVariable Long id) {
        return anuncioService.buscarPorId(id);
    }

    @GetMapping("/busca")
    public List<Anuncio> buscarPorNome(@RequestParam String produto) {
        return anuncioService.buscarPorNome(produto);
    }

    @PostMapping
    public Anuncio criar(@RequestBody Anuncio anuncio) {
        return anuncioService.salvar(anuncio);
    }

    @PutMapping("/{id}")
    public Anuncio atualizar(@PathVariable Long id, @RequestBody Anuncio anuncio) {
        return anuncioService.atualizar(id, anuncio);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        anuncioService.excluir(id);
    }
}