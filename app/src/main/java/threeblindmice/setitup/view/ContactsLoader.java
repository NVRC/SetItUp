package threeblindmice.setitup.view;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;

import threeblindmice.setitup.view.Contacts;

/**
 * Created by Slate on 2018-05-04.
 */

public class ContactsLoader implements LoaderManager.LoaderCallbacks<Cursor> {
    private Contacts contacts;
    private SimpleCursorAdapter sAdapter;
    public ContactsLoader(Contacts source, SimpleCursorAdapter adapter){
        contacts = source;
        sAdapter = adapter;
    }

    // Create and return the actual cursor loader for the contacts data
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define the columns to retrieve
        String[] projectionFields = new String[] { ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME};
        // Construct the loader
        CursorLoader cursorLoader = new CursorLoader(contacts,
                ContactsContract.Contacts.CONTENT_URI, // URI
                projectionFields, // projection fields
                null, // the selection criteria
                null, // the selection args
                null // the sort order
        );
        // Return the loader for use
        return cursorLoader;
    }

    // When the system finishes retrieving the Cursor through the CursorLoader,
    // a call to the onLoadFinished() method takes place.
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // The swapCursor() method assigns the new Cursor to the adapter
        sAdapter.swapCursor(cursor);
    }

    // This method is triggered when the loader is being reset
    // and the loader data is no longer available. Called if the data
    // in the provider changes and the Cursor becomes stale.
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Clear the Cursor we were using with another call to the swapCursor()
        sAdapter.swapCursor(null);
    }
}
