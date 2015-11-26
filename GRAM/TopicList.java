//Program by Milk Charity

/*
 *	This class imports the topics from a directory
 *	So that GRAM can use it in his conversations
 *	And identify the topic of the conversation the user has inputted
*/

import java.util.*;					//import java util directory classes
import java.io.*;					//import java io directory classes


public class TopicList {

	//define the global variables
	public HashMap<Integer, String> topicTable = new HashMap<Integer, String>();		//the master list
	public HashMap<String, int[]> topicTableBin = new HashMap<String, int[]>();		//the binary lists for the topics
	public int index = 0;																//the indexer for the master list
	public File library;

	//Constructor for the topics list
	public TopicList(String directory) {
		library = new File(directory);			//find the directory of lists
    }

	/////////////////////////////////			MASTER LIST FUNCTIONS			///////////////////////////////////////

    //create the master list using all of the files found
    public void makeMasterList(){
    	File[] lists = library.listFiles();			//get all of the list files

		//import all of the items in every list in the directory
		for(File file : lists){
			if(file.isFile())
				importList(file);
		}
    }

    //import the items of the list file
	private void importList(File file){
		try{
			Scanner in = new Scanner(file);
			while(in.hasNextLine()){			//for each line
				String word = in.nextLine();		//get the word
				topicTable.put(index, word);		//put the index - word pair into the hashmap
				index++;							//increase the index for the next item
			}
			in.close();
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}
	}

	////////////////////////////////////////////////			BINARY LIST FUNCTIONS			//////////////////////////////////////////////


	//make all of the lists in the directory into binary based on the master list
	public void binary(){
		File[] lists = library.listFiles();
		//import all of the items in every list in the directory
		for(File file : lists){
			if(file.isFile())
				topicTableBin.put(file.getName().substring(0, file.getName().indexOf(".")), makeBinaryList(file));
		}

		//print out all of the binary tables
		Set set = topicTableBin.entrySet();
		Iterator i = set.iterator();
		while(i.hasNext()){
			Map.Entry me = (Map.Entry)i.next();
			System.out.println(me.getKey() + ": " + showBinArray((int[])me.getValue()));
		}
	}

	//make the binary array for each topic
	private int[] makeBinaryList(File file){
		int[] binList = new int[topicTable.size()];		//make a new binary list
		for(int a = 0; a < binList.length; a++){			//set all default to zero
			binList[a] = 0;
		}

		//import the file's words
		try{
			Scanner in = new Scanner(file);
			while(in.hasNextLine()){			//for each line
				String word = in.nextLine();		//get the word
				binList[getHashIndex(word)] = 1;			//find the index of the word in the master list
			}
			in.close();
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}

		//return the binary list for the file
		return binList;
	}

	//find the index of a certain word in the master list
	public int getHashIndex(String word){
		Set set = topicTable.entrySet();
		Iterator i = set.iterator();

		//iterate for a matching word
		while(i.hasNext()){
			Map.Entry me = (Map.Entry)i.next();
			if(me.getValue().equals(word))
				return (int)me.getKey();
		}
		return -1;
	}


	//////////////////////////////////////////////			USER SENTENCE ANALYSIS FUNCTION			//////////////////////////////////////

	//break up the sentence into individual words for analysis
	public int[] getSentenceBin(String sentence){
		//get all of the words in the sentence
		String[] words = sentence.split(" ");

		//define the binary sentence
		int[] binSentence = new int[topicTable.size()];
		for(int b = 0; b < binSentence.length; b++){
			binSentence[b] = 0;
		}

		//find the words that are in the binary
		for(int a = 0; a < words.length; a++){
			int i = getHashIndex(words[a]);
			if(i != -1){
				binSentence[i] = 1;
			}
		}
		return binSentence;
	}

	//cosine function for determining the topic - inputs [binary array of topic bank, binary array of the sentence]
	private double cosineVector(int[] topic, int[] sentence){
		//solve for the numerator
		double numerator = 0;
		for(int c = 0; c < sentence.length; c++){
			numerator += (topic[c] * sentence[c]);
		}

		//solve for the denominator
		double denominator = 1;
		//part one of the equation
		double p1 = 0;
		for(int a = 0; a < topic.length; a++){
			p1 += topic[a];
		}
		denominator *= Math.sqrt(p1);

		//part two of the equation
		double p2 = 0;
		for(int b = 0; b < sentence.length; b++){
			p2 += sentence[b];
		}
		denominator *= Math.sqrt(p2);

    //get the result
		return numerator / denominator;

	}
	public String getSubject(String sentence){
		//convert the sentence to a binary array
		int[] binSent = getSentenceBin(sentence);

		//make the score reports for the topics
		HashMap<String, Double> topicScores = new HashMap<String, Double>();

		//get all of the topics
		Set set = topicTableBin.entrySet();
		Iterator i = set.iterator();
		while(i.hasNext()){
			Map.Entry me = (Map.Entry)i.next();
			double result = cosineVector((int[])me.getValue(), binSent); //get the score from the comparison of the sentence and the topic list
			topicScores.put((String)me.getKey(), (double)result);
		}

		//get the best score out of the topics
		double bestScore = 0;
		String subject = "";

		Set set2 = topicScores.entrySet();
		Iterator i2 = set2.iterator();
		while(i2.hasNext()){
			Map.Entry me2 = (Map.Entry)i2.next();
			double v = (double)me2.getValue();

			if(v > bestScore){
				bestScore = v;			//set the highest score to the new score
				subject = (String)me2.getKey();				//set the subject for the highest score to the new subject
			}
		}

		return subject;

	}

	///////////////////////////////////////////////				DEBUGGING FUNCTION 				/////////////////////////////////////

	//print out the topic table indexes and items
  public void printMasterList(){
		for(int i : topicTable.keySet()){
			System.out.println(i + " - " + topicTable.get(i));
		}
  }

	//output an int[] array
	public String showBinArray(int[] arr){
		String output = "[";
		for(int b = 0; b < arr.length - 1; b++){
			output += (arr[b] + ", ");
		}
		output += (arr[arr.length - 1] + "]");
		return output;
	}

}