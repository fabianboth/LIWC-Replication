package socialobservatory.textanalysis.liwc;

/**
 * @author Simon Caton and Fabian Both
 */
public class LIWCCategory implements Comparable<LIWCCategory> {

    private String name;
    private int ID;
    private double occurences = 0;
    private boolean normalised = false;

    public LIWCCategory(String name, int ID) {
        this.name = name;
        this.ID = ID;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void inc() {
    	synchronized(this){
	        if (!normalised) {
	            ++occurences;
	        } else {
	            throw new RuntimeException("LIWCCategory: " + name + " has already been normalised, it cannot be incremented anymore");
	        }
	        this.notifyAll();
    	}
    }
    
    public void dec() {
    	synchronized(this){
	        if (!normalised) {
	            --occurences;
	        } else {
	            throw new RuntimeException("LIWCCategory: " + name + " has already been normalised, it cannot be incremented anymore");
	        }
	        this.notifyAll();
    	}
    }

    public double getOccurences() {
        return occurences;
    }
    
    public void setOccurences(int occ) {
        occurences = occ;
    }

    public void normalise(int wordCount) {
        if (!normalised && occurences != 0) {
        	occurences = Math.round(100*occurences/wordCount*100d)/100.0;		//round two decimal digits
            normalised = true;
        }
    }

    public void clear() {
        occurences = 0;
        normalised = false;
    }

    @Override
    public String toString() {
        return "LIWCCategory{" + "name=" + name + ", ID=" + ID + ", occurences=" + occurences + '}';
    }

    @Override
    public int compareTo(LIWCCategory t) {
        if (t.ID < ID) {
            return 1;
        } else if (t.ID == ID) {
            return 0;
        } else {
            return -1;
        }
    }

    public void inc(double d) {
    	synchronized (this) {
	        if (!normalised) {
	            this.occurences += d;
	        } else {
	            throw new RuntimeException("LIWCCategory: " + name + " has already been normalised, it cannot be incremented anymore");
	        }
    	}
    }
}
