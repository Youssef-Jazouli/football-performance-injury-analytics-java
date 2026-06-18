package com.sports.analytics.exception;

/**
 * Exception personnalisée déclenchée lorsqu'un joueur
 * dépasse le seuil critique de fatigue (Danger d'injury).
 */
public class SeuilFatigueDepasseException extends Exception {
    public SeuilFatigueDepasseException(String message) {
        super(message);
    }
}