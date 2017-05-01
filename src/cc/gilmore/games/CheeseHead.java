package cc.gilmore.games;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;

public class CheeseHead extends Applet implements Runnable {
	private static final long serialVersionUID = -3079726382636183051L;
	
	final int     numBlocksX=17;
	final int     numBlocksY=15;
	final short   levelData[][] = {
		//Level 1
		{
			19,26,26,22, 9,12,19,26,22, 9,12,19,26,26,18,22,1,
			37,11,14,17,26,26,20,15,17,26,26,20,11,14,21,37,2,
			17,26,26,20,11, 6,17,26,20, 3,14,17,26,26,20,21,3,
			21, 3, 6,25,22, 5,21, 7,21, 5,19,28, 3, 6,21,21,4,
			21, 9, 8,14,21,13,21, 5,37,13,21,11, 8,12,37,37,5,

			25,18,26,18,24,18,28, 5,25,18,24,18,26,18,26,20,6,
			 6,21, 7,21, 7,21,11, 8,14,21, 7,21, 7,21, 3,21,7,
			 4,21, 5,21, 5,21,11,10,14,21, 5,21, 5,21, 1,21,8,
			12,21,13,21,13,21,11,10,14,21,13,21,13,21, 9,21,9,
			19,24,26,24,26,16,26,18,26,16,26,24,26,24,18,20,10,
			
			21, 3, 2, 2, 6,21,15,21,15,21, 3, 2, 2,06,21,21,11,
			21, 9, 8, 8, 4,17,26, 8,26,20, 1, 8, 8,12,21,21,12,
			17,26,26,22,13,21,11, 2,14,21,13,19,26,26,20,21,13,
			37,11,14,17,26,24,22,13,19,24,26,20,11,14,21,37,14,
			25,26,26,28, 3, 6,25,26,28, 3, 6,25,26,26,26,28,15
		},
		//Level 2
		{
			19,26,26,22, 9,12,19,26,22, 9,12,19,26,26,22,22,
			37,11,14,17,26,26,20,15,17,26,26,20,11,14,37,22,
			17,26,26,20,11, 6,17,26,20, 3,14,17,26,26,20,22,
			21, 3, 6,25,22, 5,21, 7,21, 5,19,28, 3, 6,21,22,
			21, 9, 8,14,21,13,21, 5,37,13,21,11, 8,12,37,22,
			
			25,18,26,18,24,18,28, 5,25,18,24,18,26,18,28,22,
			6,21, 7,21, 7,21,11, 8,14,21, 7,21, 7,21,03,22,
			4,21, 5,21, 5,21,11,10,14,21, 5,21, 5,21, 1,22,
			12,21,13,21,13,21,11,10,14,21,13,21,13,21, 9,22,
			19,24,26,24,26,16,26,18,26,16,26,24,26,24,22,22,
			
			21, 3, 2, 2, 6,21,15,21,15,21, 3, 2, 2,06,21,22,
			21, 9, 8, 8, 4,17,26, 8,26,20, 1, 8, 8,12,21,22,
			17,26,26,22,13,21,11, 2,14,21,13,19,26,26,20,22,
			37,11,14,17,26,24,22,13,19,24,26,20,11,14,37,22,
			25,26,26,28, 3, 6,25,26,28, 3, 6,25,26,26,28,22
		}
	};
	
	private final String imgDir = "../pacpix/";

	private int currentLevel = 1;
	private int ghostStartXBlock = 7;
	private int ghostStartYBlock = 7;
	private int pacmanStartXBlock = 7;
	private int pacmanStartYBlock = 11;
	
	Dimension	d;
	Font 		largefont = new Font("Helvetica", Font.BOLD, 24);
	Font		smallfont = new Font("Helvetica", Font.BOLD, 14);

	FontMetrics	fmsmall, fmlarge;  
	Graphics	goff;
	Image		ii;
	Thread	thethread;
	MediaTracker  thetracker = null;
	Color	dotcolor=new Color(192,192,0);
	int		bigdotcolor=192;
	int		dbigdotcolor=-2;
	Color	mazecolor;

	boolean	ingame=false;
	boolean	showtitle=true;
	boolean scared=false;
	boolean dying=false;

	final int	screendelay=120;
	final int   blocksize=24;
	final int	animdelay=8;
	final int   pacanimdelay=2;
	final int   ghostanimcount=2;
	final int   pacmananimcount=4;
	final int   maxghosts=12;
	final int	pacmanspeed=3;

	int		animcount=animdelay;
	int     pacanimcount=pacanimdelay;
	int		pacanimdir=1;
	int		count=screendelay;
	int		ghostanimpos=0;
	int		pacmananimpos=0;
	int		nrofghosts=6;
	int		pacsleft,score;
	int		deathcounter;
	int[]	dx,dy;
	int[]	ghostx, ghosty, ghostdx, ghostdy, ghostspeed;

	Image	ghost1,ghost2,ghostscared1,ghostscared2;
	Image	pacman1, pacman2up, pacman2left, pacman2right, pacman2down;
	Image	pacman3up, pacman3down, pacman3left, pacman3right;
	Image	pacman4up, pacman4down, pacman4left, pacman4right;

	int		pacmanx, pacmany, pacmandx, pacmandy;
	int		reqdx, reqdy, viewdx, viewdy;
	int		scaredcount, scaredtime;
	final	int	maxscaredtime=120;
	final int   minscaredtime=20;

	final int   scrsizeX=numBlocksX*blocksize;
	final int   scrsizeY=numBlocksY*blocksize;
	final int	validspeeds[] = { 1,2,3,4,5,6 };
	final int	maxspeed=6;

	int		currentspeed=1;
	short[]	screendata;


	public String getAppletInfo()
	{
		return("CheeseHead, like PacMan");
	}

	public void init()
	{
		GetImages();

		screendata=new short[numBlocksX*numBlocksY];

		setSize(420/*screenWidth*/, 420/*screenHeight*/);
		d = size();
		setBackground(Color.black);
		Graphics g=getGraphics();
		g.setFont(smallfont);
		fmsmall = g.getFontMetrics();
		g.setFont(largefont);
		fmlarge = g.getFontMetrics();
		ghostx=new int[maxghosts];
		ghostdx=new int[maxghosts];
		ghosty=new int[maxghosts];
		ghostdy=new int[maxghosts];
		ghostspeed=new int[maxghosts];
		dx=new int[4];
		dy=new int[4];
		
		GameInit();
	}

	public void GameInit()
	{
		pacsleft=3;
		score=0;
		LevelInit();
		nrofghosts=6;
		currentspeed=3;
		scaredtime=maxscaredtime;
	}

	public void LevelInit()
	{
		for (int i=0; i<numBlocksX*numBlocksY; i++)
			screendata[i]=levelData[currentLevel-1][i];

		LevelContinue();
	}

	public void LevelContinue()
	{
		int dx=1;
		int random;

		for (int i=0; i<nrofghosts; i++)
		{
			ghosty[i]=ghostStartXBlock*blocksize;
			ghostx[i]=ghostStartYBlock*blocksize;
			ghostdy[i]=0;
			ghostdx[i]=dx;
			dx=-dx;
			random=(int)(Math.random()*(currentspeed));
			if (random>(currentspeed-1))
				random=currentspeed-1;
			ghostspeed[i]=validspeeds[random];
		}
		screendata[7*numBlocksX+6]=10;
		screendata[7*numBlocksX+8]=10;
		pacmanx=pacmanStartXBlock*blocksize;
		pacmany=pacmanStartYBlock*blocksize;
		pacmandx=0;
		pacmandy=0;
		reqdx=0;
		reqdy=0;
		viewdx=-1;
		viewdy=0;
		dying=false;
		scared=false;
	}

	public void GetImages()
	{
		thetracker=new MediaTracker(this);

		ghost1=getImage(getDocumentBase(),imgDir+"Ghost1.gif");
		thetracker.addImage(ghost1,0);
		ghost2=getImage(getDocumentBase(),imgDir+"Ghost2.gif");
		thetracker.addImage(ghost2,0);
		ghostscared1=getImage(getDocumentBase(),imgDir+"GhostScared1.gif");
		thetracker.addImage(ghostscared1,0);
		ghostscared2=getImage(getDocumentBase(),imgDir+"GhostScared2.gif");
		thetracker.addImage(ghostscared2,0);

		pacman1=getImage(getDocumentBase(),imgDir+"PacMan1.gif");
		thetracker.addImage(pacman1,0);
		pacman2up=getImage(getDocumentBase(),imgDir+"PacMan2up.gif");
		thetracker.addImage(pacman2up,0);
		pacman3up=getImage(getDocumentBase(),imgDir+"PacMan3up.gif");
		thetracker.addImage(pacman3up,0);
		pacman4up=getImage(getDocumentBase(),imgDir+"PacMan4up.gif");
		thetracker.addImage(pacman4up,0);

		pacman2down=getImage(getDocumentBase(),imgDir+"PacMan2down.gif");
		thetracker.addImage(pacman2down,0);
		pacman3down=getImage(getDocumentBase(),imgDir+"PacMan3down.gif");
		thetracker.addImage(pacman3down,0);
		pacman4down=getImage(getDocumentBase(),imgDir+"PacMan4down.gif");
		thetracker.addImage(pacman4down,0);

		pacman2left=getImage(getDocumentBase(),imgDir+"PacMan2left.gif");
		thetracker.addImage(pacman2left,0);
		pacman3left=getImage(getDocumentBase(),imgDir+"PacMan3left.gif");
		thetracker.addImage(pacman3left,0);
		pacman4left=getImage(getDocumentBase(),imgDir+"PacMan4left.gif");
		thetracker.addImage(pacman4left,0);

		pacman2right=getImage(getDocumentBase(),imgDir+"PacMan2right.gif");
		thetracker.addImage(pacman2right,0);
		pacman3right=getImage(getDocumentBase(),imgDir+"PacMan3right.gif");
		thetracker.addImage(pacman3right,0);
		pacman4right=getImage(getDocumentBase(),imgDir+"PacMan4right.gif");
		thetracker.addImage(pacman4right,0);

		try
		{
			thetracker.waitForAll();
		}
		catch (InterruptedException e)
		{
			return;
		}
	}

	public boolean keyDown(Event e, int key)
	{
		if (ingame)
		{
			if (key == Event.LEFT)
			{
				reqdx=-1;
				reqdy=0;
			}
			else if (key == Event.RIGHT)
			{
				reqdx=1;
				reqdy=0;
			}
			else if (key == Event.UP)
			{
				reqdx=0;
				reqdy=-1;
			}
			else if (key == Event.DOWN)
			{
				reqdx=0;
				reqdy=1;
			}
			else if (key == Event.ESCAPE)
			{
				ingame=false;
			}
		}
		else
		{
			if (key == 's' || key == 'S')
			{
				ingame=true;
				GameInit();
			}
		}
		return true;
	}

	public boolean keyUp(Event e, int key)
	{
		if (key == Event.LEFT || key == Event.RIGHT || key == Event.UP ||  key == Event.DOWN) {
			reqdx=0;
			reqdy=0;
		}
		return true;
	}

	public void paint(Graphics g)
	{
		d = size();
		if (goff==null && d.width>0 && d.height>0)
		{
			ii = createImage(d.width, d.height);
			goff = ii.getGraphics();
		}
		if (goff==null || ii==null)
			return;

		goff.setColor(Color.black);
		goff.fillRect(0, 0, d.width, d.height);

		DrawMaze();
		DrawScore();
		DoAnim();
		
		if (ingame)
			PlayGame();
		else
			PlayDemo();

		g.drawImage(ii, 0, 0, this);
	}

	public void DoAnim()
	{
		animcount--;
		if (animcount<=0)
		{
			animcount=animdelay;
			ghostanimpos++;
			if (ghostanimpos>=ghostanimcount)
				ghostanimpos=0;
		}
		pacanimcount--;
		if (pacanimcount<=0)
		{
			pacanimcount=pacanimdelay;
			pacmananimpos=pacmananimpos+pacanimdir;
			if (pacmananimpos==(pacmananimcount-1) ||  pacmananimpos==0)
				pacanimdir=-pacanimdir;
		}
	}

	public void PlayGame()
	{
		if (dying)
		{
			Death();
		}
		else
		{
			CheckScared();
			MovePacMan();
			DrawPacMan();
			MoveGhosts();
			CheckMaze();
		}  
	}

	public void PlayDemo()
	{
		CheckScared();
		MoveGhosts();
		ShowIntroScreen();
	}

	public void Death()
	{
		deathcounter--;
		int k=(deathcounter&15)/4;
		switch(k)
		{
		case 0:
			goff.drawImage(pacman4up,pacmanx+1,pacmany+1,this);
			break;
		case 1:
			goff.drawImage(pacman4right,pacmanx+1,pacmany+1,this);
			break;
		case 2:
			goff.drawImage(pacman4down,pacmanx+1,pacmany+1,this);
			break;
		default:
			goff.drawImage(pacman4left,pacmanx+1,pacmany+1,this);
		}
		if (deathcounter==0)
		{
			pacsleft--;
			if (pacsleft==0)
				ingame=false;
			LevelContinue();
		}
	}

	public void MoveGhosts()
	{
		int pos;
		int count;

		for (int i=0; i<nrofghosts; i++)
		{
			if (ghostx[i]%blocksize==0 && ghosty[i]%blocksize==0)
			{
				pos=ghostx[i]/blocksize+numBlocksX*(int)(ghosty[i]/blocksize);

				count=0;
				if ((screendata[pos]&1)==0 && ghostdx[i]!=1)
				{
					dx[count]=-1;
					dy[count]=0;
					count++;
				}
				if ((screendata[pos]&2)==0 && ghostdy[i]!=1)
				{
					dx[count]=0;
					dy[count]=-1;
					count++;
				}
				if ((screendata[pos]&4)==0 && ghostdx[i]!=-1)
				{
					dx[count]=1;
					dy[count]=0;
					count++;
				}
				if ((screendata[pos]&8)==0 && ghostdy[i]!=-1)
				{
					dx[count]=0;
					dy[count]=1;
					count++;
				}
				if (count==0)
				{
					if ((screendata[pos]&15)==15)
					{
						ghostdx[i]=0;
						ghostdy[i]=0;
					}
					else
					{
						ghostdx[i]=-ghostdx[i];
						ghostdy[i]=-ghostdy[i];
					}
				}
				else
				{
					count=(int)(Math.random()*count);
					if (count>3) count=3;
					ghostdx[i]=dx[count];
					ghostdy[i]=dy[count];
				}
			}
			ghostx[i]=ghostx[i]+(ghostdx[i]*ghostspeed[i]);
			ghosty[i]=ghosty[i]+(ghostdy[i]*ghostspeed[i]);
			DrawGhost(ghostx[i]+1,ghosty[i]+1);

			if (pacmanx>(ghostx[i]-12) && pacmanx<(ghostx[i]+12) &&
					pacmany>(ghosty[i]-12) && pacmany<(ghosty[i]+12) && ingame)
			{
				if (scared)
				{
					score+=10;
					ghostx[i]=7*blocksize;
					ghosty[i]=7*blocksize;
				}
				else
				{
					dying=true;
					deathcounter=64;
				}
			}
		}
	}

	public void DrawGhost(int x, int y)
	{
		if (ghostanimpos==0 && !scared) {
			goff.drawImage(ghost1,x,y,this);
		}
		else if (ghostanimpos==1 && !scared) {
			goff.drawImage(ghost2,x,y,this);
		}
		else if (ghostanimpos==0 && scared) {
			goff.drawImage(ghostscared1,x,y,this);
		}
		else if (ghostanimpos==1 && scared) {
			goff.drawImage(ghostscared2,x,y,this);
		}
	}

	public void MovePacMan()
	{
		if (reqdx==-pacmandx && reqdy==-pacmandy)
		{
			pacmandx=reqdx;
			pacmandy=reqdy;
			viewdx=pacmandx;
			viewdy=pacmandy;
		}
		if (pacmanx%blocksize==0 && pacmany%blocksize==0)
		{
			int pos=pacmanx/blocksize+numBlocksX*(int)(pacmany/blocksize);
			short ch=screendata[pos];
			if ((ch&16)!=0)
			{
				screendata[pos]=(short)(ch&15);
				score++;
			}
			if ((ch&32)!=0)
			{
				scared=true;
				scaredcount=scaredtime;
				screendata[pos]=(short)(ch&15);
				score+=5;
			}

			if (reqdx!=0 || reqdy!=0)
			{
				if (!( (reqdx==-1 && reqdy==0 && (ch&1)!=0) ||
						(reqdx==1 && reqdy==0 && (ch&4)!=0) ||
						(reqdx==0 && reqdy==-1 && (ch&2)!=0) ||
						(reqdx==0 && reqdy==1 && (ch&8)!=0)))
				{
					pacmandx=reqdx;
					pacmandy=reqdy;
					viewdx=pacmandx;
					viewdy=pacmandy;
				}
			}

			// Check for standstill
			if ( (pacmandx==-1 && pacmandy==0 && (ch&1)!=0) ||
					(pacmandx==1 && pacmandy==0 && (ch&4)!=0) ||
					(pacmandx==0 && pacmandy==-1 && (ch&2)!=0) ||
					(pacmandx==0 && pacmandy==1 && (ch&8)!=0))
			{
				pacmandx=0;
				pacmandy=0;
			}
		}
		pacmanx=pacmanx+pacmanspeed*pacmandx;
		pacmany=pacmany+pacmanspeed*pacmandy;
	}


	public void DrawPacMan()
	{
		if (viewdx==-1)
			DrawPacManLeft();
		else if (viewdx==1)
			DrawPacManRight();
		else if (viewdy==-1)
			DrawPacManUp();
		else
			DrawPacManDown();
	}

	public void DrawPacManUp()
	{
		switch(pacmananimpos)
		{
		case 1:
			goff.drawImage(pacman2up,pacmanx+1,pacmany+1,this);
			break;
		case 2:
			goff.drawImage(pacman3up,pacmanx+1,pacmany+1,this);
			break;
		case 3:
			goff.drawImage(pacman4up,pacmanx+1,pacmany+1,this);
			break;
		default:
			goff.drawImage(pacman1,pacmanx+1,pacmany+1,this);
			break;
		}
	}


	public void DrawPacManDown()
	{
		switch(pacmananimpos)
		{
		case 1:
			goff.drawImage(pacman2down,pacmanx+1,pacmany+1,this);
			break;
		case 2:
			goff.drawImage(pacman3down,pacmanx+1,pacmany+1,this);
			break;
		case 3:
			goff.drawImage(pacman4down,pacmanx+1,pacmany+1,this);
			break;
		default:
			goff.drawImage(pacman1,pacmanx+1,pacmany+1,this);
			break;
		}
	}


	public void DrawPacManLeft()
	{
		switch(pacmananimpos)
		{
		case 1:
			goff.drawImage(pacman2left,pacmanx+1,pacmany+1,this);
			break;
		case 2:
			goff.drawImage(pacman3left,pacmanx+1,pacmany+1,this);
			break;
		case 3:
			goff.drawImage(pacman4left,pacmanx+1,pacmany+1,this);
			break;
		default:
			goff.drawImage(pacman1,pacmanx+1,pacmany+1,this);
			break;
		}
	}


	public void DrawPacManRight()
	{
		switch(pacmananimpos)
		{
		case 1:
			goff.drawImage(pacman2right,pacmanx+1,pacmany+1,this);
			break;
		case 2:
			goff.drawImage(pacman3right,pacmanx+1,pacmany+1,this);
			break;
		case 3:
			goff.drawImage(pacman4right,pacmanx+1,pacmany+1,this);
			break;
		default:
			goff.drawImage(pacman1,pacmanx+1,pacmany+1,this);
			break;
		}
	}


	public void DrawMaze()
	{
		short i=0;

		bigdotcolor=bigdotcolor+dbigdotcolor;
		if (bigdotcolor<=64 || bigdotcolor>=192)
			dbigdotcolor=-dbigdotcolor;

		for (int y=0, x; y<scrsizeY; y+=blocksize)
		{
			for (x=0; x<scrsizeX; x+=blocksize)
			{
				goff.setColor(mazecolor);
				//left wall
				if ((screendata[i]&1)!=0) {
					goff.drawLine(x,y,x,y+blocksize-1);
				}
				//top wall
				if ((screendata[i]&2)!=0) {
					goff.drawLine(x,y,x+blocksize-1,y);
				}
				//right wall
				if ((screendata[i]&4)!=0) {
					goff.drawLine(x+blocksize-1,y,x+blocksize-1,y+blocksize-1);
				}
				//bottom wall
				if ((screendata[i]&8)!=0) {
					goff.drawLine(x,y+blocksize-1,x+blocksize-1,y+blocksize-1);
				}
				//little dot
				if ((screendata[i]&16)!=0) {
					goff.setColor(dotcolor);
					goff.fillRect(x+11,y+11,2,2);
				}
				//big dot
				if ((screendata[i]&32)!=0) {
					goff.setColor(new Color(224,224-bigdotcolor,bigdotcolor));
					goff.fillRect(x+8,y+8,8,8);
				}
				i++;
			}
		}
	}

	public void ShowIntroScreen()
	{
		String s;

		goff.setFont(largefont);
		goff.setColor(new Color(0,32,48));
		goff.fillRect(16, scrsizeY/2 - 40, scrsizeX-32,80);
		goff.setColor(Color.white);
		goff.drawRect(16, scrsizeY/2 - 40, scrsizeX-32,80);

		if (showtitle)
		{
			s="Cheese Head";
			scared=false;

			goff.setColor(Color.white);
			goff.drawString(s,(scrsizeX-fmlarge.stringWidth(s)) / 2 +2, scrsizeY/2 - 20 +2);
			goff.setColor(new Color(96,128,255));
			goff.drawString(s,(scrsizeX-fmlarge.stringWidth(s)) / 2, scrsizeY/2 - 20);

			s="www.gilmore.cc";
			goff.setFont(smallfont);
			goff.setColor(new Color(255,160,64));
			goff.drawString(s,(scrsizeX-fmsmall.stringWidth(s))/2,scrsizeY/2 + 10);
		}
		else
		{
			goff.setFont(smallfont);
			goff.setColor(new Color(96,128,255));
			s="'S' to start game";
			goff.drawString(s,(scrsizeX-fmsmall.stringWidth(s))/2,scrsizeY/2 - 10);
			goff.setColor(new Color(255,160,64));
			s="Use cursor keys to move";
			goff.drawString(s,(scrsizeX-fmsmall.stringWidth(s))/2,scrsizeY/2 + 20);
			scared=true;
		}
		count--;
		if (count<=0)
		{ count=screendelay; showtitle=!showtitle; }
	}


	public void DrawScore()
	{
		int i;
		String s;

		goff.setFont(smallfont);
		goff.setColor(new Color(96,128,255));
		s="Score: "+score;
		goff.drawString(s,scrsizeX/2+96,scrsizeY+16);
		for (i=0; i<pacsleft; i++) {
			goff.drawImage(pacman3left,i*28+8,scrsizeY+1,this);
		}
	}


	public void CheckScared()
	{
		scaredcount--;
		if (scaredcount<=0)
			scared=false;

		if (scared && scaredcount>=30)
			mazecolor=new Color(192,32,255);
		else
			mazecolor=new Color(32,192,255);

		if (scared) {
			screendata[7*numBlocksX+6]=11;
			screendata[7*numBlocksX+8]=14;
		}
		else {
			screendata[7*numBlocksX+6]=10;
			screendata[7*numBlocksX+8]=10;
		}
	}


	public void CheckMaze()
	{
		short i=0;
		boolean finished=true;

		while (i<numBlocksX*numBlocksY && finished)
		{
			if ((screendata[i]&48)!=0)
				finished=false;
			i++;
		}
		if (finished)
		{
			score+=50;
			DrawScore();
			try
			{ 
				Thread.sleep(3000);
			}
			catch (InterruptedException e)
			{
			}
			if (nrofghosts < maxghosts)
				nrofghosts++; 
			if (currentspeed<maxspeed)
				currentspeed++;
			scaredtime=scaredtime-20;
			if (scaredtime<minscaredtime)
				scaredtime=minscaredtime;
			currentLevel++;
			LevelInit();
		}
	}


	public void run()
	{
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		Graphics g=getGraphics();

		while(true)
		{
			try
			{
				paint(g);
				Thread.sleep(30);
			}
			catch (InterruptedException e)
			{
				break;
			}
		}
	}

	public void start()
	{
		if (thethread == null) {
			thethread = new Thread(this);
			thethread.start();
		}
	}

	public void stop()
	{
		if (thethread != null) {
			thethread.stop();
			thethread = null;
		}
	}
}
