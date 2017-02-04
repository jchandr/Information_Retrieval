import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;

public class assignmentP2{
	
	static TreeMap<String, Integer> collection_dictionary = new TreeMap<>();
	static ArrayList<String> file_list = new ArrayList<String>();
	static ArrayList<Integer> document_freq = new ArrayList<Integer>();
	static ArrayList<Integer> posting_list_term_freq = new ArrayList<Integer>();
	static ArrayList<Integer> posting_list_docID = new ArrayList<Integer>();
	static ArrayList<Integer>  offset =  new ArrayList<Integer>();
	static int total_number_of_tokens = 0;
	public static void iterate_through_dir(File n) throws Exception
	{
		if(n.isDirectory())
		{
			String[] subdirs = n.list();
			for(String filename : subdirs)
				iterate_through_dir(new File(n,filename));
		}
		else
		if(!n.getAbsolutePath().endsWith("txt"))
			file_list.add(n.getPath());
	}
	
	public static Docs_table_entry[] create_docsTable(Docs_table_entry[] x,int n) throws Exception
	{
		int i = 1;
		for(String current_file_name : file_list)
		{
			AssignmentP1 a = new AssignmentP1();
			x[i] = a.create_table_entries(current_file_name,i);
			x[i].docId = i;
			TreeMap <String,Integer> current_dictionary = new TreeMap<>();
			current_dictionary = x[i].dictionary;
			total_number_of_tokens = total_number_of_tokens + x[i].term_array_length;
			collection_dictionary.putAll(current_dictionary);
			i++;
		}
		//generating collection frequency
		for(Entry<String,Integer> collection_entry : collection_dictionary.entrySet())
		{
			String collection_key = collection_entry.getKey();
			int total = 0;
			for(i = 1;i<n;i++)
			{
				if(x[i].dictionary.containsKey(collection_key))
					total = total + x[i].dictionary.get(collection_key);
			}
			collection_entry.setValue(total);
		}
		i = 1;
		Integer current_offset = 0;
		//generating document fequency
		for(Entry<String,Integer> entry : collection_dictionary.entrySet()) 
		{
			  String key = entry.getKey();
			  int current_term_doc_freq = 0;
			  offset.add(current_offset);
			  ArrayList<Integer> current_term_docIDs = new ArrayList<Integer>();
			  for(int j = 1;j<n;j++)
			  {
				  if(x[j].dictionary.containsKey(key))
				  {
					  current_term_docIDs.add(j);
					  current_term_doc_freq++;
				  }
			  }
			  
			  for(int j = 0;j<current_term_doc_freq;j++)
			  {
				  posting_list_term_freq.add(x[current_term_docIDs.get(j)].dictionary.get(key));
				  posting_list_docID.add(current_term_docIDs.get(j));
			  }
			  
			  current_offset = (current_offset+1) + (current_term_doc_freq-1);
			  document_freq.add(current_term_doc_freq);
		}
		
		return x;
	}
	
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		if(args.length == 0)
		{
			System.out.println("No input Directory Specified.\nUsage: java asssignmentP2 <directory_name>");
			return;
		}
		String dir = args[0];
		File dir1 = new File(dir);
		File docsTable = new File("docsTable.csv");
		File postingList = new File("postings.csv");
		File dictionary = new File("dictionary.csv");
		File total = new File("total.txt");
		if(docsTable.exists())
			docsTable.delete();
		if(dictionary.exists())
			dictionary.delete();
		docsTable.createNewFile();
		iterate_through_dir(dir1);
		int total_number_of_files = file_list.size();
		Docs_table_entry[] entries = new Docs_table_entry[total_number_of_files+1];
		entries = create_docsTable(entries,total_number_of_files+1);
		FileWriter fw = new FileWriter("dictionary.csv");
		int i=0;
		for(Entry<String,Integer> entry : collection_dictionary.entrySet()) 
		{
			String key = entry.getKey();
			Integer value = entry.getValue();
			int term_doc_freq = document_freq.get(i);
			int current_offset = offset.get(i);
			fw.append(key+","+value +","+term_doc_freq+","+current_offset+"\n");
			i++;
		}
		fw.close();
		fw = new FileWriter("postings.csv");
		for(i=0;i<posting_list_docID.size();i++)
		{
			fw.append(posting_list_docID.get(i) + "," + posting_list_term_freq.get(i) + "\n");
		}
		fw.close();
		fw = new FileWriter("total.txt");
		fw.write(total_number_of_tokens);
		fw.close();
	}
}
