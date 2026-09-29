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

import java.util.List;

import eCommerce.GamesRun.domain.AvaliacaoVendedor;
import eCommerce.GamesRun.service.AvaliacaoVendedorService;

@RestController
@RequestMapping("/api/avaliacoes")
public class AvaliacaoVendedorController {

    private final AvaliacaoVendedorService avaliacaoService;

    public AvaliacaoVendedorController(AvaliacaoVendedorService avaliacaoService) {
        this.avaliacaoService = avaliacaoService;
    }

    @GetMapping("/listar")
    public ResponseEntity<List<AvaliacaoVendedor>> listarTodos() {
        return ResponseEntity.ok(avaliacaoService.listarTodos());
    }

    @GetMapping("/listar/{id}")
    public ResponseEntity<AvaliacaoVendedor> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(avaliacaoService.buscarPorId(id));
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<AvaliacaoVendedor> criar(@RequestBody(required = false) AvaliacaoVendedor avaliacao) {
        return ResponseEntity.ok(avaliacaoService.salvar(avaliacao));
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<AvaliacaoVendedor> atualizar(@PathVariable Long id, @RequestBody(required = false) AvaliacaoVendedor avaliacao) {
        return ResponseEntity.ok(avaliacaoService.atualizar(id, avaliacao));
    }

    @DeleteMapping("/excluir/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        avaliacaoService.excluir(id);
        return ResponseEntity.ok().build();
    }
}