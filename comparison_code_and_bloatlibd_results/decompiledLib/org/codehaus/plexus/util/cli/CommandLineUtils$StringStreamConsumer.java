package org.codehaus.plexus.util.cli;

public class CommandLineUtils$StringStreamConsumer
  implements StreamConsumer
{
  private StringBuffer string = new StringBuffer();
  private String ls = System.getProperty("line.separator");
  
  public void consumeLine(String line)
  {
    string.append(line).append(ls);
  }
  
  public String getOutput()
  {
    return string.toString();
  }
}

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.cli.CommandLineUtils.StringStreamConsumer
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */