package com.example.kontrol.inventoryapp;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.kontrol.inventoryapp.data.ItemContract.ItemEntry;
import com.example.kontrol.inventoryapp.data.ItemsDbHelper;

public class EditoryActivity extends AppCompatActivity {

    static final String LOG_TAG = EditoryActivity.class.getName();

    static final int REQUEST_IMAGE_CAPTURE = 1;

    static final int EXISTING_URI_LOADER = 0;

    private Button cameraButton;

    private ImageView mImageView;

    private EditText mItemNameText;

    private EditText mItemPriceText;

    private EditText mItemQuantityText;

    private EditText mItemSupplierText;

    private Button mSaleButton;

    private Button mShipmentButton;

    private Uri mItemUri;

    private boolean mItemHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemHasChanged = true;
            return false;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editory);

        mItemNameText = (EditText)findViewById(R.id.item_name) ;
        mItemPriceText = (EditText)findViewById(R.id.item_price);
        mItemQuantityText = (EditText)findViewById(R.id.item_quantity);
        mItemSupplierText = (EditText)findViewById(R.id.item_supplier);

        mImageView = (ImageView)findViewById(R.id.item_photo);

        cameraButton = (Button)findViewById(R.id.picture_button);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCamera();
            }
        });

        mItemNameText.setOnTouchListener(mTouchListener);
        mItemPriceText.setOnTouchListener(mTouchListener);
        mItemQuantityText.setOnTouchListener(mTouchListener);
        mItemSupplierText.setOnTouchListener(mTouchListener);

    }

    private void saveItem(){


        String name = mItemNameText.getText().toString().trim();
        String price = mItemPriceText.getText().toString().trim();
        String quantity = mItemQuantityText.getText().toString().trim();
        String supplier = mItemSupplierText.getText().toString().trim();

        if (TextUtils.isEmpty(name) && TextUtils.isEmpty(price) &&
                TextUtils.isEmpty(quantity) && TextUtils.isEmpty(supplier)) {return;}

        int numPrice = Integer.parseInt(price);



        ContentValues values = new ContentValues();
        values.put(ItemEntry.COLUMN_ITEM_NAME, name);
        values.put(ItemEntry.COLUMN_ITEM_PRICE, numPrice);

        int numQuantity = 0;
        if (!TextUtils.isEmpty(quantity)) {
            numQuantity = Integer.parseInt(quantity);
        }
        values.put(ItemEntry.COLUMN_ITEM_QUANTITY, numQuantity);
        values.put(ItemEntry.COLUMN_ITEM_SUPPLIER, supplier);


        Uri resultUri = getContentResolver().insert(ItemEntry.CONTENT_URI, values);

        if (resultUri == null) {
            Toast.makeText(this, "Error with saving item", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "item saved", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.editor_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                saveItem();
                finish();
                return true;

            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigaate back to parent activity (CatalogActivity)
                if (!mItemHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditoryActivity.this);
                    return true;
                }
                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditoryActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void startCamera(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageView.setImageBitmap(imageBitmap);
        }
    }






    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Discard your changes and quit editing?");
        builder.setPositiveButton("Discard", discardButtonClickListener);
        builder.setNegativeButton("Keep Editing", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
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

    @Override
    public void onBackPressed() {
        // If the item hasn't changed, continue with handling back button press
        if (!mItemHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }



}
