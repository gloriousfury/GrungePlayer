package com.gloriousfury.musicplayer.service;

/**
 * Created by OLORIAKE KEHINDE on 6/12/2016.
 */

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.gloriousfury.musicplayer.model.Audio;
import com.gloriousfury.musicplayer.utils.StorageUtil;

import java.io.File;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;


/**
 * Created by ValueMinds on 2/26/2016.
 */
public class AppMainService extends BaseService {

    private static final String TAG = "AppMainService";

    String emailText;
    StorageUtil storage;

    public AppMainService() {
        super("AppMainService");
    }

    public AppMainService(Context c) {
        super("AppMainService", c);
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent() called with: " + "intent = [" + intent + "]");
        int action = intent.getIntExtra(ServiceActionConstants.SERVICE_ACTION,0);
//        RestApi client = RestClient.getInstance().getClient(getContext(),
//                RestApi.class);

        AppMainServiceEvent event = new AppMainServiceEvent();
        Intent responseIntent = new Intent();
        switch (action){

      /*      case ServiceActionConstants.SERVICE_ACTION_GET_UPDATES :
                try{
                    client = RestClient.getInstance().getClient2(getContext(),
                            RestApi.class,false);
                    NewsArchiveResponse newsArchiveResponse = client.getNewsArchive();
                    responseIntent.putExtra(AppMainServiceEvent.RESPONSE_DATA, Parcels.wrap(newsArchiveResponse));
                    Log.i(TAG, "STATUS>>>>>>>>>>>"+newsArchiveResponse.getStatus());
                    event.setMainIntent(responseIntent);
                    event.setEventType(AppMainServiceEvent.UPDATES_RESPONSE);
                    EventBus.getDefault().post(event);
                }
                catch(RetrofitError error){
                    Log.i("eror", "uknown error");
                    error.printStackTrace();
                    event.setEventType(AppMainServiceEvent.UPDATES_RESPONSE);
                    EventBus.getDefault().post(event);
                }
                catch(Exception e){
                    Log.i("eror", "uknown error");
                    e.printStackTrace();
                }

                break;*/

            case ServiceActionConstants.SERVICE_ACTION_GET_ALL_AUDIO:

               ArrayList<Audio> retrievedAudioList = storage.loadAllAudio();
                ArrayList<Audio> audioList = null;
//                Toast.makeText(LibraryActivity.this, String.valueOf(retrievedAudioList.size()), Toast.LENGTH_LONG).show();

                if (retrievedAudioList != null ) {
                    // Delivers any previously loaded data immediately
//                    deliverResult(retrievedAudioList);

                }else{

                    ContentResolver contentResolver = getContentResolver();
                    String album_art_string = null;
                    Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
                    String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
                    Cursor cursor = contentResolver.query(uri, null, selection, null, sortOrder);

                    if (cursor != null && cursor.getCount() > 0) {
                        audioList = new ArrayList<>();
                        Audio audio = new Audio("Header");
                        audioList.add(audio);


                        while (cursor.moveToNext()) {
                            String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                            String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                            String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                            Long albumId = cursor.getLong(cursor
                                    .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));

                            int duration = cursor.getInt(cursor
                                    .getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));

                            Uri sArtworkUri = Uri
                                    .parse("content://media/external/audio/albumart");
                            Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);

                            album_art_string = albumArtUri.toString();
                            ContentResolver cr = getContentResolver();
                            String[] projectionArt = {MediaStore.MediaColumns.DATA};
                            Cursor cur = cr.query(Uri.parse(album_art_string), projectionArt, null, null, null);
                            if (cur != null) {
                                if (cur.moveToFirst()) {
                                    String filePath = cur.getString(0);

                                    if (new File(filePath).exists()) {
                                        album_art_string = albumArtUri.toString();
                                        // do something if it exists
                                    } else {
                                        album_art_string = null;
                                        // File was not found
                                    }
                                } else {
                                    album_art_string = null;
                                    // Uri was ok but no entry found.
                                }
                                cur.close();
                            } else {
                                album_art_string = null;
                                // content Uri was invalid or some other error occurred
                            }


//
//                        Bitmap bitmap = null;
//                        try {
//                            bitmap = MediaStore.Images.Media.getBitmap(
//                                    getContentResolver(), albumArtUri);
//                            bitmap = Bitmap.createScaledBitmap(bitmap, 30, 30, true);
//
//                        } catch (FileNotFoundException exception) {
//                            exception.printStackTrace();
//                            bitmap = BitmapFactory.decodeResource(getResources(),
//                                    R.mipmap.ic_launcher);
//                        } catch (IOException e) {
//
//                            e.printStackTrace();
//                        }


                            // Save to audioList
                            audioList.add(new Audio(data, title, album, artist, duration, albumId, album_art_string));

                        }
                    }

                    cursor.close();

                    responseIntent.putExtra(AppMainServiceEvent.RESPONSE_DATA, audioList);
                    Log.i(TAG, "STATUS>>>>>>>>>>>"+audioList.get(0).getTitle());
                    event.setMainIntent(responseIntent);
                    event.setEventType(AppMainServiceEvent.ONDATA_RECIEVED_ALLSONGS);
                    EventBus.getDefault().post(event);

                }


















//                try{
//                    client = RestClient.getInstance().getClient2(getContext(),
//                            RestApi.class,true);
//                    TokenResponse tokenResponse = client.getToken(ServiceActionConstants.GRANT_TYPE,
//                            intent.getStringExtra(IntentActionConstants.USERNAME),
//                            intent.getStringExtra(IntentActionConstants.PASSWORD));
//                    responseIntent.putExtra(AppMainServiceEvent.RESPONSE_DATA, Parcels.wrap(tokenResponse));
//                    Log.i(TAG, "STATUS>>>>>>>>>>>"+tokenResponse.getUserName());
//                    event.setMainIntent(responseIntent);
//                    event.setEventType(AppMainServiceEvent.TOKEN_RESPONSE);
//                    EventBus.getDefault().post(event);
//                }
//                catch(RetrofitError error){
////                    Log.i("eror", error.getMessage());
////                    Log.i("eror", error.getUrl());
//                    error.printStackTrace();
//                    event.setEventType(AppMainServiceEvent.TOKEN_RESPONSE);
//                    EventBus.getDefault().post(event);
//                }
//                catch(Exception e){
//                    Log.i("eror", "uknown error");
//                    e.printStackTrace();
//                }

                break;





        }

    }


}

