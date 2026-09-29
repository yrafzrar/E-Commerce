package eCommerce.GamesRun.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import eCommerce.GamesRun.domain.Categoria;
import eCommerce.GamesRun.exception.ApiException;
import eCommerce.GamesRun.repository.CategoriaRepository;

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
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Categoria nao encontrada"));
	}

	public Categoria salvar(Categoria categoria) {
		validar(categoria);
		return categoriaRepository.save(categoria);
	}

	public Categoria atualizar(Long id, Categoria categoria) {
		validar(categoria);
		Categoria existente = buscarPorId(id);
		existente.setNome(categoria.getNome());
		existente.setDescricao(categoria.getDescricao());
		return categoriaRepository.save(existente);
	}

	public void excluir(Long id) {
		categoriaRepository.delete(buscarPorId(id));
	}

	private void validar(Categoria categoria) {
		if (categoria == null) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Categoria nao pode ser nula");
		}
		if (categoria.getNome() == null || categoria.getNome().isBlank()) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Nome da categoria e obrigatorio");
		}
	}
}
