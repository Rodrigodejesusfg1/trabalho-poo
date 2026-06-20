package br.usp.scc0504.calendario.persistence;

import br.usp.scc0504.calendario.model.CalendarEvent;
import br.usp.scc0504.calendario.model.EventCategory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/** Salva e carrega eventos em CSV simples, sem dependências externas. */
public class EventStorage {
    private static final String HEADER = "id,title,date,time,location,description,category,reminderLeadHours";
    private final Path file;

    public EventStorage(Path file) { this.file = file; }

    public List<CalendarEvent> load() throws IOException {
        List<CalendarEvent> events = new ArrayList<>();
        if (!Files.exists(file)) return events;
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            boolean first = true;
            while ((line = reader.readLine()) != null) {
                if (first) { first = false; if (line.equals(HEADER)) continue; }
                try { events.add(parse(line)); }
                catch (RuntimeException ignored) { /* Linhas malformadas são ignoradas para preservar os demais dados. */ }
            }
        }
        return events;
    }

    public void save(List<CalendarEvent> events) throws IOException {
        Path parent = file.getParent();
        if (parent != null) Files.createDirectories(parent);
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            writer.write(HEADER);
            writer.newLine();
            for (CalendarEvent event : events) {
                writer.write(String.join(",",
                        escape(event.getId()), escape(event.getTitle()), event.getDate().toString(), event.getTime().toString(),
                        escape(event.getLocation()), escape(event.getDescription()), event.getCategory().name(),
                        Integer.toString(event.getReminderLeadHours())));
                writer.newLine();
            }
        }
    }

    private CalendarEvent parse(String line) {
        List<String> columns = splitCsv(line);
        if (columns.size() != 8) throw new IllegalArgumentException("Linha inválida");
        return new CalendarEvent(columns.get(0), columns.get(1), LocalDate.parse(columns.get(2)), LocalTime.parse(columns.get(3)),
                columns.get(4), columns.get(5), EventCategory.valueOf(columns.get(6)), Integer.parseInt(columns.get(7)));
    }

    private String escape(String value) {
        String safe = value == null ? "" : value;
        return '"' + safe.replace("\"", "\"\"") + '"';
    }

    private List<String> splitCsv(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (quoted && i + 1 < line.length() && line.charAt(i + 1) == '"') { current.append('"'); i++; }
                else quoted = !quoted;
            } else if (c == ',' && !quoted) { result.add(current.toString()); current.setLength(0); }
            else current.append(c);
        }
        result.add(current.toString());
        return result;
    }
}
