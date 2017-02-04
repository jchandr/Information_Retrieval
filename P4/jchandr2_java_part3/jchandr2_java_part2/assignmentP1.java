import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class AssignmentP1
{
	public Docs_table_entry create_table_entries(String datafilename,int dID) throws Exception
	{		
		Docs_table_entry d = new Docs_table_entry();
		int i = 0;
		String file_String = "";
		Scanner in_file_stream = new Scanner(new File(datafilename));
		List<String> lines = new ArrayList<String>();

		while (in_file_stream.hasNextLine()) 
		{
			lines.add(in_file_stream.nextLine());
		}

		String[] arr = lines.toArray(new String[0]);

		for(i=0; i < arr.length; i++)
			file_String += arr[i]+" ";
		in_file_stream.close();

		Pattern p = Pattern.compile("<DOCNO>(.+?)</DOCNO>");
		Matcher m = p.matcher(file_String);
		m.find();
		//String docNo = m.group(1);
		p = Pattern.compile("<HEADLINE>(.+?)</HEADLINE>");
		m = p.matcher(file_String);
		m.find();
		String headline = m.group(1);
		headline = headline.replaceAll(","," ");
		p = Pattern.compile("<TEXT>(.+?)</TEXT>");
		m = p.matcher(file_String);
		m.find();
		String snippet = m.group(1).substring(0,39);
		snippet = snippet.replaceAll("<P>","");
		snippet = snippet.replaceAll("</P>","");
		snippet = snippet.trim();
		snippet = snippet.replaceAll(","," ");
		
		// Pattern regExp = Pattern.compile(".");
		String textInDoc = file_String;
		String[] termArray =null;
		// Matcher patMatcher = regExp.matcher(file_String);
		
		textInDoc = textInDoc.toLowerCase();

			//textInDoc = patMatcher.group(1);
			textInDoc = textInDoc.replaceAll("<.*?>"," ");
			textInDoc = textInDoc.replaceAll("-", " ");
			textInDoc = textInDoc.replaceAll(", ", " ");
			textInDoc = textInDoc.replaceAll("; ", " ");
			textInDoc = textInDoc.replaceAll("\\? ", " ");
			textInDoc = textInDoc.replaceAll(": ", " ");
			textInDoc = textInDoc.replaceAll("! ", " ");
			textInDoc = textInDoc.replaceAll("\\. "," ");
			textInDoc = textInDoc.replaceAll("\\.\"|\\.'" , " ");
			textInDoc = textInDoc.replaceAll(" +"," ");
			//textInDoc = textInDoc.replaceAll("[a-z]|[A-Z]"," ");
			termArray = textInDoc.split(" ");
		
		String[] stopwords = {"and","a","the","an","by","from","for","hence","of","the","with","in","within","who","when","where","why","how","whom","have","had","has","not","for","but","do","does","done"};
		for(i=0; i < termArray.length; i++)
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
		}
		
		d.term_array_length = termArray.length;

		for(i=0; i < termArray.length; i++)
			termArray[i] = termArray[i].toLowerCase();

		ArrayList<String> tokenList = new ArrayList<String>();
		for(i=0; i < termArray.length; i++)
		{
			if((!(termArray[i].length() == 1)) && (!termArray[i].equals("")) && (!Arrays.asList(stopwords).contains(termArray[i])) &&(!termArray[i].equals("ha")))
				tokenList.add(termArray[i]);
		}
		
		Collections.sort(tokenList);

		Set<String> unique = new HashSet<String>(tokenList);
		TreeMap<String, Integer> map = new TreeMap<>();
		for (String key : unique) 
		{
			map.put(key,Collections.frequency(tokenList, key));
		}
		
		d.headline = headline;
		d.docNo = Integer.toString(dID);
		d.docLength = map.size();
		d.snippet = snippet;
		d.dictionary = map;
		d.docPath = datafilename;
		d.docId = dID;
		
		FileWriter fw = new FileWriter("docsTable.csv",true);
		fw.write(dID+","+d.docPath+ "," + d.headline + "," + d.docLength + "," + d.snippet + "," + d.docPath + "\n");
		fw.close();
		
		return d;
	}
}