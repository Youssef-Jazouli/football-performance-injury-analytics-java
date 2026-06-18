package com.sports.analytics.model;

/**
 * Classe Joueur qui hérite de Personne.
 * Remplit le critère d'héritage demandé par le professeur.
 */
public class Joueur extends Personne {
    private double distanceCumulee;
    private int scoreFatigueActuel;
    private int totalMatchs;

    // Constructeur appelant le constructeur parent via super()
    public Joueur(int id, String nom) {
        super(id, nom);
        this.distanceCumulee = 0.0;
        this.scoreFatigueActuel = 0;
        this.totalMatchs = 0;
    }

    // Getters & Setters spécifiques au joueur
    public double getDistanceCumulee() { return distanceCumulee; }
    public void setDistanceCumulee(double distanceCumulee) { this.distanceCumulee = distanceCumulee; }

    public int getScoreFatigueActuel() { return scoreFatigueActuel; }
    public void setScoreFatigueActuel(int scoreFatigueActuel) { this.scoreFatigueActuel = scoreFatigueActuel; }

    public int getTotalMatchs() { return totalMatchs; }
    public void setTotalMatchs(int totalMatchs) { this.totalMatchs = totalMatchs; }
}