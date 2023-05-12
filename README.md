# **Connector Import Tool**

## Welcome!
The Connector Import Tool was born out of a frustration with the lack of the ability to easily import SysML connectors 
and associated elements (i.e. ports, item flows, etc.) into a SysML model in MagicDraw/Cameo from external sources. 
This plugin is a response to that need and provides an easy way for a modeler to import connectors directly into the
model from a Microsoft Excel (.xlsx) spreadsheet.  See the User Guide available in this project and accessible from the
plugin's main dialog via the "Read Me" button.  This is an open source project so please add features, fix bugs, etc. to your heart's content!

## Running Instructions
This project repository includes all the files you need to get up and running without needing to compile anything! Simply navigate to the "ConnectorImportTool\target" directory and find the **ConnectorImportPlugin-X.X.X.jar** file
along with the **plugin.xml** file and the **ConnectorImportPlugin User Guide**.  Simply create a new directory named **ConnectorImportPlugin** in **{MagicDraw or Cameo Install Directory}\plugins** and copy those 3 previously mentioned 
files into that newly created directory and that's it!  When you start MagicDraw/Cameo the plugin will be loaded along with the rest of the plugins in that plugin directory.  To access the main dialog, go to the "Tools" drop down menu in 
the main application and find the "Connector Tool - Import Dialog" menu item.  Please see the plugin user guide by either pressing the "Read Me" button in the plugin main dialog or by opening the **ConnectorImportPlugin User Guide**
outside the application for detailed instructions on how to use the plugin in a SysML model.

## Compiling the Project
These instructions assume the user has:
* The Apache Maven project installed (see https://maven.apache.org/download.cgi)
* MagicDraw or Cameo Systems Modeler / Enterprise Architecture version 2021x Refresh 2 Installed
* Java Standard Edition 11 installed (see https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html)

Maven is a java project builder that allows the developer to specify what java class files are to be compiled, what external libraries the java project needs to compile, and where they reside.  It also allows the developer to specify whether the project should be "installed" somewhere on the local machine, what the output file name and type should be, etc.  All that is specified in the Maven Project Object Model file named **pom.xml**.  Take a look at the POM file included in this project and see what external libraries the project depends on by examining the dependency section.  This project exclusively uses libraries that come packaged with MagicDraw / Cameo so no need to go hunting for others!  It is important that you ensure update the update the POM file dependency .jar file locations with the locations on your local machine (find the "libraries" directory in the MagicDraw / Cameo installation directory).  If you do decide to add your own functionality to this project, the Apache Maven project has a helpful dependency search function to help you find libraries that could be useful to you.
At this time, the project is only known to be compatible with MagicDraw / Cameo version 2021x Refresh 2.
To build this project with maven, open the command window, navigate to the project directory, and enter the command "maven package".  Once the build process is complete, navigate to the "\target" directory and find the .jar file.  To run the newly compiled plugin, please follow the directions in the section above titled "Running Instructions."

## A Brief Description of the Code

To be added in the future!

Note: the MainPanel.form is a form created using Apache NetBeans IDE since their GUI editor is generally nicer and easier to use.  
