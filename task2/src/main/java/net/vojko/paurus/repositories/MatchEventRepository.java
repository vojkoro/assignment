package net.vojko.paurus.repositories;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import net.vojko.paurus.models.TimingInfo;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.StringReader;
import java.sql.*;

@ApplicationScoped
public class MatchEventRepository {

    @Inject
    DataSource dataSource;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MatchEventRepository.class);

    public void copyInsertMatchEvent(String csvData) {
        String sql = "COPY match_event(match_id, market_id, outcome_id, specifiers, sequence_number, processing_time ) FROM STDIN WITH (FORMAT csv)";
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);

            BaseConnection pgConnection = connection.unwrap(BaseConnection.class);
            try (var reader = new StringReader(csvData)) {
                CopyManager copyManager = new CopyManager(pgConnection);
                copyManager.copyIn(sql, reader);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            connection.commit();
        } catch (SQLException e) {
            logger.error("Error executing COPY command", e);
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                logger.error("Error rolling back transaction", ex);
            }
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error("Error closing connection", e);
                }
            }
        }
    }


    public TimingInfo getTiming() {
        String sql = "select min(timestamp), max(timestamp), count(*) from match_event;";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    var startTime = resultSet.getTimestamp(1);
                    var endTime = resultSet.getTimestamp(2);
                    var elementCount = resultSet.getLong(3);
                    return new TimingInfo(
                            startTime,
                            endTime,
                            elementCount,
                            endTime.getTime() - startTime.getTime());
                }
            }

        } catch (SQLException e) {
            logger.error("Error executing query", e);
        }
        return null;
    }

    public void truncateAndResetSeq() {
        String sql = "truncate table match_event;alter sequence match_event_id_seq restart with 1;";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.execute();
        } catch (SQLException e) {
            logger.error("Error executing query", e);
        }
    }
}
