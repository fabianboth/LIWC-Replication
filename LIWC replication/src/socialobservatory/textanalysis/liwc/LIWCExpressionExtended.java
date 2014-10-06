package socialobservatory.textanalysis.liwc;

import java.util.ArrayList;
import java.util.regex.Matcher;

/**
 * @author Simon Caton and Fabian Both
 */
public class LIWCExpressionExtended extends LIWCExpression {
	
	private ArrayList<LIWCOptionalCategroySet> optionalList;

	public LIWCExpressionExtended(String s, LIWCDictionary dictionary) {
		super(s,dictionary);
		optionalList = new ArrayList<LIWCOptionalCategroySet>();
	}
	
	public void addOptionalCategorySet(LIWCOptionalCategroySet optionalSet){
		optionalList.add(optionalSet);
	}
	public ArrayList<LIWCOptionalCategroySet> getOptionalCategoryList(){
		return optionalList;
	}
	
	@Override
	public boolean compare(String[] s) {
    	Matcher m;
    	Matcher optionalMatcher;
    	ArrayList<LIWCCategory> categories;
        int count = 0;
        
        for(int i = 0; i  < s.length; i++){
    		m = getPattern().matcher(s[i]);
    		if(m.find()){
            	categories = new ArrayList<LIWCCategory>();
    			//increase normal categories
    			for (LIWCCategory c : getCategories()) {
	                if (c == null) {
	                    System.out.println(this);
	                }
	                categories.add(c);
	            }
    			//investigate optional categories
    			for(LIWCOptionalCategroySet ocs : optionalList){
    				optionalMatcher = null;
    				if(ocs.getPreFlag() && i > 0){		//pre condition
    					optionalMatcher = ocs.getOptionalPattern().matcher(s[i-1]);
    				}else if(!ocs.getPreFlag() && i < s.length-1){		//post condition
    					optionalMatcher = ocs.getOptionalPattern().matcher(s[i+1]);
    				}
    				if(optionalMatcher != null){
    					if(optionalMatcher.find()){
    						LIWCCategory cat = ocs.getOptionalCategory();
    						if(cat != null){
    							categories.add(cat);
    						}
    					}else{
    						LIWCCategory cat = ocs.getDefaultCategory();
    						if(cat != null){
    							categories.add(cat);
    						}
    					}
    				}
    			}
    			synchronized(dictionary){
        			dictionary.increaseAllCategories(i,new LIWCExpressionCategoryPair(categories,this));
    				dictionary.notifyAll();
    			}
    			count++;
    		}
        }
        return count > 0;
	}

    @Override
    public String toString() {
        return "Expression{ descripter=" + getDescripter() + ", categories=" + getCategories() + ", " + optionalList.toString() + '}';
    }
}
