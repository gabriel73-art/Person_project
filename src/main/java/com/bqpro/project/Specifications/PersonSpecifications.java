package com.bqpro.project.Specifications;

import com.bqpro.project.Model.Person;
import org.springframework.data.jpa.domain.Specification;
import javax.persistence.criteria.Predicate;

public class PersonSpecifications {

    public static Specification<Person> hasFirstName(String firstName) {
        return (root, query, cb) -> cb.equal(root.get("firstName"), firstName);
    }

    public static Specification<Person> hasSecondName(String secondName) {
        return (root, query, cb) -> cb.equal(root.get("secondName"), secondName);
    }

    public static Specification<Person> hasAddresses(String addresses) {
        return (root, query, cb) -> cb.equal(root.get("addresses"), addresses);
    }
}

