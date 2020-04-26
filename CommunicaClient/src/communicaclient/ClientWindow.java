package communicaclient;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.Vector;
import javax.swing.*;

public class ClientWindow extends JFrame {
    
    static final String SERVER_HOST = "192.168.56.1";
    static final int SERVER_PORT = 665;
    int GAP = 10;    
    Socket clientSocket = null;
    Scanner inMessage;
    PrintWriter outMessage;
    
    JTextField tPort, tHost, tLog, tPas, tMsg;
    JLabel l;
    JButton bCon, bLog, bAdd, bSend;    
    
    public String getClientName() {
        return this.SERVER_HOST;
    }

    // конструктор
    public ClientWindow() {
        onStart();
        onCreateView();
        createListeners();
        setVisible(true);
    }
    
    void onStart() {
        try {
            if (!Files.exists(Paths.get("data"))) {
                File dir = new File("data");
                dir.mkdir();
                System.out.println("Директория data создана");
            }
            
            if (!Files.exists(Paths.get("data/contacts.txt"))) {
                new File("data/contacts.txt").createNewFile();
                System.out.println("Файл contacts.txt создан");  
            }
            
            if (!Files.exists(Paths.get("data/settings.txt"))) {
                new File("data/settings.txt").createNewFile();
                System.out.println("Файл settings.txt создан");
            }
        } catch(IOException ex) {
                
        }
    }
    
    void onCreateView() {
        // Задаём настройки элементов на форме
        setBounds(600, 150, 600, 500);
        setTitle("Client");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //javax.swing.border.Border border = BorderFactory.createLineBorder(Color.GRAY, 5);
        //tPort.setBorder(BorderFactory.createEmptyBorder(GAP, GAP, GAP, GAP));
        //TitledBorder title = BorderFactory.createTitledBorder(10, "Title");
        
        // верхняя панель
        GridLayout grid = new GridLayout(2,3);
        grid.setHgap(10);
        grid.setVgap(5);
        JPanel topPanel = new JPanel(grid);
        topPanel.setBorder(BorderFactory.createTitledBorder("Основная панель"));
        add(topPanel, BorderLayout.NORTH);
        tPort = new JTextField(SERVER_PORT + "");
        topPanel.add(tPort);
        tHost = new JTextField(SERVER_HOST);
        topPanel.add(tHost);
        bCon = new JButton("Подключиться");
        topPanel.add(bCon);
        tLog = new JTextField("login");
        topPanel.add(tLog);
        tPas = new JTextField("password");
        topPanel.add(tPas);
        bLog = new JButton("Войти");
        topPanel.add(bLog);
        
        // левая панель
        JPanel leftPanel = new JPanel(new BorderLayout());        
        leftPanel.setBorder(BorderFactory.createTitledBorder("Контакты"));
        add(leftPanel, BorderLayout.WEST);
        bAdd = new JButton("Добавить новый контакт");
        leftPanel.add(bAdd, BorderLayout.NORTH);
        loadContacts(leftPanel);
        
        // центральная панель
        JPanel centralPanel = new JPanel(new BorderLayout());
        centralPanel.setBorder(BorderFactory.createTitledBorder("Диалог"));
        add(centralPanel, BorderLayout.CENTER);
        // вложенная панель
        JPanel msgPanel = new JPanel(new BorderLayout());
        bSend = new JButton("Отправить");
        bSend.setMargin(new Insets(5, 5, 5, 5));
        msgPanel.add(bSend, BorderLayout.EAST);
        tMsg = new JTextField("Введите сообщение");
        tMsg.setMargin(new Insets(5, 5, 5, 5));
        msgPanel.add(tMsg, BorderLayout.CENTER);        
        centralPanel.add(msgPanel, BorderLayout.SOUTH);
    }
    
    void loadContacts(JPanel panel) {
        try {
            FileInputStream fin = new FileInputStream("data/contacts.txt");
            int size = fin.available();
            byte[] buffer = new byte[size];
            fin.read(buffer, 0, size);
            String[] contacts = new String(buffer, "Cp1251").split(",");
            
            Vector<String> big = new Vector<String>();
            for (int i=0; i < contacts.length; i++) {
                big.add(contacts[i]);
                System.out.println(contacts[i]);
            }
            JList<String> listContacts = new JList<String>(big);

            panel.add(new JScrollPane(listContacts), BorderLayout.CENTER);
        } catch(IOException ex) {
                
        }
    }
    
    void createListeners() {        
        bCon.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            clientSocket = new Socket(tHost.getText(), Integer.valueOf(tPort.getText()));
                            inMessage = new Scanner(clientSocket.getInputStream());
                            outMessage = new PrintWriter(clientSocket.getOutputStream());
                            startListenServer();
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(null, e.getMessage());
                        }
                    }
                }).start();
            }
        });
        
        bSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendData("msg", "adr", tMsg.getText());
            }
        });
        
        bAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendData("get", "contacts", "");
            }
        });
        
        // закрытие окна клиентского приложения
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                try {
                    // если подключение существует
                    if (clientSocket != null) {
                        outMessage.println("exit");                    
                        outMessage.flush();
                        outMessage.close();
                        inMessage.close();
                        clientSocket.close();
                    }
                } catch (IOException exc) {

                }
            }
        });
    }
    
    // слушаем сервер
    void startListenServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("Соединение с сервером установлено.");
                    while (true) {
                        // если есть входящее сообщение
                        if (inMessage.hasNext()) {
                            String message = inMessage.nextLine();
                            System.out.println("Сообщение сервера: " + message);
                        }
                    }
                } catch (IllegalStateException ex) {
                    ex.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }

    public void sendData(String type, String param, String data) {
        try {
            String message = type + "#" + param + "#" + data;
            outMessage.println(message);
            outMessage.flush();            
        } catch (NumberFormatException exx) {
            //JOptionPane.showMessageDialog(null, "");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    @Override
    public synchronized void addWindowListener(WindowListener wl) {
        super.addWindowListener(wl); //To change body of generated methods, choose Tools | Templates.
    }

}

