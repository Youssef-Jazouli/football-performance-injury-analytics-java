package com.sports.analytics;

import com.sports.analytics.model.Joueur;
import com.sports.analytics.service.StatistiquesService;
import com.sports.analytics.util.DataSimulator;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Locale;

public class MainApp extends Application {

    private final StatistiquesService analyticsService = new StatistiquesService();
    private final String cheminCsv = "src/main/resources/data/player_fitness_data.csv";

    // Layouts
    private StackPane contentArea;
    private ScrollPane dashboardView;
    private VBox rosterView;

    // UI Components
    private Label kpiTotal, kpiRisk, kpiAvgDist, kpiAvgFatigue;
    private BarChart<String, Number> fatigueChart;
    private PieChart readinessPieChart;
    private AreaChart<String, Number> trendChart;
    private TableView<Joueur> playerTable;
    private TextArea systemLogs;
    
    // Fix: Déclaration explicite des labels du Top Performer pour éviter les bugs d'index
    private Label topPerfName;
    private Label topPerfDist;

    // Palette FRMF
    private final String FRMF_RED = "#C1272D";
    private final String FRMF_GREEN = "#006233";
    private final String ACCENT_GOLD = "#D4AF37";
    private final String TEXT_WHITE = "#FFFFFF";
    private final String TEXT_LIGHT_GRAY = "#E2E8F0";
    private final String CARD_BG = "rgba(11, 15, 25, 0.95)"; 

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Centre National d'Intelligence Footballistique - FRMF");

        BorderPane root = new BorderPane();
        
        // --- BACKGROUND ---
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #050810, #0f172a, #020408);");

        // --- HEADER ---
        root.setTop(createTopHeader());

        // --- SIDEBAR ---
        root.setLeft(createSidebar());

        // --- CONTENT AREA ---
        contentArea = new StackPane();
        dashboardView = createDashboardView();
        rosterView = createRosterView();
        rosterView.setVisible(false);
        contentArea.getChildren().addAll(rosterView, dashboardView);
        root.setCenter(contentArea);

        Scene scene = new Scene(root, 1500, 900);
        
        // --- MOTEUR CSS CORRIGÉ ---
        scene.getStylesheets().add("data:text/css," +
                ".premium-card { -fx-background-color: " + CARD_BG + "; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: rgba(212, 175, 55, 0.3); -fx-border-width: 1px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 15, 0, 0, 5); -fx-transition: all 0.2s; }" +
                ".chart-plot-background { -fx-background-color: transparent; }" +
                ".chart-vertical-grid-lines { -fx-stroke: rgba(255,255,255,0.05); }" +
                ".chart-horizontal-grid-lines { -fx-stroke: rgba(255,255,255,0.05); }" +
                ".axis { -fx-tick-label-fill: #E2E8F0; -fx-font-family: 'Segoe UI'; -fx-font-size: 13px; -fx-font-weight: bold; }" +
                ".axis-label { -fx-text-fill: #FFFFFF; -fx-font-weight: bold; -fx-font-size: 14px; }" +
                
                // FIX TABLEVIEW : Contraste Parfait
                ".table-view { -fx-background-color: transparent; -fx-border-color: transparent; }" +
                ".table-view .column-header-background { -fx-background-color: #050810; -fx-border-color: #d4af37; -fx-border-width: 0 0 2px 0; }" +
                ".table-view .column-header { -fx-background-color: transparent; }" +
                ".table-view .column-header .label { -fx-text-fill: #d4af37; -fx-font-weight: bold; -fx-font-size: 15px; -fx-alignment: center; }" +
                ".table-view .table-cell { -fx-text-fill: #FFFFFF; -fx-font-size: 15px; -fx-border-color: rgba(255,255,255,0.05); -fx-border-width: 0 0 1px 0; }" +
                ".table-row-cell { -fx-background-color: rgba(11, 15, 25, 0.8); -fx-cell-size: 55px; }" +
                ".table-row-cell:hover { -fx-background-color: rgba(193, 39, 45, 0.25); -fx-cursor: hand; }" +
                
                // FIX PIE CHART : Couleurs strictes via CSS pour corriger la légende
                ".default-color0.chart-pie { -fx-pie-color: " + FRMF_GREEN + "; }" +
                ".default-color1.chart-pie { -fx-pie-color: " + FRMF_RED + "; }" +
                ".pie-legend { -fx-background-color: transparent; }" +
                ".pie-legend .label { -fx-text-fill: #FFFFFF; -fx-font-size: 14px; -fx-font-weight: bold; }"
        );

        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();

        //refreshData();
    }

    private HBox createTopHeader() {
        HBox header = new HBox(25);
        header.setStyle("-fx-background-color: #020408; -fx-border-color: #1e293b; -fx-border-width: 0 0 2 0;");
        header.setPadding(new Insets(15, 40, 15, 40));
        header.setAlignment(Pos.CENTER_LEFT);

        ImageView logoView = new ImageView();
        try {
            Image logoImg = new Image("https://upload.wikimedia.org/wikipedia/commons/thumb/e/e0/F%C3%A9d%C3%A9ration_Royale_Marocaine_de_Football.svg/512px-F%C3%A9d%C3%A9ration_Royale_Marocaine_de_Football.svg.png", 75, 75, true, true);
            logoView.setImage(logoImg);
            logoView.setEffect(new DropShadow(15, Color.web(ACCENT_GOLD, 0.3)));
        } catch (Exception e) {}

        VBox titleBox = new VBox(5);
        Label mainTitle = new Label("CENTRE NATIONAL D'INTELLIGENCE FOOTBALLISTIQUE");
        mainTitle.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 26));
        mainTitle.setTextFill(Color.web(TEXT_WHITE));
        
        Label subTitle = new Label("Royal Moroccan Football Federation – Performance Analysis & Decision Support");
        subTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        subTitle.setTextFill(Color.web(ACCENT_GOLD));
        
        titleBox.getChildren().addAll(mainTitle, subTitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        String dateActuelle = LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.FRENCH));
        Label dateLabel = new Label("📅 " + dateActuelle.toUpperCase());
        dateLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        dateLabel.setStyle("-fx-background-color: #1e293b; -fx-padding: 10 20; -fx-background-radius: 8; -fx-text-fill: #FFFFFF; -fx-border-color: #d4af37; -fx-border-radius: 8;");

        header.getChildren().addAll(logoView, titleBox, spacer, dateLabel);
        return header;
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(20);
        sidebar.setPrefWidth(280);
        sidebar.setStyle("-fx-background-color: #050810; -fx-border-color: #1e293b; -fx-border-width: 0 2 0 0;");
        sidebar.setPadding(new Insets(40, 20, 30, 20));

        Button btnDash = createNavButton("📊 Tableau de Bord Stratégique", true);
        Button btnRoster = createNavButton("🩺 Cellule d'Aide à la Décision", false);

        btnDash.setOnAction(e -> switchView(dashboardView, rosterView, btnDash, btnRoster));
        btnRoster.setOnAction(e -> switchView(rosterView, dashboardView, btnRoster, btnDash));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button btnGenerate = new Button("📡 SYNC GILETS GPS");
        btnGenerate.setStyle("-fx-background-color: linear-gradient(to right, #006233, #008542); -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 15px; -fx-padding: 16; -fx-cursor: hand; -fx-background-radius: 8;");
        btnGenerate.setEffect(new DropShadow(15, Color.web(FRMF_GREEN, 0.4)));
        
        btnGenerate.setOnAction(e -> {
            DataSimulator.genererDonneesCsv(cheminCsv, 200);
            systemLogs.setText("[SYNC OK] Données télémétriques actualisées.\n");
            refreshData();
        });

        sidebar.getChildren().addAll(btnDash, btnRoster, spacer, btnGenerate);
        return sidebar;
    }

    private Button createNavButton(String text, boolean active) {
        Button btn = new Button(text);
        btn.setPrefWidth(240);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(16, 15, 16, 15));
        btn.setCursor(javafx.scene.Cursor.HAND);
        if (active) {
            btn.setStyle("-fx-background-color: #1e293b; -fx-text-fill: " + ACCENT_GOLD + "; -fx-font-size: 15px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-border-color: " + ACCENT_GOLD + "; -fx-border-width: 0 0 0 4; -fx-border-radius: 8;");
        } else {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + TEXT_LIGHT_GRAY + "; -fx-font-size: 15px; -fx-font-weight: bold;");
        }
        return btn;
    }

    private void switchView(Node viewToShow, Node viewToHide, Button activeBtn, Button inactiveBtn) {
        viewToHide.setVisible(false);
        viewToShow.setVisible(true);
        viewToShow.toFront();
        activeBtn.setStyle("-fx-background-color: #1e293b; -fx-text-fill: " + ACCENT_GOLD + "; -fx-font-size: 15px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-border-color: " + ACCENT_GOLD + "; -fx-border-width: 0 0 0 4; -fx-border-radius: 8;");
        inactiveBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + TEXT_LIGHT_GRAY + "; -fx-font-size: 15px; -fx-font-weight: bold;");
        
        viewToShow.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(500), viewToShow);
        ft.setToValue(1.0);
        ft.play();
    }

    private ScrollPane createDashboardView() {
        VBox view = new VBox(35);
        view.setPadding(new Insets(40, 50, 60, 50));
        view.setStyle("-fx-background-color: transparent;");

        HBox kpiRow = new HBox(25);
        kpiTotal = new Label("0"); kpiRisk = new Label("0"); 
        kpiAvgDist = new Label("0.0"); kpiAvgFatigue = new Label("0.0");
        
        kpiRow.getChildren().addAll(
            createKpiCard("EFFECTIF ANALYSÉ", kpiTotal, "#3b82f6", "Joueurs suivis"),
            createKpiCard("ALERTE DÉCHIRURE", kpiRisk, FRMF_RED, "Fatigue critique (>=8)"),
            createKpiCard("VOLUME MOYEN", kpiAvgDist, ACCENT_GOLD, "Distance (km)"),
            createKpiCard("INDICE DE FATIGUE", kpiAvgFatigue, FRMF_GREEN, "Moyenne / 10")
        );

        HBox row2 = new HBox(25);
        VBox barChartContainer = createPanel("ANALYSE DE LA CHARGE PHYSIQUE INDIVIDUELLE");
        CategoryAxis xBar = new CategoryAxis();
        xBar.setTickLabelRotation(-45); 
        NumberAxis yBar = new NumberAxis(0, 10, 1);
        fatigueChart = new BarChart<>(xBar, yBar);
        fatigueChart.setLegendVisible(false);
        fatigueChart.setPrefHeight(380);
        barChartContainer.getChildren().add(fatigueChart);
        HBox.setHgrow(barChartContainer, Priority.ALWAYS);

        VBox pieChartContainer = createPanel("RÉPARTITION STRATÉGIQUE (ÉTAT DE FORME)");
        readinessPieChart = new PieChart();
        readinessPieChart.setLegendSide(javafx.geometry.Side.BOTTOM);
        readinessPieChart.setLabelsVisible(false); 
        readinessPieChart.setPrefSize(400, 380);
        pieChartContainer.getChildren().add(readinessPieChart);

        row2.getChildren().addAll(barChartContainer, pieChartContainer, createTopPerformerCard());

        VBox areaChartContainer = createPanel("TENDANCE DU VOLUME DE JEU (KILOMÉTRAGE)");
        CategoryAxis xArea = new CategoryAxis();
        NumberAxis yArea = new NumberAxis();
        trendChart = new AreaChart<>(xArea, yArea);
        trendChart.setLegendVisible(false);
        trendChart.setPrefHeight(280);
        areaChartContainer.getChildren().add(trendChart);

        view.getChildren().addAll(kpiRow, row2, areaChartContainer);

        ScrollPane scroll = new ScrollPane(view);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        return scroll;
    }

    private VBox createRosterView() {
        VBox view = new VBox(35);
        view.setPadding(new Insets(40, 50, 60, 50));
        view.setStyle("-fx-background-color: transparent;");

        VBox tableContainer = createPanel("BASE DE DONNÉES TÉLÉMÉTRIQUE OFFICIELLE");
        playerTable = new TableView<>();
        playerTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<Joueur, String> nameCol = new TableColumn<>("IDENTITÉ DU JOUEUR");
        nameCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getNom().toUpperCase()));
        nameCol.setStyle("-fx-alignment: CENTER-LEFT; -fx-font-weight: bold; -fx-padding: 0 0 0 20;");
        
        TableColumn<Joueur, String> distCol = new TableColumn<>("VOLUME (KM)");
        distCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(String.format(Locale.US, "%.2f km", data.getValue().getDistanceCumulee())));
        distCol.setStyle("-fx-alignment: CENTER; -fx-font-family: 'Consolas'; -fx-text-fill: " + ACCENT_GOLD + "; -fx-font-weight: bold;");
        
        TableColumn<Joueur, String> fatCol = new TableColumn<>("FATIGUE /10");
        fatCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(String.valueOf(data.getValue().getScoreFatigueActuel())));
        fatCol.setStyle("-fx-alignment: CENTER; -fx-font-family: 'Consolas'; -fx-font-weight: bold;");
        
        TableColumn<Joueur, String> statusCol = new TableColumn<>("AVIS MÉDICAL");
        statusCol.setCellValueFactory(data -> {
            boolean danger = data.getValue().getScoreFatigueActuel() >= 8;
            return new ReadOnlyStringWrapper(danger ? "🛑 RISQUE DÉCHIRURE" : "✅ APTE AU JEU");
        });
        statusCol.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold;");

        playerTable.getColumns().addAll(nameCol, distCol, fatCol, statusCol);
        tableContainer.getChildren().add(playerTable);
        VBox.setVgrow(tableContainer, Priority.ALWAYS);

        VBox logsContainer = createPanel("MONITEUR D'ALERTE EN TEMPS RÉEL (LOGS)");
        systemLogs = new TextArea();
        systemLogs.setEditable(false);
        systemLogs.setPrefHeight(200);
        systemLogs.setStyle("-fx-control-inner-background: #020408; -fx-text-fill: " + FRMF_RED + "; -fx-font-family: 'Consolas'; -fx-border-color: #1e293b; -fx-font-size: 15px; -fx-font-weight: bold;");
        logsContainer.getChildren().add(systemLogs);

        view.getChildren().addAll(tableContainer, logsContainer);
        return view;
    }

    private VBox createPanel(String titleText) {
        VBox panel = new VBox(20);
        panel.getStyleClass().add("premium-card");
        panel.setPadding(new Insets(25));
        
        Label title = new Label(titleText);
        title.setTextFill(Color.web(ACCENT_GOLD));
        title.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 16)); 
        
        panel.getChildren().add(title);
        return panel;
    }

    private VBox createKpiCard(String titleText, Label valueLabel, String colorHex, String subtitleText) {
        VBox card = new VBox(5);
        card.getStyleClass().add("premium-card");
        card.setStyle("-fx-border-width: 0 0 6 0; -fx-border-color: transparent transparent " + colorHex + " transparent;");
        card.setPadding(new Insets(25));
        
        Label title = new Label(titleText);
        title.setTextFill(Color.web(TEXT_LIGHT_GRAY));
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        
        valueLabel.setTextFill(Color.web(colorHex));
        valueLabel.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 52));

        Label subtitle = new Label(subtitleText);
        subtitle.setTextFill(Color.web("#94a3b8"));
        subtitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        
        card.getChildren().addAll(title, valueLabel, subtitle);
        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }

    // FIX : Création de la carte avec variables explicites
    private VBox createTopPerformerCard() {
        VBox card = createPanel("🌟 TOP PERFORMER");
        card.setPrefWidth(300);
        card.setAlignment(Pos.TOP_CENTER);

        Label icon = new Label("🏆");
        icon.setFont(Font.font(55));
        icon.setEffect(new DropShadow(20, Color.web(ACCENT_GOLD, 0.7)));

        topPerfName = new Label("...");
        topPerfName.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 26)); 
        topPerfName.setTextFill(Color.web(TEXT_WHITE)); // Texte blanc pour meilleur lisibilité
        topPerfName.setPadding(new Insets(10, 0, 5, 0));

        topPerfDist = new Label("-- km");
        topPerfDist.setFont(Font.font("Consolas", FontWeight.EXTRA_BOLD, 36)); 
        topPerfDist.setTextFill(Color.web(ACCENT_GOLD)); // L'or pour le chiffre de la distance

        Label subStatLabel = new Label("Volume de jeu Max");
        subStatLabel.setTextFill(Color.web(TEXT_LIGHT_GRAY));
        subStatLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        card.getChildren().addAll(icon, topPerfName, topPerfDist, subStatLabel);
        return card;
    }

    private void refreshData() {
        analyticsService.analyserFichierPerformances(cheminCsv);
        var joueurs = analyticsService.getTableJoueurs().values();
        
        if (joueurs.isEmpty()) return;

        // 1. KPIs
        int total = joueurs.size();
        long riskCount = joueurs.stream().filter(j -> j.getScoreFatigueActuel() >= 8).count();
        double avgDist = joueurs.stream().mapToDouble(Joueur::getDistanceCumulee).average().orElse(0.0);
        double avgFatigue = joueurs.stream().mapToInt(Joueur::getScoreFatigueActuel).average().orElse(0.0);
        
        kpiTotal.setText(String.valueOf(total));
        kpiRisk.setText(String.valueOf(riskCount));
        kpiAvgDist.setText(String.format(Locale.US, "%.1f", avgDist));
        kpiAvgFatigue.setText(String.format(Locale.US, "%.1f", avgFatigue));

        // 2. FIX: Top Performer (Extraction du Nom de Famille pour éviter MAZRA...)
        Joueur topPlayer = joueurs.stream().max(Comparator.comparingDouble(Joueur::getDistanceCumulee)).orElse(null);
        if (topPlayer != null) {
            String fullName = topPlayer.getNom().trim();
            String lastName = fullName.substring(fullName.lastIndexOf(" ") + 1).toUpperCase(); // Ex: HAKIMI
            
            topPerfName.setText(lastName);
            topPerfDist.setText(String.format(Locale.US, "%.1f km", topPlayer.getDistanceCumulee()));
        }

        // 3. Charts
        fatigueChart.getData().clear();
        trendChart.getData().clear();
        XYChart.Series<String, Number> barSeries = new XYChart.Series<>();
        XYChart.Series<String, Number> areaSeries = new XYChart.Series<>();
        systemLogs.clear();

        for (Joueur j : joueurs) {
            barSeries.getData().add(new XYChart.Data<>(j.getNom(), j.getScoreFatigueActuel()));
            areaSeries.getData().add(new XYChart.Data<>(j.getNom(), j.getDistanceCumulee()));

            if (j.getScoreFatigueActuel() >= 8) {
                systemLogs.appendText("⚠️ [URGENCE MÉDICALE] " + j.getNom().toUpperCase() + " : Seuil critique atteint (" + j.getScoreFatigueActuel() + "/10).\n");
            }
        }
        
        fatigueChart.getData().add(barSeries);
        trendChart.getData().add(areaSeries);

        for (XYChart.Data<String, Number> data : barSeries.getData()) {
            boolean isDanger = data.getYValue().intValue() >= 8;
            String color = isDanger ? FRMF_RED : FRMF_GREEN;
            data.getNode().setStyle("-fx-bar-fill: " + color + "; -fx-border-color: #050810; -fx-border-width: 1px;");
            
            Tooltip tooltip = new Tooltip(data.getXValue() + "\nFatigue: " + data.getYValue() + "/10");
            tooltip.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
            Tooltip.install(data.getNode(), tooltip);
        }

        // 4. FIX: PieChart (Légende avec pourcentage)
        int apteCount = total - (int)riskCount;
        double aptePct = (total > 0) ? ((double)apteCount / total * 100) : 0;
        double risquePct = (total > 0) ? ((double)riskCount / total * 100) : 0;

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
            new PieChart.Data(String.format("Apte au jeu : %d (%.0f%%)", apteCount, aptePct), apteCount),
            new PieChart.Data(String.format("Risque Blessure : %d (%.0f%%)", riskCount, risquePct), riskCount)
        );
        readinessPieChart.setData(pieData);
        // Note: Les couleurs sont maintenant gérées uniquement par le CSS (.default-color0 et .default-color1)

        // 5. Table
        playerTable.getItems().setAll(joueurs);
    }

    public static void main(String[] args) { launch(args); }
}