package com.bqpro.project;

import com.bqpro.project.Enums.ERole;
import com.bqpro.project.Model.Address;
import com.bqpro.project.Model.Phone;
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
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
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
public class ProjectApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(ProjectApplication.class, args);
	}

	@Autowired
	PasswordEncoder encoder;


	@Bean
	CommandLineRunner commandLineRunner(PersonRepository personRepository, RoleRepository roleRepository, UserRepository userRepository, AddressRepository addressRepository) {

		return args -> {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

			Person p1=new Person("John", "Doe", new Date(dateFormat.parse("1990-01-01").getTime()), null);
			Person p2=new Person("Jane", "Smith", new Date(dateFormat.parse("1985-06-15").getTime()), null);
			Person p3=new Person("Michael", "Johnson", new Date(dateFormat.parse("1992-12-31").getTime()), null);

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

			Phone ph1= new Phone();
			ph1.setText("+5353546000");
			Phone ph11= new Phone();
			ph11.setText("+5351699268");
			ph1.setPerson(p1);
			ph11.setPerson(p1);
			
			Phone ph2= new Phone();
			ph2.setText("+2401234561");
			ph2.setPerson(p2);

			Phone ph3= new Phone();
			ph3.setText("+1112345678");
			ph3.setPerson(p3);

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
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(ProjectApplication.class);}

	}
