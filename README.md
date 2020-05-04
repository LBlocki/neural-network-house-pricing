# neural-network-house-pricing

### Install Java JDK and Maven
Before running you have to install both. You should also add them to the PATH.  
* [Java](https://www.oracle.com/technetwork/java/javase/downloads/index.html) - you need JDK 11
* [Maven](https://maven.apache.org/) - you need Apache Maven 3.6.3

### Running with Intellij
If you do not have maven tab on the right side of the screen right click on main project catalog
in project tab on the left side and choose 'add framework support', then choose maven.
After opening maven tab, click reimport dependencies. After that you can use maven console nicely
It's a good idea to refresh maven dependencies using refresh option you can find on the maven tab.  

Also don't forget to set project JDK ( File -> Project Structure) to JDK 11.

Also make sure to enable annotation processing for lombok.

To access logger within any class use:  
    ```
    private final MessageProducer messageProducer = ApplicationBeansConfiguration.getInstance(MessageProducer.class);
    ```  
Then when you wish to add new Message use either new Thread and its start method or Platform.runLater()  
but the body should contain:    
    ```
    messageProducer.addMessage(MessageProducer.Message message);
    ```  

### Creation of executioners:
For development if we want to create new executable jobs we have to use Execution Service interface. It allows
for creation of listeners that any controller can listen to. Example of such usage is TestExecutionService

### Maven commands
Cleaning generated files
   ```bash
   mvn clean
   ```
Packaging into jar file
   ```bash
   mvn package
   ```
Running application ( before running package it )
   ```bash
   mvn javafx:run
   ```
Unit testing:
   ```bash
   mvn test
   ```
Integration testing: 
   ```bash
   mvn verify
   ```
JavaDoc:
   ```bash
   mvn javadoc:javadoc -Dshow=private
   ```
Maven will generate documentation that can be found under target/site/apidocs/index.html  