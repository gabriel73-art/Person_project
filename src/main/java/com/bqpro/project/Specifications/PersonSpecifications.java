package com.bqpro.project.Specifications;

import com.bqpro.project.Model.Address;
import com.bqpro.project.Model.Person;
import org.springframework.data.jpa.domain.Specification;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

public class PersonSpecifications {

    public static Specification<Person> hasFirstName(String firstName) {
        return (root, query, cb) -> cb.equal(root.get("firstName"), firstName);
    }

    public static Specification<Person> hasSecondName(String secondName) {
        return (root, query, cb) -> cb.equal(root.get("secondName"), secondName);
    }

    public static Specification<Person> hasAddresses(String addresses) {
        return (root, query, cb) -> {
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<Address> subRoot = subquery.from(Address.class);
            subquery.select(subRoot.get("person").get("id"));
            subquery.where(cb.equal(subRoot.get("text"), addresses));

            return root.get("id").in(subquery);
        };
    }

}

