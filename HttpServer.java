import java.io.*;
import java.net.*;
public class HttpServer {

    public static void main(String[] args) throws IOException {

        final String DELIMITER = "\r\n\r\n";
        final String NEW_LINE = "\r\n";
        final String MAIN_PATH = "simple-http-server/static";

        ServerSocket serverSocket = new ServerSocket(8088);
        System.out.println("Server started at http://localhost:8088");


        while (true) {
            Socket clientSocket = serverSocket.accept();
            StringBuilder messageBuilder = new StringBuilder();

            try(BufferedReader in = new BufferedReader(new
                    InputStreamReader(clientSocket.getInputStream()));
                BufferedWriter out = new BufferedWriter(new
                        OutputStreamWriter(clientSocket.getOutputStream()))) {

                // Чтение запроса
                String line = null;

                while ((line = in.readLine()) != null && !line.isEmpty()) {
                    messageBuilder.append(line);
                    System.out.println(line);
                }

                String[] parts = messageBuilder.toString().split(DELIMITER);
                String head = parts[0];

                String[] headers = head.split(NEW_LINE);
                String[] firstLine = headers[0].split(" ");

                String method = firstLine[0];
                String url =firstLine[1];

                Path path = Paths.get(MAIN_PATH + url+ ".html");

                StringBuilder result = new StringBuilder();
                String response = null;

                try (BufferedReader reader = Files.newBufferedReader(path)) {

                    String fileLine;
                    while ((fileLine = reader.readLine()) != null) {
                        result.append(fileLine);
                        System.out.println(fileLine);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                response = result.toString();


                if (response == null || response.equals("")){
                    response ="<h1>Page not find!</h1>";
                    out.write("HTTP/1.1 404 OK\r\n");
                    out.write("Content-Type: text/html; charset=UTF-8\r\n");
                    out.write("Content-Length: " + response.length() + "\r\n");
                    out.write("\r\n");
                    out.write(response);
                    out.flush();
                    clientSocket.close();
                }
                else {
                    out.write("HTTP/1.1 200 OK\r\n");
                    out.write("Content-Type: text/html; charset=UTF-8\r\n");
                    out.write("Content-Length: " + response.length() + "\r\n");
                    out.write("\r\n");
                    out.write(response);
                    out.flush();
                    clientSocket.close();
                }
            }
        }
    }
}