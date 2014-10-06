package socialobservatory.textanalysis.liwc.analysers;

import java.util.ArrayList;

import socialobservatory.textanalysis.liwc.LIWCDictionary;
import socialobservatory.textanalysis.liwc.LIWCListener;

/**
 * @author Simon Caton and Fabian Both
 */
public class LIWCDocumentSplittingManager extends LIWCAnalyserManager {

    public LIWCDocumentSplittingManager(String ID, String input, LIWCDictionary dictionary, int numThreads, LIWCListener l) {
        super(ID, input, dictionary, numThreads, l);
    }

    @Override
    protected ArrayList<? extends LIWCAnalyser> initialiseAnalysers() {
        ArrayList<LIWCDocumentAnalyser> analysers = new ArrayList<LIWCDocumentAnalyser>();

        int increment = input.length / numThreads;
        LIWCDocumentAnalyser analyser;
        
        String [] docPart;

        for (int i = 0; i < numThreads - 1; i++) {
            
            docPart = new String[increment];
            System.arraycopy(input, (i * increment), docPart, 0, increment);
            analyser = new LIWCDocumentAnalyser(docPart, new Integer(i).toString(), dictionary, language);
            analyser.setLIWCListener(this);
            analysers.add(analyser);
        }

        docPart = new String[input.length - (increment * (numThreads - 1))];
        System.arraycopy(input, (increment * (numThreads - 1)), docPart, 0, input.length - (increment * (numThreads - 1)));
        analyser = new LIWCDocumentAnalyser(docPart, Integer.toString(numThreads), dictionary, language);
        analyser.setLIWCListener(this);
        analysers.add(analyser);

        return analysers;
    }
}
