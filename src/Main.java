import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import dom.*;
import fst.*;

public class Main {

	public static void main(String[] args) {
		BufferedReader br;
		StringBuffer contentOfFile = new StringBuffer();

		try {
			br = new BufferedReader(new InputStreamReader(
					new FileInputStream("Data/Train.txt")));
		
			String line;
			
			while ((line = br.readLine()) != null) {
			    contentOfFile.append(" " + line);
			}
		} catch (FileNotFoundException e) {
			System.err.println("File not found!");
			System.exit(1);
		} catch (IOException e) {
			System.err.println("IO Exception!");
			System.exit(1);
		}
		
		String text = contentOfFile.toString();
		
		// Normalize the text
		text = text.toLowerCase();
		text = text.replaceAll("[,.;!?*():\"'-]+", " ");

		HashSet<String> finalstems = new HashSet<String>();
		HashSet<String> finalprefixes = new HashSet<String>();

		List<String> wordlist = Arrays.asList(text.trim().split("\\s+"));

		// Find all prefixes
		for (int i = 4; i >= 1; i--) {
			// i is the length of the prefix to be tried
			List<String> newlist = new ArrayList<String>();

			// maps prefixes to the stems that follow
			HashMap<String, HashSet<String>> prefixes = new HashMap<String, HashSet<String>>();

			// Iterate over all words
			for (String word : wordlist) {
				if (word.length() <= i) {
					newlist.add(word);
					continue;
				}

				String prefix = word.substring(0, i);
				String stem = word.substring(i, word.length());

				HashSet<String> set = prefixes.get(prefix);

				if (set == null) {
					set = new HashSet<String>();
					prefixes.put(prefix, set);
				}

				set.add(stem);
			}

			// Get all real prefixes
			for (Map.Entry<String, HashSet<String>> entry : prefixes.entrySet()) {
				String prefix = entry.getKey();
				HashSet<String> set = entry.getValue();

				if (set.size() > 1) {
					finalprefixes.add(prefix);
					// Only take prefixes that occur in multiple words
					for (String stem : set) {
						finalstems.add(stem);
						System.out.println(prefix + "/" + stem);
					}
				} else {
					// We didn't have a prefix... put the word together again
					newlist.add(prefix + set.iterator().next());
				}
			}
			
			// replace the word list
			// the newlist only contains words which were not yet used to
			// build a prefix
			wordlist = newlist;
		}

		System.out.println("Prefixes:");
		for (String prefix : finalprefixes) {
			System.out.print(prefix + ", ");
		}
		System.out.println();

		System.out.println("Stems:");
		for (String stem : finalstems) {
			System.out.print(stem + ", ");
		}
		System.out.println();

		System.out.println("Others:");
		for (String word : new HashSet<String>(wordlist)) {
			System.out.print(word + ", ");
		}
		System.out.println();

		// create alphabets
		Alphabet lexicalAlphabet = new Alphabet();
		Alphabet lowerAlphabet = new Alphabet();

		for (char ch = 'a'; ch <= 'z'; ch++) {
			lexicalAlphabet.addSymbol(ch);
			lowerAlphabet.addSymbol(ch);
		}

		for (char ch = 'A'; ch <= 'Z'; ch++) {
			lexicalAlphabet.addSymbol(ch);
			lowerAlphabet.addSymbol(ch);
		}

		// create and fill the lower tape
		Tape lowerTape=new Tape(lowerAlphabet);
		for (char ch: "HelloWorld".toCharArray()){
			lowerTape.write(ch);
		}
		lowerTape.setPosition(0);
		
		// create the lexical tape
		Tape lexicalTape=new Tape(lexicalAlphabet);
		
		// create the start and end state
		State<ResultCollector> startState=new State<ResultCollector>();
		
		State<ResultCollector> endState=new State<ResultCollector>();
		endState.setAccepting(true);
		
		// add first link
		startState.addLink(new StringLink("HelloWorld", "Yeah", endState));
		
		// add second link
		startState.addLink(new StringLink("HelloWorld", "YeahBoah", endState));
		
		// create the result collector
		ResultCollector collector=new ResultCollector();
		
		// run the configuration
		Configuration<ResultCollector> config=new Configuration<ResultCollector>(startState,lowerTape,lexicalTape,collector);
		config.run();

		for (Configuration<ResultCollector> conf: collector.getAcceptingConfigurations()){
			System.out.println("Result: "+conf.getUpperTape());
		}
	}
}
