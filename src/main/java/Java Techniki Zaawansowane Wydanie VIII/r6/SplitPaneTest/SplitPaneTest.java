import java.awt.*;

import javax.swing.*;
import javax.swing.event.*;

/**
 * Program demonstruj¹cy wykorzystanie komponentu panelu dzielonego.
 * @version 1.03 2007-08-01
 * @author Cay Horstmann
 */
public class SplitPaneTest
{
   public static void main(String[] args)
   {
      EventQueue.invokeLater(new Runnable()
         {
            public void run()
            {
               JFrame frame = new SplitPaneFrame();
               frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
               frame.setVisible(true);
            }
         });
   }
}

/**
 * Ramka zawieraj¹ca dwa zagnie¿d¿one panele dzielone wyœwietlaj¹ce
 * obrazki planet i opisy.
 */
class SplitPaneFrame extends JFrame
{
   public SplitPaneFrame()
   {
      setTitle("SplitPaneTest");
      setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

      // tworzy komponenty dla prezentacji nazw i opisu planet
      // oraz ich obrazków

      final JList planetList = new JList(planets);
      final JLabel planetImage = new JLabel();
      final JTextArea planetDescription = new JTextArea();

      planetList.addListSelectionListener(new ListSelectionListener()
         {
            public void valueChanged(ListSelectionEvent event)
            {
               Planet value = (Planet) planetList.getSelectedValue();

               // aktualizuje obrazek i opis

               planetImage.setIcon(value.getImage());
               planetDescription.setText(value.getDescription());
            }
         });

      // tworzy panele dzielone

      JSplitPane innerPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, planetList, planetImage);

      innerPane.setContinuousLayout(true);
      innerPane.setOneTouchExpandable(true);

      JSplitPane outerPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, innerPane,
            planetDescription);

      add(outerPane, BorderLayout.CENTER);
   }

   private Planet[] planets = { new Planet("Mercury", 2440, 0), new Planet("Venus", 6052, 0),
         new Planet("Earth", 6378, 1), new Planet("Mars", 3397, 2),
         new Planet("Jupiter", 71492, 16), new Planet("Saturn", 60268, 18),
         new Planet("Uranus", 25559, 17), new Planet("Neptune", 24766, 8),
         new Planet("Pluto", 1137, 1), };
   private static final int DEFAULT_WIDTH = 300;
   private static final int DEFAULT_HEIGHT = 300;
}

/**
 * Klasa reprezentuj¹ca planety.
 */
class Planet
{
   /**
    * Tworzy obiekt reprezentuj¹cy planetê.
    * @param n nazwa planety
    * @param r promieñ planety
    * @param m liczba ksiê¿yców
    */
   public Planet(String n, double r, int m)
   {
      name = n;
      radius = r;
      moons = m;
      image = new ImageIcon(name + ".gif");
   }

   public String toString()
   {
      return name;
   }

   /**
    * Pobiera opis planety.
    * @return opis
    */
   public String getDescription()
   {
      return "Radius: " + radius + "\nMoons: " + moons + "\n";
   }

   /**
    * Pobiera obrazek planety.
    * @return obrazek
    */
   public ImageIcon getImage()
   {
      return image;
   }

   private String name;
   private double radius;
   private int moons;
   private ImageIcon image;
}
