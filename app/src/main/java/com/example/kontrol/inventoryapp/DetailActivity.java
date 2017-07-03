package com.example.kontrol.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kontrol.inventoryapp.data.ItemContract.ItemEntry;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    static final String LOG_TAG = DetailActivity.class.getName();

    private TextView mNameText;

    private TextView mPriceText;

    private TextView mQuantityText;

    private TextView mSupplierText;

    private ImageView mItemPhoto;

    Uri mDetailUri;

    static final int EXISTING_URI_LOADER = 0;

    private Button saleButton;

    private Button shipmentButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mNameText = (TextView)findViewById(R.id.detail_item_name);
        mPriceText = (TextView)findViewById(R.id.detail_item_price);
        mQuantityText = (TextView)findViewById(R.id.detail_item_quantity);
        mSupplierText = (TextView)findViewById(R.id.detail_item_supplier);

        Intent intent = getIntent();
        mDetailUri = intent.getData();

        getLoaderManager().initLoader(EXISTING_URI_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {

            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = { ItemEntry._ID,
                ItemEntry.COLUMN_ITEM_NAME,
                ItemEntry.COLUMN_ITEM_PRICE,
                ItemEntry.COLUMN_ITEM_QUANTITY,
                ItemEntry.COLUMN_ITEM_SUPPLIER  };

        return new CursorLoader(this, mDetailUri, projection, null, null, null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if(cursor == null || cursor.getCount() < 1){
            return;
        }

        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_NAME);
            int priceColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_SUPPLIER);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            
            String supplier = cursor.getString(supplierColumnIndex);

            // Update the views on the screen with the values from the database
            mNameText.setText(name);
            mPriceText.setText(Integer.toString(price));
            mQuantityText.setText(Integer.toString(quantity));
            mSupplierText.setText(supplier);


        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameText.setText("");
        mPriceText.setText("");
        mQuantityText.setText("");
        mSupplierText.setText("");
    }

    private void deleteItem(){
        int rowsDeleted = getContentResolver().delete(mDetailUri, null, null);

        // Show a toast message depending on whether or not the delete was successful.
        if (rowsDeleted == 0) {
            // If no rows were deleted, then there was an error with the delete.
            Toast.makeText(this, "Error with deleting pet",
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the delete was successful and we can display a toast.
            Toast.makeText(this,"Pet deleted",
                    Toast.LENGTH_SHORT).show();
        }

        finish();
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete this pet?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteItem();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


}
