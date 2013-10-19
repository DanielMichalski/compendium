import java.io.*;
import java.util.*;
import javax.xml.transform.*;
import javax.xml.transform.sax.*;
import javax.xml.transform.stream.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

/**
 * Program demonstruj¹cy wykorzystanie przekszta³ceñ XSL na przyk³adzie
 * zbioru informacji o pracownikach. Informacja ta umieszczona jest
 * w pliku tekstowym employee.dat i przekszta³cana na format XML. 
 * Uruchamiaj¹c program, nale¿y podaæ specfikacjê stylu, na przyk³ad:
 * java TransformTest makeprop.xsl
 */
public class TransformTest
{
   public static void main(String[] args) throws Exception
   {
      String filename;
      if (args.length > 0) filename = args[0];
      else filename = "makehtml.xsl";
      File styleSheet = new File(filename);
      StreamSource styleSource = new StreamSource(styleSheet);

      Transformer t = TransformerFactory.newInstance().newTransformer(styleSource);     
      t.setOutputProperty(OutputKeys.INDENT, "yes");
      t.setOutputProperty(OutputKeys.METHOD, "xml");      
      t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

      t.transform(new SAXSource(new EmployeeReader(), new InputSource(new FileInputStream(
            "employee.dat"))), new StreamResult(System.out));
   }
}

/**
 * Klasa odczytu pliku tekstowego employee.dat. Generuje
 * zdarzenia parsera SAX, jak podczas odczytu 
 * pliku w formacie XML.
 */
class EmployeeReader implements XMLReader
{
   public void parse(InputSource source) throws IOException, SAXException
   {
      InputStream stream = source.getByteStream();
      BufferedReader in = new BufferedReader(new InputStreamReader(stream));
      String rootElement = "staff";
      AttributesImpl atts = new AttributesImpl();

      if (handler == null) throw new SAXException("No content handler");

      handler.startDocument();
      handler.startElement("", rootElement, rootElement, atts);
      String line;
      while ((line = in.readLine()) != null)
      {
         handler.startElement("", "employee", "employee", atts);
         StringTokenizer t = new StringTokenizer(line, "|");

         handler.startElement("", "name", "name", atts);
         String s = t.nextToken();
         handler.characters(s.toCharArray(), 0, s.length());
         handler.endElement("", "name", "name");

         handler.startElement("", "salary", "salary", atts);
         s = t.nextToken();
         handler.characters(s.toCharArray(), 0, s.length());
         handler.endElement("", "salary", "salary");

         atts.addAttribute("", "year", "year", "CDATA", t.nextToken());
         atts.addAttribute("", "month", "month", "CDATA", t.nextToken());
         atts.addAttribute("", "day", "day", "CDATA", t.nextToken());
         handler.startElement("", "hiredate", "hiredate", atts);
         handler.endElement("", "hiredate", "hiredate");
         atts.clear();

         handler.endElement("", "employee", "employee");
      }

      handler.endElement("", rootElement, rootElement);
      handler.endDocument();
   }

   public void setContentHandler(ContentHandler newValue)
   {
      handler = newValue;
   }

   public ContentHandler getContentHandler()
   {
      return handler;
   }

   // poni¿sze metody maj¹ "pust¹" implementacjê
   public void parse(String systemId) throws IOException, SAXException
   {
   }

   public void setErrorHandler(ErrorHandler handler)
   {
   }

   public ErrorHandler getErrorHandler()
   {
      return null;
   }

   public void setDTDHandler(DTDHandler handler)
   {
   }

   public DTDHandler getDTDHandler()
   {
      return null;
   }

   public void setEntityResolver(EntityResolver resolver)
   {
   }

   public EntityResolver getEntityResolver()
   {
      return null;
   }

   public void setProperty(String name, Object value)
   {
   }

   public Object getProperty(String name)
   {
      return null;
   }

   public void setFeature(String name, boolean value)
   {
   }

   public boolean getFeature(String name)
   {
      return false;
   }

   private ContentHandler handler;
}
