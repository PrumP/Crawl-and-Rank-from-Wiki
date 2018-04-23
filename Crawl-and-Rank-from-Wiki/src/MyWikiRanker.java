
/*
 * Top Page(100) content results are stored in TopX100_epsilon(0.01 or 0.005).txt where X is PageRank,OutDegree and InDegree
 * Top link(15) content results are stored in TopX15_epsilon(0.01 or 0.005).txt where X is PageRank,OutDegree and InDegree
 * For this project numPages is always 100.  
 * 
 * Jaccard similarity is computed firstly for content of Top100 pages(as assignment required), secondly
 * for Top15 links. 
 * 
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class MyWikiRanker 
{

    private static ArrayList<String> pageContentTop_100PageRank;  // Page Contents for Top 100
	private static ArrayList<String> pageContentTop_100OutDegree;
	private static ArrayList<String> pageContentTop_100InDegree;
	
	private static ArrayList<String> pageLinkTop_15PageRank;    //   Page link for Top 15
	private static ArrayList<String> pageLinkTop_15OutDegree;
	private static ArrayList<String> pageLinkTop_15InDegree;
	
	private static String highestPageRank;
	private static String highestOutDegree;
	private static String highestInDegree;
	
	public static void main(String[] args) throws FileNotFoundException
	{
			
	    Scanner in = new Scanner(System.in);
	    double epsilon=0.0;
	    int numPage=100;
			
		
	    System.out.print("Enter value of Epsilon: ");
	    epsilon = in.nextDouble();
		
		

	    PageRank pk = new PageRank("MyWikiGraph.txt",  epsilon);
		
	    pageContentTop_100PageRank = new ArrayList<String>();
	    pageContentTop_100OutDegree = new ArrayList<String>();
	    pageContentTop_100InDegree = new ArrayList<String>();
		
	    pageLinkTop_15PageRank=new ArrayList<String>();
	    pageLinkTop_15OutDegree=new ArrayList<String>();
	    pageLinkTop_15InDegree=new ArrayList<String>();
		
	    System.out.println("*****************Top " +numPage+" Page Rank********************");
	    String[] topKPageRank = pk.topKPageRank(numPage);
	    highestPageRank = topKPageRank[0];
		
	    for(int i=0; i<topKPageRank.length; i++)
	    {
	        if(i<15)
	            pageLinkTop_15PageRank.add(topKPageRank[i]);
	        System.out.println("Downloading Raw Text Page for........."+topKPageRank[i]);
	        pageContentTop_100PageRank.add(downLoadRawTextPage(topKPageRank[i]));
	    }
		
		
	    System.out.println("*****************Top " +numPage+" Out  Degree********************");
	    String[] topKOutDegree = pk.topKOutDegree(numPage);
	    highestOutDegree = topKOutDegree[0];
		
	    for(int i=0; i<topKOutDegree.length; i++)
	    {
	        if(i<15)
	            pageLinkTop_15OutDegree.add(topKOutDegree[i]);
	        System.out.println("Downloading Raw Text Page for........."+topKOutDegree[i]);
	        pageContentTop_100OutDegree.add(downLoadRawTextPage(topKOutDegree[i]));
	    }
		
	
		
	    System.out.println("*****************Top "+numPage+" in Degree********************");
	    String[] topKInDegree = pk.topKInDegree(numPage);
	    highestInDegree = topKInDegree[0];
		
	    for(int i=0; i<topKInDegree.length; i++)
	    {
	        if(i<15)
	            pageLinkTop_15InDegree.add(topKInDegree[i]);
	        System.out.println("Downloading Raw Text Page for........."+topKInDegree[i]);
	        pageContentTop_100InDegree.add(downLoadRawTextPage(topKInDegree[i]));
	    }
	
	    //Writing data to files
	    System.out.println("*****************Writing data to files********************");
		
	    String filename="";
	    PrintWriter pw;
		
	    filename = "TopPageRank100_"+epsilon+".txt";
		
	    pw= new PrintWriter(filename);
	    for(int i=0; i<pageContentTop_100PageRank.size(); i++)
	    {
	        pw.println(pageContentTop_100PageRank.get(i));
	    }
	    pw.close();
		
		
		
	    filename = "TopOutDegree100_"+epsilon+".txt";
		
	    pw= new PrintWriter(filename);
	    for(int i=0; i<pageContentTop_100OutDegree.size(); i++)
	    {
	        pw.println(pageContentTop_100OutDegree.get(i));
	    }
			
	    pw.close();
	
	 
	    filename = "TopInDegree100_"+epsilon+".txt";
		
	    pw= new PrintWriter(filename);
			
	    for(int i=0; i<pageContentTop_100InDegree.size(); i++)
	    {
	        pw.println(pageContentTop_100InDegree.get(i));
	    }
			
	    pw.close();
			
	    //************** Start of writing Top15 Links************//
	    filename = "TopPageRank15_"+epsilon+".txt";
			
	    pw= new PrintWriter(filename);
	    for(int i=0; i<pageLinkTop_15PageRank.size(); i++)
	    {
	        pw.println(pageLinkTop_15PageRank.get(i));
	    }
	    pw.close();
			
			
			
	    filename = "TopOutDegree15_"+epsilon+".txt";
			
	    pw= new PrintWriter(filename);
	    for(int i=0; i< pageLinkTop_15OutDegree.size(); i++)
	    {
	        pw.println( pageLinkTop_15OutDegree.get(i));
	    }
				
	    pw.close();
		
		 
	    filename = "TopInDegree15_"+epsilon+".txt";
			
	    pw= new PrintWriter(filename);
				
	    for(int i=0; i< pageLinkTop_15InDegree.size(); i++)
	    {
	        pw.println( pageLinkTop_15InDegree.get(i));
	    }
				
	    pw.close();
				
				
		
	    System.out.println("Iterations:" + pk.getIterations());
	    System.out.println("Highest Page Rank:" + highestPageRank );
	    System.out.println("Highest Out Degree:" + highestOutDegree );
	    System.out.println("Highest In Degree:" + highestInDegree );

		
	    System.out.println(" Jaccard similarity between 100 PageRank and InDegree Contents" + exactJaccard("TopPageRank100_"+epsilon+".txt", "TopInDegree100_"+epsilon+".txt"));
	    System.out.println(" Jaccard similarity between 100 PageRank and OutDegree Contents" + exactJaccard("TopPageRank100_"+epsilon+".txt", "TopOutDegree100_"+epsilon+".txt"));
	    System.out.println(" Jaccard similarity between 100 InDegree and OutDegree Contents" + exactJaccard("TopInDegree100_"+epsilon+".txt", "TopOutDegree100_"+epsilon+".txt"));
		
	    System.out.println(" Jaccard similarity between 15 PageRank and InDegree Links" + exactJaccard("TopPageRank15_"+epsilon+".txt", "TopInDegree15_"+epsilon+".txt"));
	    System.out.println(" Jaccard similarity between 15 PageRank and OutDegree Links" + exactJaccard("TopPageRank15_"+epsilon+".txt", "TopOutDegree15_"+epsilon+".txt"));
	    System.out.println(" Jaccard similarity between 15 InDegree and OutDegree Links" + exactJaccard("TopInDegree15_"+epsilon+".txt", "TopOutDegree15_"+epsilon+".txt"));
	}
	
	// End Of Main()
	
	
	public static String downLoadRawTextPage(String pageURL)
	{
		String pageName = pageURL.substring(6);
		String pageContent = "";
		
		final String rawTextPage1= "https://en.wikipedia.org/w/index.php?title=";
		final String rawTextPage2= "&action=raw";
		
		try 
		{
			URL wikiRawPageURL = new URL(rawTextPage1+pageName+rawTextPage2);
			URLConnection conWikiRawPage = wikiRawPageURL.openConnection();
			
			Scanner pageIn = new Scanner(conWikiRawPage.getInputStream());
			
			while(pageIn.hasNext())
			{	
				pageContent += pageIn.next().toLowerCase() + " ";
				
			}
				
			
		} 
		catch (Exception ex) 
		{			
			ex.printStackTrace();
	    }
		
		return pageContent;
	}
	
	
	public static String removePunctuation(String t)
	{
			
		t = t.replace(".", "");
		t = t.replace(",", "");
		t = t.replace(":", "");
		t = t.replace(";", "");
		t = t.replace("'", "");
		
		return t;
	}
	
	
	
	public static ArrayList<String> findTerms(String file) throws FileNotFoundException
	{
		ArrayList<String> termsList = new ArrayList<String>();
		
		
		File inputFile = new File(file);

		Scanner fileIn= new Scanner(inputFile);
		
		String t = "";
		while(fileIn.hasNext())
		{
			t = fileIn.next();
			t = t.toLowerCase();
			if((t.length()>=3) && !(t.equalsIgnoreCase("the")))
			{
				t = removePunctuation(t);
				termsList.add(t);
			}
		}
		
		fileIn.close();
		
		return termsList;
	}
		
	
	public static double exactJaccard(String file1, String file2) throws FileNotFoundException
	{
		double exactJac = 0;
		
		ArrayList<String> s1 = findTerms(file1);
		ArrayList<String> s2 = findTerms(file2);
		
		Set<String> set1 = new HashSet<String>();
		Set<String> set2 = new HashSet<String>();
		Set<String> union = new HashSet<String>();
		Set<String> inter = new HashSet<String>();
		set1.addAll(s1);
		set2.addAll(s2);
		
		union.addAll(set1);
		union.addAll(set2);
		int unionCount = union.size();
		
		inter.addAll(set1);
		inter.retainAll(set2);
		int interCount = inter.size();
		
		exactJac = ((double)interCount)/((double)unionCount);
		
		return exactJac;
	}		
}
