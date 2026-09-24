package eCommerce.GamesRun.service;

import eCommerce.GamesRun.domain.Carrinho;
import eCommerce.GamesRun.repository.CarrinhoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Carrinho nao encontrado"));
	}

	public Carrinho salvar(Carrinho carrinho) {
		return carrinhoRepository.save(carrinho);
	}

	public Carrinho atualizar(Long id, Carrinho carrinho) {
		Carrinho existente = buscarPorId(id);
		existente.setUsuarioId(carrinho.getUsuarioId());
		existente.setAnuncioId(carrinho.getAnuncioId());
		existente.setQuantidade(carrinho.getQuantidade());
		return carrinhoRepository.save(existente);
	}

	public void excluir(Long id) {
		carrinhoRepository.delete(buscarPorId(id));
	}
}
