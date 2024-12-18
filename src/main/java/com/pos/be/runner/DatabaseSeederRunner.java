package com.pos.be.runner;

import com.pos.be.service.DatabaseSeederService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

//@Component
public class DatabaseSeederRunner implements CommandLineRunner {
    private final DatabaseSeederService databaseSeederService;

    public DatabaseSeederRunner(DatabaseSeederService databaseSeederService) {
        this.databaseSeederService = databaseSeederService;
    }

    @Override
    public void run(String... args) throws Exception {
        databaseSeederService.seedDatabase();
    }
}
