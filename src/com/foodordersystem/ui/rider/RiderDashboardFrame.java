package com.foodordersystem.ui.rider;

import com.foodordersystem.database.OrderDatabase;
import com.foodordersystem.model.entities.Order;
import com.foodordersystem.model.history.RiderHistory;
import com.foodordersystem.model.entities.User;
import com.foodordersystem.ui.common.BaseFrame;
import com.foodordersystem.ui.common.ImagePanel;
import com.foodordersystem.ui.common.RoleSelectionFrame;
import com.foodordersystem.ui.common.RoundedButton;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RiderDashboardFrame extends BaseFrame {
    private final User rider;
    private OrderDatabase orderDatabase;
    private JPanel ordersPanel;
    private final JLabel deliveryCountLabel;
    private int lastOrderCount = 0;

    public RiderDashboardFrame(User rider) {
        super("Rider Dashboard - " + rider.getName(), 900, 700);
        this.rider = rider;
        this.orderDatabase = new OrderDatabase();
        this.deliveryCountLabel = new JLabel("Total Deliveries: 0");
        initComponents();

        Timer timer = new Timer(10000, e -> checkForNewOrders());
        timer.start();
    }

    protected void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        ImagePanel backgroundPanel = new ImagePanel("/com/foodordersystem/Resources/ManagementDashboardFrameBg.png", 0.9f);
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        // Custom UI for the tabbed pane
        UIManager.put("TabbedPane.contentOpaque", false);
        UIManager.put("TabbedPane.background", new Color(0, 0, 0, 120));
        UIManager.put("TabbedPane.foreground", Color.WHITE);
        UIManager.put("TabbedPane.selected", new Color(79, 77, 77));
        UIManager.put("TabbedPane.focus", new Color(255, 102, 0, 50));
        UIManager.put("TabbedPane.borderHightlightColor", Color.DARK_GRAY);
        UIManager.put("TabbedPane.darkShadow", Color.DARK_GRAY);
        UIManager.put("TabbedPane.light", Color.DARK_GRAY);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setOpaque(false);
        tabbedPane.setFont(new Font("Dialog", Font.BOLD, 14));
        tabbedPane.addTab("Live Deliveries", createLiveDeliveriesPanel());
        tabbedPane.addTab("Delivery History", createHistoryPanel());

        backgroundPanel.add(tabbedPane, BorderLayout.CENTER);

        updateDeliveryCount();
    }

    private JPanel createLiveDeliveriesPanel() {
        JPanel liveDeliveriesPanel = new JPanel(new BorderLayout(10, 10));
        liveDeliveriesPanel.setOpaque(false);

        JPanel headerPanel = new JPanel(new BorderLayout(20, 10));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Available Deliveries");
        titleLabel.setFont(new Font("DialogInput", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel rightHeaderPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightHeaderPanel.setOpaque(false);

        deliveryCountLabel.setFont(new Font("Dialog", Font.BOLD, 16));
        deliveryCountLabel.setForeground(Color.WHITE);
        rightHeaderPanel.add(deliveryCountLabel);

        JButton refreshButton = new RoundedButton("Refresh");
        styleHeaderButton(refreshButton);
        refreshButton.addActionListener(e -> refreshOrderList());
        rightHeaderPanel.add(refreshButton);

        JButton logoutButton = new RoundedButton("Logout");
        logoutButton.setBackground(Color.BLACK);
        styleHeaderButton(logoutButton);
        logoutButton.addActionListener(e -> {
            new RoleSelectionFrame().setVisible(true);
            dispose();
        });
        rightHeaderPanel.add(logoutButton);

        headerPanel.add(rightHeaderPanel, BorderLayout.EAST);
        liveDeliveriesPanel.add(headerPanel, BorderLayout.NORTH);

        ordersPanel = new JPanel();
        ordersPanel.setOpaque(false);
        ordersPanel.setLayout(new BoxLayout(ordersPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(ordersPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        liveDeliveriesPanel.add(scrollPane, BorderLayout.CENTER);

        refreshOrderList();

        return liveDeliveriesPanel;
    }

    private JPanel createHistoryPanel() {
        JPanel historyPanel = new JPanel(new BorderLayout(10, 10));
        historyPanel.setOpaque(false);
        historyPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setOpaque(false);
        JComboBox<String> dateFilter = new JComboBox<>(new String[]{"All Time", "Today", "This Week", "This Month"});
        topPanel.add(new JLabel("Filter by:") {{ setForeground(Color.WHITE); }});
        topPanel.add(dateFilter);
        historyPanel.add(topPanel, BorderLayout.NORTH);

        String[] columnNames = {"Order ID", "Restaurant", "Date", "Earnings (Bdt)"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable historyTable = new JTable(tableModel);
        styleTable(historyTable);

        JScrollPane scrollPane = new JScrollPane(historyTable);
        styleScrollPane(scrollPane);
        historyPanel.add(scrollPane, BorderLayout.CENTER);

        JLabel totalEarningsLabel = new JLabel("Total Earnings: Bdt 0.00");
        totalEarningsLabel.setFont(new Font("Dialog", Font.BOLD, 16));
        totalEarningsLabel.setForeground(Color.ORANGE);
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        bottomPanel.add(totalEarningsLabel);
        historyPanel.add(bottomPanel, BorderLayout.SOUTH);

        dateFilter.addActionListener(e -> {
            updateHistoryTable(tableModel, totalEarningsLabel, (String) dateFilter.getSelectedItem());
        });

        updateHistoryTable(tableModel, totalEarningsLabel, "All Time");

        return historyPanel;
    }

    private void updateHistoryTable(DefaultTableModel model, JLabel earningsLabel, String filter) {
        model.setRowCount(0);
        List<RiderHistory.DeliveryRecord> history = new RiderHistory().getHistory(rider.getUsername());
        double totalEarnings = 0;
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        List<RiderHistory.DeliveryRecord> filteredHistory = history.stream().filter(record -> {
            LocalDate recordDate = record.getDeliveryDate().toLocalDate();
            switch (filter) {
                case "Today": return recordDate.isEqual(now);
                case "This Week": return !recordDate.isBefore(now.minusWeeks(1));
                case "This Month": return recordDate.getMonth() == now.getMonth() && recordDate.getYear() == now.getYear();
                default: return true;
            }
        }).collect(Collectors.toList());

        for (RiderHistory.DeliveryRecord record : filteredHistory) {
            model.addRow(new Object[]{
                    record.getOrderId().substring(0, 8).toUpperCase(),
                    record.getRestaurantName(),
                    record.getDeliveryDate().format(formatter),
                    String.format("%.2f", record.getEarnings())
            });
            totalEarnings += record.getEarnings();
        }

        earningsLabel.setText(String.format("Total Earnings: Bdt %.2f", totalEarnings));
    }


    public void refreshOrderList() {
        orderDatabase = new OrderDatabase();
        ordersPanel.removeAll();
        List<Order> placedOrders = orderDatabase.getAllOrders();

        if (placedOrders == null) {
            placedOrders = Collections.emptyList();
        }

        List<Order> filteredOrders = placedOrders.stream()
                .filter(Objects::nonNull)
                .filter(o -> "Placed".equalsIgnoreCase(o.getStatus()))
                .collect(Collectors.toList());

        if (filteredOrders.isEmpty()) {
            JLabel noOrdersLabel = new JLabel("No available orders at the moment.");
            noOrdersLabel.setFont(new Font("Dialog", Font.ITALIC, 18));
            noOrdersLabel.setForeground(Color.LIGHT_GRAY);
            noOrdersLabel.setHorizontalAlignment(SwingConstants.CENTER);
            ordersPanel.add(noOrdersLabel);
        } else {
            for (Order order : filteredOrders) {
                ordersPanel.add(new OrderCardPanel(order));
                ordersPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            }
        }
        lastOrderCount = filteredOrders.size();
        ordersPanel.revalidate();
        ordersPanel.repaint();
    }

    private void checkForNewOrders() {
        List<Order> currentOrders = new OrderDatabase().getAllOrders().stream()
                .filter(o -> "Placed".equalsIgnoreCase(o.getStatus()))
                .collect(Collectors.toList());

        if (currentOrders.size() > lastOrderCount) {
            JOptionPane.showMessageDialog(this, "A new delivery is available!", "New Order", JOptionPane.INFORMATION_MESSAGE);
            refreshOrderList();
        }
        lastOrderCount = currentOrders.size();
    }


    public void updateDeliveryCount() {
        RiderHistory riderHistory = new RiderHistory();
        int count = riderHistory.getDeliveryCount(rider.getUsername());
        deliveryCountLabel.setText("Total Deliveries: " + count);
    }

    private void styleHeaderButton(JButton button) {
        button.setFont(new Font("Dialog", Font.BOLD, 12));
        button.setBackground(new Color(255, 102, 0));
        button.setForeground(Color.WHITE);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(90, 35));
    }

    private void styleTable(JTable table) {
        table.getTableHeader().setFont(new Font("Dialog", Font.BOLD, 16));
        table.getTableHeader().setBackground(new Color(30, 30, 30));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setFont(new Font("Dialog", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setBackground(new Color(50, 50, 50));
        table.setForeground(Color.WHITE);
        table.setSelectionBackground(Color.ORANGE);
        table.setSelectionForeground(Color.BLACK);
        table.setFillsViewportHeight(true);
    }

    private void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(80,80,80)));
    }


    private class OrderCardPanel extends JPanel {
        public OrderCardPanel(Order order) {
            setLayout(new BorderLayout(15, 10));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

            setOpaque(false);

            setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

            JPanel detailsPanel = new JPanel(new GridLayout(3, 1, 0, 5));
            detailsPanel.setOpaque(false);

            JLabel fromLabel = new JLabel(String.format("Pickup: %s, %s",
                    order.getRestaurantName() != null ? order.getRestaurantName() : "N/A",
                    order.getRestaurantLocation() != null ? order.getRestaurantLocation() : "N/A"));
            fromLabel.setFont(new Font("Dialog", Font.BOLD, 16));
            fromLabel.setForeground(Color.WHITE);

            JLabel toLabel = new JLabel("Deliver to: " + (order.getCustomerAddress() != null ? order.getCustomerAddress() : "N/A"));
            toLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
            toLabel.setForeground(Color.LIGHT_GRAY);

            JLabel idLabel = new JLabel("ID: " + (order.getOrderId() != null ? order.getOrderId().substring(0, 8).toUpperCase() : "N/A"));
            idLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
            idLabel.setForeground(Color.GRAY);

            detailsPanel.add(fromLabel);
            detailsPanel.add(toLabel);
            detailsPanel.add(idLabel);

            JButton pickupButton = new RoundedButton("View Details");
            pickupButton.setFont(new Font("Dialog", Font.BOLD, 12));
            pickupButton.setBackground(new Color(45, 137, 45));
            pickupButton.setForeground(Color.WHITE);
            pickupButton.setPreferredSize(new Dimension(120, 35));

            pickupButton.addActionListener(e -> {
                new OrderPickupFrame(order, RiderDashboardFrame.this, rider).setVisible(true);
            });

            add(detailsPanel, BorderLayout.CENTER);
            add(pickupButton, BorderLayout.EAST);

        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0, 0, 0, 160));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}