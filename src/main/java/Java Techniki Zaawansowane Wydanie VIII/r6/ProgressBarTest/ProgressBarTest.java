import java.awt.*;
import java.awt.event.*;
import java.util.List;

import javax.swing.*;

/**
 *  Program demonstruj¹cy zastosowanie paska postêpu
 *  do monitorowania postêpu wykonania w¹tku.
 * @version 1.04 2007-08-01
 * @author Cay Horstmann
 */
public class ProgressBarTest
{
   public static void main(String[] args)
   {
      EventQueue.invokeLater(new Runnable()
         {
            public void run()
            {
               JFrame frame = new ProgressBarFrame();
               frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
               frame.setVisible(true);
            }
         });
   }
}

/**
 *  Ramka zawieraj¹ca przycisk uruchamiaj¹cy
 *  symulacjê czasoch³onnej operacji oraz pasek postêpu
 *  i pole tekstowe.
 */
class ProgressBarFrame extends JFrame
{
   public ProgressBarFrame()
   {
      setTitle("ProgressBarTest");
      setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

      // pole tekstowe, w którym prezentowane jest dzia³anie w¹tku
      textArea = new JTextArea();

      // panel zawieraj¹cy przycisk i pasek postêpu

      final int MAX = 1000;
      JPanel panel = new JPanel();
      startButton = new JButton("Start");
      progressBar = new JProgressBar(0, MAX);
      progressBar.setStringPainted(true);
      panel.add(startButton);
      panel.add(progressBar);

      checkBox = new JCheckBox("indeterminate");
      checkBox.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent event)
            {
               progressBar.setIndeterminate(checkBox.isSelected());
               progressBar.setStringPainted(!progressBar.isIndeterminate());
            }
         });
      panel.add(checkBox);
      add(new JScrollPane(textArea), BorderLayout.CENTER);
      add(panel, BorderLayout.SOUTH);

      // dodaje obiekt nas³uchuj¹cy przycisku

      startButton.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent event)
            {
               startButton.setEnabled(false);
               activity = new SimulatedActivity(MAX);
               activity.execute();
            }
         });
   }

   private JButton startButton;
   private JProgressBar progressBar;
   private JCheckBox checkBox;
   private JTextArea textArea;
   private SimulatedActivity activity;

   public static final int DEFAULT_WIDTH = 400;
   public static final int DEFAULT_HEIGHT = 200;
   
   class SimulatedActivity extends SwingWorker<Void, Integer>
   {
      /**
       * Tworzy w¹tek symulowanej operacji. Zwiêksza on wartoœæ licznika
       * do momentu osi¹gniêcia wartoœci docelowej.
       * @param t wartoœæ docelowa licznika.
       */
      public SimulatedActivity(int t)
      {
         current = 0;
         target = t;
      }

      protected Void doInBackground() throws Exception
      {
         try
         {
            while (current < target)
            {
               Thread.sleep(100);
               current++;
               publish(current);
            }
         }
         catch (InterruptedException e)
         {
         }
         return null;
      }

      protected void process(List<Integer> chunks)
      {
         for (Integer chunk : chunks)
         {
            textArea.append(chunk + "\n");
            progressBar.setValue(chunk);
         }
      }
      
      protected void done()
      {
         startButton.setEnabled(true);
      }
      
      private int current;
      private int target;
   }   
}