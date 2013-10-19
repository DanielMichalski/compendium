import java.awt.*;

import javax.swing.*;
import javax.swing.event.*;

/**
 * Program demonstruj¹cy wykorzystanie listy, która dynamicznie wyznacza swoje elementy.
 * @version 1.23 2007-08-01
 * @author Cay Horstmann
 */
public class LongListTest
{
   public static void main(String[] args)
   {
      EventQueue.invokeLater(new Runnable()
         {
            public void run()
            {
               JFrame frame = new LongListFrame();
               frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
               frame.setVisible(true);
            }
         });
   }
}

/**
 *  Ramka zawieraj¹ca d³ug¹ listê s³ów i etykietê pokazuj¹c¹ zdanie 
 *  z³o¿one z wybranych s³ów.
 */
class LongListFrame extends JFrame
{
   public LongListFrame()
   {
      setTitle("LongListTest");
      setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

      wordList = new JList(new WordListModel(3));
      wordList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      wordList.setPrototypeCellValue("www");
      JScrollPane scrollPane = new JScrollPane(wordList);

      JPanel p = new JPanel();
      p.add(scrollPane);
      wordList.addListSelectionListener(new ListSelectionListener()
         {
            public void valueChanged(ListSelectionEvent evt)
            {
               StringBuilder word = (StringBuilder) wordList.getSelectedValue();
               setSubject(word.toString());
            }

         });

      Container contentPane = getContentPane();
      contentPane.add(p, BorderLayout.NORTH);
      label = new JLabel(prefix + suffix);
      contentPane.add(label, BorderLayout.CENTER);
      setSubject("fox");
   }

   /**
    * Okreœla podmiot zdania pokazywanego za pomoc¹ etykiety.
    * @param word nowy podmiot zdania
    */
   public void setSubject(String word)
   {
      StringBuilder text = new StringBuilder(prefix);
      text.append(word);
      text.append(suffix);
      label.setText(text.toString());
   }

   private static final int DEFAULT_WIDTH = 400;
   private static final int DEFAULT_HEIGHT = 300;
   private JList wordList;
   private JLabel label;
   private String prefix = "The quick brown ";
   private String suffix = " jumps over the lazy dog.";
}

/**
 * Model listy dynamicznie generuj¹cy kombinacje n liter.
 */
class WordListModel extends AbstractListModel
{
   /**
    * Tworzy model.
    * @param n d³ugoœæ kombinacji (s³owa)
    */
   public WordListModel(int n)
   {
      length = n;
   }

   public int getSize()
   {
      return (int) Math.pow(LAST - FIRST + 1, length);
   }

   public Object getElementAt(int n)
   {
      StringBuilder r = new StringBuilder();
      ;
      for (int i = 0; i < length; i++)
      {
         char c = (char) (FIRST + n % (LAST - FIRST + 1));
         r.insert(0, c);
         n = n / (LAST - FIRST + 1);
      }
      return r;
   }

   private int length;
   public static final char FIRST = 'a';
   public static final char LAST = 'z';
}

