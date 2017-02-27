package sample;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

class clientThread extends Thread {

    private DataInputStream is = null;
    private PrintStream os = null;
    private Socket clientSocket = null;
    private final clientThread[] threads;
    private int maxClientsCount;

    public clientThread(Socket clientSocket, clientThread[] threads) {
        this.clientSocket = clientSocket;
        this.threads = threads;
        maxClientsCount = threads.length;
    }

    public void run() {
        int maxClientsCount = this.maxClientsCount;
        clientThread[] threads = this.threads;

        try {
      /*
       * Create input and output streams for this client.
       */
            is = new DataInputStream(clientSocket.getInputStream());
            os = new PrintStream(clientSocket.getOutputStream());



            String name = "morethan12letters";
            while (name.length()>12 || valid(name) == false) {
                os.println("Enter your name - max 12 letters! (Valid Input: A-Z, 0-9, comma, underscore): ");
                name = is.readLine().trim();
                
                
               /* if (name.startsWith("JOIN"))  {
                    System.out.println("");
                }
               String JOIN = "JOIN {" + name + "}, {" +
                       clientSocket.getInetAddress() + "}:{" + clientSocket.getPort()+ "}";
               System.out.println(JOIN);
               */
            }
            os.println("Hello " + name
                    + " to our chat room.\nTo leave enter /quit in a new line");

            showHeartbeat showHeartbeat = new showHeartbeat();
            showHeartbeat.name = name;

            Timer timer = new Timer();
            timer.schedule(showHeartbeat, 0, 60000);

            Client client = new Client();
            client.setUsername(name);
            client.setPort(clientSocket.getPort());
            client.setClientAddress(clientSocket.getInetAddress());

            TCPChatServer.addToClientList(client);

            System.out.println("New User :" + name + " port: " + clientSocket.getPort() + " IP: "
                    + clientSocket.getInetAddress() +
                    " has connected to the server");
            // Add også bruger til clientList
            // og tjek i while loop at der kun bruges valid input!

            // Print to all clients that a new user has joined
            for (int i = 0; i < maxClientsCount; i++) {
                if (threads[i] != null && threads[i] != this) {
                    threads[i].os.println("*** New User " + name
                            + " entered the chat room! ***");

                }
            }

            while (true) {
                String line = is.readLine();
                if (line.startsWith("/quit")) {
                    break;
                }
                for (int i = 0; i < maxClientsCount; i++) {
                    if (threads[i] != null) {
                        threads[i].os.println("[" + name + "]: " + line);
                    }
                }
                System.out.println("Message received from User: [" + name + "] port: " + clientSocket.getPort() + " IP: "
                        + clientSocket.getInetAddress());
            }

            for (int i = 0; i < maxClientsCount; i++) {
                if (threads[i] != null && threads[i] != this) {
                    threads[i].os.println(" The user [" + name
                            + "] has left the chat room.");
                }
            }
            TCPChatServer.removeFromClientList(client,name);
            System.out.println("User: [" + name + "] left the chatroom.");
            os.println("You have left the chatroom.");
            os.println("Server says Bye");
            timer.cancel();

      /*
       * Clean up. Set the current thread variable to null so that a new client
       * could be accepted by the server.
       */
            for (int i = 0; i < maxClientsCount; i++) {
                if (threads[i] == this) {
                    threads[i] = null;
                }
            }

      /*
       * Close the output stream, close the input stream, close the socket.
       */
            is.close();
            os.close();
            clientSocket.close();
        } catch (IOException e) {
        }



    }

    //regex checker ved hjælp af en Matcher om parametret kun indeholder de specificerede tegn
    public static boolean valid (String name){
        if (name.matches("[0-9a-zA-Z_-]+")){
            return true;
        }
        else
            return false;
    }

}

class showHeartbeat extends TimerTask {

    String name;
    int count = 0;

    public void run() {

        if (count == 0)
            System.out.println(name + " is now alive.");

        if (count == 1)
            System.out.println(name + " has been alive for " + count + " minute.");

        if (count > 1)
            System.out.println(name + " has been alive for " + count + " minutes.");

        count++;
    }


}
