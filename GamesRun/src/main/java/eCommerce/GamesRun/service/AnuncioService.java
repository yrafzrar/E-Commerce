package eCommerce.GamesRun.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import eCommerce.GamesRun.domain.Anuncio;
import eCommerce.GamesRun.exception.ApiException;
import eCommerce.GamesRun.repository.AnuncioRepository;

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
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Anuncio nao encontrado"));
	}

	public List<Anuncio> buscarPorNome(String produto) {
		return anuncioRepository.findByProdutoContainingIgnoreCase(produto);
	}

	public Anuncio salvar(Anuncio anuncio) {
		validar(anuncio);
		return anuncioRepository.save(anuncio);
	}

	public Anuncio atualizar(Long id, Anuncio anuncio) {
		validar(anuncio);
		Anuncio existente = buscarPorId(id);
		existente.setProduto(anuncio.getProduto());
		existente.setDescricao(anuncio.getDescricao());
		existente.setPreco(anuncio.getPreco());
		return anuncioRepository.save(existente);
	}

	public void excluir(Long id) {
		anuncioRepository.delete(buscarPorId(id));
	}

	private void validar(Anuncio anuncio) {
		if (anuncio == null) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Anuncio nao pode ser nulo");
		}
		if (anuncio.getProduto() == null || anuncio.getProduto().isBlank()) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Nome do produto e obrigatorio");
		}
		if (anuncio.getPreco() <= 0) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Preco deve ser maior que zero");
		}
	}

}