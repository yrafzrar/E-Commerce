package eCommerce.GamesRun.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;

@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(unique = true, nullable = false)
    private String nome;
    @Column(unique = true, nullable = false)
    private String nick;
    @Column(unique = true, nullable = false)
    private String cpf;
    @Column(nullable = false)
    private String senha;
    @Column(nullable = false)
    private int idade;


    public Long getId() {
        return id;
    }
    public String getNome() {
        return nome;
    }
    public String getNick() {
        return nick;
    }
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public String getSenha() {
        return senha;
    }
    public int getIdade() {
        return idade;
    }
    public String getCpf() {
        return cpf;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
}

/*Ó Deus, nosso Pai,
Obrigado por nos teres dado Carlo,
modelo de vida para os jovens, e mensagem
de amor para todos*/