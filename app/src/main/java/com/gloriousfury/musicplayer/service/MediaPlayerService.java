package com.gloriousfury.musicplayer.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaSessionManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.gloriousfury.musicplayer.R;
import com.gloriousfury.musicplayer.model.Audio;
import com.gloriousfury.musicplayer.ui.activity.SingleSongActivity;
import com.gloriousfury.musicplayer.utils.StorageUtil;
import com.gloriousfury.musicplayer.ui.activity.MainActivity;
import com.gloriousfury.musicplayer.utils.PlaybackStatus;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import de.greenrobot.event.EventBus;


public class MediaPlayerService extends Service implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener,

        AudioManager.OnAudioFocusChangeListener {

    public static MediaPlayer mediaPlayer;
    String TAG = MediaPlayerService.class.getSimpleName();
    //path to the audio file
    private String mediaFile;
    private int resumePosition;
    private AudioManager audioManager;
    //Handle incoming phone calls
    private boolean ongoingCall = false;
    private PhoneStateListener phoneStateListener;
    private TelephonyManager telephonyManager;
    //List of available Audio files
    private ArrayList<Audio> audioList;
    private int audioIndex = -1;
    private Audio activeAudio; //an object of the currently playing audio
    public static final String ACTION_PLAY = "com.gloriousfury.musicplayer.ACTION_PLAY";
    public static final String ACTION_PAUSE = "com.gloriousfury.musicplayer.ACTION_PAUSE";
    public static final String ACTION_PREVIOUS = "com.gloriousfury.musicplayer.ACTION_PREVIOUS";
    public static final String ACTION_NEXT = "com.gloriousfury.musicplayer.ACTION_NEXT";
    public static final String ACTION_STOP = "com.gloriousfury.musicplayer.ACTION_STOP";
    int duration;
    Intent responseIntent = new Intent();
    AppMainServiceEvent event = new AppMainServiceEvent();
    Context context;
    PlaybackStatus playbackStatus;
    boolean shuffleState = false;

    //MediaSession
    private MediaSessionManager mediaSessionManager;
    private MediaSessionCompat mediaSession;
    private MediaControllerCompat.TransportControls transportControls;

    //AudioPlayer notification ID
    private static final int NOTIFICATION_ID = 101;


    public Context getContext() {
        if (context == null) {
            return getApplicationContext();

        } else {

            return context;
        }

    }

    public MediaPlayerService() {


    }


    public MediaPlayerService(Context context) {

        this.context = context;
    }

    public MediaPlayer getMediaPlayerInstance() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }

        return mediaPlayer;
    }


//    public  MediaPlayer getMediaPlayerInstance2(Context context) {
//        if (mediaPlayer == null) {
//            mediaPlayer = new MediaPlayer();
//        }
//        this.context = context;
//        return mediaPlayer;
//    }


    @Override
    public void onCreate() {
        super.onCreate();

        // Perform one-time setup procedures
        StorageUtil storage = new StorageUtil(getContext());
        shuffleState = storage.getShuffleSettings();
        // Manage incoming phone calls during playback.
        // Pause MediaPlayer on incoming call,
        // Resume on hangup.
        callStateListener();
        //ACTION_AUDIO_BECOMING_NOISY -- change in audio outputs -- BroadcastReceiver
        registerBecomingNoisyReceiver();
        //Listen for new Audio to play -- BroadcastReceiver
        register_playNewAudio();


    }

    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        //Set up MediaPlayer event listeners
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnInfoListener(this);
        //Reset so that the MediaPlayer is not pointing to another data source
        mediaPlayer.reset();

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            // Set the data source to the mediaFile location
            mediaPlayer.setDataSource(activeAudio.getData());
        } catch (IOException e) {
            e.printStackTrace();
            stopSelf();
        }

        mediaPlayer.prepareAsync();
    }


    private void playMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            playbackStatus = PlaybackStatus.PLAYING;
            duration = mediaPlayer.getDuration();
        }
    }

    public void stopMedia() {
        if (mediaPlayer == null) return;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    public void pauseMedia(MediaPlayer mediaPlayer) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            playbackStatus = PlaybackStatus.PAUSED;
            resumePosition = mediaPlayer.getCurrentPosition();

        }
    }

    public void resumeMedia(MediaPlayer mediaPlayer) {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(resumePosition);
            mediaPlayer.start();
        }
    }


    // Binder given to clients
    private final IBinder iBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        //Invoked indicating buffering status of
        //a media resource being streamed over the network.
    }

    public void setList(ArrayList<Audio> theSongs) {
        audioList = theSongs;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //Invoked when playback of a media source has completed.

        StorageUtil storage = new StorageUtil(getContext());
        audioList = storage.loadAudio();
        audioIndex = storage.loadAudioIndex();
        if (audioIndex <= audioList.size()) {
            Toast.makeText(getContext(), "This is suppossed to be the next song", Toast.LENGTH_LONG).show();
            shuffleState = storage.getShuffleSettings();
            if (shuffleState) {
                shuffleToNext(audioList, audioIndex);
            } else {
                skipToNext(audioList, audioIndex, getContext(), mediaPlayer);
            }
            updateMetaData();
        } else {
            Toast.makeText(getContext(), "The list said I shouldn't play", Toast.LENGTH_LONG).show();
            stopMedia();
            //stop the service
            stopSelf();

        }
    }

    //Handle errors
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        //Invoked when there has been an error during an asynchronous operation
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Log.d("MediaPlayer Error", "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK " + extra);
                Toast.makeText(getContext(), "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK ", Toast.LENGTH_LONG).show();
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Log.d("MediaPlayer Error", "MEDIA ERROR SERVER DIED " + extra);
                Toast.makeText(getContext(), "MEDIA ERROR SERVER DIED ", Toast.LENGTH_LONG).show();
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Log.d("MediaPlayer Error", "MEDIA ERROR UNKNOWN " + extra);
                Toast.makeText(getContext(), "EDIA ERROR UNKNOWN ", Toast.LENGTH_LONG).show();

                break;
        }
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //Invoked when the media source is ready for playback
        playMedia();

    }


    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        //Invoked to communicate some info.
        return false;
    }


    @Override
    public void onSeekComplete(MediaPlayer mp) {
        //Invoked indicating the completion of a seek operation.
    }


    @Override
    public void onAudioFocusChange(int focusState) {
        //Invoked when the audio focus of the system is updated.
        switch (focusState) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // resume playback
                if (mediaPlayer == null) initMediaPlayer();
                else if (!mediaPlayer.isPlaying() & isPlayBackSupposedToContinue()) {
                    mediaPlayer.start();
                    mediaPlayer.setVolume(1.0f, 1.0f);
                }


                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                // Lost focus for an unbounded amount of time: stop playback and release media seek
                if (mediaPlayer.isPlaying()) mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media seek because playback
                // is likely to resume
                if (mediaPlayer.isPlaying()) mediaPlayer.pause();

                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                if (mediaPlayer.isPlaying()) mediaPlayer.setVolume(0.1f, 0.1f);
                break;
        }
    }

    private boolean requestAudioFocus() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            //Focus gained
            return true;
        }
        //Could not gain focus
        return false;
    }

    private boolean removeAudioFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                audioManager.abandonAudioFocus(this);
    }


    public class LocalBinder extends Binder {
        public MediaPlayerService getService() {
            return MediaPlayerService.this;
        }
    }


    private BroadcastReceiver becomingNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //pause audio on ACTION_AUDIO_BECOMING_NOISY
            pauseMedia(mediaPlayer);
            buildNotification(PlaybackStatus.PAUSED);
        }
    };

    private void registerBecomingNoisyReceiver() {
        //register after getting audio focus
        IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(becomingNoisyReceiver, intentFilter);
    }


    //Handle incoming phone calls
    private void callStateListener() {
        // Get the telephony manager
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //Starting listening for PhoneState changes
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    //if at least one call exists or the phone is ringing
                    //pause the MediaPlayer
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                    case TelephonyManager.CALL_STATE_RINGING:
                        if (mediaPlayer != null) {
                            pauseMedia(mediaPlayer);
                            ongoingCall = true;
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        // Phone idle. Start playing.
                        if (mediaPlayer != null) {
                            if (ongoingCall) {
                                ongoingCall = false;
                                resumeMedia(mediaPlayer);
                            }
                        }
                        break;
                }
            }
        };
        // Register the listener with the telephony manager
        // Listen for changes to the device call state.
        telephonyManager.listen(phoneStateListener,
                PhoneStateListener.LISTEN_CALL_STATE);
    }


    public BroadcastReceiver playNewAudio = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //Get the new media index form SharedPreferences
            StorageUtil storageUtil = new StorageUtil(context);
            audioList = storageUtil.loadAudio();
            audioIndex = storageUtil.loadAudioIndex();
            if (audioIndex != -1 && audioIndex < audioList.size()) {
                //index is in a valid range
                activeAudio = audioList.get(audioIndex);
            } else {
                stopSelf();
            }

            //A PLAY_NEW_AUDIO action received
            //reset mediaPlayer to play the new Audio
            stopMedia();
            mediaPlayer.reset();
            initMediaPlayer();
            updateMetaData();
            buildNotification(PlaybackStatus.PLAYING);
        }
    };

    public void register_playNewAudio() {
        //Register playNewMedia receiver
        IntentFilter filter = new IntentFilter(MainActivity.Broadcast_PLAY_NEW_AUDIO);
        registerReceiver(playNewAudio, filter);
    }

    private void initMediaSession() throws RemoteException {
        if (mediaSessionManager != null) return; //mediaSessionManager exists

        mediaSessionManager = (MediaSessionManager) getSystemService(getContext().MEDIA_SESSION_SERVICE);
        // Create a new MediaSession
        mediaSession = new MediaSessionCompat(getContext(), "AudioPlayer");
        //Get MediaSessions transport controls
        transportControls = mediaSession.getController().getTransportControls();
        //set MediaSession -> ready to receive media commands
        mediaSession.setActive(true);
        //indicate that the MediaSession handles transport control commands
        // through its MediaSessionCompat.Callback.
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        //Set mediaSession's MetaData
        updateMetaData();

        // Attach Callback to receive MediaSession updates
        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            // Implement callbacks
            @Override
            public void onPlay() {
                super.onPlay();
                resumeMedia(mediaPlayer);
                buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onPause() {
                super.onPause();
                pauseMedia(mediaPlayer);
                buildNotification(PlaybackStatus.PAUSED);
            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();
                StorageUtil storage = new StorageUtil(getContext());
                audioList = storage.loadAudio();
                audioIndex = storage.loadAudioIndex();
                shuffleState = storage.getShuffleSettings();
                if (shuffleState) {
                    shuffleToNext(audioList, audioIndex);
                } else {
                    skipToNext(audioList, audioIndex, getContext(), mediaPlayer);
                }

                updateMetaData();
                buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();
                skipToPrevious(audioList, audioIndex, getApplicationContext(), mediaPlayer);
                updateMetaData();
                buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onStop() {
                super.onStop();
                removeNotification();
                //Stop the service
                stopSelf();
            }

            @Override
            public void onSeekTo(long position) {
                super.onSeekTo(position);
            }
        });
    }

    private void updateMetaData() {
//        Bitmap albumArt = BitmapFactory.decodeResource(getContext().getResources(),
//                R.mipmap.ic_launcher); //replace with medias albumArt
        Uri albumArtUri = null;

        if (activeAudio.getAlbumArtUriString() != null) {
            albumArtUri = Uri.parse(activeAudio.getAlbumArtUriString());

        }
        Bitmap albumArt = null;
        try {
            albumArt = MediaStore.Images.Media.getBitmap(
                    getContentResolver(), albumArtUri);
            albumArt = Bitmap.createScaledBitmap(albumArt, 30, 30, true);

        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
            albumArt = BitmapFactory.decodeResource(getResources(),
                    R.mipmap.ic_launcher);
        } catch (IOException e) {

            e.printStackTrace();
        }
        // Update the current metadata
        mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, activeAudio.getArtist())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, activeAudio.getAlbum())
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, activeAudio.getTitle())
                .build());
    }

    public void skipToNext(ArrayList<Audio> audioList, int audioIndex, Context context, MediaPlayer mediaPlayer) {

        if (audioIndex == audioList.size() - 1) {
            //if last in playlist
            audioIndex = 0;
            activeAudio = audioList.get(audioIndex);


        } else {
            //get next in playlist
            activeAudio = audioList.get(++audioIndex);
            Log.e(TAG, "Audio Index : " + audioIndex + "Active Audio: " + activeAudio.getTitle());
        }


        //Update stored index
        new StorageUtil(context).storeAudioIndex(audioIndex);
        responseIntent.putExtra(AppMainServiceEvent.RESPONSE_DATA, activeAudio);
        event.setMainIntent(responseIntent);
        event.setEventType(AppMainServiceEvent.ONCOMPLETED_RESPONSE);
        EventBus.getDefault().post(event);
        stopMedia();
        //reset mediaPlayer
        mediaPlayer.reset();
        Log.e(TAG, "I also came here");
        initMediaPlayer();

    }

    public void shuffleToNext(ArrayList<Audio> audioList, int audioIndex) {


        Random rand = new Random();
        audioIndex = rand.nextInt(audioList.size() - 1);

        //this should work for when repeat and shuffle is on
//        else  if (audioIndex == audioList.size() - 1) {
//            //if last in playlist
//            audioIndex = 0;
//            activeAudio = audioList.get(audioIndex);
//
//        } else {
        //get next in playlist
        activeAudio = audioList.get(audioIndex);
        //Update stored index
        new StorageUtil(context).storeAudioIndex(audioIndex);
        responseIntent.putExtra(AppMainServiceEvent.RESPONSE_DATA, activeAudio);
        event.setMainIntent(responseIntent);
        event.setEventType(AppMainServiceEvent.ONCOMPLETED_RESPONSE);
        EventBus.getDefault().post(event);
        stopMedia();
        //reset mediaPlayer
        mediaPlayer.reset();
        initMediaPlayer();

    }


    public void skipToPrevious(ArrayList<Audio> audioList, int audioIndex, Context context, MediaPlayer mediaPlayer) {

        if (audioIndex == 0) {
            //if first in playlist
            //set index to the last of audioList
            audioIndex = audioList.size() - 1;
            activeAudio = audioList.get(audioIndex);

        } else {
            //get previous in playlist
            activeAudio = audioList.get(--audioIndex);
        }

        //Update stored index
        new StorageUtil(getContext()).storeAudioIndex(audioIndex);

        stopMedia();
        //reset mediaPlayer
        mediaPlayer.reset();
        initMediaPlayer();
        responseIntent.putExtra(AppMainServiceEvent.RESPONSE_DATA, activeAudio);
        event.setMainIntent(responseIntent);
        event.setEventType(AppMainServiceEvent.ONCOMPLETED_RESPONSE);
        EventBus.getDefault().post(event);
    }


    private void buildNotification(PlaybackStatus playbackStatus) {

        int notificationAction = android.R.drawable.ic_media_pause;//needs to be initialized
        PendingIntent play_pauseAction = null;

        //Build a new notification according to the current state of the MediaPlayer
        if (playbackStatus == PlaybackStatus.PLAYING) {
            notificationAction = R.drawable.ic_pause_circle_filled_black_24dp;
            //create the pause action
            play_pauseAction = playbackAction(1);
        } else if (playbackStatus == PlaybackStatus.PAUSED) {
            notificationAction = R.drawable.ic_play_circle_filled_white_black_24dp;
            //create the play action
            play_pauseAction = playbackAction(0);
        }

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_launcher); //replace with your own image

        // Create a new Notification
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setShowWhen(false)
                // Set the Notification style
                .setStyle(new NotificationCompat.MediaStyle()
                        // Attach our MediaSession token
                        .setMediaSession(mediaSession.getSessionToken())
                        // Show our playback controls in the compact notification view.
                        .setShowActionsInCompactView(0, 1, 2))
                // Set the Notification color
                .setColor(getResources().getColor(R.color.colorPrimary))
                // Set the large and small icons
                .setLargeIcon(largeIcon)
                .setSmallIcon(android.R.drawable.stat_sys_headset)
                // Set Notification content information
                .setContentText(activeAudio.getArtist())
                .setContentTitle(activeAudio.getAlbum())
                .setContentInfo(activeAudio.getTitle())
                // Add playback actions
                .addAction(android.R.drawable.ic_media_previous, "previous", playbackAction(3))
                .addAction(notificationAction, "pause", play_pauseAction)
                .addAction(android.R.drawable.ic_media_next, "next", playbackAction(2));

        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    private void removeNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }


    private PendingIntent playbackAction(int actionNumber) {
        Intent playbackAction = new Intent(this, MediaPlayerService.class);
        switch (actionNumber) {
            case 0:
                // Play
                playbackAction.setAction(ACTION_PLAY);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 1:
                // Pause
                playbackAction.setAction(ACTION_PAUSE);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 2:
                // Next track
                playbackAction.setAction(ACTION_NEXT);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 3:
                // Previous track
                playbackAction.setAction(ACTION_PREVIOUS);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            default:
                break;
        }
        return null;
    }


    public Audio getActiveAudio() {
        //TODO Here I want to check for the ending of list stuff
        StorageUtil storage = new StorageUtil(getContext());
        audioList = storage.loadAudio();
        audioIndex = storage.loadAudioIndex();
        activeAudio = audioList.get(audioIndex);
        return activeAudio;
    }

    public int getPosn() {
        return mediaPlayer.getCurrentPosition();
    }

    public int getDur() {
        return duration;
    }

    public int getCurrentDur() {
        return mediaPlayer.getCurrentPosition();
    }


    public boolean isPng() {
        return mediaPlayer.isPlaying();
    }

    public void pausePlayer() {
        mediaPlayer.pause();
    }

    public void seek(int posn) {
        mediaPlayer.seekTo(posn);
    }

    public void go() {
        mediaPlayer.start();
    }


    public void playNext() {
        audioIndex++;
        if (audioIndex != audioList.size())
            audioIndex = 0;
        playMedia();
    }


    public void playPrev() {
        audioIndex--;
        if (audioIndex != 0) audioIndex = audioList.size() - 1;
        playMedia();
    }


    private void handleIncomingActions(Intent playbackAction) {
        if (playbackAction == null || playbackAction.getAction() == null) return;

        String actionString = playbackAction.getAction();
        if (actionString.equalsIgnoreCase(ACTION_PLAY)) {
            transportControls.play();
        } else if (actionString.equalsIgnoreCase(ACTION_PAUSE)) {
            transportControls.pause();
        } else if (actionString.equalsIgnoreCase(ACTION_NEXT)) {
            transportControls.skipToNext();
        } else if (actionString.equalsIgnoreCase(ACTION_PREVIOUS)) {
            transportControls.skipToPrevious();
        } else if (actionString.equalsIgnoreCase(ACTION_STOP)) {
            transportControls.stop();
        }
    }


    //The system calls this method when an activity, requests the service be started
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            //Load data from SharedPreferences
            StorageUtil storage = new StorageUtil(getContext());
            audioList = storage.loadAudio();
            audioIndex = storage.loadAudioIndex();

            if (audioIndex != -1 && audioIndex < audioList.size()) {
                //index is in a valid range
                activeAudio = audioList.get(audioIndex);
            } else {
                stopSelf();
            }
        } catch (NullPointerException e) {
            stopSelf();
        }

        //Request audio focus
        if (requestAudioFocus() == false) {
            //Could not gain focus
            stopSelf();
        }

        if (mediaSessionManager == null) {
            try {
                initMediaSession();
                initMediaPlayer();
            } catch (RemoteException e) {
                e.printStackTrace();
                stopSelf();
            }
            buildNotification(PlaybackStatus.PLAYING);
        }

        //Handle Intent action from MediaSession.TransportControls
        handleIncomingActions(intent);
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            stopMedia();
            mediaPlayer.release();
        }
        removeAudioFocus();
        //Disable the PhoneStateListener
        if (phoneStateListener != null) {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }

        removeNotification();

        //unregister BroadcastReceivers
        unregisterReceiver(becomingNoisyReceiver);
        unregisterReceiver(playNewAudio);

        //clear cached playlist
        new StorageUtil(getApplicationContext()).clearCachedAudioPlaylist();
    }


    public boolean isPlayBackSupposedToContinue() {
        if (playbackStatus == PlaybackStatus.PLAYING) {
            return true;
        } else if (playbackStatus == PlaybackStatus.PAUSED) {

            return false;
        } else {

            return false;
        }
    }


}