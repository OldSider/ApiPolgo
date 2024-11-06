package br.com.fortsupermercados.TI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import br.com.fortsupermercados.TI.services.AuthService;

@SpringBootApplication
public class ApiPolgoApplication {

	public static void main(String[] args) {
		// Inicia o contexto da aplicação Spring
		ApplicationContext context = SpringApplication.run(ApiPolgoApplication.class, args);

		// Obtém o bean AuthService do contexto
		AuthService authService = context.getBean(AuthService.class);

		// Chama o método authenticate para realizar a autenticação
		authService.authenticate();

		// Obtém o token
		String token = authService.getToken();
	}
}
