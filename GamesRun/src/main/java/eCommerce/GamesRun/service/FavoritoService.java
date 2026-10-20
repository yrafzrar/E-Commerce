package eCommerce.GamesRun.service;

import eCommerce.GamesRun.domain.Favorito;
import eCommerce.GamesRun.exception.ApiException;
import eCommerce.GamesRun.repository.AnuncioRepository;
import eCommerce.GamesRun.repository.FavoritoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class FavoritoService {
    private final FavoritoRepository favoritos;
    private final AnuncioRepository anuncios;
    public FavoritoService(FavoritoRepository favoritos, AnuncioRepository anuncios) {
        this.favoritos = favoritos;
        this.anuncios = anuncios;
    }
    @Transactional(readOnly = true)
    public List<Long> listar(Long usuarioId) {
        return favoritos.findByUsuarioIdOrderByAnuncioIdAsc(usuarioId).stream()
                .map(Favorito::getAnuncioId).toList();
    }
    @Transactional
    public void adicionar(Long usuarioId, Long anuncioId) {
        if (!anuncios.existsById(anuncioId))
            throw new ApiException(HttpStatus.NOT_FOUND, "Anúncio não encontrado.");
        favoritos.save(new Favorito(usuarioId, anuncioId));
    }
    @Transactional
    public void remover(Long usuarioId, Long anuncioId) {
        favoritos.deleteByUsuarioIdAndAnuncioId(usuarioId, anuncioId);
    }
}
