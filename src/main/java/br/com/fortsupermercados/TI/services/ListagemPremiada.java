package br.com.fortsupermercados.TI.services;

import br.com.fortsupermercados.TI.entities.*;
import br.com.fortsupermercados.TI.repositories.*;
import br.com.fortsupermercados.TI.services.SequenciaService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@SpringBootApplication
public class ListagemPremiada {

    @Autowired
    private AuthService authService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${url.base}")
    private String baseUrl;

    @Value("${listagem.endpoint}")
    private String envioEndpoint;

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.username}")
    private String jdbcUser;

    @Value("${spring.datasource.password}")
    private String jdbcPassword;

    @Autowired
    private EnvioGePessoaLog EnvioGePessoaLog;

    @Autowired
    private EnvioMrlClienteLog envioMrlClienteLog;

    @Autowired
    private EnvioMrlClienteCreditoLog envioMrlClienteCreditoLog;

    @Autowired
    private EnvioMrlClienteCartaoLog envioMrlClienteCartaoLog;

    @Autowired
    private EnvioMrlClienteSegLog envioMrlClienteSegLog;

    @Autowired
    private EnvioClienteLogRepository envioClienteLogRepository;

    @Autowired
    private EnvioVariaveisLog envioVariaveisLog;

    @Scheduled(fixedRate = 3600000)
    public void ClientesPremiados() {
        LocalDateTime inicioExecucao = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        System.out.println("Execução iniciada em: " + inicioExecucao.format(formatter));

        // Certifica-se de que o token está disponível
        if (authService.getToken() == null) {
            authService.authenticate();
        }

        String token = authService.getToken();
        String url = baseUrl + envioEndpoint;

        RestTemplate restTemplate = new RestTemplate();

        // Configuração de headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);

        // Configurar a entidade da requisição
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        LocalDateTime fimExecucao;
        Date inicioData = Date.from(inicioExecucao.atZone(ZoneId.systemDefault()).toInstant());
        Date fimData = null;

        try {

            // Enviar requisição GET
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);


            // Processar a resposta
            System.out.println("Status da resposta: " + response.getStatusCodeValue());
            System.out.println("Corpo da resposta: " + response.getBody());

            // Interpretar o JSON da resposta
            JSONObject jsonResponse = new JSONObject(response.getBody());
            JSONArray retornoArray = jsonResponse.getJSONObject("retorno").getJSONArray("retorno");

            try (Connection conn = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword)) {
                conn.setAutoCommit(false);

                for (int i = 0; i < retornoArray.length(); i++) {
                    JSONObject item = retornoArray.getJSONObject(i);
                    String idCampanha = item.getString("id");
                    String nome = item.getJSONObject("consumidor").getString("nome");
                    String cpf = item.getJSONObject("raspada").getString("usuario");
                    String cpfCompleto = item.getJSONObject("raspada").getString("usuario");
                    String cnpjCpf9 = cpf.substring(0, 9);
                    String cnpjCpf2 = cpf.substring(9, 11);
                    int valorRecebido = item.getJSONObject("premio").getInt("valor");


                    String sqlValidacaoEntrada = "SELECT nrocartao FROM mrl_clientecartao WHERE nrocartao = ?";
                    try (PreparedStatement conectValidacao = conn.prepareStatement(sqlValidacaoEntrada)) {
                        conectValidacao.setString(1, cpfCompleto);
                        ResultSet validate = conectValidacao.executeQuery();

                        if (validate.next()) {
                            System.out.println("CPF Já cadastrado: " + cpfCompleto);
                            continue;

                        } else {
                            System.out.println("CPF Não Cadastrado, processando...");
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    SequenciaService sequenciaService = new SequenciaService();
                    // Obtém a próxima sequência para cada iteração
                    String sqlMaxSeq = String.valueOf(sequenciaService.getNextSequence("ge_pessoa"));
                    int nextSeq = 0;
                    try (Statement stmt = conn.createStatement();
                         ResultSet rs = stmt.executeQuery(sqlMaxSeq)) {
                        if (rs.next()) {
                            nextSeq = rs.getInt("next_seq");

                            // Atualiza a sequência
                            String updateSql = "UPDATE ge_sequencia SET sequencia = ? WHERE TRIM(LOWER(nometabela)) = 'ge_pessoa'";
                            try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                                pstmt.setInt(1, nextSeq);
                                pstmt.executeUpdate();
                            }

                            conn.commit();
                            System.out.println("Sequência atualizada com sucesso: " + nextSeq);
                        }
                    }

                    // Insere na tabela ge_pessoa
                    try {
                        GePessoa ge = new GePessoa();
                        ge.setSeqPessoa(nextSeq);
                        ge.setVersao(0);
                        ge.setStatus("A");
                        ge.setNomeRazao(nome);
                        ge.setFantasia("FORTNATAL24");
                        ge.setFisicaJuridica("F");
                        ge.setSexo("M");
                        ge.setSeqCidade(45);
                        ge.setCidade("SALVADOR");
                        ge.setUf("BA");
                        ge.setPais("BRASIL");
                        ge.setSeqBairro(1452);
                        ge.setBairro("ACUPE DE BROTAS");
                        ge.setSeqLogradouro(1102942);
                        ge.setLogradouro("CONDOMÍNIO RESIDENCIAL DE BROTAS");
                        ge.setNroLogradouro("111");
                        ge.setCep("40000001");
                        ge.setNroCgccpf(Integer.parseInt(cnpjCpf9));
                        ge.setDigCgccpf(Integer.parseInt(cnpjCpf2));
                        ge.setInscricaoRg("11111111111");
                        ge.setEstadoCivil("S");
                        ge.setDtaInclusao(inicioData);
                        ge.setUsuinclusao("JSAMPAIO");
                        ge.setDtaalteracao(inicioData);
                        ge.setIndContribIcms("N");
                        ge.setIndReplicacao("S");
                        ge.setIndProdRural("N");
                        ge.setIndContribIpi("N");
                        ge.setIndMicroEmpresa("N");
                        ge.setDtaBaseExportacao(inicioData);
                        ge.setIndSuspPisCofins("N");
                        ge.setIndProfPrescritor("N");
                        ge.setDtaAlteracaoRoadshow(inicioData);
                        ge.setDtaHoraInclusao(inicioData);
                        ge.setDataHoraAlteracao(inicioData);
                        ge.setInscrcaceal("N");
                        ge.setIndEquipUFOlimpiada("N");
                        ge.setIndCpfProdutor("N");
                        ge.setOrgaoPublico("N");

                        EnvioGePessoaLog.save(ge);

                    } catch (NumberFormatException e) {
                        throw new RuntimeException(e);
                    }

                    System.out.println("Dados inseridos com sucesso na tabela ge_pessoa.");

                    // Mapeia o valorRecebido para vlrFormaPagamento
                    // Alterar para o numero do novo convenio
                    int vlrFormaPagamento = switch (valorRecebido) {
                        case 50 -> 74;
                        case 100 -> 69;
                        case 200 -> 70;
                        case 300 -> 71;
                        case 400 -> 72;
                        case 500 -> 73;
                        default -> throw new IllegalArgumentException("ValorRecebido inválido: " + valorRecebido);
                    };

                    // Insere na tabela MRL_CLIENTE
                    try  {
                        MrlCliente cliente = new MrlCliente();

                        // Preenchendo os atributos da entidade
                        cliente.setSeqPessoa(nextSeq);
                        cliente.setNROEMPRESA(33);
                        cliente.setUsucadastro("JSAMPAIO");
                        cliente.setStatuscliente("A");
                        cliente.setTipocobranca("N");
                        cliente.setUsuativou("jsampaio");
                        cliente.setIndreplicacao("S");
                        cliente.setIndsusppiscofins("N");
                        cliente.setDtabaseexportacao(new Date());
                        cliente.setINDENVIOMALADIRETA("S");
                        cliente.setINDENVIOFAX("S");
                        cliente.setINDENVIOEMAIL("S");
                        cliente.setINDCONTATOFONE("S");
                        cliente.setDTACADASTRO(new Date());
                        cliente.setPZOPAGTOMAXIMO(0);
                        cliente.setDTAATIVOU(new Date());
                        cliente.setDTAALTERACAO(new Date());
                        cliente.setUSUALTERACAO("JSAMPAIO");
                        cliente.setDTABASEEXPORTACAO(new Date());
                        cliente.setINDEMITESTREFULTENTRADA("N");
                        cliente.setINDGERAITEMLIQDESCCLIE("E");
                        cliente.setDATAHORAALTERACAO(new Date());

                        // Salvando no banco de dados
                        envioMrlClienteLog.save(cliente);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("Dados inseridos com sucesso na tabela MRL_CLIENTE.");


                    try {
                        // Criar nova instância da entidade
                        MrlClienteCredito clienteCredito = new MrlClienteCredito();

                        // Preenchendo os atributos da entidade
                        clienteCredito.setSeqpessoa(nextSeq);
                        clienteCredito.setNroformapagto(vlrFormaPagamento);
                        clienteCredito.setVlrlimitecredito(valorRecebido);
                        clienteCredito.setStatuscredito("L");
                        clienteCredito.setEmpultcadastro(33);
                        clienteCredito.setIndprincipal("S");
                        clienteCredito.setIndreplicacao("S");
                        clienteCredito.setDtaalteracao(new Date());
                        clienteCredito.setUsualteracao("JSAMPAIO");
                        clienteCredito.setVlrimiteparccredito(0);

                        // Salvando no banco de dados
                        envioMrlClienteCreditoLog.save(clienteCredito);
                        System.out.println("Dados Inseridos com sucesso na tabela mrl_clientecredito -> DADOS: " + cpfCompleto + ", " + vlrFormaPagamento + ", " + nextSeq);
                    } catch (Exception e){
                        System.out.println("Erro no insert: " + e);
                    }
                    System.out.println("Dados inseridos com sucesso na tabela mrl_clientecredito.");

                    // Código de inserção na tabela mrl_clientecartao
                    String sqlInsercaoCartao = "INSERT INTO mrl_clientecartao(nrocartao, nroformapagto, seqpessoatitular, seqpessoaportador, INDEMISSAOCARTAO, statuscartao, empultcadastro, indreplicacao, dtageracao, usugeracao, INDCOMPOERENDA) VALUES (?, ?, ?, ?, 'N', 'A', 7, 'S', SYSDATE, 'JSAMPAIO', 'N')";
                    try {
                        // Criar nova instância da entidade
                        MrlClienteCartao clienteCartao = new MrlClienteCartao();

                        // Preenchendo os atributos da entidade
                        clienteCartao.setNrocartao(Integer.parseInt(cpfCompleto));
                        clienteCartao.setNroformapagto(vlrFormaPagamento);
                        clienteCartao.setSeqpessoatitular(nextSeq);
                        clienteCartao.setSeqpessoaportador(nextSeq);
                        clienteCartao.setIndemissaocartao("N");
                        clienteCartao.setStatuscartao("A");
                        clienteCartao.setEmpultcadastro(7);
                        clienteCartao.setIndreplicacao("S");
                        clienteCartao.setDtageracao(new Date());
                        clienteCartao.setUsugeracao("JSAMPAIO");
                        clienteCartao.setIndcompoerenda("N");

                        // Salvando no banco de dados
                        envioMrlClienteCartaoLog.save(clienteCartao);
                    } catch (Exception e) {
                        System.out.println("Erro no insert: " + e);
                        break;
                    }
                    System.out.println("Dados Inseridos com sucesso na tabela mrl_clientecartao, DADOS: " + cpfCompleto + ", " + vlrFormaPagamento + ", " + nextSeq);

                    //Código para inserção na tabela mrl_clienteseg
                    try{
                        // Criar nova instância da entidade
                        MrlClienteSeg clienteSeg = new MrlClienteSeg();

                        // Preenchendo os atributos da entidade
                        clienteSeg.setSeqpessoa(nextSeq);
                        clienteSeg.setNrosegmento(1);
                        clienteSeg.setNrorepresentante(7);
                        clienteSeg.setNrotabvendaprinc("1");
                        clienteSeg.setPeriodvisita("S");
                        clienteSeg.setStatus("A");
                        clienteSeg.setDtaativou(new Date());
                        clienteSeg.setUsuativou("JSAMPAIO");
                        clienteSeg.setDtaalteracao(new Date());
                        clienteSeg.setUsualteracao("JSAMPAIO");
                        clienteSeg.setIndreplicacao("S");

                        // Salvando no banco de dados
                        envioMrlClienteSegLog.save(clienteSeg);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("Dados inseridos com sucesso na tabela mrl:clienteseg.");

                    // Envia o ID da campanha para a URL
                    HttpHeaders headersCampanha = new HttpHeaders();
                    headersCampanha.set("Authorization", "Bearer " + token);
                    HttpEntity<String> entityCampanha = new HttpEntity<>(headersCampanha);
                    ResponseEntity<String> responseCampanha = restTemplate.exchange(url, HttpMethod.GET, entityCampanha, String.class);

                    System.out.println("Resposta da URL da campanha: " + responseCampanha.getBody() + ", " + token + ", " + idCampanha);

                    fimData.getTime();

                    //Enviar o JSON para a tabela testes_ti_natal_clientes
                    try {
                        EnvioClienteLog envioClienteLog = new EnvioClienteLog();
                        envioClienteLog.setUuid(idCampanha);
                        envioClienteLog.setRequestJson(null);
                        envioClienteLog.setResponseJson(response.getBody());
                        envioClienteLog.setStatus(0);
                        envioClienteLog.setDateTime(new Date());
                        envioClienteLog.setHorarioInicio(inicioData);
                        envioClienteLog.setHorarioFim(fimData);

                        envioClienteLogRepository.save(envioClienteLog);
                    }catch (Exception e) {
                        System.out.println("Erro no inserir: " + e);
                    }
                    System.out.println("Dados inseridos na tabela testes_ti_natal_clientes");

                    //Envia as variaveis usadas para cadastro em outra tabela testes_ti_natal_variaveis
                    try {
                        EnvioVariaveis envioVariaveis = new EnvioVariaveis();
                        envioVariaveis.setSeqpessoa(nextSeq);
                        envioVariaveis.setNomerazao(nome);
                        envioVariaveis.setNrocgcpf(cnpjCpf9);
                        envioVariaveis.setDigcgcpf(cnpjCpf2);
                        envioVariaveis.setNroformapagto(vlrFormaPagamento);
                        envioVariaveis.setVlrlimitecredito(valorRecebido);
                        envioVariaveis.setNrocartao(cpfCompleto);
                        envioVariaveis.setSeqpessoatitular(nextSeq);
                        envioVariaveis.setSeqpessoaportador(nextSeq);

                        envioVariaveisLog.save(envioVariaveis);

                    }catch (Exception e) {
                        System.out.println("Erro no inserir: " + e);
                    }
                    System.out.println("Dados inseridos na tabela testes_ti_natal_variaveis");

                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

