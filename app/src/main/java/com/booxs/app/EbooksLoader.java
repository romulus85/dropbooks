package com.booxs.app;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.booxs.app.ebook.EbookContent;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Fran on 14/05/2014.
 */
public class EbooksLoader extends AsyncTaskLoader<EbookContent> {
    private static final String TAG = "EbooksLoader";
    //object to access and manage Dropbox api
    private DropboxAPI<AndroidAuthSession> mDBApi;
    private List<DropboxAPI.Entry> list;

    public EbooksLoader(Context context, DropboxAPI<AndroidAuthSession> db_api) {
        super(context);
        mDBApi = db_api;
    }

    @Override
    public EbookContent loadInBackground() {
        EbookContent content = new EbookContent();

        try {
            if (mDBApi != null) {
                list = mDBApi.search("/", ".epub", 0, false);
                Integer id = 1;
                for (DropboxAPI.Entry ebook : list) {
//                client_mtime:	For files, this is the modification time set by the desktop client
//                when the file was added to Dropbox, in the standard date format. Since this time is
//                not verified (the Dropbox server stores whatever the desktop client sends up),
//                this should only be used for display purposes (such as sorting) and not,
//                for example, to determine if a file has changed or not.
                    EbookContent.EbookItem item = new EbookContent.EbookItem(id.toString(), ebook.fileName());
                    //parse dropbox date string to date
                    //TODO add to a dropbox utils class
                    SimpleDateFormat formatoDelTexto = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm:ss +S");
                    String strFecha = ebook.clientMtime;
                    Date fecha = null;
                    try {
                        fecha = formatoDelTexto.parse(strFecha);
                        item.creation_time = fecha.getTime();
                    } catch (ParseException ex) {
                        ex.printStackTrace();
                    }
                    content.addItem(item);
                    ++id;
                }
            }

        } catch (DropboxException e) {
            e.printStackTrace();
        }
        return content;
    }

}
