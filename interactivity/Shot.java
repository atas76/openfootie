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

package interactivity;

import utility.*;

/**
 * A signal for a shot. With each shot we associate a basic outome (goal, on target, off target), a detailed outcome (the state of the ball in play
 * after the shot, e.g has the goalie caught it or is it a corner kick?) and we keep the 'shooter's' name and team name, as well
 * 
 * @author Andreas Tasoulas
 *
 */

public class Shot extends Signal {
    
    private int basicOutcome;
    private int detailedOutcome;
    private String shooter;
    private String teamName;
    
    public Shot(int timerStart, int basicOutcome, int detailedOutcome, String shooter, String teamName) {
        super(timerStart);
        this.basicOutcome = basicOutcome;
        this.detailedOutcome = detailedOutcome;
        this.shooter = shooter;
        this.teamName = teamName;
    }
    
    public int getBasicOutcome() {
        return this.basicOutcome;
    }
    
    public int getDetailedOutcome() {
        return this.detailedOutcome;
    }
    
    public String getShooter() {
        return this.shooter;
    }
    
    public String getTeamName() {
        return this.teamName;
    }
    
    public String toString() {
        
        String basicOutcomeStr = "";
        String details = "";
        
        switch (this.basicOutcome) {
        case RealWorldMapping.GOAL:
            basicOutcomeStr = "Goal scored with shot";
            break;
        case RealWorldMapping.SHOT_ON:
            
            basicOutcomeStr = "Shot On";
            
            switch(this.detailedOutcome) {
            case RealWorldMapping.AFTER_SHOT_DEFENDER:
                details = "Ball to defender";
                break;
            case RealWorldMapping.AFTER_SHOT_GK:
                details = "Ball to Gk";
                break;
            case RealWorldMapping.AFTER_SHOT_CORNER_KICK:
                details = "Corner kick";
                break;
            case RealWorldMapping.AFTER_SHOT_FORWARD:
                details = "Rebound";
                break;
            case RealWorldMapping.AFTER_SHOT_DEFENDER_SAVE_THROW_IN:
                details = "Defender saves: Throw in";
                break;
            case RealWorldMapping.AFTER_SHOT_THROW_IN:
                details = "Throw in";
                break;
            case RealWorldMapping.AFTER_SHOT_POST_GOAL_KICK:
                details = "Post: Goal kick";
                break;
            case RealWorldMapping.AFTER_SHOT_POST_DEFENDER:
                details = "Post: Defender";
                break;
            case RealWorldMapping.AFTER_SHOT_POST_FORWARD:
                details = "Post: Rebounder";
                break;
            case RealWorldMapping.AFTER_SHOT_POST_GK:
                details = "Post: Goalkeeper";
                break;
            case RealWorldMapping.AFTER_SHOT_DEFENDER_SAVE_FORWARD:
                details = "Defender saves: Rebound";
                break;
            }
            
            break;
        case RealWorldMapping.SHOT_OFF:
            
            basicOutcomeStr = "Shot Off";
            
            if (detailedOutcome == 1) {
                details = "Corner kick";
            } else {
                details = "Goal kick";
            }
            
            break;
        }
        
        return this.teamName + " : " + basicOutcomeStr + " by " + shooter + " -> " + details;
        
    }

}
