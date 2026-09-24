package eCommerce.GamesRun.controller;

import eCommerce.GamesRun.domain.Pedido;
import eCommerce.GamesRun.service.PedidoService;
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
@RequestMapping("/api/pedidos")
public class PedidoController {

	private final PedidoService pedidoService;

	public PedidoController(PedidoService pedidoService) {
		this.pedidoService = pedidoService;
	}

	@GetMapping
	public List<Pedido> listarTodos() {
		return pedidoService.listarTodos();
	}

	@GetMapping("/{id}")
	public Pedido buscarPorId(@PathVariable Long id) {
		return pedidoService.buscarPorId(id);
	}

	@PostMapping
	public Pedido criar(@RequestBody Pedido pedido) {
		return pedidoService.salvar(pedido);
	}

	@PutMapping("/{id}")
	public Pedido atualizar(@PathVariable Long id, @RequestBody Pedido pedido) {
		return pedidoService.atualizar(id, pedido);
	}

	@DeleteMapping("/{id}")
	public void excluir(@PathVariable Long id) {
		pedidoService.excluir(id);
	}
}
