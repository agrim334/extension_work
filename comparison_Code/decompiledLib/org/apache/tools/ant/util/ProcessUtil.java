package org.apache.tools.ant.util;

import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

public class ProcessUtil
{
  public static String getProcessId(String fallback)
  {
    String jvmName = ManagementFactory.getRuntimeMXBean().getName();
    int index = jvmName.indexOf('@');
    if (index < 1) {
      return fallback;
    }
    try
    {
      return Long.toString(Long.parseLong(jvmName.substring(0, index)));
    }
    catch (NumberFormatException localNumberFormatException) {}
    return fallback;
  }
  
  public static void main(String[] args)
  {
    System.out.println(getProcessId("<PID>"));
    try
    {
      Thread.sleep(120000L);
    }
    catch (Exception localException) {}
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.util.ProcessUtil
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */