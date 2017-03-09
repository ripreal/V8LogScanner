package org.v8LogScanner.commonly;

import java.util.HashMap;

public class CacheVals<K, V>  {
  
  private HashMap<K, V> storage = new HashMap<>();
  
  public void putVal(K key, V val){
    storage.put(key, val);
  }
  
  public V get(K key){
    return storage.get(key);
  }
  
  public void remove(K key){
    storage.remove(key);
  }
  
}

