package socialobservatory.textanalysis.liwc.constructor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;

import socialobservatory.textanalysis.liwc.LIWCOutputBuilder;
import socialobservatory.textanalysis.liwc.LIWCDictionary;
import socialobservatory.textanalysis.liwc.analysers.LIWCExpressionAnalyserManager;
import socialobservatory.util.CharsetDetector;
import socialobservatory.util.FileIO;
import socialobservatory.util.utilities;

/**
 * @author Simon Caton and Fabian Both
 */
public class LIWCConstructor {
	private LIWCDictionary liwcdictionary;
	private LIWCDictionary liwcdictionaryDe;
	private LIWCDictionary liwcdictionaryEn;
    private String root;
    private String resultPath;
    private LIWCOutputBuilder results;
    private ArrayList<String> pathNames;
    private String language = "en";
    private boolean umlautFlag = true;
    private boolean eachLineSeparate = false;
    private boolean aggregate = true;
    private boolean message = true;
    private boolean improvedMode = false;
    private int numOfThreads = 0;
    private final int coreMultiplier = 3;
    
    public LIWCConstructor() throws IOException, LangDetectException {
		Path currentRelativePath = Paths.get("");
		root = currentRelativePath.toAbsolutePath().toString();
		DetectorFactory.loadProfile(root + "/profiles");
		pathNames = new ArrayList<String>();
		results = new LIWCOutputBuilder();
  }
	
    //args[0] contains text path
    //args[1] contains result path
    //args[2] contains language mode (optional)
    //args[3] contains replication mode (optional)
    //args[4] contains line mode (optional)
    //args[5] contains umlaut mode (optional)
	public static void main(String[] args) {
		File log = new File("./logfile.txt");
		log.delete();
		long startTime = System.currentTimeMillis();
		try{
			LIWCConstructor lc = new LIWCConstructor();
			
			lc.configureLIWC(args);
			lc.analyseFiles();
		}catch(Exception e){
			e.printStackTrace();
            utilities.printLog(e.getMessage());
            utilities.printLog(e.toString());
		}
		
		long endTime = System.currentTimeMillis();
		System.out.println("runtime: " + (endTime-startTime) + "ms");
		utilities.printLog("runtime: " + (endTime-startTime) + "ms");
	}
	
	public void configureLIWC(String[] args) throws Exception{
		if(args.length < 2){
			throw(new Exception("no path specification for input and/or output files"));
		}
		
		//define inputFile
		if(args[0].endsWith(".txt") || args[0].endsWith(".TXT")){
			pathNames.add(args[0]);
		}else{	//analyse whole directory
			pathNames = listFiles(args[0],pathNames);
		}
		//define output file
		resultPath = args[1];
		results.setOutputFile(new File(resultPath, "results.txt"));

		//define language detection mode
		if(args.length > 2 && args[2].equalsIgnoreCase("auto")){
			language = "auto";
		}else if(args.length > 2 && args[2].equalsIgnoreCase("de")){
			language = "de";
		}
		loadDictionary();
		
		//define replication mode
		if(args.length > 3 && args[3].equalsIgnoreCase("on")){
			improvedMode = true;			
		}
		else{
			improvedMode = false;
		}
		utilities.setReplicationMode(improvedMode);		
		
		//define line mode
		if(args.length > 4 && args[4].equalsIgnoreCase("on")){
			eachLineSeparate = true;
		}
		
		//define umlaut mode
		if(args.length > 5 && args[5].equalsIgnoreCase("on")){
			umlautFlag = true;
		}else{
			umlautFlag = false;
		}
		utilities.setUmlautFlag(umlautFlag);
		
		//define if results in Linemode should be aggregated or not
		if(args.length > 6 && args[6].equalsIgnoreCase("off")){
			aggregate = false;
		}else{
			aggregate = true;
		}
		if(args.length > 7 && args[7].equalsIgnoreCase("off")){
			message = false;
		}else{
			message = true;
		}
		
		//determine number of threads
		int cores = Runtime.getRuntime().availableProcessors();
		numOfThreads = cores*coreMultiplier;
	}
	
	public void analyseFiles() throws FileNotFoundException, IOException{
		DataLoader dataLoader = new DataLoader();
		dataLoader.setPriority(Thread.MAX_PRIORITY);
		dataLoader.start();
		Thread manThread = null;
		LIWCExpressionAnalyserManager man = null;
		
		for(String path : pathNames){
			//file name
			String[] split = path.split("[\\\\/]");
			String ID = split[split.length-1];
			
	        ArrayList<String> list = dataLoader.loadManager(path);
	        boolean first = true;
	        for(String s : list){
	        	//get dictionary
				chooseDictionary(s);
				
				man = new LIWCExpressionAnalyserManager(ID, s, liwcdictionary, numOfThreads, results);
		        man.setupManager();
	        	
		        try {
					if(manThread != null){
						manThread.join();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
		            utilities.printLog(e.getMessage());
		            utilities.printLog(e.toString());
				}
		        manThread = new Thread(man);

				if(first){
					System.out.println("analysing File: " + path);	
					utilities.printLog("analysing File: " + path);	
				}
				manThread.start();
				first = false;
	        }
			
		}
		//wait for last thread
		try {
			if(manThread != null){
				manThread.join();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
            utilities.printLog(e.getMessage());
            utilities.printLog(e.toString());
		}
		
		dataLoader.terminate();
		if(eachLineSeparate && aggregate){
			System.out.println("aggregating results");
			utilities.printLog("aggregating results");
			aggregateResults(new File(resultPath, "results.txt"));
		}
		
		//generate Popup-Window
		if(message) {
			new PopupWindow();
		}
	}
	
	public ArrayList<String> listFiles(String path, ArrayList<String> fileList){
		String fileName;
		File file = new File(path);
		File[] listOfFiles = file.listFiles();
		 
		if(listOfFiles != null){
			for (int i = 0; i < listOfFiles.length; i++){
			  fileName = listOfFiles[i].getName();
			  if (listOfFiles[i].isFile()){
				  if (fileName.endsWith(".txt") || fileName.endsWith(".TXT")){
					  fileList.add(listOfFiles[i].getAbsolutePath());
			      }
			   }else if(listOfFiles[i].isDirectory()) {
				   listFiles(listOfFiles[i].getAbsolutePath(),fileList);
			   }
			}
		}
		return fileList;
	}
	
	public void loadDictionary() throws Exception{
		File dictionary;
		File file = new File(root + "/LIWCDictionaries");
		File[] listOfFiles = file.listFiles();
		String fileName;
		ArrayList<String> fileList = new ArrayList<String> ();
		
		//list all dictionaries found
		for (int i = 0; i < listOfFiles.length; i++){
		  fileName = listOfFiles[i].getName();
		  if (listOfFiles[i].isFile()){
			  if (fileName.endsWith(".dic") || fileName.endsWith(".DIC")){
				  fileList.add(listOfFiles[i].getName());
		      }
		   }
		}
		
		//choose english and german dictionary
		String dicDE = null;
		String dicEN = null;
		Pattern pDE = Pattern.compile("german",Pattern.CASE_INSENSITIVE);
		Pattern pEN = Pattern.compile("english",Pattern.CASE_INSENSITIVE);
		
		for(String s : fileList){
			Matcher mDE = pDE.matcher(s);
			Matcher mEN = pEN.matcher(s);
			if(mDE.find()){
				if(dicDE == null){
					dicDE = s;
				}else{
					throw (new Exception("multiple German dictionaries found!"));
				}
			}
			if(mEN.find()){
				if(dicEN == null){
					dicEN = s;
				}else{
					throw (new Exception("multiple English dictionaries found!"));
				}
			}
		}
		if(dicEN == null){
			throw(new Exception("no English dictionary found!"));
		}
		if(dicDE == null){
			throw(new Exception("no German dictionary found!"));
		}
		
		try {
			if(language.equalsIgnoreCase("de")){
				dictionary = new File(root, "LIWCDictionaries/" + dicDE);
				liwcdictionaryDe = LIWCDictionary.Factory.loadDictionary(dictionary,"de");
			}else if(language.equalsIgnoreCase("en")){
				dictionary = new File(root, "LIWCDictionaries/" + dicEN);
				liwcdictionaryEn = LIWCDictionary.Factory.loadDictionary(dictionary,"en");
			}else{
				dictionary = new File(root, "LIWCDictionaries/" + dicDE);
				liwcdictionaryDe = LIWCDictionary.Factory.loadDictionary(dictionary,"de");
				dictionary = new File(root, "LIWCDictionaries/" + dicEN);
				liwcdictionaryEn = LIWCDictionary.Factory.loadDictionary(dictionary,"en");
			}
		} catch (IOException e) {
			e.printStackTrace();
            utilities.printLog(e.getMessage());
            utilities.printLog(e.toString());
		}
	}
	
	public void chooseDictionary(String text){
		String lang = language;
		
		if(lang.equalsIgnoreCase("auto")){
			lang = detectLanguage(text);
		}
		
		if(lang.equalsIgnoreCase("de")){
			liwcdictionary = liwcdictionaryDe;
		}else{		//english as default case
			liwcdictionary = liwcdictionaryEn;
		}
	}
	
	public String detectLanguage(String text){
    	String lang = "";
    	try {
			Detector dec = DetectorFactory.create();
			dec.setMaxTextLength(100);
			dec.append(text);
			lang = dec.detect();
		} catch (LangDetectException e) {
			e.printStackTrace();
            utilities.printLog(e.getMessage());
            utilities.printLog(e.toString());
		}
    	return lang;
    }
	
	public void aggregateResults(File f) throws FileNotFoundException, IOException{
		String results = FileIO.readFileLines(f);
		String[] resultLines = results.split("\n");
		if(resultLines.length >= 2){
			
			String[] firstLine = resultLines[0].split("\t");
			ArrayList<String[]> allLines = new ArrayList<String[]>();
			ArrayList<String> IDs = new ArrayList<String>();
			
			//add lines
			for(int z = 1; z < resultLines.length; z++){
				allLines.add(resultLines[z].split("\t"));
			}
			
			//get unique IDs
			for(int j = 0; j < allLines.size(); j++){
				if(!IDs.contains(allLines.get(j)[0])){
					IDs.add(allLines.get(j)[0]);
				}
			}

			f.delete();
			PrintWriter out = new PrintWriter(new FileWriter(f,true));
			out.println(resultLines[0]);
			
			//aggregate for all IDs
			for(int s = 0; s < IDs.size(); s++){
				double[] cats = new double[firstLine.length-1];
				int count = 0;
				for(String[] sa : allLines){
					if(sa[0].equals(IDs.get(s))){
						count++;
						//first entry is entity identifier
						for(int k = 0; k < cats.length; k++){
							cats[k] = cats[k] + Double.parseDouble(sa[k+1]);
						}
					}
				}
				
				//average (not word count)
				for(int r = 1; r < cats.length; r++){
					cats[r] = Math.round(100*cats[r]/count)/100d;
				}

				//write to file
				out.print(IDs.get(s));
				for(int l=0; l < cats.length; l++){
					out.print("\t" + cats[l]);
				}
				if(s < IDs.size()-1){
					out.print("\n");
				}
			}
			
			out.close();
		}
	}
	
	//internal Data Load Thread to improve performance on small text files
	public class DataLoader extends Thread{
		boolean stop = false;
		
		public void run(){
			synchronized (this) {
				while(!stop){
					try {
						this.wait();
					} catch (InterruptedException e) {
					}
				}
			}
		}
		
		public ArrayList<String> loadManager(String path){
			synchronized(this){
				ArrayList<String> list = new ArrayList<String>();
				String source = null;
				try {
					File file = new File(path);
					Charset charset = CharsetDetector.detectCharset(file);
					
					if(eachLineSeparate){
						source = FileIO.readFileLines(file,charset);
						//replicate false behavior
						if(!improvedMode && charset.name().equals("UTF-8")){
							source = utilities.replicateFalseUmlauts(source);
						}
							
						String[] lines = source.split("\n");

						//check if string is empty
						Pattern pattern = Pattern.compile("[a-z0-9äöüß]+",Pattern.CASE_INSENSITIVE);
						for(String s : lines){
							Matcher m = pattern.matcher(s);
							if(m.find()){
								list.add(s);
							}
						}
					}else{
						source = FileIO.readFile(file,charset);
						//replicate false behavior
						if(!improvedMode && charset.name().equals("UTF-8")){
							source = utilities.replicateFalseUmlauts(source);
						}
						list.add(source);
					}

			        
				} catch (IOException e) {
					e.printStackTrace();
		            utilities.printLog(e.getMessage());
		            utilities.printLog(e.toString());
				}
				this.notifyAll();
				return list;
			}
		}
		
		public synchronized void terminate(){
			stop = true;
			this.notifyAll();
		}
	}
}
