package eCommerce.GamesRun.service;

import eCommerce.GamesRun.domain.AvaliacaoVendedor;
import eCommerce.GamesRun.repository.AvaliacaoVendedorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Avaliacao nao encontrada"));
    }

    public AvaliacaoVendedor salvar(AvaliacaoVendedor avaliacao) {
        return avaliacaoRepository.save(avaliacao);
    }

    public AvaliacaoVendedor atualizar(Long id, AvaliacaoVendedor avaliacao) {
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
}
