package org.v8LogScanner.commonly;

public class Strokes {

  public static String repeat(String source, int counts){
    String result = source;
    for(int i = 1; i < counts; i++){
      result = result.concat(source);
    }
    return result;
  }
  
  public static String[] toStringArray(String... src){
    
    int totalSize = 0;
    for(int i = 0; i < src.length; i++){
      totalSize += src[i].length();
    }
    
    String[] result = new String[totalSize];
    
    for(int i = 0; i < src.length; i++){
      for(int j = 0; j < src[i].length(); j++)
        result[j] = ((Character) src[i].charAt(j)).toString();
    }
    return result;
  }
  
  public static boolean isOdd(int var){
    double dobVar = (double) var;
    int intVar = (int) dobVar / 2;
    return dobVar / 2 == intVar;
  }

  public static boolean isNumeric(String str) {
    if (str == null)
      return false;
    
    char[] chars = str.toCharArray();
  
    for (int i = 0; i < chars.length; i++){
      char c = chars[i];
      if (c < '0' || c> '9')
        return false;
    }
    return true;
  }
  
  public static boolean isNumericAtIndex(String str, int index){
    char c = str.charAt(index);
    if (c < '0' || c > '9')
      return false;
    return true;
  }
  
}



