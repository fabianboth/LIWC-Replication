package socialobservatory.textanalysis.liwc;

import java.util.ArrayList;

/**
 * @author Simon Caton and Fabian Both
 */
public class LIWCExpressionCategoryPair {
	
	private ArrayList<LIWCCategory> categories;
	private LIWCExpression expression;
	
	public LIWCExpressionCategoryPair(ArrayList<LIWCCategory>  categories, LIWCExpression expression){
		this.categories = categories;
		this.expression = expression;
	}

	public ArrayList<LIWCCategory> getCategory(){
		return categories;
	}
	
	public void setCategory(ArrayList<LIWCCategory> categories){
		this.categories = categories;
	}
	
	public LIWCExpression getExpression(){
		return expression;
	}
}
