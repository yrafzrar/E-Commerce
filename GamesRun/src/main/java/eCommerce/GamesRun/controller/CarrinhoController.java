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

import eCommerce.GamesRun.domain.Carrinho;
import eCommerce.GamesRun.exception.ApiException;
import eCommerce.GamesRun.service.CarrinhoService;

@RestController
@RequestMapping("/api/carrinhos")
public class CarrinhoController {

	private final CarrinhoService carrinhoService;

	public CarrinhoController(CarrinhoService carrinhoService) {
		this.carrinhoService = carrinhoService;
	}

	@GetMapping
	public ResponseEntity<Object> listarTodos() {
		return ResponseEntity.ok(carrinhoService.listarTodos());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Object> buscarPorId(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(carrinhoService.buscarPorId(id));
		} catch (ApiException exception) {
			return exception.toResponseEntity();
		}
	}

	@PostMapping
	public ResponseEntity<Object> criar(@RequestBody(required = false) Carrinho carrinho) {
		try {
			return ResponseEntity.ok(carrinhoService.salvar(carrinho));
		} catch (ApiException exception) {
			return exception.toResponseEntity();
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<Object> atualizar(@PathVariable Long id, @RequestBody(required = false) Carrinho carrinho) {
		try {
			return ResponseEntity.ok(carrinhoService.atualizar(id, carrinho));
		} catch (ApiException exception) {
			return exception.toResponseEntity();
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Object> excluir(@PathVariable Long id) {
		try {
			carrinhoService.excluir(id);
			return ResponseEntity.ok(null);
		} catch (ApiException exception) {
			return exception.toResponseEntity();
		}
	}
}
