package com.application.example.storeinventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.application.example.storeinventory.data.StoreContract;

import static  com.application.example.storeinventory.data.StoreContract.StoreEntry.COLUMN_NAME;
import static  com.application.example.storeinventory.data.StoreContract.StoreEntry.COLUMN_PRICE;


public class StoreCursorAdapter extends CursorAdapter {

    /**
     * Construct {@link StoreCursorAdapter}
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public StoreCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the it inventory data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current it device can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = view.findViewById(R.id.it_name);
        TextView priceTextView = view.findViewById(R.id.it_price);
        TextView quantityTextView = view.findViewById(R.id.it_quantity);

        Button sellButton = view.findViewById(R.id.sell);

        // Get columns we need
        int nameColumnIndex = cursor.getColumnIndex(COLUMN_NAME);
        int priceColumnIndex = cursor.getColumnIndex(COLUMN_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_QUANTITY);

        // Read the it device attributes from the Cursor for current product
        final String productName = cursor.getString(nameColumnIndex);
        final String itPrice = cursor.getString(priceColumnIndex);
        String itQuantity = cursor.getString(quantityColumnIndex);

        // Update the TextViews with the attributes for current product
        nameTextView.setText(productName);
        priceTextView.setText(itPrice);
        quantityTextView.setText(itQuantity);

        final int idColumnIndex = cursor.getInt(cursor.getColumnIndex(StoreContract.StoreEntry._ID));
        final int currentQuantityColumnIndex = cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_QUANTITY);
        final int currentQuantity = Integer.valueOf(cursor.getString(currentQuantityColumnIndex));

        //Sell button which decrease quantity
        sellButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (currentQuantity > 0) {
                    int newCurrentQuantity = currentQuantity - 1;
                    Uri quantityUri = ContentUris.withAppendedId(StoreContract.StoreEntry.CONTENT_URI, idColumnIndex);

                    ContentValues values = new ContentValues();
                    values.put(StoreContract.StoreEntry.COLUMN_QUANTITY, newCurrentQuantity);
                    context.getContentResolver().update(quantityUri, values, null, null);

                    Toast.makeText(context, "The sale was successful! \nThe new quantity for "+ productName + " is: " + newCurrentQuantity, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "You can't sell, because " + productName + " is out of stock!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}