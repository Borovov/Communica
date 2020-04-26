package communicaserver;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

public class Server extends JFrame {

    static final int PORT = 665;
    ArrayList<Handler> clients = new ArrayList<Handler>();    // список клиентов
    JTable table;
    DefaultTableModel model;
    
    public Server() {
        Socket clientSocket = null;
        ServerSocket serverSocket = null;
        
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Сервер запущен!");
            
            createGUI();
            setVisible(true);
            tableListener();
        
            while (true) {
                clientSocket = serverSocket.accept();       // ждём подключений от сервера                
                Handler clientHandler = new Handler(clientSocket, this, clients.size());// this - это наш сервер
                clients.add(clientHandler);
                
                new Thread(clientHandler).start();     // каждое подключение клиента обрабатываем в новом потоке
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                clientSocket.close();
                System.out.println("Сервер остановлен");
                serverSocket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    void createGUI() {
        // Задаём настройки элементов на форме
        setBounds(100, 50, 300, 400);
        setTitle("Server");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        // верхняя панель
        JPanel topPanel = new JPanel(new GridLayout(1,5));
        add(topPanel, BorderLayout.NORTH);
        
        // центральная панель
        String[] columnNames = {"IP", "Progress", "Time"};
        model = new DefaultTableModel(0, columnNames.length);
        model.setColumnIdentifiers(columnNames);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);
        
        // нижняя панель
        JPanel bottomPanel = new JPanel(new GridLayout(3,2));
        add(bottomPanel, BorderLayout.SOUTH);
    }

    void tableListener() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int count = 0;
                while (true) {
                    try {
                        if (count != clients.size()) {
                            count = clients.size();
                            model.setRowCount(0);
                            
                            for (int i = 0; i < clients.size(); i++) {
                                model.addRow(new Object[] {
                                clients.get(i).clientSocket.getLocalAddress(), 
                                clients.get(i).percent, 
                                clients.get(i).time});
                            }
                        }
                        model.fireTableDataChanged();
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }).start();
    }
    
    // удаляем клиента из коллекции при выходе из чата
    public void removeClient(int id) {
        clients.remove(id);
    }
}
