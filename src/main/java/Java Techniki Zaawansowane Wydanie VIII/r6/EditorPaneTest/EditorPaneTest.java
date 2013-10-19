import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * Program demonstruj¹cy wyœwietlanie stron HTML
 * za pomoc¹ panelu edytora.
 * @version 1.03 2007-08-01
 * @author Cay Horstmann
 */
public class EditorPaneTest
{
   public static void main(String[] args)
   {
      EventQueue.invokeLater(new Runnable()
         {
            public void run()
            {
               JFrame frame = new EditorPaneFrame();
               frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
               frame.setVisible(true);
            }
         });
   }
}

/**
 * Ramka zawieraj¹ca panel edytora, pole tekstowe i przycisk Load
 * umo¿liwiaj¹ce wprowadzenie adresu URL i za³adowanie strony, 
 * a tak¿e przycisk Back umo¿liwiaj¹cy powrót do poprzedniej strony.
 */
class EditorPaneFrame extends JFrame
{
   public EditorPaneFrame()
   {
      setTitle("EditorPaneTest");
      setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

      final Stack<String> urlStack = new Stack<String>();
      final JEditorPane editorPane = new JEditorPane();
      final JTextField url = new JTextField(30);

      // instaluje obiekt nas³uchuj¹cy hiper³¹cza

      editorPane.setEditable(false);
      editorPane.addHyperlinkListener(new HyperlinkListener()
         {
            public void hyperlinkUpdate(HyperlinkEvent event)
            {
               if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
               {
                  try
                  {
                     // zapamiêtuje adres URL dla potrzeb przycisku Back
                     urlStack.push(event.getURL().toString());
                     // prezentuje adres URL w polu tekstowym
                     url.setText(event.getURL().toString());
                     editorPane.setPage(event.getURL());
                  }
                  catch (IOException e)
                  {
                     editorPane.setText("Exception: " + e);
                  }
               }
            }
         });

      // konfiguruje pole wyboru umo¿liwiaj¹ce w³¹czenie trybu edycji

      final JCheckBox editable = new JCheckBox();
      editable.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent event)
            {
               editorPane.setEditable(editable.isSelected());
            }
         });

      // tworzy obiekt nas³uchuj¹cy przycisku Load

      ActionListener listener = new ActionListener()
         {
            public void actionPerformed(ActionEvent event)
            {
               try
               {
                  // zapamiêtuje adres URL dla potrzeb przycisku Back
                  urlStack.push(url.getText());
                  editorPane.setPage(url.getText());
               }
               catch (IOException e)
               {
                  editorPane.setText("Exception: " + e);
               }
            }
         };

      JButton loadButton = new JButton("Load");
      loadButton.addActionListener(listener);
      url.addActionListener(listener);

      // tworzy przycisk Back i jego obiekt nas³uchuj¹cy

      JButton backButton = new JButton("Back");
      backButton.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent event)
            {
               if (urlStack.size() <= 1) return;
               try
               {
                  // pobiera adres URL
                  urlStack.pop();
                  // umieszcza adres URL w polu tekstowym
                  String urlString = urlStack.peek();
                  url.setText(urlString);
                  editorPane.setPage(urlString);
               }
               catch (IOException e)
               {
                  editorPane.setText("Exception: " + e);
               }
            }
         });

      add(new JScrollPane(editorPane), BorderLayout.CENTER);

      // umieszcza wszystkie komponenty na g³ównym panelu okna

      JPanel panel = new JPanel();
      panel.add(new JLabel("URL"));
      panel.add(url);
      panel.add(loadButton);
      panel.add(backButton);
      panel.add(new JLabel("Editable"));
      panel.add(editable);

      add(panel, BorderLayout.SOUTH);
   }

   private static final int DEFAULT_WIDTH = 600;
   private static final int DEFAULT_HEIGHT = 400;
}
