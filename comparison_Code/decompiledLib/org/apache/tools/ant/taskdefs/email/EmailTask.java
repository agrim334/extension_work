package org.apache.tools.ant.taskdefs.email;

import java.io.File;
import java.util.StringTokenizer;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.util.ClasspathUtils;

public class EmailTask
  extends Task
{
  private static final int SMTP_PORT = 25;
  public static final String AUTO = "auto";
  public static final String MIME = "mime";
  public static final String UU = "uu";
  public static final String PLAIN = "plain";
  
  public static class Encoding
    extends EnumeratedAttribute
  {
    public String[] getValues()
    {
      return new String[] { "auto", "mime", "uu", "plain" };
    }
  }
  
  private String encoding = "auto";
  private String host = "localhost";
  private Integer port = null;
  private String subject = null;
  private Message message = null;
  private boolean failOnError = true;
  private boolean includeFileNames = false;
  private String messageMimeType = null;
  private String messageFileInputEncoding;
  private EmailAddress from = null;
  private Vector<EmailAddress> replyToList = new Vector();
  private Vector<EmailAddress> toList = new Vector();
  private Vector<EmailAddress> ccList = new Vector();
  private Vector<EmailAddress> bccList = new Vector();
  private Vector<Header> headers = new Vector();
  private Path attachments = null;
  private String charset = null;
  private String user = null;
  private String password = null;
  private boolean ssl = false;
  private boolean starttls = false;
  private boolean ignoreInvalidRecipients = false;
  
  public void setUser(String user)
  {
    this.user = user;
  }
  
  public void setPassword(String password)
  {
    this.password = password;
  }
  
  public void setSSL(boolean ssl)
  {
    this.ssl = ssl;
  }
  
  public void setEnableStartTLS(boolean b)
  {
    starttls = b;
  }
  
  public void setEncoding(Encoding encoding)
  {
    this.encoding = encoding.getValue();
  }
  
  public void setMailport(int port)
  {
    this.port = Integer.valueOf(port);
  }
  
  public void setMailhost(String host)
  {
    this.host = host;
  }
  
  public void setSubject(String subject)
  {
    this.subject = subject;
  }
  
  public void setMessage(String message)
  {
    if (this.message != null) {
      throw new BuildException("Only one message can be sent in an email");
    }
    this.message = new Message(message);
    this.message.setProject(getProject());
  }
  
  public void setMessageFile(File file)
  {
    if (message != null) {
      throw new BuildException("Only one message can be sent in an email");
    }
    message = new Message(file);
    message.setProject(getProject());
  }
  
  public void setMessageMimeType(String type)
  {
    messageMimeType = type;
  }
  
  public void addMessage(Message message)
    throws BuildException
  {
    if (this.message != null) {
      throw new BuildException("Only one message can be sent in an email");
    }
    this.message = message;
  }
  
  public void addFrom(EmailAddress address)
  {
    if (from != null) {
      throw new BuildException("Emails can only be from one address");
    }
    from = address;
  }
  
  public void setFrom(String address)
  {
    if (from != null) {
      throw new BuildException("Emails can only be from one address");
    }
    from = new EmailAddress(address);
  }
  
  public void addReplyTo(EmailAddress address)
  {
    replyToList.add(address);
  }
  
  public void setReplyTo(String address)
  {
    replyToList.add(new EmailAddress(address));
  }
  
  public void addTo(EmailAddress address)
  {
    toList.add(address);
  }
  
  public void setToList(String list)
  {
    StringTokenizer tokens = new StringTokenizer(list, ",");
    while (tokens.hasMoreTokens()) {
      toList.add(new EmailAddress(tokens.nextToken()));
    }
  }
  
  public void addCc(EmailAddress address)
  {
    ccList.add(address);
  }
  
  public void setCcList(String list)
  {
    StringTokenizer tokens = new StringTokenizer(list, ",");
    while (tokens.hasMoreTokens()) {
      ccList.add(new EmailAddress(tokens.nextToken()));
    }
  }
  
  public void addBcc(EmailAddress address)
  {
    bccList.add(address);
  }
  
  public void setBccList(String list)
  {
    StringTokenizer tokens = new StringTokenizer(list, ",");
    while (tokens.hasMoreTokens()) {
      bccList.add(new EmailAddress(tokens.nextToken()));
    }
  }
  
  public void setFailOnError(boolean failOnError)
  {
    this.failOnError = failOnError;
  }
  
  public void setFiles(String filenames)
  {
    StringTokenizer t = new StringTokenizer(filenames, ", ");
    while (t.hasMoreTokens()) {
      createAttachments().add(new FileResource(getProject().resolveFile(t.nextToken())));
    }
  }
  
  public void addFileset(FileSet fs)
  {
    createAttachments().add(fs);
  }
  
  public Path createAttachments()
  {
    if (attachments == null) {
      attachments = new Path(getProject());
    }
    return attachments.createPath();
  }
  
  public Header createHeader()
  {
    Header h = new Header();
    headers.add(h);
    return h;
  }
  
  public void setIncludefilenames(boolean includeFileNames)
  {
    this.includeFileNames = includeFileNames;
  }
  
  public boolean getIncludeFileNames()
  {
    return includeFileNames;
  }
  
  public void setIgnoreInvalidRecipients(boolean b)
  {
    ignoreInvalidRecipients = b;
  }
  
  public void execute()
  {
    Message savedMessage = message;
    try
    {
      Mailer mailer = null;
      
      boolean autoFound = false;
      if (("mime".equals(encoding)) || (
        ("auto".equals(encoding)) && (!autoFound))) {
        try
        {
          Class.forName("javax.activation.DataHandler");
          Class.forName("javax.mail.internet.MimeMessage");
          
          mailer = (Mailer)ClasspathUtils.newInstance("org.apache.tools.ant.taskdefs.email.MimeMailer", EmailTask.class
          
            .getClassLoader(), Mailer.class);
          autoFound = true;
          
          log("Using MIME mail", 3);
        }
        catch (BuildException e)
        {
          logBuildException("Failed to initialise MIME mail: ", e);
        }
      }
      if ((!autoFound) && ((user != null) || (password != null)) && (
        ("uu".equals(encoding)) || ("plain".equals(encoding)))) {
        throw new BuildException("SMTP auth only possible with MIME mail");
      }
      if ((!autoFound) && ((ssl) || (starttls)) && (
        ("uu".equals(encoding)) || ("plain".equals(encoding)))) {
        throw new BuildException("SSL and STARTTLS only possible with MIME mail");
      }
      if (("uu".equals(encoding)) || (
        ("auto".equals(encoding)) && (!autoFound))) {
        try
        {
          mailer = (Mailer)ClasspathUtils.newInstance("org.apache.tools.ant.taskdefs.email.UUMailer", EmailTask.class
          
            .getClassLoader(), Mailer.class);
          autoFound = true;
          log("Using UU mail", 3);
        }
        catch (BuildException e)
        {
          logBuildException("Failed to initialise UU mail: ", e);
        }
      }
      if (("plain".equals(encoding)) || (
        ("auto".equals(encoding)) && (!autoFound)))
      {
        mailer = new PlainMailer();
        autoFound = true;
        log("Using plain mail", 3);
      }
      if (mailer == null) {
        throw new BuildException("Failed to initialise encoding: %s", new Object[] { encoding });
      }
      if (message == null)
      {
        message = new Message();
        message.setProject(getProject());
      }
      if ((from == null) || (from.getAddress() == null)) {
        throw new BuildException("A from element is required");
      }
      if ((toList.isEmpty()) && (ccList.isEmpty()) && (bccList.isEmpty())) {
        throw new BuildException("At least one of to, cc or bcc must be supplied");
      }
      if (messageMimeType != null)
      {
        if (message.isMimeTypeSpecified()) {
          throw new BuildException("The mime type can only be specified in one location");
        }
        message.setMimeType(messageMimeType);
      }
      if (charset != null)
      {
        if (message.getCharset() != null) {
          throw new BuildException("The charset can only be specified in one location");
        }
        message.setCharset(charset);
      }
      message.setInputEncoding(messageFileInputEncoding);
      
      Vector<File> files = new Vector();
      if (attachments != null) {
        for (Resource r : attachments) {
          files.add(((FileProvider)r.as(FileProvider.class)).getFile());
        }
      }
      log("Sending email: " + subject, 2);
      log("From " + from, 3);
      log("ReplyTo " + replyToList, 3);
      log("To " + toList, 3);
      log("Cc " + ccList, 3);
      log("Bcc " + bccList, 3);
      
      mailer.setHost(host);
      if (port != null)
      {
        mailer.setPort(port.intValue());
        mailer.setPortExplicitlySpecified(true);
      }
      else
      {
        mailer.setPort(25);
        mailer.setPortExplicitlySpecified(false);
      }
      mailer.setUser(user);
      mailer.setPassword(password);
      mailer.setSSL(ssl);
      mailer.setEnableStartTLS(starttls);
      mailer.setMessage(message);
      mailer.setFrom(from);
      mailer.setReplyToList(replyToList);
      mailer.setToList(toList);
      mailer.setCcList(ccList);
      mailer.setBccList(bccList);
      mailer.setFiles(files);
      mailer.setSubject(subject);
      mailer.setTask(this);
      mailer.setIncludeFileNames(includeFileNames);
      mailer.setHeaders(headers);
      mailer.setIgnoreInvalidRecipients(ignoreInvalidRecipients);
      
      mailer.send();
      
      int count = files.size();
      
      log("Sent email with " + count + " attachment" + (
        count == 1 ? "" : "s"), 2);
    }
    catch (BuildException e)
    {
      logBuildException("Failed to send email: ", e);
      if (failOnError) {
        throw e;
      }
    }
    catch (Exception e)
    {
      log("Failed to send email: " + e.getMessage(), 1);
      if (failOnError) {
        throw new BuildException(e);
      }
    }
    finally
    {
      message = savedMessage;
    }
  }
  
  private void logBuildException(String reason, BuildException e)
  {
    Throwable t = e.getCause() == null ? e : e.getCause();
    log(reason + t.getMessage(), 1);
  }
  
  public void setCharset(String charset)
  {
    this.charset = charset;
  }
  
  public String getCharset()
  {
    return charset;
  }
  
  public void setMessageFileInputEncoding(String encoding)
  {
    messageFileInputEncoding = encoding;
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.taskdefs.email.EmailTask
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */