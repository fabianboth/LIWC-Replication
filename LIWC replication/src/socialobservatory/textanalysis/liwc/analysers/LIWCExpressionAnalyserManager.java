package socialobservatory.textanalysis.liwc.analysers;

import java.util.ArrayList;

import socialobservatory.textanalysis.liwc.LIWCCategory;
import socialobservatory.textanalysis.liwc.LIWCDictionary;
import socialobservatory.textanalysis.liwc.LIWCExpression;
import socialobservatory.textanalysis.liwc.LIWCListener;

/**
 * @author Simon Caton and Fabian Both
 */
public class LIWCExpressionAnalyserManager extends LIWCAnalyserManager {

    public LIWCExpressionAnalyserManager(String ID, String input, LIWCDictionary dictionary, int numThreads, LIWCListener l) {
        super(ID, input, dictionary, numThreads, l);
    }

    @Override
    protected ArrayList<? extends LIWCAnalyser> initialiseAnalysers() {    	
    	ArrayList<LIWCExpression> expressions = dictionary.getExpressions();
    	ArrayList<LIWCCategory> categories = dictionary.getCategories();
    	
    	ArrayList<LIWCExpressionAnalyser> analysers = new ArrayList<LIWCExpressionAnalyser>();

        int increment = expressions.size() / numThreads;
        LIWCExpressionAnalyser analyser;
        ArrayList<LIWCExpression> e = null;

        for (int i = 0; i < numThreads - 1; i++) {
            e = new ArrayList<LIWCExpression>(expressions.subList((i * increment), ((i + 1) * increment)));
            analyser = new LIWCExpressionAnalyser(input, Integer.toString(i), e, categories, language);
            analyser.setLIWCListener(this);
            analysers.add(analyser);
        }

        e = new ArrayList<LIWCExpression>(expressions.subList((increment * (numThreads - 1)), expressions.size()));
        analyser = new LIWCExpressionAnalyser(input, Integer.toString(numThreads-1), e, categories, language);	//changed from numThreads to numThreads-1
        analyser.setLIWCListener(this);
        analysers.add(analyser);

        return analysers;
    }


}
