package socialobservatory.textanalysis.liwc.analysers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import socialobservatory.textanalysis.liwc.LIWCCategory;
import socialobservatory.textanalysis.liwc.LIWCDictionary;
import socialobservatory.textanalysis.liwc.LIWCListener;
import socialobservatory.util.utilities;

/**
 * @author Simon Caton and Fabian Both
 */
public abstract class LIWCAnalyserManager implements LIWCListener, Runnable {

    private static final Object lock = new Object();
    protected final LIWCListener listener;
    protected final String[] input;
    protected final int numThreads;
    protected final LIWCDictionary dictionary;
    protected final String language;
    private int received = 0;
    protected final String ID;
    protected ArrayList<LIWCAnalyser> analysers = null;

    public LIWCAnalyserManager(String ID, String input, LIWCDictionary dictionary, int numThreads, LIWCListener l) {
        listener = l;
        this.input = utilities.split(utilities.prepareString(input, dictionary.getLanguage()));
        this.dictionary = dictionary;
        this.numThreads = numThreads;
        this.ID = ID;
        this.language = dictionary.getLanguage();
        
    }
    
    @SuppressWarnings("unchecked")
	public void setupManager(){
    	analysers = (ArrayList<LIWCAnalyser>) initialiseAnalysers();
        LIWCExtendedExpressionAnalyser eea = new LIWCExtendedExpressionAnalyser(input,new Integer(numThreads+1).toString(), dictionary);
        eea.setLIWCListener(this);
        analysers.add(eea);
    }
    
    @Override
    public void run() {
    	if(analysers == null){
    		setupManager();
    	}
		synchronized(lock){
	        dictionary.clear();
	
	        for (LIWCAnalyser liwc : analysers) {
	            new Thread(liwc).start();
	        }
	
	        synchronized (this) {
		        while (received != numThreads+1) {
		            try {
		                this.wait();
		            } catch (InterruptedException ex) {
		            }
		        }

	        }
	        lock.notifyAll();
    	}
    }

    @Override
    public void receiveAnalysisResults(String ID, TreeSet<LIWCCategory> results) {
        synchronized (this) {
            ++received;
//            System.out.println("Received results from: " + ID + " (" + received + "/" + (numThreads+1) + ")");
            if (received == numThreads + 1) {
                int wordCount = input.length;
                System.out.println("word count: " + wordCount);
                utilities.printLog("word count: " + wordCount);
                Iterator<LIWCCategory> iterator = results.iterator();
                while (iterator.hasNext()) {
                    iterator.next().normalise(wordCount);
                }
                synchronized (listener) {
                    listener.receiveAnalysisResults(this.ID, results);
                    listener.notifyAll();
                }
            }
            this.notifyAll();
        }
    }

    protected abstract ArrayList<? extends LIWCAnalyser> initialiseAnalysers();
}
