import java.awt.*;

import javax.swing.*;
import javax.swing.tree.*;

/**
 * Program wyœwietlaj¹cy najprostsze drzewo.
 * @version 1.02 2007-08-01
 * @author Cay Horstmann
 */
public class SimpleTree
{
   public static void main(String[] args)
   {
      EventQueue.invokeLater(new Runnable()
         {
            public void run()
            {
               JFrame frame = new SimpleTreeFrame();
               frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
               frame.setVisible(true);
            }
         });
   }
}

/**
 *  Ramka zawieraj¹ca drzewo wyœwietlaj¹ce dane
 *  "rêcznie" utworzonego modelu drzewa.
 */
class SimpleTreeFrame extends JFrame
{
   public SimpleTreeFrame()
   {
      setTitle("SimpleTree");
      setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

      // tworzy model drzewa

      DefaultMutableTreeNode root = new DefaultMutableTreeNode("World");
      DefaultMutableTreeNode country = new DefaultMutableTreeNode("USA");
      root.add(country);
      DefaultMutableTreeNode state = new DefaultMutableTreeNode("California");
      country.add(state);
      DefaultMutableTreeNode city = new DefaultMutableTreeNode("San Jose");
      state.add(city);
      city = new DefaultMutableTreeNode("Cupertino");
      state.add(city);
      state = new DefaultMutableTreeNode("Michigan");
      country.add(state);
      city = new DefaultMutableTreeNode("Ann Arbor");
      state.add(city);
      country = new DefaultMutableTreeNode("Germany");
      root.add(country);
      state = new DefaultMutableTreeNode("Schleswig-Holstein");
      country.add(state);
      city = new DefaultMutableTreeNode("Kiel");
      state.add(city);

      // tworzy drzewo i umieszcza je w przewijalnym panelu

      JTree tree = new JTree(root);
      add(new JScrollPane(tree));
   }

   private static final int DEFAULT_WIDTH = 300;
   private static final int DEFAULT_HEIGHT = 200;
}
