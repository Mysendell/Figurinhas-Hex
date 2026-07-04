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

    public static void main(String[] args) throws IOException {
        startServer();
    }

    private static void startServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8989), 0);

        server.createContext("/api", new RootHandler());
        server.createContext("/api/carregar", new carregarAlbumHandler());
        server.createContext("/api/sair", new ServerMain.SairHandler());

        server.setExecutor(null);

        server.start();
        System.out.println("Server started");
    }

    static class SairHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "";
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

            try {
                JsonObject bodyJson = JsonParser.parseString(body).getAsJsonObject();
                String nomeArquivo = bodyJson.get("nome").getAsString().trim().toLowerCase();
                if (nomeArquivo.isEmpty()) {
                    response = "Conta não pode ser vazia";
                    exchange.sendResponseHeaders(422, response.getBytes(StandardCharsets.UTF_8).length);
                } else if (albums.containsKey(nomeArquivo)) {
                    albums.remove(nomeArquivo);
                    response = "Conta descarregada";
                    exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
                } else {
                    response = "Conta não carregada";
                    exchange.sendResponseHeaders(401, response.getBytes(StandardCharsets.UTF_8).length);
                }
            } catch (NullPointerException e) {
                response = "Conta não incluida";
                exchange.sendResponseHeaders(400, response.getBytes(StandardCharsets.UTF_8).length);
            } catch (FileNotFoundException e) {
                response = "Conta não existe";
                exchange.sendResponseHeaders(400, response.getBytes(StandardCharsets.UTF_8).length);
            } catch (Exception e) {
                System.err.println(e);
            } finally {
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }

    static class carregarAlbumHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "";
            String query = exchange.getRequestURI().getQuery();
            String nomeArquivo = "";
            Map<String, String> queryMap = queryToMap(query);

            try {
                nomeArquivo = queryMap.get("nome").trim().toLowerCase();
                if (nomeArquivo.isEmpty()) {
                    response = "Conta não pode ser vazia";
                    exchange.sendResponseHeaders(422, response.getBytes(StandardCharsets.UTF_8).length);
                } else if (albums.containsKey(nomeArquivo)) {
                    response = "Esse conta já está em uso";
                    exchange.sendResponseHeaders(401, response.getBytes(StandardCharsets.UTF_8).length);
                } else {
                    Album album = new Album(nomeArquivo, selecoes.getSELECOES(), selecoes.getJOGADORES());
                    albums.put(nomeArquivo, album);
                    response = "Album carregado com sucesso";
                    exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
                }
            } catch (NullPointerException e) {
                response = "Conta não incluida";
                exchange.sendResponseHeaders(400, response.getBytes(StandardCharsets.UTF_8).length);
            } catch (FileNotFoundException e) {
                response = "Conta não existe";
                exchange.sendResponseHeaders(400, response.getBytes(StandardCharsets.UTF_8).length);
            } catch (Exception e) {
                System.err.println(e);
            } finally {
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }

    static class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "Hello World from Java HTTP Server!";

            // Set the HTTP response status code and content length
            exchange.sendResponseHeaders(200, response.length());

            // Write the text response to the output stream
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    static public Map<String, String> queryToMap(String query) {
        if (query == null) {
            return null;
        }
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(
                        URLDecoder.decode(entry[0], StandardCharsets.UTF_8),
                        URLDecoder.decode(entry[1], StandardCharsets.UTF_8)
                );
            } else {
                result.put(
                        URLDecoder.decode(entry[0], StandardCharsets.UTF_8),
                        ""
                );
            }
        }

        return result;
    }
}