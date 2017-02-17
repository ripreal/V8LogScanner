package org.v8LogScanner.commonly;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class SimpleTable extends AbstractCollection<SimpleTable.Entry> implements Iterable<SimpleTable.Entry> {

	private ArrayList<Entry> rows;
	private String[] columns;
	
	public SimpleTable(String columnsName){
		rows = new ArrayList<Entry>();
		columns = columnsName.split(",");
	}
	
	public Entry addRow(){
		
		Entry newEntry = new Entry();
		for (int i=0; i<columns.length; i++)
			newEntry.put(columns[i], null);
		
		rows.add(newEntry);
		
		return newEntry;
	}
	
	public void addColumn(String name){
		for(Entry currRow : rows){
			currRow.put(name, null);
		}
	}
	
	public Entry getRow(int index){
		return rows.get(index);
	}
	
	@SuppressWarnings("unchecked")
	public <T> ArrayList<T> unloadColumn(String colName, ArrayList<T> arr){
		
		for(Entry row : rows){
			arr.add((T)row.get(colName));
		}
		return arr;
	}
	
	public double total(String colName){
		
		return rows.
			stream().mapToDouble(
			(n -> (double)n.get(colName))).sum();
	}
	
	public String[][] toStringArray(){
		
		String[][] result = new String[rows.size()][columns.length];
		
		Entry currRow = null;
		int i,j;
		for (i=0; i<rows.size(); i++){
			currRow = getRow(i);
			for(j=0; j<columns.length; j++)
				result[i][j] = (String)currRow.get(columns[j]);
		}
		
		return result;
	}
	
	public void addAll(SimpleTable other){
		
		for (Entry srcRow : other){
			Entry newRow =  addRow();
			newRow.putAll(srcRow);
		}
	}
	
	@Override
	public int size() {
		return rows.size();
	}

	@Override
	public Iterator<Entry> iterator() {
		//instantiating anonymous class
		Iterator<Entry> it = new Iterator<Entry>(){
			private int currentIndex = 0;

			@Override
			public boolean hasNext() {
				return currentIndex < rows.size() && rows.get(currentIndex)!= null;
			}

			@Override
			public Entry next() {
				return rows.get(currentIndex++);
			}

			@Override
			public void remove() {
			
			}
		};
		return it;
	}
	
	@Override
	public void clear(){
		rows.clear();
	}
	
	public class Entry{
		
		private HashMap <String, Object> it = new HashMap <String, Object>();
		
		public void put(String key, Object value){
			it.put(key, value);
		}
		
		public Object get(String key){
			
			if (it.containsKey(key))
				return it.get(key);
			else{
				try {
					throw new IncorrectColException();
				} catch (IncorrectColException e) {
					ExcpReporting.LogError(this.getClass(), e);
				}
				return null;
			}
		}
		
		public void putAll(Entry other){
			it.putAll(other.getMap());
		}
		
		public HashMap<String, Object> getMap(){
			return it;
		}
	
		private class IncorrectColException extends Exception{
			private static final long serialVersionUID = -6768497416328793322L;

			public IncorrectColException() {
				super("Column does not exist!.");
			}
		}
	
	}
	
}

interface Loader<D extends ArrayList<T>, T>{
	
	void load();
	
}

