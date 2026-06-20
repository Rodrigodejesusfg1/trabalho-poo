package br.usp.scc0504.calendario.ui;

import br.usp.scc0504.calendario.model.CalendarEvent;
import br.usp.scc0504.calendario.model.EventManager;
import br.usp.scc0504.calendario.persistence.EventStorage;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

/** Tela principal com calendário mensal, lista diária e ações de gerenciamento. */
public class CalendarFrame extends JFrame {
    private final EventManager manager = new EventManager();
    private final EventStorage storage = new EventStorage(Path.of("data", "eventos.csv"));
    private final JPanel calendarPanel = new JPanel(new GridLayout(0, 7, 4, 4));
    private final DefaultListModel<CalendarEvent> listModel = new DefaultListModel<>();
    private final JList<CalendarEvent> eventList = new JList<>(listModel);
    private final JLabel monthLabel = new JLabel("", SwingConstants.CENTER);
    private YearMonth currentMonth = YearMonth.now();
    private LocalDate selectedDate = LocalDate.now();

    public CalendarFrame() {
        super("Calendário de Eventos SCC0504");
        setDefaultCloseOperation(EXIT_ON_CLOSE); setSize(980, 620); setLocationRelativeTo(null);
        loadEvents(); buildLayout(); refreshCalendar(); refreshDayList(); showStartupReminders();
    }

    private void buildLayout() {
        JButton previous = new JButton("< Mês anterior"); previous.addActionListener(e -> changeMonth(-1));
        JButton next = new JButton("Próximo mês >"); next.addActionListener(e -> changeMonth(1));
        JPanel navigation = new JPanel(new BorderLayout()); navigation.add(previous, BorderLayout.WEST); navigation.add(monthLabel, BorderLayout.CENTER); navigation.add(next, BorderLayout.EAST);
        JPanel left = new JPanel(new BorderLayout(8, 8)); left.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); left.add(navigation, BorderLayout.NORTH); left.add(calendarPanel, BorderLayout.CENTER);
        eventList.setCellRenderer((list, value, index, selected, focus) -> {
            JLabel label = new JLabel(value.toString()); label.setOpaque(true);
            label.setBackground(selected ? list.getSelectionBackground() : value.getCategory().getColor().brighter());
            label.setForeground(selected ? list.getSelectionForeground() : Color.BLACK); label.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6)); return label;
        });
        JPanel actions = new JPanel(new GridLayout(0, 1, 4, 4));
        JButton add = new JButton("Adicionar"); add.addActionListener(e -> addEvent());
        JButton edit = new JButton("Editar"); edit.addActionListener(e -> editEvent());
        JButton delete = new JButton("Excluir"); delete.addActionListener(e -> deleteEvent());
        JButton search = new JButton("Buscar"); search.addActionListener(e -> searchEvents());
        actions.add(add); actions.add(edit); actions.add(delete); actions.add(search);
        JPanel right = new JPanel(new BorderLayout(8, 8)); right.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        right.add(new JLabel("Eventos do dia selecionado"), BorderLayout.NORTH); right.add(new JScrollPane(eventList), BorderLayout.CENTER); right.add(actions, BorderLayout.SOUTH);
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right); split.setResizeWeight(0.65); add(split);
    }

    private void refreshCalendar() {
        calendarPanel.removeAll();
        monthLabel.setText(currentMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.of("pt", "BR")) + " de " + currentMonth.getYear());
        for (String day : List.of("Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb")) calendarPanel.add(new JLabel(day, SwingConstants.CENTER));
        int offset = currentMonth.atDay(1).getDayOfWeek().getValue() % 7;
        for (int i = 0; i < offset; i++) calendarPanel.add(new JLabel(""));
        for (int day = 1; day <= currentMonth.lengthOfMonth(); day++) {
            LocalDate date = currentMonth.atDay(day); JButton button = new JButton(Integer.toString(day));
            button.setToolTipText(date.toString()); button.setOpaque(true);
            if (!manager.byDate(date).isEmpty()) button.setBackground(new Color(255, 236, 179));
            if (date.equals(selectedDate)) button.setBorder(BorderFactory.createLineBorder(Color.BLUE, 3));
            button.addActionListener(e -> { selectedDate = date; refreshCalendar(); refreshDayList(); }); calendarPanel.add(button);
        }
        calendarPanel.revalidate(); calendarPanel.repaint();
    }

    private void refreshDayList() { listModel.clear(); manager.byDate(selectedDate).forEach(listModel::addElement); }
    private void changeMonth(int delta) { currentMonth = currentMonth.plusMonths(delta); selectedDate = currentMonth.atDay(Math.min(selectedDate.getDayOfMonth(), currentMonth.lengthOfMonth())); refreshCalendar(); refreshDayList(); }

    private void addEvent() { EventDialog dialog = new EventDialog(this, selectedDate, null); dialog.setVisible(true); if (dialog.getResult() != null) { manager.add(dialog.getResult()); saveEvents(); refreshCalendar(); refreshDayList(); } }
    private void editEvent() { CalendarEvent event = eventList.getSelectedValue(); if (event == null) { showMessage("Selecione um evento para editar."); return; } EventDialog dialog = new EventDialog(this, event.getDate(), event); dialog.setVisible(true); manager.sort(); saveEvents(); selectedDate = event.getDate(); currentMonth = YearMonth.from(selectedDate); refreshCalendar(); refreshDayList(); }
    private void deleteEvent() { CalendarEvent event = eventList.getSelectedValue(); if (event == null) { showMessage("Selecione um evento para excluir."); return; } if (JOptionPane.showConfirmDialog(this, "Excluir o evento selecionado?", "Confirmação", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) { manager.remove(event); saveEvents(); refreshCalendar(); refreshDayList(); } }

    private void searchEvents() {
        String keyword = JOptionPane.showInputDialog(this, "Palavra-chave:", "Buscar eventos", JOptionPane.QUESTION_MESSAGE);
        if (keyword == null) return;
        List<CalendarEvent> results = manager.search(keyword);
        JOptionPane.showMessageDialog(this, results.isEmpty() ? "Nenhum evento encontrado." : String.join("\n", results.stream().map(CalendarEvent::toString).toList()), "Resultado da busca", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showStartupReminders() {
        List<CalendarEvent> reminders = manager.remindersForNext24Hours(LocalDateTime.now());
        if (!reminders.isEmpty()) JOptionPane.showMessageDialog(this, String.join("\n", reminders.stream().map(e -> e.getReminderDateTime() + " - " + e.getTitle()).toList()), "Lembretes das próximas 24 horas", JOptionPane.INFORMATION_MESSAGE);
    }

    private void loadEvents() { try { manager.replaceAll(storage.load()); } catch (IOException e) { showMessage("Não foi possível carregar o arquivo de eventos. Um calendário vazio será aberto."); } }
    private void saveEvents() { try { storage.save(manager.getAll()); } catch (IOException e) { showMessage("Não foi possível salvar os eventos. Verifique as permissões da pasta."); } }
    private void showMessage(String message) { JOptionPane.showMessageDialog(this, message, "Calendário", JOptionPane.WARNING_MESSAGE); }
}
