package org.codehaus.plexus.util.dag;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DAG
  implements Cloneable, Serializable
{
  private Map<String, Vertex> vertexMap = new HashMap();
  private List<Vertex> vertexList = new ArrayList();
  
  public List<Vertex> getVertices()
  {
    return vertexList;
  }
  
  @Deprecated
  public List<Vertex> getVerticies()
  {
    return getVertices();
  }
  
  public Set<String> getLabels()
  {
    return vertexMap.keySet();
  }
  
  public Vertex addVertex(String label)
  {
    Vertex retValue = null;
    if (vertexMap.containsKey(label))
    {
      retValue = (Vertex)vertexMap.get(label);
    }
    else
    {
      retValue = new Vertex(label);
      
      vertexMap.put(label, retValue);
      
      vertexList.add(retValue);
    }
    return retValue;
  }
  
  public void addEdge(String from, String to)
    throws CycleDetectedException
  {
    Vertex v1 = addVertex(from);
    
    Vertex v2 = addVertex(to);
    
    addEdge(v1, v2);
  }
  
  public void addEdge(Vertex from, Vertex to)
    throws CycleDetectedException
  {
    from.addEdgeTo(to);
    
    to.addEdgeFrom(from);
    
    List<String> cycle = CycleDetector.introducesCycle(to);
    if (cycle != null)
    {
      removeEdge(from, to);
      
      String msg = "Edge between '" + from + "' and '" + to + "' introduces to cycle in the graph";
      
      throw new CycleDetectedException(msg, cycle);
    }
  }
  
  public void removeEdge(String from, String to)
  {
    Vertex v1 = addVertex(from);
    
    Vertex v2 = addVertex(to);
    
    removeEdge(v1, v2);
  }
  
  public void removeEdge(Vertex from, Vertex to)
  {
    from.removeEdgeTo(to);
    
    to.removeEdgeFrom(from);
  }
  
  public Vertex getVertex(String label)
  {
    Vertex retValue = (Vertex)vertexMap.get(label);
    
    return retValue;
  }
  
  public boolean hasEdge(String label1, String label2)
  {
    Vertex v1 = getVertex(label1);
    
    Vertex v2 = getVertex(label2);
    
    boolean retValue = v1.getChildren().contains(v2);
    
    return retValue;
  }
  
  public List<String> getChildLabels(String label)
  {
    Vertex vertex = getVertex(label);
    
    return vertex.getChildLabels();
  }
  
  public List<String> getParentLabels(String label)
  {
    Vertex vertex = getVertex(label);
    
    return vertex.getParentLabels();
  }
  
  public Object clone()
    throws CloneNotSupportedException
  {
    Object retValue = super.clone();
    
    return retValue;
  }
  
  public boolean isConnected(String label)
  {
    Vertex vertex = getVertex(label);
    
    boolean retValue = vertex.isConnected();
    
    return retValue;
  }
  
  public List<String> getSuccessorLabels(String label)
  {
    Vertex vertex = getVertex(label);
    List<String> retValue;
    if (vertex.isLeaf())
    {
      List<String> retValue = new ArrayList(1);
      
      retValue.add(label);
    }
    else
    {
      retValue = TopologicalSorter.sort(vertex);
    }
    return retValue;
  }
}

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.dag.DAG
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */