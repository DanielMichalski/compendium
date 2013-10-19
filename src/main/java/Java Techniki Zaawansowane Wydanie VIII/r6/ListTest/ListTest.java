import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * Program demonstruj¹cy wykorzystanie listy zawieraj¹cej ³añcuchy znaków.
 * @version 1.23 2007-08-01
 * @author Cay Horstmann
 */
public class ListTest
{
   public static void main(String[] args)
   {
      EventQueue.invokeLater(new Runnable()
         {
            public void run()
            {
               JFrame frame = new ListFrame();
               frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
               frame.setVisible(true);
            }
         });
   }
}

/**
 * Ramka zawieraj¹ca listê s³ów i etykietê pokazuj¹c¹ zdanie 
 *  z³o¿one z wybranych s³ów. Przytrzymuj¹c klawisz Ctrl, wybraæ mo¿na wiele s³ów,
 *  a klawisz Shift pozwala na wybór ca³ego zakresu s³ów.
 */
class ListFrame extends JFrame
{
   public ListFrame()
   {
      setTitle("ListTest");
      setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

      String[] words = { "quick", "brown", "hungry", "wild", "silent", "huge", "private",
            "abstract", "static", "final" };

      wordList = new JList(words);
      wordList.setVisibleRowCount(4);
      JScrollPane scrollPane = new JScrollPane(wordList);

      listPanel = new JPanel();
      listPanel.add(scrollPane);
      wordList.addListSelectionListener(new ListSelectionListener()
         {
            public void valueChanged(ListSelectionEvent event)
            {
               Object[] values = wordList.getSelectedValues();

               StringBuilder text = new StringBuilder(prefix);
               for (int i = 0; i < values.length; i++)
               {
                  String word = (String) values[i];
                  text.append(word);
                  text.append(" ");
               }
               text.append(suffix);

               label.setText(text.toString());
            }
         });

      buttonPanel = new JPanel();
      group = new ButtonGroup();
      makeButton("Vertical", JList.VERTICAL);
      makeButton("Vertical Wrap", JList.VERTICAL_WRAP);
      makeButton("Horizontal Wrap", JList.HORIZONTAL_WRAP);

      add(listPanel, BorderLayout.NORTH);
      label = new JLabel(prefix + suffix);
      add(label, BorderLayout.CENTER);
      add(buttonPanel, BorderLayout.SOUTH);
   }

   /**
    * Tworzy przycisk wyboru uk³adu listy.
    * @param label etykieta przycisku
    * @param orientation uk³ad listy
    */
   private void makeButton(String label, final int orientation)
   {
      JRadioButton button = new JRadioButton(label);
      buttonPanel.add(button);
      if (group.getButtonCount() == 0) button.setSelected(true);
      group.add(button);
      button.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent event)
            {
               wordList.setLayoutOrientation(orientation);
               listPanel.revalidate();
            }
         });
   }

   private static final int DEFAULT_WIDTH = 400;
   private static final int DEFAULT_HEIGHT = 300;
   private JPanel listPanel;
   private JList wordList;
   private JLabel label;
   private JPanel buttonPanel;
   private ButtonGroup group;
   private String prefix = "The ";
   private String suffix = "fox jumps over the lazy dog.";
}

