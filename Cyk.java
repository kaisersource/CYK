import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

/**
 * Implementazione dell'algoritmo CYK che necessita di una grammatica non contestuale, e verifica se tale stringa è accettata da tale grammatica.
 * Autore: KaiserSource, Bayek
 * Date: July 15, 2017
 */
public class Cyk
{
   // Definizione costanti
   public static final int GRAMMAR_FILE = 0;
   public static final int FILE_ERROR = 1;
   public static final int LAST_ARG = 1;
   public static final int PARSE_ERROR = 2;
   public static final int TOTAL_ARGS = 2;

   /* La tabella per l'algoritmo CYK*/
   private static ArrayList<String>[][] table;

   /**
   * Le variabili sono nella forma (0 U 1)+ e sono memorizzate nella hashmap.
	 * Hashmap perché hanno accesso diretto e me ne sbatto dell'ordine.
   * La forma nelle hashmap è del tipo { (0 U 1)+, (0 U 1)+ }
   */
   private HashMap<String, String[]> var;

   /** 
    * I terminali sono nella forma  (a U b)
    */
   private HashMap<String, Character> ter;

   /* Variabili iniziali */
   private static String startVar;

   /**
    * Builda un oggetto Cyk e inizializza l'hashmap delle variabili e terminali
    */
   public Cyk()
   {
      var = new HashMap<String, String[]>();
      ter = new HashMap<String, Character>();
   }

   /**
    * Lascio all'oggetto scanner la facoltà di leggere il file della grammatica.
		* Maneggio tale file in modo da ottenere l'hashmap delle variabili e terminali
    */
   public void processGrammarFile(String file)
   {
      File grammarFile = null;
      Scanner scanner = null;
      try
      {
         grammarFile = new File(file);
         scanner = new Scanner(grammarFile);
         String[] line = scanner.nextLine().split(":");
         startVar = line[0];
         do
         {
            String variable = line[0];
            if ((line[1].equals("a") || line[1].equals("b")))
            {
               ter.put(variable, line[1].charAt(0));
            }
            else
            {
               String[] rest = line[1].split(",");
               if (rest != null)
               {
                  var.put(variable, rest);
               }
            }
            if (scanner.hasNextLine())
               line = scanner.nextLine().split(":");
            else
               line = null;
         } while (line != null);
         scanner.close();
      }
      catch (IOException ex)
      {
         ex.printStackTrace();
      }
   }

   /**
    * Confronta la stringa col file della grammatica usando il CYK.
    * restituisce true se la string w è accettata dalla grammatica, false altrimenti
    */
   @SuppressWarnings("unchecked")
   public boolean processString(String w)
   {
      int length = w.length();
      table = new ArrayList[length][]; // povero heap :'(
      for (int i = 0; i < length; ++i)
      {
         table[i] = new ArrayList[length];
         for (int j = 0; j < length; ++j)
            table[i][j] = new ArrayList < String > ();
      }
      for (int i = 0; i < length; ++i)
      {
         Set<String> keys = ter.keySet();
         for (String key : keys)
         {
            if (ter.get(key).charValue() == w.charAt(i))
               table[i][i].add(key);
         }
      }
      for (int l = 2; l <= length; ++l)
      {
         for (int i = 0; i <= length - l; ++i)
         {
            int j = i + l - 1;
            for (int k = i; k <= j - 1; ++k)
            {
               Set<String> keys = var.keySet();
               for (String key : keys)
               {
                  String[] values = var.get(key);
                  if (table[i][k].contains((values[0])) && table[k + 1][j].contains(values[1]))
                     table[i][j].add(key);
               }
            }
         }
      }
      if (table[0][length - 1].contains(startVar)) // startato da 0
         return true;
      return false;
   }


      public static void main(String[] args)
   {
      if (args.length != TOTAL_ARGS)
      {
         System.out.println("Requisiti: java Cyk file_grammatica stringa_da_parsare");
         System.exit(FILE_ERROR);
      }

      Cyk c = new Cyk();
      c.processGrammarFile(args[GRAMMAR_FILE]);

      if (c.processString(args[LAST_ARG]))
         System.out.println("true");
      else
         System.out.println("false");
   }
}
