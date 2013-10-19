import java.io.*;
import java.net.*;
import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

/**
 * Program demonstruj¹cy wykorzystanie parsera SAX.
 * Wyœwietla wszystkie hiper³¹cza umiesczone na stronie XHTML.
 * Uruchomienie programu: java SAXTest url
 * @version 1.00 2001-09-29
 * @author Cay Horstmann
 */
public class SAXTest
{
   public static void main(String[] args) throws Exception
   {
      String url;
      if (args.length == 0)
      {
         url = "http://www.w3c.org";
         System.out.println("Using " + url);
      }
      else url = args[0];

      DefaultHandler handler = new DefaultHandler()
         {
            public void startElement(String namespaceURI, String lname, String qname,
                  Attributes attrs)
            {
               if (lname.equals("a") && attrs != null)
               {
                  for (int i = 0; i < attrs.getLength(); i++)
                  {
                     String aname = attrs.getLocalName(i);
                     if (aname.equals("href")) System.out.println(attrs.getValue(i));
                  }
               }
            }
         };

      SAXParserFactory factory = SAXParserFactory.newInstance();
      factory.setNamespaceAware(true);
      SAXParser saxParser = factory.newSAXParser();
      InputStream in = new URL(url).openStream();
      saxParser.parse(in, handler);
   }
}
