package communicaclient;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class ContactsDialog extends javax.swing.JDialog {
    private boolean result;
    private String selected = null;
    private String data;
        
    public ContactsDialog (java.awt.Frame parent, String data) {
        super(parent, true);
        this.data = data;
        setSize(180,250);
        this.setLocation(parent.getLocation().x + 100, parent.getLocation().y + 100);
        createGUI();
    }
    
    private void OK() {
        this.result = true;
        this.dispose();
    }
 
    private void Cancel(java.awt.event.MouseEvent evt) {
            this.result = false;
            this.dispose();
    }
    
    public String execute() {
        this.setVisible(true);
        if (selected == null)
            selected = "0";

        return selected;
    }
    
    void createGUI() {        
        setTitle("Добавление клиентов");
        JPanel panel = new JPanel(new BorderLayout());
        
        String[] contacts = data.split(",");
        Vector<String> list = new Vector<String>();
        for (int i=0; i < contacts.length; i++) {
            list.add(contacts[i]);
        }
        JList<String> listContacts = new JList<String>(list);
        panel.add(listContacts, BorderLayout.CENTER);
        
        JButton bOK = new JButton("Сохранить");
        bOK.setMargin(new Insets(5, 5, 5, 5));
        bOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OK();
                selected = listContacts.getSelectedValue();
                try {
                    FileInputStream fin = new FileInputStream("data/contacts.txt");
                    int size = fin.available();
                    fin.close();
                    
                    FileOutputStream fos = new FileOutputStream("data/contacts.txt");
                    if (size > 0) {
                        fos.();
                        fos.close();
                    } else if (size == 0) {                        
                        fos.write(selected.getBytes());
                        fos.close();
                    }
                } catch (IOException ex) {
                    System.err.print("Ошибка записи нового контакта в файл");
                }
            }
        });
        panel.add(bOK, BorderLayout.SOUTH);
        add(panel);
    }
}
