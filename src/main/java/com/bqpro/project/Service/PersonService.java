package com.bqpro.project.Service;

import com.bqpro.project.Model.Address;
import com.bqpro.project.Model.Person;
import com.bqpro.project.Model.Phone;
import com.bqpro.project.Repository.PersonRepository;
import com.bqpro.project.Repository.PhoneRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
public class PersonService {
    private final PersonRepository personRepository;

    private final PhoneRepository phoneRepository;

    @Autowired
    public PersonService(PersonRepository personRepository, PhoneRepository phoneRepository) {
        this.personRepository = personRepository;
        this.phoneRepository= phoneRepository;
    }

    public List<Person> getAllPersons() {
        return personRepository.findAll();
    }

    public Optional<Person> getPersonById(Long id) {
        return personRepository.findById(id);
    }

    public Person savePerson(Person person) {
        return personRepository.save(person);
    }

    public void deletePerson(Long id) {
        personRepository.deleteById(id);
    }

    public boolean findAddressByPerson(Person person,String address){
        boolean find = false;
        Iterator<Address> it = person.getAddresses().iterator();
        while(it.hasNext() && !find) {
            Address itaddress = it.next();
            String ittext= itaddress.getText().toLowerCase();
            if(ittext.contains(address.toLowerCase()))
            {
                find = true;
            }
        }

        return find;
    }

    public boolean findPhoneByPerson(Person person,String phone){
        boolean find = false;
        Iterator<Phone> it = person.getPhoneNumbers().iterator();
        while(it.hasNext() && !find) {
            Phone itphone = it.next();
            String ittext= itphone.getText().toLowerCase();
            if(ittext.contains(phone.toLowerCase()))
            {
                find = true;
            }
        }

        return find;
    }

    public boolean reviewString(String[] array){
        for (int i = 0; i < array.length; i++)
        {
            for (int j = i + 1; j < array.length; j++)
            {
                if (array[i].trim() != null && array[i].equals(array[j].trim())) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean findPhoneExist(String phone){
        Specification<Phone> spec = Specification.where(null);
        spec = spec.and((root, query, cb) -> cb.equal(root.get("text"), phone));
        List<Phone> phonelist= phoneRepository.findAll(spec);

        return phonelist.isEmpty()?false: true;
    }

}
