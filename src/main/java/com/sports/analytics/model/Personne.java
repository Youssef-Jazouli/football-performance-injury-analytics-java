package com.sports.analytics.model;

/**
 * Classe parente abstraite représentant une personne.
 * Implémente le principe d'encapsulation.
 */
public abstract class Personne {
    private int id;
    private String nom;

    // Constructeur
    public Personne(int id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
}