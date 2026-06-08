package org.example.project.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@Component
@RequiredArgsConstructor
@Order(1)
@Slf4j
public class DbConstraintFixer implements CommandLineRunner {

    private final DataSource dataSource;

    @Override
    public void run(String... args) {
        String[] sqls = {
            "ALTER TABLE telegram_sessions DROP CONSTRAINT IF EXISTS telegram_sessions_state_check",
            "ALTER TABLE telegram_users DROP CONSTRAINT IF EXISTS telegram_users_current_state_check"
        };

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(true);
            for (String sql : sqls) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(sql);
                    log.info("DB constraint fix applied: {}", sql);
                } catch (Exception e) {
                    log.warn("DB constraint fix skipped ({}): {}", sql, e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Failed to fix DB constraints", e);
        }
    }
}
