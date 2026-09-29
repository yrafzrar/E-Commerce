package eCommerce.GamesRun.service;

import eCommerce.GamesRun.domain.Carrinho;
import eCommerce.GamesRun.exception.ApiException;
import eCommerce.GamesRun.repository.CarrinhoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarrinhoService {

	private final CarrinhoRepository carrinhoRepository;

	public CarrinhoService(CarrinhoRepository carrinhoRepository) {
		this.carrinhoRepository = carrinhoRepository;
	}

	public List<Carrinho> listarTodos() {
		return carrinhoRepository.findAll();
	}

	public Carrinho buscarPorId(Long id) {
		return carrinhoRepository.findById(id)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Carrinho nao encontrado"));
	}

	public Carrinho salvar(Carrinho carrinho) {
		validar(carrinho);
		return carrinhoRepository.save(carrinho);
	}

	public List<Carrinho> listarPorUsuario(Long usuarioId) {
		return carrinhoRepository.findByUsuarioId(usuarioId);
	}

	public Carrinho adicionar(Long usuarioId, Long anuncioId, int quantidade) {
		Carrinho carrinho = carrinhoRepository.findByUsuarioIdAndAnuncioId(usuarioId, anuncioId).orElse(null);
		boolean novo = carrinho == null;
		if (novo) {
			carrinho = new Carrinho();
		}
		carrinho.setUsuarioId(usuarioId);
		carrinho.setAnuncioId(anuncioId);
		carrinho.setQuantidade(novo ? quantidade : carrinho.getQuantidade() + quantidade);
		validar(carrinho);
		return carrinhoRepository.save(carrinho);
	}

	public Carrinho definirQuantidade(Long usuarioId, Long anuncioId, int quantidade) {
		Carrinho carrinho = carrinhoRepository.findByUsuarioIdAndAnuncioId(usuarioId, anuncioId)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Item nao encontrado no carrinho"));
		carrinho.setQuantidade(quantidade);
		validar(carrinho);
		return carrinhoRepository.save(carrinho);
	}

	public void removerItem(Long usuarioId, Long anuncioId) {
		Carrinho carrinho = carrinhoRepository.findByUsuarioIdAndAnuncioId(usuarioId, anuncioId)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Item nao encontrado no carrinho"));
		carrinhoRepository.delete(carrinho);
	}

	public Carrinho atualizar(Long id, Carrinho carrinho) {
		validar(carrinho);
		Carrinho existente = buscarPorId(id);
		existente.setUsuarioId(carrinho.getUsuarioId());
		existente.setAnuncioId(carrinho.getAnuncioId());
		existente.setQuantidade(carrinho.getQuantidade());
		return carrinhoRepository.save(existente);
	}

	public void excluir(Long id) {
		carrinhoRepository.delete(buscarPorId(id));
	}

	private void validar(Carrinho carrinho) {
		if (carrinho == null) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Carrinho nao pode ser nulo");
		}
		if (carrinho.getUsuarioId() == null || carrinho.getUsuarioId() <= 0) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "ID do usuario deve ser maior que zero");
		}
		if (carrinho.getAnuncioId() == null || carrinho.getAnuncioId() <= 0) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "ID do anuncio deve ser maior que zero");
		}
		if (carrinho.getQuantidade() < 1) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Quantidade deve ser maior que zero");
		}
	}
}
