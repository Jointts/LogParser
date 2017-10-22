# LogParser

A simple JavaFX program that parses a server log. Uses lambdas so JDK must be 1.8 or newer.

## Ant instructions
Go to the cloned project directory <br>
```cd <project_dir> ```<br><br>
Run the program with the following command (Compilation and Jarring will be done automatically, sample log file named timing.log will be supplied)<br>
```ant run ```<br><br>

## Executing in CLI
App must be executed with 2 mandatory parameters
* The file location to parse (relative to the jar location)
* Number of longest responding resources to show
<br><br>For example<br>
```java -jar dist/proekspert.jar timing.log 10```<br>
This will read the log file 'timing.log' and show the top 10 longest responding resources

## Misc
Other useful ant commands are<br><br>
```ant compile ``` - Compiles the code<br>
```ant jar ``` - Jars the code<br>
```ant build ``` - Does the previous 2 steps together
