import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 * Program demonstruj¹cy wykorzystanie obiektów rysuj¹cych komórki
 * i edytorów komórek tabeli.
 * @version 1.02 2007-08-01
 * @author Cay Horstmann
 */
public class TableCellRenderTest
{
   public static void main(String[] args)
   {
      EventQueue.invokeLater(new Runnable()
         {
            public void run()
            {

               JFrame frame = new TableCellRenderFrame();
               frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
               frame.setVisible(true);
            }
         });
   }
}

/**
 * Ramka zawieraj¹ca tabelê danych o planetach.
 */
class TableCellRenderFrame extends JFrame
{
   public TableCellRenderFrame()
   {
      setTitle("TableCellRenderTest");
      setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

      TableModel model = new PlanetTableModel();
      JTable table = new JTable(model);
      table.setRowSelectionAllowed(false);

      // instaluje obiekty rysuj¹ce i edytory

      table.setDefaultRenderer(Color.class, new ColorTableCellRenderer());
      table.setDefaultEditor(Color.class, new ColorTableCellEditor());

      JComboBox moonCombo = new JComboBox();
      for (int i = 0; i <= 20; i++)
         moonCombo.addItem(i);

      TableColumnModel columnModel = table.getColumnModel();
      TableColumn moonColumn = columnModel.getColumn(PlanetTableModel.MOONS_COLUMN);
      moonColumn.setCellEditor(new DefaultCellEditor(moonCombo));
      moonColumn.setHeaderRenderer(table.getDefaultRenderer(ImageIcon.class));
      moonColumn.setHeaderValue(new ImageIcon("Moons.gif"));

      // pokazuje tabelê

      table.setRowHeight(100);
      add(new JScrollPane(table), BorderLayout.CENTER);
   }

   private static final int DEFAULT_WIDTH = 600;
   private static final int DEFAULT_HEIGHT = 400;
}

/**
 * Model tabeli planet okreœlaj¹cy wartoœci, 
 * sposób rysowania i edycji danych.
 */
class PlanetTableModel extends AbstractTableModel
{
   public String getColumnName(int c)
   {
      return columnNames[c];
   }

   public Class<?> getColumnClass(int c)
   {
      return cells[0][c].getClass();
   }

   public int getColumnCount()
   {
      return cells[0].length;
   }

   public int getRowCount()
   {
      return cells.length;
   }

   public Object getValueAt(int r, int c)
   {
      return cells[r][c];
   }

   public void setValueAt(Object obj, int r, int c)
   {
      cells[r][c] = obj;
   }

   public boolean isCellEditable(int r, int c)
   {
      return c == PLANET_COLUMN || c == MOONS_COLUMN || c == GASEOUS_COLUMN || c == COLOR_COLUMN;
   }

   public static final int PLANET_COLUMN = 0;
   public static final int MOONS_COLUMN = 2;
   public static final int GASEOUS_COLUMN = 3;
   public static final int COLOR_COLUMN = 4;

   private Object[][] cells = {
         { "Mercury", 2440.0, 0, false, Color.YELLOW, new ImageIcon("Mercury.gif") },
         { "Venus", 6052.0, 0, false, Color.YELLOW, new ImageIcon("Venus.gif") },
         { "Earth", 6378.0, 1, false, Color.BLUE, new ImageIcon("Earth.gif") },
         { "Mars", 3397.0, 2, false, Color.RED, new ImageIcon("Mars.gif") },
         { "Jupiter", 71492.0, 16, true, Color.ORANGE, new ImageIcon("Jupiter.gif") },
         { "Saturn", 60268.0, 18, true, Color.ORANGE, new ImageIcon("Saturn.gif") },
         { "Uranus", 25559.0, 17, true, Color.BLUE, new ImageIcon("Uranus.gif") },
         { "Neptune", 24766.0, 8, true, Color.BLUE, new ImageIcon("Neptune.gif") },
         { "Pluto", 1137.0, 1, false, Color.BLACK, new ImageIcon("Pluto.gif") } };

   private String[] columnNames = { "Planet", "Radius", "Moons", "Gaseous", "Color", "Image" };
}

/**
 * Klasa obiektu rysuj¹cego kolorowy panel wewn¹trz komórki.
 */
class ColorTableCellRenderer extends JPanel implements TableCellRenderer
{
   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
         boolean hasFocus, int row, int column)
   {
      setBackground((Color) value);
      if (hasFocus) setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
      else setBorder(null);
      return this;
   }
}

/**
 * Edytor otwieraj¹cy okno dialogowe wyboru koloru.
 */
class ColorTableCellEditor extends AbstractCellEditor implements TableCellEditor
{
   public ColorTableCellEditor()
   {
      panel = new JPanel();
      // przygotowuje okno dialogowe

      colorChooser = new JColorChooser();
      colorDialog = JColorChooser.createDialog(null, "Planet Color", false, colorChooser,
            new ActionListener() // obiekt nas³uchujacy przycisku OK
               {
                  public void actionPerformed(ActionEvent event)
                  {
                     stopCellEditing();
                  }
               }, new ActionListener() // obiekt nas³uchuj¹cy przycisku Cancel
               {
                  public void actionPerformed(ActionEvent event)
                  {
                     cancelCellEditing();
                  }
               });
      colorDialog.addWindowListener(new WindowAdapter()
         {
            public void windowClosing(WindowEvent event)
            {
               cancelCellEditing();
            }
         });
   }

   public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
         int row, int column)
   {
      // Tutaj uzyskujemy bie¿¹c¹ wartoœæ Color.
      // Przechowujemy j¹ w obiekcie okna dialogowego.

      colorChooser.setColor((Color) value);
      return panel;
   }

   public boolean shouldSelectCell(EventObject anEvent)
   {
      // rozpoczêcie edycji
      colorDialog.setVisible(true);

      // informuje metodê wywo³uj¹c¹ o rozpoczêciu edycji
      return true;
   }

   public void cancelCellEditing()
   {
      // edycja przerwana - zamyka okno dialogowe
      colorDialog.setVisible(false);
      super.cancelCellEditing();
   }

   public boolean stopCellEditing()
   {
      // edycja zakoñczona - zamyka okno dialogowe
      colorDialog.setVisible(false);
      super.stopCellEditing();

      // informuje metodê wywo³uj¹c¹, ¿e wartoœæ koloru jest dozwolona
      return true;
   }

   public Object getCellEditorValue()
   {
      return colorChooser.getColor();
   }

   private JColorChooser colorChooser;
   private JDialog colorDialog;
   private JPanel panel;
}
