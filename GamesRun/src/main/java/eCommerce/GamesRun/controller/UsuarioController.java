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

import eCommerce.GamesRun.domain.Usuario;
import eCommerce.GamesRun.exception.ApiException;
import eCommerce.GamesRun.service.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

	private final UsuarioService usuarioService;

	public UsuarioController(UsuarioService usuarioService) {
		this.usuarioService = usuarioService;
	}

	@GetMapping
	public ResponseEntity<Object> listarTodos() {
		return ResponseEntity.ok(usuarioService.listarTodos());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Object> buscarPorId(@PathVariable Long id) {
		try {
			return ResponseEntity.ok(usuarioService.buscarPorId(id));
		} catch (ApiException exception) {
			return exception.toResponseEntity();
		}
	}

	@PostMapping
	public ResponseEntity<Object> criar(@RequestBody(required = false) Usuario usuario) {
		try {
			return ResponseEntity.ok(usuarioService.salvar(usuario));
		} catch (ApiException exception) {
			return exception.toResponseEntity();
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<Object> atualizar(@PathVariable Long id, @RequestBody(required = false) Usuario usuario) {
		try {
			return ResponseEntity.ok(usuarioService.atualizar(id, usuario));
		} catch (ApiException exception) {
			return exception.toResponseEntity();
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Object> excluir(@PathVariable Long id) {
		try {
			usuarioService.excluir(id);
			return ResponseEntity.ok(null);
		} catch (ApiException exception) {
			return exception.toResponseEntity();
		}
	}
}
