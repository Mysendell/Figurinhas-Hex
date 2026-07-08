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
        int instrucao = 0;
        Album album = null;

        while (true) {
            printMenu();
            try {
                instrucao = scanner.nextInt();
                scanner.nextLine();
            } catch (InputMismatchException e) {
                //System.err.println("erm"); //TODO
                scanner.nextLine();
                instrucao = 7;
            }
            switch (instrucao) {
                case 1 -> {
                    try {
                        System.out.print("Digite o nome do álbum a ser carregado: ");
                        String nomeArquivo = scanner.nextLine().trim().toLowerCase();
                        album = new Album(nomeArquivo, selecoes.getSELECOES(), selecoes.getJOGADORES());
                    } catch(FileNotFoundException e) {
                        System.out.println("Álbum não encontrado");
                    }
                }
                case 2 -> {
                    if(album == null) {
                        System.out.println("Carregue seu álbum primeiro!");
                        continue;
                    }
                    inserirCarta(scanner, album);
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
                            for (int i = 0; i < albumUsuario.length; i++) {
                                for (int j = 0; j < albumUsuario[0].length; j++) {
                                    if (albumUsuario[i][j] == 0 && albumTrocaMatriz[i][j] > 1) {
                                        for (int k = 0; k < albumTrocaMatriz.length; k++) {
                                            for (int l = 0; l < albumTrocaMatriz[0].length; l++) {
                                                if (albumTrocaMatriz[k][l] == 0 && albumUsuario[k][l] > 1) {
                                                    albumUsuario[i][j]++;
                                                    albumTrocaMatriz[i][j]--;
                                                    albumUsuario[k][l]--;
                                                    albumTrocaMatriz[k][l]++;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            System.out.println("Seu álbum atualizado:");
                            System.out.println(album);
                            System.out.println("Outro álbum atualizado:");
                            System.out.println(albumTroca);
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
                    boolean possuiFaltantes = false;
                    System.out.print("Figurinhas faltantes do álbum " + album.getNome() + ": ");
                    for(int i = 0; i < cartas.length; i++) {
                        for(int j = 0; j < cartas[0].length; j++) {
                            if(cartas[i][j] == 0) {
                                if(!possuiFaltantes) {
                                    possuiFaltantes = true;
                                    System.out.println();
                                }
                                System.out.println("\tSeleção " + selecoes.getSelecao(i) + " jogador " + j);
                            }
                        }
                    }
                    if(!possuiFaltantes) System.out.println("Não possui");
                }
                case 5 -> {
                    if(album == null) {
                        System.out.println("Carregue seu álbum primeiro!");
                        continue;
                    }
                    int[][] cartas = album.getMatriz();
                    boolean possuiRepetidas = false;
                    System.out.print("Figurinhas repetidas do álbum " + album.getNome() + ": ");
                    for(int i = 0; i < cartas.length; i++) {
                        for(int j = 0; j < cartas[0].length; j++) {
                            if(cartas[i][j] > 1) {
                                if(!possuiRepetidas) {
                                    possuiRepetidas = true;
                                    System.out.println();
                                }
                                System.out.println("\tSeleção " + selecoes.getSelecao(i) + " jogador " + j);
                            }
                        }
                    }
                    if(!possuiRepetidas) System.out.println("Não possui");
                }
                case 6 -> {
                    return;
                }
                default -> System.out.println("Comando inválido");
            }
        }
    }

    private static void inserirCarta(Scanner scanner, Album album) { //todo fix not finding an album
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
        System.out.println("\t6. Sair");
        System.out.println("-".repeat(35));
    }
}
