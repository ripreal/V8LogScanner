### V8 Log Scanner
[![Build Status](https://travis-ci.org/ripreal/V8LogScanner.svg?branch=master)](https://travis-ci.org/ripreal/V8LogScanner)

### Running built-in console application

If you want to run the V8 Log Scanner as console application on your local machine download a executable .jar library located in [project repository](https://github.com/ripreal/V8LogScanner/tree/master/repo/org/v8LogScanner/v8LogScanner/1.0).

### Using in own projects

For development purposes you need the Java SE 1.8  and the Maven building system. See notes below on how to deploy the project.

## Maven

It's possible to gets V8LogScanner integrated with your java project on Maven. Put the xml text listened below into \<dependencies\> and \<repositories\> sections whitin your pom.xml file.

```
<!-- put it inside <dependencies> section -->
  <dependency>
    <groupId>org.v8LogScanner</groupId>
    <artifactId>v8LogScanner</artifactId>
    <version>1.0</version>      
  </dependency>

<!-- put it inside <repositories> section -->
  <repository>
    <id>v8LogScannerRepo</id>
    <name>v8LogScannerRepo</name>
    <url>https://cdn.rawgit.com/ripreal/V8LogScanner/74f24180/repo/</url>
    <snapshots>
      <enabled>false</enabled>
    </snapshots>
  </repository>
```

## Examples for developers 

Let's say you wish to output a list of all EXCP events from a some \*.log files directory. The steps would be these:

```
V8LogScannerClient client = new V8LanLogScannerClient("127.0.0.1"); // First, create client containing IP for computer with *.log files

ScanProfile profile = client.getProfile();              // obtain profile with scan settings
profile.addLogPath("C:\\v8\\logs");                     // specify directory with *.log files to scan 
profile.addRegExp(new RegxExp(EventTypes.EXCP));        // specify the EXCP events to seek inside each of *.log file

client.startRgxOp();                                    // run logs processing. It may takes a time depends on size and
                                                        // qunatity logs on scanning computer
List<SelectorsEntry> logs = client.select(100, true);   // get list with results 

for (SelectorsEntry log : logs) {
  System.out.println(log);
}
```
And result could be:
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

Explain how to run the automated tests for this system

### Break down into end to end tests

Explain what these tests test and why

```
Give an example
```

### And coding style tests

Explain what these tests test and why

```
Give an example
```

## Deployment

Add additional notes about how to deploy this on a live system

## Built With

* [Dropwizard](http://www.dropwizard.io/1.0.2/docs/) - The web framework used
* [Maven](https://maven.apache.org/) - Dependency Management
* [ROME](https://rometools.github.io/rome/) - Used to generate RSS Feeds

## Contributing

Please read [CONTRIBUTING.md](https://gist.github.com/PurpleBooth/b24679402957c63ec426) for details on our code of conduct, and the process for submitting pull requests to us.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/your/project/tags). 

## Authors

* **Billie Thompson** - *Initial work* - [PurpleBooth](https://github.com/PurpleBooth)

See also the list of [contributors](https://github.com/your/project/contributors) who participated in this project.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* Hat tip to anyone who's code was used
* Inspiration
* etc
