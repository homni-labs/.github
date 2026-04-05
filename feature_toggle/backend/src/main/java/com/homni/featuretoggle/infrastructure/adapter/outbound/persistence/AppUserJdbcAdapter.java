package com.homni.featuretoggle.infrastructure.adapter.outbound.persistence;

import com.homni.featuretoggle.application.port.out.AppUserRepositoryPort;
import com.homni.featuretoggle.domain.model.AppUser;
import com.homni.featuretoggle.domain.model.Email;
import com.homni.featuretoggle.domain.model.Role;
import com.homni.featuretoggle.domain.model.UserId;
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
public class AppUserJdbcAdapter implements AppUserRepositoryPort {

    private static final String COLUMNS =
            "id, oidc_subject, email, name, role, active, created_at, updated_at";

    private final JdbcClient jdbc;

    AppUserJdbcAdapter(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void save(AppUser u) {
        jdbc.sql("""
                INSERT INTO app_user (id, oidc_subject, email, name, role, active, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT (id) DO UPDATE
                    SET oidc_subject = EXCLUDED.oidc_subject,
                        name = EXCLUDED.name,
                        role = EXCLUDED.role,
                        active = EXCLUDED.active,
                        updated_at = EXCLUDED.updated_at
                """)
                .param(u.id.value)
                .param(u.oidcSubject())
                .param(u.email.value())
                .param(u.displayName())
                .param(u.currentRole().name())
                .param(u.isActive())
                .param(Timestamp.from(u.createdAt))
                .param(u.lastModifiedAt() != null ? Timestamp.from(u.lastModifiedAt()) : null)
                .update();
    }

    @Override
    public Optional<AppUser> findById(UserId id) {
        return jdbc.sql("SELECT " + COLUMNS + " FROM app_user WHERE id = ?")
                .param(id.value)
                .query(this::mapRow)
                .optional();
    }

    @Override
    public Optional<AppUser> findByOidcSubject(String oidcSubject) {
        return jdbc.sql("SELECT " + COLUMNS + " FROM app_user WHERE oidc_subject = ?")
                .param(oidcSubject)
                .query(this::mapRow)
                .optional();
    }

    @Override
    public Optional<AppUser> findByEmail(String email) {
        return jdbc.sql("SELECT " + COLUMNS + " FROM app_user WHERE email = ?")
                .param(email)
                .query(this::mapRow)
                .optional();
    }

    @Override
    public List<AppUser> findAll(int offset, int limit) {
        return jdbc.sql("SELECT " + COLUMNS + " FROM app_user ORDER BY email LIMIT ? OFFSET ?")
                .param(limit)
                .param(offset)
                .query(this::mapRow)
                .list();
    }

    @Override
    public long count() {
        return jdbc.sql("SELECT count(*) FROM app_user")
                .query(Long.class)
                .single();
    }

    @Override
    public void deleteById(UserId id) {
        jdbc.sql("DELETE FROM app_user WHERE id = ?")
                .param(id.value)
                .update();
    }

    private AppUser mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new AppUser(
                new UserId(rs.getObject("id", UUID.class)),
                rs.getString("oidc_subject"),
                new Email(rs.getString("email")),
                rs.getString("name"),
                Role.valueOf(rs.getString("role")),
                rs.getBoolean("active"),
                rs.getTimestamp("created_at").toInstant(),
                toInstantOrNull(rs, "updated_at")
        );
    }

    private Instant toInstantOrNull(ResultSet rs, String column) throws SQLException {
        Timestamp ts = rs.getTimestamp(column);
        return ts != null ? ts.toInstant() : null;
    }
}
