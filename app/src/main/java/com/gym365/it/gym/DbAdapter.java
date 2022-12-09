package com.gym365.it.gym;

/*
 * Asignatura Aplicaciones Moviles - UC3M
 * Update: 13/02/2018.
 *
 * Based in code by Google with Apache License, Version 2.0
 *
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

// Clase adaptadora que nos va a facilitar el uso de la BD
public class DbAdapter {
    private static final String TAG = "APMOV: DbAdapter"; // Usado en los mensajes de Log

    //Nombre de la base de datos, tablas y versión
    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "all_exercises";
    private static final String DATABASE_TABLE_TABLES="tables";
    private static final String DATABASE_TABLE_EXERCISES="exercises_table";
    private static final String DATABASE_TABLE_SERIE="series";
    private static final String DATABASE_ROUTES="all_routes";
    private static final String DATABASE_TABLE_ROUTES="routes";
    private static final int DATABASE_VERSION = 2;

    //campos de la tabla all_exercises de la base de datos
    public static final String KEY_VIDEO = "link_video";
    public static final String KEY_PHOTO = "link_photo";
    public static final String KEY_CATEGORY = "category";
    public static final String KEY_NAME = "name";
    public static final String KEY_ROWID = "_id";

    //campos de la tabla tables de la base de datos
    public static final String KEY_TABLES_HOUR = "hora";
    public static final String KEY_TABLES_SUBTITLE = "subtitulo";
    public static final String KEY_TABLES_DATE = "dia";
    public static final String KEY_TABLES_NAME = "nombre";
    public static final String KEY_TABLES_ID = "_id";

    //campos de la tabla tables de la base de datos
    public static final String KEY_TREPETICIONES = "repeticiones";
    public static final String KEY_TPESO = "peso";
    public static final String KEY_TID_TABLE = "id_table";
    public static final String KEY_TID_ALL_EXERCISE = "id_allexercise";
    public static final String KEY_T_ID = "_id";

    //campos de la tabla series de la base de datos
    public static final String KEY_SERIE_WEIGHT = "weith";
    public static final String KEY_SERIE_REPES = "repes";
    public static final String KEY_SERIE_SERIE = "series";
    public static final String KEY_SERIE_ID_ITEM = "id_item";
    public static final String KEY_SERIE_ID = "_id";

    //campos de la tabla de rutas de la base de datos
    public static final String KEY_ALL_ROUTES_ROWID = "_id";
    public static final String KEY_ALL_ROUTES_NAME = "name";

    //campos de la tabla de rutas de la base de datos
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_ROUTES_ID = "_id";
    public static final String KEY_ALL_ROUTES_ID = "id_route";

    //Creación de all_exercises
    private static final String DATABASE_CREATE = "create table " + DATABASE_TABLE + " (" +
            KEY_ROWID +" integer primary key autoincrement, " +
            KEY_NAME +" text not null, " +
            KEY_CATEGORY +" text not null, " +
            KEY_PHOTO +" text not null, " +
            KEY_VIDEO + " text );";
    //Creación de tables
    private static final String DATABASE_TABLES_CREATE = "create table " + DATABASE_TABLE_TABLES + " (" +
            KEY_TABLES_ID +" integer primary key autoincrement, " +
            KEY_TABLES_NAME +" text not null, " +
            KEY_TABLES_SUBTITLE + " text, " +
            KEY_TABLES_HOUR + " text, " +
            KEY_TABLES_DATE + " text );";
    //Creación de exercises table
    private static final String DATABASE_EXERCISE_CREATE = "create table " + DATABASE_TABLE_EXERCISES + " (" +
            KEY_T_ID + " integer primary key autoincrement, " +
            KEY_TID_TABLE +" integer not null, " +
            KEY_TID_ALL_EXERCISE + " integer not null, " +
            KEY_TPESO +" text, " +
            KEY_TREPETICIONES + " text, " +
            " FOREIGN KEY (" + KEY_TID_TABLE + ") REFERENCES " + DATABASE_TABLE_TABLES + "(" + KEY_TABLES_ID +")," +
            " FOREIGN KEY (" + KEY_TID_ALL_EXERCISE +") REFERENCES " + DATABASE_TABLE + "(" + KEY_ROWID + "));";

    private static final String DATABASE_SERIE_CREATE= "create table " + DATABASE_TABLE_SERIE + " (" +
            KEY_SERIE_ID + " integer primary key autoincrement, " +
            KEY_SERIE_ID_ITEM +" integer, " +
            KEY_SERIE_SERIE + " integer, " +
            KEY_SERIE_REPES +" integer, " +
            KEY_SERIE_WEIGHT + " integer, " +
            " FOREIGN KEY (" + KEY_SERIE_ID_ITEM +") REFERENCES " + DATABASE_TABLE_EXERCISES + "(" + KEY_TABLES_ID + "));";

    private static final String DATABASE_ALL_ROUTES_CREATE = "create table " + DATABASE_ROUTES + " (" +
            KEY_ALL_ROUTES_ROWID +" integer primary key autoincrement, " +
            KEY_ALL_ROUTES_NAME + " text );";

    private static final String DATABASE_ROUTES_CREATE = "create table " + DATABASE_TABLE_ROUTES + " (" +
            KEY_ROUTES_ID +" integer not null primary key autoincrement, " +
            KEY_LATITUDE + " integer, " +
            KEY_LONGITUDE + " integer, " +
            KEY_ALL_ROUTES_ID + " integer, " +
            " FOREIGN KEY (" + KEY_ALL_ROUTES_ID +") REFERENCES " + DATABASE_ROUTES + "(" + KEY_ALL_ROUTES_ROWID + "));";

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private final Context mCtx;


    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE);
            db.execSQL(DATABASE_TABLES_CREATE);
            db.execSQL(DATABASE_EXERCISE_CREATE);
            db.execSQL(DATABASE_SERIE_CREATE);
            db.execSQL(DATABASE_ALL_ROUTES_CREATE);
            db.execSQL(DATABASE_ROUTES_CREATE);
        }

        // Sobreescribir el siguiente método y poner disableWriteAheadLogging es necesario a partir
        // de API 28
        @Override
        public void onOpen(SQLiteDatabase db) {
            super.onOpen(db);
            db.disableWriteAheadLogging();
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_TABLES);
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_EXERCISES);
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_SERIE);
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_ROUTES);
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_ROUTES);
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     *
     * @param ctx the Context within which to work
     */

    public DbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the notes database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     *
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public DbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }


    /**
     * Create a new note using the title and body provided. If the note is
     * successfully created return the new rowId for that note, otherwise return
     * a -1 to indicate failure.
     *
     * @param name the name of the exercise
     * @param date the category of the exercise
     * @param hour the category of the exercise
     * @return rowId or -1 if failed
     */
    public long createtable(String name, String date, String subtitle, String hour) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TABLES_NAME, name);
        initialValues.put(KEY_TABLES_DATE, date);
        initialValues.put(KEY_TABLES_SUBTITLE, subtitle);
        initialValues.put(KEY_TABLES_HOUR, hour);

        return mDb.insert(DATABASE_TABLE_TABLES, null, initialValues);
    }
    /**
     *
     * @param rowId id of note to update
     * @param date value to set note title to
     * @param subtitle value to set note body to
     * @return true if the note was successfully updated, false otherwise
     */
    public long updateTable(long rowId, String name, String date, String subtitle, String hour) {
        ContentValues args = new ContentValues();
        args.put(KEY_TABLES_NAME, name);
        args.put(KEY_TABLES_DATE, date);
        args.put(KEY_TABLES_SUBTITLE, subtitle);
        args.put(KEY_TABLES_HOUR, hour);

        return mDb.insert(DATABASE_TABLE_TABLES, null, args);

    }

    /**
     * Delete the note with the given rowId
     *
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteNote(long rowId) {

        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }
    public boolean delete_all_exercises_from_table(long rowId) {
        return mDb.delete(DATABASE_TABLE_EXERCISES, KEY_TID_TABLE + "=" + rowId, null) > 0;
    }
    public boolean delete_table(long rowId) {
        return mDb.delete(DATABASE_TABLE_TABLES, KEY_TABLES_ID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all notes in the database
     *
     * @return Cursor over all notes
     */
    public Cursor fetchAllTables() {

        return mDb.query(DATABASE_TABLE_TABLES, new String[] {KEY_TABLES_ID, KEY_TABLES_NAME, KEY_TABLES_DATE, KEY_TABLES_SUBTITLE, KEY_TABLES_HOUR}, null, null, null, null, null);
    }

    /**
     * Return a Cursor positioned at the note that matches the given rowId
     *
     * @param category id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetch_exercise(String category) throws SQLException {


        return mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                        KEY_NAME, KEY_CATEGORY, KEY_PHOTO, KEY_VIDEO}, KEY_CATEGORY + "=" + category, null,
                null, null, null, null);

    }
    /**
     * Return a Cursor positioned at the note that matches the given rowId
     *
     * @param rowid id of exercise to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetch_table(long rowid) throws SQLException {


        Cursor mCursor =

                mDb.query(true, DATABASE_TABLE_TABLES, new String[] {KEY_TABLES_ID,KEY_TABLES_NAME,KEY_TABLES_SUBTITLE,
                        KEY_TABLES_DATE, KEY_TABLES_HOUR}, KEY_ROWID + "=" + rowid, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
    /**
     * Return a Cursor positioned at the note that matches the given rowId
     *
     * @param date day of table to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchDateTable(String date) throws SQLException {


        Cursor mCursor =

                mDb.query(true, DATABASE_TABLE_TABLES, new String[] {KEY_TABLES_ID,KEY_TABLES_NAME,KEY_TABLES_SUBTITLE,
                                KEY_TABLES_DATE, KEY_TABLES_HOUR}, KEY_TABLES_DATE + "=" + date, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
    public Cursor fetch_repes_weight(long _id) throws SQLException {

        String query = "SELECT  repes,weith FROM series WHERE  _id  = "+ _id;
        Cursor mCursor = mDb.rawQuery(query, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }



    public Cursor fetch_exercises_in_table(String name_table) throws SQLException {
        /*Esta función selecciona el nombre de los ejercicios dentro de una determinada tabla*/
        String query =  "SELECT exercises_table._id, name, category, all_exercises.link_photo FROM exercises_table INNER JOIN tables ON exercises_table.id_table = tables._id  INNER JOIN all_exercises ON exercises_table.id_allexercise = all_exercises._id  where tables.nombre="+"'"+name_table+"';";

        Cursor mCursor = mDb.rawQuery(query,null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    public Cursor fetch_exercise_in_table_id(long id) throws SQLException {
        /*Esta función selecciona el nombre de los ejercicios dentro de una determinada tabla*/
            String query =  "SELECT exercises_table._id, all_exercises.link_photo, name, category FROM exercises_table INNER JOIN tables ON exercises_table.id_table = tables._id  INNER JOIN all_exercises ON exercises_table.id_allexercise = all_exercises._id  where exercises_table._id= "+id+";";

        Cursor mCursor = mDb.rawQuery(query,null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
    public Cursor fetch_exercise_ID_byName(String name) throws SQLException {
        String query =  "SELECT all_exercises._id, all_exercises.link_photo FROM all_exercises WHERE name="+name;
        Cursor mCursor= mDb.rawQuery(query,null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
    public Cursor fetch_exercise_byName(String name) throws SQLException {
        String query =  "SELECT all_exercises._id, all_exercises.name, all_exercises.category, all_exercises.link_photo FROM all_exercises WHERE name='"+name+"'";
        Cursor mCursor= mDb.rawQuery(query,null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
    public long addExercise(Long id_table, Long id_exercise){
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TID_TABLE, id_table);
        initialValues.put(KEY_TID_ALL_EXERCISE,id_exercise);

        return mDb.insert(DATABASE_TABLE_EXERCISES, null, initialValues);
    }

    public Cursor fetch_id_tables(long id_table, long id_allexercise) throws SQLException {

        String query = "SELECT  _id FROM exercises_table WHERE  id_table  = "+ id_table + " and id_allexercise = " + id_allexercise;
        Cursor mCursor = mDb.rawQuery(query, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    public boolean updateTable_AddExercise(long rowId,long id_exercise){
        ContentValues args = new ContentValues();
        args.put(KEY_TID_ALL_EXERCISE, id_exercise);

        return mDb.update(DATABASE_EXERCISE_CREATE, args, KEY_T_ID + "=" + rowId, null) > 0;
    }
    public Cursor fetch_exercise_name_byId(long id) throws SQLException {
        String query =  "SELECT all_exercises._id,all_exercises.name,all_exercises.category, all_exercises.link_photo FROM all_exercises WHERE _id="+id;
        Cursor mCursor= mDb.rawQuery(query,null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
    public Cursor fetch_serie(long id_item) throws SQLException {
        String query =  "SELECT _id,series, repes, weith  FROM series WHERE id_item = " + id_item;
        Cursor mCursor= mDb.rawQuery(query,null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
    public Cursor fetch_id_tabla_with_exercise() throws SQLException{

        String query =  "SELECT  max(_id) as _id FROM exercises_table" ;
        Cursor mCursor = mDb.rawQuery(query,null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    public Cursor fetch_id_tableMax() throws SQLException{

        String query =  "SELECT  max(_id) as _id FROM tables" ;
        Cursor mCursor = mDb.rawQuery(query,null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    public Cursor fetch_id_serieMax_to_id_item(long id) throws SQLException{

            String query =  "SELECT  max(series) as series FROM series WHERE id_item = " + id;
            Cursor mCursor = mDb.rawQuery(query,null);
            if (mCursor != null) {
                mCursor.moveToFirst();
            }
            return mCursor;


    }
    public long addSerie(long number_repetitions, long number_kilos, long id_serie_item, long id_series){
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_SERIE_REPES, number_repetitions);
        initialValues.put(KEY_SERIE_WEIGHT,number_kilos);
        initialValues.put(KEY_SERIE_ID_ITEM,id_serie_item);
        initialValues.put(KEY_SERIE_SERIE,id_series);

        return mDb.insert(DATABASE_TABLE_SERIE, null, initialValues);
    }
    public Cursor updateTable_AddSerie(long rowId,long id_serie,long id_repes, long id_weight, long id_item) {
        String query = "UPDATE series SET repes = " + id_repes + " , weith = " + id_weight + " WHERE _id= " + rowId ;
        Cursor mCursor = mDb.rawQuery(query, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    /**
     * Return a Cursor over the list of all routes in the database
     *
     * @return Cursor over all routes
     */
    public Cursor fetchAllRoutes() {

        return mDb.query(DATABASE_ROUTES, new String[] {KEY_ALL_ROUTES_ROWID, KEY_ALL_ROUTES_NAME}, null, null, null, null, null);
    }

    /**
     * Return a Cursor positioned at the note that matches the given rowId
     *
     * @param id of exercise to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetch_route(long id) throws SQLException {

        String query = "SELECT routes.latitude, routes.longitude FROM routes INNER JOIN all_routes ON routes.id_route=all_routes._id WHERE all_routes._id="+id+";";

        Cursor mCursor = mDb.rawQuery(query,null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    /**
     * Create a new note using the title and body provided. If the note is
     * successfully created return the new rowId for that note, otherwise return
     * a -1 to indicate failure.
     *
     * @param name of the route
     * @return rowId or -1 if failed
     */
    public long createRoute (String name) {
        ContentValues initialValues = new ContentValues();

        initialValues.put(KEY_ALL_ROUTES_NAME, name);

        return mDb.insert(DATABASE_ROUTES, null, initialValues);
    }

    public boolean delete_route(long rowId) {
        return mDb.delete(DATABASE_ROUTES, KEY_ALL_ROUTES_ROWID + "=" + rowId, null) > 0;
    }

    public boolean delete_coordinates(long rowId) {
        return mDb.delete(DATABASE_TABLE_ROUTES, KEY_ALL_ROUTES_ID + "=" + rowId, null) > 0;
    }

    /**
     * Create a new note using the title and body provided. If the note is
     * successfully created return the new rowId for that note, otherwise return
     * a -1 to indicate failure.
     *
     * @param lat the latitudes of the exercise
     * @param lng the latitudes of the exercise
     * @return rowId or -1 if failed
     */
    public long putCoordinates(long id, double lat, double lng) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_ALL_ROUTES_ID, id);
        initialValues.put(KEY_LATITUDE, lat);
        initialValues.put(KEY_LONGITUDE, lng);

        return mDb.insert(DATABASE_TABLE_ROUTES, null, initialValues);
    }

}
