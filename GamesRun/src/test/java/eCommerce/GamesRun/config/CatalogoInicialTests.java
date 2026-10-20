package eCommerce.GamesRun.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import eCommerce.GamesRun.domain.Categoria;
import eCommerce.GamesRun.repository.CategoriaRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class CatalogoInicialTests {
    @Test
    void criaCategoriasSomenteNoPrimeiroUso() {
        var repository = mock(CategoriaRepository.class);
        when(repository.count()).thenReturn(0L);
        new CatalogoInicial(repository).run();
        var categories = ArgumentCaptor.forClass(Categoria.class);
        verify(repository, times(5)).save(categories.capture());
        assertEquals(5, categories.getAllValues().stream().map(Categoria::getNome).distinct().count());
    }

    @Test
    void preservaCatalogoExistente() {
        var repository = mock(CategoriaRepository.class);
        when(repository.count()).thenReturn(1L);
        new CatalogoInicial(repository).run();
        verify(repository, never()).save(any());
    }
}
