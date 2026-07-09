import Album.Album;
import Album.Selecoes;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

// TODO add javadocs to important methods
// TODO treat exceptions

public class Main {
    static Selecoes selecoes = Selecoes.getInstance();
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        int linhas = 0, colunas = 0, instrucao = 0;
        Album album = null;
        int[][] novas = new int[0][0];

        while (true) {
            printMenu();
            try {
                instrucao = scanner.nextInt();
                scanner.nextLine();
            } catch (InputMismatchException e) {
                //System.err.println("erm"); //TODO
                scanner.nextLine();
                instrucao = 8;
            }
            switch (instrucao) {
                case 1 -> {
                    try {
                        System.out.print("Digite o nome do álbum a ser carregado: ");
                        String nomeArquivo = scanner.nextLine().trim().toLowerCase();
                        album = new Album(nomeArquivo, selecoes.getSELECOES(), selecoes.getJOGADORES());
                        linhas = album.getMatriz().length;
                        colunas = album.getMatriz()[0].length;
                        novas = new int[linhas][colunas];
                    } catch(FileNotFoundException e) {
                        System.out.println("Álbum não encontrado");
                    }
                }
                case 2 -> {
                    if(album == null) {
                        System.out.println("Carregue seu álbum primeiro!");
                        continue;
                    }
                    inserirCarta(scanner, album, novas);
                }
                case 3 -> {
                    try {
                        if(album == null) {
                            System.out.println("Carregue seu álbum primeiro!");
                            continue;
                        }
                        String nomeArquivo;
                        Album albumTroca;
                        int[][] albumTrocaMatriz;
                        int[][] albumUsuario = album.getMatriz();
                        System.out.print("Álbum com que realizar a troca: ");
                        nomeArquivo = scanner.nextLine().trim().toLowerCase();
                        if(nomeArquivo.equals(album.getNome())) {
                            System.out.println("Você não pode trocar com si mesmo");
                        } else {
                            albumTroca = new Album(nomeArquivo, selecoes.getSELECOES(), selecoes.getJOGADORES());
                            albumTrocaMatriz = albumTroca.getMatriz();
                            if(albumTrocaMatriz.length != linhas || albumTrocaMatriz[0].length != colunas) {
                                System.out.println("Álbuns de tipos diferentes");
                                break;
                            }
                            for (int i = 0; i < linhas; i++) {
                                for (int j = 0; j < colunas; j++) {
                                    if (albumUsuario[i][j] == 0 && albumTrocaMatriz[i][j] > 1) {
                                        for (int k = 0; k < linhas; k++) {
                                            for (int l = 0; l < colunas; l++) {
                                                if (albumTrocaMatriz[k][l] == 0 && albumUsuario[k][l] > 1) {
                                                    albumUsuario[i][j]++;
                                                    albumTrocaMatriz[i][j]--;
                                                    albumUsuario[k][l]--;
                                                    albumTrocaMatriz[k][l]++;
                                                    novas[i][j]++;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            System.out.println("Seu álbum atualizado:");
                            album.exibirMatriz(); //ou toString()?
                            System.out.println("Outro álbum atualizado:");
                            albumTroca.exibirMatriz();
                        }
                    } catch(FileNotFoundException e) {
                        System.out.println("Álbum não encontrado");
                    }
                }
                case 4 -> {
                    if(album == null) {
                        System.out.println("Carregue seu álbum primeiro!");
                        continue;
                    }
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
                case 5 -> {
                    if(album == null) {
                        System.out.println("Carregue seu álbum primeiro!");
                        continue;
                    }
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
                case 6 -> {
                    if(album == null) {
                        System.out.println("Carregue seu álbum primeiro!");
                        continue;
                    }
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
                        System.out.printf("Seleção %s %.1f%% completa\n", selecoes.getSelecao(i), porcSelecAtual);
                    }
                    porc /= linhas * colunas;
                    System.out.printf("Álbum %.1f%% completo\n", porc);
                    int figurinhasNovas = 0;
                    System.out.print("Figurinhas novas desde o carregamento do álbum " + album.getNome() + ":");
                    for(int i = 0; i < linhas; i++) {
                        for(int j = 0; j < colunas; j++) {
                            if(novas[i][j] == 1) {
                                if(figurinhasNovas == 0) {
                                    System.out.println();
                                }
                                System.out.println("\tSeleção " + selecoes.getSelecao(i) + " jogador " + j);
                                figurinhasNovas++;
                            }
                        }
                    }
                    if(figurinhasNovas == 0) System.out.println(" nenhuma");
                    else System.out.println("Total: " + figurinhasNovas + (figurinhasNovas > 1? " figurinhas novas" : " figurinha nova"));
                }
                case 7 -> {
                    return;
                }
                default -> System.out.println("Comando inválido");
            }
        }
    }

    private static void inserirCarta(Scanner scanner, Album album, int[][] novas) { //todo fix not finding an album
        int jogador, quantidade, selecaoNum = -1;
        while(selecaoNum == -1) {
            System.out.print("Digite o nome da seleção: ");
            String selecaoNome = scanner.nextLine().trim();
            if (selecaoNome.equals("sair"))
                return;
            selecaoNum = selecoes.acharSelecao(selecaoNome);
            if (selecaoNum != -1)
                break;
            System.out.println("Seleção não encontrada, tente de novo, ou digite \"sair\" para desistir");
        }
        System.out.print("Digite o número do jogador: "); // TODO: Shirt number?
        jogador = scanner.nextInt();
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
}
