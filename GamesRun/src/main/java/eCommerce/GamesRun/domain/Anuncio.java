package eCommerce.GamesRun.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Anuncio {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String produto, descricao, estadoConservacao;
    @Column(name = "categoria_id", nullable = false)
    private Long categoriaId;
    @Column(name = "vendedor_id", nullable = false)
    private Long vendedorId;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id", insertable = false, updatable = false, nullable = false)
    private Categoria categoria;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vendedor_id", insertable = false, updatable = false, nullable = false)
    private Usuario vendedor;
    @Column(length = 2000)
    private String imagemUrl;
    private double preco;


    public Long getId() {
        return id;
    }
    public String getProduto() {
        return produto;
    }
    public String getDescricao() {
        return descricao;
    }
    public double getPreco() {
        return preco;
    }
    public String getEstadoConservacao() { return estadoConservacao; }
    public String getImagemUrl() { return imagemUrl; }
    public Long getCategoriaId() { return categoriaId; }
    public Long getVendedorId() { return vendedorId; }

    public void setProduto(String produto) {
        this.produto = produto;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public void setEstadoConservacao(String estadoConservacao) { this.estadoConservacao = estadoConservacao; }
    public void setImagemUrl(String imagemUrl) { this.imagemUrl = imagemUrl; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }
    public void setVendedorId(Long vendedorId) { this.vendedorId = vendedorId; }
}