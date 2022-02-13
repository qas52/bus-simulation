package trial;


import java.io.File;

import java.io.FileNotFoundException;

import java.io.IOException;

import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

public class Main {
	public static int nstation;  
	public static int travtime;
	public static int boardtime;
	public static int lambda;
	public static LinkedList <Event> businitialise(LinkedList <Event> e1,int btotal, int stotal) { //Initializing buses
		
		int evenspacing =stotal/btotal;
		int i=1;
		
		for( ;i<=btotal;i++)
		{		
		Event add1 = new Event(2,0,i,i*evenspacing); 
	    e1.add(add1);
		}
		return e1;
	}
	
	public static LinkedList <Event> personinitialise(LinkedList <Event> e1,int stotal)  //Initializing people at each stop
	{

		int i=1;
		for( ;i<=stotal;i++)
		
			{		
				Event add1 = new Event(1,0,0,i); 
			    e1.add(add1);
			}
				return e1;
		
			
	}
	public static double interArrivalTime(int lamda) {       //function for determining arrival time of next person
       
		Random rand = new Random();
         double Z= -Math.log(1-rand.nextDouble())/(lamda);
        
       
         return (double) (Math.round(Z * Math.pow(10, 3)) / Math.pow(10, 3))*100; // return the time(seconds) that next person will arrive
    }
	
	public static LinkedList <Event> parrival(LinkedList <Event> e1,double currtime, int staname){  // function for creating person arrival event and inserting for event handler
		double nexttime= interArrivalTime(lambda);
		Event add1= new Event(1,currtime+nexttime,0,staname);
		int i=0;
		while(i<e1.size()) {
		if(e1.get(i).startTime<add1.startTime)
			i++;
		else
			break;
		}
		e1.add(i,add1);
		return e1;
	}
	 public static LinkedList <Event> barrival( LinkedList <Event> e1,double currtime,int bno,int staname){ // function for creating bus arrival event and inserting for event handler
		 staname= (staname % nstation)+1;   //loops back to 1 after reaching last stop
		 Event add1= new Event(2,currtime+travtime,bno,staname);
		int i=0;
		while(i<e1.size()) {
			if(e1.get(i).startTime<add1.startTime)
				i++;
			else
				break;
			}
			e1.add(i,add1);
		 return e1;
	 }
	 
	 public static LinkedList <Event> boarding( LinkedList <Event> e1, double currtime, int bno, int staname){ // function for creating boarding event and inserting for event handler
		 Event add1= new Event(3,currtime+boardtime,bno,staname);
		 int i=0;
		 while(i<e1.size()) {
				if(e1.get(i).startTime<add1.startTime)
					i++;
				else
					break;
				}
				e1.add(i,add1);
		 return e1;
	 }
	
	
	
	public static void main(String[] args) throws IOException {
	    LinkedList <Event> evqueue = new LinkedList<Event>();  // event queue
	    
	   
	  //Instantiating a file class
	    Scanner file = null;
		int nbuses=0;
		
		try {
			file = new Scanner(new File("D:\\input.txt"));         //reading input from file 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		
		while(file.hasNext()){                               //storing the simulation parameters
			if (file.hasNextInt())
				{nbuses=file.nextInt();
			    nstation=file.nextInt();
			    travtime=file.nextInt();
			    boardtime=file.nextInt();
			    lambda=file.nextInt();}
			else file.next();
		}
	    
	    
	    int[] queue= new int[nstation+1];               //reason for nstation+1 is due to fact that stationname starts from 1 and not 0
	    evqueue=personinitialise(evqueue,nstation);        //initialization process
	    evqueue= businitialise(evqueue,nbuses,nstation);
	   
	    double time=0;                            //counter for simulation time
	    double monitertime=3600;
	    int[][] moniteroutput= new int[4][nstation+1]; 
	    int [] busposition= new int [nbuses+1];
	    while(time<28800) {                  //28800= 8hrs
	    	
	    	Event event= evqueue.poll();
	    	double buslimit=0;
			switch (event.eventType) {                   //depending on the event, appropriate action is taken
	    	
	    	case 1:
	    	       ++queue[event.stationName];
	    	       //queue index is the station name and the value stored at that location is the people in queue
	    	       if(moniteroutput[2][event.stationName]<queue[event.stationName])
	    	    	   moniteroutput[2][event.stationName]=queue[event.stationName];
	    	       else if (moniteroutput[3][event.stationName]>queue[event.stationName])
	    	    	   moniteroutput[3][event.stationName]=queue[event.stationName];
	    	       evqueue=parrival(evqueue,event.startTime,event.stationName);
	    	       time=event.startTime;
	    	       
	    	       
	    	       break;
	   	case 2:    ++moniteroutput[1][event.stationName];
	   	           moniteroutput[0][event.stationName]= queue[event.stationName]+moniteroutput[0][event.stationName];
	    	       busposition[event.busName]=event.stationName;
	   	          evqueue=barrival(evqueue,event.startTime,event.busName,event.stationName); 
	    	        buslimit= event.startTime+ travtime;
	    	       if(queue[event.stationName]>0) //  boarding event will be generated when queue at that station is greater than 0
 	               {evqueue=boarding(evqueue,event.startTime,event.busName,event.stationName);
 	                time=event.startTime; 
 	               break;}
 	               
	    	case 3: 
	    		   --queue[event.stationName];
 	             
 	               if(queue[event.stationName]>0&& event.startTime<buslimit)   //next boarding event will be generated only when queue at that station is greater than 0 or if time is left before bus leaves for next station
 	               {evqueue=boarding(evqueue,event.startTime,event.busName,event.stationName);
 	                time=event.startTime; 
 	               break;}
 	               else {
 	            	  
 	            	   time=event.startTime;
 	            	   break;}
 	               
 	              }
	    
	        if(time==monitertime) {
	        	System.out.println(time/3600 +"h");
	        	for( int i=1;i<=nstation;i++)
	        	{   
	        		if(moniteroutput[1][i]==0) //for intervals where a stop hasnt been visited by bus at all
	        			++moniteroutput[1][i];
	        		int avgsize= moniteroutput[0][i]/moniteroutput[1][i];
	        	   System.out.println("the station "+ i + " has avg size  "+  avgsize);
	        	   System.out.println("max queue size is " + moniteroutput[2][i]);
	        	   System.out.println("min queue size is " + moniteroutput[3][i]);
	        	   System.out.println("-----------------------------------------");
	        	}
	        	
	        	for(int i=0;i<3;i++)        //resets value for next time period record
	        	{ for( int j=0;j<nstation+1;j++)
	        	 moniteroutput[i][j]=0;
	        	 
	        	}
	        	for(int j=0;j<=nstation;j++)
	        		moniteroutput[3][j]=1000; // did separate or else min value wont be recorded
	        	for( int i=1;i<nbuses+1;i++)
	        	System.out.println("bus"+ i +" station:"+ busposition[i]);
	        	monitertime=time+3600;
	        }
	        	
	    
	    
	    }
	    
	} 

}
