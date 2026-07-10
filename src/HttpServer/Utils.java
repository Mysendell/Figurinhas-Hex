package HttpServer;

import Album.Album;
import Album.Selecoes;

public class Utils {
    public static String faltantes(int[][] album){
        StringBuilder albumFaltantes = new StringBuilder("Cartas que faltam para completar o seu album:");
        int numSelecoes = album.length, numJogadores = album[0].length;
        int total = 0;

        for(int i=0; i < numSelecoes; i++){
            StringBuilder selecao = new StringBuilder( Selecoes.getInstance().getSelecao(i) + ":\n");
            boolean temFaltante = false;
            for(int j=0; j < numJogadores; j++){
                if(album[i][j] == 0){
                    temFaltante = true;
                    selecao.append(j+1).append(" ");
                    total++;
                }
            }
            if(temFaltante) albumFaltantes.append(selecao).append("\n");
        }
        albumFaltantes.append("\nTotal cartas faltantes: ").append(total);
        return albumFaltantes.toString();
    }
    public static String repetentes(int[][] album){
        StringBuilder albumRepetentes = new StringBuilder("Cartas que estão repetidas no seu album:");
        int numSelecoes = album.length, numJogadores = album[0].length;
        int total = 0;

        for(int i=0; i < numSelecoes; i++){
            StringBuilder selecao = new StringBuilder(Selecoes.getInstance().getSelecao(i) + ":\n");
            boolean temRepetente = false;
            for(int j=0; j < numJogadores; j++){
                if(album[i][j] > 1){
                    temRepetente = true;
                    selecao.append(j+1).append(" ");
                    total++;
                }
            }
            if(temRepetente) albumRepetentes.append(selecao).append("\n");
        }
        albumRepetentes.append("\nTotal cartas repetidas: ").append(total);
        return albumRepetentes.toString();
    }
    public static String comparar(int[][] album, int[][] album2){
        StringBuilder response = new StringBuilder("Troca possivel entre os albums:\n");
        StringBuilder receberia = new StringBuilder("Você receberia:\n");
        StringBuilder enviaria = new StringBuilder("Você enviaria:\n");
        int numSelecoes = album.length, numJogadores = album[0].length;

        for(int i=0; i < numSelecoes; i++){
            StringBuilder selecaoReceba = new StringBuilder("\n" + Selecoes.getInstance().getSelecao(i) + ":\n");
            StringBuilder selecaoEnvia = new StringBuilder("\n" + Selecoes.getInstance().getSelecao(i) + ":\n");
            boolean recebe = false, envia = false;
            for(int j=0; j < numJogadores; j++){
                if(album[i][j] == 0 && album2[i][j] > 1){
                    recebe = true;
                    selecaoReceba.append(j+1).append(" ");
                }
                if(album2[i][j] == 0 && album[i][j] > 1){
                    envia = true;
                    selecaoEnvia.append(j+1).append(" ");
                }
            }
            if(recebe) receberia.append(selecaoReceba).append("\n");
            if(envia) enviaria.append(selecaoEnvia).append("\n");
        }
        response.append(receberia).append(";;").append(enviaria);
        return response.toString();
    }
    public static void trocar(int[][] album, int[][] album2, int[][] novas){
        int numSelecoes = album.length, numJogadores = album[0].length;
        for(int i=0; i < numSelecoes; i++){
            for(int j=0; j < numJogadores; j++){
                if(album[i][j] == 0 && album2[i][j] > 1){
                    album[i][j]++;
                    novas[i][j]++;
                    album2[i][j]--;
                }
                if(album2[i][j] == 0 && album[i][j] > 1){
                    album[i][j]--;
                    album2[i][j]++;
                }
            }
        }
    }
    public static String gerarRelatorio(Album album, int[][] novas) {
        int[][] figurinhas = album.getMatriz();
        int numSelecoes = figurinhas.length, numJogadores = figurinhas[0].length;
        double porc = 0;
        double porcSelecAtual;
        Selecoes selecoes = Selecoes.getInstance();
        StringBuilder relatorio = new StringBuilder("Relatorio de ").append(album.getNome()).append("\n");

        for(int i = 0; i < numSelecoes; i++) {
            porcSelecAtual = 0;
            for(int j = 0; j < numJogadores; j++) {
                if(figurinhas[i][j] >= 1) {
                    porc++;
                    porcSelecAtual++;
                }
            }
            porcSelecAtual /= numJogadores;
            relatorio.append(String.format("Seleção %s:\n %.1f%% completa\n\n", selecoes.getSelecao(i), porcSelecAtual*100));
            if((i+1) % 3 == 0)
                relatorio.append(";;");
        }
        porc /= numSelecoes * numJogadores;
        relatorio.append(String.format("Álbum: %.1f%% completo;;", porc*100));
        int figurinhasNovas = 0;
        relatorio.append("Figurinhas novas desde o carregamento do álbum ").append(album.getNome()).append(":\n");
        for(int i = 0; i < numSelecoes; i++) {
            for(int j = 0; j < numJogadores; j++) {
                if(novas[i][j] == 1) {
                    if(figurinhasNovas == 0) {
                        relatorio.append("\n");
                    }
                    relatorio.append("\tSeleção ").append(selecoes.getSelecao(i)).append(" jogador ").append(j).append("\n");
                    figurinhasNovas++;
                }
            }
        }
        if(figurinhasNovas == 0) relatorio.append(" nenhuma");
        else relatorio.append("\nTotal: ").append(figurinhasNovas).append(figurinhasNovas > 1 ? " figurinhas novas" : " figurinha nova");
        return relatorio.toString();
    }
}
