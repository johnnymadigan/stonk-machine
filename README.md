![unit](https://img.shields.io/badge/CAB3O2-Software%20Development-ff69b4?style=plastic)
![author](https://img.shields.io/badge/Author-Johnny%20Madigan-yellow?style=plastic)
![year](https://img.shields.io/badge/Year-2021-lightgrey?style=plastic)
![IntelliJ](https://img.shields.io/badge/IntelliJ-Community-blueviolet?style=plastic&logo=IntelliJ%20IDEA)
![Java SDK](https://img.shields.io/badge/Java%20SDK-Amazon%20Correto%2015-orange?style=plastic&logo=Java)
![JUnit5](https://img.shields.io/badge/JUnit5-5.7.0-green?style=plastic&logo=JUnit5)
![SQLite](https://img.shields.io/badge/SQLite%20JDBC-3.34.0-blue?style=plastic&logo=SQLite)
![Apache Ant](https://img.shields.io/badge/Apache%20Ant-critical?style=plastic&logo=Apache%20Ant)

<p align="center">𝔍𝔬𝔥𝔫𝔫𝔶 𝔐𝔞𝔡𝔦𝔤𝔞𝔫</p>

- [About](#about)
- [How to run jar via the terminal](#how-to-run-via-the-terminal)
- [How to build a new jar via *Ant* in *IntelliJ*](#how-to-build-a-new-jar-via-ant-in-intellij)
  - [Configure Server Settings](#configure-server-settings)
  - [BUILD - Clean, Compile, Test, Document, and Create Executable Jar](#build---clean-compile-test-document-and-create-executable-jar)
  - [OPTIONAL - Using a mock database](#optional---using-a-mock-database)
  - [Troubleshooting](#troubleshooting)
- [Diagrams](#diagrams)
  - [UML Class Diagram](#uml-class-diagram)
  - [UI Visual Paradigm](#ui-visual-paradigm)
  - [Database ERD](#database-erd)
- [Dependencies](#dependencies)

# About
### A solo extension on my team's software development project: an electronic trading app

<p align="center"><em>Team: Johnny Madigan, Scott Peachey, and Alistair Ridge</em></p>

A software development project focusing on delivering an application fulfilling a client's needs. The project was initially developed by a team of 3 using an Agile approach with an emphasis on Test-Driven-Development.

My team and I were contracted to develop a *"client-server system for electronic trading of virtual assets within their organisation"*. Given a series of user stories, we've extracted a list of requirements prioritised using the MoSCoW method. We then put together a detailed design document of the system along with 3 diagrams (UML class diagram, UI visual paradigm, and a database ERD). As we developed our app using *Java* and *IntelliJ*, we incrementally tested our features with black box and glass box testing.

While working with my team I had the pleasure of developing a segment of the app. I primarily focused on user accounts, implementing for both front-end and back-end features, using *Java Swing* for the GUI and *SQLite* for the database. Admins have the ability to add or delete users, units, and assets while also being able to update existing data. General user accounts have the ability to view their unit's current balance, holdings, and order history. Both types of accounts are able to change their own passwords using a self-service, and also view a dynamic graph showing the prices of an asset. 

The demonstration below shows a brief look at the admin pages with control panels, then searching for an asset and adjusting the graph's prices before logging out. The dynamic graph was deemed a "could have" based on the client's requirements, and therefore a feature I implemented post-project. I also implemented a new build script with more functionality for continuous integration and deployment (using *Apache Ant* to clean, compile, test, document, and generate a JAR). Finally, I created this README to be more informative with the instructions below as well as better showcasing the app.

![project animation](/img/readme-images/ezgif-demonstration.gif)

# How to run via the terminal

- Launch your OS' terminal.
- Navigate into the *"stonk-machine"* project directory with the `cd` command.
- Navigate into the *"artifacts"* folder.
- Type `java -jar stonk-machine.jar` then press the <kbd>enter</kbd> or <kbd>return</kbd> key to launch the program.

![run via terminal](/img/readme-images/run-via-terminal.gif)

# How to build a new JAR via *Ant* in *IntelliJ*
## Configure Server Settings
- In your file manager, open the *"stonk-machine"* project directory, then navigate to the server settings properties file `src > main > java > ServerSide > ServerSettings.props`
- Open the file in your preferred text editor.
- (OPTIONAL) Change PORT/HOST details if necessary
- (REQUIRED) Setup initial admin user credentials
    - Set your preferred USERNAME
    - Set your preferred PASSWORD
- Save and close file

![find and edit properties file](/img/readme-images/nav-to-props.gif)

## BUILD - Clean, Compile, Test, Document, and Create Executable JAR
- Download [JetBrain's IntelliJ IDEA](https://www.jetbrains.com/idea/download/#section=windows), *Community Edition* is free.
- Open the project directory called *"stonk-machine"* inside *IntelliJ*.
- Open the *Ant* window `View > Tool Windows > Ant`
- Click ***all*** to run the entire build script.
- Refer back to [*"How to run jar via the terminal"*](#how-to-run-via-the-terminal) to run the new jar.

![run build script](/img/readme-images/run-build-script.gif)

## OPTIONAL - Using a mock database
- First, backup your current database by saving a copy of it somewhere else.
- In your file manager, open the *"stonk-machine"* project directory.
- You will see a mock database called *"MockStonkMachine.db"*, make a copy of it.
- Rename this database to *"StonkMachine.db"* and replace/place inside the *"artifacts"* folder beside the JAR.

## Troubleshooting
This section is for rare cases / reconfiguring *IntelliJ* for future developments on this project.

- Specify a project SDK, we recommend *Amazon Correto 15* although you may be able to use other version 15s `File > Project Structure > Project section > Project SDK`
- Beneath, set Project Language Level to *15 - Text blocks*.

![What your Project SDK should look like](/img/readme-images/project-SDK.png)

- Still inside the Project Structure window, goto the Libraries section and you may also need to add the following from *Maven*:
    - org.junit.platform:junit-platform-launcher:1.7.0
    - org.xerial:sqlite-jdbc:3.34.0

![What your Libraries should look like](/img/readme-images/libraries.png)

- You may need to import *JUnit 5.7* if an error occurs with the test classes.
- If so, open any one of the test classes and right-click on the red *JUni Jupitert* import line at the top of the file.
- Select *import junit5*

![How to import JUnit 5.7](/img/readme-images/import-junit.png)

- Finally, if you are still having issues, check the Modules section in the Project Structure window.

![What your Modules section should look like](/img/readme-images/modules.png)

# Diagrams
## UML Class Diagram
![UML Class Diagram](/docs/diagrams/Class-Diagram-V3.png)

## UI Visual Paradigm
![UI Visual Paradigm](/docs/diagrams/GUI-Diagram-V2.jpg)

## Database ERD
![Database ERD](/docs/diagrams/Database-ERD.png)

# Dependencies
This app was developed using the *JetBrains' IntelliJ IDE*. If you want to run the *Ant* build script, please open the project folder *"stonk-machine"* using *IntelliJ*. If you need to configure your IntelliJ to work properly (Project SDK etc) please see [*"Troubleshooting"*](#troubleshooting).
