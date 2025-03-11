import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerApp extends JFrame {
    private JTextArea logArea; // Logları göstermek için JTextArea
    private Logger logger; // Logger sınıfı
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public ServerApp() {
        setTitle("Sunucu Uygulaması");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Log alanı
        logArea = new JTextArea();
        logArea.setEditable(false);
        add(new JScrollPane(logArea), BorderLayout.CENTER);

        // Logger sınıfını başlat (loglar hem arayüzde hem de dosyada gözük
        String logFilePath = "server_log.txt";
        logger = new Logger(logArea, logFilePath);

        // Üst panel (port girişi ve başlat butonu)
        JPanel topPanel = new JPanel();
        JTextField portField = new JTextField(10);
        JButton startButton = new JButton("Başlat");
        topPanel.add(new JLabel("Port:"));
        topPanel.add(portField);
        topPanel.add(startButton);
        add(topPanel, BorderLayout.NORTH);

        // Başlat butonu işlevi
        startButton.addActionListener(e -> {
            int port = Integer.parseInt(portField.getText());
            startServer(port);
        });

        logger.log("Sunucu uygulaması başlatıldı.");
    }

    private void startServer(int port) {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(port);
                logger.log("Sunucu " + port + " portunda başlatıldı.");

                while (true) {
                    // Önceki bağlantıyı kapat
                    if (clientSocket != null && !clientSocket.isClosed()) {
                        clientSocket.close();
                        logger.log("Önceki bağlantı kapatıldı.");
                    }

                    // Yeni bağlantıyı kabul et
                    clientSocket = serverSocket.accept();
                    logger.log("Yeni bağlantı: " + clientSocket.getInetAddress());

                    // Giriş/çıkış akışlarını başlat
                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                    // İstemciden gelen mesajları dinle
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        logger.log("Alınan mesaj: " + inputLine);
                        out.println("Sunucu: " + inputLine);
                    }

                    // Bağlantı kapatıldığında logla
                    logger.log("Bağlantı kapatıldı: " + clientSocket.getInetAddress());
                }
            } catch (IOException e) {
                logger.log("Hata: " + e.getMessage());
            }
        }).start();
    }

    @Override
    public void dispose() {
        logger.close(); // Log dosyasını kapat
        super.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ServerApp().setVisible(true);
        });
    }
}