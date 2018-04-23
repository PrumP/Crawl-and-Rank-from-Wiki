import java.text.SimpleDateFormat;
import java.util.Calendar;

public class WikiTennisCrawler 
{
	
    public static void main(String[] args) 
	{
		
		long startTime;
		long timeTaken;
		String[] topics = {"tennis", "grand slam"};

		startTime = System.currentTimeMillis();
		WikiCrawler w = new WikiCrawler("/wiki/Tennis", topics, 1000, "WikiTennisGraph.txt");
		w.crawl();
		
		System.out.println("Number of Nodes:" + w.getNumNodes());
		
		double sec;
		timeTaken = System.currentTimeMillis() - startTime;
		sec = (double) timeTaken / 1000;
		System.out.println("total time in seconds: " + sec);
			
	}
}
