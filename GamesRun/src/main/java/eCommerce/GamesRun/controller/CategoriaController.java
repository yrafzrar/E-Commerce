package eCommerce.GamesRun.controller;

import eCommerce.GamesRun.domain.Categoria;
import eCommerce.GamesRun.service.CategoriaService;
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
@RequestMapping("/api/categorias")
public class CategoriaController {

	private final CategoriaService categoriaService;

	public CategoriaController(CategoriaService categoriaService) {
		this.categoriaService = categoriaService;
	}

	@GetMapping
	public List<Categoria> listarTodos() {
		return categoriaService.listarTodos();
	}

	@GetMapping("/{id}")
	public Categoria buscarPorId(@PathVariable Long id) {
		return categoriaService.buscarPorId(id);
	}

	@PostMapping
	public Categoria criar(@RequestBody Categoria categoria) {
		return categoriaService.salvar(categoria);
	}

	@PutMapping("/{id}")
	public Categoria atualizar(@PathVariable Long id, @RequestBody Categoria categoria) {
		return categoriaService.atualizar(id, categoria);
	}

	@DeleteMapping("/{id}")
	public void excluir(@PathVariable Long id) {
		categoriaService.excluir(id);
	}
}
