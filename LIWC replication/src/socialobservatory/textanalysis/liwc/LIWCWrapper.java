package socialobservatory.textanalysis.liwc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import socialobservatory.util.utilities;

/**
 * @author Simon Caton and Fabian Both
 */
public class LIWCWrapper {

    private LIWCDictionary dictionary;
    private boolean autoNormalise = true;

    public LIWCDictionary getDictionary() {
        return dictionary;
    }

    public void setDictionary(LIWCDictionary dictionary) {
        this.dictionary = dictionary;
    }

    public ArrayList<LIWCCategory> analyse(String[] s) {
        int wordCount = s.length;
        dictionary.categorise(s);
        
        ArrayList<LIWCCategory> cats = dictionary.getCategories();

        if (autoNormalise) {
            for (LIWCCategory cat : cats) {
                cat.normalise(wordCount);
            }
        }

        return cats;
    }
    
    public ArrayList<LIWCCategory> analyseExtended(String[] s) {
        int wordCount = s.length;
        dictionary.categoriseExtended(s);
        
        ArrayList<LIWCCategory> cats = dictionary.getCategories();

        if (autoNormalise) {
            for (LIWCCategory cat : cats) {
                cat.normalise(wordCount);
            }
        }

        return cats;
    }

    public ArrayList<LIWCCategory> analyse(File f) throws IOException {
        StringBuilder sb = new StringBuilder();

        BufferedReader in = new BufferedReader(new FileReader(f));
        String line;

        while ((line = in.readLine()) != null) {
            sb.append(line).append("\n");
        }

        in.close();

        return analyse(utilities.split(utilities.prepareString(sb.toString(), dictionary.getLanguage())));

    }

    public boolean isAutoNormalise() {
        return autoNormalise;
    }

    public void setAutoNormalise(boolean autoNormalise) {
        this.autoNormalise = autoNormalise;
    }
}
