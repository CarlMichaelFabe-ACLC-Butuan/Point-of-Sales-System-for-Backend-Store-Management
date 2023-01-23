package abstractions;

import database.InventoryModel;
import database.data.EmployeeInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public abstract class App extends JFrame {
    private final List<ResetListener> resetListeners = new ArrayList<>();
    private final List<CloseListener> closeListeners = new ArrayList<>();
    public InventoryModel inventoryModel = new InventoryModel();
    public EmployeeInfo employeeInfo;

    public void addResetListener(ResetListener resetListener) {
        resetListeners.add(resetListener);
    }

    public void addCloseListener(CloseListener closeListener) {
        closeListeners.add(closeListener);
    }

    public void resetApp() {
        for (ResetListener resetListener : resetListeners) {
            resetListener.reset();
        }
    }

    public void closeApp() {
        for (CloseListener closeListener : closeListeners) {
            closeListener.close();
        }
    }

    public void highlightComponent(Component component) {
        Toolkit.getDefaultToolkit().beep();
        final Color backup = component.getBackground();
        final Timer t = new Timer(100, new ActionListener() {

            private int counter;

            @Override
            public void actionPerformed(ActionEvent e) {
                counter++;
                if (counter == 8) {
                    ((Timer) e.getSource()).stop();
                }
                if (counter % 2 == 0) {
                    component.setBackground(backup);
                    return;
                }
                component.setBackground(Color.GRAY);
            }
        });
        t.start();
    }

    public void showErrorDialog(Component parentComponent, String message) {
        Toolkit.getDefaultToolkit().beep();
        JOptionPane.showMessageDialog(parentComponent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean showConfirmDialog(Component parentComponent, String message, String title) {
        int dialogResult = JOptionPane.showConfirmDialog(parentComponent, message, title, JOptionPane.YES_NO_OPTION);
        return dialogResult == JOptionPane.YES_OPTION;
    }
}
