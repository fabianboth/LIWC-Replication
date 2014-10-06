package socialobservatory.textanalysis.liwc;

import java.util.TreeSet;

/**
 * @author Simon Caton and Fabian Both
 */
public interface LIWCListener {
	
    void receiveAnalysisResults(String ID, TreeSet<LIWCCategory> results);
}