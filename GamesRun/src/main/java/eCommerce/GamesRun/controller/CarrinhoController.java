package eCommerce.GamesRun.controller;

import eCommerce.GamesRun.domain.Carrinho;
import eCommerce.GamesRun.service.CarrinhoService;
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
@RequestMapping("/api/carrinhos")
public class CarrinhoController {

	private final CarrinhoService carrinhoService;

	public CarrinhoController(CarrinhoService carrinhoService) {
		this.carrinhoService = carrinhoService;
	}

	@GetMapping
	public List<Carrinho> listarTodos() {
		return carrinhoService.listarTodos();
	}

	@GetMapping("/{id}")
	public Carrinho buscarPorId(@PathVariable Long id) {
		return carrinhoService.buscarPorId(id);
	}

	@PostMapping
	public Carrinho criar(@RequestBody Carrinho carrinho) {
		return carrinhoService.salvar(carrinho);
	}

	@PutMapping("/{id}")
	public Carrinho atualizar(@PathVariable Long id, @RequestBody Carrinho carrinho) {
		return carrinhoService.atualizar(id, carrinho);
	}

	@DeleteMapping("/{id}")
	public void excluir(@PathVariable Long id) {
		carrinhoService.excluir(id);
	}
}
