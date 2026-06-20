package br.usp.scc0504.calendario.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import java.util.UUID;

/** Representa um evento salvo no calendário. */
public class CalendarEvent {
    private final String id;
    private String title;
    private LocalDate date;
    private LocalTime time;
    private String location;
    private String description;
    private EventCategory category;
    private int reminderLeadHours;

    public CalendarEvent(String title, LocalDate date, LocalTime time, String location,
                         String description, EventCategory category, int reminderLeadHours) {
        this(UUID.randomUUID().toString(), title, date, time, location, description, category, reminderLeadHours);
    }

    public CalendarEvent(String id, String title, LocalDate date, LocalTime time, String location,
                         String description, EventCategory category, int reminderLeadHours) {
        this.id = Objects.requireNonNull(id);
        setTitle(title);
        setDate(date);
        setTime(time);
        this.location = location == null ? "" : location.trim();
        this.description = description == null ? "" : description.trim();
        this.category = Objects.requireNonNull(category, "Categoria obrigatória");
        setReminderLeadHours(reminderLeadHours);
    }

    public LocalDateTime getDateTime() { return LocalDateTime.of(date, time); }
    public LocalDateTime getReminderDateTime() { return getDateTime().minusHours(reminderLeadHours); }
    public String getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) throw new IllegalArgumentException("O título é obrigatório.");
        this.title = title.trim();
    }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = Objects.requireNonNull(date, "Data obrigatória"); }
    public LocalTime getTime() { return time; }
    public void setTime(LocalTime time) { this.time = Objects.requireNonNull(time, "Hora obrigatória"); }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location == null ? "" : location.trim(); }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description == null ? "" : description.trim(); }
    public EventCategory getCategory() { return category; }
    public void setCategory(EventCategory category) { this.category = Objects.requireNonNull(category); }
    public int getReminderLeadHours() { return reminderLeadHours; }
    public void setReminderLeadHours(int reminderLeadHours) {
        if (reminderLeadHours < 0) throw new IllegalArgumentException("O lembrete não pode ser negativo.");
        this.reminderLeadHours = reminderLeadHours;
    }

    public boolean containsKeyword(String keyword) {
        String text = (title + " " + location + " " + description + " " + category.getLabel()).toLowerCase();
        return text.contains(keyword.toLowerCase());
    }

    @Override public String toString() { return time + " - " + title + " (" + category.getLabel() + ")"; }
}
