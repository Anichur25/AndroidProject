package com.example.hp.simple2dgame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;


public class GamePlayScene implements Scene {

    private Rect r = new Rect();
    private RectPlayer player;

    private Point playerpoint;
    private ObstacleManager obstacleManager;
    private boolean movingPlayer = false;

    private boolean gameOver = false;
    private long gameOverTime;

    private OrientationData orientationData;
    private long frameTime;

    public GamePlayScene()
    {
        player = new RectPlayer(new Rect(100,100,200,200), Color.rgb(255,0,0));
        playerpoint = new Point(Constant.SCREEN_WIDTH/2 , 3*Constant.SCREEN_HEIGHT/4);
        player.update(playerpoint);
        obstacleManager = new ObstacleManager(200,350,75,Color.BLACK);
        orientationData  = new OrientationData();
        orientationData.register();
        frameTime = System.currentTimeMillis();
    }

    public void reset()
    {
        playerpoint = new Point(Constant.SCREEN_WIDTH/2 , 3*Constant.SCREEN_HEIGHT/4);
        player.update(playerpoint);
        obstacleManager = new ObstacleManager(200,350,75,Color.BLACK);
        movingPlayer = false;
    }


    public void update()
    {
        if(!gameOver) {

            if(frameTime < Constant.INIT_TIME)
                frameTime = Constant.INIT_TIME;
            int elapsedTime = (int)(System.currentTimeMillis() - frameTime);
            frameTime = System.currentTimeMillis();
            if(orientationData.getOrientation() != null && orientationData.getStartOrientation() != null)
            {
                float pitch = orientationData.getOrientation()[1] - orientationData.getStartOrientation()[1];
                float roll = orientationData.getOrientation()[2] - orientationData.getStartOrientation()[2];

                float xSpeed = 2 * roll * Constant.SCREEN_WIDTH / 1000f;
                float ySpeed = pitch * Constant.SCREEN_HEIGHT / 1000f;
                playerpoint.x+= Math.abs(elapsedTime * xSpeed) > 5? xSpeed * elapsedTime : 0;
                playerpoint.y-= Math.abs(elapsedTime * ySpeed) > 5? ySpeed * elapsedTime : 0;
            }

            if(playerpoint.x < 0)
                playerpoint.x = 0;
            else if(playerpoint.x > Constant.SCREEN_WIDTH)
                playerpoint.x = Constant.SCREEN_WIDTH;
            if(playerpoint.y < 0)
                playerpoint.y = 0;
            else if(playerpoint.y > Constant.SCREEN_HEIGHT)
                playerpoint.y = Constant.SCREEN_HEIGHT;

            player.update(playerpoint);
            obstacleManager.update();

            if(obstacleManager.playerCollide(player))
            {
                gameOver = true;
                gameOverTime = System.currentTimeMillis();
            }
        }
    }

    public void recieveTouch(MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                if(!gameOver && player.getRectangle().contains((int) event.getX(),(int)event.getY()));
                movingPlayer = true;
                if(gameOver && System.currentTimeMillis() - gameOverTime >= 2000)
                {
                    reset();
                    gameOver = false;
                    orientationData.newGame();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(!gameOver && movingPlayer)
                    playerpoint.set((int)event.getX(),(int)event.getY());
                break;
            case MotionEvent.ACTION_UP:
                movingPlayer = false;
                break;

        }
    }


    public void draw(Canvas canvas)
    {
        canvas.drawColor(Color.WHITE);
        player.draw(canvas);
        obstacleManager.draw(canvas);

        if(gameOver)
        {
            Paint paint = new Paint();
            paint.setTextSize(100);
            paint.setColor(Color.RED);
            drawCenterText(canvas,paint,"GAME OVER");
        }
    }

    public void terminate()
    {
        SceneManager.ACTIVE_SCENE = 0;
    }

    private void drawCenterText(Canvas canvas,Paint paint,String text)
    {
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.getClipBounds(r);
        int cHeight = r.height();
        int cWidth = r.width();
        paint.getTextBounds(text,0,text.length(),r);
        float x = cWidth/2f - r.width()/2f - r.left;
        float y = cHeight/2f + r.height()/2f - r.bottom;
        canvas.drawText(text,x,y,paint);
    }
}
