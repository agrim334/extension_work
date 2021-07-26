package org.apache.tools.ant.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public class UUEncoder
{
  protected static final int DEFAULT_MODE = 644;
  private static final int MAX_CHARS_PER_LINE = 45;
  private static final int INPUT_BUFFER_SIZE = 4500;
  private OutputStream out;
  private String name;
  
  public UUEncoder(String name)
  {
    this.name = name;
  }
  
  public void encode(InputStream is, OutputStream out)
    throws IOException
  {
    this.out = out;
    encodeBegin();
    byte[] buffer = new byte['ᆔ'];
    int count;
    while ((count = is.read(buffer, 0, buffer.length)) != -1)
    {
      int pos = 0;
      while (count > 0)
      {
        int num = count > 45 ? 45 : count;
        encodeLine(buffer, pos, num, out);
        pos += num;
        count -= num;
      }
    }
    out.flush();
    encodeEnd();
  }
  
  private void encodeString(String n)
  {
    PrintStream writer = new PrintStream(out);
    writer.print(n);
    writer.flush();
  }
  
  private void encodeBegin()
  {
    encodeString("begin 644 " + name + "\n");
  }
  
  private void encodeEnd()
  {
    encodeString(" \nend\n");
  }
  
  private void encodeLine(byte[] data, int offset, int length, OutputStream out)
    throws IOException
  {
    out.write((byte)((length & 0x3F) + 32));
    for (int i = 0; i < length;)
    {
      byte b = 1;
      byte c = 1;
      
      byte a = data[(offset + i++)];
      if (i < length)
      {
        b = data[(offset + i++)];
        if (i < length) {
          c = data[(offset + i++)];
        }
      }
      byte d1 = (byte)((a >>> 2 & 0x3F) + 32);
      byte d2 = (byte)((a << 4 & 0x30 | b >>> 4 & 0xF) + 32);
      byte d3 = (byte)((b << 2 & 0x3C | c >>> 6 & 0x3) + 32);
      byte d4 = (byte)((c & 0x3F) + 32);
      
      out.write(d1);
      out.write(d2);
      out.write(d3);
      out.write(d4);
    }
    out.write(10);
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.UUEncoder
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */