package eCommerce.GamesRun.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import java.io.Serializable;
import java.util.Objects;

@Entity
@IdClass(Favorito.Chave.class)
public class Favorito {
    @Id private Long usuarioId;
    @Id private Long anuncioId;

    protected Favorito() {}
    public Favorito(Long usuarioId, Long anuncioId) {
        this.usuarioId = usuarioId;
        this.anuncioId = anuncioId;
    }
    public Long getUsuarioId() { return usuarioId; }
    public Long getAnuncioId() { return anuncioId; }

    // A chave composta impede duplicar um anúncio nos favoritos da mesma conta.
    public static class Chave implements Serializable {
        private Long usuarioId;
        private Long anuncioId;
        public Chave() {}
        public Chave(Long usuarioId, Long anuncioId) {
            this.usuarioId = usuarioId;
            this.anuncioId = anuncioId;
        }
        @Override public boolean equals(Object obj) {
            return obj instanceof Chave outra && Objects.equals(usuarioId, outra.usuarioId)
                    && Objects.equals(anuncioId, outra.anuncioId);
        }
        @Override public int hashCode() { return Objects.hash(usuarioId, anuncioId); }
    }
}
