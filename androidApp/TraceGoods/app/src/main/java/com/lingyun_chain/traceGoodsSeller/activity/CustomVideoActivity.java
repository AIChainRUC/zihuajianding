//package com.lingyun_chain.zihua.activity;
//
//import android.content.Context;
//import android.content.Intent;
//import android.content.res.Configuration;
//import android.hardware.Camera;
//import android.media.CamcorderProfile;
//import android.media.MediaRecorder;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.SystemClock;
//import android.util.Log;
//import android.view.SurfaceHolder;
//import android.view.SurfaceView;
//import android.view.View;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.Chronometer;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import com.lingyun_chain.zihua.R;
//import com.lingyun_chain.zihua.base.BaseActivity;
//
//import java.io.File;
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//public class CustomVideoActivity extends BaseActivity implements View.OnClickListener {
//    private static final String TAG = "CustomRecordActivity";
//    public static final int CONTROL_CODE = 1;
//    //UI
//    private ImageView mRecordControl;
//    // private ImageView mPauseRecord;
//    private SurfaceView surfaceView;
//    private SurfaceHolder mSurfaceHolder;
//
//    //DATA
//    private boolean isRecording;// 标记，判断当前是否正在录制
//    private boolean isPause; //暂停标识
//
//    //    private Handler mHandler = new MyHandler(CustomVideoActivity.this);
//    //
//    //    private static class MyHandler extends Handler {
//    //        private final WeakReference<CustomVideoActivity> mActivity;
//    //
//    //        public MyHandler(CustomVideoActivity activity) {
//    //            mActivity = new WeakReference<>(activity);
//    //        }
//    //
//    //        @Override
//    //        public void handleMessage(Message msg) {
//    //            System.out.println(msg);
//    //            if (mActivity.get() == null) {
//    //                return;
//    //            }
//    //            switch (msg.what) {
//    //                case CONTROL_CODE:
//    //                    //开启按钮
//    //                    mActivity.get().mRecordControl.setEnabled(true);
//    //                    break;
//    //            }
//    //        }
//    //    }
//
//    private MediaRecorder.OnErrorListener OnErrorListener = new MediaRecorder.OnErrorListener() {
//        @Override
//        public void onError(MediaRecorder mediaRecorder, int what, int extra) {
//            try {
//                if (mediaRecorder != null) {
//                    mediaRecorder.reset();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    };
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        //无title
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        //全屏
//        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
//                WindowManager.LayoutParams. FLAG_FULLSCREEN);
//        setContentView(R.layout.activity_custom_video);
//        initView();
//    }
//
//    private void initView() {
//        surfaceView = (SurfaceView) findViewById(R.id.record_surfaceView);
//        mRecordControl = (ImageView) findViewById(R.id.record_control);
//        mRecordTime = (Chronometer) findViewById(R.id.record_time);
//        //mPauseRecord = (ImageView) findViewById(R.id.record_pause);
//        mRecordControl.setOnClickListener(this);
//        //mPauseRecord.setOnClickListener(this);
//        //mPauseRecord.setEnabled(false);
//
//        //配置SurfaceHodler
//        mSurfaceHolder = surfaceView.getHolder();
//        // 设置Surface不需要维护自己的缓冲区
//        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//        // 设置分辨率
//        mSurfaceHolder.setFixedSize(320, 280);
//        // 设置该组件不会让屏幕自动关闭
//        mSurfaceHolder.setKeepScreenOn(true);
//        mSurfaceHolder.addCallback(mCallBack);//回调接口
//        mRecordTime.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
//            @Override
//            public void onChronometerTick(Chronometer chronometer) {
//                if ((SystemClock.elapsedRealtime() - chronometer.getBase()) > 5 * 1000) {
//                    mRecordTime.stop();
//                    mPauseTime = 0;
//                    //UiUtils.show("视频已录制完成，正在处理中，请稍候。。。");
//                    setToResult();
//                    //setResult(RESULT_OK);
//                    //finish();
//                }
//            }
//        });
//    }
//
//    private SurfaceHolder.Callback mCallBack = new SurfaceHolder.Callback() {
//        @Override
//        public void surfaceCreated(SurfaceHolder surfaceHolder) {
//            initCamera();
//        }
//
//        @Override
//        public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
//            if (mSurfaceHolder.getSurface() == null) {
//                return;
//            }
//        }
//
//        @Override
//        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
//            stopCamera();
//        }
//    };
//
//    /**
//     * 初始化摄像头
//     *
//     * @throws IOException
//     * @author liuzhongjun
//     * @date 2016-3-16
//     */
//    private void initCamera() {
//        if (mCamera != null) {
//            stopCamera();
//        }
//        //默认启动后置摄像头
//        mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
//        if (mCamera == null) {
//            Toast.makeText(this, "未能获取到相机！", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        try {
//            mCamera.setPreviewDisplay(mSurfaceHolder);
//            //配置CameraParams
//            setCameraParams();
//            //启动相机预览
//            mCamera.startPreview();
//        } catch (IOException e) {
//            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
//        }
//    }
//
//
//    /**
//     * 设置摄像头为竖屏
//     *
//     * @author lip
//     * @date 2015-3-16
//     */
//    private void setCameraParams() {
//        if (mCamera != null) {
//            Camera.Parameters params = mCamera.getParameters();
//            //设置相机的横竖屏(竖屏需要旋转90°)
//            if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
//                params.set("orientation", "portrait");
//                mCamera.setDisplayOrientation(90);
//            } else {
//                params.set("orientation", "landscape");
//                mCamera.setDisplayOrientation(0);
//            }
//            //设置聚焦模式
//            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
//            //缩短Recording启动时间
//            params.setRecordingHint(true);
//            //影像稳定能力
//            if (params.isVideoStabilizationSupported())
//                params.setVideoStabilization(true);
//            mCamera.setParameters(params);
//        }
//    }
//
//
//    /**
//     * 开始录制视频
//     */
//    public void startRecord() {
//        initCamera();
//        mCamera.unlock();
//        setConfigRecord();
//        try {
//            //开始录制
//            mediaRecorder.prepare();
//            mediaRecorder.start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        //isRecording = true;
//        if (mPauseTime != 0) {
//            mRecordTime.setBase(SystemClock.elapsedRealtime() - (mPauseTime - mRecordTime.getBase()));
//        } else {
//            mRecordTime.setBase(SystemClock.elapsedRealtime());
//        }
//        mRecordTime.start();
//        mRecordControl.setVisibility(View.INVISIBLE);
//    }
//
//
//    public void pauseRecord() {
//
//
//    }
//
//
//    @Override
//    public void onClick(View view) {
//        if (view.getId() == R.id.record_control)
//            //开始录制视频
//            startRecord();
//        //mRecordControl.setImageResource(R.mipmap.recordvideo_stop);
//        //                    mRecordControl.setEnabled(false);//1s后才能停止
//        //                    mHandler.sendEmptyMessageDelayed(CONTROL_CODE, 100);
//        //停止视频录制
//        //                    mRecordControl.setImageResource(R.mipmap.recordvideo_start);
//        //                    mPauseRecord.setVisibility(View.GONE);
//        //                    mPauseRecord.setEnabled(false);
//        //                    stopRecord();
//        //                    mCamera.lock();
//        //                    stopCamera();
//        //                    mRecordTime.stop();
//        //                    mPauseTime = 0;
//        //                    new Thread(new Runnable() {
//        //                        @Override
//        //                        public void run() {
//        //                            try {
//        //                                if (!(saveVideoPath.equals(""))) {
//        //                                    String[] str = new String[]{saveVideoPath, currentVideoFilePath};
//        //                                    VideoUtil.appendVideo(CustomVideoActivity.this, getSDPath(CustomVideoActivity.this) + "append.mp4", str);
//        //                                    File reName = new File(saveVideoPath);
//        //                                    File f = new File(getSDPath(CustomVideoActivity.this) + "append.mp4");
//        //                                    f.renameTo(reName);//将合成的视频复制过来
//        //                                    if (reName.exists()) {
//        //                                        f.delete();
//        //                                        new File(currentVideoFilePath).delete();
//        //                                    }
//        //                                }
//        //                                setResult(RESULT_OK);
//        //                                finish();
//        //                            } catch (IOException e) {
//        //                                e.printStackTrace();
//        //                            }
//        //                        }
//        //                    }).start();
//
//        //            case R.id.record_pause:
//        //                if (isRecording) { //正在录制的视频，点击后暂停
//        //                    mPauseRecord.setImageResource(R.mipmap.control_play);
//        //                    //暂停视频录制
//        //                    mCamera.autoFocus(new Camera.AutoFocusCallback() {
//        //                        @Override
//        //                        public void onAutoFocus(boolean success, Camera camera) {
//        //                            if (success == true)
//        //                                CustomVideoActivity.this.mCamera.cancelAutoFocus();
//        //                        }
//        //                    });
//        //                    stopRecord();
//        //                    mRecordTime.stop();
//        //                    isPause = true;
//        //                    if (saveVideoPath.equals("")) {
//        //                        saveVideoPath = currentVideoFilePath;
//        //                    } else {
//        //                        new Thread(new Runnable() {
//        //                            @Override
//        //                            public void run() {
//        //                                try {
//        //                                    String[] str = new String[]{saveVideoPath, currentVideoFilePath};
//        //                                    VideoUtil.appendVideo(CustomVideoActivity.this, getSDPath(getApplicationContext()) + "append.mp4", str);
//        //                                    File reName = new File(saveVideoPath);
//        //                                    File f = new File(getSDPath(getApplicationContext()) + "append.mp4");
//        //                                    f.renameTo(reName);
//        //                                    if (reName.exists()) {
//        //                                        f.delete();
//        //                                        new File(currentVideoFilePath).delete();
//        //                                    }
//        //                                } catch (IOException e) {
//        //                                    e.printStackTrace();
//        //                                }
//        //                            }
//        //                        }).start();
//        //                    }
//        //
//        //                } else {
//        //                    mPauseRecord.setImageResource(R.mipmap.control_pause);
//        //                    if (mPauseTime != 0) {
//        //                        mRecordTime.setBase(SystemClock.elapsedRealtime() - (mPauseTime - mRecordTime.getBase()));
//        //                    } else {
//        //                        mRecordTime.setBase(SystemClock.elapsedRealtime());
//        //                    }
//        //                    mRecordTime.start();
//        //                    //继续视频录制
//        //                    startRecord();
//        //                    isPause = false;
//        //     }
//        //                break;
//        //        }
//
//    }
//
//
//    /**
//     * 创建视频文件保存路径
//     */
//    private boolean createRecordDir() {
//        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
//            Toast.makeText(this, "请查看您的SD卡是否存在！", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//        File sampleDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Record");
//        if (!sampleDir.exists()) {
//            sampleDir.mkdirs();
//        }
//        String recordName = "VID_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".mp4";
//        mVecordFile = new File(sampleDir, recordName);
//        currentVideoFilePath = mVecordFile.getAbsolutePath();
//        return true;
//    }
//
//
//    public static String getSDPath(Context context) {
//        File sdDir = null;
//        boolean sdCardExist = Environment.getExternalStorageState().equals(
//                Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
//        if (sdCardExist) {
//            sdDir = Environment.getExternalStorageDirectory();
//        } else if (!sdCardExist) {
//
//            Toast.makeText(context, "SD卡不存在", Toast.LENGTH_SHORT).show();
//
//        }
//        File eis = new File(sdDir.toString() + "/Video/");
//        try {
//            if (!eis.exists()) {
//                eis.mkdir();
//            }
//        } catch (Exception e) {
//
//        }
//        return sdDir.toString() + "/Video/";
//    }
//
//
//    /**
//     * 配置MediaRecorder()
//     */
//    private void setConfigRecord() {
//        mediaRecorder = new MediaRecorder();
//        mediaRecorder.reset();
//        mediaRecorder.setCamera(mCamera);
//        mediaRecorder.setOnErrorListener(OnErrorListener);
//
//        //使用SurfaceView预览
//        mediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
//
//        //1.设置采集声音
//        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        //设置采集图像
//        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
//        //2.设置视频，音频的输出格式 mp4
//        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
//        //3.设置音频的编码格式
//        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
//        //设置图像的编码格式
//        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
//        //设置立体声
//        //        mediaRecorder.setAudioChannels(2);
//        //设置最大录像时间 单位：毫秒
//        //        mediaRecorder.setMaxDuration(60 * 1000);
//        //设置最大录制的大小 单位，字节
//        //        mediaRecorder.setMaxFileSize(1024 * 1024);
//        //音频一秒钟包含多少数据位
//        CamcorderProfile mProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
//        mediaRecorder.setAudioEncodingBitRate(44100);
//        if (mProfile.videoBitRate > 2 * 1024 * 1024)
//            mediaRecorder.setVideoEncodingBitRate(2 * 1024 * 1024);
//        else
//            mediaRecorder.setVideoEncodingBitRate(1024 * 1024);
//        mediaRecorder.setVideoFrameRate(mProfile.videoFrameRate);
//
//        //设置选择角度，顺时针方向，因为默认是逆向90度的，这样图像就是正常显示了,这里设置的是观看保存后的视频的角度
//        mediaRecorder.setOrientationHint(90);
//        //设置录像的分辨率
//        mediaRecorder.setVideoSize(352, 288);
//
//        //设置录像视频保存地址
//        currentVideoFilePath = Environment.getExternalStorageDirectory() + "/Pictures" + getVideoName();
//        mediaRecorder.setOutputFile(currentVideoFilePath);
//    }
//
//    private String getVideoName() {
//        return "VID_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".mp4";
//    }
//
//
//    private void setToResult() {
//        Intent intent = new Intent();
//        intent.putExtra("videoPath", currentVideoFilePath);
//        setResult(RESULT_OK, intent);
//        finish();
//    }
//}
