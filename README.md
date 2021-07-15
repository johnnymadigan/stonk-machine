![unit](https://img.shields.io/badge/CAB203-Discrete%20Structures-ff69b4?style=plastic)
![author](https://img.shields.io/badge/Author-Johnny%20Madigan-yellow?style=plastic)
![year](https://img.shields.io/badge/Year-2021-lightgrey?style=plastic)
![python version](https://img.shields.io/badge/Python%20version-2.7%20%7C%203.8%20|%203.9-informational?style=plastic&logo=python)

<p align="center">𝔍𝔬𝔥𝔫𝔫𝔶 𝔐𝔞𝔡𝔦𝔤𝔞𝔫 🐰</p>

- [About](#about)
- [Usage](#usage)
- [How to run via the terminal](#how-to-run-via-the-terminal)
- [How to run via *Visual Studio Code*](#how-to-run-via-visual-studio-code)
- [Call Graph](#call-graph)
- [Dependencies](#dependencies)

# About
### SUB TITLE
A Graph Theory project that demonstrates...

![project animation](/img/readme-images/demonstration.gif)

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
