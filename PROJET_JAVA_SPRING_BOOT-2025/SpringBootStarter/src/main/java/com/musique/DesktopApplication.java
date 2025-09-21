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
 * Desktop application using Swing that displays the Music Equipment Store.
 * This creates a native desktop window that manages the Spring Boot web application.
 */
public class DesktopApplication extends JFrame {
    
    private ConfigurableApplicationContext springContext;
    private JButton openBrowserButton;
    private JButton stopServerButton;
    private JButton restartServerButton;
    private JLabel statusLabel;
    private JTextArea logArea;
    private JProgressBar progressBar;
    private boolean serverRunning = false;
    
    public DesktopApplication() {
        initializeUI();
        startSpringBoot();
    }
    
    private void initializeUI() {
        setTitle("Musique - Location et Vente d'Équipements Musicaux");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);
        
        // Créer le panneau principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Panneau de contrôle en haut
        JPanel controlPanel = createControlPanel();
        mainPanel.add(controlPanel, BorderLayout.NORTH);
        
        // Zone de log au centre
        JPanel logPanel = createLogPanel();
        mainPanel.add(logPanel, BorderLayout.CENTER);
        
        // Panneau de statut en bas
        JPanel statusPanel = createStatusPanel();
        mainPanel.add(statusPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Gérer la fermeture de la fenêtre
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stopSpringBoot();
                System.exit(0);
            }
        });
    }
    
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Contrôles du Serveur"));
        
        openBrowserButton = new JButton("🌐 Ouvrir l'Application Web");
        openBrowserButton.setEnabled(false);
        openBrowserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openWebApplication();
            }
        });
        
        stopServerButton = new JButton("⏹️ Arrêter le Serveur");
        stopServerButton.setEnabled(false);
        stopServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopSpringBoot();
            }
        });
        
        restartServerButton = new JButton("🔄 Redémarrer le Serveur");
        restartServerButton.setEnabled(false);
        restartServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restartSpringBoot();
            }
        });
        
        panel.add(openBrowserButton);
        panel.add(stopServerButton);
        panel.add(restartServerButton);
        
        return panel;
    }
    
    private JPanel createLogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Logs du Serveur"));
        
        logArea = new JTextArea(15, 60);
        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        logArea.setBackground(Color.BLACK);
        logArea.setForeground(Color.GREEN);
        
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        // Bouton pour effacer les logs
        JButton clearLogButton = new JButton("Effacer les Logs");
        clearLogButton.addActionListener(e -> logArea.setText(""));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(clearLogButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        statusLabel = new JLabel("🔄 Démarrage du serveur Spring Boot...");
        statusLabel.setForeground(Color.BLUE);
        statusLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setStringPainted(false);
        
        panel.add(statusLabel, BorderLayout.CENTER);
        panel.add(progressBar, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void startSpringBoot() {
        new Thread(() -> {
            try {
                logMessage(" Démarrage du serveur Spring Boot...");
                logMessage(" Répertoire de travail: " + System.getProperty("user.dir"));
                logMessage(" Version Java: " + System.getProperty("java.version"));
                
                springContext = SpringApplication.run(MusiqueApplication.class);
                serverRunning = true;
                
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText(" Serveur démarré - Application prête à utiliser");
                    statusLabel.setForeground(Color.GREEN);
                    openBrowserButton.setEnabled(true);
                    stopServerButton.setEnabled(true);
                    restartServerButton.setEnabled(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(100);
                    progressBar.setString("Serveur actif");
                    
                    logMessage(" Serveur Spring Boot démarré avec succès!");
                    logMessage(" L'application est accessible sur: http://localhost:8080");
                    logMessage(" Interface web disponible dans votre navigateur");
                });
                
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText(" Erreur lors du démarrage du serveur");
                    statusLabel.setForeground(Color.RED);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(0);
                    progressBar.setString("Erreur");
                    logMessage(" Erreur: " + e.getMessage());
                    e.printStackTrace();
                });
            }
        }).start();
    }
    
    private void openWebApplication() {
        try {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                desktop.browse(new URI("http://localhost:8080"));
                logMessage(" Ouverture de l'application dans le navigateur...");
                logMessage(" URL: http://localhost:8080");
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Impossible d'ouvrir le navigateur automatiquement.\n" +
                    "Veuillez ouvrir manuellement: http://localhost:8080", 
                    "Information", 
                    JOptionPane.INFORMATION_MESSAGE);
                logMessage(" Ouverture manuelle requise: http://localhost:8080");
            }
        } catch (IOException | URISyntaxException e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de l'ouverture du navigateur: " + e.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
            logMessage(" Erreur lors de l'ouverture: " + e.getMessage());
        }
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
                    logMessage(" Erreur lors de l'arrêt: " + e.getMessage());
                }
            }).start();
        }
    }
    
    private void restartSpringBoot() {
        if (serverRunning) {
            stopSpringBoot();
            // Attendre un peu puis redémarrer
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText(" Redémarrage du serveur...");
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
                new DesktopApplication().setVisible(true);
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