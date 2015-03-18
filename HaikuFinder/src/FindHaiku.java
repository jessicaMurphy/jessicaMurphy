import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.StringTokenizer;

public class FindHaiku
{
    Hashtable syllableTable;      	    // words and syllable counts
    ArrayList current;  // the current line of pairs
    int syllables;            			// number of syllables in the current line
    ArrayList<String> lines;    // accumulated lines
    BufferedReader fileIn;       		// file reading
    StringTokenizer tokens;  			// tokens read


   
    public FindHaiku() 
    {
    	syllableTable = new Hashtable();
    	ArrayList current = new ArrayList();
    	syllables = 0;
    	ArrayList lines = new ArrayList();

    	String filename ="bin/TheBrothersGrimmFairytales.txt";
    	try
    	{
    		readDictionary(filename);
    	}
    	catch(Exception e)
    	{
    		System.out.println ("Couldn't read the dictionary: " + filename);
    		e.printStackTrace();
    		System.exit (-1);
    	}
    }

    //--------------------------------------------------------MAIN--------------------------------------------------------------------------------

    public static void main(String[] args)
    {
    	// this needs to be the full filepath if its being provided to 
    	String filename = "TheBrothersGrimmFairytales.txt";
    	
    	if(filename.isEmpty())
    		if(args.length == 0) 
    	{
            System.out.println("You must specify a filename.");
            System.exit(0);
        }
    	


        FindHaiku haiku = new FindHaiku();

        int[] foundOne = {5, 7, 5};
        haiku.getForms (filename, foundOne);
    }//end main
    
    
    
    
    
    
 //--------------------------------------------------------METHODS--------------------------------------------------------------------------------   
    
    //read file and build syllableTable
    public void readDictionary(String filename) throws FileNotFoundException, IOException 
    {
	   	FileReader fileReader = new FileReader(filename);
		BufferedReader in = new BufferedReader(fileReader);
		String string;
		while((string = in.readLine()) != null) // this is the standard pattern for reading a file line by line
		{
			System.out.println("DEBUG: " + string); // remove later -- make sure every line is being read for now
			if (string.length() > 0 && string.charAt(0) == '#') continue;
			wordEntry (string);
		}
    }

    
    //make an entry in syllableTable
    public void wordEntry(String s)
    {
    	int numSyll = 0;
        StringTokenizer sToke = new StringTokenizer(s);
        if (!sToke.hasMoreTokens())
        {
        	return;
        }
        
        String word = sToke.nextToken().toLowerCase();

      
        while (sToke.hasMoreTokens())
        {
            String phone = sToke.nextToken();
            if (hasDigit(phone))
            {
            	numSyll++;
            }
        }
        syllableTable.put (word, new Integer(numSyll));
    }

    
    //return true if the string contains a digit
    public boolean hasDigit(String s)
    {
    	for (int i=0; i<s.length(); i++) 
    	{
    		if (Character.isDigit(s.charAt (i)))
    		{
    			return true;
    		}
    	}	
    	return false;
    }

    //get the next word-syllable pair from the file
    public Pair getPair()
    {
    	String word = getWord();
    	int numSyll = numSyll();
    	numSyll = (Integer) syllableTable.get(clean(word));
    	return new Pair(word, numSyll);
    	
    }

    
    //clean string by tokenizing and taking first token that begins with a letter
    public String clean(String s)
    {
    	StringTokenizer st = new StringTokenizer(s,"0123456789@#$%^&*\"`'()<>[]{}.,:;?!+=/\\");
        while (st.hasMoreTokens()) 
        {
            String word = st.nextToken();
            if (Character.isLetter(word.charAt(0))) 
            {
        		return word.toLowerCase();
            }

        }	    
        return "";
    }

    
    //get the next word from the file
    public String getWord()
    {
    	while (tokens == null || tokens.hasMoreTokens() == false) 
    	{
    		tokens = getTokens();
    	}
    	return tokens.nextToken();
    }

    
    
    
    //read a line from the file and tokenize it
    public StringTokenizer getTokens()
    {
    	try 
    	{
    		String s = fileIn.readLine();
    		if(s == null)
    		{
    			System.exit(0);
    		}
    		return new StringTokenizer(s, " -");
    	} 
    	catch(Exception e) 
    	{
    		System.out.println("I/O Error.");
    		System.exit(-1);
    	}
    	return null;
    }

        
    
    
    // keep adding word pairs to the current line until the total syllables gets to numSyll
    public void getSyllables(int numSyllables) 
    {
    	while(syllables < numSyll())
    	{
    		Pair pair = getPair();
    		if (pair.numSyll == -1)
    		{
    			syllables = 0;
    			current.remove(current);
    			return;
    		}
    		else
    		{
    			syllables += pair.numSyll;
    			if (current == null) System.err.println("why won't this garbage work???????");
    			current.add(pair);
    		}
    	}
    }

	
    
    // find the next set of words that can be assembled into the number of syllables in each line for haiku
    public void getForm(int[] form) 
    {
    	for (int i = 0; i < form.length; i++)
    	{
    		getSyllables(form[i]);
    		if (syllables != 0 || syllables < form[i] || lines.size() != 0 )
    		{
    			lines.addAll(current);
    			current = new ArrayList<String>();
    			syllables = 0;
    		}
    		else
    		{
    			lines.removeAll(lines);
    		}
    	}
    }
    
  
    
    
    //get all the forms from the given file.
    public void getForms (String filename, int[] form) 
    {
    	try 
    	{
    		FileReader fileReader = new FileReader (filename);
    		fileIn = new BufferedReader(fileReader);
    	}
    	catch(Exception e)
    	{
    		System.out.println("Error opening file: " + filename);
    		e.printStackTrace();
    		System.exit (-1);
    	}
    	while(true) 
    	{
    		getForm(form);
    		lines.toString();
    		lines.removeAll(lines);
    	}
    }
    
    
    public boolean isVowel(char vowel)
    {
    	if(vowel == 'a' || vowel == 'e' || vowel == 'i' || vowel == 'o' || vowel == 'u')
    	{
    		return true;
    	}
    	else
    		return false;
    }
    
    


public int numSyll()
{
	int numSyll = 0;
	String word = getWord();
	for(int i= 0; i < word.length(); i++)
	{
		if(isVowel(word.charAt(i)) == true)
		{
			numSyll++;
		}
	}
	return numSyll;
}


}//end class

