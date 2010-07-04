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

package report;

/**
 * 
 * @author Andreas Tasoulas
 *
 */

public class Report {
    
    public static final byte Foul = 1;
    public static final byte PenaltyMissed = 2;
    public static final byte ShotOn = 3;
    public static final byte ShotOff = 4;
    public static final byte Goal = 5;
    public static final byte Offside = 6;
    public static final byte ThrowIn = 7;
    public static final byte GoalKick = 8;
    public static final byte PenaltyGoal = 9;
    
    public static final String [] resultDescription = {null, "Foul", "Missed penalty", "Shot on target", "Shot off target", "Goal", 
        "Offside", "Throw in", "Goal kick", "Goal (pen)"};

}
