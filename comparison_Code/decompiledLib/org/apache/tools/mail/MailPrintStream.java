package org.apache.tools.mail;

import java.io.OutputStream;
import java.io.PrintStream;

class MailPrintStream
  extends PrintStream
{
  private int lastChar;
  
  public MailPrintStream(OutputStream out)
  {
    super(out, true);
  }
  
  public void write(int b)
  {
    if ((b == 10) && (lastChar != 13))
    {
      rawWrite(13);
      rawWrite(b);
    }
    else if ((b == 46) && (lastChar == 10))
    {
      rawWrite(46);
      rawWrite(b);
    }
    else
    {
      rawWrite(b);
    }
    lastChar = b;
  }
  
  public void write(byte[] buf, int off, int len)
  {
    for (int i = 0; i < len; i++) {
      write(buf[(off + i)]);
    }
  }
  
  void rawWrite(int b)
  {
    super.write(b);
  }
  
  void rawPrint(String s)
  {
    for (char ch : s.toCharArray()) {
      rawWrite(ch);
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.mail.MailPrintStream
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */