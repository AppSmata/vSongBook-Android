package com.jackson_siro.visongbook.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.jackson_siro.visongbook.models.*;
import com.jackson_siro.visongbook.retrofitconfig.*;

public class SQLiteHelper extends SQLiteOpenHelper {

    public static SQLiteDatabase db;
    String DB_PATH;
    private Context context;

    public SQLiteHelper(Context context) {
        super(context, Utils.DATABASE_NAME, null, Utils.DATABASE_VERSION);
        DB_PATH = BaseUrlConfig.SqlDbPath;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(Utils.CREATE_BOOKS_TABLE_SQL);
        sqLiteDatabase.execSQL(Utils.CREATE_SONGS_TABLE_SQL);
        sqLiteDatabase.execSQL(Utils.CREATE_USERS_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Utils.TBL_BOOKS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Utils.TBL_SONGS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Utils.TBL_USERS);
        onCreate(sqLiteDatabase);
    }

    public void addBook(CategoryModel book){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Utils.CATEGORYID, book.categoryid);
        values.put(Utils.TITLE, book.title);
        values.put(Utils.TAGS, book.tags);
        values.put(Utils.QCOUNT, book.qcount);
        values.put(Utils.POSITION, book.position);
        values.put(Utils.CONTENT, book.content);
        values.put(Utils.BACKPATH, book.backpath);

        try{
            db.insert(Utils.TBL_BOOKS, null, values);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        db.close();
    }

    public void addSong(PostModel song) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Utils.POSTID, song.postid);
        values.put(Utils.BOOKID, song.bookid);
        values.put(Utils.CATEGORYID, song.categoryid);
        values.put(Utils.BASETYPE, song.basetype);
        values.put(Utils.NUMBER, song.number);
        values.put(Utils.ALIAS, song.alias);
        values.put(Utils.TITLE, song.title);
        values.put(Utils.TAGS, song.tags);
        values.put(Utils.CONTENT, song.content);
        values.put(Utils.CREATED, song.created);
        values.put(Utils.WHAT, song.what);
        values.put(Utils.WHEN, song.what);
        values.put(Utils.WHERE, song.what);
        values.put(Utils.WHO, song.what);
        values.put(Utils.NETTHUMBS, song.netthumbs);
        values.put(Utils.VIEWS, song.views);
        values.put(Utils.ACOUNT, song.acount);
        values.put(Utils.USERID, song.userid);

        try{
            db.insert(Utils.TBL_SONGS, null, values);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        db.close();
    }

    public List<CategoryModel> getBookList() {
        List<CategoryModel> SongBooks = new LinkedList<CategoryModel>();
        String query = "SELECT * FROM " + Utils.TBL_BOOKS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                CategoryModel book = new CategoryModel(
                    Integer.parseInt(cursor.getString(0)),
                    Integer.parseInt(cursor.getString(1)),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getString(7)
                );
                SongBooks.add(book);
            } while (cursor.moveToNext());
        }
        return SongBooks;
    }

    public List<SearchModel> searchSongs(String searchthis){
        List<SearchModel> SearchLists = new ArrayList<SearchModel>();
        String whereQuery = "";

        if (searchthis.length() > 1) {
            if (TextUtils.isDigitsOnly(searchthis))
                whereQuery = " WHERE " + Utils.NUMBER + "=" + searchthis;
            else
                whereQuery = " WHERE " + Utils.TITLE + " LIKE '%" + searchthis + "%' OR " + Utils.CONTENT + " LIKE '%" + searchthis + "%'";
        }

        String fullQuery = "SELECT  * FROM " + Utils.TBL_SONGS + whereQuery + " ORDER BY " + Utils.NUMBER + " LIMIT 30";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(fullQuery, null);
        SearchModel searchresult;
        if (cursor.moveToFirst()) {
            do {
                searchresult = new SearchModel(cursor.getString(7), Integer.parseInt(cursor.getString(0)));
                SearchLists.add(searchresult);
            } while (cursor.moveToNext());
        }

        return SearchLists;
    }

    public List<PostModel> searchForSongs(String searchthis) {
        List<PostModel> SongsList = new LinkedList<PostModel>();
        String whereQuery = "";

        if (searchthis.length() > 1) {
            if (TextUtils.isDigitsOnly(searchthis)) whereQuery = " WHERE as_songs." + Utils.NUMBER + "=" + searchthis;
            else
                whereQuery = " WHERE as_songs." + Utils.TITLE + " LIKE '%" + searchthis +
                        "%' OR as_songs." + Utils.CONTENT + " LIKE '%" + searchthis + "%'";
        }

        String fullQuery = "SELECT songid, as_songs.bookid, number, alias, as_songs.title, as_songs.tags, as_songs.content, as_books.title " +
                "FROM " + Utils.TBL_SONGS + " INNER JOIN as_books ON as_books.categoryid = as_songs.categoryid" +
                whereQuery + " ORDER BY as_songs." + Utils.NUMBER;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(fullQuery, null);
        PostModel song;
        if (cursor.moveToFirst()) {
            do {
                song = new PostModel();
                song.songid = Integer.parseInt(cursor.getString(0));
                song.bookid = cursor.getInt(1);
                song.number = cursor.getInt(2);
                song.alias = cursor.getString(3);
                song.title = cursor.getString(4);
                song.tags = cursor.getString(5);
                song.content = cursor.getString(6);
                song.categoryname = cursor.getString(7);

                SongsList.add(song);
            } while (cursor.moveToNext());
        }
        return SongsList;
    }

    public List<PostModel> getSongList(int songbook) {
        List<PostModel> SongsList = new LinkedList<PostModel>();
        String whereQuery = (songbook == 0) ? "" : " WHERE as_songs." + Utils.CATEGORYID + "=" + songbook;
        String fullQuery = "SELECT songid, as_songs.bookid, number, alias, as_songs.title, as_songs.tags, as_songs.content, as_books.title " +
                "FROM " + Utils.TBL_SONGS +
                " INNER JOIN as_books ON as_books.categoryid = as_songs.categoryid" +
                whereQuery + " ORDER BY as_songs." + Utils.NUMBER;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(fullQuery, null);
        PostModel song;
        if (cursor.moveToFirst()) {
            do {
                song = new PostModel();
                song.songid = Integer.parseInt(cursor.getString(0));
                song.bookid = cursor.getInt(1);
                song.number = cursor.getInt(2);
                song.alias = cursor.getString(3);
                song.title = cursor.getString(4);
                song.tags = cursor.getString(5);
                song.content = cursor.getString(6);
                song.categoryname = cursor.getString(7);

                SongsList.add(song);
            } while (cursor.moveToNext());
        }
        return SongsList;
    }

    public PostModel viewSong(int songid) {
        String query = "SELECT songid, as_songs.bookid, number, alias, as_songs.title, as_songs.tags, as_songs.content, as_books.title " +
                "FROM " + Utils.TBL_SONGS +
                " INNER JOIN as_books ON as_books.categoryid = as_songs.categoryid WHERE " + Utils.SONGID + "=" + songid;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null) cursor.moveToFirst();

        PostModel song = new PostModel();
        song.songid = Integer.parseInt(cursor.getString(0));
        song.bookid = cursor.getInt(1);
        song.number = cursor.getInt(2);
        song.alias = cursor.getString(3);
        song.title = cursor.getString(4);
        song.tags = cursor.getString(5);
        song.content = cursor.getString(6);
        song.categoryname = cursor.getString(7);

        return song;
    }

    public String getBookName(int bookid) {
        String query = "SELECT " + Utils.TITLE + " FROM " + Utils.TBL_BOOKS + " WHERE " + Utils.BOOKID + "=" + bookid;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null) cursor.moveToFirst();
        return cursor.getString(0);
    }

    public ArrayList<ArrayList<Object>> getSongsAll(){
        ArrayList<ArrayList<Object>> dataArray = new ArrayList<ArrayList<Object>>();
        db = this.getReadableDatabase();
        try{
            Cursor cursor = db.query(
                    Utils.TBL_SONGS, new String[]{Utils.SONGID, Utils.POSTID, Utils.BOOKID, Utils.CATEGORYID, Utils.BASETYPE,
                            Utils.TITLE, Utils.TAGS, Utils.CONTENT, Utils.CREATED, Utils.WHAT, Utils.WHEN,
                            Utils.WHERE, Utils.NETTHUMBS, Utils.VIEWS, Utils.ACOUNT, Utils.USERID }
                    , null, null, null, null, Utils.SONGID + " ASC"
            );
           
            if (cursor.moveToFirst()){
                do {
                    ArrayList<Object> dataList = new ArrayList<Object>();
                    dataList.add(cursor.getLong(0));
					dataList.add(cursor.getString(1));
					dataList.add(cursor.getString(2));
					dataList.add(cursor.getString(3));
					dataList.add(cursor.getString(4));
					dataList.add(cursor.getString(5));
					dataList.add(cursor.getString(6));
					dataList.add(cursor.getString(7));
					dataList.add(cursor.getString(8));
					dataList.add(cursor.getString(9));
					dataList.add(cursor.getString(10));
					dataList.add(cursor.getString(11));
					dataList.add(cursor.getString(12));
					dataList.add(cursor.getString(13));
					dataList.add(cursor.getString(14));
					dataList.add(cursor.getString(15));
					dataList.add(cursor.getString(16));
                    dataArray.add(dataList);
                }while (cursor.moveToNext());
            }

            cursor.close();
        }catch (SQLiteException ex){
            ex.printStackTrace();
        }

        return dataArray;
    }

    public ArrayList<ArrayList<Object>> getAllDataOne(int id){
        ArrayList<ArrayList<Object>> dataArray = new ArrayList<ArrayList<Object>>();
        db = this.getReadableDatabase();

        try{
            Cursor cursor = db.query(
                    Utils.TBL_SONGS, new String[]{Utils.POSTID, Utils.TITLE, Utils.CATEGORYID}
                    , Utils.POSTID+"=?"+id, null, null, null, Utils.POSTID + " ASC"
            );

            if (cursor.moveToFirst()){
                do {
                    ArrayList<Object> dataList = new ArrayList<Object>();
                    dataList.add(cursor.getLong(0));
                    dataList.add(cursor.getString(1));
                    dataList.add(cursor.getString(2));

                    dataArray.add(dataList);
                }while (cursor.moveToNext());
            }

            cursor.close();
        }catch (SQLiteException ex){
            ex.printStackTrace();
        }

        return dataArray;
    }

    public boolean isDataExist(long id){
        db = this.getReadableDatabase();
        boolean existDatabase = false;

        try{
            Cursor cursor = db.query(Utils.TBL_SONGS, new String[]
                    {Utils.POSTID}, Utils.POSTID +"="+id
            ,null, null, null, null, null);
            if (cursor.getCount() > 0){
                existDatabase = true;
            }

            cursor.close();
        }catch (SQLException ex){
            ex.printStackTrace();
        }

        return existDatabase;
    }

    public boolean isPreviousDataExist(){
        boolean exist = false;
        try{
            Cursor cursor = db.query(Utils.TBL_SONGS,
                    new String[]{Utils.POSTID}, null, null, null, null, null);

            if (cursor.getCount() > 0){
                exist = true;
            }
        }catch (SQLException ex){
            ex.printStackTrace();
        }

        return exist;
    }

    public void deleteData(long id){
        try {
            db.delete(Utils.TBL_SONGS, Utils.POSTID+"="+id, null);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void deleteAllData(){
        try {
            db.delete(Utils.TBL_SONGS, null, null);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void close(){
        db.close();
    }

    public long getUpdateCountWish(){
        db = this.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, Utils.TBL_SONGS);
        db.close();
        return count;
    }
}
