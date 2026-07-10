package HttpServer;

import Album.Album;
import Album.Selecoes;
import com.google.gson.*;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


public class ServerMain {
    static Selecoes selecoes = Selecoes.getInstance();
    static Map<String, Album> albums = new HashMap<>();
    static Map<String, int[][]> novas = new HashMap<>();

    public static void main(String[] args) throws IOException {
        startServer();
    }

    private static void startServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8989), 0);

        server.createContext("/api", new RootHandler());
        server.createContext("/api/sair", new ServerMain.SairHandler());
        server.createContext("/api/trocar", new ServerMain.TrocarHandler());
        server.createContext("/api/comparar", new ServerMain.CompararHandler());
        server.createContext("/api/carregar", new ServerMain.carregarHandler());
        server.createContext("/api/relatorio", new ServerMain.RelatorioHandler());
        server.createContext("/api/registrar", new ServerMain.RegistrarHandler());
        server.createContext("/api/listarFalta", new ServerMain.FaltantesHandler());
        server.createContext("/api/listarRepete", new ServerMain.RepetidasHandler());



        server.setExecutor(null);

        server.start();
        System.out.println("Server started");
    }

    static class RepetidasHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "Erro desconhecido";
            int code = 400;
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

            try {
                JsonObject bodyJson = JsonParser.parseString(body).getAsJsonObject();
                String nomeArquivo = bodyJson.get("nome").getAsString().trim().toLowerCase();
                if (nomeArquivo.isEmpty()) {
                    response = "Conta não pode ser vazia";
                    code = 422;
                } else if (!albums.containsKey(nomeArquivo)) {
                    response = "Conta não carregada";
                    code = 401;
                } else {
                    code = 200;
                    response = Utils.repetentes(albums.get(nomeArquivo).getMatriz());
                }
            } catch (NullPointerException e) {
                response = "Conta não incluida";
            } catch (Exception e) {
                System.err.println(e);
            } finally {
                exchange.sendResponseHeaders(code, response.getBytes(StandardCharsets.UTF_8).length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }

    static class RelatorioHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "Erro desconhecido";
            int code = 400;
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

            try {
                JsonObject bodyJson = JsonParser.parseString(body).getAsJsonObject();
                String nomeArquivo = bodyJson.get("nome").getAsString().trim().toLowerCase();
                if (nomeArquivo.isEmpty()) {
                    response = "Conta não pode ser vazia";
                    code = 422;
                } else if (!albums.containsKey(nomeArquivo)) {
                    response = "Conta não carregada";
                    code = 401;
                } else {
                    code = 200;
                    response = Utils.gerarRelatorio(albums.get(nomeArquivo), novas.get(nomeArquivo));
                }
            } catch (NullPointerException e) {
                response = "Conta não incluida";
            } catch (Exception e) {
                System.err.println(e);
            } finally {
                exchange.sendResponseHeaders(code, response.getBytes(StandardCharsets.UTF_8).length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }

    static class TrocarHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "Erro desconhecido";
            int code = 400;
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

            try {
                JsonObject bodyJson = JsonParser.parseString(body).getAsJsonObject();
                String nomeArquivo = bodyJson.get("nome").getAsString().trim().toLowerCase();
                String nomeArquivo2 = bodyJson.get("album2").getAsString().trim().toLowerCase();
                if (nomeArquivo.isEmpty()) {
                    response = "Conta não pode ser vazia";
                    code = 422;
                } else if (!albums.containsKey(nomeArquivo)) {
                    response = "Conta não carregada";
                    code = 401;
                } else if (nomeArquivo2.isEmpty()) {
                    response = "Segunda Conta não pode ser vazia";
                    code = 422;
                } else {
                    int numSelecoes = Selecoes.getInstance().getSELECOES(), numJogadores = Selecoes.getInstance().getJOGADORES();
                    Album album2;
                    Album album = albums.get(nomeArquivo);
                    if (albums.containsKey(nomeArquivo2))
                        album2 = albums.get(nomeArquivo2);
                    else
                        album2 = new Album(nomeArquivo2, numSelecoes, numJogadores);
                    Utils.trocar(album.getMatriz(), album2.getMatriz(), novas.get(nomeArquivo));
                    album.salvarAlbum();
                    album2.salvarAlbum();
                    code = 200;
                    response = "Troca realizada";
                }
            } catch (NullPointerException e) {
                response = "Conta não incluida";
                exchange.sendResponseHeaders(400, response.getBytes(StandardCharsets.UTF_8).length);
            } catch (FileNotFoundException e) {
                response = "Conta não existe";
            } catch (Exception e) {
                System.err.println(e);
            } finally {
                exchange.sendResponseHeaders(code, response.getBytes(StandardCharsets.UTF_8).length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }

    static class CompararHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "Erro desconhecido";
            int code = 400;
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

            try {
                JsonObject bodyJson = JsonParser.parseString(body).getAsJsonObject();
                String nomeArquivo = bodyJson.get("nome").getAsString().trim().toLowerCase();
                String nomeArquivo2 = bodyJson.get("album2").getAsString().trim().toLowerCase();
                if (nomeArquivo.isEmpty()) {
                    response = "Conta não pode ser vazia";
                    code = 422;
                } else if (!albums.containsKey(nomeArquivo)) {
                    response = "Conta não carregada";
                    code = 401;
                } else if (nomeArquivo2.isEmpty()) {
                    response = "Segunda Conta não pode ser vazia";
                    code = 422;
                } else {
                    int numSelecoes = Selecoes.getInstance().getSELECOES(), numJogadores = Selecoes.getInstance().getJOGADORES();
                    Album album2;
                    Album album = albums.get(nomeArquivo);
                    if (albums.containsKey(nomeArquivo2))
                        album2 = albums.get(nomeArquivo2);
                    else
                        album2 = new Album(nomeArquivo2, numSelecoes, numJogadores);
                    response = Utils.comparar(album.getMatriz(), album2.getMatriz());
                    code = 200;
                }
            } catch (NullPointerException e) {
                response = "Conta não incluida";
                exchange.sendResponseHeaders(400, response.getBytes(StandardCharsets.UTF_8).length);
            } catch (FileNotFoundException e) {
                response = "Conta não existe";
            } catch (Exception e) {
                System.err.println(e);
            } finally {
                exchange.sendResponseHeaders(code, response.getBytes(StandardCharsets.UTF_8).length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }

    static class RegistrarHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "Erro desconhecido";
            int code = 400;
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

            try {
                JsonObject bodyJson = JsonParser.parseString(body).getAsJsonObject();
                String nomeArquivo = bodyJson.get("nome").getAsString().trim().toLowerCase();
                if (nomeArquivo.isEmpty()) {
                    response = "Conta não pode ser vazia";
                    code = 422;
                } else if (!albums.containsKey(nomeArquivo)) {
                    response = "Conta não carregada";
                    code = 401;
                } else {
                    Album album = albums.get(nomeArquivo);
                    String selecao = bodyJson.get("selecao").getAsString().trim().toLowerCase();
                    int selecaoNum = Selecoes.getInstance().acharSelecao(selecao);
                    if (selecaoNum == -1) {
                        response = "Seleção não encontrada";
                        code = 401;
                        return;
                    }
                    int jogador = bodyJson.get("jogador").getAsInt() - 1;
                    if (jogador > Selecoes.getInstance().getJOGADORES()) {
                        code = 401;
                        response = "Jogador não existente";
                        return;
                    }
                    int quantidade = bodyJson.get("quantidade").getAsInt();
                    album.inserirJogador(selecaoNum, jogador, quantidade);
                    int[][] nova = novas.get(nomeArquivo);
                    nova[selecaoNum][jogador]++;
                    response = "Jogador adicionado ao album com sucesso";
                    code = 200;

                }
            } catch (NullPointerException e) {
                response = "Conta não incluida";
            } catch (Exception e) {
                System.err.println(e);
            } finally {
                exchange.sendResponseHeaders(code, response.getBytes(StandardCharsets.UTF_8).length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }

    static class FaltantesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "Erro desconhecido";
            int code = 400;
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

            try {
                JsonObject bodyJson = JsonParser.parseString(body).getAsJsonObject();
                String nomeArquivo = bodyJson.get("nome").getAsString().trim().toLowerCase();
                if (nomeArquivo.isEmpty()) {
                    response = "Conta não pode ser vazia";
                    code = 422;
                } else if (!albums.containsKey(nomeArquivo)) {
                    response = "Conta não carregada";
                    code = 401;
                } else {
                    Album album = albums.get(nomeArquivo);
                    response = Utils.faltantes(album.getMatriz());
                    code = 200;
                }
            } catch (NullPointerException e) {
                response = "Conta não incluida";
            } catch (Exception e) {
                System.err.println(e);
            } finally {
                exchange.sendResponseHeaders(code, response.getBytes(StandardCharsets.UTF_8).length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }

    static class SairHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "Erro desconhecido";
            int code = 400;
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

            try {
                JsonObject bodyJson = JsonParser.parseString(body).getAsJsonObject();
                String nomeArquivo = bodyJson.get("nome").getAsString().trim().toLowerCase();
                if (nomeArquivo.isEmpty()) {
                    response = "Conta não pode ser vazia";
                    code = 422;
                } else if (albums.containsKey(nomeArquivo)) {
                    albums.remove(nomeArquivo);
                    novas.remove(nomeArquivo);
                    response = "Conta descarregada";
                    code = 200;
                } else {
                    response = "Conta não carregada";
                    code = 411;
                }
            } catch (NullPointerException e) {
                response = "Conta não incluida";
            } catch (Exception e) {
                System.err.println(e);
            } finally {
                exchange.sendResponseHeaders(code, response.getBytes(StandardCharsets.UTF_8).length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }

    static class carregarHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "Erro desconhecido";
            int code = 400;
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

            try {
                JsonObject bodyJson = JsonParser.parseString(body).getAsJsonObject();
                String nomeArquivo = bodyJson.get("nome").getAsString().trim().toLowerCase();
                if (nomeArquivo.isEmpty()) {
                    response = "Conta não pode ser vazia";
                    code = 422;
                } else if (albums.containsKey(nomeArquivo)) {
                    response = "Esse conta já está em uso";
                    code = 401;
                } else {
                    Album album = new Album(nomeArquivo, selecoes.getSELECOES(), selecoes.getJOGADORES());
                    albums.put(nomeArquivo, album);
                    Selecoes selecoes = Selecoes.getInstance();
                    novas.put(nomeArquivo, new int[selecoes.getSELECOES()][selecoes.getJOGADORES()]);
                    response = "Album carregado com sucesso";
                    code = 200;
                }
            } catch (NullPointerException e) {
                response = "Conta não incluida";
            } catch (FileNotFoundException e) {
                response = "Conta não existe";
            } catch (Exception e) {
                System.err.println(e);
            } finally {
                exchange.sendResponseHeaders(code, response.getBytes(StandardCharsets.UTF_8).length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }

    static class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "API não encontrada";

            exchange.sendResponseHeaders(404, response.getBytes(StandardCharsets.UTF_8).length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
}