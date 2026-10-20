package eCommerce.GamesRun.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import eCommerce.GamesRun.security.SessaoUsuario;
import eCommerce.GamesRun.service.PagamentoSimuladoService;

import eCommerce.GamesRun.domain.Pedido;
import eCommerce.GamesRun.service.PedidoService;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

	private final PedidoService pedidoService;
    private final PagamentoSimuladoService pagamentos;

	public PedidoController(PedidoService pedidoService, PagamentoSimuladoService pagamentos) {
		this.pedidoService = pedidoService;
        this.pagamentos = pagamentos;
	}

	@GetMapping("/listar")
	public ResponseEntity<List<Pedido>> listarTodos() {
		return ResponseEntity.ok(pedidoService.listarTodos());
	}

	@GetMapping("/comprador/{compradorId}")
	public ResponseEntity<List<Pedido>> listarPorComprador(@PathVariable Long compradorId) {
		return ResponseEntity.ok(pedidoService.listarPorComprador(compradorId));
	}

	@GetMapping("/listar/{id}")
	public ResponseEntity<Pedido> buscarPorId(@PathVariable Long id) {
		return ResponseEntity.ok(pedidoService.buscarPorId(id));
	}

	@PostMapping("/cadastrar")
	public ResponseEntity<Pedido> criar(@RequestBody(required = false) Pedido pedido, HttpServletRequest request) {
        if(pedido != null) {
            pedido.setCompradorId(SessaoUsuario.exigir(request));
            pedido.setStatus("AGUARDANDO_PAGAMENTO");
            pedido.setCodigoSimulado(null);
            pedido.setPagamentoSimuladoEm(null);
        }
		return ResponseEntity.ok(pedidoService.salvar(pedido));
	}

	@PostMapping("/{id}/simulacao")
    public ResponseEntity<PagamentoSimuladoService.Simulacao> simular(@PathVariable Long id, HttpServletRequest request) {
        return ResponseEntity.ok(pagamentos.abrir(id, SessaoUsuario.exigir(request)));
    }

    @PostMapping("/{id}/simulacao/confirmar")
    public ResponseEntity<Pedido> confirmar(@PathVariable Long id, @RequestBody Confirmacao dados, HttpServletRequest request) {
        return ResponseEntity.ok(pagamentos.confirmar(id, SessaoUsuario.exigir(request), dados.codigo()));
    }

    public record Confirmacao(String codigo) {}

    @PutMapping("/atualizar/{id}")
	public ResponseEntity<Pedido> atualizar(@PathVariable Long id, @RequestBody(required = false) Pedido pedido, HttpServletRequest request) {
        exigirComprador(id, request);
		return ResponseEntity.ok(pedidoService.atualizar(id, pedido));
	}

	@DeleteMapping("/excluir/{id}")
	public ResponseEntity<Void> excluir(@PathVariable Long id, HttpServletRequest request) {
        exigirComprador(id, request);
		pedidoService.excluir(id);
		return ResponseEntity.ok().build();
	}
    private void exigirComprador(Long id, HttpServletRequest request) {
        Long comprador = SessaoUsuario.exigir(request);
        if (!comprador.equals(pedidoService.buscarPorId(id).getCompradorId()))
            throw new eCommerce.GamesRun.exception.ApiException(org.springframework.http.HttpStatus.FORBIDDEN,
                    "Somente o comprador pode alterar este pedido.");
    }
}
