package org.v8LogScanner.logs;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.regex.Pattern;

import org.v8LogScanner.commonly.ExcpReporting;
import org.v8LogScanner.commonly.SimpleTable;

public class LogsDirVisitor extends SimpleFileVisitor<Path> {
  
  public enum AcceptedLogTypes {ALL, LOG}
  
  private Pattern dirPattern = null;
  private Date startDate = null;
  private Date endDate = null;
  private Date currfileDate = null;
  private boolean currDirAccepted = true;
  private SimpleTable logFilesData = new SimpleTable("filePath, size, fileDate");
  private AcceptedLogTypes acceptedType = AcceptedLogTypes.LOG;
  
  public LogsDirVisitor(Date _startDate, Date _endDate){
    startDate = _startDate;
    endDate = _endDate;
  }
  
  public void setDirPattern(String dirPattern) {
    // Empty string means that file instead directory was passed for walking tree.
    if (!dirPattern.isEmpty())
      this.dirPattern = Pattern.compile(dirPattern, 
          Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
  }
  
  public void acceptLogType(AcceptedLogTypes acceptedType) {
    this.acceptedType = acceptedType;
  }
  
  public SimpleTable getLogFilesData(){ return logFilesData;}
  
  @Override
  public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {

    currDirAccepted = (acceptDir(dir));
    
    return FileVisitResult.CONTINUE;
  }
  
  @Override
  public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
    
    if (!currDirAccepted)
      return FileVisitResult.CONTINUE;
    
    if (acceptFile(file))
      addTableEntry(file, attrs);

    return FileVisitResult.CONTINUE;
  }
  
  @Override
  public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
    
    String errDescr = String.format("Cannot access to the dir or file with path: %s. It skipped.", 
        file.toString());
    
    ExcpReporting.logInfo(errDescr);
    
    return FileVisitResult.CONTINUE;
    
  }
  
  private boolean acceptDir(Path dir){
    
    boolean isAcceptable = false;

    Path lastName =  dir.getFileName();
    
    if (lastName != null && dirPattern != null)
      isAcceptable = dirPattern.matcher(lastName.toString()).matches();
    else
      // when searching for a system drive c:\\
      isAcceptable = true;

    return isAcceptable;
  }
  
  private boolean acceptFile(Path file){
    
    boolean isAcceptable = false;
    
    if (Files.isDirectory(file))
      return false;
    
    String currFileName = file.getFileName().toString();
    if (acceptedType == AcceptedLogTypes.LOG && logFileMatches(currFileName))
      return false;
    // this operator must be placed above null checking of startDate and endDate 
    currfileDate = LogsOperations.parseDate(currFileName);
    
    if (startDate == null && endDate == null)
      return true;
    
    if (currfileDate.equals(startDate)|| currfileDate.equals(endDate)||
      (currfileDate.before(endDate) && currfileDate.after(startDate)))
      
      isAcceptable = true;
    
    return isAcceptable;
  }
  
  private boolean logFileMatches(String onlyFileName){
    return onlyFileName.matches("(?i).*\\.log$");
  }

  private SimpleTable.Entry addTableEntry(Path path, BasicFileAttributes attrs) throws IOException{
    
    SimpleTable.Entry newRow = logFilesData.addRow();
    newRow.put("filePath", path.toAbsolutePath().toString());
    double size = ((Number) attrs.size()).doubleValue();
    newRow.put("size", size / 1024 / 1024);
    newRow.put("fileDate", currfileDate);
    return newRow;
  }
 
}

