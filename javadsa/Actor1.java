package javadsa;
 
public class Actor1 implements Comparable<Actor1> {
    public enum State {
        SUSCEPTIBLE, INFECTED, REMOVED, RECOVERED
    }

    private int id;
    private State state;
    private int row;
    private int col;
    private double infectionProbability;
    private double recoveryProbability;
    private boolean vaccinated;
    private boolean quarantined;

    // Constructor
    public Actor1(State state, int row, int col, double infectionProbability, double recoveryProbability) {
        this.state = state;
        this.row = row;
        this.col = col;
        this.infectionProbability = infectionProbability;
        this.recoveryProbability = recoveryProbability;
        this.vaccinated = false;
        this.quarantined = false;
    }

    // Getters and setters for all fields including ID
    @Override
    public int compareTo(Actor1 other) {
        return Integer.compare(this.id, other.id);
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public double getInfectionProbability() {
        return infectionProbability;
    }

    public void setInfectionProbability(double infectionProbability) {
        this.infectionProbability = infectionProbability;
    }

    public double getRecoveryProbability() {
        return recoveryProbability;
    }

    public void setRecoveryProbability(double recoveryProbability) {
        this.recoveryProbability = recoveryProbability;
    }

    public boolean isVaccinated() {
        return vaccinated;
    }

    public void setVaccinated(boolean vaccinated) {
        this.vaccinated = vaccinated;
    }

    public boolean isQuarantined() {
        return quarantined;
    }

    public void setQuarantined(boolean quarantined) {
        this.quarantined = quarantined;
    }
}
