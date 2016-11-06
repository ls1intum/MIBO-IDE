# MIBO IDE
MIBO IDE is an integrated development environment for creating and managing multimodal controls in smart buildings. It offers a convenient and interactive interface without the need for any programming skills. MIBO IDE introduces several components, such as the MIBO Editor. Further information can be found [here][7].

![MIBO IDE ](Documentation/Screenshots/MIBO-IDE.png?raw=true)

## Setup Eclipse
This project is developed using the _Google Web Toolkit_ \("GWT"\). In order to prepare your Eclipse environment for GWT, install the _Plugin for Eclipse_ from [here][1] (at least the _Google Plugin for Eclipse_ as well as the _Google Web Toolkit SDK_ are required). If not included within the Eclipse installation, it is recommended to add GWT version 2.7 from [here][2]. The latest Eclipse version successfully tested with MIBO IDE is [Eclipse Mars (4.5.1)][3].

## Setup the project
This project uses the library and elements of the _Polymer_ project. Add them by following these steps:
* Download the [Polymer Starter Kit][4]. The latest Polymer version successfully tested with MIBO IDE is [Polymer Starter Kit 1.1.1][5] 
* Extract the ZIP file and copy the `bower_components` folder into `war/polymer/` directory of this project. Remove the `bower_components.txt` text file \(optional\)

## Test the project
The MIBO-IDE is designed to connect to an instance of the [MIBO framework][6] in order to retrieve information about available fixtures, modalities, and controls as well as to modify and create new interaction definitions.
However, in case no instance of the MIBO framework is available (indicated by an empty `baseURL.txt`, default setting), MIBO IDE will enter a demonstration mode. This will load a predefined set of data. Any changes made to interaction definitions will not be persisted.

## Compile the project
While GWT automatically compiles the project during testing, a dedicated compile process is required before deploying the project:
* In Eclipse, click on the Google button \(called "GDT Pulldown"\) and choose "GWT Compile Project\.\.\.”. Note, that clicking on the menu item will not work until the root project “MIBO-IDE” in the package explorer is selected
* Make sure that the correct entry point (default name is "edu.tum.ls1.mibo.editor") is selected. No further input is required. However, setting the log level to "Info" and output style to "Detailed" might help when tracing errors within the browser's console
* Click on "Compile" to start the compile action

## Deploy the project
In order to deploy the MIBO IDE in an openHAB environment, copy the `war` folder of the GWT project into the `webapps` folder of openHAB.
The root folder `war` may be renamed to `mibo-ide` to achieve a convenient URL such as `http://127.0.0.1:8080/mibo-ide` \(assuming the standard configuration of openHAB\).

## Run the project
When using a different system configuration than running the MIBO IDE inside of the `webapps` folder of openHAB, update the base URL to the MIBO framework in the `baseURL.txt` which is located in the root folder of the deployed project \(`war` or `mibo-ide`\).
Make sure that the definition files \(XML files\) contain only "known" items. If this is not the case, the MIBO IDE will still work. However, it will not display the unknown parts of the definition, nor will it be able to update or applied them correctly.

[1]:	http://www.gwtproject.org/download.html
[2]:	http://www.gwtproject.org/versions.html
[3]:	http://www.eclipse.org/downloads/
[4]:	https://github.com/polymerelements/polymer-starter-kit/releases
[5]:	https://github.com/PolymerElements/polymer-starter-kit/releases/download/v1.1.1/polymer-starter-kit-light-1.1.1.zip
[6]:	https://mediatum.ub.tum.de/node?id=1304127
[7]:	http://dx.doi.org/10.1145/2993148.2993162
