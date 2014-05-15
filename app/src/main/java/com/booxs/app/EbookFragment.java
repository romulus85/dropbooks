package com.booxs.app;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.booxs.app.ebook.EbookContent;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class EbookFragment extends Fragment implements AbsListView.OnItemClickListener, LoaderManager.LoaderCallbacks<EbookContent> {

    private static final int LOADER_ID = 1;
    private static final String TAG = "EbookFragment";
    public static final String ORDER_BY_PREF = "orderBy";
    public static final String SHOW_AS_PREF = "showAs";
    private EbookContent mBookList;
    private EbooksLoader mEbooksLoader;

    enum showAsEnum {
        SHOW_AS_LIST,
        SHOW_AS_GRID
    }

    enum orderByEnum {
        ORDER_BY_DATE,
        ORDER_BY_NAME
    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EbookFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        setHasOptionsMenu(true);

        mBookList = new EbookContent();

        //TODO customize array adapter to show an image
        //adapter to display all the ebooks
        mAdapter = new ArrayAdapter<EbookContent.EbookItem>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, mBookList.getAllItems());
        // Initialize the Loader with id '1' and callbacks 'mCallbacks'.
        // If the loader doesn't already exist, one is created. Otherwise,
        // the already created Loader is reused. In either case, the
        // LoaderManager will manage the Loader across the Activity/Fragment
        // lifecycle, will receive any new loads once they have completed,
        // and will report this new data back to the 'mCallbacks' object.
        LoaderManager lm = getLoaderManager();
        if (lm != null) {
            lm.initLoader(LOADER_ID, null, this).forceLoad();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: Item = " + item);
        switch (item.getItemId()) {
            case R.id.action_order_name:
                onOrderByNameAction();
                return true;
            case R.id.action_order_date:
                onOrderByDateAction();
                return true;
            case R.id.action_see_grid:
                onSeeAsGridAction();
                return true;
            case R.id.action_see_list:
                onSeeAsListAction();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onSeeAsListAction() {
        changePreference(SHOW_AS_PREF, showAsEnum.SHOW_AS_LIST.ordinal());
        refreshView();
    }

    private void refreshView() {
        //THIS IS THE CODE TO REFRESH THE FRAGMENT.
        FragmentManager manager = getActivity().getFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        Fragment newFragment = this;
        ft.remove(this);
        ft.replace(R.id.container, newFragment);
        //container is the ViewGroup of current fragment
        ft.addToBackStack(null);
        ft.commit();
    }

    private void onSeeAsGridAction() {
        changePreference(SHOW_AS_PREF, showAsEnum.SHOW_AS_GRID.ordinal());
        refreshView();
    }

    private void onOrderByDateAction() {
        changePreference(ORDER_BY_PREF, orderByEnum.ORDER_BY_DATE.ordinal());

        //sort by date and reload adapter
        List<EbookContent.EbookItem> booksList = new ArrayList<EbookContent.EbookItem>(mBookList.getAllItems());
        Collections.sort(booksList, EbookContent.orderDateComparator);
        mAdapter = new ArrayAdapter<EbookContent.EbookItem>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, booksList);
        mListView.setAdapter(mAdapter);
    }

    private void onOrderByNameAction() {
        changePreference(ORDER_BY_PREF, orderByEnum.ORDER_BY_NAME.ordinal());

        //sort by name and reload adapter
        List<EbookContent.EbookItem> booksList = new ArrayList<EbookContent.EbookItem>(mBookList.getAllItems());
        Collections.sort(booksList, EbookContent.orderNameComparator);
        mAdapter = new ArrayAdapter<EbookContent.EbookItem>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, booksList);
        mListView.setAdapter(mAdapter);
    }

    //change the prefence value called pref_name.
    //only valid for integers
    private void changePreference(String pref_name, int value) {
        Activity activity = getActivity();
        if (activity != null) {
            // We need an Editor object to make preference changes.
            SharedPreferences settings = getActivity().getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(pref_name, value);

            // Commit the edits!
            editor.commit();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.list_ebooks_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = null;


        if (getActivity() != null) {
            final int NOT_DEFINED = -111;
            SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
            int show_as = prefs.getInt(SHOW_AS_PREF, NOT_DEFINED);
            if (NOT_DEFINED == show_as) {
                view = inflater.inflate(R.layout.fragment_ebook, container, false);
            } else {
                showAsEnum show_as_enum = showAsEnum.values()[show_as];
                if (show_as_enum.equals(showAsEnum.SHOW_AS_GRID)) {
                    view = inflater.inflate(R.layout.fragment_ebook_grid, container, false);
                } else if (show_as_enum.equals(showAsEnum.SHOW_AS_LIST)) {
                    view = inflater.inflate(R.layout.fragment_ebook_list, container, false);
                }
            }
        } else {
            view = inflater.inflate(R.layout.fragment_ebook, container, false);
        }

        // Set the adapter
        if (view != null) {
            mListView = (AbsListView) view.findViewById(android.R.id.list);

            ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

            // Set OnItemClickListener so we can be notified on item clicks
            mListView.setOnItemClickListener(this);
        }

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(mBookList.get(position).id);
        }
    }

    /**
     * The default title for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyText instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    @Override
    public Loader<EbookContent> onCreateLoader(int i, Bundle bundle) {
        mEbooksLoader = new EbooksLoader(getActivity(), mListener.getDropboxApi());
        return mEbooksLoader;
    }

    @Override
    public void onLoadFinished(Loader<EbookContent> ebookContentLoader, EbookContent ebookContent) {
        // A switch-case is useful when dealing with multiple Loaders/IDs
        switch (ebookContentLoader.getId()) {
            case LOADER_ID:
                mBookList = ebookContent;
                List<EbookContent.EbookItem> booksList = new ArrayList<EbookContent.EbookItem>(mBookList.getAllItems());
                //default order
                Comparator<EbookContent.EbookItem> comparator = EbookContent.orderNameComparator;
                if (getActivity() != null) {
                    SharedPreferences pref = getActivity().getPreferences(Context.MODE_PRIVATE);
                    orderByEnum order_by = orderByEnum.values()[
                            pref.getInt(ORDER_BY_PREF, orderByEnum.ORDER_BY_NAME.ordinal())];
                    if (order_by == orderByEnum.ORDER_BY_DATE)
                        comparator = EbookContent.orderDateComparator;
                }
                Collections.sort(booksList, comparator);
                Log.d(TAG, "booksList = " + booksList);
                // The asynchronous load is complete and the data
                // is now available for use. Only now can we associate
                // the queried Cursor with the SimpleCursorAdapter.
                mAdapter = new ArrayAdapter<EbookContent.EbookItem>(getActivity(),
                        android.R.layout.simple_list_item_1, android.R.id.text1, booksList);
                mListView.setAdapter(mAdapter);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<EbookContent> loader) {
        mListView.setAdapter(null);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(String id);

        public DropboxAPI<AndroidAuthSession> getDropboxApi();
    }

}
