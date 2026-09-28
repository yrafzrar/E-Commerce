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
}
