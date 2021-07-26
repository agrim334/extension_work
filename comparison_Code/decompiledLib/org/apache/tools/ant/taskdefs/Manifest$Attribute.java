package org.apache.tools.ant.taskdefs;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Objects;
import java.util.Vector;

public class Manifest$Attribute
{
  private static final int MAX_NAME_VALUE_LENGTH = 68;
  private static final int MAX_NAME_LENGTH = 70;
  private String name = null;
  private Vector<String> values = new Vector();
  private int currentIndex = 0;
  
  public Manifest$Attribute() {}
  
  public Manifest$Attribute(String line)
    throws ManifestException
  {
    parse(line);
  }
  
  public Manifest$Attribute(String name, String value)
  {
    this.name = name;
    setValue(value);
  }
  
  public int hashCode()
  {
    return Objects.hash(new Object[] { getKey(), values });
  }
  
  public boolean equals(Object rhs)
  {
    if ((rhs == null) || (rhs.getClass() != getClass())) {
      return false;
    }
    if (rhs == this) {
      return true;
    }
    Attribute rhsAttribute = (Attribute)rhs;
    String lhsKey = getKey();
    String rhsKey = rhsAttribute.getKey();
    return ((lhsKey != null) || (rhsKey == null)) && ((lhsKey == null) || 
      (lhsKey.equals(rhsKey))) && (values.equals(values));
  }
  
  public void parse(String line)
    throws ManifestException
  {
    int index = line.indexOf(": ");
    if (index == -1) {
      throw new ManifestException("Manifest line \"" + line + "\" is not valid as it does not contain a name and a value separated by ': '");
    }
    name = line.substring(0, index);
    setValue(line.substring(index + 2));
  }
  
  public void setName(String name)
  {
    this.name = name;
  }
  
  public String getName()
  {
    return name;
  }
  
  public String getKey()
  {
    return name == null ? null : name.toLowerCase(Locale.ENGLISH);
  }
  
  public void setValue(String value)
  {
    if (currentIndex >= values.size())
    {
      values.addElement(value);
      currentIndex = (values.size() - 1);
    }
    else
    {
      values.setElementAt(value, currentIndex);
    }
  }
  
  public String getValue()
  {
    return values.isEmpty() ? null : 
      String.join(" ", values);
  }
  
  public void addValue(String value)
  {
    currentIndex += 1;
    setValue(value);
  }
  
  public Enumeration<String> getValues()
  {
    return values.elements();
  }
  
  public void addContinuation(String line)
  {
    setValue((String)values.elementAt(currentIndex) + line.substring(1));
  }
  
  public void write(PrintWriter writer)
    throws IOException
  {
    write(writer, false);
  }
  
  public void write(PrintWriter writer, boolean flatten)
    throws IOException
  {
    if (flatten) {
      writeValue(writer, getValue());
    } else {
      for (String value : values) {
        writeValue(writer, value);
      }
    }
  }
  
  private void writeValue(PrintWriter writer, String value)
    throws IOException
  {
    int nameLength = name.getBytes(Manifest.JAR_CHARSET).length;
    String line;
    String line;
    if (nameLength > 68)
    {
      if (nameLength > 70) {
        throw new IOException("Unable to write manifest line " + name + ": " + value);
      }
      writer.print(name + ": " + "\r\n");
      line = " " + value;
    }
    else
    {
      line = name + ": " + value;
    }
    while (line.getBytes(Manifest.JAR_CHARSET).length > 70)
    {
      int breakIndex = 70;
      if (breakIndex >= line.length()) {
        breakIndex = line.length() - 1;
      }
      String section = line.substring(0, breakIndex);
      while ((section.getBytes(Manifest.JAR_CHARSET).length > 70) && (breakIndex > 0))
      {
        breakIndex--;
        section = line.substring(0, breakIndex);
      }
      if (breakIndex == 0) {
        throw new IOException("Unable to write manifest line " + name + ": " + value);
      }
      writer.print(section + "\r\n");
      line = " " + line.substring(breakIndex);
    }
    writer.print(line + "\r\n");
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Manifest.Attribute
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */