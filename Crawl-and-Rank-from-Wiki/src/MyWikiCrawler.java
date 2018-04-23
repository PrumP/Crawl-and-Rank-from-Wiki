import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MyWikiCrawler 
{
	
    public static void main(String[] args) 
    {
		
		long startTime;
		long timeTaken;
		
		String[] topics = {"malware", "security"};
		
		startTime = System.currentTimeMillis();
		WikiCrawler w = new WikiCrawler("/wiki/Malware", topics, 1000, "MyWikiGraph.txt");
		w.crawl();
		System.out.println("Nodes:" + w.getNumNodes());
		
		
		double sec;
		timeTaken = System.currentTimeMillis() - startTime;
		sec = (double) timeTaken / 1000;
		System.out.println("total time in seconds: " + sec);
		
	}
}
