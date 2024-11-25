package br.com.fortsupermercados.TI.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name = "ge_pessoa")
public class GePessoa {

    @Id
    private int seqPessoa;

    private int versao;

    private String status;

    private String nomeRazao;

    private String fantasia;

    private String fisicaJuridica;

    private String sexo;

    private int seqCidade;

    private String cidade;

    private String uf;

    private String pais;

    private int seqBairro;

    private String bairro;

    private int seqLogradouro;

    private String logradouro;

    private String nroLogradouro;

    private String cep;

    private int nroCgccpf;

    private int digCgccpf;

    private String inscricaoRg;

    private String estadoCivil;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dtaInclusao;

    private String usuinclusao;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dtaalteracao;

    private String indContribIcms;

    private String indReplicacao;

    private String indProdRural;

    private String indContribIpi;

    private String indMicroEmpresa;

    @Temporal(TemporalType.DATE)
    private Date dtaBaseExportacao;

    private String indSuspPisCofins;

    private String indProfPrescritor;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dtaAlteracaoRoadshow;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dtaHoraInclusao;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dataHoraAlteracao;

    private String inscrcaceal;

    private String indEquipUFOlimpiada;

    private String indCpfProdutor;

    private String orgaoPublico;

}
