package com.example.workPay.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseMigration implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        jdbcTemplate.execute(
            "ALTER TABLE public.work_entry ALTER COLUMN shift DROP NOT NULL"
        );
        migrateExpenditureTable();
    }

    private void migrateExpenditureTable() {
        // Add id column if it doesn't exist
        jdbcTemplate.execute(
            "DO $$ BEGIN " +
            "IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema='public' AND table_name='Expenditure' AND column_name='id') THEN " +
            "ALTER TABLE public.\"Expenditure\" ADD COLUMN \"id\" VARCHAR(255); " +
            "END IF; END $$"
        );

        // Populate existing rows with generated ids
        jdbcTemplate.execute(
            "UPDATE public.\"Expenditure\" SET \"id\" = gen_random_uuid()::text WHERE \"id\" IS NULL"
        );

        // Drop old composite primary key if it exists
        jdbcTemplate.execute(
            "DO $$ BEGIN " +
            "ALTER TABLE public.\"Expenditure\" DROP CONSTRAINT IF EXISTS \"Expenditure_pkey\"; " +
            "EXCEPTION WHEN undefined_object THEN NULL; END $$"
        );

        // Set id as primary key
        jdbcTemplate.execute(
            "DO $$ BEGIN " +
            "ALTER TABLE public.\"Expenditure\" ALTER COLUMN \"id\" SET NOT NULL; " +
            "ALTER TABLE public.\"Expenditure\" ADD PRIMARY KEY (\"id\"); " +
            "EXCEPTION WHEN duplicate_object THEN NULL; END $$"
        );

        // Add branchId column if it doesn't exist
        jdbcTemplate.execute(
            "DO $$ BEGIN " +
            "IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema='public' AND table_name='Expenditure' AND column_name='branchId') THEN " +
            "ALTER TABLE public.\"Expenditure\" ADD COLUMN \"branchId\" INTEGER; " +
            "END IF; END $$"
        );
    }
}
