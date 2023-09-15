package com.bqpro.project.Request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

public class  SignupRequest {

    @NotBlank
    @Size(max = 50)
    private String firstname;

    @NotBlank
    @Size(max = 50)
    private String lastname;

    @NotBlank
    @Size(min = 3, max = 20)
    private String username;


    private Set<String> role;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    public SignupRequest(){
       super();
    }

   /* public SignupRequest(String firstname, String lastname, String username, String password){
        this.firstname=firstname;
        this.lastname=lastname;
        this.username=username;
        this.password=password;
    }*/

    public String getFirstname() {
        return firstname;
    }

   /* public void setFirstname(String firstname) {
        this.firstname = firstname;
    }*/

    public String getLastname() {
        return lastname;
    }

   /* public void setLastname(String lastname) {
        this.lastname = lastname;
    }*******NUEVOS COMENTARIOS*/
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<String> getRole() {
        return this.role;
    }

   /* public void setRole(Set<String> role) {
       this.role = role;
    } ******NUEVOS COMENATRIOS*/
}

