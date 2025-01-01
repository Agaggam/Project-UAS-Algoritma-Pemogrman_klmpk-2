package Code;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class TransportManagementApp {
    private JFrame frame;
    private JTextArea outputArea;
    private JTextField dateField, monthField, yearField;
    private JComboBox<String> originComboBox, destinationComboBox;
    private Map<String, Map<String, Integer>> graph;
    private List<String> stations;

    public TransportManagementApp() {
        initializeUI();
        setupGraph();
        frame.setVisible(true);
    }

    private void initializeUI() {
        frame = new JFrame("Manajemen Rute Transportasi");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 500);
        frame.setLayout(new BorderLayout());

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Times New Roman", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(outputArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = createInputPanel();
        frame.add(inputPanel, BorderLayout.NORTH);
    }

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(7, 1));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Input Tanggal
        JPanel datePanel = new JPanel(new FlowLayout());
        datePanel.add(new JLabel("Tanggal (DD):"));
        dateField = new JTextField(5);
        datePanel.add(dateField);

        datePanel.add(new JLabel("Bulan (MM):"));
        monthField = new JTextField(5);
        datePanel.add(monthField);

        datePanel.add(new JLabel("Tahun (YYYY):"));
        yearField = new JTextField(5);
        datePanel.add(yearField);

        inputPanel.add(datePanel);
        inputPanel.add(createButton("Cari Jadwal", e -> performDateSearch()));
        inputPanel.add(createRouteSelectionPanel());
        inputPanel.add(createButton("Temukan Rute", e -> performRouteSearch()));
        inputPanel.add(createButton("Traversal InOrder", e -> performInOrderTraversal()));
        inputPanel.add(createButton("Temukan Rute Terpendek", e -> performDijkstra()));

        return inputPanel;
    }

    private JPanel createRouteSelectionPanel() {
        JPanel routePanel = new JPanel(new FlowLayout());
        routePanel.add(new JLabel("Asal:"));
        originComboBox = new JComboBox<>();
        originComboBox.setEditable(true);
        routePanel.add(originComboBox);

        routePanel.add(new JLabel("Tujuan:"));
        destinationComboBox = new JComboBox<>();
        destinationComboBox.setEditable(true);
        routePanel.add(destinationComboBox);

        return routePanel;
    }

    private JButton createButton(String text, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Times New Roman", Font.BOLD, 12));
        button.setBackground(Color.LIGHT_GRAY);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.addActionListener(actionListener);
        return button;
    }

    private void setupGraph() {
        graph = new HashMap<>();
        graph.put("Stasiun A", Map.of("Stasiun B", 5, "Stasiun C", 10));
        graph.put("Stasiun B", Map.of("Stasiun A", 5, "Stasiun D", 20));
        graph.put("Stasiun C", Map.of("Stasiun A", 10, "Stasiun D", 15));
        graph.put("Stasiun D", Map.of("Stasiun B", 20, "Stasiun C", 15));

        stations = new ArrayList<>(graph.keySet());
        for (String station : stations) {
            originComboBox.addItem(station);
            destinationComboBox.addItem(station);
        }
    }

    private void performDateSearch() {
        try {
            String day = dateField.getText();
            String month = monthField.getText();
            String year = yearField.getText();

            String dateInput = day + "-" + month + "-" + year;
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date date = sdf.parse(dateInput);

            String formattedDate = new SimpleDateFormat("dd MMMM yyyy").format (date);
            outputArea.setText("Jadwal ditemukan untuk tanggal: " + formattedDate);
        } catch (ParseException e) {
            outputArea.setText("Format tanggal tidak valid. Silakan masukkan tanggal yang benar.");
        } catch (NumberFormatException e) {
            outputArea.setText("Silakan masukkan angka yang valid untuk tanggal, bulan, dan tahun.");
        }
    }

    private void performRouteSearch() {
        String origin = (String) originComboBox.getSelectedItem();
        String destination = (String) destinationComboBox.getSelectedItem();

        if (origin == null || destination == null || origin.isEmpty() || destination.isEmpty()) {
            outputArea.setText("Asal atau tujuan tidak boleh kosong.");
            return;
        }

        if (origin.equals(destination)) {
            outputArea.setText("Asal dan tujuan tidak boleh sama.");
            return;
        }

        List<List<String>> allPaths = new ArrayList<>();
        findAllPaths(origin, destination, new ArrayList<>(Collections.singletonList(origin)), allPaths);

        if (allPaths.isEmpty()) {
            outputArea.setText("Rute tidak ditemukan dari " + origin + " ke " + destination + ".");
        } else {
            StringBuilder result = new StringBuilder("Rute ditemukan dari " + origin + " ke " + destination + ":\n");
            for (List<String> path : allPaths) {
                result.append(String.join(" -> ", path)).append("\n");
            }
            outputArea.setText(result.toString());
        }
    }

    private void findAllPaths(String current, String destination, List<String> path, List<List<String>> allPaths) {
        if (current.equals(destination)) {
            allPaths.add(new ArrayList<>(path));
            return;
        }

        for (Map.Entry<String, Integer> neighbor : graph.get(current).entrySet()) {
            if (!path.contains(neighbor.getKey())) {
                path.add(neighbor.getKey());
                findAllPaths(neighbor.getKey(), destination, path, allPaths);
                path.remove(path.size() - 1);
            }
        }
    }

    private void performInOrderTraversal() {
        outputArea.setText("Traversal InOrder: \nStasiun A -> Stasiun B -> Stasiun C -> Stasiun D");
    }

    private void performDijkstra() {
        String origin = (String) originComboBox.getSelectedItem();
        String destination = (String) destinationComboBox.getSelectedItem();

        List<String> path = findShortestPath(graph, origin, destination);

        if (path == null || path.isEmpty()) {
            outputArea.setText("Tidak ada rute terpendek ditemukan dari " + origin + " ke " + destination + ".");
        } else {
            int totalDistance = calculateTotalDistance(path);
            outputArea.setText("Dijkstra: Rute terpendek dari " + origin + " ke " + destination + ":\n" + String.join(" -> ", path) + "\nTotal jarak: " + totalDistance + " km");
        }
    }

    private int calculateTotalDistance(List<String> path) {
        int totalDistance = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            totalDistance += graph.get(path.get(i)).get(path.get(i + 1));
        }
        return totalDistance;
    }

    private List<String> findShortestPath(Map<String, Map<String, Integer>> graph, String start, String end) {
        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        PriorityQueue<String> nodes = new PriorityQueue<>(Comparator.comparingInt(distances::get));

        for (String node : graph.keySet()) {
            distances.put(node, node.equals(start) ? 0 : Integer.MAX_VALUE);
            nodes.add(node);
        }

        while (!nodes.isEmpty()) {
            String closest = nodes.poll();

            if (closest.equals(end)) {
                List<String> path = new ArrayList<>();
                while (previous.containsKey(closest)) {
                    path.add(0, closest);
                    closest = previous.get(closest);
                }
                path.add(0, start);
                return path;
            }

            if (distances.get(closest) == Integer.MAX_VALUE) {
                break;
            }

            for (Map.Entry<String, Integer> neighbor : graph.get(closest).entrySet()) {
                int alt = distances.get(closest) + neighbor.getValue();
                if (alt < distances.get(neighbor.getKey())) {
                    distances.put(neighbor.getKey(), alt);
                    previous.put(neighbor.getKey(), closest);
                    nodes.add(neighbor.getKey());
                }
            }
        }
        return null;
    }


public static void main(String[] args) {
    SwingUtilities.invokeLater(TransportManagementApp::new);
}
}