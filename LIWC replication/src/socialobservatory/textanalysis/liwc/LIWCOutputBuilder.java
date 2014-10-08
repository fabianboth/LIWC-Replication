package socialobservatory.textanalysis.liwc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.TreeSet;

import socialobservatory.textanalysis.liwc.LIWCCategory;
import socialobservatory.util.utilities;

/**
 * @author Simon Caton and Fabian Both
 */
public class LIWCOutputBuilder implements LIWCListener {

    private ArrayList<TreeSet<LIWCCategory>> data;
    private ArrayList<String> IDs, lines;
    private File file;
    private boolean written = false;

    public LIWCOutputBuilder() {
        data = new ArrayList<TreeSet<LIWCCategory>>();
        IDs = new ArrayList<String>();
    }

    public void add(String ID, TreeSet<LIWCCategory> t) {
        data.add(t);
        IDs.add(ID);
    }

    public void writeAllToFile(File f) throws IOException {
        PrintWriter out = new PrintWriter(new FileWriter(f));
        StringBuffer title = new StringBuffer();
        title.append("Entity\t");
        StringBuffer sb;
        LIWCCategory c;

        int count = 0;

        boolean firstWrite = false;
        for (TreeSet<LIWCCategory> t : data) {

            sb = new StringBuffer();
            sb.append(IDs.get(count));
            count++;

            while (!t.isEmpty()) {
                c = t.pollFirst();
                if (!firstWrite) {
                    title.append(c.getName()).append("\t");
                }

                sb.append(c.getOccurences()).append("\t");
            }

            if (!firstWrite) {
                out.println(title.toString());
                firstWrite = true;
            }

            out.println(sb.toString());

        }

        out.close();

    }

    public void setOutputFile(File f) {
        file = f;
        file.delete();
    }

    @Override
    public synchronized void receiveAnalysisResults(String ID, TreeSet<LIWCCategory> results) {
    	lines = new ArrayList<String>();
        try {
            LIWCCategory c;
            StringBuffer sb = new StringBuffer();
            StringBuffer title = new StringBuffer();
            
            if (!written) {
                title.append("Entity");
            }
            
            sb.append(ID);

            while (!results.isEmpty()) {
                c = results.pollFirst();
                if (!written) {
                    title.append("\t").append(c.getName());
                }
                sb.append("\t").append(c.getOccurences());
            }
            
            PrintWriter out = new PrintWriter(new FileWriter(file,true));
            
            if (!written) {
                lines.add(title.toString());
                written = true;
            }
            
            lines.add(sb.toString());
            
            for(String line : lines) {
                out.println(line);
            }
            
            out.close();

        } catch (IOException ex) {
            ex.printStackTrace();
            utilities.printLog(ex.getMessage());
            utilities.printLog(ex.toString());
            
        }
        
        this.notifyAll();
    }
}
