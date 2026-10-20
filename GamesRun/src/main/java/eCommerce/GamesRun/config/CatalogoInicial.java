package eCommerce.GamesRun.config;

import eCommerce.GamesRun.domain.Categoria;
import eCommerce.GamesRun.repository.CategoriaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Disponibiliza categorias no primeiro uso, preservando catálogos já cadastrados. */
@Component
@ConditionalOnProperty(name = "gamesrun.catalogo-inicial", havingValue = "true", matchIfMissing = true)
public class CatalogoInicial implements CommandLineRunner {
    private final CategoriaRepository categorias;

    public CatalogoInicial(CategoriaRepository categorias) {
        this.categorias = categorias;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (categorias.count() != 0) return;
        for (String nome : new String[]{"Nintendo 3DS", "Nintendo 64", "PlayStation 5", "Xbox Series", "Retro & Colecionáveis"}) {
            Categoria categoria = new Categoria();
            categoria.setNome(nome);
            categoria.setDescricao("Jogos, consoles e acessórios — " + nome);
            categorias.save(categoria);
        }
    }
}
