package socialobservatory.textanalysis.liwc;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import socialobservatory.util.utilities;

/**
 * @author Simon Caton and Fabian Both
 */
public class LIWCExpression {

    private ArrayList<LIWCCategory> categories;
    private String descripter;
    private String word;
    private Pattern pattern;
    private double expressionLength = 0;
    protected LIWCDictionary dictionary;

	public static ArrayList<Integer> matches;

    public LIWCExpression(String s, LIWCDictionary dictionary) {
    	this.word = s;
        setDescripter(s);
        this.dictionary = dictionary;
        categories = new ArrayList<LIWCCategory>();
        
        if(matches == null){
        	matches = new ArrayList<Integer>();
        }
    }

    public double getExpressionLength(){
    	return expressionLength;
    }
    
    public double getWordLength(){
    	return word.length();
    }
    
    public String getWord(){
    	return word;
    }
    
    public void addLIWCCategory(LIWCCategory c) {
        categories.add(c);
    }

    public ArrayList<LIWCCategory> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<LIWCCategory> categories) {
        this.categories = categories;
    }

    public String getDescripter() {
        return descripter;
    }

    public void setDescripter(String s) {

    	descripter = utilities.strToRegex(s);
        
        pattern = Pattern.compile(descripter);
    	expressionLength = descripter.length();
		if(descripter.contains("*")){
			expressionLength = expressionLength - utilities.expressionLengthMod;
		}
    }

    public Pattern getPattern(){
    	return pattern;
    }
    
    public boolean compare(String[] s) {
        int count = 0;
        
    	for(int i = 0; i < s.length; i++){
    		String word = s[i];
	        Matcher m = pattern.matcher(word);
	        while (m.find()) {
	        	synchronized(dictionary){
		        	dictionary.increaseAllCategories(i,new LIWCExpressionCategoryPair(categories,this));
		        	dictionary.notifyAll();
	        	}
	        }

            count++;
        }

        return count > 0;
    }
    
    public boolean matches(String s) {
    	boolean result = false;
    	Matcher m = pattern.matcher(s);
    	if (m.find()) {
    		result = true;
    	}
    	return result;
    }
    
    public boolean hasCategory(int ID){
    	boolean result = false;
    	for(LIWCCategory c : categories){
    		if(ID == c.getID()){
    			result = true;
    			break;
    		}
    	}
    	return result;
    }

    @Override
    public String toString() {
        return "Expression{ descripter=" + descripter + ", categories=" + categories + '}';
    }
}
