## V8 Log Scanner
[![Build Status](https://travis-ci.org/ripreal/V8LogScanner.svg?branch=master)](https://travis-ci.org/ripreal/V8LogScanner)
[![codecov](https://codecov.io/gh/ripreal/V8LogScanner/branch/master/graph/badge.svg)](https://codecov.io/gh/ripreal/V8LogScanner)
[![BCH compliance](https://bettercodehub.com/edge/badge/ripreal/V8LogScanner?branch=master)](https://bettercodehub.com/)
[![jitpack](https://jitpack.io/v/ripreal/V8LogScanner.svg)](https://jitpack.io/#ripreal/V8LogScanner)

## Running built-in console application

If you want just to run the V8 Log Scanner as console application on your local machine all you need are the Java SE 1.8 Environment and a executable .jar library located in [project repository](https://github.com/ripreal/V8LogScanner/tree/master/v8LogScanner_release). Just for convenience i put inside the repo a several *.cmd and *.bat files for running on windows.

![main menu](https://infostart.ru/upload/iblock/437/43784745e12e355fbd4efd7c10458c79.png)

## Using in own projects

For development purposes you need the Java SE 1.8  and the Maven building system. See notes below on how to install the project.

### Install with Maven

It's possible to gets V8LogScanner integrated with your java project on Maven. Put the xml text listened below into \<dependencies\> and \<repositories\> sections whitin your pom.xml file.

```
<!-- put it inside <dependencies> section -->
<dependency>
    <groupId>com.github.ripreal</groupId>
    <artifactId>V8LogScanner</artifactId>
    <version>-SNAPSHOT</version>
</dependency>

<!-- put it inside <repositories> section -->
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
```

### Examples for developers 

Let's say you wish to output a list of all EXCP events from a some \*.log files directory. The steps would be these:

```
V8LogScannerClient client =
  new V8LanLogScannerClient("127.0.0.1");              // First, create client containing IP for computer 
                                                       // with *.log files

ScanProfile profile = client.getProfile();             // obtain profile with scan settings
profile.addLogPath("C:\\v8\\logs");                    // specify directory with *.log files to scan 
profile.addRegExp(new RegxExp(EventTypes.EXCP));       // specify the EXCP events to seek inside each of *.log file

client.startRgxOp();                                   // run logs processing. It may takes a time depends on size and
                                                       // qunatity logs on scanning computer
List<SelectorsEntry> logs = client.select(100,         
  SelectDirections.FORWARD);                           // get cursor list with first top 100 results                            

for (SelectorsEntry log : logs) {
  System.out.println(log);
}
```
And result can be:
```
53:08.840011-20440475011,EXCPCNTX,0,SrcName=PROC,OSThread=4860,process=1cv8
53:08.872005-0,EXCP,1,process=1cv8,ClientID=10,Exception=NetDataExchangeException,Descr='server_addr=(2)192.168.1.39:62233 descr=recv returns zero, disconnected line=2235 file=src\DataExchangeServerImpl.cpp'
53:08.872006-0,EXCPCNTX,0,ClientComputerName=,ServerComputerName=,UserName=,ConnectString=
53:08.872007-3,EXCPCNTX,0,SrcName=MEM,OSThread=1620,process=1cv8

// and others up to 100 logs since we set limit   
```
locations put in your logcfg.xml configure file (which may be located somewhere in C:\Program Files (x86)\1cv8\conf)
End with an example of getting some data out of the system or using it for a little demo

## Running the tests

Test are located in scr/test folder and should be run using Junit and Mockito frameworks. The intelliJ IDEA is a good choice to tackle with them. Mainly the tests are for checking various parsing cases. You may want to explore them to find useful code snippets about how to work with V8LogScanner.

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management
* [jacoco-maven-plugin](https://github.com/jacoco/jacoco/tree/master/jacoco-maven-plugin) - Code statictic plugin for Codecov 
* [Junit](http://junit.org/junit5/) - Test framework
* [Mockito](http://site.mockito.org/) - Mocking framework

## Authors

* **Ripreal** - *Creator*

See also the list of [contributors](https://github.com/ripreal/V8LogScanner/graphs/contributors) who participated in this project.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details
