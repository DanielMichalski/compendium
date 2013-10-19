/**
   @version 1.00 1997-07-01
   @author Cay Horstmann
*/

#include "Win32RegKey.h"
#include "Win32RegKeyNameEnumeration.h"
#include <string.h>
#include <stdlib.h>
#include <windows.h>

JNIEXPORT jobject JNICALL Java_Win32RegKey_getValue(JNIEnv* env, jobject this_obj, jobject name)
{  
   const char* cname;
   jstring path;
   const char* cpath;
   HKEY hkey;
   DWORD type;
   DWORD size;
   jclass this_class;
   jfieldID id_root;
   jfieldID id_path;
   HKEY root;
   jobject ret;
   char* cret;

   /* ustala klasê */
   this_class = (*env)->GetObjectClass(env, this_obj);

   /* pobiera identyfikatory pól */
   id_root = (*env)->GetFieldID(env, this_class, "root", "I");
   id_path = (*env)->GetFieldID(env, this_class, "path", "Ljava/lang/String;");

   /* pobiera wartoœci pól */
   root = (HKEY) (*env)->GetIntField(env, this_obj, id_root);
   path = (jstring)(*env)->GetObjectField(env, this_obj, id_path);
   cpath = (*env)->GetStringUTFChars(env, path, NULL);

   /* otwiera klucz rejestru */
   if (RegOpenKeyEx(root, cpath, 0, KEY_READ, &hkey) != ERROR_SUCCESS)
   {  
      (*env)->ThrowNew(env, (*env)->FindClass(env, "Win32RegKeyException"), 
         "Open key failed");
      (*env)->ReleaseStringUTFChars(env, path, cpath);
      return NULL;
   }

   (*env)->ReleaseStringUTFChars(env, path, cpath);
   cname = (*env)->GetStringUTFChars(env, name, NULL);

   /* pobiera typ i rozmiar wartoœci */
   if (RegQueryValueEx(hkey, cname, NULL, &type, NULL, &size) != ERROR_SUCCESS)
   {  
      (*env)->ThrowNew(env, (*env)->FindClass(env, "Win32RegKeyException"),
         "Query value key failed");
      RegCloseKey(hkey);
      (*env)->ReleaseStringUTFChars(env, name, cname);
      return NULL;
   }

   /* przydziela pamiêæ potrzebn¹ do przechowania wartoœci */
   cret = (char*)malloc(size);

   /* pobiera wartoœæ */
   if (RegQueryValueEx(hkey, cname, NULL, &type, cret, &size) != ERROR_SUCCESS)
   {  
      (*env)->ThrowNew(env, (*env)->FindClass(env, "Win32RegKeyException"),
         "Query value key failed");
      free(cret);
      RegCloseKey(hkey);
      (*env)->ReleaseStringUTFChars(env, name, cname);
      return NULL;
   }

   /* w zale¿noœci od typu wartoœci przechowuje j¹ jako
      ³añcuch znaków, liczbê ca³kowit¹ lub tablicê bajtów */
   if (type == REG_SZ)
   {  
      ret = (*env)->NewStringUTF(env, cret);
   }
   else if (type == REG_DWORD)
   {  
      jclass class_Integer = (*env)->FindClass(env, "java/lang/Integer");
      /* pobiera identyfikator konstruktora */
      jmethodID id_Integer = (*env)->GetMethodID(env, class_Integer, "<init>", "(I)V");
      int value = *(int*) cret;
      /* wywo³uje konstruktor */
      ret = (*env)->NewObject(env, class_Integer, id_Integer, value);
   }
   else if (type == REG_BINARY)
   {  
      ret = (*env)->NewByteArray(env, size);
      (*env)->SetByteArrayRegion(env, (jarray) ret, 0, size, cret);
   }
   else
   {  
      (*env)->ThrowNew(env, (*env)->FindClass(env, "Win32RegKeyException"),
         "Unsupported value type");
      ret = NULL;
   }

   free(cret);
   RegCloseKey(hkey);
   (*env)->ReleaseStringUTFChars(env, name, cname);

   return ret;
}

JNIEXPORT void JNICALL Java_Win32RegKey_setValue(JNIEnv* env, jobject this_obj, 
   jstring name, jobject value)
{  
   const char* cname;
   jstring path;
   const char* cpath;
   HKEY hkey;
   DWORD type;
   DWORD size;
   jclass this_class;
   jclass class_value;
   jclass class_Integer;
   jfieldID id_root;
   jfieldID id_path;
   HKEY root;
   const char* cvalue;
   int ivalue;

   /* ustala klasê */
   this_class = (*env)->GetObjectClass(env, this_obj);

   /* pobiera identyfikatory pól */
   id_root = (*env)->GetFieldID(env, this_class, "root", "I");
   id_path = (*env)->GetFieldID(env, this_class, "path", "Ljava/lang/String;");

   /* pobiera wartoœci pól */
   root = (HKEY)(*env)->GetIntField(env, this_obj, id_root);
   path = (jstring)(*env)->GetObjectField(env, this_obj, id_path);
   cpath = (*env)->GetStringUTFChars(env, path, NULL);

   /* otwiera klucz rejestru */
   if (RegOpenKeyEx(root, cpath, 0, KEY_WRITE, &hkey) != ERROR_SUCCESS)
   {  
      (*env)->ThrowNew(env, (*env)->FindClass(env, "Win32RegKeyException"),
         "Open key failed");
      (*env)->ReleaseStringUTFChars(env, path, cpath);
      return;
   }

   (*env)->ReleaseStringUTFChars(env, path, cpath);
   cname = (*env)->GetStringUTFChars(env, name, NULL);

   class_value = (*env)->GetObjectClass(env, value);
   class_Integer = (*env)->FindClass(env, "java/lang/Integer");
   /* ustala typ obiektu value */
   if ((*env)->IsAssignableFrom(env, class_value, (*env)->FindClass(env, "java/lang/String")))
   {  
      /* jeœli ³añcuch - pobiera wskaŸnik do jego znaków */
      cvalue = (*env)->GetStringUTFChars(env, (jstring) value, NULL);
      type = REG_SZ;
      size = (*env)->GetStringLength(env, (jstring) value) + 1;
   }
   else if ((*env)->IsAssignableFrom(env, class_value, class_Integer))
   {  
      /* jeœli liczba ca³kowita - wywo³uje intValue, aby pobraæ wartoœæ  */
      jmethodID id_intValue = (*env)->GetMethodID(env, class_Integer, "intValue", "()I");
      ivalue = (*env)->CallIntMethod(env, value, id_intValue);
      type = REG_DWORD;
      cvalue = (char*)&ivalue;
      size = 4;
   }
   else if ((*env)->IsAssignableFrom(env, class_value, (*env)->FindClass(env, "[B")))
   {  
      /* jeœli tablica bajtów - pobiera wskaŸnik bajtów */
      type = REG_BINARY;
      cvalue = (char*)(*env)->GetByteArrayElements(env, (jarray) value, NULL);
      size = (*env)->GetArrayLength(env, (jarray) value);
   }
   else
   {  
      /* jeœli jeszcze inny typ, to nie potrafi go obs³u¿yæ  */
      (*env)->ThrowNew(env, (*env)->FindClass(env, "Win32RegKeyException"),
         "Unsupported value type");
      RegCloseKey(hkey);
      (*env)->ReleaseStringUTFChars(env, name, cname);
      return;
   }

   /* Wpisuje wartoœæ do rejestru */
   if (RegSetValueEx(hkey, cname, 0, type, cvalue, size) != ERROR_SUCCESS)
   {  
      (*env)->ThrowNew(env, (*env)->FindClass(env, "Win32RegKeyException"),
         "Set value failed");
   }

   RegCloseKey(hkey);
   (*env)->ReleaseStringUTFChars(env, name, cname);

   /* jeœli wartoœæ by³a ³añcuchem lub tablic¹,
      zwalnia odpowiedni wskaŸnik */
   if (type == REG_SZ)
   {  
      (*env)->ReleaseStringUTFChars(env, (jstring) value, cvalue);
   }
   else if (type == REG_BINARY)
   {  
      (*env)->ReleaseByteArrayElements(env, (jarray) value, (jbyte*) cvalue, 0);
   }
}

/* funkcja pomocnicza wyliczenia nazw */
static int startNameEnumeration(JNIEnv* env, jobject this_obj, jclass this_class)
{  
   jfieldID id_index;
   jfieldID id_count;
   jfieldID id_root;
   jfieldID id_path;
   jfieldID id_hkey;
   jfieldID id_maxsize;

   HKEY root;
   jstring path;
   const char* cpath;
   HKEY hkey;
   DWORD maxsize = 0;
   DWORD count = 0;

   /* pobiera identyfikatory pól */
   id_root = (*env)->GetFieldID(env, this_class, "root", "I");
   id_path = (*env)->GetFieldID(env, this_class, "path", "Ljava/lang/String;");
   id_hkey = (*env)->GetFieldID(env, this_class, "hkey", "I");
   id_maxsize = (*env)->GetFieldID(env, this_class, "maxsize", "I");
   id_index = (*env)->GetFieldID(env, this_class, "index", "I");
   id_count = (*env)->GetFieldID(env, this_class, "count", "I");

   /* pobiera wartoœci pól */
   root = (HKEY)(*env)->GetIntField(env, this_obj, id_root);
   path = (jstring)(*env)->GetObjectField(env, this_obj, id_path);
   cpath = (*env)->GetStringUTFChars(env, path, NULL);

   /* otwiera klucz rejestru */
   if (RegOpenKeyEx(root, cpath, 0, KEY_READ, &hkey) != ERROR_SUCCESS)
   {  
      (*env)->ThrowNew(env, (*env)->FindClass(env, "Win32RegKeyException"),
         "Open key failed");
      (*env)->ReleaseStringUTFChars(env, path, cpath);
      return -1;
   }
   (*env)->ReleaseStringUTFChars(env, path, cpath);

   /* pobiera liczbê nazw i rozmiar najd³u¿szej z nazw */
   if (RegQueryInfoKey(hkey, NULL, NULL, NULL, NULL, NULL, NULL, &count, &maxsize, 
          NULL, NULL, NULL) != ERROR_SUCCESS)
   {  
      (*env)->ThrowNew(env, (*env)->FindClass(env, "Win32RegKeyException"),
         "Query info key failed");
      RegCloseKey(hkey);
      return -1;
   }

   /* nadaje wartoœci polom */
   (*env)->SetIntField(env, this_obj, id_hkey, (DWORD) hkey);
   (*env)->SetIntField(env, this_obj, id_maxsize, maxsize + 1);
   (*env)->SetIntField(env, this_obj, id_index, 0);
   (*env)->SetIntField(env, this_obj, id_count, count);
   return count;
}

JNIEXPORT jboolean JNICALL Java_Win32RegKeyNameEnumeration_hasMoreElements(JNIEnv* env, 
   jobject this_obj)
{  jclass this_class;
   jfieldID id_index;
   jfieldID id_count;
   int index;
   int count;
   /* ustala klasê */
   this_class = (*env)->GetObjectClass(env, this_obj);

   /* pobiera identyfikatory pól */
   id_index = (*env)->GetFieldID(env, this_class, "index", "I");
   id_count = (*env)->GetFieldID(env, this_class, "count", "I");

   index = (*env)->GetIntField(env, this_obj, id_index);
   if (index == -1) /* pierwszy raz? */
   {  
      count = startNameEnumeration(env, this_obj, this_class);
      index = 0;
   }
   else
      count = (*env)->GetIntField(env, this_obj, id_count);
   return index < count;
}

JNIEXPORT jobject JNICALL Java_Win32RegKeyNameEnumeration_nextElement(JNIEnv* env, 
   jobject this_obj)
{  
   jclass this_class;
   jfieldID id_index;
   jfieldID id_hkey;
   jfieldID id_count;
   jfieldID id_maxsize;

   HKEY hkey;
   int index;
   int count;
   DWORD maxsize;

   char* cret;
   jstring ret;

   /* ustala klasê */
   this_class = (*env)->GetObjectClass(env, this_obj);

   /* pobiera identyfikatory pól */
   id_index = (*env)->GetFieldID(env, this_class, "index", "I");
   id_count = (*env)->GetFieldID(env, this_class, "count", "I");
   id_hkey = (*env)->GetFieldID(env, this_class, "hkey", "I");
   id_maxsize = (*env)->GetFieldID(env, this_class, "maxsize", "I");

   index = (*env)->GetIntField(env, this_obj, id_index);
   if (index == -1) /* pierwszy raz?  */
   {  
      count = startNameEnumeration(env, this_obj, this_class);
      index = 0;
   }
   else
      count = (*env)->GetIntField(env, this_obj, id_count);

   if (index >= count) /* koniec wyliczenia */
   {  
      (*env)->ThrowNew(env, (*env)->FindClass(env, "java/util/NoSuchElementException"),
         "past end of enumeration");
      return NULL;
   }

   maxsize = (*env)->GetIntField(env, this_obj, id_maxsize);
   hkey = (HKEY)(*env)->GetIntField(env, this_obj, id_hkey);
   cret = (char*)malloc(maxsize);

   /* wyszukuje nastêpn¹ nazwê */
   if (RegEnumValue(hkey, index, cret, &maxsize, NULL, NULL, NULL, NULL) != ERROR_SUCCESS)
   {  
      (*env)->ThrowNew(env, (*env)->FindClass(env, "Win32RegKeyException"),
         "Enum value failed");
      free(cret);
      RegCloseKey(hkey);
      (*env)->SetIntField(env, this_obj, id_index, count);
      return NULL;
   }

   ret = (*env)->NewStringUTF(env, cret);
   free(cret);

   /* zwiêksza indeks */
   index++;
   (*env)->SetIntField(env, this_obj, id_index, index);

   if (index == count) /* koniec */
   {  
      RegCloseKey(hkey);
   }

   return ret;
}


