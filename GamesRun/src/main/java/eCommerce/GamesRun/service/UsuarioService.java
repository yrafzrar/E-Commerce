package eCommerce.GamesRun.service;

import eCommerce.GamesRun.domain.Usuario;
import eCommerce.GamesRun.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UsuarioService {

	private final UsuarioRepository usuarioRepository;

	public UsuarioService(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}

	public List<Usuario> listarTodos() {
		return usuarioRepository.findAll();
	}

	public Usuario buscarPorId(Long id) {
		return usuarioRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario nao encontrado"));
	}

	public Usuario salvar(Usuario usuario) {
		return usuarioRepository.save(usuario);
	}

	public Usuario atualizar(Long id, Usuario usuario) {
		Usuario existente = buscarPorId(id);
		existente.setNome(usuario.getNome());
		existente.setNick(usuario.getNick());
		existente.setSenha(usuario.getSenha());
		existente.setIdade(usuario.getIdade());
		existente.setCpf(usuario.getCpf());
		return usuarioRepository.save(existente);
	}

	public void excluir(Long id) {
		usuarioRepository.delete(buscarPorId(id));
	}
}
