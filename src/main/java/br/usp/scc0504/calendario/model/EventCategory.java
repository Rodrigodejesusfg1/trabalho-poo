package br.usp.scc0504.calendario.model;

import java.awt.Color;

/** Categorias permitidas para organizar e colorir os eventos. */
public enum EventCategory {
    MEETING("Reunião", new Color(79, 129, 189)),
    BIRTHDAY("Aniversário", new Color(155, 187, 89)),
    APPOINTMENT("Compromisso", new Color(192, 80, 77));

    private final String label;
    private final Color color;

    EventCategory(String label, Color color) {
        this.label = label;
        this.color = color;
    }

    public String getLabel() { return label; }
    public Color getColor() { return color; }
    @Override public String toString() { return label; }
}
