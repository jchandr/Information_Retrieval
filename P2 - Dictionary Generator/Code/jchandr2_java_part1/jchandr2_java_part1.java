import java.io.*;
import java.util.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class assignmentP1 
{
	public static void main(String argv[]) throws Exception
	{
		if(argv.length == 0)
			System.out.println(" NO INPUT FILE SPECIFIED !!\nUsage: java assignmentP1 <input_file_name>");
		else
		{		
			String datafilename = argv[0];
			Integer i = 0;
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
	
			Pattern regExp = Pattern.compile("<DOC.*?>(.*?)<\\/DOC>");
			String textInDoc = "";
			String[] termArray =null;
			Matcher patMatcher = regExp.matcher(file_String);
			
			textInDoc = textInDoc.toLowerCase();

			if (patMatcher.find())
			{
				textInDoc = patMatcher.group(1);
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
				textInDoc = textInDoc.replaceAll(" [a-z] | [A-Z] "," ");
				termArray = textInDoc.split(" ");
			
			}
	
			for(i=0; i < termArray.length; i++)
			{
				termArray[i] = termArray[i].toLowerCase();
				termArray[i] = termArray[i].replaceAll("^\\[|\\]$","");
				termArray[i] = termArray[i].replaceAll("^\\(|\\)$","");
				termArray[i] = termArray[i].replaceAll("^'|'$","");
				termArray[i] = termArray[i].replaceAll("'","");
				termArray[i] = termArray[i].replaceAll("^\"|\"$", "");
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
	
			for(i=0; i < termArray.length; i++)
				termArray[i] = termArray[i].toLowerCase();

			ArrayList<String> tokenList = new ArrayList<String>();
			String resultToFile = "";
			String[] stopwords = {"and","a","the","an","by","from","for","hence","of","the","with","in","within","who","when","where","why","how","whom","have","had","has","not","for","but","do","does","done"};
			for(i=0; i < termArray.length; i++)
			{
				if((!(termArray[i].length() == 1)) && (!termArray[i].equals("")) && (!Arrays.asList(stopwords).contains(termArray[i])))
					tokenList.add(termArray[i]);
			}
	
			String[] resultTest = tokenList.toArray(new String[0]);
			
			Collections.sort(tokenList);

			Set<String> unique = new HashSet<String>(tokenList);
			TreeMap<String, Integer> map = new TreeMap<>();
			for (String key : unique) 
			{
				map.put(key,Collections.frequency(tokenList, key));
			}
			for(Map.Entry<String, Integer> entry : map.entrySet()) 
			{
				String key = entry.getKey();
				Integer value = entry.getValue();
				resultToFile += (key+" "+value+"\n");
			}
			String filename = "";
			if(argv.length == 1)
				filename= "dictionary.txt";
			else
				filename = argv[1];
			File f = new File("dictionary.txt");
			f.createNewFile();
			
			FileWriter fw = new FileWriter(filename);
			fw.write(resultToFile+"\n");
			fw.close();
		}
	}
}