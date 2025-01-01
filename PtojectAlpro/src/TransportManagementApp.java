import javax.swing.*;
import java.awt.*;
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
        frame = new JFrame("Transport Route Management");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 500);
        frame.setLayout(new BorderLayout());

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(7, 1));

        // Input for Date, Month, and Year
        JPanel datePanel = new JPanel(new FlowLayout());
        datePanel.add(new JLabel("Date (DD):"));
        dateField = new JTextField(5);
        datePanel.add(dateField);

        datePanel.add(new JLabel("Month (MM):"));
        monthField = new JTextField(5);
        datePanel.add(monthField);

        datePanel.add(new JLabel("Year (YYYY):"));
        yearField = new JTextField(5);
        datePanel.add(yearField);

        inputPanel.add(datePanel);

        JButton dateSearchButton = new JButton("Search Schedule");
        dateSearchButton.addActionListener(e -> performDateSearch());
        inputPanel.add(dateSearchButton);

        // Input for Origin and Destination
        JPanel routePanel = new JPanel(new FlowLayout());
        routePanel.add(new JLabel("Origin:"));
        originComboBox = new JComboBox<>();
        originComboBox.setEditable(true);
        routePanel.add(originComboBox);

        routePanel.add(new JLabel("Destination:"));
        destinationComboBox = new JComboBox<>();
        destinationComboBox.setEditable(true);
        routePanel.add(destinationComboBox);

        inputPanel.add(routePanel);

        JButton routeSearchButton = new JButton("Find Route");
        routeSearchButton.addActionListener(e -> performRouteSearch());
        inputPanel.add(routeSearchButton);

        JButton inorderButton = new JButton("InOrder Traversal");
        inorderButton.addActionListener(e -> performInOrderTraversal());
        inputPanel.add(inorderButton);

        JButton dijkstraButton = new JButton("Find Shortest Route");
        dijkstraButton.addActionListener(e -> performDijkstra());
        inputPanel.add(dijkstraButton);

        frame.add(inputPanel, BorderLayout.NORTH);

        setupGraph();
        frame.setVisible(true);
    }

    private void setupGraph() {
        graph = new HashMap<>();
        graph.put("Station A", Map.of("Station B", 5, "Station C", 10));
        graph.put("Station B", Map.of("Station A", 5, "Station D", 20));
        graph.put("Station C", Map.of("Station A", 10, "Station D", 15));
        graph.put("Station D", Map.of("Station B", 20, "Station C", 15));

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

            String formattedDate = new SimpleDateFormat("dd MMMM yyyy").format(date);
            outputArea.setText("Schedule found for date: " + formattedDate);
        } catch (ParseException e) {
            outputArea.setText("Invalid date format. Please enter a valid date.");
        }
    }

    private void performRouteSearch() {
        String origin = (String) originComboBox.getSelectedItem();
        String destination = (String) destinationComboBox.getSelectedItem();

        if (origin == null || destination == null || origin.isEmpty() || destination.isEmpty()) {
            outputArea.setText("Origin or destination cannot be empty.");
            return;
        }

        List<String> path = findShortestPath(graph, origin, destination);

        if (path == null || path.isEmpty()) {
            outputArea.setText("Route not found from " + origin + " to " + destination + ".");
        } else {
            outputArea.setText("Shortest route from " + origin + " to " + destination + ":\n" + String.join(" -> ", path));
        }
    }

    private void performInOrderTraversal() {
        outputArea.setText("InOrder Traversal: \nStation A -> Station B -> Station C -> Station D");
    }

    private void performDijkstra() {
        String origin = (String) originComboBox.getSelectedItem();
        String destination = (String) destinationComboBox.getSelectedItem();

        List<String> path = findShortestPath(graph, origin, destination);

        if (path == null || path.isEmpty()) {
            outputArea.setText("No shortest route found from " + origin + " to " + destination + ".");
        } else {
            outputArea.setText("Dijkstra: Shortest route from " + origin + " to " + destination + ":\n" + String.join(" -> ", path));
        }
    }

    private List<String> findShortestPath(Map<String, Map<String, Integer>> graph, String start, String end) {
        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        PriorityQueue<String> nodes = new PriorityQueue<>(Comparator.comparingInt(distances::get));

        for (String node : graph.keySet()) {
            if (node.equals(start)) {
                distances.put(node, 0);
            } else {
                distances.put(node, Integer.MAX_VALUE);
            }
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
