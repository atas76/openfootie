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

package gameplay;

import java.util.ArrayList;
import java.util.HashMap;

import core.MatchReport;
import core.PlayerStats;

/**
 * This class is used for the 'gameplay' functionality relating to a player object. By 'gameplay', I refer to the reference host application.
 * For instance, a player's skill is tightly bound to the host application, because there needs to be an interaction with the database to read this
 * skill. Of course, this is all ad-hoc. The same functionality could be perceived as part of the 'core' package.
 * 
 * @author Andreas Tasoulas
 *
 */

public class Player extends utility.Player {
    
    private int position;
    private HashMap<String, Double> skills = new HashMap<String, Double>();
    
    private ArrayList<Integer> timeIn = new ArrayList<Integer>();
    private ArrayList<Integer> timeOut = new ArrayList<Integer>();
    
    private boolean [] posRep = new boolean[7];
    
    private PlayerStats stats = new PlayerStats();

    /**
     * Initialize the player according to 'gameplay' functionality
     * @param shirtNo Shirt number
     * @param firstName First name 
     * @param lastName Last name
     * @param position The codified value of the position as retrieved from database
     */
    public Player(int shirtNo, String firstName, String lastName, int position) {
        super(shirtNo, firstName, lastName);
        this.position = position;
        convertPosToRep(this.position);
    }
    
    /**
     * Utility function to register the virtual times when a player has entered the match
     * @param timeIn The virtual time the player has entered the match
     */
    public void setTimeIn(int timeIn) {
        this.timeIn.add(timeIn);
    }
    
    /**
     * Utility function to register the virtual times when a player has been substituted
     * @param timeOut The virtual time of the player's substitution
     */
    public void setTimeOut(int timeOut) {
        this.timeOut.add(timeOut);
    }
    
    /**
     * Calculate the simulated time in minutes a player has played in the match. Please note that the match engine naturally supports a player
     * entering the match and being substituted from the match multiple times. This is contrary to the reality, of course, but there is nothing
     * in the 'nature' of a match to not allow this to happen, i.e. we keep the match engine 'pure' and independent of any particular rules
     * and restrictions. External definition to the match engine of rules about this and maybe other subjects is a requirement of the future 
     * @return The number of minutes played by player in simulated time
     */
    public int getMinutesPlayed() {
        
        int lastStep;
        
        if (timeIn.isEmpty()) {
            return 0;
        }
        
        if (timeOut.isEmpty() || timeOut.size() < timeIn.size()) {
            lastStep = 2 * MatchReport.halfDuration;
        } else {
            lastStep = timeOut.get(timeOut.size() - 1);
        }
        
        int duration = 0;
        for (int i = 0; i < timeIn.size() - 1; i++) {
            duration += (timeOut.get(i) - timeIn.get(i));
        }
        
        duration += (lastStep - timeIn.get(timeIn.size() - 1));
        
        return convertToMinutes(duration);
    }
    
    /**
     * Utility function to convert virtual time to minutes of simulated time
     * @param timer The virtual time
     * @return The number of minutes of simulated time
     */
    private int convertToMinutes(int timer) {
        
        double retVal = ((double) timer / 510d) * 2 * MatchReport.halfDuration;
        
        return (int) retVal;
        
    }
    
    /**
     * Convert the codified position value to a position representation 'native' to the current object. Each player position is represented in the 
     * database as an integer, which can be 'decomposed' to an intuitive position (by converting it to its binary representation). This function
     * decomposes the position to a boolean array containing a 'native' representation of the player's position
     * @param position The codified position value
     */
    private void convertPosToRep(int position) {
        
        double temp = 0;
        
        for (int j = 0; j < 7; j++) {
            posRep[j] = false;
        }
        
        for (int i = 6; i >= 0; i--) {
            
            double p = Math.pow(2d, (double) i);
            
            // System.out.println("2^" + i + " == " + p);
            
            temp = position / Math.pow(2d, (double) i);
            
            // System.out.println("Temp: " + temp);
            
            if (temp >= 1) {
                posRep[i] = true;
                position -= p;
            }
        }
    }
    
    /**
     * Checks whether the player's position justifies an attribute being 'privileged', i.e. to enhance it in relation to its assigned value.
     * This is used for crossing and actions that are dependent on the player's strong foot (to determine dynamically the outcome of a cross
     * based on the side and the player's strength of foot; please note that the strength of foot is dependent on a player's designated position and
     * it is perceived as an attribute in itself) 
     * @param attr The attribute checked
     * @return Whether the attribute's value should be enhanced according to the player position
     */
    public boolean isAttrPrivileged(PlayerAttribute attr) {
        
        // First check positions then sides
        
        // Positions
        Integer [] optimalPositions = attr.getOptimalPositions();
        
        if (optimalPositions != null) {
        
            for (int i = 0; i < optimalPositions.length; i++) {
            
                // System.out.println("Optimal position: " + i);
                // System.out.println("Optimal position represenation: " + optimalPositions[i]);
                // System.out.println("Position representation: ");
                
                // for (int j = 0; j < posRep.length; j++) {
                //     System.out.print(posRep[j] + ",");
                // }
                
                // System.out.println();
            
                if (posRep[optimalPositions[i] - 1]) {
                    // System.out.println("Optimal position matched");
                    return true;
                }
            } 
        }
        
        // Sides
        if (attr.isSideAffected()) {
            // System.out.println("Side affected attribute");
            if (posRep[Constants.RIGHT_INDEX - 1] || posRep[Constants.LEFT_INDEX - 1]) {
                // System.out.println("Side affected attribute matched");
                return true;
            }
        } else if (attr.isRightFoot()) {
            // System.out.println("Right foot");
            if (posRep[Constants.RIGHT_INDEX - 1]) {
                // System.out.println("Right foot matched");
                return true;
            }
        } else if (attr.isLeftFoot()) {
            // System.out.println("Left foot");
            if (posRep[Constants.LEFT_INDEX - 1]) {
                // System.out.println("Left foot matched");
                return true;
            }
        }
        return false;  
    }
    
    public int getPosition() {
        return position;
    }
    
    public boolean [] getPositionRep() {
        return this.posRep;
    }
    
    public void setPosition(int position) {
        this.position = position;
    }
    
    /**
     * Add a player's skill and its rate in the list of its skills
     * @param attribute The player's attribute
     * @param rate The rate of the attribute
     */
    public void addSkill(String attribute, Double rate) {
        skills.put(attribute, rate);
    }
    
    public Double getSkill(String attribute) {
        return skills.get(attribute);
    }
    
    public String toString() {
        return this.getFamilyName() + " " + this.getFirstName();
    }
    
    public PlayerStats getStats() {
        return stats;
    }
    
    public void clearStats() {
        this.stats = new PlayerStats();
    }
}
