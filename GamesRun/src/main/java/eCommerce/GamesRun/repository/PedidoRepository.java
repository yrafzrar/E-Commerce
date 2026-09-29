package eCommerce.GamesRun.repository;

import eCommerce.GamesRun.domain.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
	List<Pedido> findByCompradorIdOrderByIdDesc(Long compradorId);
}
