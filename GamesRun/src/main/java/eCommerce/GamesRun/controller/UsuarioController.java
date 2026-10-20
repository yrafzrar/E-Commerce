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
import jakarta.servlet.http.HttpServletRequest;
import eCommerce.GamesRun.security.SessaoUsuario;

import eCommerce.GamesRun.domain.Usuario;
import eCommerce.GamesRun.service.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

	private final UsuarioService usuarioService;

	public UsuarioController(UsuarioService usuarioService) {
		this.usuarioService = usuarioService;
	}

	@GetMapping("/listar")
	public ResponseEntity<List<Usuario>> listarTodos() {
		return ResponseEntity.ok(usuarioService.listarTodos());
	}

	@GetMapping("/listar/{id}")
	public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
		return ResponseEntity.ok(usuarioService.buscarPorId(id));
	}

	@PostMapping("/cadastrar")
	public ResponseEntity<Usuario> criar(@RequestBody(required = false) Usuario usuario, HttpServletRequest request) {
        Usuario salvo = usuarioService.salvar(usuario);
        SessaoUsuario.entrar(request, salvo.getId());
        return ResponseEntity.ok(salvo);
	}

	@PostMapping("/login")
	public ResponseEntity<Usuario> login(@RequestBody LoginRequest dados, HttpServletRequest request) {
        Usuario usuario = usuarioService.autenticar(dados.nick(), dados.senha());
        SessaoUsuario.entrar(request, usuario.getId());
        return ResponseEntity.ok(usuario);
	}

	@GetMapping("/sessao")
    public ResponseEntity<Usuario> sessao(HttpServletRequest request) {
        return ResponseEntity.ok(usuarioService.buscarPorId(SessaoUsuario.exigir(request)));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        var session = request.getSession(false);
        if (session != null) session.invalidate();
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/atualizar/{id}")
	public ResponseEntity<Usuario> atualizar(@PathVariable Long id, @RequestBody(required = false) Usuario usuario, HttpServletRequest request) {
        SessaoUsuario.exigirProprio(request, id);
		return ResponseEntity.ok(usuarioService.atualizar(id, usuario));
	}

	@DeleteMapping("/excluir/{id}")
	public ResponseEntity<Void> excluir(@PathVariable Long id, HttpServletRequest request) {
        SessaoUsuario.exigirProprio(request, id);
		usuarioService.excluir(id);
		return ResponseEntity.ok().build();
	}

	public record LoginRequest(String nick, String senha) {}
}
