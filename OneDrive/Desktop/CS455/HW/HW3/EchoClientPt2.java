import java.io.*;
import java.net.*;

public class EchoClientPt2 {
    public static void main(String[] args) {
        //define proper usage for server connection phase
        if (args.length != 2) {
            System.out.println("Usage: java EchoClientPt2 <server_hostname> <server_port>");
            return;
        }

        //get server Hostname and Port Number
        String serverHostname = args[0];
        int serverPort = Integer.parseInt(args[1]);

        //try to connect to socket
        try (Socket socket = new Socket(serverHostname, serverPort)) {

            //init writer, reader, and server reader
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //setup phase information
            System.out.print("Enter Measurement Type, Number of Probes, Message Size, and Server Delay: ");
            String message = reader.readLine();

            //send to server to check for entry errors
            writer.println("s" + " " + message);
            
            String msg = serverReader.readLine();
            //if server return 200 OK print ok message
            if(msg.equals("200 OK: Ready")) {
                System.out.println("--------------------");
                System.out.println(msg);
                System.out.println("--------------------");
            }
            else { 
                //otherwise 404 and exit
                System.out.println("--------------------");
                System.out.println(msg);
                System.out.println("--------------------");
                System.exit(1);
            }
            
            //split message up to get information
            String[] tokens = message.split(" ");
            //take input variables
            String measurementType = tokens[0];
            int numProbes = Integer.parseInt(tokens[1]);
            int messageSize = Integer.parseInt(tokens[2]);
            int serverDelay = Integer.parseInt(tokens[3]);
            
            //generate payload
            String payload = "";
            for (int x = 0; x < messageSize; x++) {
                payload += "a";
             }

            //collect rtt's for each probe
            long Trtt = 0;
            //sending probes
            int i = 0;
            while(i < numProbes) {
                
                //start timer
                long startTime = System.currentTimeMillis();
                
                //implement optional server delay
                try { Thread.sleep(serverDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();}

                //generate & send probe
                writer.println("m" + " " + i + " " + payload);

                //recieve probe
                String mmsg = serverReader.readLine();
                //split probe
                String[] mTok = mmsg.split(" ");

                //end timer
                long endTime = System.currentTimeMillis();

                //check server response
                if (mmsg.equals("404 ERROR: Invalid Measurement Message")) {
                    System.out.println(mmsg);
                    System.out.println("--------------------");
                    System.exit(1);
                }
                else {
                    //calculate the rtt and add to total rtt
                    long rtt = (endTime - startTime);
                    Trtt += rtt;
                    //print server response
                    System.out.println("Server Echo: " + "{" + mTok[0] + " " + mTok[1] + " |" + " Payload: " + messageSize + " a's"+ "}");
                    System.out.println("Round trip time: " + rtt + "ms");
                    System.out.println("--------------------");
                }
                //increment i
                i++;
            } 

            //rtt or tput calculations
            if (measurementType.equals("rtt")) {
                System.out.print("Mean round trip time for this connection: ");
                System.out.println(Trtt / numProbes + "ms");
                System.out.println("--------------------");
            }
            else {
                System.out.print("Throughput for this connection: ");
                System.out.println(((long)messageSize)/(Trtt / numProbes) + "MBps");
                System.out.println("--------------------");
            }

            //send closing connection message
            writer.println("t");
            
            String tmsg = serverReader.readLine();
            //error checking closing connection
            if(tmsg.equals("200 OK: Closing Connection")) {
                //print 200 msg from server
                System.out.println(tmsg);
                System.out.println("--------------------");
                //close writer, reader, and server reader
                writer.close();
                reader.close();
                serverReader.close();
                socket.close();
            }
            else { 
                //otherwise print 404 and exit
                System.out.println(tmsg);
                System.out.println("--------------------");
                System.exit(1);
            }
        } 
        //exception catching
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