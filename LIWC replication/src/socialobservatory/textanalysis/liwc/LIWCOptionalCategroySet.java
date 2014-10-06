package socialobservatory.textanalysis.liwc;

import java.util.regex.Pattern;

/**
 * @author Simon Caton and Fabian Both
 */
public class LIWCOptionalCategroySet {
	private Pattern optionalPattern;
	private LIWCCategory optionalCategory;
	private LIWCCategory defaultCategory;
	private boolean preFlag;
    
    public void setOptionalPattern(Pattern optionalPattern){
    	this.optionalPattern = optionalPattern;
    }
    
    public void setOptionalCategory(LIWCCategory optionalCategory){
    	this.optionalCategory = optionalCategory;
    }

    public void setDefaultCategory(LIWCCategory defaultCategory){
    	this.defaultCategory = defaultCategory;
    }
    
    public void setPreFlag(boolean flag){
    	preFlag = flag;
    }
    
    public Pattern getOptionalPattern(){
    	return optionalPattern;
    }
    
    public LIWCCategory getOptionalCategory(){
    	return optionalCategory;
    }

    public LIWCCategory getDefaultCategory(){
    	return defaultCategory;
    }
    
    public boolean getPreFlag(){
    	return preFlag;
    }
    
    public boolean isEmpty(){
    	return optionalPattern == null && optionalCategory == null && defaultCategory == null;
    }
    
    @Override
    public String toString(){
    	String result = "optionalPattern=" + optionalPattern.toString() + ", optionalCategory=" + optionalCategory.toString() + ", defaultCategory=" + defaultCategory;
    	
    	return result;
    }
}
