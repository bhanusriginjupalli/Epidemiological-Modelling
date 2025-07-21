package javadsa;
import java.sql.*;

public class DatabaseUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/disease_simulation";
    private static final String USER = "root";
    private static final String PASSWORD = "mysql2024";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void insertActor(Actor1 actor) throws SQLException {
    	String query = "INSERT INTO actor (state, `row`, `col`, infection_probability, recovery_probability, vaccinated, quarantined) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = getConnection(); PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, actor.getState().name());
            ps.setInt(2, actor.getRow());
            ps.setInt(3, actor.getCol());
            ps.setDouble(4, actor.getInfectionProbability());
            ps.setDouble(5, actor.getRecoveryProbability());
            ps.setBoolean(6, actor.isVaccinated());
            ps.setBoolean(7, actor.isQuarantined());
            ps.executeUpdate();
            
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    actor.setId(generatedKeys.getInt(1));
                }
            }
        }
    }
    public static void updateActor(Actor1 actor) throws SQLException {
    	String query = "UPDATE actor SET state = ?, `row` = ?, `col` = ?, vaccinated = ?, quarantined = ? WHERE id = ?";
        try (Connection connection = getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, actor.getState().name());
            ps.setInt(2, actor.getRow());
            ps.setInt(3, actor.getCol());
            ps.setBoolean(4, actor.isVaccinated());
            ps.setBoolean(5, actor.isQuarantined());
            ps.setInt(6, actor.getId());
            ps.executeUpdate();
        }
    }


    public static void insertSimulationStep(int step, int healthyCount, int infectedCount, int recoveredCount, int vaccinatedCount) throws SQLException {
        String query = "INSERT INTO simulation_step (step, healthy_count, infected_count, recovered_count, vaccinated_count) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = getConnection(); PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, step);
            ps.setInt(2, healthyCount);
            ps.setInt(3, infectedCount);
            ps.setInt(4, recoveredCount);
            ps.setInt(5, vaccinatedCount);
            ps.executeUpdate();
        }
    }
}

