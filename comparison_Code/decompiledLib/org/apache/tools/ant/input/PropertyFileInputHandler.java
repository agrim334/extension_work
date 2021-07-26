package org.apache.tools.ant.input;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.util.Properties;
import org.apache.tools.ant.BuildException;

public class PropertyFileInputHandler
  implements InputHandler
{
  private Properties props = null;
  public static final String FILE_NAME_KEY = "ant.input.properties";
  
  public void handleInput(InputRequest request)
    throws BuildException
  {
    readProps();
    
    Object o = props.get(request.getPrompt());
    if (o == null) {
      throw new BuildException("Unable to find input for '" + request.getPrompt() + "'");
    }
    request.setInput(o.toString());
    if (!request.isInputValid()) {
      throw new BuildException("Found invalid input " + o + " for '" + request.getPrompt() + "'");
    }
  }
  
  private synchronized void readProps()
    throws BuildException
  {
    if (props == null)
    {
      String propsFile = System.getProperty("ant.input.properties");
      if (propsFile == null) {
        throw new BuildException("System property ant.input.properties for PropertyFileInputHandler not set");
      }
      props = new Properties();
      try
      {
        props.load(Files.newInputStream(Paths.get(propsFile, new String[0]), new OpenOption[0]));
      }
      catch (IOException e)
      {
        throw new BuildException("Couldn't load " + propsFile, e);
      }
    }
  }
}

/* Location:
 * Qualified Name:     org.apache.tools.ant.input.PropertyFileInputHandler
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */