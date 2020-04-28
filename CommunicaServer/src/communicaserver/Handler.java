package communicaserver;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import javax.swing.JOptionPane;

public class Handler implements Runnable {

    Server server;                  // экземпляр нашего сервера
    PrintWriter outMessage;         // исходящее сообщение
    Scanner inMessage;              // входящее собщение
    String name;
    int id;
    public Socket clientSocket = null;
    public int percent = 0;
    public int time = 0;

    public Handler(Socket socket, Server server, int id) {
        try {
            this.server = server;
            this.clientSocket = socket;
            this.outMessage = new PrintWriter(socket.getOutputStream());
            this.inMessage = new Scanner(socket.getInputStream());
            this.name = "[" + clientSocket.getLocalAddress() + "(" + id + ")]";
            this.id = id;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    @Override
    public void run() {
        try {
            System.out.println(name + " в сети.");
            // Слушаем клиента
            while (true) {
                if (inMessage.hasNext()) {
                    String[] msg = inMessage.nextLine().split("#");
                    
                    if (msg[0].equals("exit")) {
                        System.out.println(name + " покинул сеть.");
                        break;
                    } else {
                        switch(msg[0]) {
                            case "msg":
                                msg_handlers(msg[1], msg[2]);
                                break;
                            case "get":
                                get_handlers(msg[1], msg[2]);
                                break;
                            default:
                                System.out.println(name + ": " + msg[0] + msg[1] + msg[2]);
                                break;
                        }
                    }
                }
                //Thread.sleep(100);
            }
        } catch (NumberFormatException ex) {
            //JOptionPane.showMessageDialog(null, "Неверный формат введённых значений");
        } finally {
            this.close();
        }
    }
    
    void msg_handlers(String param, String data) {
        switch(param) {
            case "msg":
                
                break;
            case "get":
                
                break;
            default:
                System.out.println("Message eror from " + name + param + "|" + data);
                break;
        }
    }
    
    void get_handlers(String param, String data) {
        switch(param) {
            case "contacts":
                try {
                    FileInputStream fin = new FileInputStream("data/contacts.txt");
                    int size = fin.available();
                    if (size != 0) {
                        byte[] buffer = new byte[size];
                        fin.read(buffer, 0, size);
                        String contacts = new String(buffer, "Cp1251");
                        sendData("post", "contacts", contacts);
                    }
                } catch(IOException ex) {
                    
                }
                break;
            case "anything":
                
                break;
            default:
                System.out.println(name + ": message eror");
                break;
        }
    }
    
    public void sendData(String type, String param, String data) {
        try {
            String message = type + "#" + param + "#" + data;
            outMessage.println(message);
            outMessage.flush();            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    // клиент выходит из чата
    public void close() {
        for (int i = 0; i < server.clients.size(); i++) {
            if (server.clients.get(i).id == this.id) {
                server.removeClient(i);
                break;
            }
        }        
    }
}
