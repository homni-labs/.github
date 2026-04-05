package com.homni.featuretoggle.infrastructure.adapter.outbound.persistence;

import com.homni.featuretoggle.application.port.out.EnvironmentRepositoryPort;
import com.homni.featuretoggle.domain.exception.EnvironmentAlreadyExistsException;
import com.homni.featuretoggle.domain.model.Environment;
import com.homni.featuretoggle.domain.model.EnvironmentId;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class EnvironmentJdbcAdapter implements EnvironmentRepositoryPort {

    private static final String COLUMNS = "id, name, created_at";

    private final JdbcClient jdbc;

    EnvironmentJdbcAdapter(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void save(Environment env) {
        try {
            jdbc.sql("""
                    INSERT INTO environment (id, name, created_at)
                    VALUES (?, ?, ?)
                    ON CONFLICT (id) DO UPDATE
                        SET name = EXCLUDED.name
                    """)
                    .param(env.id.value)
                    .param(env.name())
                    .param(Timestamp.from(env.createdAt))
                    .update();
        } catch (DuplicateKeyException e) {
            throw new EnvironmentAlreadyExistsException(env.name());
        }
    }

    @Override
    public Optional<Environment> findById(EnvironmentId id) {
        return jdbc.sql("SELECT " + COLUMNS + " FROM environment WHERE id = ?")
                .param(id.value)
                .query(this::mapRow)
                .optional();
    }

    @Override
    public List<Environment> findAll() {
        return jdbc.sql("SELECT " + COLUMNS + " FROM environment ORDER BY name")
                .query(this::mapRow)
                .list();
    }

    @Override
    public void deleteById(EnvironmentId id) {
        jdbc.sql("DELETE FROM environment WHERE id = ?")
                .param(id.value)
                .update();
    }

    @Override
    public boolean isEnvironmentInUse(String name) {
        long count = jdbc.sql("""
                SELECT count(*) FROM toggle_environment te
                JOIN environment e ON e.id = te.environment_id
                WHERE e.name = ?
                """)
                .param(name)
                .query(Long.class)
                .single();
        return count > 0;
    }

    private Environment mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Environment(
                new EnvironmentId(rs.getObject("id", UUID.class)),
                rs.getString("name"),
                rs.getTimestamp("created_at").toInstant()
        );
    }
}
