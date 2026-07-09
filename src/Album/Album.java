package Album;

import java.io.*;

public class Album {
    private int[][] matriz;
    private String caminhoArquivo;
    private String nome;

    //=======================
    // Método de leitura do arquivo
    //=======================
    public Album(String caminhoArquivo, int selecoesNum, int jogadoresNum) throws IOException, FileNotFoundException {
        nome = caminhoArquivo;
        this.caminhoArquivo = new File("").getAbsolutePath().concat("/src/data/" + nome + "Album.txt");

        try (BufferedReader br = new BufferedReader(new FileReader(this.caminhoArquivo))) {
            matriz = new int[selecoesNum][jogadoresNum];

            // próximas M linhas: valores da matriz que representam as figrinhas
            String linha;
            while ((linha = br.readLine()) != null) {
                String selecaoNome = linha.trim();
                String[] valores = br.readLine().trim().split("\\s+");
                int selecao = Selecoes.getInstance().acharSelecao(selecaoNome);
                if (selecao == -1) {
                    System.out.println("Selecao: " + selecaoNome + ", Não Existe, ignorando");
                    continue;
                }
                for (int j = 0; j < jogadoresNum; j++) {
                    matriz[selecao][j] = Integer.parseInt(valores[j]);//Converte valores para inteiro
                }
            }

            System.out.println("Álbum carregado com sucesso!");
            System.out.printf("Seleções: %d | Jogadores por seleção: %d%n", matriz.length, jogadoresNum);
        }
    }

    public void inserirJogador(int selecaoNum, int jogadorNum, int quantidade) {
        if (jogadorNum >= matriz[selecaoNum].length) {
            System.out.println("Jogador Fora da matriz"); // TODO: shirt number??
        }
        matriz[selecaoNum][jogadorNum] += quantidade;
        this.salvarAlbum();
        System.out.println("Carta inserida com sucesso");
    }

    //=======================
    // Método para imprimir o álbum atual
    //=======================
    public void exibirMatriz() {
        if (matriz == null) {
            System.out.println("Nenhum álbum carregado.");
            return;
        }
        // cabeçalho com números dos jogadores
        System.out.println("ALBUM ATUAL");
        System.out.printf("%-15s", "Seleção");
        //Imprime a palavra e reserva 15 espaços para impressão, alinhando à esquerda
        for (int j = 1; j <= matriz[0].length; j++) {
            System.out.printf(" J%-3d", j);
        }
        System.out.println();

        // linha separadora
        //Imprime 15 hifens e depois mais 5 hifens por coluna da matriz
        System.out.println("-".repeat(15 + matriz.length * 5));

        // linhas da matriz
        for (int i = 0; i < matriz.length; i++) {
            //System.out.printf("%-15s", selecoes[i]);
            for (int j = 0; j < matriz[0].length; j++) {
                System.out.printf(" %-4d", matriz[i][j]);
            }
            System.out.println();
        }
    }

    public void salvarAlbum() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(caminhoArquivo))) {
            writer.write(this.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString(){
        String string = "";
        int selecaoNum = matriz.length, jogadorNum = matriz[0].length;
        Selecoes selecoes = Selecoes.getInstance();
        for(int i=0; i < selecaoNum; i++){
            String selecao = selecoes.getSelecao(i) + "\n";
            int cartas = 0;
            for(int j=0; j < jogadorNum; j++){
                selecao = selecao.concat(matriz[i][j] + " ");
                cartas += matriz[i][j];
            }
            selecao = selecao.concat("\n");
            if(cartas > 0)
                string = string.concat(selecao);
        }
        return string;
    }

    public String getNome() {
        return nome;
    }

    public int[][] getMatriz() {
        return matriz;
    }
}