package com.example.workPay.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Lazy
public class DatabaseMigration {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * These migrations are idempotent and the schema is already stable.
     * No longer runs on every startup — this prevents waking Neon compute
     * during Render's health-ping restart cycles.
     *
     * To run manually if needed: call POST /admin/run-migrations
     */
    public void migrate() {
        jdbcTemplate.execute(
            "ALTER TABLE public.work_entry ALTER COLUMN shift DROP NOT NULL"
        );
        migrateExpenditureTable();
    }

    private void migrateExpenditureTable() {
        jdbcTemplate.execute(
            "DO $$ BEGIN " +
            "IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema='public' AND table_name='Expenditure' AND column_name='id') THEN " +
            "ALTER TABLE public.\"Expenditure\" ADD COLUMN \"id\" VARCHAR(255); " +
            "END IF; END $$"
        );

        jdbcTemplate.execute(
            "UPDATE public.\"Expenditure\" SET \"id\" = gen_random_uuid()::text WHERE \"id\" IS NULL"
        );

        jdbcTemplate.execute(
            "DO $$ " +
            "DECLARE pk_name TEXT; " +
            "BEGIN " +
            "SELECT constraint_name INTO pk_name FROM information_schema.table_constraints " +
            "WHERE table_schema='public' AND table_name='Expenditure' AND constraint_type='PRIMARY KEY'; " +
            "IF pk_name IS NOT NULL THEN " +
            "EXECUTE format('ALTER TABLE public.\"Expenditure\" DROP CONSTRAINT \"%s\"', pk_name); " +
            "END IF; " +
            "END $$"
        );

        jdbcTemplate.execute(
            "ALTER TABLE public.\"Expenditure\" ALTER COLUMN \"id\" SET NOT NULL"
        );
        jdbcTemplate.execute(
            "ALTER TABLE public.\"Expenditure\" ADD PRIMARY KEY (\"id\")"
        );

        jdbcTemplate.execute(
            "DO $$ BEGIN " +
            "IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema='public' AND table_name='Expenditure' AND column_name='branchId') THEN " +
            "ALTER TABLE public.\"Expenditure\" ADD COLUMN \"branchId\" INTEGER; " +
            "END IF; END $$"
        );
    }
}
