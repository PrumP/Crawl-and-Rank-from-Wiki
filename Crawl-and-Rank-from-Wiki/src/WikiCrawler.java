import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.io.PrintWriter;
import java.io.FileNotFoundException;

public class WikiCrawler 
{
	
	private static final String BASE_URL  = "https://en.wikipedia.org";
	private String seedURL;
	private String[] keywords;
	private int max;
	private String fileName;
	
	private int pagesReq;
	
	private final String robotsURL = BASE_URL +"/robots.txt";
	private ArrayList<String> disallowedSites;
	
	private final String rawTextPage1= "https://en.wikipedia.org/w/index.php?title=";
	private final String rawTextPage2= "&action=raw";
	
	private int num_nodes;

	private ArrayList<String> edgesList;
	private Set<String> nodeList;
	private ArrayList<String> keywordExist;
	private ArrayList<String> noKeywordExist;
	
	public WikiCrawler(String seedURL, String[] keywords, int max, String fileName) 
	{
		this.seedURL = seedURL;
		this.keywords = keywords;
		this.max = max;
		this.fileName = fileName;
		this.disallowedSites =  new ArrayList<String>();
		setDisallowedSites();
		
		this.pagesReq = 0;
		this.num_nodes = 0;
		
		
		this.edgesList = new ArrayList<String>();
		this.nodeList = new HashSet<String>();
		this.keywordExist= new ArrayList<String>();
		this.noKeywordExist = new ArrayList<String>();

	}
		
	public void setDisallowedSites()
	{
		try 
		{
			URL wikiRobots = new URL(this.robotsURL);
			URLConnection conWikiRobots = wikiRobots.openConnection();
			Scanner pageIn = new Scanner(conWikiRobots.getInputStream());
			
			String line = "";
			
			while(pageIn.hasNextLine())
			{	
				line = pageIn.nextLine();
				
				if(line.startsWith("Disallow: /wiki"))
				{
					this.disallowedSites.add(line.substring(10, line.length()));
				
				}
	            	
			}
			
		} 
		catch (Exception ex) 
		{
			ex.printStackTrace();	
	    }
	}
	
	public boolean isDisallowed(String link)
	{
		boolean disallowed = false;
		
		for(int i=0; i<this.disallowedSites.size(); i++)
		{
			if(this.disallowedSites.get(i).contains(link))
			{
				disallowed = true;
			}
		}
		
		return disallowed;
	}
	
	public ArrayList<String> findAllowedLinks(String pageURL)
	{
		ArrayList<String> pageLinks = new ArrayList<String>();
		
		try 
		{
			URL wikiPage = new URL(this.BASE_URL +pageURL);
            URLConnection conWikiPage = wikiPage.openConnection();
            
            Scanner pageIn = new Scanner(conWikiPage.getInputStream());
            
            String word = ""; 
            
            boolean flag = false;
            String relativeLink = "";
            
          
            while(pageIn.hasNext())
            {
            	word = pageIn.next();
            	if(word.startsWith("<p>"))
            	{
            		flag = true;
            	}
            		
            	if(flag && word.startsWith("href") && !word.contains("#") && !word.contains(":"))
            	{
            		relativeLink = word.substring(6,word.length()-1);
            		if(!isDisallowed(relativeLink))
            		{
            			pageLinks.add(relativeLink);
            		}
            		else
            		{
            			System.out.println("Disallowed");
            		}
            	
            	}
            	
            }
          
        } 
		
		catch (Exception ex) 
		{
			//ex.printStackTrace();	
        }
		
		return pageLinks;
	}
	
	public String downLoadRawTextPage(String pageURL)
	{
		String pageName = pageURL.substring(6);
		String pageContent = "";
		
		try 
		{
			URL wikiRawPageURL = new URL(this.rawTextPage1+pageName+this.rawTextPage2);
			URLConnection conWikiRawPage = wikiRawPageURL.openConnection();
			
			Scanner pageIn = new Scanner(conWikiRawPage.getInputStream());
			
			while(pageIn.hasNext())
			{	
				pageContent += pageIn.next().toLowerCase() + " ";
			}
			
			
		} 
		catch (Exception ex) 
		{
			//ex.printStackTrace();
	    }		
		
		return pageContent;
	}
	
	public boolean keywordsExist(String pageContent)
	{
			boolean exist = true;
			
			int[] keywordCount = new int[this.keywords.length];
			
			
			
			for(int k=0; k<this.keywords.length; k++)
			{
				
				String[] terms = this.keywords[k].toLowerCase().split("\\s+");
				String regex =  "(\\s|^)" + terms[0];
				for(int t=1; t<terms.length; t++)
				{
					regex += "\\s+" + terms[t];
				}
				
				regex += "(\\s|$)";
				
				Pattern p = Pattern.compile(regex);
				
				if(p.matcher(pageContent).find())
				{
							keywordCount[k]++;
				}			
			}
					
			for(int k=0; k<this.keywords.length; k++)
			{
				
				if(keywordCount[k]< 1)
				{
					exist = false;
				}
			}
			
			return exist;
		}
		
	
	public void crawl() 
	{
	
		Queue<String> Q = new LinkedList<String>();
		Set visited = new HashSet<String>();
		
		ArrayList<String> links = new ArrayList<String>();
		String src = "";
		String dest = "";
		String edge = "";
		boolean kwExistFlag = false;
			
		if(keywordsExist(downLoadRawTextPage(this.seedURL)))
		{
			pagesReq++;
			Q.add(this.seedURL);
			visited.add(this.seedURL);
			this.keywordExist.add(this.seedURL);
			this.nodeList.add(this.seedURL);
			this.num_nodes = this.nodeList.size();
			
			
			while(!Q.isEmpty() && (this.num_nodes < this.max))
			{
				src = Q.remove();
				this.nodeList.add(src);
				this.num_nodes = this.nodeList.size();
				
				links = findAllowedLinks(src);
				pagesReq++;
				
				for(int i=0; i<links.size();i++)
				{
					
					pagesReq++;
					String ith_link = links.get(i);
					if(this.keywordExist.contains(ith_link))
					{
						kwExistFlag = true;
					}
					else if(this.noKeywordExist.contains(ith_link))
					{
						kwExistFlag = false;
					}
					else 
					{
						kwExistFlag = keywordsExist(downLoadRawTextPage(ith_link));
						if(kwExistFlag)
						{
							this.keywordExist.add(ith_link);
						}
						else
						{
							this.noKeywordExist.add(ith_link);
						}
						
					}
					
					if(kwExistFlag && (this.num_nodes < this.max))
					{
						
						dest = ith_link;
						this.nodeList.add(dest);
						this.num_nodes = this.nodeList.size();
						edge = src+"\t"+dest;
						
						if(!visited.contains(dest))
						{
							Q.add(dest);
							visited.add(dest);
							
						}
						if(!dest.equals(src) && !this.edgesList.contains(edge))
						{
							System.out.println(src+"\t"+dest+"\t"+this.num_nodes);
							this.edgesList.add(edge);
							
						}
						
					}
					
					if(this.pagesReq%100==0)
					{
						
						try 
						{
							TimeUnit.SECONDS.sleep(5); // wait for 5 seconds
						} 
						catch (InterruptedException e) 
						{
							//e.printStackTrace();
						}					
					}
				}
			}		
		}
		System.out.println("Connections:" +this.pagesReq);

		try {
			PrintWriter fileOut= new PrintWriter(this.fileName);
			fileOut.println(this.num_nodes);
		
			for(int i=0; i<this.edgesList.size(); i++)
			{
				fileOut.println(this.edgesList.get(i));
			}
			
		fileOut.close();
		}
		catch (FileNotFoundException e) {
			
		}	     
	}
	
	
	public int getNumNodes()
	{
		return this.num_nodes;
	}	
}
