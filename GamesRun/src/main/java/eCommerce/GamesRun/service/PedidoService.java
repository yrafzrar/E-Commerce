package eCommerce.GamesRun.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import eCommerce.GamesRun.domain.Pedido;
import eCommerce.GamesRun.exception.ApiException;
import eCommerce.GamesRun.repository.PedidoRepository;

@Service
public class PedidoService {

	private final PedidoRepository pedidoRepository;

	public PedidoService(PedidoRepository pedidoRepository) {
		this.pedidoRepository = pedidoRepository;
	}

	public List<Pedido> listarTodos() {
		return pedidoRepository.findAll();
	}

	public List<Pedido> listarPorComprador(Long compradorId) {
		return pedidoRepository.findByCompradorIdOrderByIdDesc(compradorId);
	}

	public Pedido buscarPorId(Long id) {
		return pedidoRepository.findById(id)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Pedido nao encontrado"));
	}

	public Pedido salvar(Pedido pedido) {
		validar(pedido);
		return pedidoRepository.save(pedido);
	}

	public Pedido atualizar(Long id, Pedido pedido) {
		validar(pedido);
		Pedido existente = buscarPorId(id);
		existente.setCompradorId(pedido.getCompradorId());
		existente.setStatus(pedido.getStatus());
		existente.setDataCriacao(pedido.getDataCriacao());
		existente.setValorTotal(pedido.getValorTotal());
		existente.setFormaPagamento(pedido.getFormaPagamento());
		existente.setEnderecoEntrega(pedido.getEnderecoEntrega());
		existente.setItensResumo(pedido.getItensResumo());
		return pedidoRepository.save(existente);
	}

	public void excluir(Long id) {
		pedidoRepository.delete(buscarPorId(id));
	}

	private void validar(Pedido pedido) {
		if (pedido == null) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Pedido nao pode ser nulo");
		}
		if (pedido.getCompradorId() == null || pedido.getCompradorId() <= 0) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "ID do comprador deve ser maior que zero");
		}
		if (pedido.getStatus() == null || pedido.getStatus().isBlank()) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Status do pedido e obrigatorio");
		}
		if (pedido.getValorTotal() < 0) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Valor total nao pode ser negativo");
		}
	}
}
