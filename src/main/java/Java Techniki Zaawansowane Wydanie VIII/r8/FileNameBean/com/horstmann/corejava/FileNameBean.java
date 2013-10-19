package com.horstmann.corejava;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.filechooser.*;

/**
 * Ziarnko wyboru pliku.
 * @version 1.30 2007-10-03
 * @author Cay Horstmann
 */
public class FileNameBean extends JPanel
{
   public FileNameBean()
   {
      dialogButton = new JButton("...");
      nameField = new JTextField(30);

      chooser = new JFileChooser();
      setPreferredSize(new Dimension(XPREFSIZE, YPREFSIZE));

      setLayout(new GridBagLayout());
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.weightx = 100;
      gbc.weighty = 100;
      gbc.anchor = GridBagConstraints.WEST;
      gbc.fill = GridBagConstraints.BOTH;
      gbc.gridwidth = 1;
      gbc.gridheight = 1;
      add(nameField, gbc);

      dialogButton.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent event)
            {
               chooser.setFileFilter(new FileNameExtensionFilter(Arrays.toString(extensions),
                     extensions));
               int r = chooser.showOpenDialog(null);
               if (r == JFileChooser.APPROVE_OPTION)
               {
                  File f = chooser.getSelectedFile();
                  String name = f.getAbsolutePath();
                  setFileName(name);
               }
            }
         });
      nameField.setEditable(false);

      gbc.weightx = 0;
      gbc.anchor = GridBagConstraints.EAST;
      gbc.fill = GridBagConstraints.NONE;
      gbc.gridx = 1;
      add(dialogButton, gbc);
   }

   /**
    * Metoda set w³aœciwoœci fileName.
    * @param newValue nowa nazwa pliku
    */
   public void setFileName(String newValue)
   {
      String oldValue = nameField.getText();
      nameField.setText(newValue);
      firePropertyChange("fileName", oldValue, newValue);
   }

   /**
    * Metoda get w³aœciwoœci fileName.
    * @return nazwa wybranego pliku
    */
   public String getFileName()
   {
      return nameField.getText();
   }

   /**
    * Metoda get w³aœciwoœci extensions.
    * @return domyœlne rozszerzenia nazwy pliku
    */
   public String[] getExtensions()
   {
      return extensions;
   }

   /**
    * Metoda set w³aœciwoœci extensions.
    * @param newvalue nowe domyœlne rozszerzenia nazwy pliku
    */
   public void setExtensions(String[] newValue)
   {
      extensions = newValue;
   }

   /**
    * Zwraca jedn¹ z wartoœci w³aœciwoœci extensions.
    * @param i indeks wartoœci w³aœciwoœci
    * @return wartoœæ o podanym indeksie
    */
   public String getExtensions(int i)
   {
      if (0 <= i && i < extensions.length) return extensions[i];
      else return "";
   }

   /**
    * Nadaje wartoœæ w³aœciwoœci extensions .
    * @param indeks wartoœci w³aœciwoœci
    * @param newValue nowa wartoœæ o podanym indeksie
    */
   public void setExtensions(int i, String newValue)
   {
      if (0 <= i && i < extensions.length) extensions[i] = newValue;
   }

   private static final int XPREFSIZE = 200;
   private static final int YPREFSIZE = 20;
   private JButton dialogButton;
   private JTextField nameField;
   private JFileChooser chooser;
   private String[] extensions = { "gif", "png" };
}
