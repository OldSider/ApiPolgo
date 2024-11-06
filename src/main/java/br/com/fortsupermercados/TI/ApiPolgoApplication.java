package br.com.fortsupermercados.TI;

import br.com.fortsupermercados.TI.services.AuthService;
import br.com.fortsupermercados.TI.services.EnvioDoc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class ApiPolgoApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(ApiPolgoApplication.class, args);

		// Autenticar e obter o token
		AuthService authService = context.getBean(AuthService.class);
		authService.authenticate();

		// Enviar os documentos
		EnvioDoc envioDoc = context.getBean(EnvioDoc.class);
		envioDoc.sendDocuments();
	}
}
