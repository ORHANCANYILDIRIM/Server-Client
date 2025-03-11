import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientApp extends JFrame {
    private JTextArea logArea; // Logları göstermek için JTextArea
    private Logger logger; // Logger sınıfı
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public ClientApp() {
        setTitle("İstemci Uygulaması");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Log alanı
        logArea = new JTextArea();
        logArea.setEditable(false);
        add(new JScrollPane(logArea), BorderLayout.CENTER);

        // Logger sınıfını başlat (loglar hem arayüzde hem de dosyada gözükecek)
        String logFilePath = "client_log.txt";
        logger = new Logger(logArea, logFilePath);

        // Üst panel (IP ve port girişi, bağlan butonu)
        JPanel topPanel = new JPanel();
        JTextField ipField = new JTextField(10);
        JTextField portField = new JTextField(5);
        JButton connectButton = new JButton("Bağlan");
        topPanel.add(new JLabel("IP:"));
        topPanel.add(ipField);
        topPanel.add(new JLabel("Port:"));
        topPanel.add(portField);
        topPanel.add(connectButton);
        add(topPanel, BorderLayout.NORTH);

        // Alt panel (mesaj gönderme)
        JPanel bottomPanel = new JPanel();
        JTextField messageField = new JTextField(20);
        JButton sendButton = new JButton("Gönder");
        bottomPanel.add(messageField);
        bottomPanel.add(sendButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Bağlan butonu işlevi
        connectButton.addActionListener(e -> {
            String ip = ipField.getText();
            int port = Integer.parseInt(portField.getText());
            connectToServer(ip, port);
        });

        // Gönder butonu işlevi
        sendButton.addActionListener(e -> {
            sendMessage(messageField.getText());
            messageField.setText("");
        });

        logger.log("İstemci uygulaması başlatıldı.");
    }

    private void connectToServer(String ip, int port) {
        try {
            socket = new Socket(ip, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            logger.log("Sunucuya bağlandı: " + ip + ":" + port);

            // Sunucudan gelen mesajları dinle
            new Thread(() -> {
                try {
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        logger.log("Sunucudan gelen mesaj: " + inputLine);
                    }
                } catch (IOException e) {
                    logger.log("Hata: " + e.getMessage());
                }
            }).start();
        } catch (IOException e) {
            logger.log("Hata: " + e.getMessage());
        }
    }

    private void sendMessage(String message) {
        if (out != null) {
            out.println(message);
            logger.log("Gönderilen mesaj: " + message);
        }
    }

    @Override
    public void dispose() {
        logger.close(); // Log dosyasını kapat
        super.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ClientApp().setVisible(true);
        });
    }
}