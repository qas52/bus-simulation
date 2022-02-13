package trial;



public class Event {
    
    /*
     * event type
     * 0 : initial value, means nothing.
     * 1 : a person comes to a station.
     * 2: a bus arrives a station
     * 3: one person boards the bus
     
     */
    public int eventType =0;
    // the time this event occurs
    public double startTime = 0;
    // bus name
    public int busName = 0;
    // station name
    public int stationName = 0;
    
    // constructor
    public Event(int eventType, double startTime, int busName, int stationName) {
    	
    	this.eventType = eventType;
        this.startTime =(double) (Math.round(startTime * Math.pow(10, 3)) / Math.pow(10, 3)); 
        this.busName = busName;
        this.stationName = stationName;
    }
    
}
