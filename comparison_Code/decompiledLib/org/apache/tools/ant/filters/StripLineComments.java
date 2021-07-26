package org.apache.tools.ant.filters;

import java.io.IOException;
import java.io.Reader;
import java.util.Vector;
import org.apache.tools.ant.types.Parameter;

public final class StripLineComments
  extends BaseParamFilterReader
  implements ChainableReader
{
  private static final String COMMENTS_KEY = "comment";
  private Vector<String> comments = new Vector();
  private String line = null;
  
  public StripLineComments() {}
  
  public StripLineComments(Reader in)
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
      line = readLine();
      int commentsSize = comments.size();
      while (line != null)
      {
        for (int i = 0; i < commentsSize; i++)
        {
          String comment = (String)comments.elementAt(i);
          if (line.startsWith(comment))
          {
            line = null;
            break;
          }
        }
        if (line != null) {
          break;
        }
        line = readLine();
      }
      if (line != null) {
        return read();
      }
    }
    return ch;
  }
  
  public void addConfiguredComment(Comment comment)
  {
    comments.addElement(comment.getValue());
  }
  
  private void setComments(Vector<String> comments)
  {
    this.comments = comments;
  }
  
  private Vector<String> getComments()
  {
    return comments;
  }
  
  public Reader chain(Reader rdr)
  {
    StripLineComments newFilter = new StripLineComments(rdr);
    newFilter.setComments(getComments());
    newFilter.setInitialized(true);
    return newFilter;
  }
  
  private void initialize()
  {
    Parameter[] params = getParameters();
    if (params != null) {
      for (Parameter param : params) {
        if ("comment".equals(param.getType())) {
          comments.addElement(param.getValue());
        }
      }
    }
  }
  
  public static class Comment
  {
    private String value;
    
    public final void setValue(String comment)
    {
      if (value != null) {
        throw new IllegalStateException("Comment value already set.");
      }
      value = comment;
    }
    
    public final String getValue()
    {
      return value;
    }
    
    public void addText(String comment)
    {
      setValue(comment);
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.filters.StripLineComments
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */