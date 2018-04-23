import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;


public class WebGraph 
{
	
	private String fileName;
	private int num_nodes;
	private int num_edges;
	private ArrayList[] nodesOutEdges_Map;
	private ArrayList[] nodesInEdges_Map;
	ArrayList<String> nodeList;
	ArrayList<String> edgeList;
    private LinkedHashMap<String, Integer> nodeIndexMap;
    private LinkedHashMap<String, Integer> nodeOutDegreeMap;
    private LinkedHashMap<String, Integer> nodeInDegreeMap;
    private LinkedHashMap<String, Integer> sortedNodeOutDegreeMap;
    private LinkedHashMap<String, Integer> sortedNodeInDegreeMap;
    
	private int nodes_addedOut;
	private int nodes_addedIn;
	
	public WebGraph(String fileName)
	{
		this.fileName = fileName;
		
		File inputFile = new File(this.fileName);
		try 
		{
			Scanner fileIn= new Scanner(inputFile);
			String line = "";
			if(fileIn.hasNextLine())
			{
				line = fileIn.nextLine();
				
				
				this.num_nodes = Integer.parseInt(line)+1;
			}
			
			this.nodeList = new ArrayList<String>();
			this.edgeList = new ArrayList<String>();
			String[] directEdge;
			
			while(fileIn.hasNextLine())
			{
				line = fileIn.nextLine();
				this.edgeList.add(line);
				this.num_edges++;
				directEdge = line.split("\\s+");
				addToUniqueNodes(directEdge[0]);
				addToUniqueNodes(directEdge[1]);
			}
			
			
			
			this.nodesOutEdges_Map = new ArrayList[this.num_nodes];
			this.nodesInEdges_Map = new ArrayList[this.num_nodes];
			int i =0;
			this.nodes_addedOut = 0;
			this.nodes_addedIn = 0;
			System.out.println(this.nodeList.size());
			for(String node: this.nodeList)
			{
				this.nodesOutEdges_Map[i] = new ArrayList();
				this.nodesInEdges_Map[i] = new ArrayList();
				this.nodesOutEdges_Map[i].add(node);
				this.nodesInEdges_Map[i].add(node);
				this.nodes_addedOut++;
				this.nodes_addedIn++;
				i++;
			}
			
			String src;
			String dest;
			String[] twoEnds;
						
			for(String edge: this.edgeList)
			{
				twoEnds = edge.split("\\s+");
				src = twoEnds[0];
				dest = twoEnds[1];
				addNodesInOutEdges_Map(src, dest);
			}
			
			
			nodeIndexMap = new LinkedHashMap<String, Integer>();
			int index = 0;
			for(String n : this.nodeList)
			{
				this.nodeIndexMap.put(n, index);
				index++;
			}
			
			
			this.nodeOutDegreeMap = new LinkedHashMap<String, Integer>(); 
			this.nodeInDegreeMap = new LinkedHashMap<String, Integer>(); 
			for(String n : this.nodeList)
			{
				this.nodeOutDegreeMap.put(n, getOutLinks_P(n).size()-1);
				this.nodeInDegreeMap.put(n, getInLinks_P(n).size()-1);
				
			}
			
			this.sortedNodeOutDegreeMap = sortHashMapByValues(this.nodeOutDegreeMap); 
			this.sortedNodeInDegreeMap = sortHashMapByValues(this.nodeInDegreeMap);
				
		} 
		catch (FileNotFoundException e) 
		{
		}
	}
	
	public void addToUniqueNodes(String s)
	{
		if(!this.nodeList.contains(s))
		{
			this.nodeList.add(s);
		}		
	}
	
	public void addNodesInOutEdges_Map(String s, String d)
	{
		boolean is_existOut = false;
		boolean is_existIn = false;
		for(int i=0; i<this.nodes_addedOut; i++)
		{
			if(this.nodesOutEdges_Map[i].get(0).equals(s))
			{
				this.nodesOutEdges_Map[i].add(d);
				is_existOut = true;
				break;
			}
			
		}
		
		for(int i=0; i<this.nodes_addedIn; i++)
		{
			if(this.nodesInEdges_Map[i].get(0).equals(d))
			{
				this.nodesInEdges_Map[i].add(s);
				is_existIn = true;
				break;
			}
			
		}
		
		if(!is_existOut && (this.nodes_addedOut<this.num_nodes))
		{
			this.nodesOutEdges_Map[this.nodes_addedOut].add(s);
			this.nodesOutEdges_Map[this.nodes_addedOut].add(d);
			this.nodes_addedOut++;
		}
		if(!is_existIn && (this.nodes_addedIn<this.num_nodes))
		{
			this.nodesInEdges_Map[this.nodes_addedIn].add(d);
			this.nodesInEdges_Map[this.nodes_addedIn].add(s);
			this.nodes_addedIn++;
		}	
	}
	
	public ArrayList getOutLinks_P(String p)
	{
		ArrayList linkedPages = new ArrayList();
		for(int i=0; i<this.num_nodes; i++)
		{
			if(this.nodesOutEdges_Map[i].get(0).equals(p))
			{
				linkedPages =  this.nodesOutEdges_Map[i];
				break;
			}
		}
		
		return linkedPages;		
	}
	
	public ArrayList getInLinks_P(String p)
	{
		ArrayList linkedPages = new ArrayList();
		for(int i=0; i<this.num_nodes; i++)
		{
			if(this.nodesInEdges_Map[i].get(0).equals(p))
				{
					linkedPages =  this.nodesInEdges_Map[i];
					break;
				}
			
				
		}
		
		return linkedPages;		
	}
	
	
	public LinkedHashMap<String, Integer> sortHashMapByValues(Map<String, Integer> unsortedMap) 
	{
		   ArrayList keys = new ArrayList(unsortedMap.keySet());
		   ArrayList values = new ArrayList(unsortedMap.values());
		   Collections.sort(values, Collections.reverseOrder());
		   Collections.sort(keys);

		   LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
		   
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
		               sortedMap.put((String)key, (Integer)val);
		               break;
		           }

		       }

		   }
		   return sortedMap;
		}
	
	public int getNumNodes()
	{
		return this.num_nodes;
	}
	
	public int getNumEdges()
	{
		return this.num_edges;
	}
	
	
	public ArrayList<String> getNodeList()
	{
		return this.nodeList;
	}
	
	public LinkedHashMap<String, Integer> getNodeIndexMap()
	{
		return this.nodeIndexMap;
	}
	
	
	public LinkedHashMap<String, Integer> getNodeOutDegreeMap()
	{
		return this.nodeOutDegreeMap;
	}
	
	public LinkedHashMap<String, Integer> getNodeInDegreeMap()
	{
		return this.nodeInDegreeMap;
	}
	
	public LinkedHashMap<String, Integer> sortedNodeOutDegreeMap()
	{
		return this.sortedNodeOutDegreeMap;
	}
	
	public LinkedHashMap<String, Integer> sortedNodeInDegreeMap()
	{
		return this.sortedNodeInDegreeMap;
	}

}
