// DatabaseDescription.java
// Describes the table name and column names for this app's database,
// and other information required by the ContentProvider
package com.br.android.storycard.data;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class DatabaseDescription {
   // ContentProvider's name: typically the package name
   public static final String AUTHORITY =
      "com.br.android.storycard.data";

   // base URI used to interact with the ContentProvider
   private static final Uri BASE_CONTENT_URI =
      Uri.parse("content://" + AUTHORITY);

   // nested class defines contents of the stories table
   public static final class Story implements BaseColumns {
      public static final String TABLE_NAME = "stories"; // table's name

      // Uri for the stories table
      public static final Uri CONTENT_URI =
         BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

      // column names for stories table's columns
      public static final String COLUMN_TITULO = "titulo";
      public static final String COLUMN_INTERESSADO = "interessado";
      public static final String COLUMN_NECESSIDADE = "necessidade";
      public static final String COLUMN_MOTIVO = "motivo";

      // creates a Uri for a specific story
      public static Uri buildStoryUri(long id) {
         return ContentUris.withAppendedId(CONTENT_URI, id);
      }
   }
}

