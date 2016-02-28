package brianrossi.runforyourlife;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Random;



public class GamePanel extends SurfaceView implements SurfaceHolder.Callback
{
    public static final int WIDTH = 856;
    public static final int HEIGHT = 480;
    public static final int MOVESPEED = -5;
    private long smokeStartTime;
    private MainThread thread;
    private Background bg;
    private Player player;
    private ArrayList<SmokePuff> smoke;
    private ArrayList<TopBorder> topborder;
    private ArrayList<BotBorder> botborder;
    private Random rand = new Random();
    private int topBorderHeight;
    private int botBorderHeight;
    private boolean topDown = true;
    private boolean botDown = true;
    private boolean newGameCreated;
    private HRMRecCalc mHRM;
    private Context mContext;
    private Activity mActivity;
    private long lastRead = 0;
    private boolean hrActive = false;
    private HeartRateWindow window;
    private long lastAdvance;
    private HeartRateWindow[] path = {mHRM.Limits, mHRM.Limits, mHRM.Moderate, mHRM.Moderate, mHRM.Anaerobic,mHRM.Moderate,
            mHRM.Limits,mHRM.Low};
    //increase to slow down difficulty progression, decrease to speed up difficulty progression
    private int progressDenom = 20;

    private Explosion explosion;
    private long startReset;
    private boolean reset;
    private boolean dissapear;
    private boolean started;
    private int best;



    public GamePanel(Context context, Activity activity)
    {
        super(context);


        //add the callback to the surfaceholder to intercept events
        getHolder().addCallback(this);



        //make gamePanel focusable so it can handle events
        setFocusable(true);
        mContext = context;
        mActivity = activity;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder){
        boolean retry = true;
        int counter = 0;
        while(retry && counter<1000)
        {
            counter++;
            try{thread.setRunning(false);
                thread.join();
                retry = false;
                thread = null;

            }catch(InterruptedException e){e.printStackTrace();}

        }

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder){

        bg = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.grassbg1));
        player = new Player(BitmapFactory.decodeResource(getResources(), R.drawable.helicopter), 65, 25, 3);
        smoke = new ArrayList<SmokePuff>();

        topborder = new ArrayList<TopBorder>();
        botborder = new ArrayList<BotBorder>();
        smokeStartTime=  System.nanoTime();


        thread = new MainThread(getHolder(), this);
        //we can safely start the game loop
        thread.setRunning(true);
        thread.start();

    }
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if(event.getAction()==MotionEvent.ACTION_DOWN){
            if(!player.getPlaying() && newGameCreated && reset)
            {
                player.setPlaying(true);

            }
            if(player.getPlaying())
            {

                if(!started)started = true;
                reset = false;

            }
            return true;
        }
        if(event.getAction()==MotionEvent.ACTION_UP)
        {

            return true;
        }

        return super.onTouchEvent(event);
    }

    public void update()

    {

        if(player.getPlaying()) {

            if(botborder.isEmpty())
            {
                player.setPlaying(false);
                return;
            }
            if(topborder.isEmpty())
            {
                player.setPlaying(false);
                return;
            }
            if(System.currentTimeMillis() - lastRead > 3000){
                mHRM.recordTrial();
                lastRead = System.currentTimeMillis();
            }
            player.setY((int) (HEIGHT - (mHRM.percentInRange() * HEIGHT)));
            bg.update();
            player.update();

            if(System.currentTimeMillis() - lastAdvance> 60000){
                lastAdvance = System.currentTimeMillis();
            }



            //check bottom border collision
            for(int i = 0; i<botborder.size(); i++)
            {
                if(collision(botborder.get(i), player))
                    player.setPlaying(false);
            }
            //check top border collision
            for(int i = 0; i <topborder.size(); i++)
            {
                if(collision(topborder.get(i),player))
                    player.setPlaying(false);
            }

            System.out.println("---->" + (HEIGHT - (window.getPercentMax() * HEIGHT)));
            botBorderHeight = (int)(HEIGHT - (window.getPercentMin() * HEIGHT));
            topBorderHeight = (int)(HEIGHT - (window.getPercentMax() * HEIGHT));
            //update top border
            this.updateTopBorder();

            //udpate bottom border
            this.updateBottomBorder();

            //add smoke puffs on timer
            long elapsed = (System.nanoTime() - smokeStartTime)/1000000;
            if(elapsed > 120){
                smoke.add(new SmokePuff(player.getX(), player.getY()+10));
                smokeStartTime = System.nanoTime();
            }

            for(int i = 0; i<smoke.size();i++)
            {
                smoke.get(i).update();
                if(smoke.get(i).getX()<-10)
                {
                    smoke.remove(i);
                }
            }
        }
        else{
            player.resetDY();
            if(!reset)
            {
                newGameCreated = false;
                startReset = System.nanoTime();
                reset = true;
                dissapear = true;
                explosion = new Explosion(BitmapFactory.decodeResource(getResources(),R.drawable.explosion),player.getX(),
                        player.getY()-30, 100, 100, 25);
            }

            explosion.update();
            long resetElapsed = (System.nanoTime()-startReset)/1000000;

            if(resetElapsed > 2500 && !newGameCreated)
            {
                newGame();
            }


        }

    }
    public boolean collision(GameObject a, GameObject b)
    {
        if(Rect.intersects(a.getRectangle(), b.getRectangle()))
        {
            return true;
        }
        return false;
    }
    @Override
    public void draw(Canvas canvas)
    {
        final float scaleFactorX = getWidth()/(WIDTH*1.f);
        final float scaleFactorY = getHeight()/(HEIGHT*1.f);

        if(canvas!=null) {
            final int savedState = canvas.save();
            canvas.scale(scaleFactorX, scaleFactorY);
            bg.draw(canvas);
            if(!dissapear) {
                player.draw(canvas);
            }
            //draw smokepuffs
            for(SmokePuff sp: smoke)
            {
                sp.draw(canvas);
            }




            //draw topborder
            for(TopBorder tb: topborder)
            {
                tb.draw(canvas);
            }

            //draw botborder
            for(BotBorder bb: botborder)
            {
                bb.draw(canvas);
            }
            //draw explosion
            if(started)
            {
                explosion.draw(canvas);
            }
            drawText(canvas);
            canvas.restoreToCount(savedState);

        }
    }

    public void updateTopBorder()
    {

        for(int i = 0; i<topborder.size(); i++)
        {
            topborder.get(i).update();
            if(topborder.get(i).getX()<-20) {
                topborder.remove(i);
                //remove element of arraylist, replace it by adding a new one

                topborder.add(new TopBorder(BitmapFactory.decodeResource(getResources(),
                        R.drawable.brick),topborder.get(topborder.size()-1).getX()+20,
                        0,   topBorderHeight));
                /*if(topborder.get(topborder.size()-1).getHeight() >= topBorderHeight){
                    topDown = true;
                }
                else{
                    topDown = false;
                }
                //new border added will have larger height
                if(topDown)
                {
                    topborder.add(new TopBorder(BitmapFactory.decodeResource(getResources(),
                            R.drawable.brick),topborder.get(topborder.size()-1).getX()+20,
                            0, topborder.get(topborder.size()-1).getHeight()+1));
                }
                //new border added wil have smaller height
                else
                {
                    topborder.add(new TopBorder(BitmapFactory.decodeResource(getResources(),
                            R.drawable.brick),topborder.get(topborder.size()-1).getX()+20,
                            0, topborder.get(topborder.size()-1).getHeight()-1));
                }*/

            }
        }

    }
    public void updateBottomBorder()
    {


        //update bottom border
        for(int i = 0; i<botborder.size(); i++)
        {
            botborder.get(i).update();

            //if border is moving off screen, remove it and add a corresponding new one
            if(botborder.get(i).getX()<-20) {
                botborder.remove(i);
                botborder.add(new BotBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick
                ), botborder.get(botborder.size() - 1).getX() + 20, botBorderHeight));
                /*if (botborder.get(botborder.size() -1).getY() <= botBorderHeight){
                    botDown = false;
                }
                else{
                    botDown = true;
                }
                if (botDown) {
                    botborder.add(new BotBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick
                    ), botborder.get(botborder.size() - 1).getX() + 20, botborder.get(botborder.size() - 1
                    ).getY() + 1));
                } else {
                    botborder.add(new BotBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick
                    ), botborder.get(botborder.size() - 1).getX() + 20, botborder.get(botborder.size() - 1
                    ).getY() - 1));
                }*/
            }
        }
    }
    public void newGame()
    {
        dissapear = false;
        mHRM = new HRMRecCalc(mContext, mActivity);
        window = mHRM.Limits;
        lastAdvance = System.currentTimeMillis();
        botborder.clear();
        topborder.clear();


        smoke.clear();

        botBorderHeight = (int) (-10 + HEIGHT - (HEIGHT * (window.getPercentMin())));
        topBorderHeight = (int) (10 + HEIGHT - (HEIGHT * (window.getPercentMax())));

        player.resetDY();
        player.resetScore();
        player.setY(HEIGHT/2);

        if(player.getScore()>best)
        {
            best = player.getScore();

        }

        //create initial borders

        //initial top border
        for(int i = 0; i*20<WIDTH+40;i++)
        {
            //first top border create
            if(i==0)
            {
                topborder.add(new TopBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick
                ),i*20,0,10)); //(int) (10 + HEIGHT - (HEIGHT * (window.getPercentMax())))));
            }
            else
            {
                topborder.add(new TopBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick
                ),i*20,0, topborder.get(i-1).getHeight()));
            }
        }
        //initial bottom border
        for(int i = 0; i*20<WIDTH+40; i++)
        {
            //first border ever created
            if(i==0)
            {
                botborder.add(new BotBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick)
                        ,i*20,480));//(int) (-10 + HEIGHT - (HEIGHT * (window.getPercentMin())))));
            }
            //adding borders until the initial screen is filed
            else
            {
                botborder.add(new BotBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick),
                        i * 20, botborder.get(i - 1).getY()));
            }
        }
        mHRM.start();

        newGameCreated = true;


    }
    public void drawText(Canvas canvas)
    {
        Paint paint = new Paint();
        Paint paint2 = new Paint();
        paint2.setColor(Color.WHITE);
        paint.setColor(Color.BLUE);
        Paint paint3 = new Paint();
        paint3.setColor(Color.RED);
        paint3.setTextSize(30);
        paint3.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setTextSize(30);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawRect(9, HEIGHT - 9, 830, HEIGHT - 33, paint2);
        canvas.drawText("DISTANCE: " + (player.getScore() * 3), 10, HEIGHT - 10, paint);
        canvas.drawText("BPM: " + (int)mHRM.getLastTrial(), WIDTH - 215, HEIGHT - 10, paint3);

        if(!player.getPlaying()&&newGameCreated&&reset)
        {
            Paint paint1 = new Paint();
            paint1.setColor(Color.BLUE);
            paint1.setTextSize(40);
            paint1.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            canvas.drawRect(WIDTH/2 - 60, HEIGHT/2 - 30, WIDTH/2 + 300, HEIGHT/2 + 50, paint2);
            canvas.drawText("PRESS TO START", WIDTH/2-50, HEIGHT/2, paint1);

            paint1.setTextSize(20);
            canvas.drawText("PRESS AND HOLD TO GO UP", WIDTH/2-50, HEIGHT/2 + 20, paint1);
            canvas.drawText("RELEASE TO GO DOWN", WIDTH/2-50, HEIGHT/2 + 40, paint1);
        }
    }

}
