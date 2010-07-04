/*
 * Copyright 2010 Andreas Tasoulas
 *  
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 *  
 */

package core;

import interactivity.EndOfHalf;
import interactivity.Signal;

import java.util.ArrayList;

/**
 * This class handles the match report functionality relevant to match interruptions
 * 
 * @author Andreas Tasoulas
 *
 */

public class MatchRewind extends MatchReport {
    
    private ArrayList<Signal> events = new ArrayList<Signal>();
    private Signal currentSignal;
    private boolean secondHalf;
    
    /**
     * Removes all match events from a certain time onward to be overwritten by the updated events
     * 
     * @param startTime The virtual time from which the events will be ovewritten
     * 
     */
    public void remove(int startTime) {
        
        ArrayList<Signal> temp = new ArrayList<Signal>();
        
        for (Signal currentSignal:events) {
            if (currentSignal.getTime() <= startTime) {
                temp.add(currentSignal);
            } else {
                break;
            }
        }
        
        this.events = temp;
        
    }
    
    public boolean isSecondHalf() {
        return this.secondHalf;
    }
    
    public void resetHalf() {
        this.secondHalf = false;
    }
    
    /**
     * 
     * Adds a signal as an event and turns it to a current signal. Changes the 'second half status' if the signal is of type 'End of Half'.
     * 
     * @param currentSignal
     */
    public void addSignal(Signal currentSignal) {
        
        events.add(events.size(), currentSignal);
        this.currentSignal = currentSignal;
        
        if (currentSignal instanceof EndOfHalf){
            this.secondHalf = true;
        }
    }
    
    /**
     * Reset the current signal
     *
     */
    public void reset() {
        this.currentSignal = null;
    }
    
    public Signal getCurrentSignal() {
        return this.currentSignal;
    }
    
    public ArrayList<Signal> getMatchEvents() {
        return this.events;
    }
    
    public void setMatchEvents(ArrayList<Signal> matchEvents) {
        this.events = matchEvents;
    }
    
    /**
     * Display all the match events so far
     *
     */
    public void dump() {
        for (Signal currentSignal:events) {
            System.out.println(currentSignal.getTime() + " " + currentSignal);     
        }
    }
    
}
