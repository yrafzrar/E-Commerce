package eCommerce.GamesRun.service;

import eCommerce.GamesRun.domain.Anuncio;
import eCommerce.GamesRun.repository.AnuncioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AnuncioService {

	private final AnuncioRepository anuncioRepository;

	public AnuncioService(AnuncioRepository anuncioRepository) {
		this.anuncioRepository = anuncioRepository;
	}

	public List<Anuncio> buscarTodos() {
		return anuncioRepository.findAll();
	}

	public Anuncio buscarPorId(Long id) {
		return anuncioRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Anuncio nao encontrado"));
	}

	public List<Anuncio> buscarPorNome(String produto) {
		return anuncioRepository.findByProdutoContainingIgnoreCase(produto);
	}

	public Anuncio salvar(Anuncio anuncio) {
		return anuncioRepository.save(anuncio);
	}

	public Anuncio atualizar(Long id, Anuncio anuncio) {
		Anuncio existente = buscarPorId(id);
		existente.setProduto(anuncio.getProduto());
		existente.setDescricao(anuncio.getDescricao());
		existente.setPreco(anuncio.getPreco());
		return anuncioRepository.save(existente);
	}

	public void excluir(Long id) {
		anuncioRepository.delete(buscarPorId(id));
	}

}