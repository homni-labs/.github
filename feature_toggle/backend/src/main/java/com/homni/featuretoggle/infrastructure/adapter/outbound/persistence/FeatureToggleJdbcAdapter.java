package com.homni.featuretoggle.infrastructure.adapter.outbound.persistence;

import com.homni.featuretoggle.application.port.out.FeatureToggleRepositoryPort;
import com.homni.featuretoggle.domain.model.FeatureToggle;
import com.homni.featuretoggle.domain.model.FeatureToggleId;
import com.homni.featuretoggle.domain.exception.ToggleAlreadyExistsException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public class FeatureToggleJdbcAdapter implements FeatureToggleRepositoryPort {

    private static final String SELECT_WITH_ENVS = """
            SELECT ft.id, ft.name, ft.description, ft.enabled, ft.created_at, ft.updated_at,
                   (SELECT COALESCE(array_agg(e.name ORDER BY e.name), '{}')
                    FROM toggle_environment te
                    JOIN environment e ON e.id = te.environment_id
                    WHERE te.toggle_id = ft.id) AS environments
            FROM feature_toggle ft""";

    private final JdbcClient jdbc;

    FeatureToggleJdbcAdapter(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    @Transactional
    public void save(FeatureToggle t) {
        upsertToggle(t);
        syncEnvironments(t);
    }

    @Override
    public Optional<FeatureToggle> findById(FeatureToggleId id) {
        return jdbc.sql(SELECT_WITH_ENVS + " WHERE ft.id = ?")
                .param(id.value)
                .query(this::mapRow)
                .optional();
    }

    @Override
    public List<FeatureToggle> findAll(Boolean enabled, String environment,
                                       int offset, int limit) {
        WhereClause where = buildWhere(enabled, environment);
        JdbcClient.StatementSpec spec = jdbc.sql(
                SELECT_WITH_ENVS + where.sql + " ORDER BY ft.name LIMIT ? OFFSET ?");
        for (Object param : where.params) {
            spec = spec.param(param);
        }
        return spec.param(limit).param(offset).query(this::mapRow).list();
    }

    @Override
    public long count(Boolean enabled, String environment) {
        WhereClause where = buildWhere(enabled, environment);
        JdbcClient.StatementSpec spec = jdbc.sql(
                "SELECT count(*) FROM feature_toggle ft" + where.sql);
        for (Object param : where.params) {
            spec = spec.param(param);
        }
        return spec.query(Long.class).single();
    }

    @Override
    public void deleteById(FeatureToggleId id) {
        jdbc.sql("DELETE FROM feature_toggle WHERE id = ?")
                .param(id.value)
                .update();
    }

    private void upsertToggle(FeatureToggle t) {
        try {
            jdbc.sql("""
                    INSERT INTO feature_toggle (id, name, description, enabled, created_at, updated_at)
                    VALUES (?, ?, ?, ?, ?, ?)
                    ON CONFLICT (id) DO UPDATE
                        SET name = EXCLUDED.name,
                            description = EXCLUDED.description,
                            enabled = EXCLUDED.enabled,
                            updated_at = EXCLUDED.updated_at
                    """)
                    .param(t.id.value)
                    .param(t.name())
                    .param(t.description())
                    .param(t.isEnabled())
                    .param(Timestamp.from(t.createdAt))
                    .param(t.lastModifiedAt() != null ? Timestamp.from(t.lastModifiedAt()) : null)
                    .update();
        } catch (DuplicateKeyException e) {
            throw new ToggleAlreadyExistsException(t.name());
        }
    }

    private void syncEnvironments(FeatureToggle t) {
        jdbc.sql("DELETE FROM toggle_environment WHERE toggle_id = ?")
                .param(t.id.value)
                .update();
        String[] envNames = t.environments().toArray(String[]::new);
        jdbc.sql("""
                INSERT INTO toggle_environment (toggle_id, environment_id)
                SELECT ?, e.id FROM environment e WHERE e.name = ANY(?)
                """)
                .param(t.id.value)
                .param(envNames)
                .update();
    }

    private WhereClause buildWhere(Boolean enabled, String environment) {
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();
        if (enabled != null) {
            sql.append(" WHERE ft.enabled = ?");
            params.add(enabled);
        }
        if (environment != null) {
            sql.append(params.isEmpty() ? " WHERE " : " AND ");
            sql.append("ft.id IN (SELECT te.toggle_id FROM toggle_environment te "
                    + "JOIN environment e ON e.id = te.environment_id WHERE e.name = ?)");
            params.add(environment);
        }
        return new WhereClause(sql.toString(), params);
    }

    private record WhereClause(String sql, List<Object> params) {}

    private FeatureToggle mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new FeatureToggle(
                new FeatureToggleId(rs.getObject("id", UUID.class)),
                rs.getString("name"),
                rs.getString("description"),
                rs.getBoolean("enabled"),
                parseEnvironments(rs.getArray("environments")),
                rs.getTimestamp("created_at").toInstant(),
                toInstantOrNull(rs, "updated_at")
        );
    }

    private Set<String> parseEnvironments(Array sqlArray) throws SQLException {
        if (sqlArray == null) {
            return Set.of();
        }
        String[] values = (String[]) sqlArray.getArray();
        return new LinkedHashSet<>(Arrays.asList(values));
    }

    private Instant toInstantOrNull(ResultSet rs, String column) throws SQLException {
        Timestamp ts = rs.getTimestamp(column);
        return ts != null ? ts.toInstant() : null;
    }
}
