package com.booxs.app;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.booxs.app.ebook.EbookContent;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Fran on 15/05/2014.
 */
public class EbookDetailLoader extends AsyncTaskLoader<File> {
    //object to access and manage Dropbox api
    private DropboxAPI<AndroidAuthSession> mDBApi;
    private File localFile;
    private EbookContent.EbookItem mEbook;

    public EbookDetailLoader(Context context, EbookContent.EbookItem ebook, DropboxAPI<AndroidAuthSession> db_api) {
        super(context);
        mDBApi = db_api;
        mEbook = ebook;
        localFile = new File(context.getFilesDir(), mEbook.file_name);
    }

    @Override
    public File loadInBackground() {
        try {
            downloadDropboxFile();
            return localFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean downloadDropboxFile() throws IOException {

        BufferedInputStream br = null;
        BufferedOutputStream bw = null;

        try {
            if (!localFile.exists()) {
                localFile.createNewFile(); //otherwise dropbox client will fail silently
            }

            DropboxAPI.DropboxInputStream fd = mDBApi.getFileStream(mEbook.path, null);
            br = new BufferedInputStream(fd);
            bw = new BufferedOutputStream(new FileOutputStream(localFile));

            byte[] buffer = new byte[4096];
            int read;
            while (true) {
                read = br.read(buffer);
                if (read <= 0) {
                    break;
                }
                bw.write(buffer, 0, read);
            }
        } catch (DropboxException e) {
            e.printStackTrace();
        } finally {
            //in finally block:
            if (bw != null) {
                bw.close();
            }
            if (br != null) {
                br.close();
            }
        }

        return true;
    }
}
