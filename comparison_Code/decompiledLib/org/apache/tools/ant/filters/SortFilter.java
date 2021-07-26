package org.apache.tools.ant.filters;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Parameter;

public final class SortFilter
  extends BaseParamFilterReader
  implements ChainableReader
{
  private static final String REVERSE_KEY = "reverse";
  private static final String COMPARATOR_KEY = "comparator";
  private Comparator<? super String> comparator = null;
  private boolean reverse;
  private List<String> lines;
  private String line = null;
  private Iterator<String> iterator = null;
  
  public SortFilter() {}
  
  public SortFilter(Reader in)
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
      if (lines == null)
      {
        lines = new ArrayList();
        for (line = readLine(); line != null; line = readLine()) {
          lines.add(line);
        }
        sort();
        iterator = lines.iterator();
      }
      if (iterator.hasNext())
      {
        line = ((String)iterator.next());
      }
      else
      {
        line = null;
        lines = null;
        iterator = null;
      }
      if (line != null) {
        return read();
      }
    }
    return ch;
  }
  
  public Reader chain(Reader rdr)
  {
    SortFilter newFilter = new SortFilter(rdr);
    newFilter.setReverse(isReverse());
    newFilter.setComparator(getComparator());
    newFilter.setInitialized(true);
    return newFilter;
  }
  
  public boolean isReverse()
  {
    return reverse;
  }
  
  public void setReverse(boolean reverse)
  {
    this.reverse = reverse;
  }
  
  public Comparator<? super String> getComparator()
  {
    return comparator;
  }
  
  public void setComparator(Comparator<? super String> comparator)
  {
    this.comparator = comparator;
  }
  
  public void add(Comparator<? super String> comparator)
  {
    if ((this.comparator != null) && (comparator != null)) {
      throw new BuildException("can't have more than one comparator");
    }
    setComparator(comparator);
  }
  
  private void initialize()
  {
    Parameter[] params = getParameters();
    if (params != null) {
      for (Parameter param : params)
      {
        String paramName = param.getName();
        if ("reverse".equals(paramName)) {
          setReverse(Boolean.valueOf(param.getValue()).booleanValue());
        } else if ("comparator".equals(paramName)) {
          try
          {
            String className = param.getValue();
            
            Comparator<? super String> comparatorInstance = (Comparator)Class.forName(className).newInstance();
            setComparator(comparatorInstance);
          }
          catch (InstantiationException|ClassNotFoundException|IllegalAccessException e)
          {
            throw new BuildException(e);
          }
          catch (ClassCastException e)
          {
            throw new BuildException("Value of comparator attribute should implement java.util.Comparator interface");
          }
          catch (Exception e)
          {
            throw new BuildException(e);
          }
        }
      }
    }
  }
  
  private void sort()
  {
    if (comparator == null)
    {
      if (isReverse()) {
        lines.sort(Comparator.reverseOrder());
      } else {
        Collections.sort(lines);
      }
    }
    else {
      lines.sort(comparator);
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.filters.SortFilter
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */