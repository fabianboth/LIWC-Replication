package socialobservatory.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * @author Simon Caton and Fabian Both
 */
public class FileIO {
	//parses text in file to string
    public static String readFile(File f) throws FileNotFoundException, IOException {
        String line = null;
    	InputStreamReader isr = new InputStreamReader(new FileInputStream(f),"UTF-8");
    	BufferedReader in = new BufferedReader(isr);
    	
        StringBuffer sb = new StringBuffer();
        while ((line = in.readLine()) != null) {
            sb.append(line + " ");
        }
        in.close();
        return sb.toString();
    }
    
    //parses text in file to string and retains newline characters
    public static String readFileAll(File f) throws FileNotFoundException, IOException {
        Path path = Paths.get(f.getAbsolutePath());
        byte[] data = Files.readAllBytes(path);
		
		String str = new String(data, "UTF-8");
        
        return str;
    }
    
    //parses text in file to string, encoding charset defined
    public static String readFile(File f, Charset charset) throws FileNotFoundException, IOException {
        String line = null;
    	InputStreamReader isr = new InputStreamReader(new FileInputStream(f), charset);
    	BufferedReader in = new BufferedReader(isr);
    	
        StringBuffer sb = new StringBuffer();
        while ((line = in.readLine()) != null) {
            sb.append(line + " ");
        }
        in.close();
        return sb.toString();
    }
    
    //parses text in file to string and retains newline characters, encoding charset defined
    public static String readFileAll(File f, Charset charset) throws FileNotFoundException, IOException {
        Path path = Paths.get(f.getAbsolutePath());
        byte[] data = Files.readAllBytes(path);
		
		String str = new String(data, charset);
        
        return str;
    }

    public static void writeFile(File dest, ArrayList<String> posts) throws IOException {
        PrintWriter out = new PrintWriter(new FileWriter(dest));
        
        for (String s : posts) {
            out.println(s.trim() + "\n\n");
        }
        out.close();
    }

    public static void writeFile(File dest, String content) throws IOException {
        PrintWriter out = new PrintWriter(new FileWriter(dest));
        out.println(content);
        out.close();
        
        System.out.println("Created: " + dest);
    }

}
