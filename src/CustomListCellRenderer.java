import javax.swing.*;
import java.awt.*;

class CustomListCellRenderer extends DefaultListCellRenderer {
    private static final Color SEPARATOR_COLOR = Color.LIGHT_GRAY;
    private static final int SEPARATOR_HEIGHT = 1;

    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        label.setFont(Util.loadPoppins(24, "regular"));
        if (isSelected) {
            label.setBackground(Color.decode("#D4344D"));
            label.setForeground(Color.WHITE);
        } else {
            label.setBackground(Color.decode("#E8EBF1"));
            label.setForeground(Color.decode("#7D828B"));
        }
        label.setOpaque(true);
        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding

        panel.add(label, BorderLayout.CENTER);

        // Add separator
        if (index < list.getModel().getSize() - 1) {
            JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
            separator.setForeground(SEPARATOR_COLOR);
            separator.setPreferredSize(new Dimension(0, SEPARATOR_HEIGHT));
            panel.add(separator, BorderLayout.SOUTH);
        }

        return panel;
    }
}
