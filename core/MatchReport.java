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

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;

import report.ReportObject;

/**
 * This class handles the match report functionality
 * 
 * @author Andreas Tasoulas
 * 
 */

public class MatchReport {
    
    public final static int halfDuration = 255;
    
    private State currentState;
    private Team firstHalfKickOffTeam;
    private int timer;
    private double time;
    
    private ArrayList<ReportObject> report = new ArrayList<ReportObject>();
    private TreeMap<HighLightOrdinal, String> highlights = new TreeMap<HighLightOrdinal, String>();
    private ReportObject currentEvent = new ReportObject();
    
    public ArrayList<ReportObject> getReport() {
        return report;
    }
    
    /**
     * Clears from match report all the highlights that are later than a specific virtual time
     * @param time The virtual time after which the highlights will be cleared
     */
    public void clearLaterHighlights(int time) {
        
        // System.out.println("---- CLEAR HIGHLIGHTS ----");
        // System.out.println("Highlights before : " + highlights.size());
        
        Set<HighLightOrdinal> highlightKeys = highlights.keySet();
        TreeMap<HighLightOrdinal, String> retainedHighlights = new TreeMap<HighLightOrdinal, String>();
        
        for (HighLightOrdinal highlight:highlightKeys) {
            if (highlight.getTime() <= time) {
                retainedHighlights.put(highlight, highlights.get(highlight));
            }
        }
        
        highlights = retainedHighlights;
        
        // System.out.println("Highlights after: " + highlights.size());
        // System.out.println("---- CLEAR HIGHLIGHTS ----");
    }
    
    /**
     * Get only the highlights that are earlier from a specific time
     * @param time The virtual time up to which the highlights will be returned
     * @return The 'earlier' highlights list
     */
    public TreeMap<HighLightOrdinal, String> getEarlierHighlights(int time) {
        
        Set<HighLightOrdinal> highlightKeys = highlights.keySet();
        TreeMap<HighLightOrdinal, String> earlierHighlights = new TreeMap<HighLightOrdinal, String>();
        
        for (HighLightOrdinal highlight:highlightKeys) {
            if (highlight.getTime() < time) {
                earlierHighlights.put(highlight, highlights.get(highlight));
            }
        }
        
        return earlierHighlights;
        
    }
    
    public TreeMap<HighLightOrdinal, String> getHighlights() {
        return highlights;
    }
    
    /**
     * Append a highlight to match report
     * @param time The virtual time of a highlight
     * @param highlight The highlight as a String. Please note that throughout this documentation the word 'highlight' might have two slightly 
     * different meanings. Here it means just a line in the report. In other places it may mean the highlight as a 'whole' comprising more than one
     * lines
     */
    public void appendHighlight(Integer time, String highlight) {
        
        // System.out.println("Current highlight: " + highlight);
        
        ArrayList<HighLightOrdinal> simultaneousHighlights = new ArrayList<HighLightOrdinal>();
        
        HighLightOrdinal currentHighlight = null;
        HighLightOrdinal newHighlight = null;
        
        for (HighLightOrdinal regHighlight:highlights.keySet()) {
            // System.out.println("Sought highlight: " + regHighlight);
            if (regHighlight.isSimultaneous(time)) {
                // System.out.println("Simultaneous: true");
                // System.out.println("Registered highlight: " + regHighlight);
                currentHighlight = regHighlight;
            } else {
                if (currentHighlight != null) break;
            }
        }
        
        if (currentHighlight == null) {
            newHighlight = new HighLightOrdinal(time, 1);
        } else {
            newHighlight = new HighLightOrdinal(time, currentHighlight.getSequence() + 1);
        }
        
        // System.out.println("Highlight sequence: " + newHighlight.getSequence());
        
        // System.out.println("Highlights before: " + highlights.size());
        // System.out.println(newHighlight);
        
        highlights.put(newHighlight, highlight);
        
        // System.out.println("Highlights after: " + highlights.size());
    }
   
    /**
     * Add the current event to the report and reset it afterwards
     *
     */
    public void submitEvent() {
        report.add(currentEvent);
        currentEvent = new ReportObject();
    }
    
    public ReportObject getCurrentEvent() {
        return currentEvent;
    }
    
    public void setCurrentState(State currentState) {
        this.currentState = currentState;
    }
    
    public State getCurrentState() {
        return currentState;
    }
    
    public void setFirstHalfKickOff(Team team) {
        this.firstHalfKickOffTeam = team;
    }
    
    public Team getFirstHalfKickOff() {
        return this.firstHalfKickOffTeam;
    }
    
    public void setTimer(int timer) {
        this.timer = timer;
    }
    
    public int getTimer() {
        return this.timer;
    }
    
    /** 
     * Converts the time from the virtual (turn-based) timer to the 'real-world' 'user-friendly' simulated 90-minute time
     * 
     * @return The time in simulated minutes
     */
    public double getTime() {
        return Math.ceil(((double) timer / (double) halfDuration) * 45d);
    }
    
    /**
     * Getter for a String representation of the current scoreline
     * @param homeTeam The home team object
     * @param awayTeam The away team object
     * @return The scoreline
     */
    public String getScoreLine(Team homeTeam, Team awayTeam) {
        return homeTeam.getName() + " - " + awayTeam.getName() + " " + 
            homeTeam.getStats().getGoalsScored() + " - " + awayTeam.getStats().getGoalsScored();
    }
}
