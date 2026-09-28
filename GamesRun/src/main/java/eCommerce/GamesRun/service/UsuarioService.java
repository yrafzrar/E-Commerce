package eCommerce.GamesRun.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import eCommerce.GamesRun.domain.Usuario;
import eCommerce.GamesRun.exception.ApiException;
import eCommerce.GamesRun.repository.UsuarioRepository;

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
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario nao encontrado"));
	}

	public Usuario salvar(Usuario usuario) {
		validar(usuario);
		return usuarioRepository.save(usuario);
	}

	public Usuario atualizar(Long id, Usuario usuario) {
		validar(usuario);
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

	private void validar(Usuario usuario) {
		if (usuario == null) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Usuario nao pode ser nulo");
		}
		if (usuario.getNome() == null || usuario.getNome().isBlank()) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Nome do usuario e obrigatorio");
		}
		if (usuario.getNick() == null || usuario.getNick().isBlank()) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Nick do usuario e obrigatorio");
		}
		if (usuario.getSenha() == null || usuario.getSenha().isBlank()) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Senha do usuario e obrigatoria");
		}
		if (usuario.getIdade() <= 0 || usuario.getCpf() <= 0) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Idade e CPF devem ser maiores que zero");
		}
	}
}
