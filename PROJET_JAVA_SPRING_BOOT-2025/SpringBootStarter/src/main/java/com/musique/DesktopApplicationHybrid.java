package com.musique;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Hybrid Desktop Application - Best of both worlds
 * Combines desktop interface with full web browser functionality
 */
public class DesktopApplicationHybrid extends JFrame {
    
    private ConfigurableApplicationContext springContext;
    private JButton openBrowserButton;
    private JButton stopServerButton;
    private JButton restartServerButton;
    private JLabel statusLabel;
    private JTextArea logArea;
    private JProgressBar progressBar;
    private boolean serverRunning = false;
    
    public DesktopApplicationHybrid() {
        initializeUI();
        startSpringBoot();
    }
    
    private void initializeUI() {
        setTitle("Musique - Location et Vente d'Équipements Musicaux (Solution Hybride)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        // Créer le panneau principal avec onglets
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Onglet 1: Interface Web (avec bouton pour ouvrir le navigateur)
        JPanel webPanel = createWebPanel();
        tabbedPane.addTab("🌐 Application Web", webPanel);
        
        // Onglet 2: Logs du serveur
        JPanel logPanel = createLogPanel();
        tabbedPane.addTab("📋 Logs du Serveur", logPanel);
        
        // Onglet 3: Contrôles
        JPanel controlPanel = createControlPanel();
        tabbedPane.addTab("⚙️ Contrôles", controlPanel);
        
        add(tabbedPane);
        
        // Gérer la fermeture de la fenêtre
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stopSpringBoot();
                System.exit(0);
            }
        });
    }
    
    private JPanel createWebPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Interface Web - Solution Hybride"));
        
        // Panneau d'information
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(new Color(240, 248, 255));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Titre et description
        JLabel titleLabel = new JLabel("🎵 Musique - Application Web");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        titleLabel.setForeground(new Color(44, 62, 80));
        
        JTextArea descriptionArea = new JTextArea(8, 50);
        descriptionArea.setEditable(false);
        descriptionArea.setBackground(new Color(240, 248, 255));
        descriptionArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        descriptionArea.setText(
            "🌟 SOLUTION HYBRIDE - Meilleur des deux mondes\n\n" +
            "✅ Interface Desktop Native\n" +
            "   • Contrôles du serveur intégrés\n" +
            "   • Logs en temps réel\n" +
            "   • Gestion centralisée\n\n" +
            "✅ Navigateur Web Complet\n" +
            "   • Toutes les fonctionnalités web\n" +
            "   • JavaScript et CSS modernes\n" +
            "   • Formulaires et interactions avancées\n\n" +
            "🚀 Comment utiliser :\n" +
            "1. Cliquez sur 'Ouvrir l'Application Web'\n" +
            "2. Votre navigateur s'ouvre avec l'application complète\n" +
            "3. Utilisez cette fenêtre pour contrôler le serveur\n" +
            "4. Profitez de l'expérience desktop + web !"
        );
        
        // Boutons d'action
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        openBrowserButton = new JButton("🌐 Ouvrir l'Application Web Complète");
        openBrowserButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        openBrowserButton.setPreferredSize(new Dimension(300, 40));
        openBrowserButton.setEnabled(false);
        openBrowserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openWebApplication();
            }
        });
        
        JButton helpButton = new JButton("❓ Aide");
        helpButton.addActionListener(e -> showHelp());
        
        buttonPanel.add(openBrowserButton);
        buttonPanel.add(helpButton);
        
        // Statut du serveur
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(new Color(240, 248, 255));
        
        JLabel serverStatusLabel = new JLabel("Statut du serveur : ");
        serverStatusLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        
        JLabel statusIndicator = new JLabel("⏳ Démarrage...");
        statusIndicator.setForeground(Color.ORANGE);
        statusIndicator.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        
        statusPanel.add(serverStatusLabel);
        statusPanel.add(statusIndicator);
        
        // Mise à jour du statut
        Timer statusTimer = new Timer(1000, e -> {
            if (serverRunning) {
                statusIndicator.setText("✅ Actif - Prêt à utiliser");
                statusIndicator.setForeground(Color.GREEN);
            } else {
                statusIndicator.setText("❌ Arrêté");
                statusIndicator.setForeground(Color.RED);
            }
        });
        statusTimer.start();
        
        // Assemblage du panneau
        infoPanel.add(titleLabel, BorderLayout.NORTH);
        infoPanel.add(descriptionArea, BorderLayout.CENTER);
        infoPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        panel.add(infoPanel, BorderLayout.CENTER);
        panel.add(statusPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createLogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Logs du Serveur Spring Boot"));
        
        logArea = new JTextArea(20, 60);
        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        logArea.setBackground(Color.BLACK);
        logArea.setForeground(Color.GREEN);
        
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        // Boutons pour les logs
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton clearLogButton = new JButton("🗑️ Effacer les Logs");
        clearLogButton.addActionListener(e -> logArea.setText(""));
        
        JButton saveLogButton = new JButton("💾 Sauvegarder les Logs");
        saveLogButton.addActionListener(e -> saveLogs());
        
        JButton copyLogButton = new JButton("📋 Copier les Logs");
        copyLogButton.addActionListener(e -> copyLogs());
        
        buttonPanel.add(clearLogButton);
        buttonPanel.add(copyLogButton);
        buttonPanel.add(saveLogButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Contrôles du Serveur"));
        
        // Panneau de contrôle principal
        JPanel mainControl = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Statut du serveur
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        statusLabel = new JLabel("🔄 Démarrage du serveur Spring Boot...");
        statusLabel.setForeground(Color.BLUE);
        statusLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        mainControl.add(statusLabel, gbc);
        
        // Barre de progression
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setStringPainted(true);
        progressBar.setString("Démarrage en cours...");
        progressBar.setPreferredSize(new Dimension(400, 25));
        mainControl.add(progressBar, gbc);
        
        // Boutons de contrôle
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        openBrowserButton = new JButton("🌐 Ouvrir l'Application Web");
        openBrowserButton.setEnabled(false);
        openBrowserButton.setPreferredSize(new Dimension(200, 40));
        openBrowserButton.addActionListener(e -> openWebApplication());
        mainControl.add(openBrowserButton, gbc);
        
        gbc.gridx = 1; gbc.gridy = 2;
        stopServerButton = new JButton("⏹️ Arrêter le Serveur");
        stopServerButton.setEnabled(false);
        stopServerButton.setPreferredSize(new Dimension(200, 40));
        stopServerButton.addActionListener(e -> stopSpringBoot());
        mainControl.add(stopServerButton, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        restartServerButton = new JButton("🔄 Redémarrer le Serveur");
        restartServerButton.setEnabled(false);
        restartServerButton.setPreferredSize(new Dimension(200, 40));
        restartServerButton.addActionListener(e -> restartSpringBoot());
        mainControl.add(restartServerButton, gbc);
        
        // Informations système
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JPanel infoPanel = new JPanel(new GridLayout(0, 2));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Informations Système"));
        infoPanel.setPreferredSize(new Dimension(400, 120));
        
        infoPanel.add(new JLabel("Java Version: " + System.getProperty("java.version")));
        infoPanel.add(new JLabel("OS: " + System.getProperty("os.name")));
        infoPanel.add(new JLabel("Répertoire: " + System.getProperty("user.dir")));
        infoPanel.add(new JLabel("Port: 8080"));
        infoPanel.add(new JLabel("URL: http://localhost:8080"));
        infoPanel.add(new JLabel("Status: " + (serverRunning ? "Actif" : "Arrêté")));
        
        mainControl.add(infoPanel, gbc);
        
        panel.add(mainControl, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void openWebApplication() {
        try {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                desktop.browse(new URI("http://localhost:8080"));
                logMessage("🌐 Ouverture de l'application dans le navigateur...");
                logMessage("📱 URL: http://localhost:8080");
                logMessage("✨ Solution hybride active - Interface desktop + Web complète !");
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Impossible d'ouvrir le navigateur automatiquement.\n" +
                    "Veuillez ouvrir manuellement: http://localhost:8080", 
                    "Information", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException | URISyntaxException e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de l'ouverture du navigateur: " + e.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showHelp() {
        String helpText = 
            "🌟 SOLUTION HYBRIDE - Guide d'utilisation\n\n" +
            "🎯 CONCEPT :\n" +
            "Cette application combine le meilleur des deux mondes :\n" +
            "• Interface desktop native pour contrôler le serveur\n" +
            "• Navigateur web complet pour utiliser l'application\n\n" +
            "🚀 UTILISATION :\n" +
            "1. L'application démarre automatiquement le serveur Spring Boot\n" +
            "2. Une fois prêt, cliquez sur 'Ouvrir l'Application Web'\n" +
            "3. Votre navigateur s'ouvre avec l'application complète\n" +
            "4. Utilisez cette fenêtre pour contrôler le serveur\n\n" +
            "✨ AVANTAGES :\n" +
            "• Toutes les fonctionnalités web (JavaScript, CSS, formulaires)\n" +
            "• Contrôle centralisé du serveur\n" +
            "• Logs en temps réel\n" +
            "• Interface familière\n\n" +
            "🔧 CONTRÔLES :\n" +
            "• Arrêter/Redémarrer le serveur\n" +
            "• Voir les logs en temps réel\n" +
            "• Ouvrir l'application web\n" +
            "• Sauvegarder les logs";
            
        JOptionPane.showMessageDialog(this, helpText, "Aide - Solution Hybride", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void saveLogs() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File("musique-logs.txt"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.File file = fileChooser.getSelectedFile();
                java.nio.file.Files.write(file.toPath(), logArea.getText().getBytes());
                JOptionPane.showMessageDialog(this, "Logs sauvegardés avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Erreur lors de la sauvegarde: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void copyLogs() {
        String logs = logArea.getText();
        java.awt.datatransfer.StringSelection stringSelection = new java.awt.datatransfer.StringSelection(logs);
        java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
        JOptionPane.showMessageDialog(this, "Logs copiés dans le presse-papiers!", "Succès", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void startSpringBoot() {
        new Thread(() -> {
            try {
                logMessage("🚀 Démarrage du serveur Spring Boot...");
                logMessage("📁 Répertoire de travail: " + System.getProperty("user.dir"));
                logMessage("☕ Version Java: " + System.getProperty("java.version"));
                logMessage("🌟 Solution hybride activée - Interface desktop + Web complète");
                
                springContext = SpringApplication.run(MusiqueApplication.class);
                serverRunning = true;
                
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("✅ Serveur démarré - Application prête à utiliser");
                    statusLabel.setForeground(Color.GREEN);
                    openBrowserButton.setEnabled(true);
                    stopServerButton.setEnabled(true);
                    restartServerButton.setEnabled(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(100);
                    progressBar.setString("Serveur actif");
                    
                    logMessage("✅ Serveur Spring Boot démarré avec succès!");
                    logMessage("🌐 L'application est accessible sur: http://localhost:8080");
                    logMessage("📱 Cliquez sur 'Ouvrir l'Application Web' pour accéder à l'interface complète");
                    logMessage("✨ Solution hybride prête - Profitez de l'expérience desktop + web !");
                });
                
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("❌ Erreur lors du démarrage du serveur");
                    statusLabel.setForeground(Color.RED);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(0);
                    progressBar.setString("Erreur");
                    logMessage("❌ Erreur: " + e.getMessage());
                    e.printStackTrace();
                });
            }
        }).start();
    }
    
    private void stopSpringBoot() {
        if (springContext != null && serverRunning) {
            new Thread(() -> {
                try {
                    logMessage("⏹️ Arrêt du serveur Spring Boot...");
                    springContext.close();
                    serverRunning = false;
                    
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("⏹️ Serveur arrêté");
                        statusLabel.setForeground(Color.RED);
                        openBrowserButton.setEnabled(false);
                        stopServerButton.setEnabled(false);
                        restartServerButton.setEnabled(true);
                        progressBar.setIndeterminate(false);
                        progressBar.setValue(0);
                        progressBar.setString("Serveur arrêté");
                        logMessage("⏹️ Serveur Spring Boot arrêté.");
                    });
                } catch (Exception e) {
                    logMessage("❌ Erreur lors de l'arrêt: " + e.getMessage());
                }
            }).start();
        }
    }
    
    private void restartSpringBoot() {
        if (serverRunning) {
            stopSpringBoot();
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("🔄 Redémarrage du serveur...");
                        statusLabel.setForeground(Color.ORANGE);
                        progressBar.setIndeterminate(true);
                        progressBar.setString("Redémarrage...");
                    });
                    startSpringBoot();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        } else {
            startSpringBoot();
        }
    }
    
    private void logMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            String timestamp = java.time.LocalTime.now().toString().substring(0, 8);
            logArea.append("[" + timestamp + "] " + message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }
    
    public static void main(String[] args) {
        // Démarrer l'interface Swing
        SwingUtilities.invokeLater(() -> {
            try {
                new DesktopApplicationHybrid().setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, 
                    "Erreur lors du démarrage de l'application: " + e.getMessage(), 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }
}
