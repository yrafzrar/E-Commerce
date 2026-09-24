package eCommerce.GamesRun.controller;

import eCommerce.GamesRun.domain.AvaliacaoVendedor;
import eCommerce.GamesRun.service.AvaliacaoVendedorService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/avaliacoes")
public class AvaliacaoVendedorController {

    private final AvaliacaoVendedorService avaliacaoService;

    public AvaliacaoVendedorController(AvaliacaoVendedorService avaliacaoService) {
        this.avaliacaoService = avaliacaoService;
    }

    @GetMapping
    public List<AvaliacaoVendedor> listarTodos() {
        return avaliacaoService.listarTodos();
    }

    @GetMapping("/{id}")
    public AvaliacaoVendedor buscarPorId(@PathVariable Long id) {
        return avaliacaoService.buscarPorId(id);
    }

    @PostMapping
    public AvaliacaoVendedor criar(@RequestBody AvaliacaoVendedor avaliacao) {
        return avaliacaoService.salvar(avaliacao);
    }

    @PutMapping("/{id}")
    public AvaliacaoVendedor atualizar(@PathVariable Long id, @RequestBody AvaliacaoVendedor avaliacao) {
        return avaliacaoService.atualizar(id, avaliacao);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        avaliacaoService.excluir(id);
    }
}