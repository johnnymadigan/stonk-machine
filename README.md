![unit](https://img.shields.io/badge/CAB203-Discrete%20Structures-ff69b4?style=plastic)
![author](https://img.shields.io/badge/Author-Johnny%20Madigan-yellow?style=plastic)
![year](https://img.shields.io/badge/Year-2021-lightgrey?style=plastic)
![python version](https://img.shields.io/badge/Python%20version-2.7%20%7C%203.8%20|%203.9-informational?style=plastic&logo=python)

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

<p align="center">Team: Johnny Madigan, Scott Peachey, and Alistair Ridge</p>

A software development project focusing on delivering an application fulfilling a client's needs. The project was initially developed by a team of 3 using an Agile approach with an emphasis on Test-Driven-Development.

My team and I were contracted to develop a *"client-server system for electronic trading of virtual assets within their organisation"*. Given a series of user stories, we've extracted a list of requirements prioritised using the MoSCoW method. We then put together a detailed design document of the system along with 3 diagrams (UML class diagram, UI visual paradigm, and a database ERD). As we developed our app using *Java* and *IntelliJ*, we incrementally tested our features with black box and glass box testing.

Some of the features I worked on...

Some of the additional features I worked on...

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
