package javadsa;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class SimulationGUI extends JFrame {
    private Board1 board;
    private JPanel controlPanel;
    private JLabel statusLabel;
    private Timer timer;
    private int stepCount = 0;
    private JTextArea infoArea;
    private JButton simulateButton, vaccinateButton, quarantineButton, suggestionButton;
    private JPanel graphPanel1, graphPanel2;

    public SimulationGUI(int rows, int cols, double infectionProbability, double recoveryProbability, int actorCount) {
        setTitle("Disease Spread Simulation");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        board = new Board1(rows, cols, infectionProbability, recoveryProbability, actorCount);

        controlPanel = new JPanel();
        JButton startButton = new JButton("Start Simulation");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startSimulation();
            }
        });

        JButton stepButton = new JButton("Next Step");
        stepButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nextStep();
            }
        });

        JButton stopButton = new JButton("Stop Simulation");
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopSimulation();
            }
        });

        JButton vaccinateButton = new JButton("Vaccinate Actors");
        vaccinateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                board.vaccinateActors();
                updateStatusLabel();
                repaint();
            }
        });

        JButton immunizeButton = new JButton("Immunize Recovered Actors");
        immunizeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                board.simulateImmunization();
                updateStatusLabel();
                repaint();
            }
        });

        simulateButton = new JButton("Simulate Spread");
        vaccinateButton = new JButton("Vaccinate");
        quarantineButton = new JButton("Quarantine Infected");
        suggestionButton = new JButton("Show Suggestions");

        simulateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                board.spreadInfection();
                updateInfo();
                graphPanel1.repaint();
                graphPanel2.repaint();
            }
        });

        vaccinateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                board.vaccinateActors();
                updateInfo();
            }
        });

        quarantineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                board.quarantineInfectedActors();
                updateInfo();
            }
        });

        suggestionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSuggestions();
            }
        });

        controlPanel.add(startButton);
        controlPanel.add(stepButton);
        controlPanel.add(stopButton);
        controlPanel.add(vaccinateButton);
        controlPanel.add(immunizeButton);
        controlPanel.add(simulateButton);
        controlPanel.add(vaccinateButton);
        controlPanel.add(quarantineButton);
        controlPanel.add(suggestionButton);

        add(controlPanel, BorderLayout.NORTH);

        statusLabel = new JLabel("Status: Ready");
        add(statusLabel, BorderLayout.CENTER);

        infoArea = new JTextArea();
        infoArea.setEditable(false);
        add(new JScrollPane(infoArea), BorderLayout.SOUTH);

        graphPanel1 = new GraphPanel1(board);
        graphPanel2 = new GraphPanel2(board);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, graphPanel1, graphPanel2);
        splitPane.setDividerLocation(400);
        getContentPane().add(splitPane, BorderLayout.CENTER);

        updateStatusLabel();
        updateInfo();
    }

    private void startSimulation() {
        if (timer == null || !timer.isRunning()) {
            timer = new Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    nextStep();
                }
            });
            timer.start();
            statusLabel.setText("Status: Simulation running");
        }
    }

    private void nextStep() {
        board.spreadInfection();
        board.moveActors();
        updateStatusLabel();
        repaint();

        if (board.isSimulationComplete()) {
            stopSimulation();
            statusLabel.setText("Status: Simulation complete");
        }
    }

    private void stopSimulation() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
            statusLabel.setText("Status: Simulation stopped");
        }
    }

    private void updateStatusLabel() {
        statusLabel.setText(String.format("Status: Step %d, Healthy: %d, Sick: %d, Recovered: %d, Vaccinated: %d",
                stepCount++, board.countHealthy(), board.countSick(), board.countCured(), board.countVaccinated()));
    }

    private void updateInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Healthy: ").append(board.countHealthy()).append("\n");
        info.append("Infected: ").append(board.countSick()).append("\n");
        info.append("Cured: ").append(board.countCured()).append("\n");
        info.append("Vaccinated: ").append(board.countVaccinated()).append("\n");
        infoArea.setText(info.toString());
    }

    private void showSuggestions() {
        Actor1 actor = board.getActors().get(0); // For simplicity, get the first actor
        String suggestions = board.provideSuggestions(actor);
        JOptionPane.showMessageDialog(this, suggestions, "Suggestions for Actor", JOptionPane.INFORMATION_MESSAGE);
    }

    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SimulationGUI simulationGUI = new SimulationGUI(50, 50, 0.2, 0.1, 100);
            simulationGUI.setVisible(true);
        });
    }}
    