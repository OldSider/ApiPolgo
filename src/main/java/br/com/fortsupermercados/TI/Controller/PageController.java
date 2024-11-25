package br.com.fortsupermercados.TI.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class PageController {

    @GetMapping("/dashboard")
    public Map<String, Object> getDashboardData() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("usuario", "12345678901"); // Exemplo: CPF
        requestBody.put("extra", "5"); // Exemplo: quantidade total
        requestBody.put("numeroDocumento", "DOC12320241118"); // Exemplo: doc + data formatada
        requestBody.put("dataHoraEmissao", "2024-11-18T10:00:00"); // Exemplo: timestamp
        requestBody.put("valorTotal", 150.75); // Exemplo: BigDecimal
        requestBody.put("cnpjEmitente", "12345678000199"); // Exemplo: CNPJ

        Map<String, Object> campanha = new HashMap<>();
        campanha.put("identificacao", "FORTNATAL24");
        campanha.put("ano", 2024);
        requestBody.put("campanha", campanha);

        Map<String, Object> vendedor = new HashMap<>();
        vendedor.put("codigo", "789");
        vendedor.put("nome", "João da Silva");
        requestBody.put("vendedor", vendedor);

        return requestBody;
    }
}
