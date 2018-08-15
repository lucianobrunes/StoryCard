// StoryCardProvider.java
// ContentProvider subclass for manipulating the app's database
package com.br.android.storycard.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import com.br.android.storycard.R;
import com.br.android.storycard.data.DatabaseDescription.Story;

public class StoryCardContentProvider extends ContentProvider {
   // used to access the database
   private StoryCardDatabaseHelper dbHelper;

   // UriMatcher helps ContentProvider determine operation to perform
   private static final UriMatcher uriMatcher =
      new UriMatcher(UriMatcher.NO_MATCH);

   // constants used with UriMatcher to determine operation to perform
   private static final int ONE_STORY = 1; // manipulate one story
   private static final int STORIES = 2; // manipulate stories table

   // static block to configure this ContentProvider's UriMatcher
   static {
      // Uri for Story with the specified id (#)
      uriMatcher.addURI(DatabaseDescription.AUTHORITY,
         Story.TABLE_NAME + "/#", ONE_STORY);

      // Uri for Stories table
      uriMatcher.addURI(DatabaseDescription.AUTHORITY,
         Story.TABLE_NAME, STORIES);
   }

   // called when the StoryCardContentProvider is created
   @Override
   public boolean onCreate() {
      // create the StoryCardDatabaseHelper
      dbHelper = new StoryCardDatabaseHelper(getContext());
      return true; // ContentProvider successfully created
   }

   // required method: Not used in this app, so we return null
   @Override
   public String getType(Uri uri) {
      return null;
   }

   // query the database
   @Override
   public Cursor query(Uri uri, String[] projection,
                       String selection, String[] selectionArgs, String sortOrder) {

      // create SQLiteQueryBuilder for querying stories table
      SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
      queryBuilder.setTables(Story.TABLE_NAME);

      switch (uriMatcher.match(uri)) {
         case ONE_STORY: // story with specified id will be selected
            queryBuilder.appendWhere(
               Story._ID + "=" + uri.getLastPathSegment());
            break;
         case STORIES: // all stories will be selected
            break;
         default:
            throw new UnsupportedOperationException(
               getContext().getString(R.string.invalid_query_uri) + uri);
      }

      // execute the query to select one or all stories
      Cursor cursor = queryBuilder.query(dbHelper.getReadableDatabase(),
         projection, selection, selectionArgs, null, null, null);

      // configure to watch for content changes
      cursor.setNotificationUri(getContext().getContentResolver(), uri);
      return cursor;
   }

   // insert a new story in the database
   @Override
   public Uri insert(Uri uri, ContentValues values) {
      Uri newStoryUri = null;

      switch (uriMatcher.match(uri)) {
         case STORIES:
            // insert the new story--success yields new stories's row id
            long rowId = dbHelper.getWritableDatabase().insert(
               Story.TABLE_NAME, null, values);

            // if the story was inserted, create an appropriate Uri;
            // otherwise, throw an exception
            if (rowId > 0) { // SQLite row IDs start at 1
               newStoryUri = Story.buildStoryUri(rowId);

               // notify observers that the database changed
               getContext().getContentResolver().notifyChange(uri, null);
            }
            else
               throw new SQLException(
                  getContext().getString(R.string.insert_failed) + uri);
            break;
         default:
            throw new UnsupportedOperationException(
               getContext().getString(R.string.invalid_insert_uri) + uri);
      }

      return newStoryUri;
   }

   // update an existing story in the database
   @Override
   public int update(Uri uri, ContentValues values,
                     String selection, String[] selectionArgs) {
      int numberOfRowsUpdated; // 1 if update successful; 0 otherwise

      switch (uriMatcher.match(uri)) {
         case ONE_STORY:
            // get from the uri the id of stories to update
            String id = uri.getLastPathSegment();

            // update the story
            numberOfRowsUpdated = dbHelper.getWritableDatabase().update(
               Story.TABLE_NAME, values, Story._ID + "=" + id,
               selectionArgs);
            break;
         default:
            throw new UnsupportedOperationException(
               getContext().getString(R.string.invalid_update_uri) + uri);
      }

      // if changes were made, notify observers that the database changed
      if (numberOfRowsUpdated != 0) {
         getContext().getContentResolver().notifyChange(uri, null);
      }

      return numberOfRowsUpdated;
   }

   // delete an existing story from the database
   @Override
   public int delete(Uri uri, String selection, String[] selectionArgs) {
      int numberOfRowsDeleted;

      switch (uriMatcher.match(uri)) {
         case ONE_STORY:
            // get from the uri the id of story to update
            String id = uri.getLastPathSegment();

            // delete the story
            numberOfRowsDeleted = dbHelper.getWritableDatabase().delete(
               Story.TABLE_NAME, Story._ID + "=" + id, selectionArgs);
            break;
         default:
            throw new UnsupportedOperationException(
               getContext().getString(R.string.invalid_delete_uri) + uri);
      }

      // notify observers that the database changed
      if (numberOfRowsDeleted != 0) {
         getContext().getContentResolver().notifyChange(uri, null);
      }

      return numberOfRowsDeleted;
   }
}

