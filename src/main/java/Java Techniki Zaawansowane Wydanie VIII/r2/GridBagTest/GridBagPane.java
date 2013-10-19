import java.awt.*;
import java.beans.*;
import java.io.*;
import java.lang.reflect.*;
import javax.swing.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

/**
 * Panel wykorzystuj¹cy plik XML zawieraj¹cy
 * opis komponentów i ich uk³adu na siatce typu GridBagLayout.
 * @version 1.10 2004-09-04
 * @author Cay Horstmann
 */
public class GridBagPane extends JPanel
{
   /**
    * Tworzy obiekt klasy GridBagPane.
    * @param filename nazwa pliku XML opisuj¹cego komponenty
    * i ich uk³ad
    */
   public GridBagPane(String filename)
   {
      setLayout(new GridBagLayout());
      constraints = new GridBagConstraints();

      try
      {
         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         factory.setValidating(true);

         if (filename.contains("-schema"))
         {
            factory.setNamespaceAware(true);
            final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
            final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
            factory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
         }

         factory.setIgnoringElementContentWhitespace(true);

         DocumentBuilder builder = factory.newDocumentBuilder();
         Document doc = builder.parse(new File(filename));

         if (filename.contains("-schema"))
         {
            int count = removeElementContentWhitespace(doc.getDocumentElement());
            System.out.println(count + " whitespace nodes removed.");
         }

         parseGridbag(doc.getDocumentElement());
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   /**
    * Usuwa odstêpy z zawartoœci elementu
    * @param e element korzenia
    * @return liczba usuniêtych wêz³ów reprezentuj¹cych odstêpy
    */
   private int removeElementContentWhitespace(Element e)
   {
      NodeList children = e.getChildNodes();
      int count = 0;
      boolean allTextChildrenAreWhiteSpace = true;
      int elements = 0;
      for (int i = 0; i < children.getLength() && allTextChildrenAreWhiteSpace; i++)
      {
         Node child = children.item(i);
         if (child instanceof Text && ((Text) child).getData().trim().length() > 0) allTextChildrenAreWhiteSpace = false;
         else if (child instanceof Element)
         {
            elements++;
            count += removeElementContentWhitespace((Element) child);
         }
      }
      if (elements > 0 && allTextChildrenAreWhiteSpace) // heuristics for element content
      {
         for (int i = children.getLength() - 1; i >= 0; i--)
         {
            Node child = children.item(i);
            if (child instanceof Text)
            {
               e.removeChild(child);
               count++;
            }
         }
      }
      return count;
   }

   /**
    * Zwraca komponent o podanej nazwie
    * @param name nazwa komponentu
    * @return the komponent o podanej nazwie lub wartoœæ null,
    * jeœli ¿aden z komponentów panelu nie posiada takiej nazwy
    */
   public Component get(String name)
   {
      Component[] components = getComponents();
      for (int i = 0; i < components.length; i++)
      {
         if (components[i].getName().equals(name)) return components[i];
      }
      return null;
   }

   /**
    * Parsuje element panelu.
    * @param e element panelu
    */
   private void parseGridbag(Element e)
   {
      NodeList rows = e.getChildNodes();
      for (int i = 0; i < rows.getLength(); i++)
      {
         Element row = (Element) rows.item(i);
         NodeList cells = row.getChildNodes();
         for (int j = 0; j < cells.getLength(); j++)
         {
            Element cell = (Element) cells.item(j);
            parseCell(cell, i, j);
         }
      }
   }

   /**
    * Parsuje element komórki.
    * @param e element komórki
    * @param r wiersz komórki
    * @param c kolumna komórki
    */
   private void parseCell(Element e, int r, int c)
   {
      // pobiera atrybuty

      String value = e.getAttribute("gridx");
      if (value.length() == 0) // stosuje wartoœæ domyœln¹
      {
         if (c == 0) constraints.gridx = 0;
         else constraints.gridx += constraints.gridwidth;
      }
      else constraints.gridx = Integer.parseInt(value);

      value = e.getAttribute("gridy");
      if (value.length() == 0) // stosuje wartoœæ domyœln¹
      constraints.gridy = r;
      else constraints.gridy = Integer.parseInt(value);

      constraints.gridwidth = Integer.parseInt(e.getAttribute("gridwidth"));
      constraints.gridheight = Integer.parseInt(e.getAttribute("gridheight"));
      constraints.weightx = Integer.parseInt(e.getAttribute("weightx"));
      constraints.weighty = Integer.parseInt(e.getAttribute("weighty"));
      constraints.ipadx = Integer.parseInt(e.getAttribute("ipadx"));
      constraints.ipady = Integer.parseInt(e.getAttribute("ipady"));

      // stosuje refleksjê dla uzyskania wartoœci ca³kowitych pól statycznych
      Class<GridBagConstraints> cl = GridBagConstraints.class;

      try
      {
         String name = e.getAttribute("fill");
         Field f = cl.getField(name);
         constraints.fill = f.getInt(cl);

         name = e.getAttribute("anchor");
         f = cl.getField(name);
         constraints.anchor = f.getInt(cl);
      }
      catch (Exception ex) // metody refleksji mog¹ wyrzucaæ ró¿ne wyj¹tki
      {
         ex.printStackTrace();
      }

      Component comp = (Component) parseBean((Element) e.getFirstChild());
      add(comp, constraints);
   }

   /**
    * Parsuje element ziarnka.
    *  @param e element ziarnka
    */
   private Object parseBean(Element e)
   {
      try
      {
         NodeList children = e.getChildNodes();
         Element classElement = (Element) children.item(0);
         String className = ((Text) classElement.getFirstChild()).getData();

         Class<?> cl = Class.forName(className);

         Object obj = cl.newInstance();

         if (obj instanceof Component) ((Component) obj).setName(e.getAttribute("id"));

         for (int i = 1; i < children.getLength(); i++)
         {
            Node propertyElement = children.item(i);
            Element nameElement = (Element) propertyElement.getFirstChild();
            String propertyName = ((Text) nameElement.getFirstChild()).getData();

            Element valueElement = (Element) propertyElement.getLastChild();
            Object value = parseValue(valueElement);
            BeanInfo beanInfo = Introspector.getBeanInfo(cl);
            PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
            boolean done = false;
            for (int j = 0; !done && j < descriptors.length; j++)
            {
               if (descriptors[j].getName().equals(propertyName))
               {
                  descriptors[j].getWriteMethod().invoke(obj, value);
                  done = true;
               }
            }

         }
         return obj;
      }
      catch (Exception ex) // metody refleksji mog¹ wyrzucaæ ró¿ne wyj¹tki
      {
         ex.printStackTrace();
         return null;
      }
   }

   /**
    * Parsuje element wartoœci.
    * @param e element wartoœci
    */
   private Object parseValue(Element e)
   {
      Element child = (Element) e.getFirstChild();
      if (child.getTagName().equals("bean")) return parseBean(child);
      String text = ((Text) child.getFirstChild()).getData();
      if (child.getTagName().equals("int")) return new Integer(text);
      else if (child.getTagName().equals("boolean")) return new Boolean(text);
      else if (child.getTagName().equals("string")) return text;
      else return null;
   }

   private GridBagConstraints constraints;
}