package com.bigdrum.metronomemate.database;

import static android.provider.BaseColumns._ID;
import static com.bigdrum.metronomemate.database.Constants.AUTHORITY;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

public class DataProvider extends ContentProvider {
   private static final int SETLIST = 1;
   private static final int SETLIST_ID = 2;
   private static final int SONG = 3;
   private static final int SONG_ID = 4;

   /** The MIME type of a directory of events */
   private static final String CONTENT_TYPE
      = "vnd.android.cursor.dir/vnd.example.event";

   /** The MIME type of a single event */
   private static final String CONTENT_ITEM_TYPE
      = "vnd.android.cursor.item/vnd.example.event";

   private DataService dbService;
   private UriMatcher uriMatcher;
   // ...
   

   
   @Override
   public boolean onCreate() {
      uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
      uriMatcher.addURI(AUTHORITY, "setlists", SETLIST);
      uriMatcher.addURI(AUTHORITY, "setlists/#", SETLIST_ID);
      uriMatcher.addURI(AUTHORITY, "songs", SONG);
      uriMatcher.addURI(AUTHORITY, "songs/#", SONG_ID);
      dbService = DataService.getDataService(getContext());
      return true;
   }
   

   
   @Override
   public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String orderBy) {
	   
	   String tableName = null;
	   
      switch (uriMatcher.match(uri)) {
      case SETLIST:
    	  tableName = Constants.SETLIST_TABLE;
    	  break;
      case SETLIST_ID:
    	  tableName = Constants.SETLIST_TABLE;
    	  long id = Long.parseLong(uri.getPathSegments().get(1));
    	  selection = appendRowId(selection, id);
    	  break;
      case SONG:
    	  tableName = Constants.SONG_TABLE;
      }

      // Get the database and run the query
      SQLiteDatabase db = dbService.getReadableDatabase();
      Cursor cursor = db.query(tableName, projection, selection,
            selectionArgs, null, null, orderBy);

      // Tell the cursor what uri to watch, so it knows when its
      // source data changes
      cursor.setNotificationUri(getContext().getContentResolver(),
            uri);
      return cursor;
   }
   

   
   @Override
   public String getType(Uri uri) {
      switch (uriMatcher.match(uri)) {
      case SETLIST:
         return CONTENT_TYPE;
      case SETLIST_ID:
         return CONTENT_ITEM_TYPE;
      default:
         throw new IllegalArgumentException("Unknown URI " + uri);
      }
   }
   

   
   @Override
   public Uri insert(Uri uri, ContentValues values) {
      SQLiteDatabase db = dbService.getWritableDatabase();

      String tableName = Constants.SETLIST_TABLE;
      // Validate the requested uri
      switch (uriMatcher.match(uri)) {
      case SETLIST:
    	  tableName = Constants.SETLIST_TABLE;
    	  break;
      	default:
      		throw new IllegalArgumentException("Unknown URI " + uri);
      }

      // Insert into database
      long id = db.insertOrThrow(tableName, null, values);

      // Notify any watchers of the change
      Uri tableUri = Uri.parse("content://" + Constants.AUTHORITY + "/" + tableName);
      Uri newUri = ContentUris.withAppendedId(tableUri, id);
      getContext().getContentResolver().notifyChange(newUri, null);
      return newUri;
   }
   

   
   @Override
   public int delete(Uri uri, String selection,
         String[] selectionArgs) {
      SQLiteDatabase db = dbService.getWritableDatabase();
      int count;
      switch (uriMatcher.match(uri)) {
      case SETLIST:
         count = db.delete(Constants.SETLIST_TABLE, selection, selectionArgs);
         break;
      case SETLIST_ID:
         long id = Long.parseLong(uri.getPathSegments().get(1));
         count = db.delete(Constants.SETLIST_TABLE, appendRowId(selection, id),
               selectionArgs);
         break;
      default:
         throw new IllegalArgumentException("Unknown URI " + uri);
      }

      // Notify any watchers of the change
      getContext().getContentResolver().notifyChange(uri, null);
      return count;
   }
   

   
   @Override
   public int update(Uri uri, ContentValues values,
         String selection, String[] selectionArgs) {
      SQLiteDatabase db = dbService.getWritableDatabase();
      int count;
      switch (uriMatcher.match(uri)) {
      case SETLIST:
         count = db.update(Constants.SETLIST_TABLE, values, selection,
               selectionArgs);
         break;
      case SETLIST_ID:
         long id = Long.parseLong(uri.getPathSegments().get(1));
         count = db.update(Constants.SETLIST_TABLE, values, appendRowId(
               selection, id), selectionArgs);
         break;
      default:
         throw new IllegalArgumentException("Unknown URI " + uri);
      }

      // Notify any watchers of the change
      getContext().getContentResolver().notifyChange(uri, null);
      return count;
   }
   

   
   /** Append an id test to a SQL selection expression */
   private String appendRowId(String selection, long id) {
      return _ID + "=" + id
            + (!TextUtils.isEmpty(selection)
                  ? " AND (" + selection + ')'
                  : "");
   }
   
   
}