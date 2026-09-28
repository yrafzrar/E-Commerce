package eCommerce.GamesRun.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import eCommerce.GamesRun.domain.AvaliacaoVendedor;
import eCommerce.GamesRun.exception.ApiException;
import eCommerce.GamesRun.repository.AvaliacaoVendedorRepository;

@Service
public class AvaliacaoVendedorService {

    private final AvaliacaoVendedorRepository avaliacaoRepository;

    public AvaliacaoVendedorService(AvaliacaoVendedorRepository avaliacaoRepository) {
        this.avaliacaoRepository = avaliacaoRepository;
    }

    public List<AvaliacaoVendedor> listarTodos() {
        return avaliacaoRepository.findAll();
    }

    public AvaliacaoVendedor buscarPorId(Long id) {
        return avaliacaoRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Avaliacao nao encontrada"));
    }

    public AvaliacaoVendedor salvar(AvaliacaoVendedor avaliacao) {
        validar(avaliacao);
        return avaliacaoRepository.save(avaliacao);
    }

    public AvaliacaoVendedor atualizar(Long id, AvaliacaoVendedor avaliacao) {
        validar(avaliacao);
        AvaliacaoVendedor existente = buscarPorId(id);
        existente.setAvaliadorId(avaliacao.getAvaliadorId());
        existente.setVendedorId(avaliacao.getVendedorId());
        existente.setNota(avaliacao.getNota());
        existente.setComentario(avaliacao.getComentario());
        return avaliacaoRepository.save(existente);
    }

    public void excluir(Long id) {
        avaliacaoRepository.delete(buscarPorId(id));
    }

    private void validar(AvaliacaoVendedor avaliacao) {
        if (avaliacao == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Avaliacao nao pode ser nula");
        }
        if (avaliacao.getAvaliadorId() == null || avaliacao.getAvaliadorId() <= 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "ID do avaliador deve ser maior que zero");
        }
        if (avaliacao.getVendedorId() == null || avaliacao.getVendedorId() <= 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "ID do vendedor deve ser maior que zero");
        }
        if (avaliacao.getNota() < 1 || avaliacao.getNota() > 5) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Nota deve estar entre 1 e 5");
        }
    }
}
