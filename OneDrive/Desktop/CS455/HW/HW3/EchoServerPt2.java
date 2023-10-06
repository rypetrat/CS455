import java.io.*;
import java.net.*;

public class EchoServerPt2 {

    public static void main(String[] args) {
        //define proper usage for opening listening port
        if (args.length != 1) {
            System.err.println("Usage: java EchoServerPt2 <port number>");
            System.exit(1);
        }
        //set server Port Number
        int PORT = Integer.parseInt(args[0]);

        //create listening socket
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started. Waiting for a client to connect...");

            //accept client connection
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected: " + clientSocket.getInetAddress());

            //init reader and writer
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);

            //break up client setup message
            String smsg = reader.readLine();
            String[] stokens = smsg.split(" ");

            //if length of arguments not correct length then return connection error msg
            if (stokens.length != 5) {
                writer.println("404 ERROR: Invalid Connection Setup Message");
                reader.close();
                writer.close();
                clientSocket.close();
                System.exit(1);
            }
            //identify the messages phase and other tags
            String protcolPhase = stokens[0];
            String measurementType = stokens[1];
            int numProbes = Integer.parseInt(stokens[2]);
            int messageSize = Integer.parseInt(stokens[3]);
            
            //error check setup phase message
            boolean check = true;
            //if phase protocol s
            if (protcolPhase.equals("s") == true) {
                //if more than 10 probes
                if (numProbes >= 10) {
                    //if rtt selected
                    if (measurementType.equals("rtt")) {
                        //if valid message size for rtt
                        if (messageSize == 1 || messageSize == 100 || messageSize == 200 || messageSize == 400 || messageSize == 800 || messageSize == 1000) {
                            //send to client 200
                            writer.println("200 OK: Ready");
                        }
                        //else false
                        else {
                            check = false; }
                    }
                    //else if tput selected
                    else if (measurementType.equals("tput")) {
                        //if valid message size for rtt
                        if (messageSize == 1000 || messageSize == 2000 || messageSize == 4000 || messageSize == 8000 || messageSize == 16000 || messageSize == 32000) {
                            //send to client 200
                            writer.println("200 OK: Ready");
                        }
                        //else false
                        else {
                            check = false; }
                    }
                    //else false
                    else {
                        check = false; }
                }
                //else false
                else {
                    check = false; }
            }
            //else false
            else {
                check = false; }

            //if check changed to false send 404 to client
            if (check == false) {
                writer.println("404 ERROR: Invalid Connection Setup Message");
                reader.close();
                writer.close();
                clientSocket.close();
                System.exit(1);
            }

            //error check measurement phase messages
            int i = 0;
            while(i < numProbes) {
                //recieve & segment parts of m phase message
                String mmsg = reader.readLine();
                String[] mtokens = mmsg.split(" ");
                int seqNum = Integer.parseInt(mtokens[1]);

                //if m phase probe is valid
                if (mtokens[0].equals("m") == true && seqNum == i) {
                    //echo back message
                    System.out.println("Recieved packet " + "{" + mmsg + "}");
                    writer.println(mmsg);
                }
                else {
                    //else send client 404 & close connection
                    writer.println("404 ERROR: Invalid Measurement Message");
                    reader.close();
                    writer.close();
                    clientSocket.close();
                    System.exit(1);
                }
                //increment i
                i++;
            }

            //recieve & segment parts of t phase message
            String tmsg = reader.readLine();
            String[] ttokens = tmsg.split(" ");
            //error check termination phase message
            if (ttokens[0].equals("t")) {
                //if t message only contains the letter t
                if (ttokens.length == 1) {
                    //echo 200 & close connection
                    writer.println("200 OK: Closing Connection");
                    reader.close();
                    writer.close();
                    clientSocket.close();
                    System.exit(1);
                }
                //else send client 404 & close connection
                writer.println("404 ERROR: Invalid Connection Termination Message");
                reader.close();
                writer.close();
                clientSocket.close();
                System.exit(1);
            }
        //exception catching
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                + PORT + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}