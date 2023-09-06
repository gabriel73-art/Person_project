package com.bqpro.project;

import com.bqpro.project.Enums.ERole;
import com.bqpro.project.Model.Person;
import com.bqpro.project.Model.Role;
import com.bqpro.project.Repository.PersonRepository;
import com.bqpro.project.Repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.sql.Date;
import java.text.SimpleDateFormat;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class ProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjectApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(PersonRepository personRepository, RoleRepository roleRepository) {
		return args -> {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

			personRepository.save(new Person("John", "Doe", "123 Main St", new Date(dateFormat.parse("1990-01-01").getTime()), "555-1234", null));
			personRepository.save(new Person("Jane", "Smith", "456 Elm St", new Date(dateFormat.parse("1985-06-15").getTime()), "555-5678", null));
			personRepository.save(new Person("Michael", "Johnson", "789 Oak St", new Date(dateFormat.parse("1992-12-31").getTime()), "555-9876", null));

			roleRepository.save(new Role(ERole.valueOf("ROLE_ADMIN")));
			roleRepository.save(new Role(ERole.valueOf("ROLE_USER")));

		};
	}
	/*@Bean
	CommandLineRunner commandLineRunner(RoleRepository roleRepository) {
		return args -> {

			roleRepository.save(new Role(ERole.valueOf("ROLE_ADMIN")));
			roleRepository.save(new Role(ERole.valueOf("ROLE_USER")));
		};
	}*/

}
