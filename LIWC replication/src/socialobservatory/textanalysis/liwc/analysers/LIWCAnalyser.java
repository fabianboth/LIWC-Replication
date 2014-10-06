package socialobservatory.textanalysis.liwc.analysers;

import java.util.TreeSet;
import socialobservatory.textanalysis.liwc.LIWCCategory;
import socialobservatory.textanalysis.liwc.LIWCListener;

/**
 * @author Simon Caton and Fabian Both
 */
public interface LIWCAnalyser extends Runnable {

    String getID();

    TreeSet<LIWCCategory> getResults();

    boolean hasResults();

    void setLIWCListener(LIWCListener l);
    
}
