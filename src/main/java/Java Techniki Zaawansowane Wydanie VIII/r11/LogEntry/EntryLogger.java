import java.io.*;
import org.apache.bcel.*;
import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;

/**
 * Dodaje kod zapisu komunikatów dziennika
 * do wszystkich metod oznaczonych adnotacj¹ LogEntry.
 * @version 1.10 2007-10-27
 * @author Cay Horstmann
 */
public class EntryLogger
{
   /**
    * Dodaje kod zapisu komunikatów dziennika do podanej klasy
    * @param args nazwa pliku klasy
    */
   public static void main(String[] args)
   {
      try
      {
         if (args.length == 0) System.out.println("USAGE: java EntryLogger classname");
         else
         {
            JavaClass jc = Repository.lookupClass(args[0]);
            ClassGen cg = new ClassGen(jc);
            EntryLogger el = new EntryLogger(cg);
            el.convert();
            File f = new File(Repository.lookupClassFile(cg.getClassName()).getPath());
            cg.getJavaClass().dump(f.getPath());
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   /**
    * Tworzy obiekt EntryLogger, który dodaje kod zapisu komunikatów dziennika 
    * do wszystkich metod oznaczonych adnotacj¹ nale¿¹cych do podanej klasy
    * @param cg klasa
    */
   public EntryLogger(ClassGen cg)
   {
      this.cg = cg;
      cpg = cg.getConstantPool();
   }

   /**
    * Przekszta³ca klasê, dodaj¹c wywo³ania zapisu do dziennika.
    */
   public void convert() throws IOException
   {
      for (Method m : cg.getMethods())
      {
         AnnotationEntry[] annotations = m.getAnnotationEntries();
         for (AnnotationEntry a : annotations)
         {
            if (a.getAnnotationType().equals("LLogEntry;"))
            {
               for (ElementValuePair p : a.getElementValuePairs())
               {
                  if (p.getNameString().equals("logger"))
                  {
                     String loggerName = p.getValue().stringifyValue();
                     cg.replaceMethod(m, insertLogEntry(m, loggerName));
                  }
               }
            }
         }
      }
   }

   /**
    * Wstawia kod na pocz¹tku metody.
    * @param m metod
    * @param loggerName nazwa wywo³ywanego obiektu zapisu do dziennika
    */
   private Method insertLogEntry(Method m, String loggerName)
   {
      MethodGen mg = new MethodGen(m, cg.getClassName(), cpg);
      String className = cg.getClassName();
      String methodName = mg.getMethod().getName();
      System.out.printf("Adding logging instructions to %s.%s%n", className, methodName);

      int getLoggerIndex = cpg.addMethodref("java.util.logging.Logger", "getLogger",
            "(Ljava/lang/String;)Ljava/util/logging/Logger;");
      int enteringIndex = cpg.addMethodref("java.util.logging.Logger", "entering",
            "(Ljava/lang/String;Ljava/lang/String;)V");

      InstructionList il = mg.getInstructionList();
      InstructionList patch = new InstructionList();
      patch.append(new PUSH(cpg, loggerName));
      patch.append(new INVOKESTATIC(getLoggerIndex));
      patch.append(new PUSH(cpg, className));
      patch.append(new PUSH(cpg, methodName));
      patch.append(new INVOKEVIRTUAL(enteringIndex));
      InstructionHandle[] ihs = il.getInstructionHandles();
      il.insert(ihs[0], patch);

      mg.setMaxStack();
      return mg.getMethod();
   }

   private ClassGen cg;
   private ConstantPoolGen cpg;
}
