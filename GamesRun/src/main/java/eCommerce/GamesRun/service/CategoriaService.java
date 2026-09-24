package eCommerce.GamesRun.service;

import eCommerce.GamesRun.domain.Categoria;
import eCommerce.GamesRun.repository.CategoriaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CategoriaService {

	private final CategoriaRepository categoriaRepository;

	public CategoriaService(CategoriaRepository categoriaRepository) {
		this.categoriaRepository = categoriaRepository;
	}

	public List<Categoria> listarTodos() {
		return categoriaRepository.findAll();
	}

	public Categoria buscarPorId(Long id) {
		return categoriaRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria nao encontrada"));
	}

	public Categoria salvar(Categoria categoria) {
		return categoriaRepository.save(categoria);
	}

	public Categoria atualizar(Long id, Categoria categoria) {
		Categoria existente = buscarPorId(id);
		existente.setNome(categoria.getNome());
		existente.setDescricao(categoria.getDescricao());
		return categoriaRepository.save(existente);
	}

	public void excluir(Long id) {
		categoriaRepository.delete(buscarPorId(id));
	}
}
