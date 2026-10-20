package eCommerce.GamesRun.service;

import java.util.List;
import eCommerce.GamesRun.repository.CarrinhoRepository;
import eCommerce.GamesRun.repository.FavoritoRepository;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import eCommerce.GamesRun.domain.Anuncio;
import eCommerce.GamesRun.exception.ApiException;
import eCommerce.GamesRun.repository.AnuncioRepository;

@Service
public class AnuncioService {

	private final AnuncioRepository anuncioRepository;
    private final CarrinhoRepository carrinhos;
    private final FavoritoRepository favoritos;

	public AnuncioService(AnuncioRepository anuncioRepository, CarrinhoRepository carrinhos, FavoritoRepository favoritos) {
		this.anuncioRepository = anuncioRepository;
        this.carrinhos = carrinhos;
        this.favoritos = favoritos;
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

	public Anuncio atualizar(Long id, Anuncio anuncio, Long usuarioId) {
		validar(anuncio);
		Anuncio existente = buscarPorId(id);
        exigirVendedor(existente, usuarioId);
		existente.setProduto(anuncio.getProduto());
		existente.setDescricao(anuncio.getDescricao());
		existente.setPreco(anuncio.getPreco());
		existente.setEstadoConservacao(anuncio.getEstadoConservacao());
		existente.setImagemUrl(anuncio.getImagemUrl());
		existente.setCategoriaId(anuncio.getCategoriaId());
		// O proprietário original nunca é transferido pelo corpo da requisição.
		return anuncioRepository.save(existente);
	}

	@Transactional
    public void excluir(Long id, Long usuarioId) {
        Anuncio anuncio = buscarPorId(id);
        exigirVendedor(anuncio, usuarioId);
        carrinhos.deleteByAnuncioId(id);
        carrinhos.flush();
        favoritos.deleteByAnuncioId(id);
        favoritos.flush();
        anuncioRepository.delete(anuncio);
    }

    private void exigirVendedor(Anuncio anuncio, Long usuarioId) {
        if (!usuarioId.equals(anuncio.getVendedorId()))
            throw new ApiException(HttpStatus.FORBIDDEN, "Somente o vendedor que criou este anúncio pode alterá-lo ou excluí-lo.");
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
