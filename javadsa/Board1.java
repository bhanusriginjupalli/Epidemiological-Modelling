package javadsa; 
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Board1 {
    private List<Actor1> actors;
    private Graph<Actor1> actorGraph;
    private BinaryTree<Actor1> actorSearchTree;
    
    private int maxRow;
    
    private int maxCol;
    private Lock lock = new ReentrantLock();
    private Random rand = new Random();
    private int stepCount = 0;

    public Board1(int maxRow, int maxCol, double infectionProbability, double recoveryProbability, int actorCount) {
        this.maxRow = maxRow;
        this.maxCol = maxCol;
        actors = new ArrayList<>();
        actorGraph = new Graph<>();
        actorSearchTree = new BinaryTree<>();
        initializeActors(infectionProbability, recoveryProbability, actorCount);
        connectActors();
        buildActorSearchTree();
    }

    private void initializeActors(double infectionProbability, double recoveryProbability, int actorCount) {
        for (int i = 0; i < actorCount; i++) {
            int row = rand.nextInt(maxRow);
            int col = rand.nextInt(maxCol);
            Actor1.State state = (i < actorCount / 10) ? Actor1.State.INFECTED : Actor1.State.SUSCEPTIBLE;
            Actor1 actor = new Actor1(state, row, col, infectionProbability, recoveryProbability);
            actors.add(actor);
            try {
                DatabaseUtil.insertActor(actor);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public int getMaxCol() {
        return maxCol;
    }

    public int getMaxRow() {
        return maxRow;
    }

    private void connectActors() {
        for (Actor1 actor : actors) {
            for (Actor1 other : actors) {
                if (actor != other && areNeighbors(actor, other)) {
                    actorGraph.addEdge(actor, other);
                }
            }
        }
    }

    private boolean areNeighbors(Actor1 a1, Actor1 a2) {
        int dx = Math.abs(a1.getRow() - a2.getRow());
        int dy = Math.abs(a1.getCol() - a2.getCol());
        return (dx <= 1 && dy <= 1);
    }

    private void buildActorSearchTree() {
        for (Actor1 actor : actors) {
            actorSearchTree.insert(actor);
        }
    }

    public void vaccinateActors() {
        lock.lock();
        try {
            for (Actor1 actor : actors) {
                if (actor.getState() == Actor1.State.SUSCEPTIBLE) {
                    actor.setVaccinated(true);
                    try {
                        DatabaseUtil.updateActor(actor);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public void simulateStep() {
        lock.lock();
        try {
            spreadInfection();
            moveActors();
            quarantineInfectedActors();

            // Count the state of actors and store the step information
            int healthyCount = countHealthy();
            int sickCount = countSick();
            int recoveredCount = countCured();
            int vaccinatedCount = countVaccinated();
            try {
                DatabaseUtil.insertSimulationStep(stepCount++, healthyCount, sickCount, recoveredCount, vaccinatedCount);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } finally {
            lock.unlock();
        }
    }

    public void spreadInfection() {
        List<Actor1> newlyInfected = new ArrayList<>();
        for (Actor1 actor : actors) {
            if (actor.getState() == Actor1.State.INFECTED) {
                for (Actor1 neighbor : actorGraph.getNeighbors(actor)) {
                    if (neighbor.getState() == Actor1.State.SUSCEPTIBLE && !neighbor.isVaccinated()) {
                        if (rand.nextDouble() < actor.getInfectionProbability()) {
                            newlyInfected.add(neighbor);
                        }
                    }
                }
            }
        }
        for (Actor1 actor : newlyInfected) {
            actor.setState(Actor1.State.INFECTED);
            try {
                DatabaseUtil.updateActor(actor);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public void simulateImmunization() {
        lock.lock();
        try {
            for (Actor1 actor : actors) {
                if (actor.getState() == Actor1.State.RECOVERED) {
                    actor.setVaccinated(true);
                    try {
                        DatabaseUtil.updateActor(actor);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public void moveActors() {
        for (Actor1 actor : actors) {
            int newRow = Math.max(0, Math.min(maxRow - 1, actor.getRow() + rand.nextInt(3) - 1));
            int newCol = Math.max(0, Math.min(maxCol - 1, actor.getCol() + rand.nextInt(3) - 1));
            actor.setRow(newRow);
            actor.setCol(newCol);
            try {
                DatabaseUtil.updateActor(actor);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void quarantineInfectedActors() {
        for (Actor1 actor : actors) {
            if (actor.getState() == Actor1.State.INFECTED) {
                actor.setQuarantined(true);
                try {
                    DatabaseUtil.updateActor(actor);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean isSimulationComplete() {
        for (Actor1 actor : actors) {
            if (actor.getState() == Actor1.State.INFECTED) {
                return false;
            }
        }
        return true;
    }

    public List<Actor1> getActors() {
        return actors;
    }

    public int countHealthy() {
        int count = 0;
        for (Actor1 actor : actors) {
            if (actor.getState() == Actor1.State.SUSCEPTIBLE && !actor.isVaccinated()) {
                count++;
            }
        }
        return count;
    }

    public int countSick() {
        int count = 0;
        for (Actor1 actor : actors) {
            if (actor.getState() == Actor1.State.INFECTED) {
                count++;
            }
        }
        return count;
    }

    public int countCured() {
        int count = 0;
        for (Actor1 actor : actors) {
            if (actor.getState() == Actor1.State.RECOVERED) {
                count++;
            }
        }
        return count;
    }

    public int countVaccinated() {
        int count = 0;
        for (Actor1 actor : actors) {
            if (actor.isVaccinated()) {
                count++;
            }
        }
        return count;
    }
    

    public String provideSuggestions(Actor1 actor) {
        // Provide suggestions for an actor based on the current state and environment
        String suggestions = "Suggestions for Actor:\n";
        if (actor.getState() == Actor1.State.INFECTED) {
            suggestions += "- Stay in quarantine to avoid spreading the infection.\n";
        } else if (actor.getState() == Actor1.State.SUSCEPTIBLE && !actor.isVaccinated()) {
            suggestions += "- Get vaccinated to reduce the risk of infection.\n";
        } else if (actor.getState() == Actor1.State.RECOVERED) {
            suggestions += "- Continue following health guidelines to avoid reinfection.\n";
        }
        return suggestions;
    }
    
  

    public class Graph<T> {
        private Map<T, List<T>> adjacencyList = new HashMap<>();

        public void addEdge(T source, T destination) {
            adjacencyList.computeIfAbsent(source, k -> new ArrayList<>()).add(destination);
            adjacencyList.computeIfAbsent(destination, k -> new ArrayList<>()).add(source);
        }

        public List<T> getNeighbors(T node) {
            return adjacencyList.getOrDefault(node, new ArrayList<>());
        }
    }
    public class BinaryTree<T extends Comparable<T>> {
        private class Node {
            T data;
            Node left, right;

            Node(T data) {
                this.data = data;
                left = right = null;
            }
        }

        private Node root;

        public void insert(T data) {
            root = insertRec(root, data);
        }

        private Node insertRec(Node root, T data) {
            if (root == null) {
                root = new Node(data);
                return root;
            }

            if (data.compareTo(root.data) < 0) {
                root.left = insertRec(root.left, data);
            } else if (data.compareTo(root.data) > 0) {
                root.right = insertRec(root.right, data);
            }

            return root;
        }
    }

}
