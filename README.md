# **Person project**
##  Contact management using Spring Boot, H2 Database in-memory, and Swagger
### Api Documentation by Swagger 
http://localhost:8080/
###   Points to consider:

1. In order to create a new person, becareful about date format in "date of birth " field. The format should be: yyyy-mm-dd
2. Fill in all the text fields in create person.
3. The photo is saved in the person-images project folder
4. When the Tomcat server is running on port 8080, go to your browser and type localhost:8080 and index.html will list all persons in a table.
5. On the left you will see a sidebar containing the link to create a person. Above you'll see diferent filters to persons.
6. On the Update Person page, you need to enter an ID to search for the person, and then continue with the update.

# ContractExGroup2

ABC Bank Contact Training Exercise - Group 2

Tasks to be achieved:

Part #1
- consolidate ABC Bank contact management exercise you did in the hiring process in one final solution
- for the FE part, having the swagger interface is enough
- once ready push changed to /deploy branch

Part #2
- what is present under the deploy branch will be taken from Jenkins, built and deployed to tomcat. Code will be also analized by Sonar (details will follow)
- fix issues detected by Sonar
