// StoryCardDatabaseHelper.java
// SQLiteOpenHelper subclass that defines the app's database
package com.br.android.storycard.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.br.android.storycard.data.DatabaseDescription.Story;

class StoryCardDatabaseHelper extends SQLiteOpenHelper {
   private static final String DATABASE_NAME = "StoryCard.db";
   private static final int DATABASE_VERSION = 1;

   // constructor
   public StoryCardDatabaseHelper(Context context) {
      super(context, DATABASE_NAME, null, DATABASE_VERSION);
   }

   // creates the contacts table when the database is created
   @Override
   public void onCreate(SQLiteDatabase db) {
      // SQL for creating the contacts table
      final String CREATE_STORIES_TABLE =
         "CREATE TABLE " + Story.TABLE_NAME + "(" +
         Story._ID + " integer primary key, " +
         Story.COLUMN_TITULO + " TEXT, " +
         Story.COLUMN_INTERESSADO + " TEXT, " +
         Story.COLUMN_NECESSIDADE + " TEXT, " +
         Story.COLUMN_MOTIVO + " TEXT);";
         db.execSQL(CREATE_STORIES_TABLE); // create the stories table
   }

   // normally defines how to upgrade the database when the schema changes
   @Override
   public void onUpgrade(SQLiteDatabase db, int oldVersion,
                         int newVersion) { }
}

