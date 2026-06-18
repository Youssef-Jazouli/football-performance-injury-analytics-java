package com.sports.analytics;

import com.sports.analytics.service.StatistiquesService;
import com.sports.analytics.util.DataSimulator;

/**
 * Classe principale pour lancer l'application de détection de fatigue.
 */
public class Main {
    public static void main(String[] args) {
        // Le chemin relatif correct pour l'écriture et la lecture directe sur le disque
        String cheminCsv = "src/main/resources/data/player_fitness_data.csv";

        // 1. Générer automatiquement 500 lignes de données réalistes
        DataSimulator.genererDonneesCsv(cheminCsv, 500);

        // 2. Initialisation du service d'analyse
        StatistiquesService analyticsService = new StatistiquesService();

        // 3. Lancement de l'analyse avec le BON chemin de fichier
        analyticsService.analyserFichierPerformances(cheminCsv);

        // 4. Affichage du rapport médical de disponibilité pour le staff technique
        analyticsService.afficherRapportDisponibilite();
    }
}