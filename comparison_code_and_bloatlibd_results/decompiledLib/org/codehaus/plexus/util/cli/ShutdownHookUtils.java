package org.codehaus.plexus.util.cli;

import java.security.AccessControlException;

class ShutdownHookUtils
{
  public static void addShutDownHook(Thread hook)
  {
    try
    {
      Runtime.getRuntime().addShutdownHook(hook);
    }
    catch (IllegalStateException localIllegalStateException) {}catch (AccessControlException localAccessControlException) {}
  }
  
  public static void removeShutdownHook(Thread hook)
  {
    try
    {
      Runtime.getRuntime().removeShutdownHook(hook);
    }
    catch (IllegalStateException localIllegalStateException) {}catch (AccessControlException localAccessControlException) {}
  }
}

/* Location:
 * Qualified Name:     org.codehaus.plexus.util.cli.ShutdownHookUtils
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */