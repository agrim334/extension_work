package org.apache.tools.ant.taskdefs.cvslib;

import java.io.ByteArrayOutputStream;
import java.util.StringTokenizer;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.AbstractCvsTask;

public class CvsVersion
  extends AbstractCvsTask
{
  static final long VERSION_1_11_2 = 11102L;
  static final long MULTIPLY = 100L;
  private String clientVersion;
  private String serverVersion;
  private String clientVersionProperty;
  private String serverVersionProperty;
  
  public String getClientVersion()
  {
    return clientVersion;
  }
  
  public String getServerVersion()
  {
    return serverVersion;
  }
  
  public void setClientVersionProperty(String clientVersionProperty)
  {
    this.clientVersionProperty = clientVersionProperty;
  }
  
  public void setServerVersionProperty(String serverVersionProperty)
  {
    this.serverVersionProperty = serverVersionProperty;
  }
  
  public boolean supportsCvsLogWithSOption()
  {
    if (serverVersion == null) {
      return false;
    }
    StringTokenizer tokenizer = new StringTokenizer(serverVersion, ".");
    long counter = 10000L;
    long version = 0L;
    while (tokenizer.hasMoreTokens())
    {
      String s = tokenizer.nextToken();
      for (int i = 0; i < s.length(); i++) {
        if (!Character.isDigit(s.charAt(i))) {
          break;
        }
      }
      String s2 = s.substring(0, i);
      version += counter * Long.parseLong(s2);
      if (counter == 1L) {
        break;
      }
      counter /= 100L;
    }
    return version >= 11102L;
  }
  
  public void execute()
  {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    setOutputStream(bos);
    ByteArrayOutputStream berr = new ByteArrayOutputStream();
    setErrorStream(berr);
    setCommand("version");
    super.execute();
    String output = bos.toString();
    log("Received version response \"" + output + "\"", 4);
    
    StringTokenizer st = new StringTokenizer(output);
    boolean client = false;
    boolean server = false;
    String cvs = null;
    String cachedVersion = null;
    boolean haveReadAhead = false;
    while ((haveReadAhead) || (st.hasMoreTokens()))
    {
      String currentToken = haveReadAhead ? cachedVersion : st.nextToken();
      haveReadAhead = false;
      if ("Client:".equals(currentToken)) {
        client = true;
      } else if ("Server:".equals(currentToken)) {
        server = true;
      } else if ((currentToken.startsWith("(CVS")) && 
        (currentToken.endsWith(")"))) {
        cvs = " " + currentToken;
      }
      if ((!client) && (!server) && (cvs != null) && (cachedVersion == null) && 
        (st.hasMoreTokens()))
      {
        cachedVersion = st.nextToken();
        haveReadAhead = true;
      }
      else if ((client) && (cvs != null))
      {
        if (st.hasMoreTokens()) {
          clientVersion = (st.nextToken() + cvs);
        }
        client = false;
        cvs = null;
      }
      else if ((server) && (cvs != null))
      {
        if (st.hasMoreTokens()) {
          serverVersion = (st.nextToken() + cvs);
        }
        server = false;
        cvs = null;
      }
      else if (("(client/server)".equals(currentToken)) && (cvs != null) && (cachedVersion != null) && (!client) && (!server))
      {
        client = server = 1;
        clientVersion = (serverVersion = cachedVersion + cvs);
        cachedVersion = cvs = null;
      }
    }
    if (clientVersionProperty != null) {
      getProject().setNewProperty(clientVersionProperty, clientVersion);
    }
    if (serverVersionProperty != null) {
      getProject().setNewProperty(serverVersionProperty, serverVersion);
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.cvslib.CvsVersion
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */