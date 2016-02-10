package testes;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import SAXParser.CustomHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;

public class Main {
	/*

	static TextPreprocessing textPre = new TextPreprocessing();
	static ParserXML parserxml = new ParserXML();
	//OpenNLPTools tokenizer = new OpenNLPTools();
	Tokenizer tokenizer = new Tokenizer();

	public static void getRawField(String path, String targetword){
		String info; // content of each node
		ArrayList<String> fileContent = new ArrayList<>();
		NodeList documentList = parserxml.getDocumentsTagsPubmed(path);
		for (int s = 0; s < documentList.getLength(); s++) {  //transform document nodes in elements
			Node fstNode = documentList.item(s);
			Element element = (Element) fstNode;
			NodeList nodeList2 = element.getElementsByTagName("str");
			for (int j = 0; j < nodeList2.getLength(); j++) {
				Node text = nodeList2.item(j);
				parserxml.readPubmedElements(fileContent,text);

				if(fileContent.get(0)!=null){
					System.out.println("index0:"+ fileContent.get(0));
					String id = fileContent.get(0);
					String journal = fileContent.get(1);
					fileContent.remove(0);
					fileContent.remove(1);
					fileContent.remove(null);
					ArrayList<ArrayList<String>> listolists = textPre.getWindowSentences(fileContent, targetword, 5, id,journal);
					for (ArrayList<String> arrayList : listolists) {
						arrayList = textPre.bigramCreator(arrayList);
						parserxml.saveContenttoFile(arrayList, "Files\\"+targetword+"-preprocessed.csv");
					}

					fileContent.removeAll(fileContent);
				}
			}
		}
		documentList=null;
	}

	public static void main(String[] args) throws IOException {

		TextPreprocessing textPre = new TextPreprocessing();
		ParserXML parserxml = new ParserXML();
		//OpenNLPTools tokenizer = new OpenNLPTools();
		ArrayList<String> stopWords = textPre.readFromFile("D:\\Summer Internship\\Preprocessing\\stopwords.txt");
		ArrayList<String> listagenes = new ArrayList<String>();
		//TODO make it run through all the gene names and gene files

		File folder = new File("D:\\Summer Internship\\Preprocessing\\Files\\HighFrequencyContext");
		File[] listOfFiles = folder.listFiles();
		String geneName = null;
		String gene = null;
	    for (int i = 0; i < listOfFiles.length; i++) {
	    	     gene = listOfFiles[i].getName();
	    	     gene = gene.replace(".xml","");
	    		 listagenes.add(gene);}

		System.out.println(listagenes);
		for (String geneItem : listagenes) {
			 geneName = geneItem.replace("%22","");
			getRawField("D:\\Summer Internship\\Preprocessing\\Files\\HighFrequencyContext\\"+ geneItem +".xml", geneName);
		}

	}*/


	/**This snipet is hard coded because we were trying to proove the concept, 
	 * this section should be automated in the future.
	 * */
	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
		TextPreprocessing textPre = new TextPreprocessing();
		ParserXML parserxml = new ParserXML();
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();
		DefaultHandler handler = new CustomHandler("A1");  //creating handler for our xml files
		
		ArrayList<String> header = textPre.createHeader(5, textPre.getHintWords(), 3);
		parserxml.writeHeader(header, "Files\\A1-CompleteAbstractpreprocessedV1.0.csv");
		saxParser.parse("D:\\Summer Internship\\Preprocessing\\Files\\HighFrequencyContext\\A1.xml", handler);
	}
}
