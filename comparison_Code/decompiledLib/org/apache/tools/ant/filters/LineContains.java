package org.apache.tools.ant.filters;

import java.io.IOException;
import java.io.Reader;
import java.util.Vector;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Parameter;

public final class LineContains
  extends BaseParamFilterReader
  implements ChainableReader
{
  private static final String CONTAINS_KEY = "contains";
  private static final String NEGATE_KEY = "negate";
  private Vector<String> contains = new Vector();
  private String line = null;
  private boolean negate = false;
  private boolean matchAny = false;
  
  public LineContains() {}
  
  public LineContains(Reader in)
  {
    super(in);
  }
  
  public int read()
    throws IOException
  {
    if (!getInitialized())
    {
      initialize();
      setInitialized(true);
    }
    int ch = -1;
    if (line != null)
    {
      ch = line.charAt(0);
      if (line.length() == 1) {
        line = null;
      } else {
        line = line.substring(1);
      }
    }
    else
    {
      int containsSize = contains.size();
      for (line = readLine(); line != null; line = readLine())
      {
        boolean matches = true;
        for (int i = 0; i < containsSize; i++)
        {
          String containsStr = (String)contains.elementAt(i);
          matches = line.contains(containsStr);
          if (!matches) {
            if (!matchAny) {
              break;
            }
          } else {
            if (matchAny) {
              break;
            }
          }
        }
        if ((matches ^ isNegated())) {
          break;
        }
      }
      if (line != null) {
        return read();
      }
    }
    return ch;
  }
  
  public void addConfiguredContains(Contains contains)
  {
    this.contains.addElement(contains.getValue());
  }
  
  public void setNegate(boolean b)
  {
    negate = b;
  }
  
  public boolean isNegated()
  {
    return negate;
  }
  
  public void setMatchAny(boolean matchAny)
  {
    this.matchAny = matchAny;
  }
  
  public boolean isMatchAny()
  {
    return matchAny;
  }
  
  private void setContains(Vector<String> contains)
  {
    this.contains = contains;
  }
  
  private Vector<String> getContains()
  {
    return contains;
  }
  
  public Reader chain(Reader rdr)
  {
    LineContains newFilter = new LineContains(rdr);
    newFilter.setContains(getContains());
    newFilter.setNegate(isNegated());
    newFilter.setMatchAny(isMatchAny());
    return newFilter;
  }
  
  private void initialize()
  {
    Parameter[] params = getParameters();
    if (params != null) {
      for (Parameter param : params) {
        if ("contains".equals(param.getType())) {
          contains.addElement(param.getValue());
        } else if ("negate".equals(param.getType())) {
          setNegate(Project.toBoolean(param.getValue()));
        }
      }
    }
  }
  
  public static class Contains
  {
    private String value;
    
    public final void setValue(String contains)
    {
      value = contains;
    }
    
    public final String getValue()
    {
      return value;
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.filters.LineContains
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */