package com.bqpro.project.Controller;

import com.bqpro.project.Exceptions.NotMatchException;
import com.bqpro.project.Model.Address;
import com.bqpro.project.Model.Person;
import com.bqpro.project.Model.Phone;
import com.bqpro.project.Repository.AddressRepository;
import com.bqpro.project.Repository.PersonRepository;
import com.bqpro.project.Repository.PhoneRepository;
import com.bqpro.project.Response.MessageResponse;
import com.bqpro.project.Service.PersonService;
import com.bqpro.project.Utils.FileUploadUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.hibernate.resource.jdbc.spi.PhysicalConnectionHandlingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/persons")
public class PersonController {
    private final PersonService personService;
    @Autowired
    PersonRepository personRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    PhoneRepository phoneRepository;

    @Autowired
    public PersonController(PersonService personService) {
        this.personService = personService;
    }


    @GetMapping
    public List<Person> getAllPersons() {
        return personService.getAllPersons();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> getPersonById(@PathVariable Long id) {
        Optional<Person> person = personService.getPersonById(id);
        return person.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }


    //@PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value="/create", method=RequestMethod.POST,consumes={MediaType.MULTIPART_FORM_DATA_VALUE},
            produces=MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<?> create(@RequestParam(value = "image", required = false) MultipartFile file,
                                          @RequestParam("firstName") String firstName,
                                          @RequestParam("secondName") String secondName,
                                          @RequestParam("dateOfBirth") Date dateOfBirth,
                                          @RequestParam("phoneNumber") String[] phoneNumber,
                                          @RequestParam("address") String address) throws IOException {
        try {

            String[] addresses = address.split(";");
            if (!isValidName(firstName)||!isValidName(secondName)) {
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Error: Firstname or Secondname contains stranger characters !"));
            }

            for (String text : phoneNumber) {
                if (!isValidPhoneNumber(text)) {
                    return ResponseEntity
                            .badRequest()
                            .body(new MessageResponse("Error: Phone number must starts with character + and contains only numbers !"));
                }

                if(personService.findPhoneExist(text)){
                    return ResponseEntity
                            .badRequest()
                            .body(new MessageResponse("Error: "+text+" Phone number must be unique !"));
                }
                
            }

            
            
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateOfBirthStr = dateFormat.format(dateOfBirth);
            int year = Integer.parseInt(dateOfBirthStr.substring(0, 4));

            if (year < 1900) {
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Error: Year can't be lower than 1900 !"));

            }
        if(personService.reviewString(phoneNumber))
        {
            return ResponseEntity
                            .badRequest()
                            .body(new MessageResponse("Error: Phone number repeat !"));
        }

        if(personService.reviewString(addresses))
        {
            return ResponseEntity
                            .badRequest()
                            .body(new MessageResponse("Error: Address repeat !"));
        }

        

        Person person=new Person(firstName,secondName,dateOfBirth);
        String a="";
        if(file!=null){
        int index=file.getOriginalFilename().indexOf(".");
        String extension;
        extension="."+file.getOriginalFilename().substring(index+1);
        String nombreFoto= Calendar.getInstance().getTimeInMillis()+extension;
        FileUploadUtil.saveFile("person-images",nombreFoto,file);
        String absolute= Paths.get("person-images").toFile().getAbsolutePath()+ File.separator+nombreFoto;
        person.setPersonalPhoto(absolute);
        }

            Person personsave = personService.savePerson(person);
            if(personsave!=null)
            {
                List<Address> addressList = new ArrayList<Address>();
                for (String text : addresses) {
                    Address ad= new Address();
                    ad.setText(text);
                    ad.setPerson(personsave);
                    Address addressSave = addressRepository.save(ad);
                    addressList.add(addressSave);
                }
                personsave.setAddresses(addressList);

                List<Phone> phoneList = new ArrayList<Phone>();
                for (String text : phoneNumber) {
                    Phone ph= new Phone();
                    ph.setText(text);
                    ph.setPerson(personsave);
                    Phone phoneSave = phoneRepository.save(ph);
                    phoneList.add(phoneSave);
                }
                personsave.setPhoneNumbers(phoneList);
            }
                
            return ResponseEntity.status(HttpStatus.OK).body(personsave);
        }
        catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }


    //@PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updatePerson(@PathVariable Long id, @RequestBody Person person) {
        Optional<Person> existingPerson = personService.getPersonById(id);
        if(person.getDateOfBirth()==null)
        {
            return ResponseEntity
            .badRequest()
            .body(new MessageResponse("Error: empty dateOfBirth !"));
        }

        if(person.getFirstName()==null)
        {
            return ResponseEntity
            .badRequest()
            .body(new MessageResponse("Error: empty firstName !"));
        }

        if(person.getSecondName()==null)
        {
            return ResponseEntity
            .badRequest()
            .body(new MessageResponse("Error: empty secondName !"));
        }

        if(person.getAddresses().size()>0)
        {
            for(Address ad: person.getAddresses()){
                if(personService.findAddressByPerson(existingPerson.get(), ad.getText()))
                {
                    return ResponseEntity
                            .badRequest()
                            .body(new MessageResponse("Error: Address repeat !"));
                }
            }
        }

        if(person.getPhoneNumbers().size()>0)
        {
            for(Phone ph: person.getPhoneNumbers()){
                if (!isValidPhoneNumber(ph.getText())) {
                    return ResponseEntity
                            .badRequest()
                            .body(new MessageResponse("Error: Phone number must starts with character + and contains only numbers !"));
                }

                if(personService.findPhoneByPerson(existingPerson.get(), ph.getText()))
                {
                    return ResponseEntity
                            .badRequest()
                            .body(new MessageResponse("Error: Phone number repeat !"));
                }

                if(personService.findPhoneExist(ph.getText())){
                    return ResponseEntity
                            .badRequest()
                            .body(new MessageResponse("Error: "+ph.getText()+" Phone number must be unique !"));
                }
            }
        }

        if (existingPerson.isPresent()) {
            person.setId(id);
            Person updatedPerson = existingPerson.get();
            List<Address> existingAddress = updatedPerson.getAddresses();
            List<Address> newsAddress = updatedPerson.getAddresses();
            
            for (Address add : person.getAddresses()) {
                Iterator<Address> it = existingAddress.iterator();
                boolean find = false;
                while(it.hasNext()) {
                    Address address = it.next();
                    if(add.getId()==address.getId())
                    {
                        find = true;
                        address.setText(add.getText());
                        addressRepository.save(address);
                    }
                }
                if(!find)
                {
                    Address ad= new Address();
                    ad.setText(add.getText());
                    ad.setPerson(updatedPerson);
                    Address addressSave = addressRepository.save(ad);
                    newsAddress.add(addressSave); 
                }
            }

            List<Phone> existingPhone = updatedPerson.getPhoneNumbers();
            List<Phone> newsPhone = updatedPerson.getPhoneNumbers();
            for (Phone ph : person.getPhoneNumbers()) {
                Iterator<Phone> it = existingPhone.iterator();
                boolean find = false;
                while(it.hasNext()) {
                    Phone phone = it.next();
                    if(ph.getId()==phone.getId())
                    {
                        find = true;
                        phone.setText(ph.getText());
                        phoneRepository.save(phone);
                    }
                }

                if(!find)
                {
                    Phone phnew= new Phone();
                    phnew.setText(ph.getText());
                    phnew.setPerson(updatedPerson);
                    Phone phSave = phoneRepository.save(phnew);
                    newsPhone.add(phSave);   
                }
            }

            person.setAddresses(existingAddress);
            person.setPhoneNumbers(existingPhone);
            Person updatedPersonAll = personService.savePerson(person);
            return ResponseEntity.ok(updatedPersonAll);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //@PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePerson(@PathVariable Long id) {
        Optional<Person> existingPerson = personService.getPersonById(id);
        if (existingPerson.isPresent()) {
            List<Address> address= existingPerson.get().getAddresses();
            for (Address add : address) {
                addressRepository.delete(add);
            }
            personService.deletePerson(id);
            return ResponseEntity.ok().body(new MessageResponse("User deleted successfully!"));
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("The ID does'nt exists!"));
        }
    }

    @GetMapping("/search")
    public List<Person> searchPerson(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String secondName,
            @RequestParam(required = false) String addresses
    ) throws NotMatchException {
    try {


        Specification<Person> spec = Specification.where(null);

        if (firstName != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("firstName"), firstName));
        }
        if (secondName != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("secondName"), secondName));
        }


        List<Person> personlist= personRepository.findAll(spec);
        List<Person> newlist= new ArrayList<>();
        if (addresses != null) {
            for (Person person : personlist) {
                if (personService.findAddressByPerson(person, addresses))
                    newlist.add(person);
            }
        }
        if(personlist==null && newlist==null)
            throw new NotMatchException("There is no clients for that specifications");
        return addresses!= null?newlist:personlist;
    }
        catch (NotMatchException ex){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"", ex);


    }}




    @GetMapping("/age-range")
    public ResponseEntity<List<Person>> getPersonsByAgeRange(
            @RequestParam(value = "startAge", required = false) String startAgeStr,
            @RequestParam(value = "endAge", required = false) String endAgeStr) {

        List<Person> persons = new ArrayList<>();
        try {
            Integer startAge = (startAgeStr != null) ? Integer.parseInt(startAgeStr) : null;
            Integer endAge = (endAgeStr != null) ? Integer.parseInt(endAgeStr) : null;

            LocalDate currentDate = LocalDate.now();
            LocalDate startDate = null;
            LocalDate endDate = null;

            if (startAge != null && endAge != null) {
                startDate = currentDate.minusYears(endAge);
                endDate = currentDate.minusYears(startAge);
            } else if (startAge != null) {
                startDate = currentDate.minusYears(200);
                endDate = currentDate.minusYears(startAge);
            } else if (endAge != null) {
                startDate = currentDate.minusYears(endAge);
                endDate = currentDate;
            }

            if (startDate != null && endDate != null) {
                Date sqlStartDate = Date.valueOf(startDate);
                Date sqlEndDate = Date.valueOf(endDate);

                persons = personRepository.findByDateOfBirthBetween(sqlStartDate, sqlEndDate);

                if (persons.isEmpty()) {
                    throw new NotMatchException("No hay clientes en ese rango de edad");
                }
            }

            return ResponseEntity.ok(persons);
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "", e);
        } catch (NotMatchException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "", ex);
        }
    }

    private boolean isValidName(String name) {
        return name.matches("^[a-zA-Z ]+$");
    }
    private boolean isValidPhoneNumber(String phone) {
        return phone.matches("^\\+[0-9]+$");
    }




    @ExceptionHandler(NotMatchException.class)
    public ResponseEntity<String> handleNotMatchException(NotMatchException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<String> handleNumberFormatException(NumberFormatException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {

        String errorMessage = "Error en el parámetro '" + ex.getName() + ". El formato debe ser yyyy-MM-dd.";


        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> handleMissingRequestParamException(MissingServletRequestParameterException ex) {
        String paramName = ex.getParameterName();
        String errorMessage = "Required request parameters '" + paramName + "' is not present.";
        List<String> errors = new ArrayList<>();
        errors.add(errorMessage);
        return ResponseEntity.badRequest().body(new MessageResponse(errors));
    }



}

