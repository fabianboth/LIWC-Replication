package socialobservatory.textanalysis.liwc.analysers;

import java.util.TreeSet;

import socialobservatory.textanalysis.liwc.LIWCCategory;
import socialobservatory.textanalysis.liwc.LIWCDictionary;

/**
 * @author Simon Caton and Fabian Both
 */
public class LIWCExtendedExpressionAnalyser extends SimpleLIWCAnalyser {
	private LIWCDictionary dictionary;

	public LIWCExtendedExpressionAnalyser(String[] input, String ID, LIWCDictionary dictionary) {
		super(input, ID, dictionary.getLanguage());
		this.dictionary = dictionary;
	}

	@Override
	protected void setupLIWCWrapper() {
		liwc.setDictionary(dictionary);
        liwc.setAutoNormalise(false);
	}
	
	@Override
    public void run() {

        setupLIWCWrapper();

        synchronized (this) {
            liwcResults = new TreeSet<LIWCCategory>(liwc.analyseExtended(input));
            resultsAvailable = true;
            this.notifyAll();
        }

        if (listener != null) {
            synchronized (listener) {
                listener.receiveAnalysisResults(ID, liwcResults);
                listener.notifyAll();
            }
        }
    }

}
