package br.com.fortsupermercados.TI.components;

import java.util.Random;

public class CPFGenerator {

    public static String generateCPF() {
        Random random = new Random();
        int[] cpf = new int[11];

        for (int i = 0; i < 9; i++) {
            cpf[i] = random.nextInt(10);
        }

        int sum = 0;
        for (int i = 0, peso = 10; i < 9; i++, peso--) {
            sum += cpf[i] * peso;
        }
        int resto = sum % 11;
        cpf[9] = (resto < 2) ? 0 : 11 - resto;

        sum = 0;
        for (int i = 0, peso = 11; i < 10; i++, peso--) {
            sum += cpf[i] * peso;
        }
        resto = sum % 11;
        cpf[10] = (resto < 2) ? 0 : 11 - resto;

        StringBuilder cpfStr = new StringBuilder();
        for (int digit : cpf) {
            cpfStr.append(digit);
        }

        return cpfStr.toString();
    }

    public static void main(String[] args) {
        String cpf = generateCPF();
    }
}
