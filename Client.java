import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class Client extends JFrame {
    Socket socket;
    BufferedReader br;
    PrintWriter out;

    private JTextArea messagesArea;
    private JTextField inputField;

    public Client() {
        try {
            socket = new Socket("127.0.0.1", 7777);
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            createGUI();
            handleEvents();

            startReading();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createGUI() {
        this.setTitle("Client");
        this.setSize(400, 400);
        messagesArea = new JTextArea();
        messagesArea.setEditable(false);
        inputField = new JTextField();
        
        this.setLayout(new java.awt.BorderLayout());
        this.add(new JScrollPane(messagesArea), "Center");
        this.add(inputField, "South");

        this.setVisible(true);
    }

    private void handleEvents() {
        inputField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String message = inputField.getText();
                messagesArea.append("Me: " + message + "\n");
                out.println(message);
                inputField.setText("");
                if (message.equals("exit")) {
                    System.exit(0);
                }
            }
        });
    }

    public void startReading() {
        Runnable r = () -> {
            try {
                String msg;
                while ((msg = br.readLine()) != null) {
                    messagesArea.append("Server: " + msg + "\n");
                    if (msg.equals("exit")) {
                        socket.close();
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        new Thread(r).start();
    }

    public static void main(String[] args) {
        new Client();
    }
}
