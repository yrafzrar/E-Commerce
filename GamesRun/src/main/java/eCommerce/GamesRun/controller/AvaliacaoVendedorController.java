package eCommerce.GamesRun.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import eCommerce.GamesRun.domain.AvaliacaoVendedor;
import eCommerce.GamesRun.exception.ApiException;
import eCommerce.GamesRun.service.AvaliacaoVendedorService;

@RestController
@RequestMapping("/api/avaliacoes")
public class AvaliacaoVendedorController {

    private final AvaliacaoVendedorService avaliacaoService;

    public AvaliacaoVendedorController(AvaliacaoVendedorService avaliacaoService) {
        this.avaliacaoService = avaliacaoService;
    }

    @GetMapping
    public ResponseEntity<Object> listarTodos() {
        return ResponseEntity.ok(avaliacaoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> buscarPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(avaliacaoService.buscarPorId(id));
        } catch (ApiException exception) {
            return exception.toResponseEntity();
        }
    }

    @PostMapping
    public ResponseEntity<Object> criar(@RequestBody(required = false) AvaliacaoVendedor avaliacao) {
        try {
            return ResponseEntity.ok(avaliacaoService.salvar(avaliacao));
        } catch (ApiException exception) {
            return exception.toResponseEntity();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> atualizar(@PathVariable Long id, @RequestBody(required = false) AvaliacaoVendedor avaliacao) {
        try {
            return ResponseEntity.ok(avaliacaoService.atualizar(id, avaliacao));
        } catch (ApiException exception) {
            return exception.toResponseEntity();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> excluir(@PathVariable Long id) {
        try {
            avaliacaoService.excluir(id);
            return ResponseEntity.ok(null);
        } catch (ApiException exception) {
            return exception.toResponseEntity();
        }
    }
}