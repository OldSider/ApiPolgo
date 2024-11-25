package br.com.fortsupermercados.TI.services;

import br.com.fortsupermercados.TI.components.CPFGenerator;
import br.com.fortsupermercados.TI.entities.EnvioDocLog;
import br.com.fortsupermercados.TI.repositories.EnvioDocLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class EnvioDoc {

    @Autowired
    private AuthService authService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${url.base}")
    private String baseUrl;

    @Value("${envio.endpoint}")
    private String envioEndpoint;

    @Autowired
    private EnvioDocLogRepository envioDocLogRepository;

    @Scheduled(fixedRate = 900000)
    public void sendDocuments() {
        LocalDateTime inicio = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        // Exibir mensagem no início
        System.out.println("O envio está sendo executado as: " + inicio.format(formatter));

        // Certifica-se de que o token está disponível
        if (authService.getToken() == null) {
            authService.authenticate();
        }

        String token = authService.getToken();

        String dataHora = null;
        String dataHoraEmissaoFormatada = null;

        // Consulta para pegar a última DATAHORAEMISSAO
        String slqData = "SELECT JSON_VALUE(REQUEST_JSON, '$.dataHoraEmissao') AS dataHora " +
                "FROM testes_integracao_campanha_natal " +
                "WHERE STATUS != 500 " +
                "ORDER BY dataHora DESC FETCH FIRST 1 ROW ONLY";

        try {
        // Executa a consulta e obtém o valor diretamente
            dataHora = jdbcTemplate.queryForObject(slqData, String.class);

        // Converte a String para Timestamp
            Timestamp timestamp = Timestamp.valueOf(dataHora);

        // Converte o Timestamp para Date (java.util.Date)
            Date date = new Date(timestamp.getTime());

        // Formata a data para o formato desejado
            dataHoraEmissaoFormatada = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(date);

        // Exibe a data formatada
            System.out.println("Data formatada: " + dataHoraEmissaoFormatada);
        } catch (Exception e) {
            System.out.println("Erro ao obter DATAHORAEMISSAO: " + e.getMessage());
        }

        String sql = "SELECT " +
                "    SUM(a.vlrtotal) AS VLRTOTAL, " +
                "    a.seqdocto, " +
                "    MAX(a.dtahoremissao) AS DTAHOREMISSAO, " +
                "    b.cnpjcpf AS CPFCLIENTE, " +
                "    a.nrocheckout AS PDV, " +
                "    a.nroempresa AS EMPRESA, " +
                "    (c.nrocgccpf || c.digcgccpf) AS CNPJ,  " +
                "    (a.seqdocto || a.nrocheckout || c.nrocgccpf || b.cnpjcpf) AS DOC, " +
                "    c.nomerazao AS EMPRESARAZAO, " +
                "    e.nomerazao AS NOMERAZAO, " +
                "    d.sequsuario AS SEQUSUARIO, " +
                "    COUNT(a.QUANTIDADE) AS QUANTIDADETOTAL " +
                "FROM " +
                "    consincomonitor.tb_doctoitem a " +
                "JOIN " +
                "    consincomonitor.tb_doctocupom b ON b.seqdocto = a.seqdocto " +
                "    AND a.nrocheckout = b.nrocheckout  " +
                "    AND b.nroempresa = a.nroempresa " +
                "JOIN  " +
                "    ge_pessoa c ON c.seqpessoa = a.nroempresa " +
                "JOIN  " +
                "    consincomonitor.tb_docto d ON a.nroempresa = d.nroempresa  " +
                "    AND a.nrocheckout = d.nrocheckout  " +
                "    AND a.seqdocto = d.seqdocto " +
                "JOIN  " +
                "    ge_pessoa e ON e.seqpessoa = d.sequsuario " +
                "WHERE " +
                "    a.dtahoremissao > TO_DATE(?, 'DD/MM/YYYY HH24:MI:SS') " +
                "    AND a.nroempresa IN (1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,23,24,25,26,27,28,29,30,31,32,35,36,37,40,42,44) " +
                "GROUP BY " +
                "    a.seqdocto, " +
                "    b.cnpjcpf, " +
                "    a.nrocheckout, " +
                "    a.nroempresa, " +
                "    (c.nrocgccpf || c.digcgccpf), " +
                "    (a.seqdocto || a.nrocheckout || c.nrocgccpf || b.cnpjcpf), " +
                "    c.nomerazao, " +
                "    e.nomerazao, " +
                "    d.sequsuario " +
                "ORDER BY " +
                "    DTAHOREMISSAO";

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, dataHoraEmissaoFormatada);

        RestTemplate restTemplate = new RestTemplate();

        ObjectMapper objectMapper = new ObjectMapper();

        for (Map<String, Object> row : rows) {
            try {
        // Extrair os dados
                BigDecimal vlrTotal = (BigDecimal) row.get("VLRTOTAL");
                int seqDocto = ((Number) row.get("SEQDOCTO")).intValue();
                Timestamp dtahorEmissao = (Timestamp) row.get("DTAHOREMISSAO");
                String cpfCliente = (String) row.get("CPFCLIENTE");
                Integer pdv = ((Number) row.get("PDV")).intValue();
                Integer empresa = ((Number) row.get("EMPRESA")).intValue();
                String cnpj = (String) row.get("CNPJ");
                String doc = (String) row.get("DOC");
                String campanhaPolgo = "FORTNATAL24";
                int ano = 2024;
                String nomerazao = (String) row.get("NOMERAZAO");
                String nomeempresa = (String) row.get("EMPRESARAZAO");
                int quantidadetotal = ((Number) row.get("QUANTIDADETOTAL")).intValue();

                // Se o CPF do cliente estiver vazio, gerar um CPF
                if (cpfCliente == null || cpfCliente.trim().isEmpty()) {
                    cpfCliente = CPFGenerator.generateCPF();
                }

                if (row.get("EMPRESA").toString().equals("3")) {
                    cnpj = "22826076000103";
                } else if (row.get("EMPRESA").toString().equals("15")) {
                    cnpj = "28950902000108";
                } else if (row.get("EMPRESA").toString().equals("25")) {
                    cnpj = "19629293000107";
                } else if (row.get("EMPRESA").toString().equals("35")) {
                    cnpj = "31009409000100";
                } else if (row.get("EMPRESA").toString().equals("9")) {
                    cnpj = "28977571000108";
                } else if (row.get("EMPRESA").toString().equals("10")) {
                    cnpj = "23306904000145";
                } else if (row.get("EMPRESA").toString().equals("1")) {
                    cnpj = "46997642000108";
                } else if (row.get("EMPRESA").toString().equals("40")) {
                    cnpj = "50225211000109";
                }

                // Formatar a data
                String dataHoraEmissao = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dtahorEmissao);

                String quantidadeTotalString = String.valueOf(quantidadetotal);
                // Construir o corpo da requisição
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("usuario", cpfCliente);
                requestBody.put("extra", quantidadeTotalString);
                requestBody.put("numeroDocumento", doc + dataHoraEmissaoFormatada);
                requestBody.put("dataHoraEmissao", dataHoraEmissao);
                requestBody.put("valorTotal", vlrTotal);
                requestBody.put("cnpjEmitente", cnpj);

                // Criar o objeto 'campanha' com os valores fornecidos
                Map<String, Object> campanha = new HashMap<>();
                campanha.put("identificacao", campanhaPolgo);
                campanha.put("ano", ano);

                // Adicionar o objeto 'campanha' ao 'requestBody'
                requestBody.put("campanha", campanha);

                // Criar o objeto 'vendedor' com os valores fornecidos
                String seqDoctorString = String.valueOf(seqDocto);
                Map<String, Object> vendedor = new HashMap<>();
                vendedor.put("codigo", seqDoctorString);
                vendedor.put("nome", nomerazao);

                requestBody.put("vendedor", vendedor);

                // Converter o corpo em JSON
                String requestJson = objectMapper.writeValueAsString(requestBody);

                // Exibir o JSON sendo enviado
                System.out.println("Enviando JSON: " + requestJson);

                // Configurar os headers
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("Authorization", token);

                // Criar a entidade da requisição
                HttpEntity<String> requestEntity = new HttpEntity<>(requestJson, headers);

                // URL completa
                String url = baseUrl + envioEndpoint;

                // Enviar a requisição
                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

                // Obtém o horário de término
                LocalDateTime fim = LocalDateTime.now();
                //Converter as variaves LocalDateTime para Date
                Date dateFim = Date.from(fim.atZone(ZoneId.systemDefault()).toInstant());
                Date dateInicio = Date.from(inicio.atZone(ZoneId.systemDefault()).toInstant());

                // Registrar o log
                EnvioDocLog log = new EnvioDocLog();
                log.setRequestJson(requestJson);
                log.setResponseJson(response.getBody());
                log.setStatus(response.getStatusCodeValue());
                log.setDateTime(new Date());
                log.setHorarioInicio(dateInicio);
                log.setHorarioFim(dateFim);

                envioDocLogRepository.save(log);

                System.out.println("Documento enviado com sucesso: " + doc);
                System.out.println("Horario final: " + fim.format(formatter));

                // Calcula a duração entre inicio e fim
                Duration duracao = Duration.between(inicio, fim);
                // Adiciona a duração ao momento inicial para ter o "soma" dos tempos
                LocalDateTime somaDosTempos = inicio.plus(duracao);

            } catch (Exception e) {
                // Em caso de erro, registrar o log
                EnvioDocLog log = new EnvioDocLog();
                log.setRequestJson(row.toString());
                log.setResponseJson(e.getMessage());
                log.setStatus(500);
                log.setDateTime(new Date());


                envioDocLogRepository.save(log);

                System.out.println("Erro ao enviar documento: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}