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

  public enum LogFileTypes {TEXT, FILE}

  public V8LogFileConstructor addEXCP() {

    logData.add("52:13.872002-0,EXCP,0,process=1cv8c,setTerminateHandler=setTerminateHandler");
    logData.add("52:13.887000-0,EXCP,0,process=1cv8c,setUnhandledExceptionFilter=setUnhandledExceptionFilter");
    logData.add("52:17.725002-0,EXCP,2,process=1cv8c,Exception=580392e6-ba49-4280-ac67-fcd6f2180121,Descr='src\\VResourceSessionImpl.cpp(507)");

    return this;
  }

  public V8LogFileConstructor addTDEADLOCK() {
    logData.add("45:36.115007-0,TDEADLOCK,6,process=rphost,p:processName=test,t:clientID=56,t:applicationName=1CV8C,t:computerName=yardnout,t:connectID=17,SessionID=62,Usr=DefUser,AppID=1CV8C,DeadlockConnectionIntersections='17 18 Reference10.REFLOCK Exclusive ID=10:9d31eb60b487eef9426efe8ac93cf86c,18 17 Reference10.REFLOCK Exclusive ID=10:95aae4f5c33133584335e2ca22f204fd',Context='Форма.Вызов : Обработка.Взаимоблокировки.Форма.Форма.Модуль.ВзаимоблокировкаНаСервере"
            + "\nОбработка.Взаимоблокировки.Форма.Форма.Форма : 32 : ВзаимоблокировкаЗахватРесурсовВРазномПорядке();"
            + "\nОбработка.Взаимоблокировки.Форма.Форма.Форма : 91 : ЭлементОбъект2.Записать();'");
    return this;
  }

  public V8LogFileConstructor addSQlDEADLOCK() {
    logData.add("EXCP,process=rphost,p:processName=test,t:applicationName=1CV8C,t:computerName=yardnout,t:connectID=22,SessionID=65,Usr=DefUser,AppID=1CV8C,Exception=580392e6-ba49-4280-ac67-fcd6f2180121,Descr='src\\VResourceInfoBaseImpl.cpp(1031):"
    +"\n580392e6-ba49-4280-ac67-fcd6f2180121: Non-specified resource error"
    +"\nError executing the query POST to resource /e1cib/logForm:"
    +"\n8d366056-4d5a-4d88-a207-0ae535b7d28e: {Обработка.Взаимоблокировки.Форма.Форма.Форма(90)}: Error calling context method (ПолучитьОбъект)"
    +"\nf08d92f8-9eb2-4e19-9dd9-977d907cec2d"
    +"\ndc31263e-ecbf-41bd-9b3a-7b55897d5fd6: Lock conflict during transaction:"
    +"\nMicrosoft SQL Server Native Client 11.0: Transaction (Process ID 55) was deadlocked on lock resources with another process and has been chosen as the deadlock victim. Rerun the transaction./;"
    +"\nHRESULT=80004005, SQLSrvr: SQLSTATE=40001, state=33, Severity=D, native=1205, line=1");
    return this;
  }

  public V8LogFileConstructor addSQlTimeout() {
    logData.add(
    "51:50.126000-0,EXCP,6,process=rphost,p:processName=test,t:clientID=110,t:applicationName=1CV8C,t:computerName=yardnout,t:connectID=24,SessionID=18,Usr=DefUser,AppID=1CV8C,Exception=DataBaseException,Descr='Lock conflict during transaction:"
    +"\nMicrosoft SQL Server Native Client 11.0: Lock request time out period exceeded."
    +"\nHRESULT=80040E31, SQLSrvr: SQLSTATE=HYT00, state=33, Severity=10, native=1222, line=1"
    +"\n',Context=Форма.Записать : Документ.ДокументДляБлокировки.ФормаОбъекта");
    return this;
  }

  public V8LogFileConstructor addDBMSSQL() {
    logData.add("41:58.067005-1,DBMSSQL,2,process=rphost,p:processName=test,t:clientID=59,t:applicationName=1CV8C,t:computerName=yardnout,t:connectID=18,SessionID=63,Usr=DefUser,AppID=1CV8C,Trans=0,dbpid=54,Sql=\"{call sp_executesql(N'SELECT Creation,Modified,Attributes,DataSize,BinaryData FROM Config WHERE FileName = @P1 ORDER BY PartNo', N'@P1 nvarchar(128)', N'40047d1f-4006-473a-a10f-4110ae135d02')}\",Rows=1,Context=Система.ПолучитьФорму : Документ.ПоступлениеТоваров.ФормаОбъекта");
    return this;
  }

  public V8LogFileConstructor addSDBL() {
    logData.add(";21:04.471025-6844025,SDBL,2,process=rphost,p:processName=test,t:clientID=59,t:applicationName=1CV8C,t:computerName=yardnout,t:connectID=18,SessionID=63,Usr=DefUser,AppID=1CV8C,Trans=0,Func=HoldConnection,Context=Форма.Вызов :Обработка.Взаимоблокировки.Форма.Форма.Модуль.УправляемаяВзаимоблокировкаНаСервере");
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
