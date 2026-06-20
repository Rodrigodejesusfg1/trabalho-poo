package br.usp.scc0504.calendario;

import br.usp.scc0504.calendario.ui.CalendarFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/** Classe de entrada da aplicação de calendário. */
public final class App {
    private App() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException e) {
                // Mantém o visual padrão do Swing caso o tema do sistema não esteja disponível.
            }
            new CalendarFrame().setVisible(true);
        });
    }
}
