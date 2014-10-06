package socialobservatory.textanalysis.liwc.analysers;

import java.util.ArrayList;

import socialobservatory.textanalysis.liwc.LIWCCategory;
import socialobservatory.textanalysis.liwc.LIWCDictionary;
import socialobservatory.textanalysis.liwc.LIWCExpression;

/**
 * @author Simon Caton and Fabian Both
 */
public class LIWCExpressionAnalyser extends SimpleLIWCAnalyser {

    private final ArrayList<LIWCExpression> expressions;
    private final ArrayList<LIWCCategory> categories;

    public LIWCExpressionAnalyser(String[] input, String ID, ArrayList<LIWCExpression> expressions, ArrayList<LIWCCategory> categories, String language) {

        super(input, ID, language);
        this.expressions = expressions;
        this.categories = categories;
    }

    @Override
    protected void setupLIWCWrapper() {
        LIWCDictionary d = new LIWCDictionary(getID(),language);
        d.setExpressions(expressions);
        d.setCategories(categories);
        liwc.setDictionary(d);
        liwc.setAutoNormalise(false);
    }
}
