package communicaserver;
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
            System.out.println("Новый клиент в сети.");
            // Слушаем клиента
            while (true) {
                if (inMessage.hasNext()) {
                    //String[] mes = inMessage.nextLine().split("#");
                    System.out.println(inMessage.nextLine() + "");
                    
                    /*if (mes[0].equals("exit")) {
                        System.out.println(name + " покинул сеть.");
                        break;
                    } else {
                        switch(Integer.valueOf(mes[0])) {
                            case 101:
                                
                                break;
                            case 102:

                                break;
                        }
                    }*/
                }
                //Thread.sleep(100);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Неверный формат введённых значений");
        } finally {
            this.close();
        }
    }
    
    public void sendData(String msg) {
        try {
            outMessage.println(msg);
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
