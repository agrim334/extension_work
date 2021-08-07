package org.codehaus.plexus.util.dag;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Vertex
  implements Cloneable, Serializable
{
  private String label = null;
  List<Vertex> children = new ArrayList();
  List<Vertex> parents = new ArrayList();
  
  public Vertex(String label)
  {
    this.label = label;
  }
  
  public String getLabel()
  {
    return label;
  }
  
  public void addEdgeTo(Vertex vertex)
  {
    children.add(vertex);
  }
  
  public void removeEdgeTo(Vertex vertex)
  {
    children.remove(vertex);
  }
  
  public void addEdgeFrom(Vertex vertex)
  {
    parents.add(vertex);
  }
  
  public void removeEdgeFrom(Vertex vertex)
  {
    parents.remove(vertex);
  }
  
  public List<Vertex> getChildren()
  {
    return children;
  }
  
  public List<String> getChildLabels()
  {
    List<String> retValue = new ArrayList(children.size());
    for (Vertex vertex : children) {
      retValue.add(vertex.getLabel());
    }
    return retValue;
  }
  
  public List<Vertex> getParents()
  {
    return parents;
  }
  
  public List<String> getParentLabels()
  {
    List<String> retValue = new ArrayList(parents.size());
    for (Vertex vertex : parents) {
      retValue.add(vertex.getLabel());
    }
    return retValue;
  }
  
  public boolean isLeaf()
  {
    return children.size() == 0;
  }
  
  public boolean isRoot()
  {
    return parents.size() == 0;
  }
  
  public boolean isConnected()
  {
    return (isRoot()) || (isLeaf());
  }
  
  public Object clone()
    throws CloneNotSupportedException
  {
    Object retValue = super.clone();
    
    return retValue;
  }
  
  public String toString()
  {
    return "Vertex{label='" + label + "'" + "}";
  }
}

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.dag.Vertex
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */