![unit](https://img.shields.io/badge/CAB302-Software%20Development-ff69b4?style=plastic)
![author](https://img.shields.io/badge/Author-Johnny%20Madigan-yellow?style=plastic)
![year](https://img.shields.io/badge/Year-2021-lightgrey?style=plastic)
![IntelliJ](https://img.shields.io/badge/IntelliJ-Community-blueviolet?style=plastic&logo=IntelliJ%20IDEA)
![Java SDK](https://img.shields.io/badge/Java%20SDK-Amazon%20Correto%2015-orange?style=plastic&logo=Java)
![JUnit5](https://img.shields.io/badge/JUnit5-5.7.0-green?style=plastic&logo=JUnit5)
![SQLite](https://img.shields.io/badge/SQLite%20JDBC-3.34.0-blue?style=plastic&logo=SQLite)
![Apache Ant](https://img.shields.io/badge/Apache%20Ant-critical?style=plastic&logo=Apache%20Ant)



<p align="center">𝔍𝔬𝔥𝔫𝔫𝔶 𝔐𝔞𝔡𝔦𝔤𝔞𝔫 🐰</p>

- [About](#about)
- [How to run jar via the terminal](#how-to-run-via-the-terminal)
- [How to build a new jar via *Ant* in *IntelliJ*](#how-to-build-a-new-jar-via-ant-in-intellij)
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

While working with my team I had the pleasure of developing a segment of the entire app. I primarily focused on user accounts, implementing for both front-end and back-end features, using *Java Swing* for the GUI and *SQLite* for the database. Admins have the ability to add or delete users, units, and assets while also being able to update existing data. General user accounts have the ability to view their unit's current balance, holdings, and order history. Both types of accounts are able to change their own passwords using a self-service, and also view a dynamic graph showing the prices of an asset. 

The demonstration below shows a brief look at the admin pages with control panels, then searching for an asset and adjusting the graph's prices before logging out. The dynamic graph was deemed a "could have" based on the client's requirements, and therefore a feature I implemented post-project. We were also not able to implement a build script for continuous integration and deployment, therefore this is another feature I implemented post-project (using *Apache Ant* to clean, compile, test, document, and generate a JAR). I also fixed some nasty bugs like issues reading the properties file properly to configure the server & database along with some issues reconciling trades. Finally, I created this README to be more informative with the instructions below as well as better showcasing the app.

![project animation](/img/readme-images/ezgif-demonstration.gif)

# How to run via the terminal

# How to build a new jar via *Ant* in *IntelliJ*

# Diagrams

## UML Class Diagram

## UI Visual Paradigm

## Database ERD

# Dependencies
IntelliJ community ed, IntelliJ SDK, project strucutre, JUnit5.7, SQLite database


------------------


By Alistair Ridge, Johnny Madigan & Scott Peachey


INSTRUCTIONS:

- Please download "IntelliJ IDEA Community Edition" to run our app https://www.jetbrains.com/idea/download/#section=windows
- In your file manager, open the "CAB302_StonkMachine" folder and navigate to:

        CAB302_Assignment-1 > Setup > ServerSettings.props

- Open this file in your preferred text editor
- (OPTIONAL) Change PORT/HOST details if necessary
- (REQUIRED) Setup initial admin user credentials
    - Set your preferred USERNAME
    - Set your preferred PASSWORD
- Save and close file


- Launch IntelliJ IDEA CE
- Open the 'CAB302_StonkMachine' folder inside IntelliJ
- Specify a project SDK, we recommend Java version 15
  
      File > Project Structure > Project SDK
  
- Select Project Language Level 15 - Text blocks
- Navigate to Libraries in the left menu of the Project Structure
- Select the sqlite-jdbc library and right click
- Select add to modules
- Select the CAB302-Assignment-1 module and click ok
- Click 'Build Project' (^+F9 for Windows & Cmd+F9 for Mac)
- Click 'Run Main' (Shift+F10 for Windows & ^+R for Mac)

If an error occurs with the Test classes, open any one of the test classes and 
right-click on the red import method. Select the import junit5.

TESTING WITH A FAKE DATABASE - OPTIONAL
- In your file manager, open the "CAB302_StonkMachine" folder and navigate to:

        CAB302_Assignment-1 > Setup > ServerSettings.props

- Open this file in your preferred text editor
- Change the SCHEMA to "MockStonkMachine" to test our app with a fake database
- Save and close file
- Run via the instructions above
- To revert, change SCHEMA to "StonkMachine"
