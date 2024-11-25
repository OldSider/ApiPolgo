package br.com.fortsupermercados.TI.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "MRL_CLIENTE")
public class MrlCliente {

    @Id
    private int seqPessoa;

    private int NROEMPRESA;

    private String usucadastro;

    private String statuscliente;

    private String tipocobranca;

    private String usuativou;

    private String indreplicacao;

    private String indsusppiscofins;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dtabaseexportacao;

    private String INDENVIOMALADIRETA;

    private String INDENVIOFAX;

    private String INDENVIOEMAIL;

    private String INDCONTATOFONE;

    @Temporal(TemporalType.TIMESTAMP)
    private Date DTACADASTRO;

    private int PZOPAGTOMAXIMO;

    @Temporal(TemporalType.TIMESTAMP)
    private Date DTAATIVOU;

    @Temporal(TemporalType.TIMESTAMP)
    private Date DTAALTERACAO;

    private String USUALTERACAO;

    @Temporal(TemporalType.TIMESTAMP)
    private Date DTABASEEXPORTACAO;

    private String INDEMITESTREFULTENTRADA;

    private String INDGERAITEMLIQDESCCLIE;

    @Temporal(TemporalType.TIMESTAMP)
    private Date DATAHORAALTERACAO;

}


