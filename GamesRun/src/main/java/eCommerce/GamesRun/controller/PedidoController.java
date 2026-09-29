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

import eCommerce.GamesRun.domain.Pedido;
import eCommerce.GamesRun.service.PedidoService;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

	private final PedidoService pedidoService;

	public PedidoController(PedidoService pedidoService) {
		this.pedidoService = pedidoService;
	}

	@GetMapping("/listar")
	public ResponseEntity<List<Pedido>> listarTodos() {
		return ResponseEntity.ok(pedidoService.listarTodos());
	}

	@GetMapping("/comprador/{compradorId}")
	public ResponseEntity<List<Pedido>> listarPorComprador(@PathVariable Long compradorId) {
		return ResponseEntity.ok(pedidoService.listarPorComprador(compradorId));
	}

	@GetMapping("/listar/{id}")
	public ResponseEntity<Pedido> buscarPorId(@PathVariable Long id) {
		return ResponseEntity.ok(pedidoService.buscarPorId(id));
	}

	@PostMapping("/cadastrar")
	public ResponseEntity<Pedido> criar(@RequestBody(required = false) Pedido pedido) {
		return ResponseEntity.ok(pedidoService.salvar(pedido));
	}

	@PutMapping("/atualizar/{id}")
	public ResponseEntity<Pedido> atualizar(@PathVariable Long id, @RequestBody(required = false) Pedido pedido) {
		return ResponseEntity.ok(pedidoService.atualizar(id, pedido));
	}

	@DeleteMapping("/excluir/{id}")
	public ResponseEntity<Void> excluir(@PathVariable Long id) {
		pedidoService.excluir(id);
		return ResponseEntity.ok().build();
	}
}
