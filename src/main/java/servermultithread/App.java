package servermultithread;

/* 
* Trabalho #1 Redes de Computadores
* Guilherme Alves da Silva - 11511ECP020
*/

import java.net.*;
import java.net.ServerSocket;
import java.util.StringTokenizer;
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
    final static String CRLF = "\r\n";
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
        long time = System.currentTimeMillis();
        InputStream is = clientSocket.getInputStream();
        OutputStream os = clientSocket.getOutputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr, 2);
        String requestLine = br.readLine();
        System.out.println(requestLine);

        String headerLine = null;
        while ((headerLine = br.readLine()).length() != 0) {
            System.out.println(headerLine);
        }

        os.write(("HTTP/1.1 200 OK\n\nRequest: " + time + "").getBytes());
        // System.out.println("Request processed: " + time);

        StringTokenizer tokens = new StringTokenizer(requestLine);
        tokens.nextToken();
        String filename = tokens.nextToken();
        filename = "." + filename;
        System.out.println(filename);

        FileInputStream fis = null;
        Boolean fileExists = true;
        try {
            fis = new FileInputStream(filename);
        } catch (FileNotFoundException e) {
            fileExists = false;
        }

        String statusLine = null;
        String contentTypeLine = null;
        String entityBody = null;
        statusLine = "";
        contentTypeLine = "Content-type: " + contentType(filename) + CRLF;
        statusLine = "";
        contentTypeLine = "";
        entityBody = "<HTML><HEAD><TITLE>Not Found</TITLE></HEAD><BODY>Not Found</BODY></HTML>";

        os.write((statusLine).getBytes());
        os.write((contentTypeLine).getBytes());
        os.write(CRLF.getBytes());
        if (fileExists) {
            sendBytes(fis, os);
            fis.close();
        } else {
            os.write((entityBody).getBytes());
        }
        os.close();
        br.close();
        // System.out.println(input.read());
        is.close();
        clientSocket.close();
    }

    private static String contentType(String filename) {
        if (filename.endsWith(".htm") || filename.endsWith(".html")) {
            return "text/html";
        }
        if (filename.endsWith(".gif")) {
            return "image/gif";
        }
        if (filename.endsWith(".jpeg") || filename.endsWith(".jpg")) {
            return "image/jpeg";
        }
        return "application/octet-stream";
    }

    private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception {
        byte[] buffer = new byte[1024];
        int bytes = 0;
        while ((bytes = fis.read(buffer)) != -1) {
            os.write(buffer, 0, bytes);
        }

    }
}
