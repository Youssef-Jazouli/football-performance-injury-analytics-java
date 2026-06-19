package com.sports.analytics.service;

import com.sports.analytics.exception.SeuilFatigueDepasseException;
import com.sports.analytics.model.Joueur;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Service gérant l'analyse des données de performance et de fatigue.
 */
public class StatistiquesService {

    private final Map<Integer, Joueur> tableJoueurs = new HashMap<>();

    /**
     * Lit le fichier CSV des performances directement depuis le disque et analyse l'état de chaque joueur.
     * @param cheminFichier Le chemin absolu ou relatif du fichier CSV
     */
    public void analyserFichierPerformances(String cheminFichier) {
        tableJoueurs.clear(); 
        
        try {
            Reader reader = new FileReader(cheminFichier, StandardCharsets.UTF_8);
            
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .withIgnoreHeaderCase()
                    .withTrim());

            System.out.println("\n=== DEBUT DE L'ANALYSE DES DONNEES GPS ===");

            for (CSVRecord record : csvParser) {
                int id = Integer.parseInt(record.get("playerId"));
                String nom = record.get("playerName");
                double distance = Double.parseDouble(record.get("distanceCoveredKm"));
                int fatigue = Integer.parseInt(record.get("fatigueScore"));

                Joueur joueur = tableJoueurs.computeIfAbsent(id, k -> new Joueur(id, nom));

                joueur.setDistanceCumulee(distance);
                joueur.setTotalMatchs(joueur.getTotalMatchs() + 1);
                joueur.setScoreFatigueActuel(fatigue);

                System.out.printf("[Tracking] Joueur: %-15s | Distance: %5.2f km | Fatigue: %d/10\n", 
                        nom, distance, fatigue);

                if (fatigue >= 8) {
                    try {
                        throw new SeuilFatigueDepasseException("[ALERTE MEDICALE] Le joueur " 
                                + nom + " a dépassé le seuil de fatigue critique (" + fatigue + "/10) ! Risque élevé de blessure.");
                    } catch (SeuilFatigueDepasseException e) {
                        System.err.println(e.getMessage());
                    }
                }
            }
            csvParser.close();
            System.out.println("=== FIN DE L'ANALYSE ===\n");

        } catch (Exception e) {
            System.err.println("[Erreur lors de l'analyse] : " + e.getMessage());
        }
    }

    /**
     * Affiche le rapport final de disponibilité des joueurs.
     */
    public void afficherRapportDisponibilite() {
        System.out.println("================================================================");
        System.out.println("            RAPPORT FINAL DE DISPONIBILITE DU STAFF            ");
        System.out.println("================================================================");
        for (Joueur j : tableJoueurs.values()) {
            String statut = (j.getScoreFatigueActuel() >= 8) ? "NON DISPONIBLE (REPOS)" : "DISPONIBLE (OK)";
            System.out.printf("Joueur: %-15s | Matchs: %2d | Dist. Totale: %6.1f km | Statut: %s\n",
                    j.getNom(), j.getTotalMatchs(), j.getDistanceCumulee(), statut);
        }
        System.out.println("================================================================");
    }

    /**
     * Retourne la map des joueurs pour l'affichage dans l'interface graphique.
     */
    public Map<Integer, Joueur> getTableJoueurs() {
        return tableJoueurs;
    }
}