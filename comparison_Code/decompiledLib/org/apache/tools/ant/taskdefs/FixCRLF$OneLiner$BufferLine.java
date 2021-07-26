package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.BuildException;

class FixCRLF$OneLiner$BufferLine
{
  private int next = 0;
  private int column = 0;
  private int lookahead = -1;
  private String line;
  private String eolStr;
  
  public FixCRLF$OneLiner$BufferLine(FixCRLF.OneLiner this$1, String line, String eolStr)
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
    this$1.setState(state);
  }
  
  public int getState()
  {
    return this$1.getState();
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.FixCRLF.OneLiner.BufferLine
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */