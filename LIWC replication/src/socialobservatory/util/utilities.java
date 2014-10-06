package socialobservatory.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Simon Caton and Fabian Both
 */
public class utilities {
	
	private static boolean umlautFlag = false;
	private static boolean improved = false;
	public final static double expressionLengthMod = 3.5;
    
    public static String[] split(String s) {
    	String[] words = new String[0];
    	if(!s.equals("")){
    		words = s.split("\\s+");
    	}
        return words;
    }
    
    //method for consistent string preparation
    public static String prepareString(String s, String language){
    	s = s.toLowerCase();
    	s = s.replaceAll("\\uFEFF", "");
    	if(language.equals("en")){
    		if(improved){
    			s = s.replaceAll("https?://\\S+\\s?", " ");
    			s = s.replaceAll("’","'");
    			s = s.replaceAll("[^a-z0-9']", " ");
    			s = s.replaceAll("(?<=(\\s|^))'+(?=(\\s|$))", "");
    			s = s.replaceAll("(?<=(\\s|^))'+(?=[a-z])", "");
    			s = s.replaceAll("(?<=[a-z])'+(?=(\\s|$))", "");
    		}else{
    			s = s.replaceAll("[!-&(-,./:-@\\[-`{-~…“”‘‚«»]", "");
    			s = s.replaceAll("\\-", " ");
    			s = s.replaceAll("(?<=(\\s|^))'+(?=(\\s|$))", "");
    			s = s.replaceAll("’", "'");
    			s = s.replaceAll("(?<=(\\s|^))'+(?=[a-z])", "");
    			s = s.replaceAll("(?<=[a-z])'+(?=(\\s|$))", "");
    			s = s.replaceAll("(?<=(\\s|^))–(?=(\\s|$))", "");
    		}
    		
    	}else if(language.equals("de")){
    		if(improved){
    			s = s.replaceAll("https?://\\S+\\s?", " ");
    			s = s.replaceAll("’","'");
    			s = s.replaceAll("[^a-z0-9öäüß']", " ");
    			s = s.replaceAll("(?<=(\\s|^))'+(?=(\\s|$))", "");
    			s = s.replaceAll("(?<=(\\s|^))'+(?=[a-z])", "");
    			s = s.replaceAll("(?<=[a-z])'+(?=(\\s|$))", "");
    		}else{
	    		s = s.replaceAll("[!-&(-,./:-@\\[-`{-~…“”‘‚'’«»]", " ");
	    		s = s.replaceAll("(?<=(\\s|^))[0-9]+(?=[a-z])", "");
	    		s = s.replaceAll("(?<=[a-z])[0-9]+(?=(\\s|$))", "");
    		}
    	}
		
		s = s.trim();
		if(umlautFlag){
			s = removeUmlaut(s);
		}
    	return s;
    }
    
    public static String replicateFalseUmlauts(String s){
    	s = s.replaceAll("ö", "o ");
    	s = s.replaceAll("ä", "a ");
    	s = s.replaceAll("ü", "u ");
    	s = s.replaceAll("ß", "");
    	return s;
    }
    
    //only lowercase is matched
    private static String removeUmlaut(String s){
    	s = s.replaceAll("ö", "oe");
    	s = s.replaceAll("ä", "ae");
    	s = s.replaceAll("ü", "ue");
    	return s;
    }
    
    public static void setUmlautFlag(boolean b){
    	umlautFlag = b;
    }
    
    public static boolean getUmlautFlag(){
    	return umlautFlag;
    }
    
    public static void setReplicationMode(boolean improved){
    	utilities.improved = improved;
    }
    
    public static boolean getReplicationMode(){
    	return improved;
    }
    
    //if changed, expressionLengthMod needs to be adapted
    public static String strToRegex(String s){
    	String regex;
    	if (s.contains("*")) {
    		regex = "^" + s.replaceAll("\\*", ".*?").trim() + "$";
    	} else {
    		regex = "^" + s + "$";
    	}
    	return regex;
    }
    
    public static void printLog(String s){
    	File f = new File("./logfile.txt");
		try {
	    	PrintWriter out = new PrintWriter(new FileWriter(f,true));
	    	Date date = new Date(System.currentTimeMillis());
	        SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	    	out.println("["+df2.format(date)+"] " + s);
	        out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
