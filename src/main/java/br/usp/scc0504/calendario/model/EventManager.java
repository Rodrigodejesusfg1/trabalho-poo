package br.usp.scc0504.calendario.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Centraliza operações de consulta e alteração dos eventos em memória. */
public class EventManager {
    private final List<CalendarEvent> events = new ArrayList<>();

    public void replaceAll(List<CalendarEvent> loadedEvents) { events.clear(); events.addAll(loadedEvents); sort(); }
    public List<CalendarEvent> getAll() { return List.copyOf(events); }
    public void add(CalendarEvent event) { events.add(event); sort(); }
    public void remove(CalendarEvent event) { events.remove(event); }
    public void sort() { events.sort(Comparator.comparing(CalendarEvent::getDateTime)); }

    public List<CalendarEvent> byDate(LocalDate date) {
        return events.stream().filter(e -> e.getDate().equals(date)).sorted(Comparator.comparing(CalendarEvent::getTime)).toList();
    }

    public List<CalendarEvent> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return getAll();
        return events.stream().filter(e -> e.containsKeyword(keyword.trim())).toList();
    }

    public List<CalendarEvent> remindersForNext24Hours(LocalDateTime now) {
        LocalDateTime limit = now.plusHours(24);
        return events.stream()
                .filter(e -> !e.getReminderDateTime().isBefore(now) && !e.getReminderDateTime().isAfter(limit))
                .sorted(Comparator.comparing(CalendarEvent::getReminderDateTime)).toList();
    }
}
