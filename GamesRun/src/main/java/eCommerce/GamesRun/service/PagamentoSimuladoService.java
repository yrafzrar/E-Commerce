package eCommerce.GamesRun.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.QRCodeWriter;
import eCommerce.GamesRun.domain.Pedido;
import eCommerce.GamesRun.exception.ApiException;
import eCommerce.GamesRun.repository.PedidoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import java.security.SecureRandom;

/** Não integra bancos, não cobra valores e não recebe dados de cartão. */
@Service
public class PagamentoSimuladoService {
    public static final String CONFIRMADO = "PAGAMENTO_SIMULADO";
    private final PedidoRepository pedidos;
    private final SecureRandom random = new SecureRandom();

    public PagamentoSimuladoService(PedidoRepository pedidos) { this.pedidos = pedidos; }

    @Transactional
    public Simulacao abrir(Long id, Long compradorId) {
        Pedido pedido = proprio(id, compradorId);
        if (!CONFIRMADO.equals(pedido.getStatus())) {
            if (!"AGUARDANDO_PAGAMENTO".equals(pedido.getStatus()))
                throw new ApiException(HttpStatus.CONFLICT, "Este pedido não está aguardando pagamento.");
            String metodo = pedido.getFormaPagamento();
            if (!java.util.Set.of("pix", "cartao", "boleto").contains(metodo == null ? "" : metodo))
                throw new ApiException(HttpStatus.BAD_REQUEST, "Forma de pagamento inválida.");
            String codigo = "GAMESRUN-" + metodo.toUpperCase() + "-";
            if ("boleto".equals(metodo)) {
                StringBuilder numeros = new StringBuilder();
                for (int i=0;i<47;i++) numeros.append(random.nextInt(10));
                codigo += numeros;
            } else codigo += UUID.randomUUID().toString();
            pedido.setCodigoSimulado(codigo);
            pedidos.save(pedido);
        }
        return representar(pedido);
    }

    @Transactional
    public Pedido confirmar(Long id, Long compradorId, String codigo) {
        Pedido pedido = proprio(id, compradorId);
        if (codigo == null || !codigo.equals(pedido.getCodigoSimulado()))
            throw new ApiException(HttpStatus.CONFLICT, "Este código expirou. Gere os dados de pagamento novamente.");
        if (CONFIRMADO.equals(pedido.getStatus())) return pedido;
        if (!"AGUARDANDO_PAGAMENTO".equals(pedido.getStatus()))
            throw new ApiException(HttpStatus.CONFLICT, "Este pedido não permite confirmar o pagamento.");
        pedido.setStatus(CONFIRMADO);
        pedido.setPagamentoSimuladoEm(Instant.now().toString());
        return pedidos.save(pedido);
    }

    private Pedido proprio(Long id, Long compradorId) {
        Pedido pedido = pedidos.findById(id).orElseThrow(() ->
                new ApiException(HttpStatus.NOT_FOUND, "Pedido não encontrado."));
        if (!compradorId.equals(pedido.getCompradorId()))
            throw new ApiException(HttpStatus.FORBIDDEN, "Somente o comprador pode acessar este pagamento.");
        return pedido;
    }

    private Simulacao representar(Pedido pedido) {
        String qr = null, barras = null;
        if (pedido.getCodigoSimulado() != null && "pix".equals(pedido.getFormaPagamento())) {
            try {
                qr = svg(new QRCodeWriter().encode(pedido.getCodigoSimulado(), BarcodeFormat.QR_CODE,
                        0, 0, Map.of(EncodeHintType.MARGIN, 4)));
            } catch (WriterException e) {
                throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Não foi possível gerar o QR Code.");
            }
        }
        if (pedido.getCodigoSimulado() != null && "boleto".equals(pedido.getFormaPagamento()))
            barras = svg(new Code128Writer().encode(pedido.getCodigoSimulado(), BarcodeFormat.CODE_128, 0, 32));
        return new Simulacao(pedido.getId(), pedido.getFormaPagamento(), pedido.getCodigoSimulado(),
                qr, barras, pedido.getValorTotal(), pedido.getStatus(), true, LocalDate.now().plusDays(3).toString());
    }

    private String svg(BitMatrix matrix) {
        StringBuilder path = new StringBuilder();
        for (int y=0; y<matrix.getHeight(); y++) {
            int x=0;
            while (x<matrix.getWidth()) {
                if (!matrix.get(x,y)) { x++; continue; }
                int inicio=x;
                while(x<matrix.getWidth() && matrix.get(x,y)) x++;
                int largura=x-inicio;
                path.append("M").append(inicio).append(",").append(y).append("h").append(largura)
                        .append("v1h-").append(largura).append("z");
            }
        }
        return "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 " + matrix.getWidth() + " "
                + matrix.getHeight() + "\" shape-rendering=\"crispEdges\"><rect width=\"100%\" height=\"100%\" fill=\"white\"/>"
                + "<path fill=\"black\" d=\"" + path + "\"/></svg>";
    }

    public record Simulacao(Long pedidoId, String formaPagamento, String codigo, String qrSvg,
                            String barrasSvg, double valorTotal, String status, boolean simulado, String vencimento) {}
}
