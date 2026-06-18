package com.sports.analytics;

import com.sports.analytics.model.Joueur;
import com.sports.analytics.service.StatistiquesService;
import com.sports.analytics.util.DataSimulator;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * Interface Graphique principale du projet (JavaFX Dashboard).
 */
public class MainApp extends Application {

    private final StatistiquesService analyticsService = new StatistiquesService();
    private final String cheminCsv = "src/main/resources/data/player_fitness_data.csv";
    private TextArea logArea;
    private ListView<String> joueurListView;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("EST/EMIAG - Sports Performance & Injury Analytics");

        // --- TITRE DE L'APPLICATION ---
        Label titleLabel = new Label("SPORTS PERFORMANCE & INJURY ANALYTICS");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #ffffff;");
        
        HBox header = new HBox(titleLabel);
        header.setStyle("-fx-background-color: #2E3440; -fx-padding: 15px;");
        header.setAlignment(Pos.CENTER);

        // --- SECTION DES BOUTONS ---
        Button btnGenerer = new Button("1. Générer Data (CSV)");
        Button btnAnalyser = new Button("2. Lancer l'Analyse");
        
        btnGenerer.setStyle("-fx-background-color: #81A1C1; -fx-text-fill: white; -fx-font-weight: bold;");
        btnAnalyser.setStyle("-fx-background-color: #A3BE8C; -fx-text-fill: white; -fx-font-weight: bold;");

        HBox controlBox = new HBox(15, btnGenerer, btnAnalyser);
        controlBox.setPadding(new Insets(10));
        controlBox.setAlignment(Pos.CENTER);

        // --- PANNEAU DE COMPOSANTS (DASHBOARD) ---
        joueurListView = new ListView<>();
        joueurListView.setPrefWidth(450);

        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setPromptText("Logs d'analyse en temps réel (Alertes Médicales)...");
        logArea.setStyle("-fx-control-inner-background: #3B4252; -fx-text-fill: #E5E9F0; -fx-font-family: 'Courier New';");

        HBox dashboardBox = new HBox(15, joueurListView, logArea);
        dashboardBox.setPadding(new Insets(10));
        HBox.setHgrow(logArea, Priority.ALWAYS);

        // --- LOGIQUE DES BOUTONS (ACTIONS) ---
        btnGenerer.setOnAction(e -> {
            DataSimulator.genererDonneesCsv(cheminCsv, 100);
            logArea.setText("[Système] 100 lignes de données GPS générées avec succès dans le fichier CSV.\n");
        });

        btnAnalyser.setOnAction(e -> {
            logArea.appendText("[Système] Début de l'analyse des flux de données...\n\n");
            
            analyticsService.analyserFichierPerformances(cheminCsv);
            
            joueurListView.getItems().clear();
            for (Joueur j : analyticsService.getTableJoueurs().values()) {
                String statut = (j.getScoreFatigueActuel() >= 8) ? "❌ INVALIDE (⚠️ RISQUE BLESSURE)" : "✅ DISPONIBLE (OK)";
                String info = String.format("ID: %d | %-15s | Matchs: %2d | Fatigue: %2d/10 -> %s", 
                        j.getId(), j.getNom(), j.getTotalMatchs(), j.getScoreFatigueActuel(), statut);
                joueurListView.getItems().add(info);
                
                if (j.getScoreFatigueActuel() >= 8) {
                    logArea.appendText("[ALERTE CRITIQUE] " + j.getNom() + " doit être mis au repos immédiatement ! (Fatigue: " + j.getScoreFatigueActuel() + "/10)\n");
                }
            }
            logArea.appendText("\n[Système] Analyse terminée avec succès.\n");
        });

        // --- LAYOUT GENERAL ---
        BorderPane root = new BorderPane();
        root.setTop(header);
        root.setCenter(dashboardBox);
        root.setBottom(controlBox);

        Scene scene = new Scene(root, 950, 550);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}