package com.example.kontrol.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kontrol.inventoryapp.data.ItemContract.ItemEntry;

/**
 * Created by Kontrol on 4/20/2017.
 */

public class ItemCursorAdapter extends CursorAdapter {

    public ItemCursorAdapter(Context context, Cursor c){
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context).inflate(R.layout.item_list_view, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView nameTextView = (TextView)view.findViewById(R.id.name);
        TextView priceTextView = (TextView)view.findViewById(R.id.price);
        TextView quantityTextView = (TextView)view.findViewById(R.id.quantity_number);


        String db_name = cursor.getString(cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_NAME));
        int db_price = cursor.getInt(cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_PRICE));
        int db_quantity = cursor.getInt(cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_QUANTITY));

        nameTextView.setText(db_name);
        priceTextView.setText(String.valueOf(db_price));
        quantityTextView.setText(String.valueOf(db_quantity));

    }


}
