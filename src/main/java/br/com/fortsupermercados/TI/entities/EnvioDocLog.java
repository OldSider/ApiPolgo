package br.com.fortsupermercados.TI.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import java.util.Date;

@Entity
@Table(name = "testes_integracao_campanha_natal")
public class EnvioDocLog {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "uuid2")
    @Column(name = "uuid", updatable = false, nullable = false)
    private String uuid;

    @Column(columnDefinition = "CLOB")
    private String requestJson;

    @Column(columnDefinition = "CLOB")
    private String responseJson;

    private int status;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateTime;

    @Column
    private Date horarioInicio;

    @Column
    private Date horarioFim;

    // Construtores

    public EnvioDocLog() {
        // Construtor padrão
    }

    // Getters e Setters

    public String getUuid() {
        return uuid;
    }

    public String getRequestJson() {
        return requestJson;
    }

    public void setRequestJson(String requestJson) {
        this.requestJson = requestJson;
    }

    public String getResponseJson() {
        return responseJson;
    }

    public void setResponseJson(String responseJson) {
        this.responseJson = responseJson;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public Date getHorarioInicio() {
        return horarioInicio;
    }

    public void setHorarioInicio(Date horarioInicio) {
        this.horarioInicio = horarioInicio;
    }

    public Date getHorarioFim() {
        return horarioFim;
    }

    public void setHorarioFim(Date horarioFim) {
        this.horarioFim = horarioFim;
    }
}
