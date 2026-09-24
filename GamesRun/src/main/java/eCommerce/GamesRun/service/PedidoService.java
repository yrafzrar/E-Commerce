package eCommerce.GamesRun.service;

import eCommerce.GamesRun.domain.Pedido;
import eCommerce.GamesRun.repository.PedidoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PedidoService {

	private final PedidoRepository pedidoRepository;

	public PedidoService(PedidoRepository pedidoRepository) {
		this.pedidoRepository = pedidoRepository;
	}

	public List<Pedido> listarTodos() {
		return pedidoRepository.findAll();
	}

	public Pedido buscarPorId(Long id) {
		return pedidoRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido nao encontrado"));
	}

	public Pedido salvar(Pedido pedido) {
		return pedidoRepository.save(pedido);
	}

	public Pedido atualizar(Long id, Pedido pedido) {
		Pedido existente = buscarPorId(id);
		existente.setCompradorId(pedido.getCompradorId());
		existente.setStatus(pedido.getStatus());
		existente.setDataCriacao(pedido.getDataCriacao());
		existente.setValorTotal(pedido.getValorTotal());
		return pedidoRepository.save(existente);
	}

	public void excluir(Long id) {
		pedidoRepository.delete(buscarPorId(id));
	}
}
