package Album;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class Selecoes {
    private final int SELECOES, JOGADORES;
    private static final String caminhoArquivo = "/src/data/selecoes.txt";
    private static Selecoes instance;
    private String[] selecoes;

    private Selecoes(){
        String filePath = new File("").getAbsolutePath().concat(caminhoArquivo);
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String[] dimensoes = br.readLine().trim().split("\\s+");
            SELECOES = Integer.parseInt(dimensoes[0]);
            JOGADORES = Integer.parseInt(dimensoes[1]);

            selecoes = new String[SELECOES];
            for (int i = 0; i < SELECOES; i++) {
                selecoes[i] = br.readLine().trim().toLowerCase();
            }
            System.out.println(Arrays.toString(selecoes));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Selecoes getInstance() {
        if(instance == null)
            instance = new Selecoes();
        return instance;
    }

    public int acharSelecao(String selecao){
        selecao = selecao.trim().toLowerCase();
        for(int i=0; i < SELECOES; i++){
            if(selecoes[i].equals(selecao))
                return i;
        }
        return -1;
    }

    public String getSelecao(int numero){
        return selecoes[numero];
    }

    public int getSELECOES() {
        return SELECOES;
    }

    public int getJOGADORES() {
        return JOGADORES;
    }

    public String[] getSelecoes() {
        return selecoes;
    }
}
