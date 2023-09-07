package com.bqpro.project;

import com.bqpro.project.Enums.ERole;
import com.bqpro.project.Model.Address;
import com.bqpro.project.Model.Person;
import com.bqpro.project.Model.Role;
import com.bqpro.project.Repository.AddressRepository;
import com.bqpro.project.Model.User;
import com.bqpro.project.Repository.PersonRepository;
import com.bqpro.project.Repository.RoleRepository;
import com.bqpro.project.Repository.UserRepository;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
//@OpenAPIDefinition(info = @Info(title = "Clients of the bank API", version = "2.0", description = "Clients Information"))
/*@SecurityScheme(name = "bearerAuth",
		type = SecuritySchemeType.HTTP,
		scheme = "bearer",
		bearerFormat = "JWT",
		in = SecuritySchemeIn.HEADER)*/
public class ProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjectApplication.class, args);
	}

	@Autowired
	PasswordEncoder encoder;


	@Bean
	CommandLineRunner commandLineRunner(PersonRepository personRepository, RoleRepository roleRepository, UserRepository userRepository, AddressRepository addressRepository) {

		return args -> {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

			Person p1=new Person("John", "Doe", new Date(dateFormat.parse("1990-01-01").getTime()), "555-1234", null);
			Person p2=new Person("Jane", "Smith", new Date(dateFormat.parse("1985-06-15").getTime()), "555-5678", null);
			Person p3=new Person("Michael", "Johnson", new Date(dateFormat.parse("1992-12-31").getTime()), "555-9876", null);

			personRepository.save(p1);
			personRepository.save(p2);
			personRepository.save(p3);

			roleRepository.save(new Role(ERole.valueOf("ROLE_ADMIN")));
			roleRepository.save(new Role(ERole.valueOf("ROLE_USER")));

			Address a1= new Address();
			a1.setText("123 Main St");
			Address a11= new Address();
			a11.setText("456 Miami St");
			a1.setPerson(p1);
			a11.setPerson(p1);
			
			Address a2= new Address();
			a2.setText("456 Elm St");
			a2.setPerson(p2);

			Address a3= new Address();
			a3.setText("789 Oak St");
			a3.setPerson(p3);

			addressRepository.save(a1);
			addressRepository.save(a11);
			addressRepository.save(a2);
			addressRepository.save(a3);
			User adminUser = new User("Admin", "User", "admin", encoder.encode("123"));
			Set<Role> adminRoles = new HashSet<>();
			adminRoles.add(roleRepository.findByName(ERole.ROLE_ADMIN).orElseThrow(() -> new RuntimeException("Error: Role not found.")));
			adminUser.setRoles(adminRoles);
			userRepository.save(adminUser);

			User userUser = new User("User", "User", "user", encoder.encode("123"));
			Set<Role> userRoles = new HashSet<>();
			userRoles.add(roleRepository.findByName(ERole.ROLE_USER).orElseThrow(() -> new RuntimeException("Error: Role not found.")));
			userUser.setRoles(userRoles);
			userRepository.save(userUser);

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
