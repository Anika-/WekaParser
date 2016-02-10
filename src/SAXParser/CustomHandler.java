package SAXParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import testes.ParserXML;
import XMLObjects.PubmedDocument;

public class CustomHandler extends DefaultHandler{

	String TAG_RESULT = "result";
	String TAG_DOCUMENT = "doc";
	String TAG_ARRAY = "arr";
	String TAG_PUBMED_CONTENT = "str";	
	String ATRIBUTE_ABSTRACT = "medline_abstract_text";
	String ATRIBUTE_JOURNAL = "medline_journal_title";
	String ATRIBUTE_TITLE = "medline_article_title";
	String ATRIBUTE_ID = "id";

	boolean flag_abstract_txt;
	boolean flag_abstract_part;
	boolean flag_journal;
	boolean flag_title;
	boolean flag_id;

	private List<PubmedDocument> results = new ArrayList<PubmedDocument>();
	private Stack<PubmedDocument> documentStack = new Stack<PubmedDocument>();;
	public StringBuilder content = new StringBuilder();

	String gene;

	public CustomHandler(String targetWord) {
		flag_abstract_txt = false;
		flag_journal = false;
		flag_title = false;
		flag_id = false;
		gene = targetWord;
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		content.append(new String(ch, start, length)); //holds node value
	}

	@Override
	public void startElement(String uri, String localName,String qName, Attributes attributes)
			throws SAXException {

		if (qName.equalsIgnoreCase(TAG_RESULT)) {
			//do nothing
		}
		if (qName.equals(TAG_DOCUMENT)) {
			PubmedDocument currentDocument = new PubmedDocument();
			documentStack.push(currentDocument);
		}

		//=========== IN PROGRESS ============

		if(qName.equals(TAG_ARRAY)){
			if(attributes.getValue("name").equals(ATRIBUTE_ABSTRACT)){
				flag_abstract_txt = true;	
			}
		}

		//=========== IN PROGRESS ============

		if (qName.equals(TAG_PUBMED_CONTENT)) {
	
			if(attributes.getLength()!=0){
				if(attributes.getValue("name").equals(ATRIBUTE_JOURNAL)){
					flag_journal = true;
				}
				if (attributes.getValue("name").equals(ATRIBUTE_TITLE)){
					flag_title = true;
				}
				if (attributes.getValue("name").equals(ATRIBUTE_ID)){
					flag_id = true;
				}
			}else {	if(flag_abstract_txt){ // if we are inside arr
				flag_abstract_part = true;
			}
			}
		}

	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {

		if (qName.equals(TAG_DOCUMENT)) {
			PubmedDocument currentDocument =  documentStack.pop();
			if (documentStack.isEmpty()) { //not necessary because we don't have nested documents

				/*===== PRE PROCESSING ====*/	

				//TODO do the preprocessing HERE
				ParserXML parserxml = new ParserXML();
				if (currentDocument.getAbstract_text()!=null){
					parserxml.preprocessDocumentAbstract(currentDocument, 5, gene);}
				//parserxml.preprocessDocumentTitle(currentDocument, 5, gene);
				/*===== PRE PROCESSING ====*/

			}
		}else if (qName.equals(TAG_PUBMED_CONTENT)){
			if(documentStack!=null && !documentStack.isEmpty()){ //we are inside a document element
				PubmedDocument currentDocument = documentStack.peek();
				//	if(flag_abstract_txt){
				//		flag_abstract_txt = false;
				//	}
				if(flag_abstract_part){
					currentDocument.setAbstract_text(currentDocument.getAbstract_text() + content.toString());
					flag_abstract_part = false;	}
				else if(flag_journal){
					String temp = content.toString().replace("\n", "");
					temp = temp.substring(8); //removing space and line breaks
					currentDocument.setJournal(temp);
					flag_journal = false;
				}else if(flag_title){
					currentDocument.setTitle(content.toString());
					flag_title = false;
				}else if(flag_id){
					String temp = content.toString().replace("\n", "");
					temp = temp.substring(4);
					currentDocument.setId(temp);
					flag_id = false;
				}
				content = new StringBuilder();
			}
		}else if(qName.equals(TAG_ARRAY)){
			flag_abstract_txt = false;
		}
	}

	public List<PubmedDocument> getResults(){
		return  results;
	}
}


