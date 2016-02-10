package testes;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.util.InvalidFormatException;

public class OpenNLPTools {

	public String[] TokenizerOpenNLP(String text) {

		//The file "en-token.bin", is in the root folder of this project.

		try {
			InputStream modelIn = new FileInputStream("en-token.bin");
			TokenizerModel model = new TokenizerModel(modelIn);
			Tokenizer tokenizer = new TokenizerME(model);
			String[] result = tokenizer.tokenize(text);
			if (modelIn != null) {
				modelIn.close();
			}
			return result;


		}catch(FileNotFoundException e){

		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}