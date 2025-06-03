package br.com.api_techcollab.model;

import br.com.api_techcollab.model.enums.TiposUsuarios;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "usuario")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_usuario", nullable = false)
    private TiposUsuarios tipoUsuario;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_cadastro", nullable = false)
    private Date dataCadastro;

    public Usuario() {
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public TiposUsuarios getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(TiposUsuarios tipoUsuario) { this.tipoUsuario = tipoUsuario; }
    public Date getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(Date dataCadastro) { this.dataCadastro = dataCadastro; }

    // Equals e HashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(id, usuario.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}