package com.book.network.config;

import com.book.network.entity.Role;
import com.book.network.repository.RoleRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class StartUpApp implements CommandLineRunner {

    private final RoleRepo roleRepo;

    @Override
    public void run(String... args) throws Exception {
        if (roleRepo.findByName("ADMIN").isEmpty())
            roleRepo.save(Role.builder()
                    .name("ADMIN")
                    .build());

        if (roleRepo.findByName("USER").isEmpty())
            roleRepo.save(Role.builder()
                    .name("USER")
                    .build());
    }
}
