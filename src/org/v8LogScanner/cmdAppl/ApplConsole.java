/////////////////////////////////////
//cmdAppl package define and implements console application with COMMAND, SINGLETONE patterns
///////////////////////////////////

package org.v8LogScanner.cmdAppl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import org.v8LogScanner.commonly.Constants;
import org.v8LogScanner.commonly.ExcpReporting;
import org.v8LogScanner.commonly.Strokes;

public class ApplConsole {
	
	public BufferedReader in  = null;
	public PrintStream out    = null;
	private String title = "New application";

	public ApplConsole(InputStream in, PrintStream out){
		this.in           = new BufferedReader(new InputStreamReader(in));
		this.out          = out;
	}
	
	public ApplConsole(){
		this.in           = new BufferedReader(new InputStreamReader(System.in));
		this.out          = System.out;
		
	}

	public void runAppl(MenuCmd currMenu){
		try
		{
			String userInput = "";
			do
			{
				if (isValid(userInput)){
					
					if (userInput.compareTo("q") == 0)
						break;
					else if (Strokes.isNumeric(userInput)){
					
						int inputNum = 0;
						inputNum = Integer.parseInt(userInput);
						if (inputNum <= currMenu.size()){
							currMenu = currMenu.clickItem(inputNum-1);
							if (currMenu == null)
								break;
						}
					}
				}
				clearConsole();
				currMenu.showMenu(out, title);
			}
			while((userInput = in.readLine()) != null);
		}
		catch (IOException e){
			ExcpReporting.LogError(this, e);
		}
	}
	
	private boolean isValid(String userInput){
		
		if (userInput.length() == 0)
			return false;
		else if (userInput.compareTo("q") == 0)
			return true;
		else if (Strokes.isNumeric(userInput)){
			try{ 
				Integer.parseInt(userInput);
			}
			catch(NumberFormatException e){
				showModalInfo(String.format("Number is too big, only integers until %s accepting",Integer.MAX_VALUE));
				return false;
			}
			return true;
		}
		else 
			return true;
	}
	
	public void clearConsole(){
		String osName = Constants.osType;
		
		if (osName.matches("(?i).*windows.*")){
			ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "cls");
			try {
				pb.inheritIO().start().waitFor();
			} catch (InterruptedException | IOException e) {
				ExcpReporting.LogError(this, e);
			}
		}
		else if (osName.matches("(?i).*linux.*"))
			out.print("\033[H\033[2J");
	}
	
	public String askInput(String[] textMessage, Predicate<String> predicate, boolean clearConsole) {
		
		if(clearConsole) 
			clearConsole();
		
		String userInput= "";
		for(int i = 0; i < textMessage.length; i++)
			out.println(textMessage[i]);
		try {
			while((userInput = in.readLine()) != null){
				if (!isValid(userInput))
					continue;
				if (userInput.charAt(0) == 'q'){ 
						userInput = null;
						break;
				}
				if (predicate.test(userInput))
					break;
				clearConsole();
				out.println(String.format("Input \"%s\" incorrect! (q - exit)", userInput));
				for(int i = 0; i < textMessage.length; i++)
					out.println(textMessage[i]);
			}
		} catch(IOException e) {
			ExcpReporting.LogError(this, e);
		}
		return userInput;
	}

	private <F extends List<T>, T> String askInputFromList(String promt, F list, int start, int end){
		
		if (list.size() == 1)
			return "0";
		
		out.println();
		
		String[] messages = new String[end + 1];
		
		for(int i = start; i < end; i++)
			messages[i] = String.format("%s %s", i, list.get(i));
		messages[end] = promt;
		
		String userInput = askInput(
				messages, n -> existsInList(n, list), true);
		
		return userInput;
	}

	public <T> String askInputFromList(String textMessage, T[] list, int start, int end){
		
		ArrayList<T> intermArray = new ArrayList<>();
		for (int i = 0; i< list.length; i++)
			intermArray.add(list[i]);
		
		return askInputFromList(textMessage, intermArray, start, end);
	}
	
	public <F extends List<T>, T> String askInputFromList(String textMessage, F list){
		return askInputFromList(textMessage, list, 0, list.size());
	}
	
	public <T> String askInputFromList(String textMessage, T[] list){
		return askInputFromList(textMessage, list, 0, list.length);
	}
	
	public void showModalInfo(String text){
		out.println(text);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			ExcpReporting.LogError(this, e);
		}
	}
	
	public <T> void showInfoStack(List<T> info){
		info.forEach(out :: println);
	}
	
	public void showNumberedList(ArrayList<String> info){
		for(int i = 0; i < info.size(); i++)
			out.println(String.format("%s %s", i, info.get(i)));
	}
	
	public void showNumberedList(String[] info){
		for(int i = 0; i < info.length; i++)
			out.println(String.format("%s %s", i, info[i]));
	}
	
	public <F,T extends List<F>> boolean existsInList(String n, T list){
		if (isValid(n) && Strokes.isNumeric(n)){
			int inputNum = Integer.parseInt(n, 10);
			return list.size() > inputNum;
		}
		else{
			return false;
		}
	}
	
	public <T> boolean existsInList(String n, T[] list){
		if (n.matches("\\d+")){
			int inputNum = Integer.parseInt(n, 10);
			return list.length > inputNum;
		}
		else{
			return false;
		}
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
}
