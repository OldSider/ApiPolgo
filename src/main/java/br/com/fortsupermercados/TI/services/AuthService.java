package br.com.fortsupermercados.TI.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    @Value("${url.base}")
    private String baseUrl;

    @Value("${auth.endpoint}")
    private String authEndpoint;

    @Value("${auth.usuario}")
    private String usuario;

    @Value("${auth.senha}")
    private String senha;

    private String token;

    public void authenticate() {
        String url = baseUrl + authEndpoint;

        RestTemplate restTemplate = new RestTemplate();

        // Construir o corpo da requisição
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("usuario", usuario);
        requestBody.put("senha", senha);

        // Definir os headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Criar a entidade da requisição
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            // Fazer a requisição POST
            ResponseEntity<Map> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = responseEntity.getBody();

                if (responseBody != null && responseBody.containsKey("retorno")) {
                    Map<String, Object> retorno = (Map<String, Object>) responseBody.get("retorno");

                    if (retorno.containsKey("token")) {
                        token = (String) retorno.get("token");
                        System.out.println("Token obtido: " + token);
                    } else {
                        System.out.println("Token não encontrado na resposta.");
                    }
                } else {
                    System.out.println("Resposta inválida do servidor.");
                }
            } else {
                System.out.println("Falha na autenticação. Código de status: " + responseEntity.getStatusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getToken() {
        return token;
    }
}
