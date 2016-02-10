package testes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.util.WordlistLoader;
import org.apache.lucene.util.Version;
import org.apache.commons.lang3.StringEscapeUtils;


public class TextPreprocessing {

	ArrayList<String> hintWords = readFromFile("D:\\Summer Internship\\Preprocessing\\indicativewordsV1.0.txt");
	/**
	 * Receives a list of strings tokenized. 
	 * @param window is the number of words to get after and before <i>targetWord</i>
	 * @return A group of lists with the sentences containing <i>targetWord</i> for the given abstract
	 * */
	public ArrayList<ArrayList<String>> getWindowSentences(ArrayList<String> tokenizedText, String targetWord, int window, String pubMedId, String journal){
		ArrayList<ArrayList<String>> listOfLists = new ArrayList<ArrayList<String>>();
		ArrayList<String> tuple = new ArrayList<String>();
		//removing journal name from abstract 
		//tokenizedText.remove(0);
		for (int i = 0; i < tokenizedText.size(); i++) {
			try{
				if(tokenizedText.get(i).equals(targetWord)){
					tuple = new ArrayList<String>( Arrays.asList("","","","","","","","","","","","",""));
					tuple.set(0,pubMedId);
					tuple.set(1,journal);
					for (int j = -window; j <= window; j++){	
						if(i+j < tokenizedText.size() && i+j >= 0){ 
							if(tokenizedText.get(i+j) != null){
								tuple.set(j+window+2,tokenizedText.get(i+j));
							}	
						}else if (i+j < 0){ //when the targetWord is in the beginning of the text

						}
					}
					tuple.remove(7);
					listOfLists.add(tuple);
					//System.out.println(listOfLists);
				}
			}catch(Exception e){
				System.out.println("id:" + pubMedId);
				System.out.println("indice" + i);
				System.out.println("content of indice:" + tokenizedText.get(i));
				System.out.println(e.getMessage());}
		}
		return listOfLists; 
	}

	/** @param sentence Sentence to tokenize.
	 * @return words: A list of strings in the same order as they are in the text.
	 * */
	public ArrayList<String> tokenizer(String sentence){
		//Source: http://stackoverflow.com/questions/4674850/converting-a-sentence-string-to-a-string-array-of-words-in-java
		ArrayList<String> words = new ArrayList<String>();
		BreakIterator breakIterator = BreakIterator.getWordInstance();
		breakIterator.setText(sentence);
		int lastIndex = breakIterator.first();
		while (BreakIterator.DONE != lastIndex) {
			int firstIndex = lastIndex;
			lastIndex = breakIterator.next();
			if (lastIndex != BreakIterator.DONE && Character.isLetterOrDigit(sentence.charAt(firstIndex))) {
				words.add(sentence.substring(firstIndex, lastIndex));
			}
		}
		return words;
	}

	public ArrayList<String> bigramCreator(ArrayList<String> tuple){
		String bigram;
		for(int i=5; i<8; i++){
			bigram = "";
			if (tuple.get(i) != null){
				bigram = tuple.get(i);
				if(tuple.get(i+1) != null){
					bigram = bigram +" "+ tuple.get(i+1);
				}
			}
			tuple.add(bigram);
		}
		return tuple;
	}

	/**@param: stopWords list with words to be removed from the text
	 * */
	public ArrayList<String> stopWordsRemover(ArrayList<String> input, ArrayList<String> stopWords) {
		for (int j = 0; j < stopWords.size(); j++) {
			for (int i = 0; i < input.size(); i++) {
				if (stopWords.contains(input.get(i)) || input.get(i).equals(",") || input.get(i).equals(".")) {
					input.remove(i);
				}
			}
		}return input;
	}

	public ArrayList<String> readFromFile(String pathtoFile){
		ArrayList<String> result = new ArrayList<>();
		File file = new File(pathtoFile);
		FileReader fileReader;
		try {
			fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				result.add(line);
			}
			fileReader.close();
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	
	/**Searches in the tuple for numbers using the specified 
	 * regular expression and tag them as #num#
	 * @param text A tokenized sentence, organized in a ArrayList
	 * */
	public ArrayList<String> tagNumbers(ArrayList<String> text){
		// Pattern p = Pattern.compile("[^a-zA-Z](\p{Punct}*\d+(\\p{Punct}*|-+))+[^a-zA-Z]");
		for (int i = 0; i < text.size(); i++) {
			//	if(text[i].matches("^-?\\d+$")){
			if(text.get(i).matches("[^a-zA-Z](\\p{Punct}*\\d+(\\p{Punct}*|-+))+[^a-zA-Z]")){
				text.set(i, "#num#");
			}
		}
		return text;
	}


	public void removeSinglePontuaction(ArrayList<String> tokens){
		for(int i=0;i<tokens.size();i++)
		{
			if(tokens.get(i).equals(".") || tokens.get(i).equals(",")){}
		}
	}

	/** 
	 * */
	public ArrayList<String> tagIrrelevantWords(ArrayList<String> tokenizedText, String optionTag, Boolean optionRemove){
		for (int i = 0; i < tokenizedText.size()-1; i++) {
			if(tokenizedText.get(i).contains("cell") || tokenizedText.get(i).contains("Cell")){
				/*if(tokenizedText.get(i).contains("T-cell") || tokenizedText.get(i).contains("T-Cell") ||
						tokenizedText.get(i).contains("cell-T") || tokenizedText.get(i).contains("Cell-T")){
					if (optionTag.equals("tag")){
						tokenizedText = tagTCell(tokenizedText, i);//Tag T-cell and its variations
					}else if(optionTag.equals("tag_boolean")){
						tokenizedText = tagBoolean(tokenizedText, i);//Tag and add "1" to the end of the ArrayList
					}
				}*/
				/*	if(i != 0 && tokenizedText.get(i-1).contentEquals("T")){// || //To check before the position 
						//i<text.size() && text.get(i+1).contentEquals("T")){ //To check after the position.
					if (optionTag.equals("tag")){
						tokenizedText = tagTCell(tokenizedText, i);//Tag T-cell and its variations
						tokenizedText = remove(tokenizedText, i-1);
					}else if(optionTag.equals("tag_boolean")){
						tokenizedText = tagBoolean(tokenizedText, i);//Tag and add "1" to the end of the ArrayList
						tokenizedText = remove(tokenizedText, i-1);
					}
				}	*/

				if(optionRemove == true){
					if(tokenizedText.get(i).matches("cell") ||
							tokenizedText.get(i).matches("cells") ||
							tokenizedText.get(i).matches("Cell") ||
							tokenizedText.get(i).matches("Cells")){
						tokenizedText = remove(tokenizedText, i);
					}
				}
			}
		}
		return tokenizedText;
	}

	private ArrayList<String> tagBoolean(ArrayList<String> text, int index) {
		text.set(index,"#tagged#");
		text.add("1");
		return text;
	}

	private ArrayList<String> tagTCell(ArrayList<String> text, int index){
		text.set(index,"#tagged#");
		return text;
	}

	public HashMap<String,Boolean> hintWords(ArrayList<String> tokenizedText) {
		HashMap<String, Boolean> map = new HashMap<>();
		for (String word : hintWords) {
			if(tokenizedText.contains(word)){
					map.put(word, true);}
				else{map.put(word, false);}
			}
		
		return map;
	}
	
	public HashMap<String,Boolean>hintGroups(ArrayList<String> tokenizedText){
		
	}

	/**Creates the header of the Weka input file.
	 *  
	 * @param: window the number of words in each side of the gene.
	 * hintWords: 
	 * bigramNumber:
	 * @return:header a list of words*/
	public ArrayList<String> createHeader(int window, ArrayList<String> hintWords, int bigramNumber){
		ArrayList<String> header = new ArrayList<>();
		header.add("id");	
		header.add("journal");
		for(int i = 1; i <= window*2; i++){
			header.add("word"+ Integer.toString(i));
		}
		for(int i = 1; i <= bigramNumber; i++){
			header.add("bigram"+ Integer.toString(i));
		}
		header.addAll(hintWords);
		return header;
	}
	
	/** Verifies the existence of a specific word in the given window and builds a new row for the table. 
	 * If the word is present, a "1" is added, if it is not present, a "0" is added to the row. 
	 * @return: tuple with zeros and ones
	 * */
	public ArrayList<String> createHintWordsTuple(HashMap<String,Boolean> map){
		ArrayList<String> tuple = new ArrayList<>();
		for (String word : hintWords) {
			if (map.get(word) == true){
			tuple.add("1");}
			else{tuple.add("0");}
		}
		return tuple;
	}
	
	public ArrayList<String> getHintWords() {
		return hintWords;
	}

	public void setHintWords(ArrayList<String> hintWords) {
		this.hintWords = hintWords;
	}
	
	public ArrayList<String> hintgroups(){
		
	}
}