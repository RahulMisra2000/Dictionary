package com.codingwithmitch.dictionary.threading;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.codingwithmitch.dictionary.models.Word;
import com.codingwithmitch.dictionary.persistence.AppDatabase;
import com.codingwithmitch.dictionary.util.Constants;

import java.util.ArrayList;


public class RetrieveWordsRunnable implements Runnable {

    private static final String TAG = "RetrieveWordsRunnable";

    private Handler mMainThreadHandler;     // Need this so we can send message back to the calling thread
    private AppDatabase mDb;
    private String mQuery;


    public RetrieveWordsRunnable(Context context, Handler mMainThreadHandler, String mQuery) {
        this.mMainThreadHandler = mMainThreadHandler;
        this.mQuery = mQuery;
        mDb = AppDatabase.getDatabase(context);
    }

    // This work is done on the background thread
    @Override
    public void run() {
        Log.d(TAG, "run: retrieving words. This is from thread: " + Thread.currentThread().getName());
        ArrayList<Word> words = new ArrayList<>(mDb.wordDataDao().getWords(mQuery));
        Message message = null;
        if (words.size() > 0) {
            message = Message.obtain(null, Constants.WORDS_RETRIEVE_SUCCESS);
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("words_retrieve", words);
            message.setData(bundle);
        } else {
            message = Message.obtain(null, Constants.WORDS_RETRIEVE_FAIL);
        }

        mMainThreadHandler.sendMessage(message);            // sending info back to the calling thread
    }
}
