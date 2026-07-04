import Album.Album;
import Album.Selecoes;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

// TODO add javadocs to important methods
// TODO treat exceptions

public class Main {
    static Selecoes selecoes = Selecoes.getInstance();
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        int instrucao = 0;
        Album album = null;

        do {
            printMenu();
            try {
                instrucao = scanner.nextInt();
                scanner.nextLine();
            } catch (InputMismatchException e) {
                System.err.println("erm"); //TODO
            }
            switch (instrucao) {
                case 1 -> {
                    System.out.println("Digite o nome do album a ser carregado: ");
                    String nomeArquivo = scanner.nextLine().trim().toLowerCase();
                    album = new Album(nomeArquivo, selecoes.getSELECOES(), selecoes.getJOGADORES());
                }
                case 2 -> {
                    inserirCarta(scanner, album);
                }
            }
        } while (instrucao != 6);
    }

    private static void inserirCarta(Scanner scanner, Album album) {
        int jogador, quantidade, selecaoNum = 0;
        while (selecaoNum != -1) {
            System.out.println("Digite o nome da selecao: ");
            String selecaoNome = scanner.nextLine().trim().toLowerCase();
            if (selecaoNome.equals("sair"))
                return;

            selecaoNum = selecoes.acharSelecao(selecaoNome);
            if (selecaoNum != -1)
                break;
            System.out.println("Selecao não encontrada, tente denovo, ou digite sair para desistir");
        }
        System.out.println("Digite o numero do jogador: "); // TODO: Shirt number?
        jogador = scanner.nextInt();
        System.out.println("Digite a quantidade de cards a inserir: ");
        quantidade = scanner.nextInt();
        album.inserirJogador(selecaoNum, jogador, quantidade);
        scanner.nextLine();
    }

    private static void printMenu() {

    }
}
