/**
   @version 1.10 1997-07-01
   @author Cay Horstmann
*/

#include "Printf3.h"
#include <string.h>
#include <stdlib.h>
#include <float.h>

/**
   @param format ³añcuch okreœlaj¹cy sposób formatowania
   (na przyk³ad "%8.2f"). Pod³añcuchy "%%" s¹ pomijane.
   @return wskaŸnik specyfikatora formatu (z pominiêciem znaku '%')
   lub wartoœæ NULL, jeœli nie zosta³ odnaleziony.
*/
char* find_format(const char format[])
{  
   char* p;
   char* q;

   p = strchr(format, '%');
   while (p != NULL && *(p + 1) == '%') /* pomija %% */
      p = strchr(p + 2, '%');
   if (p == NULL) return NULL;
   /* sprawdza czy % jest unikalny */
   p++;
   q = strchr(p, '%');
   while (q != NULL && *(q + 1) == '%') /* pomija %% */
      q = strchr(q + 2, '%');
   if (q != NULL) return NULL; /* % nie jest unikalny */
   q = p + strspn(p, " -0+#"); /* pomija znaczniki */
   q += strspn(q, "0123456789"); /* i specyfikacjê szerokoœci pola */
   if (*q == '.') { q++; q += strspn(q, "0123456789"); }
      /* pomija specyfikacjê precyzji */
   if (strchr("eEfFgG", *q) == NULL) return NULL;
      /* to nie jest format zmiennoprzecinkowy */
   return p;
}

JNIEXPORT void JNICALL Java_Printf3_fprint(JNIEnv* env, jclass cl, 
   jobject out, jstring format, jdouble x)
{  
   const char* cformat;
   char* fmt;
   jstring str;
   jclass class_PrintWriter;
   jmethodID id_print;

   cformat = (*env)->GetStringUTFChars(env, format, NULL);
   fmt = find_format(cformat);
   if (fmt == NULL)
      str = format;
   else
   {  
      char* cstr;
      int width = atoi(fmt);
      if (width == 0) width = DBL_DIG + 10;
      cstr = (char*) malloc(strlen(cformat) + width);
      sprintf(cstr, cformat, x);
      str = (*env)->NewStringUTF(env, cstr);
      free(cstr);
   }
   (*env)->ReleaseStringUTFChars(env, format, cformat);

   /* wywo³uje metodê ps.print(str) */

   /* ustala klasê */
   class_PrintWriter = (*env)->GetObjectClass(env, out);

   /* pobiera identyfikator metody */
   id_print = (*env)->GetMethodID(env, class_PrintWriter, "print", "(Ljava/lang/String;)V");

   /* wywo³uje metodê */
   (*env)->CallVoidMethod(env, out, id_print, str);
}


