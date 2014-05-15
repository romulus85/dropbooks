package com.booxs.app;


import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.booxs.app.ebook.EbookContent;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;


/**
 * A simple {@link Fragment} subclass.
 */
public class EbookDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<File> {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_EBOOK = "Ebook";
    private static final int DETAIL_LOADER_ID = 89465845;
    private static final String TAG = "EbookDetailFragment";

    private EbookContent.EbookItem mEbook;
    private EbookFragment.OnFragmentInteractionListener mListener;
    private EbookDetailLoader mDetailEbookLoader;

    public EbookDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mEbook = getArguments().getParcelable(ARG_EBOOK);
        }
        LoaderManager lm = getLoaderManager();
        if (lm != null) {
            lm.initLoader(DETAIL_LOADER_ID, null, this).forceLoad();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (EbookFragment.OnFragmentInteractionListener) activity;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ebook_detail, container, false);
    }


    @Override
    public Loader<File> onCreateLoader(int i, Bundle bundle) {
        mDetailEbookLoader = null;
        if (mEbook != null && mListener != null) {
            mDetailEbookLoader = new EbookDetailLoader(getActivity(), mEbook, mListener.getDropboxApi());
        }
        return mDetailEbookLoader;
    }

    @Override
    public void onLoadFinished(Loader<File> objectLoader, File file) {
        try {
            View view = getView();
            Book book = (new EpubReader()).readEpub(new FileInputStream(file));
            if (book != null && view != null) {
                ImageView cover = (ImageView) view.findViewById(R.id.IvDetailCover);
                Bitmap cover_bitmap = BitmapFactory.decodeStream(book.getCoverImage().getInputStream());
                cover.setImageBitmap(cover_bitmap);
                TextView tvTitle = (TextView) view.findViewById(R.id.TvDetailTitle);
                tvTitle.setText(book.getTitle());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoaderReset(Loader<File> objectLoader) {

    }
}
