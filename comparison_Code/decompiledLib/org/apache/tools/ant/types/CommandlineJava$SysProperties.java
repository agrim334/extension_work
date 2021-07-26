package org.apache.tools.ant.types;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;
import java.util.Vector;
import org.apache.tools.ant.BuildException;

public class CommandlineJava$SysProperties
  extends Environment
  implements Cloneable
{
  Properties sys = null;
  private Vector<PropertySet> propertySets = new Vector();
  
  public String[] getVariables()
    throws BuildException
  {
    List<String> definitions = new LinkedList();
    addDefinitionsToList(definitions.listIterator());
    if (definitions.isEmpty()) {
      return null;
    }
    return (String[])definitions.toArray(new String[definitions.size()]);
  }
  
  public void addDefinitionsToList(ListIterator<String> listIt)
  {
    String[] props = super.getVariables();
    if (props != null) {
      for (String prop : props) {
        listIt.add("-D" + prop);
      }
    }
    Properties propertySetProperties = mergePropertySets();
    for (String key : propertySetProperties.stringPropertyNames()) {
      listIt.add("-D" + key + "=" + propertySetProperties.getProperty(key));
    }
  }
  
  public int size()
  {
    Properties p = mergePropertySets();
    return variables.size() + p.size();
  }
  
  public void setSystem()
    throws BuildException
  {
    try
    {
      sys = System.getProperties();
      Properties p = new Properties();
      for (String name : sys.stringPropertyNames())
      {
        String value = sys.getProperty(name);
        if (value != null) {
          p.put(name, value);
        }
      }
      p.putAll(mergePropertySets());
      for (Environment.Variable v : variables)
      {
        v.validate();
        p.put(v.getKey(), v.getValue());
      }
      System.setProperties(p);
    }
    catch (SecurityException e)
    {
      throw new BuildException("Cannot modify system properties", e);
    }
  }
  
  public void restoreSystem()
    throws BuildException
  {
    if (sys == null) {
      throw new BuildException("Unbalanced nesting of SysProperties");
    }
    try
    {
      System.setProperties(sys);
      sys = null;
    }
    catch (SecurityException e)
    {
      throw new BuildException("Cannot modify system properties", e);
    }
  }
  
  public Object clone()
    throws CloneNotSupportedException
  {
    try
    {
      SysProperties c = (SysProperties)super.clone();
      variables = ((Vector)variables.clone());
      propertySets = ((Vector)propertySets.clone());
      return c;
    }
    catch (CloneNotSupportedException e) {}
    return null;
  }
  
  public void addSyspropertyset(PropertySet ps)
  {
    propertySets.addElement(ps);
  }
  
  public void addSysproperties(SysProperties ps)
  {
    variables.addAll(variables);
    propertySets.addAll(propertySets);
  }
  
  private Properties mergePropertySets()
  {
    Properties p = new Properties();
    for (PropertySet ps : propertySets) {
      p.putAll(ps.getProperties());
    }
    return p;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.types.CommandlineJava.SysProperties
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */