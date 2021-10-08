package banking.cardManagement;

import javax.sql.DataSource;
import java.sql.*;

public class Customer {

    private final DataSource dataSource;
    private final int id;
    private final String number;

    Customer(DataSource dataSource, int id, String number) {
        this.dataSource = dataSource;
        this.id = id;
        this.number = number;
    }

    public String getNumber() {
        return number;
    }

    public long getBalance() {
        String input = "SELECT balance FROM card WHERE id = ?;";
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(input)) {
                statement.setInt(1, id);
                ResultSet resultSet = statement.executeQuery();
                return resultSet.getLong("balance");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public boolean addIncome(long income) {
        String input = "UPDATE card SET balance = balance + ? WHERE id = ?;";

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(input)) {
                statement.setLong(1, income);
                statement.setInt(2, id);

                return 0 != statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean doTransfer(String number, long amount) {
        String withdraw = "UPDATE card " +
                "SET balance = balance - ? " +
                "WHERE id = ?;";
        String checkBalance = "SELECT balance FROM card " +
                "WHERE id = ?;";
        String transferMoney = "UPDATE card " +
                "SET balance = balance + ? " +
                "WHERE number = ?;";

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            long remain = 0;
            Savepoint savepoint = connection.setSavepoint();


            try (PreparedStatement statement = connection.prepareStatement(withdraw)) {
                statement.setLong(1, amount);
                statement.setInt(2, id);

                statement.executeUpdate();
            }

            try (PreparedStatement statement = connection.prepareStatement(checkBalance)) {
                statement.setInt(1, id);

                ResultSet resultSet = statement.executeQuery();
                remain = resultSet.getLong("balance");
            }

            if (remain < 0) {
                connection.rollback(savepoint);

                return false;
            } else {
                try (PreparedStatement statement = connection.prepareStatement(transferMoney)) {
                    statement.setLong(1, amount);
                    statement.setString(2, number);

                    statement.executeUpdate();
                }
                connection.commit();
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void closeAccount() {
        String input = "DELETE FROM card WHERE id = ?;";

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(input)) {
                statement.setInt(1, id);

                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
