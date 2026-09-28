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

import eCommerce.GamesRun.domain.Categoria;
import eCommerce.GamesRun.exception.ApiException;
import eCommerce.GamesRun.service.CategoriaService;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

	private final CategoriaService categoriaService;

	public CategoriaController(CategoriaService categoriaService) {
		this.categoriaService = categoriaService;
	}

	@GetMapping
	public ResponseEntity<Object> listarTodos() {
		return ResponseEntity.ok(categoriaService.listarTodos());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Object> buscarPorId(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(categoriaService.buscarPorId(id));
		} catch (ApiException exception) {
			return exception.toResponseEntity();
		}
	}

	@PostMapping
	public ResponseEntity<Object> criar(@RequestBody(required = false) Categoria categoria) {
		try {
			return ResponseEntity.ok(categoriaService.salvar(categoria));
		} catch (ApiException exception) {
			return exception.toResponseEntity();
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<Object> atualizar(@PathVariable Long id, @RequestBody(required = false) Categoria categoria) {
		try {
			return ResponseEntity.ok(categoriaService.atualizar(id, categoria));
		} catch (ApiException exception) {
			return exception.toResponseEntity();
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Object> excluir(@PathVariable Long id) {
		try {
			categoriaService.excluir(id);
			return ResponseEntity.ok(null);
		} catch (ApiException exception) {
			return exception.toResponseEntity();
		}
	}
}
