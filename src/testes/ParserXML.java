package testes;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.BreakIterator;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringEscapeUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import XMLObjects.PubmedDocument;


public class ParserXML {
	/*public ParserXML(String path, String gene) {
		this.gene = gene;
		this.path = path;
	}*/
	public ParserXML() {
		// TODO Auto-generated constructor stub
	}

	Tokenizer tokenizer = new Tokenizer();
	//OpenNLPTools tokenizer = new OpenNLPTools();
	TextPreprocessing textPre = new TextPreprocessing();
	//path to file containing selected stopwords
	ArrayList<String> stopWords = textPre.readFromFile("D:\\Summer Internship\\Preprocessing\\stopwords.txt");

	/**Gets the content of the "doc" tags
	 * @return: A nodeList 
	 * */
	public NodeList getDocumentsTagsPubmed(String path){
		File file = new File(path);
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = builderFactory.newDocumentBuilder();
			Document document = builder.parse(file);
			NodeList documentList = document.getElementsByTagName("doc"); //get all document tags
			return documentList;
		} catch (ParserConfigurationException e) {
			System.out.println("A problem occured while trying to parse the xml file:");
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (SAXException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * This method inputs a xml file and extracts Id, title and abstract info.
	 * This method is to be used when the highlight is set on.
	 * It also searches for matches between abbreviations and their meanings within the sentence. 
	 * */
	public void getContexts(String path, String targetword){
		AbbMatch abbMatch = new AbbMatch();
		abbMatch.setShortForm(targetword);

		try{
			File file = new File(path);
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			Document document = builder.parse(file);
			NodeList nodeList = document.getElementsByTagName("lst"); //get all document tags

			for (int s = 1; s < nodeList.getLength(); s++) { //If it starts from 0, it'll get the info twice. See the format of our XML for further reference.
				Node fstNode = nodeList.item(s);
				Element element = (Element) fstNode;
				if (element.hasAttribute("name")){
					String docID = element.getAttribute("name");//The ID

					element.getElementsByTagName("str");
					NodeList nodeList2 = element.getElementsByTagName("str"); 
					for (int j = 0; j < nodeList2.getLength(); j++) {
						Node text = nodeList2.item(j);
						Element textElem = (Element) text;

						String docContext = textElem.getTextContent();//The context

						String match = abbMatch.findBestLongForm(docContext);//Verifies if the short form has any long form match.
						if (match == null){
							SaveContent("ID: "+docID+"\n Match: "+match+"\n Context: "+match);
							System.out.println("Not a Match");
						}else{
							SaveContent("ID: "+docID+"\n Match: "+match+"\n Context: "+docContext+"\n");
							System.out.println("Match");
						}
					}       	
				}
			}
		}catch(Exception e){

		}
	}

	/**Saves the string on the path specified in <b>destination</b>*/
	public void saveContenttoFile(ArrayList<String> content,ArrayList<String> header, String pathResult){
		try{
			//File file = new File(path+"/"+gene+".csv");
			File file = new File(pathResult);
			if(!file.exists()){

			}
			FileWriter fw = new FileWriter(file,true);
			BufferedWriter bw = new BufferedWriter(fw, 1000000);
			StringBuilder sb = new StringBuilder();
			bw.write(content.get(0));
			content.remove(0); 
			for(String str: content) {
				if(str!=null ){
					bw.append("," +"\""+str+"\"");
				}
			}
			bw.newLine();
			bw.close();
			fw.close();
		}catch(IOException ioe){
			System.out.println("IOException occurred:");
			ioe.printStackTrace();
		}
		return;
	}

	/**Saves the string on the path specified in <b>destination</b>*/
	public void saveContenttoFile(ArrayList<String> content, String pathResult){
		try{
			//File file = new File(path+"/"+gene+".csv");
			File file = new File(pathResult);
			if(!file.exists()){
				file.createNewFile(); 
			}
			FileWriter fw = new FileWriter(file,true);
			BufferedWriter bw = new BufferedWriter(fw, 1000000);
			StringBuilder sb = new StringBuilder();
			bw.write(content.get(0));
			content.remove(0); 
			for(String str: content) {
				if(str!=null ){
					bw.append("," +"\""+str+"\"");
				}
			}
			bw.newLine();
			//	bw.write(bw.toString());
			bw.close();
			fw.close();
		}catch(IOException ioe){
			System.out.println("IOException occurred: not possible to find" + pathResult);
			ioe.printStackTrace();
		}
		return;
	}


	void writeHeader(ArrayList<String> header, String path){
		try{	
			File file = new File(path);
			if (!file.exists()){
				System.out.println(header);
				file.createNewFile(); 
				FileWriter fw = new FileWriter(file,true);
				fw.write(header.get(0));
				header.remove(0); 
				for(String str: header) {
					if(str!=null ){
						fw.append(","+str);
					}
				}
				fw.write("\n");
				fw.close();
			}
		}catch(IOException ioe){
			System.out.println("IOException occurred: not possible to find" + path);
			ioe.printStackTrace();
		}
	}

	/***/
	public void getField(NodeList nodeList){// To extract the info from the 'good' articles
		try{

			String info;// This variables are necessary due to the order in which the xml files are presented.
			String bufferAbs= "";
			String bufferTit= "";
			int flag = 0;

			for (int s = 0; s < nodeList.getLength(); s++) { 
				Node fstNode = nodeList.item(s);
				Element element = (Element) fstNode;

				NodeList nodeList2 = element.getElementsByTagName("str");
				for (int j = 0; j < nodeList2.getLength(); j++) {
					Node text = nodeList2.item(j);
					info = text.getTextContent();
					Element textElem = (Element) text;
					if(textElem.getAttribute("name").equals("id")){
						String id = info;
						flag = 1;//This variable is to sign that the ID has been acquainted.
					}else if(textElem.getAttribute("name").equals("medline_abstract_text")){ 
						bufferAbs = bufferAbs+info;
					}else{//In this: medline_article_title
						bufferTit = bufferTit+info;
					}
					if(flag==1){
						flag=0;
						//SaveContent(calcDist(tokenizer(bufferAbs), gene));
						calcDist(tokenizer(bufferAbs), gene);
						bufferAbs = "";
						//SaveContent(calcDist(tokenizer(bufferTit), gene));
						calcDist(tokenizer(bufferTit), gene);
						bufferTit = "";
					}
				}
			}

		}catch(Exception e){

		}
	}

	public List<String> gatherCommonWords(String path) throws IOException{
		List<String> commomWords = null;

		BufferedReader br = new BufferedReader(new FileReader(path));
		try {
			String line = br.readLine();

			while (line != null) {
				if(!line.contains("id: ") && !line.contains("id,")){
					System.out.println(line);

				}
				line = br.readLine();
			}
		} finally {
			br.close();
		}

		return commomWords ;
	}

	/**
	 * Receives a String with the path for the document to be parsed.
	 * @return 
	 * */
	public void getRawField(String path){
		String info; // content of each node
		ArrayList<String> fileContent = new ArrayList<>();
		NodeList documentList = getDocumentsTagsPubmed(path);
		for (int s = 0; s < documentList.getLength(); s++) {  //transform document nodes in elements
			Node fstNode = documentList.item(s);
			Element element = (Element) fstNode;
			NodeList nodeList2 = element.getElementsByTagName("str");
			for (int j = 0; j < nodeList2.getLength(); j++) {
				Node text = nodeList2.item(j);
				readPubmedElements(fileContent,text);
				if(fileContent.get(0)!=null){
					//	getWindowSentences(List<String> tokenizedText, T, int window, fileContent.get(0));
					saveContenttoFile(fileContent, "Files\\result.txt");
					fileContent.removeAll(fileContent);
				}
			}
		}
	}

	public ArrayList<String> readPubmedElements(ArrayList<String> fileContent,Node content){
		fileContent.add(null); //position 0 ID
		fileContent.add(null); //position 1 journal name
		String info = content.getTextContent();
		//info = StringEscapeUtils.unescapeXml(info);
		Element textElement = (Element) content;
		if(textElement.getAttribute("name").equals("id")){

			System.out.println("id:"+info);
			fileContent.remove(null);
		}else if(textElement.getAttribute("name").equals("medline_journal_title")){
			info = info.replaceAll("[\",]","");
			fileContent.set(1,info); //position 1 contains the journal name
		}else if(textElement.getAttribute("name").equals("medline_abstract_text")){ 
			info.replaceAll("[\"]","");
			ArrayList<String> temp = tokenizer.tokenize(info);
			temp = textPre.stopWordsRemover(temp, stopWords);
			temp = textPre.tagNumbers(temp);
			temp = textPre.tagIrrelevantWords(temp,"tag",true);
			fileContent.addAll(temp);
		}else if(textElement.getAttribute("name").equals("medline_article_title")){//In this: medline_article_title
			ArrayList<String> temp = tokenizer.tokenize(info);
			temp = textPre.stopWordsRemover(temp, stopWords);
			temp = textPre.tagNumbers(temp);

			fileContent.addAll(temp);
		}

		return fileContent;
	}



	public List<String> fileToVariable(String path) throws IOException{
		//Return all info in the file. (id and the content)
		BufferedReader br = new BufferedReader(new FileReader(path));
		List<String> sentences = null;
		try{
			sentences = Files.readAllLines(new File(path), Charset.forName("utf-8"));
		} finally {
			br.close();
		}
		return sentences ;
	}

	public ArrayList<String> fileToVariable(String path, int option) throws IOException{
		//This method returns only the text of all docs.
		BufferedReader br = new BufferedReader(new FileReader(path));
		String line = br.readLine();

		ArrayList<String> list = new ArrayList<String>();

		while ((line = br.readLine()) != null) {
			if(!line.contains("id: ") && !line.contains("id,")){
				list.add(line);
			}
		}
		br.close();
		return list;
	}

	public void preprocessDocumentAbstract(PubmedDocument document, int window, String targetWord) {
		String resume = document.getAbstract_text();
		String journal = document.getJournal();
		journal = journal.replaceAll("[\"]","");
		String title = document.getTitle();
		ArrayList<String> tuple = new ArrayList<>();
		HashMap<String, Boolean> map = new HashMap<>();
		ArrayList<String> hintWordsinText = new ArrayList<>();
		tuple.add(document.getId());
		tuple.add(journal);
		resume = resume.replaceAll("[\"]","");
		tuple = tokenizer.tokenize(resume);
		tuple = textPre.stopWordsRemover(tuple, stopWords);
		tuple = textPre.tagNumbers(tuple);
		map = textPre.hintWords(tuple);
		hintWordsinText = textPre.createHintWordsTuple(map);
		ArrayList<ArrayList<String>> listolists = textPre.getWindowSentences(tuple, targetWord, window, document.getId(),journal);
		for (ArrayList<String> arrayList : listolists) {
			arrayList = textPre.bigramCreator(arrayList);
			arrayList.addAll(hintWordsinText);
			saveContenttoFile(arrayList, "Files\\"+targetWord+"-CompleteAbstractpreprocessedV1.0.csv");
		}
	}
	public void preprocessDocumentTitle(PubmedDocument document, int window, String targetWord){	

		String journal = document.getJournal();
		journal = journal.replaceAll("[\"]","");
		String title = document.getTitle();
		ArrayList<String> tuple = new ArrayList<>();
		ArrayList<String> tuple2 = new ArrayList<>();
		HashMap<String, Boolean> map = new HashMap<>();
		ArrayList<String> hintWordsinText = new ArrayList<>();
		String resume = document.getAbstract_text();
		tuple.add(document.getId());
		tuple.add(journal);
		title = title.replaceAll("[\"]","");
		tuple = tokenizer.tokenize(title);
		tuple = textPre.stopWordsRemover(tuple, stopWords);
		tuple = textPre.tagNumbers(tuple);
		map = textPre.hintWords(tuple);
		hintWordsinText = textPre.createHintWordsTuple(map);

		ArrayList<ArrayList<String>> listolists = textPre.getWindowSentences(tuple, targetWord, window, document.getId(),journal);
		for (ArrayList<String> arrayList : listolists) {
			arrayList = textPre.bigramCreator(arrayList);
			arrayList.addAll(hintWordsinText);
			saveContenttoFile(arrayList, "Files\\"+targetWord+"-CompleteAbstractpreprocessedV1.0.csv");
		}
	}
}