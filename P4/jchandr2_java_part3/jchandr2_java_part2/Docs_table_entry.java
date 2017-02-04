import java.util.TreeMap;

public class Docs_table_entry{
	public
		String docNo;
		int docId;
		String headline;
		int docLength;
		String snippet;
		String docPath;
		TreeMap<String, Integer> dictionary = new TreeMap<>();
		int term_array_length;
	public
		Docs_table_entry(Docs_table_entry n)
		{
		 	dictionary = n.dictionary;
		 	docId = n.docId;
		 	docLength = n.docLength;
		 	docNo = n.docNo;
		 	docPath = n.docPath;
		 	headline = n.headline;
		 	snippet = n.snippet;
		}
	public
		Docs_table_entry()
		{
		
		}
}
