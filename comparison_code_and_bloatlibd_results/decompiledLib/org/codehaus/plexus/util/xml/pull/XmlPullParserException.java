package org.codehaus.plexus.util.xml.pull;

import java.io.PrintStream;

public class XmlPullParserException
  extends Exception
{
  /**
   * @deprecated
   */
  protected Throwable detail;
  protected int row = -1;
  protected int column = -1;
  
  public XmlPullParserException(String s)
  {
    super(s);
  }
  
  public XmlPullParserException(String msg, XmlPullParser parser, Throwable chain)
  {
    super((msg == null ? "" : new StringBuilder().append(msg).append(" ").toString()) + (parser == null ? "" : new StringBuilder().append("(position:").append(parser.getPositionDescription()).append(") ").toString()) + (chain == null ? "" : new StringBuilder().append("caused by: ").append(chain).toString()), chain);
    if (parser != null)
    {
      row = parser.getLineNumber();
      column = parser.getColumnNumber();
    }
    detail = chain;
  }
  
  /**
   * @deprecated
   */
  public Throwable getDetail()
  {
    return getCause();
  }
  
  public int getLineNumber()
  {
    return row;
  }
  
  public int getColumnNumber()
  {
    return column;
  }
  
  public void printStackTrace()
  {
    if (getCause() == null) {
      super.printStackTrace();
    } else {
      synchronized (System.err)
      {
        System.err.println(super.getMessage() + "; nested exception is:");
        getCause().printStackTrace();
      }
    }
  }
}

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.xml.pull.XmlPullParserException
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */