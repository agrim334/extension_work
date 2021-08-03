package org.apache.maven.it;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

class Verifier$UserModelReader
  extends DefaultHandler
{
  private String localRepository;
  private StringBuffer currentBody = new StringBuffer();
  
  public void parse(File file)
    throws VerificationException
  {
    try
    {
      SAXParserFactory saxFactory = SAXParserFactory.newInstance();
      
      SAXParser parser = saxFactory.newSAXParser();
      
      InputSource is = new InputSource(new FileInputStream(file));
      
      parser.parse(is, this);
    }
    catch (FileNotFoundException e)
    {
      throw new VerificationException("file not found path : " + file.getAbsolutePath(), e);
    }
    catch (IOException e)
    {
      throw new VerificationException(" IOException path : " + file.getAbsolutePath(), e);
    }
    catch (ParserConfigurationException e)
    {
      throw new VerificationException(e);
    }
    catch (SAXException e)
    {
      throw new VerificationException("Parsing exception for file " + file.getAbsolutePath(), e);
    }
  }
  
  public void warning(SAXParseException spe)
  {
    printParseError("Warning", spe);
  }
  
  public void error(SAXParseException spe)
  {
    printParseError("Error", spe);
  }
  
  public void fatalError(SAXParseException spe)
  {
    printParseError("Fatal Error", spe);
  }
  
  private void printParseError(String type, SAXParseException spe)
  {
    System.err.println(type + " [line " + spe
      .getLineNumber() + ", row " + spe.getColumnNumber() + "]: " + spe.getMessage());
  }
  
  public String getLocalRepository()
  {
    return localRepository;
  }
  
  public void characters(char[] ch, int start, int length)
    throws SAXException
  {
    currentBody.append(ch, start, length);
  }
  
  public void endElement(String uri, String localName, String rawName)
    throws SAXException
  {
    if ("localRepository".equals(rawName)) {
      if (notEmpty(currentBody.toString())) {
        localRepository = currentBody.toString().trim();
      } else {
        throw new SAXException("Invalid mavenProfile entry. Missing one or more fields: {localRepository}.");
      }
    }
    currentBody = new StringBuffer();
  }
  
  private boolean notEmpty(String test)
  {
    return (test != null) && (test.trim().length() > 0);
  }
  
  public void reset()
  {
    currentBody = null;
    localRepository = null;
  }
}

/* Location:
 * Qualified Name:     org.apache.maven.it.Verifier.UserModelReader
 * Java Class Version: 7 (51.0)
 * JD-Core Version:    0.7.1
 */