package eCommerce.GamesRun.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String nome, nick, senha;
    private int idade, cpf;


    public Long getId() {
        return id;
    }
    public String getNome() {
        return nome;
    }
    public String getNick() {
        return nick;
    }
    public String getSenha() {
        return senha;
    }
    public int getIdade() {
        return idade;
    }
    public int getCpf() {
        return cpf;
    }
}