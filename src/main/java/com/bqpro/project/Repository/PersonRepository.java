package com.bqpro.project.Repository;

import com.bqpro.project.Model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public interface PersonRepository extends JpaRepository<Person, Long>, JpaSpecificationExecutor<Person> {
    List<Person> findByDateOfBirthBetween(Date startDate, Date endDate);

}

