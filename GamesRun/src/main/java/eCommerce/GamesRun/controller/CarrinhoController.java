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

import eCommerce.GamesRun.domain.Carrinho;
import eCommerce.GamesRun.service.CarrinhoService;

@RestController
@RequestMapping("/api/carrinhos")
public class CarrinhoController {

	private final CarrinhoService carrinhoService;

	public CarrinhoController(CarrinhoService carrinhoService) {
		this.carrinhoService = carrinhoService;
	}

	@GetMapping("/listar")
	public ResponseEntity<List<Carrinho>> listarTodos() {
		return ResponseEntity.ok(carrinhoService.listarTodos());
	}

	@GetMapping("/usuario/{usuarioId}")
	public ResponseEntity<List<Carrinho>> listarPorUsuario(@PathVariable Long usuarioId) {
		return ResponseEntity.ok(carrinhoService.listarPorUsuario(usuarioId));
	}

	@PostMapping("/usuario/{usuarioId}/itens")
	public ResponseEntity<Carrinho> adicionarItem(@PathVariable Long usuarioId, @RequestBody ItemCarrinhoRequest request) {
		return ResponseEntity.ok(carrinhoService.adicionar(usuarioId, request.anuncioId(), request.quantidade()));
	}

	@PutMapping("/usuario/{usuarioId}/itens/{anuncioId}")
	public ResponseEntity<Carrinho> definirQuantidade(@PathVariable Long usuarioId, @PathVariable Long anuncioId,
			@RequestParam int quantidade) {
		return ResponseEntity.ok(carrinhoService.definirQuantidade(usuarioId, anuncioId, quantidade));
	}

	@DeleteMapping("/usuario/{usuarioId}/itens/{anuncioId}")
	public ResponseEntity<Void> removerItem(@PathVariable Long usuarioId, @PathVariable Long anuncioId) {
		carrinhoService.removerItem(usuarioId, anuncioId);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/listar/{id}")
	public ResponseEntity<Carrinho> buscarPorId(@PathVariable Long id) {
		return ResponseEntity.ok(carrinhoService.buscarPorId(id));
	}

	@PostMapping("/cadastrar")
	public ResponseEntity<Carrinho> criar(@RequestBody(required = false) Carrinho carrinho) {
		return ResponseEntity.ok(carrinhoService.salvar(carrinho));
	}

	@PutMapping("/atualizar/{id}")
	public ResponseEntity<Carrinho> atualizar(@PathVariable Long id, @RequestBody(required = false) Carrinho carrinho) {
		return ResponseEntity.ok(carrinhoService.atualizar(id, carrinho));
	}

	@DeleteMapping("/excluir/{id}")
	public ResponseEntity<Void> excluir(@PathVariable Long id) {
		carrinhoService.excluir(id);
		return ResponseEntity.ok().build();
	}

	public record ItemCarrinhoRequest(Long anuncioId, int quantidade) {}
}
