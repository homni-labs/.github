package com.homni.featuretoggle.infrastructure.adapter.outbound.persistence;

import com.homni.featuretoggle.application.port.out.ApiKeyRepositoryPort;
import com.homni.featuretoggle.domain.model.ApiKey;
import com.homni.featuretoggle.domain.model.ApiKeyId;
import com.homni.featuretoggle.domain.model.TokenHash;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ApiKeyJdbcAdapter implements ApiKeyRepositoryPort {

    private static final String COLUMNS = "id, name, token_hash, active, created_at, expires_at";

    private final JdbcClient jdbc;

    ApiKeyJdbcAdapter(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void save(ApiKey k) {
        Timestamp expiresAt = k.expiresAt != null ? Timestamp.from(k.expiresAt) : null;
        jdbc.sql("""
                INSERT INTO api_key (id, name, token_hash, active, created_at, expires_at)
                VALUES (?, ?, ?, ?, ?, ?)
                ON CONFLICT (id) DO UPDATE
                    SET active = EXCLUDED.active,
                        expires_at = EXCLUDED.expires_at
                """)
                .param(k.id.value)
                .param(k.name)
                .param(k.tokenHash.value)
                .param(k.isActive())
                .param(Timestamp.from(k.createdAt))
                .param(expiresAt)
                .update();
    }

    @Override
    public Optional<ApiKey> findById(ApiKeyId id) {
        return jdbc.sql("SELECT " + COLUMNS + " FROM api_key WHERE id = ?")
                .param(id.value)
                .query(this::mapRow)
                .optional();
    }

    @Override
    public Optional<ApiKey> findByTokenHash(TokenHash tokenHash) {
        return jdbc.sql("SELECT " + COLUMNS + " FROM api_key WHERE token_hash = ? AND active = true")
                .param(tokenHash.value)
                .query(this::mapRow)
                .optional();
    }

    @Override
    public List<ApiKey> findAll(int offset, int limit) {
        return jdbc.sql("SELECT " + COLUMNS + " FROM api_key ORDER BY created_at DESC LIMIT ? OFFSET ?")
                .param(limit)
                .param(offset)
                .query(this::mapRow)
                .list();
    }

    @Override
    public long count() {
        return jdbc.sql("SELECT count(*) FROM api_key")
                .query(Long.class)
                .single();
    }

    @Override
    public void deleteById(ApiKeyId id) {
        jdbc.sql("DELETE FROM api_key WHERE id = ?")
                .param(id.value)
                .update();
    }

    private ApiKey mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new ApiKey(
                new ApiKeyId(rs.getObject("id", UUID.class)),
                rs.getString("name"),
                new TokenHash(rs.getString("token_hash")),
                rs.getBoolean("active"),
                rs.getTimestamp("created_at").toInstant(),
                toInstantOrNull(rs, "expires_at")
        );
    }

    private Instant toInstantOrNull(ResultSet rs, String column) throws SQLException {
        Timestamp ts = rs.getTimestamp(column);
        return ts != null ? ts.toInstant() : null;
    }
}
