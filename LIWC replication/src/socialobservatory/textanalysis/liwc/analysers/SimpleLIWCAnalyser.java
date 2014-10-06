package socialobservatory.textanalysis.liwc.analysers;

import java.util.TreeSet;

import socialobservatory.textanalysis.liwc.LIWCCategory;
import socialobservatory.textanalysis.liwc.LIWCListener;
import socialobservatory.textanalysis.liwc.LIWCWrapper;

/**
 * @author Simon Caton and Fabian Both
 */
public abstract class SimpleLIWCAnalyser implements LIWCAnalyser, Runnable {

    protected final String[] input;
    protected final String ID;
    protected final String language;
    protected TreeSet<LIWCCategory> liwcResults;
    protected boolean resultsAvailable = false;
    protected LIWCWrapper liwc;
    protected LIWCListener listener;

    public SimpleLIWCAnalyser(String[] input, String ID, String language) {
        this.input = input;
        this.ID = ID;
        this.language = language;
        liwcResults = new TreeSet<LIWCCategory>();
        liwc = new LIWCWrapper();
    }

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public TreeSet<LIWCCategory> getResults() {
        return liwcResults;
    }

    @Override
    public boolean hasResults() {
        return resultsAvailable;
    }

    protected abstract void setupLIWCWrapper();

    protected LIWCWrapper getLIWCWrapper() {
        return liwc;
    }

    @Override
    public void run() {

        setupLIWCWrapper();

        synchronized (this) {
            liwcResults = new TreeSet<LIWCCategory>(liwc.analyse(input));
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

    @Override
    public void setLIWCListener(LIWCListener l) {
        listener = l;
    }
}
