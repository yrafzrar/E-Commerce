package eCommerce.GamesRun.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import eCommerce.GamesRun.domain.Produto;
import eCommerce.GamesRun.exception.ApiException;
import eCommerce.GamesRun.repository.ProdutoRepository;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    public Produto buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Produto nao encontrado"));
    }

    public Produto salvar(Produto produto) {
        validar(produto);
        return produtoRepository.save(produto);
    }

    public Produto atualizar(Long id, Produto produto) {
        validar(produto);
        Produto existente = buscarPorId(id);
        existente.setNome(produto.getNome());
        existente.setDescricao(produto.getDescricao());
        existente.setPreco(produto.getPreco());
        existente.setEstoque(produto.getEstoque());
        return produtoRepository.save(existente);
    }

    public void excluir(Long id) {
        produtoRepository.delete(buscarPorId(id));
    }

    private void validar(Produto produto) {
        if (produto == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Produto nao pode ser nulo");
        }
        if (produto.getNome() == null || produto.getNome().isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Nome do produto e obrigatorio");
        }
        if (produto.getPreco() <= 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Preco deve ser maior que zero");
        }
        if (produto.getEstoque() < 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Estoque nao pode ser negativo");
        }
    }
}
