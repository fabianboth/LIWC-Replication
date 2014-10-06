package com.cybozu.labs.langdetect;

public class TestClass {

	public static void main(String[] args) throws LangDetectException {
		DetectorFactory.loadProfile("D:/Eigene Dateien/Programmieren/workspace/Language detect/profiles");
		Detector dec = DetectorFactory.create();
		dec.append("This is bullshit!");
		
		String language = dec.detect();
		System.out.println("Detected Language: " + language);
	}

}
