package org.v8LogScanner.testV8LogScanner;

import org.v8LogScanner.commonly.fsys;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.v8LogScanner.commonly.fsys;

// BUILDER PATTERN 
  
public class V8LogFileConstructor {
  
  private List<String> logData = new ArrayList<>();
  public  enum LogFileTypes {TEXT, FILE} 
  
  public V8LogFileConstructor addEXCP() {
    
    logData.add("52:13.872002-0,EXCP,0,process=1cv8c,setTerminateHandler=setTerminateHandler");
    logData.add("52:13.887000-0,EXCP,0,process=1cv8c,setUnhandledExceptionFilter=setUnhandledExceptionFilter");
    logData.add("52:17.725002-0,EXCP,2,process=1cv8c,Exception=580392e6-ba49-4280-ac67-fcd6f2180121,Descr='src\\VResourceSessionImpl.cpp(507)");

    return this;
  }
  
  public String build(LogFileTypes type) {
    String preparedText = String.join("\n", logData);;
    switch (type) {
    case TEXT:
      return preparedText;
    case FILE:
      return fsys.createTempFile(preparedText);
    default:
      return "";
    }
  }

  public static void deleteLogFile(String fileName) {
    try {
      Files.deleteIfExists(Paths.get(fileName));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
}
