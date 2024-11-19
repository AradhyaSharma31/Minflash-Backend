package com.flashcard.FlashcardBackend;

import com.flashcard.FlashcardBackend.Configuration.AppConstants;
import com.flashcard.FlashcardBackend.Entity.Role;
import com.flashcard.FlashcardBackend.Repository.RoleRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.UUID;

@SpringBootApplication
public class FlashcardBackendApplication implements CommandLineRunner {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private RoleRepo roleRepo;

	public static void main(String[] args) {
		SpringApplication.run(FlashcardBackendApplication.class, args);
	}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	@Override
	public void run(String... args) throws Exception {

		try {
			Role roleAdmin = new Role();
			roleAdmin.setId(AppConstants.ADMIN_USER);
			roleAdmin.setName("ROLE_ADMIN");

			Role roleUser = new Role();
			roleUser.setId(AppConstants.NORMAL_USER);
			roleUser.setName("ROLE_USER");

			List<Role> roles = List.of(roleAdmin, roleUser);

			List<Role> result = this.roleRepo.saveAll(roles);

			result.forEach(r -> {
				System.out.println(r.getName());
			});

		} catch(Exception e) {
			e.printStackTrace();
		}

	}
}
