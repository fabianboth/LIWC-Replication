package socialobservatory.textanalysis.liwc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import socialobservatory.util.utilities;

/**
 * @author Simon Caton and Fabian Both
 */
public class LIWCDictionary {
    
	@SuppressWarnings("unused")
	private String name;
    private String language;
    private ArrayList<LIWCExpression> expressions;
    private ArrayList<LIWCExpressionExtended> expressionsExtended;
    private ArrayList<LIWCCategory> categories;
    private HashMap<Integer, LIWCExpressionCategoryPair> matchedWords;

    public LIWCDictionary(String name, String language) {
        this.name = name;
        this.language = language;
        expressions = new ArrayList<LIWCExpression>();
        expressionsExtended = new ArrayList<LIWCExpressionExtended>();
        matchedWords = new HashMap<Integer, LIWCExpressionCategoryPair>();
    }
    
    public void addExpression(LIWCExpression e) {
        expressions.add(e);
    }
    
    public void addExpressionExtended(LIWCExpressionExtended e) {
        expressionsExtended.add(e);
    }
    
    public void setExpressions(ArrayList<LIWCExpression> expressions) {
        this.expressions = expressions;
    }
    
    public void setExpressionsExtended(ArrayList<LIWCExpressionExtended> expressions) {
        this.expressionsExtended = expressions;
    }
    
    public void setLanguage(String language){
    	this.language = language;
    }
    
    public String getLanguage(){
    	return language;
    }
    
    public ArrayList<LIWCExpression> getExpressions() {
        return expressions;
    }
    
    public ArrayList<LIWCExpressionExtended> getExpressionsExtended() {
        return expressionsExtended;
    }

    public void setCategories(ArrayList<LIWCCategory> categories) {
        this.categories = categories;
    }

    public ArrayList<LIWCCategory> getCategories() {
        return categories;
    }
    
    public void clear() {
        for (LIWCCategory c : categories) {
            c.clear();
        }
        matchedWords.clear();
    }

    
    public void categorise(String[] s) {
        for (LIWCExpression w : expressions) {
            w.compare(s);
        }
    }
    
    public synchronized void increaseAllCategories(int wordIndex,LIWCExpressionCategoryPair pair){
    	ArrayList<LIWCCategory> categories = pair.getCategory();
    	LIWCExpression expression = pair.getExpression();
    	if(language.equals("en")){
    		for(LIWCCategory lc : categories){
    			lc.inc();
    		}
    	}else{
    		double expressionLength = expression.getExpressionLength();
    		LIWCExpressionCategoryPair match = matchedWords.get(wordIndex);
    		if(match == null){
    			for(LIWCCategory cat : categories){
        			cat.inc();
        		}
    			matchedWords.put(wordIndex,pair);
    		}else if(match.getExpression().getExpressionLength() < expressionLength){
				decreaseAllCategories(match.getCategory());
				for(LIWCCategory cat : categories){
        			cat.inc();
        		}
    			matchedWords.put(wordIndex,pair);
    		}
    	}
    	this.notifyAll();
    }
    
    private void decreaseAllCategories(ArrayList<LIWCCategory> lc){
    for(LIWCCategory cat : lc){
			cat.dec();
		}
    }
    
    public void categoriseExtended(String[] s) {
        for (LIWCExpression w : expressionsExtended) {
            w.compare(s);
        }
    }
    
    public static class Factory {
        
        public static LIWCDictionary loadDictionary(File f,String language) throws IOException {
            
            if (f.exists()) {
            	String line = null;
            	
                BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
                LIWCDictionary d = new LIWCDictionary(f.getName().substring(0, f.getName().indexOf(".")),language);
                
                boolean categories = true;
                in.readLine();
                
                HashMap<Integer, LIWCCategory> cats = new HashMap<Integer, LIWCCategory>();
                
                String[] array;
                String[] catSplit;
                LIWCExpression w;
                LIWCExpressionExtended wExt;
                LIWCOptionalCategroySet optionalSet;
                ArrayList<String> specialLines = new ArrayList<String>();
                Pattern pattern = Pattern.compile(".*[/]+.*");
                Matcher m;
                
                while ((line = in.readLine()) != null) {
                    if (!line.trim().equals("%")) {
                        array = line.split("\t");
                        
                        if (categories) {
                            cats.put(new Integer(array[0]), new LIWCCategory(array[1], Integer.parseInt(array[0])));
                        } else {
                        	m = pattern.matcher(line);
                        	if(!m.find()){
	                            w = new LIWCExpression(array[0],d);
	                            for (int i = 1; i < array.length; i++) {
	                                w.addLIWCCategory(cats.get(new Integer(array[i])));
	                            }
	                            d.addExpression(w);
                        	}else{
                        		specialLines.add(line);	//generate extended Expression later on
                        	}
                        }
                        
                    } else {
                        categories = false;
                        d.setCategories(new ArrayList<LIWCCategory>(cats.values()));
                    }
                }
                in.close();

                //LIWCExpressionsExtended
        		for(String l : specialLines){
                	array = l.split("\t");
                	wExt = new LIWCExpressionExtended(array[0],d);
                	
                	for(int c = 1; c < array.length; c++){	  
                		
                		if(array[c].contains(")") || array[c].contains(">")){
                    		optionalSet = new LIWCOptionalCategroySet();
                			if(array[c].contains(")")){
                				optionalSet.setPreFlag(true);
                			}else{
                				optionalSet.setPreFlag(false);
                			}
                			catSplit = array[c].split("/");
                			if(catSplit.length == 2){
                				optionalSet.setDefaultCategory(cats.get(new Integer(catSplit[1])));
                    		}
                			catSplit = catSplit[0].split("[)>]");
                			optionalSet.setOptionalCategory(cats.get(new Integer(catSplit[1])));
                			catSplit[0] = catSplit[0].replaceAll("[\\(<]","");
                			optionalSet.setOptionalPattern(Pattern.compile(generateRegex(catSplit[0],d)));

                			wExt.addOptionalCategorySet(optionalSet);
                		}else{
                			wExt.addLIWCCategory(cats.get(new Integer(array[c])));
                		}
                	}
                	d.addExpressionExtended(wExt);
        		}
        		
                return d;
                
            } else {
                throw new FileNotFoundException("Cannot find: " + f);
            }
            
        }
        
        public static String generateRegex(String cat, LIWCDictionary d){
        	Pattern pattern = Pattern.compile("[0-9]+");
    		Matcher m;
        	String[] categories = cat.split(" ");
        	String regex = "";
			
        	for(String word : categories){
        		m = pattern.matcher(word);
				if(m.find()){
					for(LIWCExpression le : d.getExpressions()){
						if(le.hasCategory(Integer.parseInt(word))){
							regex = regex + "|(" + le.getDescripter() + ")";
						}
					}
				}else{
					regex = regex + "|(" + utilities.strToRegex(word) + ")";
				}
			}
        	if(regex.length() > 0){
				regex = regex.substring(1);
			}
				
        	return regex;
        }
    }
}
