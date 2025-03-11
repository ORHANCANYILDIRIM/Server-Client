import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    private JTextArea logArea; // Logları göstermek için JTextArea
    private FileWriter logFileWriter; // Logları dosyaya yazmak için FileWriter

    public Logger(JTextArea logArea, String logFilePath) {
        this.logArea = logArea;
        try {
            // Log dosyasını aç (append modunda)
            this.logFileWriter = new FileWriter(logFilePath, true);
        } catch (IOException e) {
            System.err.println("Log dosyası açılamadı: " + e.getMessage());
        }
    }

    public void log(String message) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = sdf.format(new Date());
        String logMessage = "[" + timestamp + "] " + message;

        // Logu arayüzde göster
        SwingUtilities.invokeLater(() -> {
            logArea.append(logMessage + "\n");
        });

        // Logu dosyaya yaz
        if (logFileWriter != null) {
            try {
                logFileWriter.write(logMessage + "\n");
                logFileWriter.flush(); // Dosyaya hemen yaz
            } catch (IOException e) {
                System.err.println("Log dosyasına yazılamadı: " + e.getMessage());
            }
        }
    }

    public void close() {
        if (logFileWriter != null) {
            try {
                logFileWriter.close();
            } catch (IOException e) {
                System.err.println("Log dosyası kapatılamadı: " + e.getMessage());
            }
        }
    }
}