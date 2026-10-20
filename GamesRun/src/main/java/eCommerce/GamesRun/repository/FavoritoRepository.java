package eCommerce.GamesRun.repository;

import eCommerce.GamesRun.domain.Favorito;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FavoritoRepository extends JpaRepository<Favorito, Favorito.Chave> {
    List<Favorito> findByUsuarioIdOrderByAnuncioIdAsc(Long usuarioId);
    void deleteByUsuarioIdAndAnuncioId(Long usuarioId, Long anuncioId);
    void deleteByAnuncioId(Long anuncioId);
}
