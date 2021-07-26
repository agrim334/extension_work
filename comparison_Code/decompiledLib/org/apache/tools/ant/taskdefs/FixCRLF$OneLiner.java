package org.apache.tools.ant.taskdefs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.filters.FixCrLfFilter;

@Deprecated
public class FixCRLF$OneLiner
  implements Enumeration<Object>
{
  private static final int UNDEF = -1;
  private static final int NOTJAVA = 0;
  private static final int LOOKING = 1;
  private static final int INBUFLEN = 8192;
  private static final int LINEBUFLEN = 200;
  private static final char CTRLZ = '\032';
  private int state = FixCRLF.access$000(this.this$0).getJavafiles() ? 1 : 0;
  private StringBuffer eolStr = new StringBuffer(200);
  private StringBuffer eofStr = new StringBuffer();
  private BufferedReader reader;
  private StringBuffer line = new StringBuffer();
  private boolean reachedEof = false;
  private File srcFile;
  
  public FixCRLF$OneLiner(FixCRLF this$0, File srcFile)
    throws BuildException
  {
    this.srcFile = srcFile;
    try
    {
      reader = new BufferedReader(FixCRLF.access$100(this$0) == null ? new FileReader(srcFile) : new InputStreamReader(Files.newInputStream(srcFile.toPath(), new OpenOption[0]), FixCRLF.access$100(this$0)), 8192);
      
      nextLine();
    }
    catch (IOException e)
    {
      throw new BuildException(srcFile + ": " + e.getMessage(), e, this$0.getLocation());
    }
  }
  
  protected void nextLine()
    throws BuildException
  {
    int ch = -1;
    int eolcount = 0;
    
    eolStr = new StringBuffer();
    line = new StringBuffer();
    try
    {
      ch = reader.read();
      while ((ch != -1) && (ch != 13) && (ch != 10))
      {
        line.append((char)ch);
        ch = reader.read();
      }
      if ((ch == -1) && (line.length() == 0))
      {
        reachedEof = true;
        return;
      }
      switch ((char)ch)
      {
      case '\r': 
        eolcount++;
        eolStr.append('\r');
        reader.mark(2);
        ch = reader.read();
        switch (ch)
        {
        case 13: 
          ch = reader.read();
          if ((char)ch == '\n')
          {
            eolcount += 2;
            eolStr.append("\r\n");
          }
          else
          {
            reader.reset();
          }
          break;
        case 10: 
          eolcount++;
          eolStr.append('\n');
          break;
        case -1: 
          break;
        default: 
          reader.reset();
        }
        break;
      case '\n': 
        eolcount++;
        eolStr.append('\n');
        break;
      }
      if (eolcount == 0)
      {
        int i = line.length();
        do
        {
          i--;
        } while ((i >= 0) && (line.charAt(i) == '\032'));
        if (i < line.length() - 1)
        {
          eofStr.append(line.toString().substring(i + 1));
          if (i < 0)
          {
            line.setLength(0);
            reachedEof = true;
          }
          else
          {
            line.setLength(i + 1);
          }
        }
      }
    }
    catch (IOException e)
    {
      throw new BuildException(srcFile + ": " + e.getMessage(), e, this$0.getLocation());
    }
  }
  
  public String getEofStr()
  {
    return eofStr.substring(0);
  }
  
  public int getState()
  {
    return state;
  }
  
  public void setState(int state)
  {
    this.state = state;
  }
  
  public boolean hasMoreElements()
  {
    return !reachedEof;
  }
  
  public Object nextElement()
    throws NoSuchElementException
  {
    if (!hasMoreElements()) {
      throw new NoSuchElementException("OneLiner");
    }
    BufferLine tmpLine = new BufferLine(line.toString(), eolStr.substring(0));
    nextLine();
    return tmpLine;
  }
  
  public void close()
    throws IOException
  {
    if (reader != null) {
      reader.close();
    }
  }
  
  class BufferLine
  {
    private int next = 0;
    private int column = 0;
    private int lookahead = -1;
    private String line;
    private String eolStr;
    
    public BufferLine(String line, String eolStr)
      throws BuildException
    {
      next = 0;
      column = 0;
      this.line = line;
      this.eolStr = eolStr;
    }
    
    public int getNext()
    {
      return next;
    }
    
    public void setNext(int next)
    {
      this.next = next;
    }
    
    public int getLookahead()
    {
      return lookahead;
    }
    
    public void setLookahead(int lookahead)
    {
      this.lookahead = lookahead;
    }
    
    public char getChar(int i)
    {
      return line.charAt(i);
    }
    
    public char getNextChar()
    {
      return getChar(next);
    }
    
    public char getNextCharInc()
    {
      return getChar(next++);
    }
    
    public int getColumn()
    {
      return column;
    }
    
    public void setColumn(int col)
    {
      column = col;
    }
    
    public int incColumn()
    {
      return column++;
    }
    
    public int length()
    {
      return line.length();
    }
    
    public int getEolLength()
    {
      return eolStr.length();
    }
    
    public String getLineString()
    {
      return line;
    }
    
    public String getEol()
    {
      return eolStr;
    }
    
    public String substring(int begin)
    {
      return line.substring(begin);
    }
    
    public String substring(int begin, int end)
    {
      return line.substring(begin, end);
    }
    
    public void setState(int state)
    {
      FixCRLF.OneLiner.this.setState(state);
    }
    
    public int getState()
    {
      return FixCRLF.OneLiner.this.getState();
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.FixCRLF.OneLiner
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */