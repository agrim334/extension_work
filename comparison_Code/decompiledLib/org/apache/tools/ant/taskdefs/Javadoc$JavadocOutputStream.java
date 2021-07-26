package org.apache.tools.ant.taskdefs;

class Javadoc$JavadocOutputStream
  extends LogOutputStream
{
  Javadoc$JavadocOutputStream(Javadoc paramJavadoc, int level)
  {
    super(paramJavadoc, level);
  }
  
  private String queuedLine = null;
  private boolean sawWarnings = false;
  
  protected void processLine(String line, int messageLevel)
  {
    if (line.contains("warning")) {
      sawWarnings = true;
    }
    if ((messageLevel == 2) && 
      (line.startsWith("Generating ")))
    {
      if (queuedLine != null) {
        super.processLine(queuedLine, 3);
      }
      queuedLine = line;
    }
    else
    {
      if (queuedLine != null)
      {
        if (line.startsWith("Building ")) {
          super.processLine(queuedLine, 3);
        } else {
          super.processLine(queuedLine, 2);
        }
        queuedLine = null;
      }
      super.processLine(line, messageLevel);
    }
  }
  
  protected void logFlush()
  {
    if (queuedLine != null)
    {
      super.processLine(queuedLine, 3);
      queuedLine = null;
    }
  }
  
  public boolean sawWarnings()
  {
    return sawWarnings;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.Javadoc.JavadocOutputStream
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */