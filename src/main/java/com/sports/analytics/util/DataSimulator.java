package com.sports.analytics.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class DataSimulator {
    public static void genererDonneesCsv(String cheminFichier, int nbLignes) {
        // زدنا اللعابة باش اللّيستة تجي عامرة ومستفة
        String[] joueurs = {
            "Achraf Hakimi", "Sofyan Amrabat", "Hakim Ziyech", "Brahim Diaz", "Yassine Bounou",
            "Romain Saiss", "Nayef Aguerd", "Noussair Mazraoui", "Azzedine Ounahi", "Youssef En-Nesyri",
            "Amine Adli", "Ayoub El Kaabi", "Ismael Saibari", "Chadi Riad", "Amir Richardson"
        };
        Random random = new Random();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(cheminFichier))) {
            writer.write("playerId,playerName,date,distanceCoveredKm,topSpeedKmh,avgHeartRate,sleepHours,fatigueScore");
            writer.newLine();

            for (int i = 0; i < nbLignes; i++) {
                int id = random.nextInt(joueurs.length);
                String nom = joueurs[id];
                String date = "2026-06-" + String.format("%02d", random.nextInt(30) + 1);
                
                double distance = 5.0 + (10.0 * random.nextDouble());
                double vitesse = 25.0 + (10.0 * random.nextDouble());
                int bpm = 130 + random.nextInt(55);
                double sommeil = 5.0 + (4.0 * random.nextDouble());
                
                int fatigue = random.nextInt(10) + 1;
                if (sommeil < 6.0 && bpm > 170) {
                    fatigue = random.nextInt(3) + 8; // فرض إرهاق عالي
                }

                writer.write(String.format("%d,%s,%s,%.2f,%.1f,%d,%.1f,%d", 
                        (id + 1), nom, date, distance, vitesse, bpm, sommeil, fatigue));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("[Erreur] : " + e.getMessage());
        }
    }
}