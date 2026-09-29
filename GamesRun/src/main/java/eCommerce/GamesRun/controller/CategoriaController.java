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

import eCommerce.GamesRun.domain.Categoria;
import eCommerce.GamesRun.service.CategoriaService;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

	private final CategoriaService categoriaService;

	public CategoriaController(CategoriaService categoriaService) {
		this.categoriaService = categoriaService;
	}

	@GetMapping("/listar")
	public ResponseEntity<List<Categoria>> listarTodos() {
		return ResponseEntity.ok(categoriaService.listarTodos());
	}

	@GetMapping("/listar/{id}")
	public ResponseEntity<Categoria> buscarPorId(@PathVariable Long id) {
		return ResponseEntity.ok(categoriaService.buscarPorId(id));
	}

	@PostMapping("/cadastrar")
	public ResponseEntity<Categoria> criar(@RequestBody(required = false) Categoria categoria) {
		return ResponseEntity.ok(categoriaService.salvar(categoria));
	}

	@PutMapping("/atualizar/{id}")
	public ResponseEntity<Categoria> atualizar(@PathVariable Long id, @RequestBody(required = false) Categoria categoria) {
		return ResponseEntity.ok(categoriaService.atualizar(id, categoria));
	}

	@DeleteMapping("/excluir/{id}")
	public ResponseEntity<Void> excluir(@PathVariable Long id) {
		categoriaService.excluir(id);
		return ResponseEntity.ok().build();
	}
}
