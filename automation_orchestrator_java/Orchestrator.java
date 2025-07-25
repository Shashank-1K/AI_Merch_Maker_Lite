// automation_orchestrator_java/Orchestrator.java
import javax.swing.*;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Paths;
import java.util.List;
// import java.util.concurrent.ExecutionException;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject;
import org.json.JSONArray;

public class Orchestrator extends JFrame {
    private final JTextField themeField;
    private final JButton generateButton;
    private final JTextPane logPane;
    private final JLabel imageLabel;
    private final JTabbedPane mainTabs;
    private final JTextField titleField;
    private final JTextArea descriptionArea;
    private final JTextArea tagsArea;

    public Orchestrator() {
        setTitle("AI Merch Maker - Final");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        themeField = new JTextField("A majestic eagle soaring over a mountain range, synthwave style");
        generateButton = new JButton("Generate & Publish");
        topPanel.add(new JLabel("Enter Theme:"), BorderLayout.WEST);
        topPanel.add(themeField, BorderLayout.CENTER);
        topPanel.add(generateButton, BorderLayout.EAST);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainTabs = new JTabbedPane();
        logPane = new JTextPane();
        logPane.setContentType("text/html");
        logPane.setEditable(false);
        StyleSheet styleSheet = ((HTMLDocument) logPane.getDocument()).getStyleSheet();
        styleSheet.addRule("body { font-family: Segoe UI, sans-serif; margin: 5px; } h3 { margin-top: 10px; } p { margin: 0; font-size: 0.9em; }");
        styleSheet.addRule(".success { color: #198754; } .error { color: #dc3545; } .info { color: #6c757d; } .detail { color: #0d6efd; font-weight: bold;}");
        imageLabel = new JLabel("Mockup will appear here", SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(400, 400));
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(logPane), imageLabel);
        splitPane.setDividerLocation(700);
        mainTabs.addTab("Live Log & Mockup", splitPane);
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        detailsPanel.add(new JLabel("Product Title:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        titleField = new JTextField();
        titleField.setEditable(false);
        detailsPanel.add(titleField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; gbc.anchor = GridBagConstraints.NORTH;
        detailsPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 0.4;
        descriptionArea = new JTextArea(5, 30);
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        detailsPanel.add(new JScrollPane(descriptionArea), gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0; gbc.anchor = GridBagConstraints.NORTH;
        detailsPanel.add(new JLabel("Tags:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 0.6;
        tagsArea = new JTextArea(4, 30);
        tagsArea.setEditable(false);
        tagsArea.setLineWrap(true);
        tagsArea.setWrapStyleWord(true);
        detailsPanel.add(new JScrollPane(tagsArea), gbc);
        mainTabs.addTab("Product Details", detailsPanel);
        mainPanel.add(mainTabs, BorderLayout.CENTER);
        add(mainPanel);
        generateButton.addActionListener(this::startPipeline);
    }

    // ... (logHtml, displayImage, and startPipeline methods)
    private void logHtml(String html) {
        SwingUtilities.invokeLater(() -> {
            try {
                HTMLDocument doc = (HTMLDocument) logPane.getDocument();
                ((HTMLEditorKit) logPane.getEditorKit()).insertHTML(doc, doc.getLength(), html, 0, 0, null);
                logPane.setCaretPosition(doc.getLength());
            } catch (Exception e) { e.printStackTrace(); }
        });
    }
    private void displayImage(String imagePath) {
        try {
            String cleanPath = imagePath.replace("\\\\", "\\");
            BufferedImage bufferedImage = ImageIO.read(new File(cleanPath));
            if (bufferedImage == null) throw new IOException("ImageIO could not read the file.");
            Image scaledImage = bufferedImage.getScaledInstance(380, 380, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaledImage));
            imageLabel.setText(null);
        } catch (IOException e) {
            imageLabel.setText("Failed to load image");
            logHtml("<p class='error'>Error loading image: " + e.getMessage() + "</p>");
        }
    }
    private void startPipeline(ActionEvent e) {
        generateButton.setEnabled(false);
        logPane.setText("<html><body></body></html>");
        imageLabel.setIcon(null);
        imageLabel.setText("Generating...");
        titleField.setText("");
        descriptionArea.setText("");
        tagsArea.setText("");
        mainTabs.setSelectedIndex(0);
        new PipelineWorker(themeField.getText()).execute();
    }
    
    private class PipelineWorker extends SwingWorker<String, String> {
        private final String theme;
        public PipelineWorker(String theme) { this.theme = theme; }


        @Override
        protected String doInBackground() throws Exception {
            publish("<h3>🚀 Pipeline Started</h3>");

            // --- Step 1: Python (NOW with robust JSON parsing) ---
            publish("<p class='info'>Running Python AI Content Generator...</p>");
            ProcessBuilder pythonPb = new ProcessBuilder("python", "../content_generator/main.py", theme);
            Process pythonProcess = pythonPb.start();
            String pythonOutput = new String(pythonProcess.getInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();
            if (pythonProcess.waitFor() != 0) throw new RuntimeException("Python script failed.");
            publish("<p class='success'>✔️ Content generation complete.</p>");

            // Use the JSON library to parse the output reliably
            JSONObject pythonJson = new JSONObject(pythonOutput);
            JSONObject details = pythonJson.getJSONObject("product_details");
            publish("details_title:" + details.getString("title"));
            publish("details_desc:" + details.getString("description"));
            JSONArray tagsArray = details.getJSONArray("tags");
            String tags = String.join(", ", tagsArray.toList().stream().map(Object::toString).toArray(String[]::new));
            publish("details_tags:" + tags);
            String artworkPath = pythonJson.getString("artwork_file");
            String absoluteArtworkPath = Paths.get(artworkPath).toAbsolutePath().toString();

            // --- Step 2: JavaScript ---
            publish("<h3>🎨 Mockup Creation</h3><p class='info'>Running JavaScript script...</p>");
            ProcessBuilder jsPb = new ProcessBuilder("node", "../mockup_visualizer/main.js", absoluteArtworkPath);
            Process jsProcess = jsPb.start();
            String mockupOutput = new String(jsProcess.getInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();
            if (jsProcess.waitFor() != 0) throw new RuntimeException("JavaScript script failed");
            publish("<p class='success'>✔️ Mockup created successfully.</p>");
            
            // Use the JSON library to get the mockup URL
            JSONObject mockupJson = new JSONObject(mockupOutput);
            publish("image:" + mockupJson.getString("mockup_url"));

            // --- Step 3: PHP ---
            publish("<h3>☁️ Publishing to Server</h3><p class='info'>Sending final data...</p>");
            String finalPayload = "{\"content_data\": " + pythonOutput + ", \"mockup_data\": " + mockupOutput + "}";
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://127.0.0.1:8000")).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(finalPayload)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 201) throw new RuntimeException("PHP server returned error: " + response.body());
            publish("<p class='success'>✔️ Data published.</p>");
            
            return response.body();
        }

        // ... (process and done methods )
        @Override
        protected void process(List<String> chunks) {
            for (String chunk : chunks) {
                if (chunk.startsWith("image:")) displayImage(chunk.substring(6));
                else if (chunk.startsWith("details_title:")) titleField.setText(chunk.substring(14));
                else if (chunk.startsWith("details_desc:")) descriptionArea.setText(chunk.substring(13));
                else if (chunk.startsWith("details_tags:")) tagsArea.setText(chunk.substring(13));
                else logHtml(chunk);
            }
        }
        @Override
        protected void done() {
            try {
                String finalResult = get();
                JSONObject serverResponse = new JSONObject(finalResult);
                String formattedResponse = "Status: " + serverResponse.getString("status") + 
                                           "<br>Message: " + serverResponse.getString("message") + 
                                           "<br>Product ID: " + serverResponse.getString("product_id");
                logHtml("<h3>🎉 PIPELINE COMPLETED SUCCESSFULLY!</h3><p class='detail'>Server Response:</p><p class='info'>" + formattedResponse + "</p>");
                // mainTabs.setSelectedIndex(1);
            } catch (Exception e) {
                String message = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
                logHtml("<h3>❌ PIPELINE FAILED!</h3><p class='error'>" + message + "</p>");
            }
            generateButton.setEnabled(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Orchestrator().setVisible(true));
    }
}