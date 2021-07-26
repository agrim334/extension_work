package org.apache.tools.ant.filters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Parameter;

public final class ConcatFilter
  extends BaseParamFilterReader
  implements ChainableReader
{
  private File prepend;
  private File append;
  private Reader prependReader = null;
  private Reader appendReader = null;
  
  public ConcatFilter() {}
  
  public ConcatFilter(Reader in)
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
    if (prependReader != null)
    {
      ch = prependReader.read();
      if (ch == -1)
      {
        prependReader.close();
        prependReader = null;
      }
    }
    if (ch == -1) {
      ch = super.read();
    }
    if (ch == -1) {
      if (appendReader != null)
      {
        ch = appendReader.read();
        if (ch == -1)
        {
          appendReader.close();
          appendReader = null;
        }
      }
    }
    return ch;
  }
  
  public void setPrepend(File prepend)
  {
    this.prepend = prepend;
  }
  
  public File getPrepend()
  {
    return prepend;
  }
  
  public void setAppend(File append)
  {
    this.append = append;
  }
  
  public File getAppend()
  {
    return append;
  }
  
  public Reader chain(Reader rdr)
  {
    ConcatFilter newFilter = new ConcatFilter(rdr);
    newFilter.setPrepend(getPrepend());
    newFilter.setAppend(getAppend());
    
    return newFilter;
  }
  
  private void initialize()
    throws IOException
  {
    Parameter[] params = getParameters();
    if (params != null) {
      for (Parameter param : params)
      {
        String paramName = param.getName();
        if ("prepend".equals(paramName)) {
          setPrepend(new File(param.getValue()));
        } else if ("append".equals(paramName)) {
          setAppend(new File(param.getValue()));
        }
      }
    }
    if (prepend != null)
    {
      if (!prepend.isAbsolute()) {
        prepend = new File(getProject().getBaseDir(), prepend.getPath());
      }
      prependReader = new BufferedReader(new FileReader(prepend));
    }
    if (append != null)
    {
      if (!append.isAbsolute()) {
        append = new File(getProject().getBaseDir(), append.getPath());
      }
      appendReader = new BufferedReader(new FileReader(append));
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.filters.ConcatFilter
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */