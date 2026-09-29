package eCommerce.GamesRun.repository;

import eCommerce.GamesRun.domain.Anuncio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnuncioRepository extends JpaRepository<Anuncio, Long> {

    List<Anuncio> findByProdutoContainingIgnoreCase(String produto);
}