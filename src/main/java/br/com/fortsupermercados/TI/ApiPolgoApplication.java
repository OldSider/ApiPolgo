package br.com.fortsupermercados.TI;

import br.com.fortsupermercados.TI.components.CPFGenerator;
import br.com.fortsupermercados.TI.services.AuthService;
import br.com.fortsupermercados.TI.services.EnvioDoc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Timer;
import java.util.TimerTask;

@SpringBootApplication
@EnableScheduling
public class ApiPolgoApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(ApiPolgoApplication.class, args);
	}
}
//