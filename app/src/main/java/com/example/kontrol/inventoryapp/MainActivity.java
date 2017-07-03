package com.example.kontrol.inventoryapp;




import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.example.kontrol.inventoryapp.data.ItemContract.ItemEntry;
import com.example.kontrol.inventoryapp.data.ItemsDbHelper;



public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MainActivity.class.getName();

    private static final int URI_LOADER = 0;

    private ListView listView;

    private ItemsDbHelper mDbHelper;

    private ItemCursorAdapter itemCursorAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView)findViewById(R.id.itemsList);

        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        itemCursorAdapter = new ItemCursorAdapter(this, null);
        listView.setAdapter(itemCursorAdapter);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditoryActivity.class);
                startActivity(intent);
            }
        });
        mDbHelper = new ItemsDbHelper(this);

        getLoaderManager().initLoader(URI_LOADER, null, this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                Uri currentItemUri = ContentUris.withAppendedId(ItemEntry.CONTENT_URI, id);
                intent.setData(currentItemUri);
                startActivity(intent);
            }
        });

    }

    private void insertItem(){
        //Gets the database in the write mode.
        //SQLiteDatabase db = mDbHelper.getWritableDatabase();

        //Create ContentValues object where the column names are the keys
        //and iDroid's item attributes are the values.
        ContentValues values = new ContentValues();
        values.put(ItemEntry.COLUMN_ITEM_NAME, "iDroid");
        values.put(ItemEntry.COLUMN_ITEM_PRICE, 10000);
        values.put(ItemEntry.COLUMN_ITEM_QUANTITY, 10);
        values.put(ItemEntry.COLUMN_ITEM_SUPPLIER, "Yayvo");

        //long newRowId = db.insert(ItemEntry.TABLE_NAME, null, values);
        Uri resultUri = getContentResolver().insert(ItemEntry.CONTENT_URI, values);
        if(resultUri == null){
            Toast.makeText(this, "Error adding dummy data ", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Dummy data inserted", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertItem();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:

                deleteAllPets();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = { ItemEntry._ID,
                ItemEntry.COLUMN_ITEM_NAME,
                ItemEntry.COLUMN_ITEM_PRICE,
                ItemEntry.COLUMN_ITEM_QUANTITY  };

        return new CursorLoader(this, ItemEntry.CONTENT_URI, projection, null, null, null);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        itemCursorAdapter.swapCursor(data);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        itemCursorAdapter.swapCursor(null);
    }

    private void deleteAllPets(){

        int rowsDeleted = getContentResolver().delete(ItemEntry.CONTENT_URI, null, null);
        Log.v("MainActivity", rowsDeleted + " rows from Items database");
    }
}
