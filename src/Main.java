import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import dom.Text;
import dom.Word;
import dom.WordPart;
import fst.Configuration;
import fst.FstPrinter;
import fst.ResultCollector;
import fst.State;
import fst.StringLink;
import fst.Tape;

public class Main {

	public static void main(String[] args) {
		Text trainingText = new Text("Data/Train.txt");
		Text testText = new Text("Data/Test.txt");

		try {
			trainingText.readText();
			testText.readText();
		} catch (FileNotFoundException e) {
			System.err.println("File not found!");
			System.exit(1);
		} catch (IOException e) {
			System.err.println("IO Exception!");
			System.exit(1);
		}

		trainingText.generateWords();
		trainingText.searchForPrefixes();
		trainingText.searchForSuffixes();
		trainingText.generateStatistics();

		// create the states
		State<ResultCollector> 
			startState = new State<ResultCollector>(),
			preStemState=new State<ResultCollector>(),
			postStemState=new State<ResultCollector>(),
			finalState=new State<ResultCollector>(),
			pastWordEndState=new State<ResultCollector>();

		// for testing the fst, we'll add # to the end of every word.
		// this makes sure the whole word is parsed before reaching the
		// accepting state
		pastWordEndState.setAccepting(true);

		// add empty prefixes and suffixes
		startState.addLink(new StringLink("", "", preStemState));
		postStemState.addLink(new StringLink("", "", finalState));
		
		// add word end link
		finalState.addLink(new StringLink("#", "", pastWordEndState));
		
		// add links for the prefixes
		for (WordPart part:trainingText.prefixes.values()){
			startState.addLink(new StringLink(part.name, part.name+"^", preStemState));
		}
		
		// add links for the stems
		for (WordPart part:trainingText.stems.values()){
			preStemState.addLink(new StringLink(part.name, part.name, postStemState));
		}
		
		// add links for the postfixes
		for (WordPart part:trainingText.stems.values()){
			postStemState.addLink(new StringLink(part.name, "^"+part.name, finalState));
		}

		
		try {
			FstPrinter.print(startState, new PrintStream("graph.dot"));
			//Runtime.getRuntime().exec(new String[]{"dot","-o","graph.gif","-T","gif","graph.dot"});
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		testText.generateWords();
		for (Word word:testText.words){
			// create and fill the input tape
			Tape inputTape = new Tape();
			for (char ch : word.getName().toCharArray()) {
				inputTape.write(ch);
			}
			// add the final # to mark the end of the word
			inputTape.write('#');
			inputTape.setPosition(0);
	
			// create the output tape
			Tape outputTape = new Tape();
			
			// create the result collector
			ResultCollector collector = new ResultCollector();
	
			// run the configuration
			Configuration<ResultCollector> config = new Configuration<ResultCollector>(
					startState, inputTape, outputTape, collector);
			config.run();
	
			collector.sortAcceptionConfigurations();
			System.out.printf("%s\n", word.getName());
			for (Configuration<ResultCollector> conf : collector
					.getAcceptingConfigurations()) {
				System.out.printf("  %s (%.1f)\n",conf.getOutputTape(),conf.getProbability());
			}
		}
	}
}
