package socialobservatory.textanalysis.liwc.analysers;

import socialobservatory.textanalysis.liwc.LIWCDictionary;

/**
 * @author Simon Caton and Fabian Both
 */
public class LIWCDocumentAnalyser extends SimpleLIWCAnalyser {
    
    private final LIWCDictionary dictionary;

    public LIWCDocumentAnalyser(String[] input, String ID, LIWCDictionary dictionary, String language) {
        
        super(input, ID, language);
        this.dictionary = dictionary;
    }

    @Override
    protected void setupLIWCWrapper() {
        liwc.setDictionary(dictionary);
        liwc.setAutoNormalise(false);
    }
    
}
