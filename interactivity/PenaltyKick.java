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

import utility.RealWorldMapping;

/**
 * A signal for a penalty kick. The data specific to this class are: the taker's name, the outcome and the name of the team having won the penalty
 * 
 * @author Andreas Tasoulas
 *
 */

public class PenaltyKick extends Signal {
    
    private String taker;
    private int outcome;
    private String teamName;
    
    public PenaltyKick(int timerStart, String taker, int outcome, String teamName) {
        super(timerStart);
        this.taker = taker;
        this.outcome = outcome;
        this.teamName = teamName;
    }
    
    public String getTaker() {
        return this.taker;
    }
    
    public int getOutcome() {
        return this.outcome;
    }
    
    public String getTeamName() {
        return this.teamName;
    }
    
    public void setTaker(String taker) {
        this.taker = taker;
    }
    
    public void setOutcome(int outcome) {
        this.outcome = outcome;
    }
    
    public String toString() {
        
        String outcomeDescription = "";
        
        switch(this.outcome) {
        case RealWorldMapping.PENALTY_GOAL:
            outcomeDescription = "Goal";
            break;
        case RealWorldMapping.PENALTY_GOAL_KICK:
            outcomeDescription = "Goal kick";
            break;
        case RealWorldMapping.PENALTY_SAVE_DEFENDER:
            outcomeDescription = "Saved by Gk";
            break;
        }
        
        return this.teamName + " : " + "Penalty taken from " + taker + " : " + outcomeDescription;
        
    }

}
