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

/**
 * The most common signal sent from the match engine to its caller application is that of a ball possession update. 
 * It comprises the team names and their respective percentage of ball possession
 * 
 * @author Andreas Tasoulas
 *
 */

public class BallPossessionUpdate extends Signal {
    
    private int homeTeamPoss;
    private int awayTeamPoss;
    private String homeTeamName;
    private String awayTeamName;
    
    public BallPossessionUpdate(int timerStart, int homeTeamPoss, int awayTeamPoss, String homeTeamName, String awayTeamName) {
        super(timerStart);
        this.homeTeamPoss = homeTeamPoss;
        this.awayTeamPoss = awayTeamPoss;
        this.homeTeamName = homeTeamName;
        this.awayTeamName = awayTeamName;
    }
    
    public int getHomeTeamPoss() {
        return this.homeTeamPoss;
    }
    
    public int getAwayTeamPoss() {
        return this.awayTeamPoss;
    }
    
    public String toString() {
        return homeTeamName + " : " + homeTeamPoss + "% " + awayTeamName + " : " + awayTeamPoss + "%";
    }

}
