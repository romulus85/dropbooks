package com.booxs.app;


import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.booxs.app.ebook.EbookContent;

import java.io.File;
import java.io.IOException;

import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;


/**
 * A simple {@link Fragment} subclass.
 */
public class EbookDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<File> {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_EBOOK = "Ebook";
    private static final int DETAIL_LOADER_ID = 89;
    private static final String TAG = "EbookDetailFragment";

    private EbookContent.EbookItem mEbook;
    private EbookDetailLoader mDetailEbookLoader;
    private Book book_file;
    private ProgressBar pb;

    public EbookDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mEbook = getArguments().getParcelable(ARG_EBOOK);
        }
        // Retain this fragment across configuration changes.
        LoaderManager lm = getLoaderManager();
        if (lm != null) {
            lm.initLoader(DETAIL_LOADER_ID, null, this).forceLoad();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        pb = (ProgressBar) activity.findViewById(R.id.progressBar);
        activity.getActionBar().setTitle(R.string.title_activity_main);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ebook_detail, container, false);
    }


    @Override
    public Loader<File> onCreateLoader(int i, Bundle bundle) {
        Log.d(TAG, "onCreateLoader");
        mDetailEbookLoader = null;
        if (mEbook != null) {
            mDetailEbookLoader = new EbookDetailLoader(getActivity(), mEbook, LoginActivity.mDBApi);
            if (pb != null)
                pb.setVisibility(View.VISIBLE);
        }
        return mDetailEbookLoader;
    }

    @Override
    public void onLoadFinished(Loader<File> objectLoader, File file) {
        Log.d(TAG, "onLoadFinished");
        LoadBookCoverTask loadBookCoverTask = new LoadBookCoverTask();
        LoadBookTitleTask loadBookTitleTask = new LoadBookTitleTask();
        //sequential asynctask execution, first one and the the other, not at the same time
        loadBookTitleTask.execute(file.getPath());
        loadBookCoverTask.execute(file.getPath());
    }

    @Override
    public void onLoaderReset(Loader<File> objectLoader) {

    }

    private class LoadBookTitleTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            if (pb != null)
                pb.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... title) {
            if (book_file == null) {
                try {
                    book_file = (new EpubReader()).readEpubLazy(title[0], Constants.CHARACTER_ENCODING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Bitmap cover_bitmap = null;
            try {
                cover_bitmap = BitmapFactory.decodeStream(book_file.getCoverImage().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return book_file.getTitle();
        }

        @Override
        protected void onPostExecute(String title) {
            super.onPostExecute(title);
            View view = getView();
            if (view != null) {
                TextView tvTitle = (TextView) view.findViewById(R.id.TvDetailTitle);
                tvTitle.setText(title);
            }
            if (pb != null)
                pb.setVisibility(View.INVISIBLE);
        }
    }

    private class LoadBookCoverTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            if (pb != null)
                pb.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(String... title) {
            if (book_file == null) {
                try {
                    book_file = (new EpubReader()).readEpubLazy(title[0], Constants.CHARACTER_ENCODING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Bitmap cover_bitmap = null;
            try {
                cover_bitmap = BitmapFactory.decodeStream(book_file.getCoverImage().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return cover_bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            View view = getView();
            if (view != null) {
                ImageView cover = (ImageView) view.findViewById(R.id.IvDetailCover);
                cover.setImageBitmap(bitmap);
            }
            if (pb != null)
                pb.setVisibility(View.INVISIBLE);
        }
    }
}
