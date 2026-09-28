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

import eCommerce.GamesRun.domain.Pedido;
import eCommerce.GamesRun.exception.ApiException;
import eCommerce.GamesRun.service.PedidoService;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

	private final PedidoService pedidoService;

	public PedidoController(PedidoService pedidoService) {
		this.pedidoService = pedidoService;
	}

	@GetMapping
	public ResponseEntity<Object> listarTodos() {
		return ResponseEntity.ok(pedidoService.listarTodos());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Object> buscarPorId(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(pedidoService.buscarPorId(id));
		} catch (ApiException exception) {
			return exception.toResponseEntity();
		}
	}

	@PostMapping
	public ResponseEntity<Object> criar(@RequestBody(required = false) Pedido pedido) {
		try {
			return ResponseEntity.ok(pedidoService.salvar(pedido));
		} catch (ApiException exception) {
			return exception.toResponseEntity();
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<Object> atualizar(@PathVariable Long id, @RequestBody(required = false) Pedido pedido) {
		try {
			return ResponseEntity.ok(pedidoService.atualizar(id, pedido));
		} catch (ApiException exception) {
			return exception.toResponseEntity();
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Object> excluir(@PathVariable Long id) {
		try {
			pedidoService.excluir(id);
			return ResponseEntity.ok(null);
		} catch (ApiException exception) {
			return exception.toResponseEntity();
		}
	}
}
