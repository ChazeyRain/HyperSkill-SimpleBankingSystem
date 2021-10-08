package banking.cardManagement;

import org.sqlite.SQLiteDataSource;

import javax.management.OperationsException;
import java.io.File;
import java.io.IOException;
import java.sql.*;

public class CardDataBase {

    private static final String defaultPath = "sample.db";
    private static CardDataBase currentDataBase = new CardDataBase(defaultPath);

    private final SQLiteDataSource dataSource;

    private CardDataBase(String path) {
        dataSource = new SQLiteDataSource();
        dataSource.setUrl("jdbc:sqlite:" + path);
    }

    public boolean createNewCard(String number, String pin) {
        String query = "INSERT INTO card (number, pin) VALUES" +
                "(?, ?);";
        int affectedRows = 0;

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, number);
                statement.setString(2, pin);

                affectedRows = statement.executeUpdate();
            }
        } catch (SQLException e) {
            return false;
        }

        return 0 != affectedRows;
    }

    public Customer getCustomerData(String number, String pin) {
         String query = "SELECT id, number " +
                "FROM card " +
                "WHERE number = ? " +
                "AND " +
                "pin = ?;";

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(query)) {

                statement.setString(1, number);
                statement.setString(2, pin);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.isClosed()) {
                    return null;
                }
                return new Customer(dataSource, resultSet.getInt("id"), resultSet.getString("number"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isCardExist(String number) {
        String input = "SELECT number FROM card WHERE number = ?;";

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(input)) {
                statement.setString(1, number);

                ResultSet resultSet = statement.executeQuery();

                return !resultSet.isClosed();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void getAccessToDataBase(String path) {
        File file = new File(path);

        try {
            if(file.createNewFile()) {
                SQLiteDataSource dataSource = new SQLiteDataSource();
                dataSource.setUrl("jdbc:sqlite:" + path);
                try (Connection connection = dataSource.getConnection()) {
                    try (Statement statement = connection.createStatement()) {
                        statement.execute("CREATE TABLE card(" +
                                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                "number TEXT UNIQUE," +
                                "pin TEXT," +
                                "balance INTEGER DEFAULT 0);");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        currentDataBase = new CardDataBase(path);
    }

    public static CardDataBase getCurrentDataBase() {
        return currentDataBase;
    }
}
