# zendesk-code-challenge

1. This project built on Java platform using JDK 11.0.9
2. The unit test use JUnit4
3. The source code is in the the "zcc/src" folder.

4. To run the compiled application:
    - go to folder "zcc/target"
    - update the "zcc/target/config" file (replace the "{}" with the appropriate value):
        - API=https://{subdomain}.zendesk.com/api/v2
        - USERNAME={your_username}
        - PASSWORD={your_password}
    - make sure that the password access has been enabled in the Admin Center 
      (https://developer.zendesk.com/api-reference/ticketing/introduction/#basic-authentication)
    - run java -jar zcc-1.0.jar

5. How to use it:
    - On start, the application display the ticket list and the menu list
    - In the menu list, user can choose:
        - input 0 : Exit from the application
        - input 1 : Go to previous page of the ticket list
        - input 2 : Go to next page of the ticket list
        - input 3 : 
            - The system will ask user to input a ticket ID
            - The system displays the details of the ticket which ID is inputted by the user
