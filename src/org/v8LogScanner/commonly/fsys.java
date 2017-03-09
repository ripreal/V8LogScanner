package org.v8LogScanner.commonly;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.v8LogScanner.rgx.RgxReader;

public class fsys {

  // for reading small files  
  public static String readAllFromFile(String fullFileName){
    return readAllFromFile(Paths.get(fullFileName));
  }

  public static String readAllFromFile(Path path){
    
    String result = "";
      try(SeekableByteChannel sbc = Files.newByteChannel(path)) {
        ByteBuffer buf = ByteBuffer.allocate(1024*128);
        while (sbc.read(buf) > 0) {
          buf.rewind();
          result += Charset.defaultCharset().decode(buf);
          buf.flip();
          }
      }
      catch (IOException e) {
        ExcpReporting.LogError(fsys.class, e);
      }
      return result;
  }

  // requires large amount of memory
  public static ArrayList<String> readFileToBuffer(String fullFileName){
      
    ArrayList<CharBuffer> result = new ArrayList<CharBuffer>();
    try(SeekableByteChannel sbc = Files.newByteChannel(Paths.get(fullFileName))) {
       
      int filSize = (int) Files.size(Paths.get(fullFileName));
      
      ByteBuffer buf = ByteBuffer.allocate(filSize);
       while (sbc.read(buf) > 0) {
        buf.rewind();
        result.add(Charset.defaultCharset().decode(buf));
        buf.flip();
      }
    } 
    catch (IOException e) {
      ExcpReporting.LogError(fsys.class, e);
    }
    ArrayList<String> res = new ArrayList<String>(); 
    try (RgxReader reader = new RgxReader(result.get(0))){
      reader.readFromBuffer(0);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      ExcpReporting.LogError(fsys.class, e);
    }
    return res;
  }  
    
  public static String readFileWithRegularExp(String fullFileName, String rxgDelimiter, String regExp) throws IOException{
    
    String result = "";
    
    File file = new File(fullFileName);

    Scanner sc = new Scanner(file);
    sc.useDelimiter(Pattern.compile(rxgDelimiter));
    while (sc.hasNext()) {  
      try{
        //result = sc.next(pattern);
        result += sc.next();        
        }        
      catch (java.util.InputMismatchException e){
        // will be invoked when an input stroke does not match the pattern
        // therefore no actions is required
      }
    }
    sc.close();
    return result;
  }
  
  public static void WriteInNewFile(String text, String fullFileName ) throws IOException{
    
    BufferedWriter fl = Files.newBufferedWriter(Paths.get(fullFileName), Charset.defaultCharset(), 
          StandardOpenOption.WRITE, 
          StandardOpenOption.CREATE, 
          StandardOpenOption.TRUNCATE_EXISTING);
    
    fl.write(text);
    fl.close();
  }
  
  public static boolean DeleteFile(String fullFileName) throws IOException{
    return Files.deleteIfExists(Paths.get(fullFileName));
  }
  
  public static boolean pathExist(String filename){
    
    try {
      Path path = Paths.get(filename);
      File file = path.toFile();
      return file.exists();
    }
    catch(InvalidPathException  e){
      // User input a illegal symbol. There is no need to record a exception
    }
    catch(UnsupportedOperationException | SecurityException e){
      ExcpReporting.LogError(fsys.class, e);
    }
    return false;
  }
}
