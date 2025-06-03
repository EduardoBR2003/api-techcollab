package br.com.api_techcollab.model;

import br.com.api_techcollab.model.enums.TiposUsuarios;
import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "empresa")
@PrimaryKeyJoinColumn(name = "usuario_id")
public class Empresa extends Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(nullable = false, unique = true, length = 18)
    private String cnpj;

    @Column(name = "razao_social", nullable = false, length = 150)
    private String razaoSocial;

    @Column(name = "desc_empresa", columnDefinition = "TEXT")
    private String descEmpresa;

    @Column(name = "site_url", length = 255)
    private String siteUrl;

    public Empresa() {
        super();
        this.setTipoUsuario(TiposUsuarios.EMPRESA);
    }

    // Getters e Setters
    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }
    public String getRazaoSocial() { return razaoSocial; }
    public void setRazaoSocial(String razaoSocial) { this.razaoSocial = razaoSocial; }
    public String getDescEmpresa() { return descEmpresa; }
    public void setDescEmpresa(String descEmpresa) { this.descEmpresa = descEmpresa; }
    public String getSiteUrl() { return siteUrl; }
    public void setSiteUrl(String siteUrl) { this.siteUrl = siteUrl; }
}