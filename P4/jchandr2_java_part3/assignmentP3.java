import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.io.*;
import java.util.*;

public class assignmentP3 {
	static HashMap <Integer,Double> sc = new HashMap<Integer,Double>();
	static ArrayList<Integer> posting_tf = new ArrayList<Integer>();
	static ArrayList<Integer> posting_docID = new ArrayList<Integer>();
	static ArrayList<Integer> cf = new ArrayList<Integer>();
	static ArrayList<Integer> df = new ArrayList<Integer>();
	static ArrayList<String> filename = new ArrayList<String>();
	static ArrayList<Integer> doclength = new ArrayList<Integer>();
	static ArrayList<String> headline = new ArrayList<String>();
	static ArrayList<String> snippet = new ArrayList<String>();
	static ArrayList<String> dictionary = new ArrayList<String>();
	static ArrayList<Integer> offset = new ArrayList<Integer>();
	static ArrayList<Integer> docno = new ArrayList<Integer>();
	static ArrayList<String> docpath = new ArrayList<String>();
	static ArrayList<Double> scores;
	static Integer[] sorted_indexes;
	
	public static ArrayList<String> tokenize(String q)
	{
		ArrayList<String> query_tokenized = new ArrayList<String>();
		String[] termArray =null;
		q = q.toLowerCase();
		q = q.replaceAll("<.*?>"," ");
		q = q.replaceAll("-", " ");
		q = q.replaceAll(", ", " ");
		q = q.replaceAll("; ", " ");
		q = q.replaceAll("\\? ", " ");
		q = q.replaceAll(": ", " ");
		q = q.replaceAll("! ", " ");
		q = q.replaceAll("\\. "," ");
		q = q.replaceAll("\\.\"|\\.'" , " ");
		q = q.replaceAll(" +"," ");
		termArray = q.split(" ");
		String[] stopwords = {"and","a","the","an","by","from","for","hence","of","the","with","in","within","who","when","where","why","how","whom","have","had","has","not","for","but","do","does","done"};
		for(int i=0; i < termArray.length; i++)
		{
			termArray[i] = termArray[i].toLowerCase();
			termArray[i] = termArray[i].replaceAll("^\\[|\\]$","");
			termArray[i] = termArray[i].replaceAll("^\\(|\\)$","");
			termArray[i] = termArray[i].replaceAll("^'|'$","");
			termArray[i] = termArray[i].replaceAll("'","");
			termArray[i] = termArray[i].replaceAll("^\"|\"$", "");
			termArray[i] = termArray[i].replaceAll("[^a-zA-Z0-9]", "");
			termArray[i] = termArray[i].trim();

			if(termArray[i].endsWith("ies"))
			{ 
				if(!(termArray[i].endsWith("aies")) && !(termArray[i].endsWith("eies")))
				termArray[i] = termArray[i].replaceAll("ies$","y");
			}
			else if (termArray[i].endsWith("es"))
			{
				if(!(termArray[i].endsWith("aes")) && !(termArray[i].endsWith("ees")) && !(termArray[i].endsWith("oes")))
					termArray[i] = termArray[i].replaceAll("es$","e");
			}
			else if (termArray[i].endsWith("s"))
			{
				if(!(termArray[i].endsWith("us")) && !(termArray[i].endsWith("ss")))
					termArray[i] = termArray[i].replaceAll("s$","");
			}
			if((!(termArray[i].length() == 1)) && (!termArray[i].equals("")) && (!Arrays.asList(stopwords).contains(termArray[i])) &&(!termArray[i].equals("ha")))
				query_tokenized.add(termArray[i]);
		}
		
		return query_tokenized;
	}
	
	public static void create_arraylists() throws NumberFormatException, IOException
	{
		String file = "./jchandr2_java_part2/dictionary.csv";
		BufferedReader br = null;
		br = new BufferedReader(new FileReader(file));
		String line = null;
		while((line = br.readLine())!= null)
		{
			String[] record = line.split(",");
			dictionary.add(record[0]);
			cf.add(Integer.parseInt(record[1]));
			df.add(Integer.parseInt(record[2]));
			offset.add(Integer.parseInt(record[3]));
		}
		br.close();
		file = "./jchandr2_java_part2/docsTable.csv";
		br = new BufferedReader(new FileReader(file));
		while((line = br.readLine())!= null)
		{
			String[] record = line.split(",");
			docno.add(Integer.parseInt(record[0]));
			docpath.add(record[1]);
			headline.add(record[2]);
			doclength.add(Integer.parseInt(record[3]));
			snippet.add(record[4]);
		}
		br.close();
		file = "./jchandr2_java_part2/postings.csv";
		br = new BufferedReader(new FileReader(file));
		while((line = br.readLine())!= null)
		{
			String[] record = line.split(",");
			posting_docID.add(Integer.parseInt(record[0]));
			posting_tf.add(Integer.parseInt(record[1]));
		}
		br.close();
	}
	
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
	    return map.entrySet()
	              .stream()
	              .sorted(Map.Entry.comparingByValue(/*Collections.reverseOrder()*/))
	              .collect(Collectors.toMap(
	                Map.Entry::getKey, 
	                Map.Entry::getValue, 
	                (e1, e2) -> e1, 
	                LinkedHashMap::new
	              ));
	}
	
	public static void return_scores_for_query(ArrayList<String> query) throws Exception
	{
		double score = 0;
		scores = new ArrayList<Double>(docno.size());
		for(int j = 0;j < docno.size();j++)
			scores.add(0.0);
			
		for(String t : query)
		{
			int index;
			int col_freq;
			int doc_freq;
			int off;
			int term_freq;
			if(dictionary.contains(t))
			{
				index = dictionary.indexOf(t);
				col_freq = cf.get(index);
				doc_freq = df.get(index);
				off = offset.get(index);
				term_freq = 0;
				
				for(int x : docno)
				{
					for(int i = 0;i < doc_freq;i++)
					{
						double y1=0.0,y2;
						if(posting_docID.get(off+i).equals(x))
						{
								term_freq = posting_tf.get(off + i);
								//y1 = ((double) Math.log(0.9 * ((double) term_freq)/doclength.get(x-1)))/Math.log(2);
								y1 = 0.9 * ((double) term_freq/doclength.get(x-1));
						}
						else
							term_freq = 0;
						
						//y2 = ((double) Math.log(0.1 * ((double) col_freq)/dictionary.size()))/Math.log(2);
						y2 = 0.1 * ((double) col_freq/dictionary.size());
						//score = ((double)y1) + (double) y2 + (double) scores.get(x-1);
						score = (double) Math.log(y1 + y2)/Math.log(2);
						scores.set(x-1, score);
					}
				}
			}
		}
		sorted_indexes = new Integer[dictionary.size()];
		for(Integer x : docno)
		{
			sc.put(x, scores.get(x-1));
		}
		sc = (HashMap<Integer, Double>) sortByValue(sc);
		int i = 0;
		for(int k : sc.keySet())
		{
			sorted_indexes[i] = k-1;
			i++;
		}
	}
	
	private static void write_to_results_txt() throws Exception 
	{
		// TODO Auto-generated method stub
		FileWriter fw = new FileWriter("results.txt",true);
		int one_entry_min = 0;
		for(int i = 0;i < 5;i++)
		{
			if(scores.get(sorted_indexes[i]) != 0.00)
			{
				fw.append(headline.get(sorted_indexes[i])+"\n");
				fw.append(docpath.get(sorted_indexes[i])+"\n");
				fw.append("Computed Probability: " + scores.get(sorted_indexes[i])+"\n");
				fw.append(snippet.get(sorted_indexes[i])+"\n\n");
				one_entry_min = 1;
			}
		}
		if(one_entry_min > 0)
			fw.append("\n");
		fw.close();
	}
	
	public static void main(String[] args) throws Exception
	{
		// TODO Auto-generated method stub
		create_arraylists();
		Scanner reader = new Scanner(System.in);
		String query = null;
		File results = new File("results.txt");
		if(results.exists())
			results.delete();
		results.createNewFile();
		do{
			query = reader.nextLine();
			if(!query.equals("EXIT"))
			{
				ArrayList<String> query_words = new ArrayList<String>(tokenize(query));
				return_scores_for_query(query_words);
				write_to_results_txt();
			}
		}while(!query.equals("EXIT"));
		
		reader.close();
	}
}