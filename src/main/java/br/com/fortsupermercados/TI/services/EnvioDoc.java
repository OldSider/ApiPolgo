package br.com.fortsupermercados.TI.services;

import br.com.fortsupermercados.TI.components.CPFGenerator;
import br.com.fortsupermercados.TI.entities.EnvioDocLog;
import br.com.fortsupermercados.TI.repositories.EnvioDocLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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

    public void sendDocuments() {
        // Exibir mensagem no início
        System.out.println("O envio está sendo executado");

        // Certifica-se de que o token está disponível
        if (authService.getToken() == null) {
            authService.authenticate();
        }

        String token = authService.getToken();

        String sql = "SELECT " +
                "    SUM(vlrtotal) AS VLRTOTAL, " +
                "    a.seqdocto, " +
                "    MAX(dtahoremissao) AS DTAHOREMISSAO, " +
                "    B.cnpjcpf AS CPFCLIENTE, " +
                "    A.NROCHECKOUT AS PDV, " +
                "    A.nroempresa AS EMPRESA, " +
                "    (c.nrocgccpf || c.DIGCGCCPF) AS CNPJ, " +
                "    (a.seqdocto || a.nrocheckout || c.nrocgccpf || b.cnpjcpf) AS DOC " +
                "FROM " +
                "    consincomonitor.tb_doctoitem a " +
                "JOIN " +
                "    consincomonitor.tb_doctocupom b ON b.seqdocto = a.seqdocto " +
                "    AND a.nrocheckout = b.nrocheckout " +
                "    AND B.NROEMPRESA = A.NROEMPRESA " +
                "JOIN " +
                "    ge_pessoa c ON c.seqpessoa = a.nroempresa " +
                "WHERE " +
                "    TRUNC(a.dtahoremissao) = TO_DATE('06/11/2024', 'DD/MM/yyyy') " +
                "    AND a.nroempresa IN (1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,23,24,25,26,27,28,29,30,31,32,35,36,37,40,42,44) " +
                "GROUP BY " +
                "    a.seqdocto, " +
                "    b.cnpjcpf, " +
                "    A.NROCHECKOUT, " +
                "    A.nroempresa, " +
                "    (c.nrocgccpf || c.DIGCGCCPF), " +
                "    (a.seqdocto || a.nrocheckout || c.nrocgccpf || b.cnpjcpf) " +
                "ORDER BY " +
                "    DTAHOREMISSAO";

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

        RestTemplate restTemplate = new RestTemplate();

        ObjectMapper objectMapper = new ObjectMapper();

        for (Map<String, Object> row : rows) {
            try {
                // Extrair os dados
                BigDecimal vlrTotal = (BigDecimal) row.get("VLRTOTAL");
                Long seqDocto = ((Number) row.get("SEQDOCTO")).longValue();
                Timestamp dtahorEmissao = (Timestamp) row.get("DTAHOREMISSAO");
                String cpfCliente = (String) row.get("CPFCLIENTE");
                Integer pdv = ((Number) row.get("PDV")).intValue();
                Integer empresa = ((Number) row.get("EMPRESA")).intValue();
                String cnpj = (String) row.get("CNPJ");
                String doc = (String) row.get("DOC");
                String campanhaPolgo = "FORTNATAL24";
                int ano = 2024;

                // Se o CPF do cliente estiver vazio
                if (cpfCliente == null || cpfCliente.trim().isEmpty()) {
                    cpfCliente = CPFGenerator.generateCPF();
                }

                // Formatar a data
                String dataHoraEmissao = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dtahorEmissao);

                // Construir o corpo da requisição
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("usuario", cpfCliente);
                requestBody.put("numeroDocumento", doc);
                requestBody.put("dataHoraEmissao", dataHoraEmissao);
                requestBody.put("valorTotal", vlrTotal);
                requestBody.put("cnpjEmitente", cnpj);

                 // Criar o objeto 'campanha' com os valores fornecidos
                Map<String, Object> campanha = new HashMap<>();
                campanha.put("identificacao", campanhaPolgo); // Valor fornecido para 'identificacao'
                campanha.put("ano", ano); // Valor fornecido para 'ano'

                // Adicionar o objeto 'campanha' ao 'requestBody'
                requestBody.put("campanha", campanha);


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

                // Registrar o log
                EnvioDocLog log = new EnvioDocLog();
                log.setRequestJson(requestJson);
                log.setResponseJson(response.getBody());
                log.setStatus(response.getStatusCodeValue());
                log.setDateTime(new Date());

                envioDocLogRepository.save(log);

                System.out.println("Documento enviado com sucesso: " + doc);

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
