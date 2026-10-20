package eCommerce.GamesRun.config;

import eCommerce.GamesRun.domain.Anuncio;
import eCommerce.GamesRun.domain.Categoria;
import eCommerce.GamesRun.domain.Usuario;
import eCommerce.GamesRun.repository.AnuncioRepository;
import eCommerce.GamesRun.repository.CategoriaRepository;
import eCommerce.GamesRun.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

/** Grava dados fictícios no banco somente ao iniciar com o perfil mock. */
@Component
@Profile("mock")
public class DadosMockados implements CommandLineRunner {
    public static final String LOGIN = "demo@gamesrun.local";
    public static final String SENHA = "GamesRun123!";
    private final UsuarioRepository usuarios;
    private final CategoriaRepository categorias;
    private final AnuncioRepository anuncios;

    public DadosMockados(UsuarioRepository usuarios, CategoriaRepository categorias, AnuncioRepository anuncios) {
        this.usuarios = usuarios;
        this.categorias = categorias;
        this.anuncios = anuncios;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Usuario vendedor = usuarios.findByNick(LOGIN).orElseGet(this::criarUsuario);
        if (vendedor.isDadosMockadosCarregados()) return;
        cadastrar(vendedor, "Nintendo 3DS XL azul", "Nintendo 3DS", 1190, "Usado - Como Novo", "3ds",
                "Console com carregador, telas preservadas e funcionando.");
        cadastrar(vendedor, "PlayStation 5 Slim 1 TB", "PlayStation 5", 3299.90, "Novo - Lacrado", "ps5",
                "Console com controle, cabos e embalagem original.");
        cadastrar(vendedor, "Game Boy Classic original", "Retro & Colecionáveis", 649.90, "Usado - Bom Estado", "handheld",
                "Console portátil clássico, com marcas de uso e funcionamento testado.");
        cadastrar(vendedor, "Nintendo 64 com controle", "Nintendo 64", 899, "Usado - Bom Estado", "n64",
                "Console acompanhado de controle e cabos.");
        cadastrar(vendedor, "Xbox Series X 1 TB", "Xbox Series", 3490, "Usado - Como Novo", "xbox",
                "Console com controle sem fio e cabo de alimentação.");
        cadastrar(vendedor, "Controle DualSense branco", "PlayStation 5", 389.90, "Novo - Lacrado", "controller",
                "Controle branco com embalagem de demonstração.");
        cadastrar(vendedor, "Cartucho retrô edição especial", "Retro & Colecionáveis", 249.90, "Usado - Bom Estado", "cartridge",
                "Cartucho ilustrativo para testar o catálogo e o carrinho.");
        cadastrar(vendedor, "Console portátil com controles", "Retro & Colecionáveis", 1799, "Usado - Como Novo", "switch",
                "Console portátil com dois controles e carregador.");
        vendedor.setDadosMockadosCarregados(true);
        usuarios.save(vendedor);
    }

    private Usuario criarUsuario() {
        var existentes = usuarios.findAll();
        var cpfs = new HashSet<String>();
        var nomes = new HashSet<String>();
        existentes.forEach(usuario -> { cpfs.add(usuario.getCpf()); nomes.add(usuario.getNome()); });
        String nome = "Jogador Demo GamesRun";
        while (nomes.contains(nome)) nome += " Demo";
        long base = 900000001L;
        while (cpfs.contains(cpfDeTeste(base))) base++;
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setNick(LOGIN);
        usuario.setCpf(cpfDeTeste(base));
        usuario.setIdade(18);
        usuario.setSenha(SENHA);
        return usuarios.save(usuario);
    }

    private String cpfDeTeste(long base) {
        String digits = Long.toString(base);
        for (int tamanho = 9; tamanho <= 10; tamanho++) {
            int soma = 0;
            for (int i = 0; i < tamanho; i++) soma += (digits.charAt(i) - '0') * (tamanho + 1 - i);
            int digito = (soma * 10) % 11;
            digits += digito == 10 ? 0 : digito;
        }
        return digits;
    }

    private void cadastrar(Usuario vendedor, String titulo, String universo, double preco,
                           String estado, String imagem, String descricao) {
        boolean existe = anuncios.findAll().stream().anyMatch(anuncio ->
                vendedor.getId().equals(anuncio.getVendedorId()) && titulo.equals(anuncio.getProduto()));
        if (existe) return;
        Categoria categoria = categorias.findAll().stream()
                .filter(item -> universo.equalsIgnoreCase(item.getNome())).findFirst()
                .orElseGet(() -> {
                    Categoria nova = new Categoria();
                    nova.setNome(universo);
                    nova.setDescricao("Jogos, consoles e acessórios — " + universo);
                    return categorias.save(nova);
                });
        Anuncio anuncio = new Anuncio();
        anuncio.setProduto(titulo);
        anuncio.setDescricao("Dados fictícios de demonstração. " + descricao);
        anuncio.setCategoriaId(categoria.getId());
        anuncio.setVendedorId(vendedor.getId());
        anuncio.setEstadoConservacao(estado);
        anuncio.setImagemUrl("assets/" + imagem + ".svg");
        anuncio.setPreco(preco);
        anuncios.save(anuncio);
    }
}
