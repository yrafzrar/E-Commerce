package eCommerce.GamesRun.repository;

import eCommerce.GamesRun.domain.Carrinho;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CarrinhoRepository extends JpaRepository<Carrinho, Long> {
	List<Carrinho> findByUsuarioId(Long usuarioId);
	Optional<Carrinho> findByUsuarioIdAndAnuncioId(Long usuarioId, Long anuncioId);
}
