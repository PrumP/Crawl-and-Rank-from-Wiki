import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class PageRank 
{

	private String fileName;
	private double apprx_parameter;
	private double beta;
	private LinkedHashMap<String, Double> pageRank;
	private LinkedHashMap<String, Double> sortedPageRank;
	private int N;
	private int iterations;
	
	private WebGraph graph;
	private ArrayList<String> nodeList;
	private LinkedHashMap<String, Integer> nodeIndexMap;
	private LinkedHashMap<String, Integer> sortedNodeOutDegreeMap;
    private LinkedHashMap<String, Integer> sortedNodeInDegreeMap;
    
    
	
	
	public PageRank(String fileName, double apprx_parameter)
	{
		System.out.println("Initialized variables");
		this.fileName = fileName;
		this.apprx_parameter = apprx_parameter;
		this.beta = 0.85;
		
		System.out.println("Create web graph");
		this.graph = new WebGraph(this.fileName);
		System.out.println("Finish web graph");
		
		System.out.println("Get number of nodes");
		this.N = this.graph.getNumNodes();
		System.out.println("Get list of nodes");
		this.nodeList = this.graph.getNodeList();
		System.out.println("Get index map");
		this.nodeIndexMap = this.graph.getNodeIndexMap();
		
		System.out.println("Get sorted node outdegree map");
		this.sortedNodeOutDegreeMap = this.graph.sortedNodeOutDegreeMap();
		System.out.println("Get sorted node indegree map");
		this.sortedNodeInDegreeMap = this.graph.sortedNodeInDegreeMap();
		this.iterations = 0;
		this.pageRank = new LinkedHashMap<String, Double>();
		System.out.println("Calculate Page Rank");
		calculatePageRank();
		System.out.println("Finish Calculate Page Rank");
		
		System.out.println("Get sorted page ranks");
		this.sortedPageRank = sortHashMapByValues(this.pageRank);
			
	}
	
	
	public double[] find_Pn_next(double[] Pn)
	{
		
		double[] Pn_next = new double[this.N];
		
		for(int i=0; i<this.N; i++)
		{
			Pn_next[i] = (1-this.beta)/this.N;
		}
		
		for(String p : this.nodeList)
		{
			ArrayList<String> Q = this.graph.getOutLinks_P(p);
			int p_index = this.nodeIndexMap.get(p);
			
			if(Q.size() != 0)
			{	
				for(String q : Q)
				{
					int q_index = this.nodeIndexMap.get(q);
					Pn_next[q_index] += (double)(this.beta * (Pn[p_index])/ Q.size());
				}
			}
			
			if(Q.size() == 0)
			{	
				for(String q : this.nodeList)
				{
					int q_index = this.nodeIndexMap.get(q);
					Pn_next[q_index] += (double)(this.beta * (Pn[p_index])/ this.N);
				}
			}
			
		}
		
		return Pn_next;
	}
	
	public void calculatePageRank()
	{
		double[] P0 = new double[this.N];
		
		for(int i=0; i<this.N; i++)
		{
			P0[i] = (double)1/this.N;
		}
		
		
		double[] Pn = P0;
		double[] Pn_next;
		
		boolean converged = false;
		
		while (!converged)
		{
			Pn_next = find_Pn_next(Pn);
			System.out.println("Pn_next:" + Pn_next);
			System.out.println("Norm:" + Norm(Pn_next,Pn));
			if(Norm(Pn_next,Pn) <= this.apprx_parameter)
			{
				converged = true;
			}
			Pn = Pn_next;
			this.iterations++;
		}
		
		for(String s : this.nodeList)
		{
			int index = this.nodeIndexMap.get(s);
			this.pageRank.put(s, Pn[index]);			
		}
		
		
	}
	
	public double Norm(double[] m1, double[] m2)
	{
		double abs_sum = 0;
		for(int i =0; i<this.N; i++)
		{
			abs_sum += Math.abs(m1[i]-m2[i]);
		}
		
		return abs_sum;
	}
	
	
	public double pageRankOf(String n)
	{		
		return this.pageRank.get(n);
	}
	
	public int outDegreeOf(String n)
	{
		return (int)this.sortedNodeOutDegreeMap.get(n);
	}
	
	public int inDegreeOf(String n)
	{
		return (int)this.sortedNodeInDegreeMap.get(n);
	}
	
	public int numEdges()
	{
		return this.graph.getNumEdges();
	}
	
	public int getIterations()
	{
		return this.iterations;
	}
	
	public String[] topKPageRank(int k)
	{
		String[] topK = new String[k];
		int i = 0;
		
		for (Map.Entry<String, Double> entry : this.sortedPageRank.entrySet()) {
			topK[i] = entry.getKey();
			i++;
			if(i==k)
				break;
			
		}		
		return topK;
	}
	
	public String[] topKOutDegree(int k)
	{
		String[] topKOut = new String[k];
		int i = 0;
		for (Map.Entry<String, Integer> entry : this.sortedNodeOutDegreeMap.entrySet())
		{
			topKOut[i] = entry.getKey();
			i++;
			if(i==k)
				break;
		}
		
		return topKOut;
	}
	
	public String[] topKInDegree(int k)
	{
		String[] topKIn = new String[k];
		int i = 0;
		for (Map.Entry<String, Integer> entry : this.sortedNodeInDegreeMap.entrySet())
		{
			//System.out.println(entry.getKey());
			topKIn[i] = entry.getKey();
			i++;
			if(i==k)
				break;
		}
		
		return topKIn;
	}
	
	
	
	public LinkedHashMap<String, Double> sortHashMapByValues(Map<String, Double> unsortedMap) 
	{
		   ArrayList keys = new ArrayList(unsortedMap.keySet());
		   ArrayList values = new ArrayList(unsortedMap.values());
		   Collections.sort(values, Collections.reverseOrder());
		   Collections.sort(keys);

		   LinkedHashMap<String, Double> sortedMap = new LinkedHashMap<String, Double>();
		   
		   Iterator valuei = values.iterator();
		   
		   while (valuei.hasNext()) 
		   {
		       Object val = valuei.next();
		       Iterator keyi = keys.iterator();

		       while (keyi.hasNext()) 
		       {
		           Object key = keyi.next();
		           String comp1 = unsortedMap.get(key).toString();
		           String comp2 = val.toString();

		           if (comp1.equals(comp2))
		           {
		               unsortedMap.remove(key);
		               keys.remove(key);
		               sortedMap.put((String)key, (Double)val);
		               break;
		           }

		       }

		   }
		   return sortedMap;
	}
}
