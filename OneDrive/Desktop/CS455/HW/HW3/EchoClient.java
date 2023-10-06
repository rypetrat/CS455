import java.io.*;
import java.net.*;

public class EchoClient {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java EchoClient <server_hostname> <server_port>");
            return;
        }

        String serverHostname = args[0];
        int serverPort = Integer.parseInt(args[1]);

        try (Socket socket = new Socket(serverHostname, serverPort)) {
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.print("Enter a message: ");
            String message = reader.readLine();

            writer.println(message);

            String serverResponse = serverReader.readLine();
            System.out.println("Server response: " + serverResponse);
        } 
        catch (UnknownHostException e) {
            System.err.println("Don't know about host " + serverHostname);
            System.exit(1);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.err.println("Couldn't get I/O for the connection to " +
                serverHostname);
            System.exit(1);
        } 
    }
}

