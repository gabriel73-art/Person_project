package com.bqpro.project.Controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.bqpro.project.Exceptions.NotMatchException;
import com.bqpro.project.Model.Address;
import com.bqpro.project.Model.Person;
import com.bqpro.project.Model.Phone;
import com.bqpro.project.Repository.AddressRepository;
import com.bqpro.project.Repository.PersonRepository;
import com.bqpro.project.Repository.PhoneRepository;
import com.bqpro.project.Response.MessageResponse;
import com.bqpro.project.Service.PersonService;
import com.bqpro.project.Specifications.PersonSpecifications;
import com.bqpro.project.Utils.FileUploadUtil;

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
    @PostMapping(value="/create",consumes={MediaType.MULTIPART_FORM_DATA_VALUE},
            produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(@RequestParam(value = "image", required = false) MultipartFile file,
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
            if (!arePhoneNumbersValid(phoneNumber)) {
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Error: Phone numbers are not valid, must starts with character + and contains only numbers or already exists in Database !"));
            }
            if (!isDateOfBirthValid(dateOfBirth)) {
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Error: Invalid date of birth. Year can't be lower than 1900 !"));
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
        person.setPersonalPhoto(createFile(file));
        Person personsave = personService.savePerson(person);

        personsave.setAddresses(createAndSaveAddresses(addresses,person));

        personsave.setPhoneNumbers(createAndSavePhone(phoneNumber, person));
                
            return ResponseEntity.status(HttpStatus.OK).body(person);
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

        String validRequest= validPersonRequest(person);
        if(validRequest!="")
        {
            return ResponseEntity
            .badRequest()
            .body(new MessageResponse(validRequest));
        }


        if (existingPerson.isPresent()) {

            String validPersonAP= validPersonAddresAndPhones(existingPerson.get(), person); 
            if(validPersonAP!="")
            {
                return ResponseEntity
                .badRequest()
                .body(new MessageResponse(validPersonAP));
            }   

            person.setId(id);
            Person updatedPerson = existingPerson.get();
            
            List<Address> existingAddress = updateAndSaveAddress(person, updatedPerson);
            List<Phone> existingPhone = updateAndSavePhones(person, updatedPerson);

            person.setAddresses(existingAddress);
            person.setPhoneNumbers(existingPhone);
            Person updatedPersonAll = personService.savePerson(person);
            return ResponseEntity.ok(updatedPersonAll);

        }else {
            return ResponseEntity.badRequest().body(new MessageResponse("That person does'nt exists!"));
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
            List<Phone> phones= existingPerson.get().getPhoneNumbers();
            for (Phone ph : phones) {
                phoneRepository.delete(ph);
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
        if (addresses != null) {
            spec = spec.and(PersonSpecifications.hasAddresses(addresses));

        }

        List<Person> personlist= personRepository.findAll(spec);

        if(personlist.isEmpty())
            throw new NotMatchException("There is no clients for that specifications");

        return personlist;
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
        return phone.matches("^\\+[0-9 ]+$");
    }
    private boolean isDateOfBirthValid(Date dateOfBirth) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateOfBirthStr = dateFormat.format(dateOfBirth);
        int year = Integer.parseInt(dateOfBirthStr.substring(0, 4));
        return year >= 1900;
    }
    private boolean arePhoneNumbersValid(String[] phoneNumbers) {
        for (String phoneNumber : phoneNumbers) {
            if (!isValidPhoneNumber(phoneNumber)||personService.findPhoneExist(phoneNumber)) {
                return false;
            }
        }
        return true;
    }
    private String createFile( MultipartFile file) throws IOException {
        //Person person = new Person();

        if (file != null) {
            int index = file.getOriginalFilename().indexOf(".");
            String extension = "." + file.getOriginalFilename().substring(index + 1);
            String nombreFoto = Calendar.getInstance().getTimeInMillis() + extension;
            FileUploadUtil.saveFile("person-images", nombreFoto, file);
            String absolute = Paths.get("person-images").toFile().getAbsolutePath() + File.separator + nombreFoto;
            return absolute ;
        }

        return null;
    }
    private List<Address> createAndSaveAddresses(String[] addresses, Person personsave) {
        List<Address> addressList = new ArrayList<>();
        for (String text : addresses) {
            Address ad = new Address();
            ad.setText(text.trim());
            ad.setPerson(personsave);
            Address addressSave = addressRepository.save(ad);
            addressList.add(addressSave);
        }
        return addressList;
    }
    private List<Phone> createAndSavePhone(String[] phones, Person personsave) {
        List<Phone> phoneList = new ArrayList<>();
        for (String text : phones) {
            Phone ph = new Phone();
            ph.setText(text.trim());
            ph.setPerson(personsave);
            Phone phoneSave = phoneRepository.save(ph);
            phoneList.add(phoneSave);
        }
        return phoneList;
    }



    private String validPersonRequest(Person person){
        String response="";
        if(person.getDateOfBirth()==null)
        {
            response="Error: empty dateOfBirth !";
        }

        if(person.getFirstName()==null)
        {
            response="Error: empty firstName !";
        }

        if(person.getSecondName()==null)
        {
            response="Error: empty secondName !";
        }

        if(!person.getAddresses().isEmpty())
        {
            String[] strings= new String[person.getAddresses().size()];
            int index=0;
            for(Address ad: person.getAddresses()){
                strings[index] = ad.getText();
                index++;
            }

            if(personService.reviewString(strings))
                response="Error: Address repeat !";
        }

        if(!person.getPhoneNumbers().isEmpty())
        {
            String[] strings= new String[person.getPhoneNumbers().size()];
            int index=0;
            for(Phone ph: person.getPhoneNumbers()){
                strings[index] = ph.getText();
                index++;
            }

            if(personService.reviewString(strings))
                response="Error: Phones repeat !";
        }

        return response;
    }

    private String validPersonAddresAndPhones(Person existingPerson, Person person){
        String response="";

        for(Address ad: person.getAddresses()){
            if(personService.findAddressByPerson(existingPerson, ad.getText()))
            {
                response="Error: Address repeat !";
            }
        }


        for(Phone ph: person.getPhoneNumbers()){
            if (!isValidPhoneNumber(ph.getText())) {
                response="Error: Phone number must starts with character + and contains only numbers !";
            }

            if(personService.findPhoneByPerson(existingPerson, ph.getText()))
            {
                response="Error: Phone number repeat !";
            }

            if(personService.findPhoneExist(ph.getText())){
                response="Error: "+ph.getText()+" Phone number must be unique !";
            }
        }

        return response;
    }

    private List<Address> updateAndSaveAddress(Person person, Person updatedPerson){
        List<Address> existingAddress = updatedPerson.getAddresses();
        for (Address add : person.getAddresses()) {
            Iterator<Address> it = existingAddress.iterator();
            boolean find = false;
            while(it.hasNext()) {
                Address address = it.next();
                if(add.getId().equals(address.getId()))
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
                addressRepository.save(ad); 
                existingAddress.add(ad);
            }
        }
        return existingAddress;
    }

    private List<Phone> updateAndSavePhones(Person person, Person updatedPerson){
        List<Phone> existingPhone = updatedPerson.getPhoneNumbers();
        for (Phone ph : person.getPhoneNumbers()) {
            Iterator<Phone> it = existingPhone.iterator();
            boolean find = false;
            while(it.hasNext()) {
                Phone phone = it.next();
                if(ph.getId().equals(phone.getId()))
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
                phoneRepository.save(phnew); 
                existingPhone.add(phnew);
            }
        }
        return existingPhone;
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

