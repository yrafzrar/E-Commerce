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

import eCommerce.GamesRun.domain.Produto;
import eCommerce.GamesRun.exception.ApiException;
import eCommerce.GamesRun.service.ProdutoService;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

	private final ProdutoService produtoService;

	public ProdutoController(ProdutoService produtoService) {
		this.produtoService = produtoService;
	}

	@GetMapping("/listar")
	public ResponseEntity<Object> listarTodos() {
		return ResponseEntity.ok(produtoService.listarTodos());
	}

	@GetMapping("/listar/{id}")
	public ResponseEntity<Object> buscarPorId(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(produtoService.buscarPorId(id));
		} catch (ApiException exception) {
			return exception.toResponseEntity();
		}
	}

	@PostMapping("/cadastrar")
	public ResponseEntity<Object> criar(@RequestBody(required = false) Produto produto) {
		try {
			return ResponseEntity.ok(produtoService.salvar(produto));
		} catch (ApiException exception) {
			return exception.toResponseEntity();
		}
	}

	@PutMapping("/atualizar/{id}")
	public ResponseEntity<Object> atualizar(@PathVariable Long id, @RequestBody(required = false) Produto produto) {
		try {
			return ResponseEntity.ok(produtoService.atualizar(id, produto));
		} catch (ApiException exception) {
			return exception.toResponseEntity();
		}
	}

	@DeleteMapping("/excluir/{id}")
	public ResponseEntity<Object> excluir(@PathVariable Long id) {
		try {
			produtoService.excluir(id);
			return ResponseEntity.ok(null);
		} catch (ApiException exception) {
			return exception.toResponseEntity();
		}
	}
}
