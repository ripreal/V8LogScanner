package org.v8LogScanner.commonly;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Filter<T1> implements Iterable<T1>, Serializable{
	
	private static final long serialVersionUID = -5249716837573681233L;

	public enum ComparisonTypes {equal, greater, less, range};
	
	public ComparisonTypes comparisonType = ComparisonTypes.equal;
	
	private List<T1> elements = new ArrayList<T1>();
	
	public Filter<T1> add(T1 val){
		elements.add(val);
		return this;
	}
	
	public  boolean isActive(){
		return elements.size()>0;
	}
	
	public void reset(){
		elements.clear();
	}
	
	public int size(){
		return elements.size();
	}
	
	public T1 get(int index){
		return elements.get(index);
	}
		
	public void set(int index, T1 element){
		elements.set(index, element);
	}
	
	public Iterator<T1> iterator() {
		return elements.iterator();
	}
	
	public List<T1> getElements() {
	  return elements;
	}
	
	public void setElements(List<T1> elements) {
	  this.elements = elements;
	}
}

