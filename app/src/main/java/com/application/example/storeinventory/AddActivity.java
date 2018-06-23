package com.application.example.storeinventory;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.application.example.storeinventory.data.StoreContract;

import java.util.Locale;

import static com.application.example.storeinventory.data.StoreContract.StoreEntry.COLUMN_NAME;
import static com.application.example.storeinventory.data.StoreContract.StoreEntry.COLUMN_PRICE;
import static com.application.example.storeinventory.data.StoreContract.StoreEntry.COLUMN_QUANTITY;
import static com.application.example.storeinventory.data.StoreContract.StoreEntry.COLUMN_SUP_NAME;
import static com.application.example.storeinventory.data.StoreContract.StoreEntry.COLUMN_SUP_PHONE;

public class AddActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    // Tracking of db changes
    private boolean storeHasChanged = false;

    // Store loader identifier
    private static final int EXISTING_STORE_LOADER = 0;

    private Uri storeCurrentUri;

    // Private Variables to hold db info
    private EditText storeName;
    private EditText storePrice;
    private EditText storeQuantity;
    private EditText storeSupplierName;
    private EditText storeSupplierPhone;

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            storeHasChanged = true;
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // determine store creation
        Intent intent = getIntent();
        storeCurrentUri = intent.getData();

        // check if new uri is being created
        if (storeCurrentUri == null) {
            setTitle("Add a new product");

            // Hide delete option
            invalidateOptionsMenu();
        } else {
            setTitle("Edit product");

            getLoaderManager().initLoader(EXISTING_STORE_LOADER, null, this);
        }

        Button increase_button = findViewById(R.id.increase_button);
        Button decrease_button = findViewById(R.id.decrease_button);
        Button order_button = findViewById(R.id.order_button);
        storeName = findViewById(R.id.productName);
        storePrice = findViewById(R.id.productPrice);
        storeQuantity = findViewById(R.id.productQuantity);
        storeSupplierName = findViewById(R.id.productSupplier);
        storeSupplierPhone = findViewById(R.id.productSupplierPhone);

        storeName.setOnTouchListener(touchListener);
        storePrice.setOnTouchListener(touchListener);
        storeQuantity.setOnTouchListener(touchListener);
        storeSupplierName.setOnTouchListener(touchListener);
        storeSupplierPhone.setOnTouchListener(touchListener);
        increase_button.setOnTouchListener(touchListener);
        decrease_button.setOnTouchListener(touchListener);
        order_button.setOnTouchListener(touchListener);

        increase_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantity = storeQuantity.getText().toString();
                if (TextUtils.isEmpty(quantity)) {
                    storeQuantity.setText("1");
                } else {
                    int not_null_quantity = Integer.parseInt(storeQuantity.getText().toString().trim());
                    not_null_quantity++;
                    storeQuantity.setText(String.valueOf(not_null_quantity));
                }
            }
        });

        decrease_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String quantity = storeQuantity.getText().toString();
                if (TextUtils.isEmpty(quantity)) {
                    storeQuantity.setText("0");
                } else {
                    int new_quantity = Integer.parseInt(storeQuantity.getText().toString().trim());
                    if (new_quantity > 0) {
                        new_quantity--;
                        storeQuantity.setText(String.valueOf(new_quantity));
                    } else {
                        Toast.makeText(AddActivity.this, "Cannot go below 0",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        order_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = storeSupplierPhone.getText().toString().trim();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phone));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

    }

    /**
     * Get input from EditText and save to db
     */
    private void insertStoreProduct() {
        // Reads from input fields, trim to eliminate white space
        String nameString = storeName.getText().toString().trim();
        String priceString = storePrice.getText().toString().trim();
        String quantityString = storeQuantity.getText().toString().trim();
        String supplierNameString = storeSupplierName.getText().toString().trim();
        String supplierPhoneString = storeSupplierPhone.getText().toString().trim();

        // Check if it is a new or product already exists
        if (storeCurrentUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(quantityString) && TextUtils.isEmpty(supplierNameString) &&
                TextUtils.isEmpty(supplierPhoneString)) {
            // return with no changes
            Toast.makeText(this, R.string.nothing_was_changed, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (TextUtils.isEmpty(nameString)) {
            storeName.requestFocus();
            storeName.setError(getString(R.string.empty_field_error));
            Toast.makeText(this, getString(R.string.enter_product_name), Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(priceString)) {
            storePrice.requestFocus();
            storePrice.setError(getString(R.string.empty_field_error));
            Toast.makeText(this, getString(R.string.enter_product_price), Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(quantityString)) {
            storeQuantity.requestFocus();
            storeQuantity.setError(getString(R.string.empty_field_error));
            Toast.makeText(this, getString(R.string.enter_product_quantity), Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(supplierNameString)) {
            storeSupplierName.requestFocus();
            storeSupplierName.setError(getString(R.string.empty_field_error));
            Toast.makeText(this, getString(R.string.enter_supplier_name), Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(supplierPhoneString)) {
            storeSupplierPhone.requestFocus();
            storeSupplierPhone.setError(getString(R.string.empty_field_error));
            Toast.makeText(this, getString(R.string.enter_supplier_phone), Toast.LENGTH_LONG).show();
            return;
        }

        // ContentValues object with column names - keys, product att - values
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, nameString);
        float priceFloat = Float.parseFloat(priceString);
        values.put(COLUMN_PRICE, priceFloat);
        values.put(COLUMN_QUANTITY, quantityString);
        values.put(COLUMN_SUP_NAME, supplierNameString);
        values.put(COLUMN_SUP_PHONE, supplierPhoneString);

        // Check for new / existing product
        if (storeCurrentUri == null) {

            Uri newUri = getContentResolver().insert(StoreContract.StoreEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, R.string.save_error,
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.added_msg,
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            int rowsAffected = getContentResolver().update(storeCurrentUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, R.string.updateError,
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, R.string.updateYes,
                        Toast.LENGTH_SHORT).show();
            }
            // Exit activity
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/add_edit_menu.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.add_edit_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new it device, hide the "Delete" menu item.
        if (storeCurrentUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle options menu
        switch (item.getItemId()) {
            case R.id.action_save:
                insertStoreProduct();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                // If unchanged continue navigation
                if (!storeHasChanged) {
                    NavUtils.navigateUpFromSameTask(AddActivity.this);
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
                                NavUtils.navigateUpFromSameTask(AddActivity.this);
                            }
                        };

                // Display dialog that the user has unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the it device hasn't changed, continue with handling back button press
        if (!storeHasChanged) {
            super.onBackPressed();
            return;
        }

        // Discard changes dialog
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


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all the it device attributes, define a projection that contains
        // all columns from the it table
        String[] projection = {
                StoreContract.StoreEntry._ID,
                StoreContract.StoreEntry.COLUMN_NAME,
                StoreContract.StoreEntry.COLUMN_PRICE,
                StoreContract.StoreEntry.COLUMN_QUANTITY,
                StoreContract.StoreEntry.COLUMN_SUP_NAME,
                StoreContract.StoreEntry.COLUMN_SUP_PHONE};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                storeCurrentUri,         // Query the content URI for the current it device
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                // No selection arguments
                null);                  // Default sort order
    }




    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of the it device attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_NAME);
            int priceColumnIndex = cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_QUANTITY);
            int supplier_nameColumnIndex = cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_SUP_NAME);
            int supplier_phoneColumnIndex = cursor.getColumnIndex(StoreContract.StoreEntry.COLUMN_SUP_PHONE);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            Float price = cursor.getFloat(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplier_name = cursor.getString(supplier_nameColumnIndex);
            String phone = cursor.getString(supplier_phoneColumnIndex);

            // Update the views on the screen with the values from the database
            storeName.setText(name);
            storePrice.setText(String.format(Float.toString(price), Locale.getDefault()));
            storeQuantity.setText(String.format(Integer.toString(quantity), Locale.getDefault()));
            storeSupplierName.setText(supplier_name);
            storeSupplierPhone.setText(phone);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // On invalid loader, clear all fields
        storeName.setText(R.string.empty);
        storePrice.setText(R.string.empty);
        storeQuantity.setText(R.string.empty);
        storeSupplierName.setText(R.string.empty);
        storeSupplierPhone.setText(R.string.empty);
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.discard_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    // delete product method
    private void deleteProduct() {
        //  delete if product exists
        if (storeCurrentUri != null) {
            int rowsDeleted = getContentResolver().delete(storeCurrentUri, null, null);
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, R.string.error_delete,
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, R.string.product_delete,
                        Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }
}