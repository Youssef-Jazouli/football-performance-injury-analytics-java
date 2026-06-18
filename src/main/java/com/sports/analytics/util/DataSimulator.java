package com.sports.analytics.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/**
 * Générateur automatique de données de fitness pour simuler des données GPS réelles.
 * Permet de s'affranchir de Kaggle et de créer un scénario de test parfait.
 */
public class DataSimulator {

    /**
     * Génère un fichier CSV de test avec des données réalistes pour plusieurs joueurs.
     * @param cheminFichier Le chemin où sauvegarder le CSV
     * @param nbLignes Le nombre de lignes de données à générer
     */
    public static void genererDonneesCsv(String cheminFichier, int nbLignes) {
        String[] joueurs = {"Achraf Hakimi", "Sofyan Amrabat", "Hakim Ziyech", "Brahim Diaz", "Yassine Bounou"};
        Random random = new Random();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(cheminFichier))) {
            // Écriture du Header CSV
            writer.write("playerId,playerName,date,distanceCoveredKm,topSpeedKmh,avgHeartRate,sleepHours,fatigueScore");
            writer.newLine();

            for (int i = 0; i < nbLignes; i++) {
                int id = random.nextInt(joueurs.length);
                String nom = joueurs[id];
                String date = "2026-06-" + String.format("%02d", random.nextInt(30) + 1);
                
                // Génération de données réalistes
                double distance = 5.0 + (10.0 * random.nextDouble()); // Entre 5 et 15 km
                double vitesse = 25.0 + (10.0 * random.nextDouble());  // Entre 25 et 35 km/h
                int bpm = 130 + random.nextInt(55);                   // Entre 130 et 185 bpm
                double sommeil = 5.0 + (4.0 * random.nextDouble());   // Entre 5h et 9h de sommeil
                
                // Simulation d'un pic de fatigue (Scénario d'alerte pour le projet)
                int fatigue = random.nextInt(10) + 1;
                if (sommeil < 6.0 && bpm > 170) {
                    fatigue = random.nextInt(3) + 8; // Force une fatigue critique (8, 9 ou 10)
                }

                // Écriture de la ligne
                writer.write(String.format("%d,%s,%s,%.2f,%.1f,%d,%.1f,%d", 
                        (id + 1), nom, date, distance, vitesse, bpm, sommeil, fatigue));
                writer.newLine();
            }
            
            System.out.println("[Générateur] Fichier de simulation créé avec succès : " + nbLignes + " lignes générées.");

        } catch (IOException e) {
            System.err.println("[Erreur Générateur] Impossible de créer les données : " + e.getMessage());
        }
    }
}