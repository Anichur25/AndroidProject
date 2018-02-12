package com.example.hp.simple2dgame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;


public class ObstacleManager {

    private ArrayList<Obstracle> obstacles;
    private int playerGap;
    private int obstacleGap;
    private int obstacleHeight;
    private int color;
    private long startTime;
    private long initTime;
    private int score;

    public ObstacleManager(int playerGap,int obstacleGap,int obstacleHeight,int color)
    {
        this.playerGap = playerGap;
        this.obstacleGap = obstacleGap;
        this.obstacleHeight = obstacleHeight;
        this.color = color;
        startTime = initTime = System.currentTimeMillis();
        obstacles = new ArrayList<>();
        populateObstacle();
    }

    public boolean playerCollide(RectPlayer player)
    {
        for(Obstracle ob : obstacles)
        {
            if(ob.playerCollide(player))
            {
                return true;
            }
        }

        return false;
    }

    private void populateObstacle()
    {
        int currY = -5*Constant.SCREEN_HEIGHT/4;

        while(currY < 0)
        {
            int xStart = (int)(Math.random()*(Constant.SCREEN_WIDTH - playerGap));
            obstacles.add(new Obstracle(obstacleHeight,color,xStart,currY,playerGap));
            currY+= obstacleHeight + obstacleGap;
        }
    }

    public void update()
    {
        if(startTime < Constant.INIT_TIME)
            startTime = Constant.INIT_TIME;
        int elapsedTime = (int)(System.currentTimeMillis() - startTime);
        startTime = System.currentTimeMillis();
        float speed = (float)(Math.sqrt(1 + (startTime - initTime) / 2000.0))*Constant.SCREEN_HEIGHT / (10000.0f);

        for(Obstracle ob : obstacles)
        {
            ob.increamentY(speed * elapsedTime);
        }

        if(obstacles.get(obstacles.size() - 1).getRectangle().top>=Constant.SCREEN_HEIGHT)
        {
            int xStart = (int)(Math.random()*(Constant.SCREEN_WIDTH - playerGap));
            obstacles.add(0,new Obstracle(obstacleHeight,color,xStart,obstacles.get(0).getRectangle().top - obstacleHeight - obstacleGap,playerGap));
            obstacles.remove(obstacles.size() - 1);
            score++;
        }
    }

    public void draw(Canvas canvas)
    {
        for(Obstracle ob : obstacles)
        {
            ob.draw(canvas);
            Paint paint = new Paint();
            paint.setTextSize(100);
            paint.setColor(Color.BLUE);
            canvas.drawText("" + score ,50,50 + paint.descent() - paint.ascent(),paint);
        }
    }


}
