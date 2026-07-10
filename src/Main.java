import Album.Album;
import Album.Selecoes;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;


public class Main {
    static Selecoes selecoes = Selecoes.getInstance();
    static Scanner scanner = new Scanner(System.in);
    static Album album = null;
    static int linhas = 0, colunas = 0;
    static int[][] novas = new int[0][0];
    public static void main(String[] args) throws IOException {
        int instrucao = 0;

        while (true) {
            printMenu();
            try {
                instrucao = scanner.nextInt();
                scanner.nextLine();
            } catch (InputMismatchException e) {
                scanner.nextLine();
                instrucao = 8;
            }
            switch (instrucao) {
                case 1 -> {
                    try {
                        System.out.print("Digite o nome do álbum a ser carregado: ");
                        String nomeArquivo = scanner.nextLine().trim().toLowerCase();
                        carregarAlbum(nomeArquivo);
                    } catch(FileNotFoundException e) {
                        System.out.println("Álbum não encontrado");
                    }
                }
                case 2 -> {
                    if(album == null) {
                        System.out.println("Carregue seu álbum primeiro!");
                        continue;
                    }
                    try {
                        inserirCarta(album, novas);
                    } catch(IndexOutOfBoundsException e) {
                        System.out.println("Jogador inexistente");
                    }
                }
                case 3 -> {
                    try {
                        if(album == null) {
                            System.out.println("Carregue seu álbum primeiro!");
                            continue;
                        }
                        troca();
                    } catch(FileNotFoundException e) {
                        System.out.println("Álbum não encontrado");
                    }
                }
                case 4 -> {
                    if(album == null) {
                        System.out.println("Carregue seu álbum primeiro!");
                        continue;
                    }
                    mostrarFaltantes(album);
                }
                case 5 -> {
                    if(album == null) {
                        System.out.println("Carregue seu álbum primeiro!");
                        continue;
                    }
                    mostrarRepetidas(album);
                }
                case 6 -> {
                    if(album == null) {
                        System.out.println("Carregue seu álbum primeiro!");
                        continue;
                    }
                    gerarRelatorio();
                }
                case 7 -> {
                    return;
                }
                default -> System.out.println("Comando inválido");
            }
        }
    }

    private static void inserirCarta(Album album, int[][] novas) throws IndexOutOfBoundsException { //todo fix not finding an album
        int jogador, quantidade, selecaoNum = -1;
        while(true) {
            System.out.print("Digite o nome da seleção: ");
            String selecaoNome = scanner.nextLine().trim();
            if (selecaoNome.equals("sair"))
                return;
            selecaoNum = selecoes.acharSelecao(selecaoNome);
            if (selecaoNum != -1)
                break;
            System.out.println("Seleção não encontrada, tente de novo, ou digite \"sair\" para desistir");
        }
        System.out.print("Digite o número do jogador: ");
        jogador = scanner.nextInt()-1;
        System.out.print("Digite a quantidade de figurinhas a inserir: ");
        quantidade = scanner.nextInt();
        album.inserirJogador(selecaoNum, jogador, quantidade);
        novas[selecaoNum][jogador]++;
        scanner.nextLine();
    }

    private static void printMenu() {
        System.out.println("-".repeat(35));
        System.out.println("Organizador de Figurinhas".toUpperCase());
        System.out.println("\t1. Carregar álbum");
        System.out.println("\t2. Registrar nova figurinha");
        System.out.println("\t3. Realizar trocas possíveis com outro álbum");
        System.out.println("\t4. Listar figurinhas faltantes");
        System.out.println("\t5. Listar figurinhas repetidas");
        System.out.println("\t6. Gerar relatório");
        System.out.println("\t7. Sair");
        System.out.println("-".repeat(35));
    }

    private static void carregarAlbum(String nomeArquivo) throws IOException {
        album = new Album(nomeArquivo, selecoes.getSELECOES(), selecoes.getJOGADORES());
        linhas = album.getMatriz().length;
        colunas = album.getMatriz()[0].length;
        novas = new int[linhas][colunas];
    }

    private static void troca() throws IOException {
        String nomeArquivo;
        Album albumTroca;
        int[][] albumTrocaMatriz;
        int[][] albumUsuarioMatriz = album.getMatriz().clone();
        int[][] novasClone = novas.clone();
        System.out.print("Álbum com que realizar a troca: ");
        nomeArquivo = scanner.nextLine().trim().toLowerCase();
        if(nomeArquivo.equals(album.getNome())) {
            System.out.println("Você não pode trocar com si mesmo");
        } else {
            albumTroca = new Album(nomeArquivo, selecoes.getSELECOES(), selecoes.getJOGADORES());
            albumTrocaMatriz = albumTroca.getMatriz().clone();
            if(albumTrocaMatriz.length != linhas || albumTrocaMatriz[0].length != colunas) {
                System.out.println("Álbuns de tipos diferentes");
                return;
            }
            System.out.println("Calculando as trocas possíveis...\n");
            for(int i=0; i < linhas; i++){
                for(int j=0; j < colunas; j++){
                    if(albumUsuarioMatriz[i][j] == 0 && albumTrocaMatriz[i][j] > 1){
                        albumUsuarioMatriz[i][j]++;
                        novasClone[i][j]++;
                        albumTrocaMatriz[i][j]--;
                        System.out.println("Seu álbum recebe seleção " + selecoes.getSelecao(i) + " jogador " + (j+1) + " do outro");
                    }
                    if(albumTrocaMatriz[i][j] == 0 && albumUsuarioMatriz[i][j] > 1){
                        albumUsuarioMatriz[i][j]--;
                        albumTrocaMatriz[i][j]++;
                        System.out.println("O outro álbum recebe seleção " + selecoes.getSelecao(i) + " jogador " + (j+1) + " de você\n");
                    }
                }
            }
            System.out.println("Seu álbum atualizado será:");
            System.out.println(Arrays.deepToString(albumUsuarioMatriz));
            System.out.println("O outro álbum atualizado será:");
            System.out.println(Arrays.deepToString(albumTrocaMatriz));
            System.out.println("Aceitar? (Y/N)");
            char input = Character.toLowerCase(scanner.next().charAt(0));
            if(input == 'y') {
                album.setMatriz(albumUsuarioMatriz);
                albumTroca.setMatriz(albumTrocaMatriz);
                album.salvarAlbum();
                albumTroca.salvarAlbum();
                novas = novasClone;
                System.out.println("Troca realizada.");
            }
        }
    }

    private static void mostrarFaltantes(Album album) {
        int[][] cartas = album.getMatriz();
        int totalFaltantes = 0;
        System.out.print("Figurinhas faltantes do álbum " + album.getNome() + ":");
        for(int i = 0; i < linhas; i++) {
            for(int j = 0; j < colunas; j++) {
                if(cartas[i][j] == 0) {
                    if(totalFaltantes == 0) {
                        System.out.println();
                    }
                    System.out.println("\tSeleção " + selecoes.getSelecao(i) + " jogador " + j);
                    totalFaltantes++;
                }
            }
        }
        if(totalFaltantes == 0) System.out.println(" não possui");
        else System.out.println("Total: " + totalFaltantes + (totalFaltantes > 1? " figurinhas faltantes" : " figurinha faltante"));
    }

    private static void mostrarRepetidas(Album album) {
        int[][] cartas = album.getMatriz();
        int totalRepetidas = 0;
        System.out.print("Figurinhas repetidas do álbum " + album.getNome() + ":");
        for(int i = 0; i < linhas; i++) {
            for(int j = 0; j < colunas; j++) {
                if(cartas[i][j] > 1) {
                    if(totalRepetidas == 0) {
                        System.out.println();
                    }
                    System.out.println("\tSeleção " + selecoes.getSelecao(i) + " jogador " + j + " - x" + cartas[i][j]);
                    totalRepetidas++;
                }
            }
        }
        if(totalRepetidas == 0) System.out.println(" não possui");
        else System.out.println("Total: " + totalRepetidas + (totalRepetidas > 1? " figurinhas repetidas" : " figurinha repetida"));
    }

    private static void gerarRelatorio() {
        int[][] figurinhas = album.getMatriz();
        double porc = 0;
        double porcSelecAtual;
        for(int i = 0; i < linhas; i++) {
            porcSelecAtual = 0;
            for(int j = 0; j < colunas; j++) {
                if(figurinhas[i][j] >= 1) {
                    porc++;
                    porcSelecAtual++;
                }
            }
            porcSelecAtual /= colunas;
            System.out.printf("Seleção %s %.1f%% completa\n", selecoes.getSelecao(i), porcSelecAtual*100);
        }
        porc /= linhas * colunas;
        System.out.printf("Álbum %.1f%% completo\n", porc*100);
        int figurinhasNovas = 0;
        System.out.print("Figurinhas novas desde o carregamento do álbum " + album.getNome() + ":");
        for(int i = 0; i < linhas; i++) {
            for(int j = 0; j < colunas; j++) {
                if(novas[i][j] == 1) {
                    if(figurinhasNovas == 0) {
                        System.out.println();
                    }
                    System.out.println("\tSeleção " + selecoes.getSelecao(i) + " jogador " + (j+1));
                    figurinhasNovas++;
                }
            }
        }
        if(figurinhasNovas == 0) System.out.println(" nenhuma");
        else System.out.println("Total: " + figurinhasNovas + (figurinhasNovas > 1? " figurinhas novas" : " figurinha nova"));
    }
}
