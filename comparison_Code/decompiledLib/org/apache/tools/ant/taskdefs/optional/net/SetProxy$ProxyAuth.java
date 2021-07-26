package org.apache.tools.ant.taskdefs.optional.net;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

final class SetProxy$ProxyAuth
  extends Authenticator
{
  private PasswordAuthentication auth;
  
  private SetProxy$ProxyAuth(String user, String pass)
  {
    auth = new PasswordAuthentication(user, pass.toCharArray());
  }
  
  protected PasswordAuthentication getPasswordAuthentication()
  {
    return auth;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.optional.net.SetProxy.ProxyAuth
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */