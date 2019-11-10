package servermultithread;

import java.net.*;
import java.net.ServerSocket;
import java.io.*;

public final class App {

    protected int serverPort = 5000;
    protected ServerSocket serverSocket = null;

    public static void main(String[] args) throws Exception {
        // Instanciando um objeto servidor
        App server = new App();
        System.out.println("Servidor executando na porta " + Integer.toString(server.serverPort) + " ...");

        // Abrindo o socket servidor na PORTA 5000
        try {
            server.serverSocket = new ServerSocket(server.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possivel abrir o processo na porta 5000", e);
        }

        // Recebendo requests do cliente
        while (true) {
            Socket clientSocket = null;
            try {
                clientSocket = server.serverSocket.accept();
                HttpRequest request = new HttpRequest(clientSocket);
                Thread thread = new Thread(request);
                thread.start();
            } catch (IOException e) {
                throw new RuntimeException("Erro", e);
            }

        }
    }
}

final class HttpRequest implements Runnable {
    /*
     * final static String CRLF = “\r\n”; Socket socket;
     */
    String CRLF = "";
    protected Socket clientSocket = null;

    // Construtor
    public HttpRequest(Socket clientSocket) throws Exception {
        this.clientSocket = clientSocket;
    }

    // Implemente o método run() da interface Runnable.
    public void run() {
        try {
            // processRequest();
            processRequest();
        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    private void processRequest() throws Exception {
        InputStream input = clientSocket.getInputStream();
        OutputStream output = clientSocket.getOutputStream();
        long time = System.currentTimeMillis();
        output.write(("HTTP/1.1 200 OK\n\nRequest: " + time + "").getBytes());
        output.close();
        // System.out.println(input.read());
        input.close();
        System.out.println("Request processed: " + time);
        clientSocket.close();
        System.out.println("OI");

    }
}
