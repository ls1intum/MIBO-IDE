# MIBO IDE
An IDE for Multimodal Controls in Smart Buildings

## Setup Eclipse
* This project reqiures the _Google Web Toolkit_ \("GWT"\). In order to prepare your Eclipse environment for GWT, the following steps are required:
	1. Install the _Plugin for Eclipse_ from [here][1]
		* Minimum requirements are the _Google Plugin for Eclipse_ as well as the _Google Web Toolkit SDK_
	2. If not included within the Eclipse installation, it is recommended to add GWT Version 2.7 from [here][2]
* The latest Eclipse version successfully tested with GWT is [Eclipse Mars (4.5.1)][3]  

## Setup the project
* Add the latest library and elements of the _Polymer_ project by following these steps:
	1. Download the [Polymer Starter Kit][4]
	2. Extract the ZIP file and copy the `bower_components` folder into `war/polymer/` directory of this project
	3. Remove the `bower_components.txt` text file \(optional\)
 * The latest Polymer version successfully tested with MIBO IDE is [Polymer Starter Kit 1.1.1][5]  

## Compile the project
* While GWT automatically compiles the project during testing, a dedicated compile process is required before actually deploying the project:
	1. Within Eclipse, click on the Google button \(called "GDT Pulldown"\) and choose "GWT Compile Project\.\.\."
		* Note, that clicking on the menu item will not work until the root project “MIBO-IDE” in the Package Explorer is selected
	2. Make sure that the correct entry point (default name is "edu.tum.ls1.mibo.editor") is selected
		* No further input is required. However, setting the log level to "Info" and output style to "Detailed" might help when tracing errors within the browser's console
	3. Click on "Compile" to start the compile action

## Deploy the project
* In order to deploy the MIBO IDE in an openHAB environment, copy the `war` folder of the GWT project into the `webapps` folder of openHAB
* The root folder `war` may be renamed to `mibo-ide` to achieve a convenient URL such as `http://127.0.0.1:8080/mibo-ide` \(assuming the standard configuration of openHAB\)

## Run the project
* When using a different system configuration than running the MIBO IDE inside of the `webapps` folder of openHAB, update the base URL to the Mibo framework in the `baseURL.txt` which is located in the root folder of the deployed project \(`war` or `mibo-ide`\)
* Make sure that the definition files \(XML files\) contain only "known" items. If this is not the case, the MIBO IDE will still work. However, it will not display the unknown parts of the definition, nor will it be able to update or applied them correctly

[1]:	http://www.gwtproject.org/download.html
[2]:	http://www.gwtproject.org/versions.html
[3]:	http://www.eclipse.org/downloads/ "Download link"
[4]:	https://github.com/polymerelements/polymer-starter-kit/releases
[5]:	https://github.com/PolymerElements/polymer-starter-kit/releases/download/v1.1.1/polymer-starter-kit-light-1.1.1.zip "Download link"