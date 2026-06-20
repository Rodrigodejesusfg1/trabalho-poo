package br.usp.scc0504.calendario.ui;

import br.usp.scc0504.calendario.model.CalendarEvent;
import br.usp.scc0504.calendario.model.EventCategory;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

/** Janela modal usada para criar ou editar um evento. */
public class EventDialog extends JDialog {
    private final JTextField titleField = new JTextField(25);
    private final JTextField dateField = new JTextField(10);
    private final JTextField timeField = new JTextField(5);
    private final JTextField locationField = new JTextField(25);
    private final JTextArea descriptionArea = new JTextArea(4, 25);
    private final JComboBox<EventCategory> categoryBox = new JComboBox<>(EventCategory.values());
    private final JComboBox<ReminderOption> reminderBox = new JComboBox<>(ReminderOption.values());
    private CalendarEvent result;

    public EventDialog(Window owner, LocalDate selectedDate, CalendarEvent event) {
        super(owner, event == null ? "Novo evento" : "Editar evento", ModalityType.APPLICATION_MODAL);
        buildLayout();
        fill(selectedDate, event);
        pack();
        setLocationRelativeTo(owner);
    }

    private void buildLayout() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4); gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.HORIZONTAL;
        addRow(form, gbc, 0, "Título*:", titleField); addRow(form, gbc, 1, "Data (AAAA-MM-DD)*:", dateField);
        addRow(form, gbc, 2, "Hora (HH:MM)*:", timeField); addRow(form, gbc, 3, "Local:", locationField);
        addRow(form, gbc, 4, "Categoria:", categoryBox); addRow(form, gbc, 5, "Lembrete:", reminderBox);
        gbc.gridx = 0; gbc.gridy = 6; form.add(new JLabel("Descrição:"), gbc);
        gbc.gridx = 1; form.add(new JScrollPane(descriptionArea), gbc);
        JButton save = new JButton("Salvar"); save.addActionListener(e -> saveEvent());
        JButton cancel = new JButton("Cancelar"); cancel.addActionListener(e -> dispose());
        JPanel buttons = new JPanel(); buttons.add(save); buttons.add(cancel);
        add(form, BorderLayout.CENTER); add(buttons, BorderLayout.SOUTH);
    }

    private void addRow(JPanel form, GridBagConstraints gbc, int y, String label, JComponent component) {
        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0; form.add(new JLabel(label), gbc);
        gbc.gridx = 1; gbc.weightx = 1; form.add(component, gbc);
    }

    private void fill(LocalDate selectedDate, CalendarEvent event) {
        dateField.setText(selectedDate.toString()); timeField.setText("09:00");
        if (event != null) {
            titleField.setText(event.getTitle()); dateField.setText(event.getDate().toString()); timeField.setText(event.getTime().toString());
            locationField.setText(event.getLocation()); descriptionArea.setText(event.getDescription()); categoryBox.setSelectedItem(event.getCategory());
            reminderBox.setSelectedItem(ReminderOption.fromHours(event.getReminderLeadHours())); result = event;
        }
    }

    private void saveEvent() {
        try {
            String title = titleField.getText(); LocalDate date = LocalDate.parse(dateField.getText().trim());
            LocalTime time = LocalTime.parse(timeField.getText().trim()); EventCategory category = (EventCategory) categoryBox.getSelectedItem();
            int hours = ((ReminderOption) reminderBox.getSelectedItem()).hours;
            if (result == null) result = new CalendarEvent(title, date, time, locationField.getText(), descriptionArea.getText(), category, hours);
            else { result.setTitle(title); result.setDate(date); result.setTime(time); result.setLocation(locationField.getText()); result.setDescription(descriptionArea.getText()); result.setCategory(category); result.setReminderLeadHours(hours); }
            dispose();
        } catch (DateTimeParseException ex) { JOptionPane.showMessageDialog(this, "Informe data e hora válidas nos formatos indicados.", "Dados inválidos", JOptionPane.WARNING_MESSAGE);
        } catch (IllegalArgumentException ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Dados inválidos", JOptionPane.WARNING_MESSAGE); }
    }

    public CalendarEvent getResult() { return result; }

    private enum ReminderOption {
        ONE_DAY("1 dia antes", 24), THREE_DAYS("3 dias antes", 72), ONE_WEEK("1 semana antes", 168), NONE("No horário do evento", 0);
        private final String label; private final int hours;
        ReminderOption(String label, int hours) { this.label = label; this.hours = hours; }
        static ReminderOption fromHours(int hours) { for (ReminderOption o : values()) if (o.hours == hours) return o; return ONE_DAY; }
        @Override public String toString() { return label; }
    }
}
