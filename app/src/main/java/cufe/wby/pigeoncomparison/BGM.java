package cufe.wby.pigeoncomparison;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.media.MediaPlayer;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class BGM extends IntentService {

    // action声明
    public static final String ACTION_MUSIC = "cufe.wby.pigeoncomparison.action.music";

    // 声明MediaPlayer对象
    private MediaPlayer mediaPlayer;

    public BGM() {
        super("BGM");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();

            // 根据intent设置的action来执行对应服务的操作
            if (ACTION_MUSIC.equals(action)){
                handleActionMusic();
            }
        }
    }
    /**
     * 该服务执行的操作用来播放背景音乐
     */
    private void handleActionMusic() {

        if (mediaPlayer == null){
            // 根据音乐资源文件创建MediaPlayer对象 设置循环播放属性 开始播放
            mediaPlayer = MediaPlayer.create(this, R.raw.asoul);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }

    }

}