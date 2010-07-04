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

import gameplay.Player;

import java.util.ArrayList;

import utility.MathUtil;
import utility.RealWorldMapping;
import utility.Tactics;

/**
 * 
 * @author Andreas Tasoulas
 *
 */

public class PlayerStats {
    
    public PercStats gkLongPass = new PercStats();
    public PercStats longPass = new PercStats();
    public PercStats forwardPass = new PercStats();
    public PercStats flankPass = new PercStats();
    public PercStats ballControl = new PercStats();
    public PercStats dribbling = new PercStats();
    public PercStats longFlankPass = new PercStats();
    public PercStats pass = new PercStats();
    public PercStats areaPass = new PercStats();
    public PercStats runBall = new PercStats();
    public PercStats lowCross = new PercStats();
    
    public PercStats cross = new PercStats();
    
    public PercStats shots = new PercStats();
    public PercStats headingsOnTarget = new PercStats();
    
    public UnitStats personalChallenges = new UnitStats();
    public UnitStats interceptions = new UnitStats();
    
    public UnitStats saves = new UnitStats();
    public UnitStats concedings = new UnitStats();
    
    private int goalsScored;
    private ArrayList<Integer> goalsRecord = new ArrayList<Integer>();
    
    private int penaltiesMissed;
    private ArrayList<Integer> penaltiesMissedRecord = new ArrayList<Integer>();
    
    private int penaltiesSaved;
    private ArrayList<Integer> penaltiesSavedRecord = new ArrayList<Integer>();
    
    /**
     * Utility function for adjusting effort thresholds for stats categories to the current time: i.e. if a player is expected to make n passes
     * during the whole match, then it is expected to make n / 2 passes in the first half
     * @param value The threshold value to adjust
     * @param time The virtual time point
     * @return The threshold value for the time played
     */
    private int adjustThresholdToTime(int value, int time) {
        
        float timePerc = (float) time / (float) (2 * MatchReport.halfDuration);
        float currentThreshold = (float) value * timePerc;
        
        return Math.round(currentThreshold);
        
    }
    /**
     * Getter for player stats up to a specific virtual time point in match
     * @param time The virtual time point up to which the stats will be calculated
     * @return A new player stats object containing the player stats up to the time point specified
     */
    public PlayerStats getStats(int time) {
        
        PlayerStats timedPlayerStats = new PlayerStats();
        
        timedPlayerStats.goalsScored = adjustQuantity(timedPlayerStats.goalsRecord = adjustToTime(this.goalsRecord, time));
        timedPlayerStats.penaltiesMissed = adjustQuantity(timedPlayerStats.penaltiesMissedRecord = adjustToTime(this.penaltiesMissedRecord, time));
        timedPlayerStats.penaltiesSaved = adjustQuantity(timedPlayerStats.penaltiesSavedRecord = adjustToTime(this.penaltiesSavedRecord, time));
        
        timedPlayerStats.gkLongPass.successfulAttempts = 
            adjustQuantity(timedPlayerStats.gkLongPass.successRecord = adjustToTime(this.gkLongPass.successRecord, time));
        timedPlayerStats.gkLongPass.totalAttempts = 
            adjustQuantity(timedPlayerStats.gkLongPass.totalRecord = adjustToTime(this.gkLongPass.totalRecord, time));
        
        timedPlayerStats.longPass.successfulAttempts =
            adjustQuantity(timedPlayerStats.longPass.successRecord = adjustToTime(this.longPass.successRecord, time));
        timedPlayerStats.longPass.totalAttempts =
            adjustQuantity(timedPlayerStats.longPass.totalRecord = adjustToTime(this.longPass.totalRecord, time));
        
        timedPlayerStats.forwardPass.successfulAttempts =
            adjustQuantity(timedPlayerStats.forwardPass.successRecord = adjustToTime(this.forwardPass.successRecord, time));
        timedPlayerStats.forwardPass.totalAttempts =
            adjustQuantity(timedPlayerStats.forwardPass.totalRecord = adjustToTime(this.forwardPass.totalRecord, time));
        
        timedPlayerStats.flankPass.successfulAttempts =
            adjustQuantity(timedPlayerStats.flankPass.successRecord = adjustToTime(this.flankPass.successRecord, time));
        timedPlayerStats.flankPass.totalAttempts =
            adjustQuantity(timedPlayerStats.flankPass.totalRecord = adjustToTime(this.flankPass.totalRecord, time));
        
        timedPlayerStats.ballControl.successfulAttempts =
            adjustQuantity(timedPlayerStats.ballControl.successRecord = adjustToTime(this.ballControl.successRecord, time));
        timedPlayerStats.ballControl.totalAttempts =
            adjustQuantity(timedPlayerStats.ballControl.totalRecord = adjustToTime(this.ballControl.totalRecord, time));
        
        timedPlayerStats.dribbling.successfulAttempts =
            adjustQuantity(timedPlayerStats.dribbling.successRecord = adjustToTime(this.dribbling.successRecord, time));
        timedPlayerStats.dribbling.totalAttempts =
            adjustQuantity(timedPlayerStats.dribbling.totalRecord = adjustToTime(this.dribbling.totalRecord, time));
        
        timedPlayerStats.longFlankPass.successfulAttempts =
            adjustQuantity(timedPlayerStats.longFlankPass.successRecord = adjustToTime(this.longFlankPass.successRecord, time));
        timedPlayerStats.longFlankPass.totalAttempts =
            adjustQuantity(timedPlayerStats.longFlankPass.totalRecord = adjustToTime(this.longFlankPass.totalRecord, time));
        
        timedPlayerStats.pass.successfulAttempts =
            adjustQuantity(timedPlayerStats.pass.successRecord = adjustToTime(this.pass.successRecord, time));
        timedPlayerStats.pass.totalAttempts =
            adjustQuantity(timedPlayerStats.pass.totalRecord = adjustToTime(this.pass.totalRecord, time));
        
        timedPlayerStats.areaPass.successfulAttempts = 
            adjustQuantity(timedPlayerStats.areaPass.successRecord = adjustToTime(this.areaPass.successRecord, time));
        timedPlayerStats.areaPass.totalAttempts =
            adjustQuantity(timedPlayerStats.pass.totalRecord = adjustToTime(this.areaPass.totalRecord, time));
        
        timedPlayerStats.runBall.successfulAttempts =
            adjustQuantity(timedPlayerStats.runBall.successRecord = adjustToTime(this.runBall.successRecord, time));
        timedPlayerStats.runBall.totalAttempts =
            adjustQuantity(timedPlayerStats.runBall.totalRecord = adjustToTime(this.runBall.totalRecord, time));
        
        timedPlayerStats.lowCross.successfulAttempts =
            adjustQuantity(timedPlayerStats.lowCross.successRecord = adjustToTime(this.lowCross.successRecord, time));
        timedPlayerStats.lowCross.totalAttempts =
            adjustQuantity(timedPlayerStats.lowCross.totalRecord = adjustToTime(this.lowCross.totalRecord, time));
        
        timedPlayerStats.cross.successfulAttempts =
            adjustQuantity(timedPlayerStats.cross.successRecord = adjustToTime(this.cross.successRecord, time));
        timedPlayerStats.cross.totalAttempts =
            adjustQuantity(timedPlayerStats.cross.totalRecord = adjustToTime(this.cross.totalRecord, time));
        
        timedPlayerStats.shots.successfulAttempts =
            adjustQuantity(timedPlayerStats.shots.successRecord = adjustToTime(this.shots.successRecord, time));
        timedPlayerStats.shots.totalAttempts =
            adjustQuantity(timedPlayerStats.shots.totalRecord = adjustToTime(this.shots.totalRecord, time));
        
        timedPlayerStats.headingsOnTarget.successfulAttempts =
            adjustQuantity(timedPlayerStats.headingsOnTarget.successRecord = adjustToTime(this.headingsOnTarget.successRecord, time));
        timedPlayerStats.headingsOnTarget.totalAttempts =
            adjustQuantity(timedPlayerStats.headingsOnTarget.totalRecord = adjustToTime(this.headingsOnTarget.totalRecord, time));
        
        timedPlayerStats.personalChallenges.occurences =
            adjustQuantity(timedPlayerStats.personalChallenges.occurenceRecord = adjustToTime(this.personalChallenges.occurenceRecord, time));
        
        timedPlayerStats.interceptions.occurences =
            adjustQuantity(timedPlayerStats.interceptions.occurenceRecord = adjustToTime(this.interceptions.occurenceRecord, time));
        
        timedPlayerStats.saves.occurences =
            adjustQuantity(timedPlayerStats.saves.occurenceRecord = adjustToTime(this.saves.occurenceRecord, time));
        
        timedPlayerStats.concedings.occurences = 
            adjustQuantity(timedPlayerStats.concedings.occurenceRecord = adjustToTime(this.concedings.occurenceRecord, time));
        
        return timedPlayerStats;
        
    }
    
    /**
     * Adjust all player stats to their values in a specific time
     * @param time The virtual time to which player stats will be adjusted
     */
    public void adjustToTime(int time) {
        
        this.goalsScored = adjustQuantity(this.goalsRecord = adjustToTime(this.goalsRecord, time));
        this.penaltiesMissed = adjustQuantity(this.penaltiesMissedRecord = adjustToTime(this.penaltiesMissedRecord, time));
        this.penaltiesSaved = adjustQuantity(this.penaltiesSavedRecord = adjustToTime(this.penaltiesSavedRecord, time));
        
        this.gkLongPass.successfulAttempts = adjustQuantity(this.gkLongPass.successRecord = adjustToTime(this.gkLongPass.successRecord, time));
        this.gkLongPass.totalAttempts = adjustQuantity(this.gkLongPass.totalRecord = adjustToTime(this.gkLongPass.totalRecord, time));
        
        this.longPass.successfulAttempts = adjustQuantity(this.longPass.successRecord = adjustToTime(this.longPass.successRecord, time));
        this.longPass.totalAttempts = adjustQuantity(this.longPass.totalRecord = adjustToTime(this.longPass.totalRecord, time));
        
        this.forwardPass.successfulAttempts = adjustQuantity(this.forwardPass.successRecord = adjustToTime(this.forwardPass.successRecord, time));
        this.forwardPass.totalAttempts = adjustQuantity(this.forwardPass.totalRecord = adjustToTime(this.forwardPass.totalRecord, time));
        
        this.flankPass.successfulAttempts = adjustQuantity(this.flankPass.successRecord = adjustToTime(this.flankPass.successRecord, time));
        this.flankPass.totalAttempts = adjustQuantity(this.flankPass.totalRecord = adjustToTime(this.flankPass.totalRecord, time));
        
        this.ballControl.successfulAttempts = adjustQuantity(this.ballControl.successRecord = adjustToTime(this.ballControl.successRecord, time));
        this.ballControl.totalAttempts = adjustQuantity(this.ballControl.totalRecord = adjustToTime(this.ballControl.totalRecord, time));
        
        this.dribbling.successfulAttempts = adjustQuantity(this.dribbling.successRecord = adjustToTime(this.dribbling.successRecord, time));
        this.dribbling.totalAttempts = adjustQuantity(this.dribbling.totalRecord = adjustToTime(this.dribbling.totalRecord, time));
        
        this.longFlankPass.successfulAttempts = adjustQuantity(this.longFlankPass.successRecord = adjustToTime(this.longFlankPass.successRecord, time));
        this.longFlankPass.totalAttempts = adjustQuantity(this.longFlankPass.totalRecord = adjustToTime(this.longFlankPass.totalRecord, time));
        
        this.pass.successfulAttempts = adjustQuantity(this.pass.successRecord = adjustToTime(this.pass.successRecord, time));
        this.pass.totalAttempts = adjustQuantity(this.pass.totalRecord = adjustToTime(this.pass.totalRecord, time));
        
        this.areaPass.successfulAttempts = adjustQuantity(this.areaPass.successRecord = adjustToTime(this.areaPass.successRecord, time));
        this.areaPass.totalAttempts = adjustQuantity(this.areaPass.totalRecord = adjustToTime(this.areaPass.totalRecord, time));
        
        this.runBall.successfulAttempts = adjustQuantity(this.runBall.successRecord = adjustToTime(this.runBall.successRecord, time));
        this.runBall.totalAttempts = adjustQuantity(this.runBall.totalRecord = adjustToTime(this.runBall.totalRecord, time));
        
        this.lowCross.successfulAttempts = adjustQuantity(this.lowCross.successRecord = adjustToTime(this.lowCross.successRecord, time));
        this.lowCross.totalAttempts = adjustQuantity(this.lowCross.totalRecord = adjustToTime(this.lowCross.totalRecord, time));
        
        this.cross.successfulAttempts = adjustQuantity(this.cross.successRecord = adjustToTime(this.cross.successRecord, time));
        this.cross.totalAttempts = adjustQuantity(this.cross.totalRecord = adjustToTime(this.cross.totalRecord, time));
        
        this.shots.successfulAttempts = adjustQuantity(this.shots.successRecord = adjustToTime(this.shots.successRecord, time));
        this.shots.totalAttempts = adjustQuantity(this.shots.totalRecord = adjustToTime(this.shots.totalRecord, time));
        
        this.headingsOnTarget.successfulAttempts = 
            adjustQuantity(this.headingsOnTarget.successRecord = adjustToTime(this.headingsOnTarget.successRecord, time));
        
        this.headingsOnTarget.totalAttempts =
            adjustQuantity(this.headingsOnTarget.totalRecord = adjustToTime(this.headingsOnTarget.totalRecord, time));
        
        this.personalChallenges.occurences = 
            adjustQuantity(this.personalChallenges.occurenceRecord = adjustToTime(this.personalChallenges.occurenceRecord, time));
        
        this.interceptions.occurences = 
            adjustQuantity(this.interceptions.occurenceRecord = adjustToTime(this.interceptions.occurenceRecord, time));
        
        this.saves.occurences = adjustQuantity(this.saves.occurenceRecord = adjustToTime(this.saves.occurenceRecord, time));
        
        this.concedings.occurences = adjustQuantity(this.concedings.occurenceRecord = adjustToTime(this.concedings.occurenceRecord, time));
        
    }
    
    /**
     * Utility function for making the code a little more 'function-oriented': 
     * @param statRecord The stats record ArrayList. Could have been any ArrayList for that matter
     * @return The size of the ArrayList
     */
    private int adjustQuantity(ArrayList<Integer> statRecord) {
        return statRecord.size();
    }
    
    /**
     * Adjust a specific statistic category to a specific time
     * @param statsRecord The timeline of a specific statistic category
     * @param time The virtual time from which the statistic category will not be taken into account
     * @return The timeline of the statistic category up to a specific virtual time
     */
    private ArrayList<Integer> adjustToTime(ArrayList<Integer> statsRecord, int time) {
        
        ArrayList<Integer> retVal = new ArrayList<Integer>();
        
        for (Integer stat:statsRecord) {
            if (stat < time) retVal.add(stat);
        }
        
        return retVal;
        
    }
    
    /**
     * Calculate a player's rating in the match
     * @param team The player's team object
     * @param player The player object
     * @return The player's rating
     */
    public double getRating(Team team, Player player) {
        
        // System.out.println("Rating for player: " + player.getFamilyName());
        
        Double currentValue = null;
        
        double totalValue = 0;
        double statsSize = 0;
        
        Tactics.TacticLine yPos = team.getPosYByPlayer(player);
        Tactics.TacticPosition xPos = team.getPosXByPlayer(player);
        
        int posQualifier = 0;
        // int surrogates = 0;
        
        // Gk Long Pass
        currentValue = gkLongPass.getRating(RealWorldMapping.EXP_GkLongPass, 0);
        
        // System.out.println("Gk Long Pass: " + currentValue);
        
        if (currentValue != null) {
            totalValue += currentValue * (1 + RealWorldMapping.EVAL_GkLongPass);
            statsSize += (1 + RealWorldMapping.EVAL_GkLongPass);
        }
        
        if (yPos == Tactics.TacticLine.DEFENDER) {
            posQualifier = RealWorldMapping.THR_LongPass_DEF;
            // surrogates = team.getNumberByTL(Constants.DEFENDER);
        } else if (yPos == Tactics.TacticLine.MIDFIELDER) {
            posQualifier = RealWorldMapping.THR_LongPass_MID;
            // surrogates = team.getNumberByTL(Constants.MIDFIELDER);
        }
        
        // Long pass
        currentValue = longPass.getRating(RealWorldMapping.EXP_LongPass, posQualifier);
        
        // System.out.println("Long pass: " + currentValue);
        
        if (currentValue != null) {
            totalValue += currentValue * (1 + RealWorldMapping.EVAL_LongPass);
            statsSize += (1 + RealWorldMapping.EVAL_LongPass);
        }
        
        if (yPos == Tactics.TacticLine.DEFENDER) {
            posQualifier = RealWorldMapping.THR_ForwPass_DEF;
            // surrogates = team.getNumberByTL(Constants.DEFENDER);
        } else if (yPos == Tactics.TacticLine.MIDFIELDER) {
            posQualifier = RealWorldMapping.THR_ForwPass_MID;
            // surrogates = team.getNumberByTL(Constants.MIDFIELDER);
        }
        
        // Forward pass
        currentValue = forwardPass.getRating(RealWorldMapping.EXP_ForwardPass, posQualifier);
        
        // System.out.println("Forward pass: " + currentValue);
        
        if (currentValue != null) {
            totalValue += currentValue * (1 + RealWorldMapping.EVAL_ForwardPass);
            statsSize += (1 + RealWorldMapping.EVAL_ForwardPass);
        }
        
        if (yPos == Tactics.TacticLine.DEFENDER) {
            posQualifier = RealWorldMapping.THR_FlankPass_DEF;
            // surrogates = team.getNumberByTL(Constants.DEFENDER);
        } else if (yPos == Tactics.TacticLine.MIDFIELDER) {
            posQualifier = RealWorldMapping.THR_FlankPass_MID;
            // surrogates = team.getNumberByTL(Constants.MIDFIELDER);
        }
        
        // Flank pass
        currentValue = flankPass.getRating(RealWorldMapping.EXP_FlankPass, posQualifier);
        
        // System.out.println("Flank pass: " + currentValue);
        
        if (currentValue != null) {
            totalValue += currentValue * (1 + RealWorldMapping.EVAL_FlankPass);
            statsSize += (1 + RealWorldMapping.EVAL_FlankPass);
        }
        
        if (yPos == Tactics.TacticLine.MIDFIELDER) {
            posQualifier = RealWorldMapping.THR_BallControl_MID;
            // surrogates = team.getNumberByTL(Constants.MIDFIELDER);
        } else if (yPos == Tactics.TacticLine.FORWARD) {
            posQualifier = RealWorldMapping.THR_BallControl_FOR;
            // surrogates = team.getNumberByTL(Constants.FORWARD);
        }
        
        // Ball countrol
        currentValue = ballControl.getRating(RealWorldMapping.EXP_BallControl, posQualifier);
        
        // System.out.println("Ball control: " + currentValue);
        
        if (currentValue != null) {
            totalValue += currentValue * (1 + RealWorldMapping.EVAL_BallControl);
            statsSize += (1 + RealWorldMapping.EVAL_BallControl);
        }
        
        if (yPos == Tactics.TacticLine.MIDFIELDER) {
            posQualifier = RealWorldMapping.THR_Pass_MID;
            // surrogates = team.getNumberByTL(Constants.MIDFIELDER);
        }
        
        // Pass
        currentValue = pass.getRating(RealWorldMapping.EXP_Pass, posQualifier);
        
        // System.out.println("Pass: " + currentValue);
        
        if (currentValue != null) {
            totalValue += currentValue * (1 + RealWorldMapping.EVAL_Pass);
            statsSize += (1 + RealWorldMapping.EVAL_Pass);
        }
        
        if (yPos == Tactics.TacticLine.DEFENDER) {
            posQualifier = RealWorldMapping.THR_RunBall_DEF;
            // surrogates = team.getNumberByTL(Constants.DEFENDER);
        } else if (yPos == Tactics.TacticLine.MIDFIELDER) {
            posQualifier = RealWorldMapping.THR_RunBall_MID;
            // surrogates = team.getNumberByTL(Constants.MIDFIELDER);
        }
        
        // Run ball
        currentValue = runBall.getRating(RealWorldMapping.EXP_RunBall, posQualifier);
        
        // System.out.println("Run ball: " + currentValue);
        
        if (currentValue != null) {
            totalValue += currentValue * (1 + RealWorldMapping.EVAL_RunBall);
            statsSize += (1 + RealWorldMapping.EVAL_RunBall);
        }
        
        if (yPos == Tactics.TacticLine.MIDFIELDER) {
            
            if (xPos == Tactics.TacticPosition.LEFT_AXIS || xPos == Tactics.TacticPosition.RIGHT_AXIS) {
                posQualifier = RealWorldMapping.THR_LowCross_MID_CF;
            } else if (xPos == Tactics.TacticPosition.LEFT || xPos == Tactics.TacticPosition.RIGHT) {
                posQualifier = RealWorldMapping.THR_LowCross_MID_F;
            }
            
            // surrogates = team.getNumberByTL(Constants.MIDFIELDER);
            
        } else if (yPos == Tactics.TacticLine.FORWARD) {
            
            if (xPos == Tactics.TacticPosition.LEFT_AXIS || xPos == Tactics.TacticPosition.RIGHT_AXIS) {
                posQualifier = RealWorldMapping.THR_LowCross_FOR_CF;
            } else if (xPos == Tactics.TacticPosition.LEFT || xPos == Tactics.TacticPosition.RIGHT) {
                posQualifier = RealWorldMapping.THR_LowCross_FOR_F;
            }
            
            // surrogates = team.getNumberByTL(Constants.FORWARD);
            
        }
        
        // Low cross
        currentValue = lowCross.getRating(RealWorldMapping.EXP_LowCross, posQualifier);
        
        // System.out.println("Low cross: " + currentValue);
        
        if (currentValue != null) {
            totalValue += currentValue * (1 + RealWorldMapping.EVAL_LowCross);
            statsSize += (1 + RealWorldMapping.EVAL_LowCross);
        }
        
        if (yPos == Tactics.TacticLine.DEFENDER) {
            
            if (xPos == Tactics.TacticPosition.LEFT_AXIS || xPos == Tactics.TacticPosition.RIGHT_AXIS) {
                posQualifier = RealWorldMapping.THR_Cross_DEF_CF;
            } else if (xPos == Tactics.TacticPosition.LEFT || xPos == Tactics.TacticPosition.RIGHT) {
                posQualifier = RealWorldMapping.THR_Cross_DEF_F;
            }
            
            // surrogates = team.getNumberByTL(Constants.DEFENDER);
            
        } else if (yPos == Tactics.TacticLine.MIDFIELDER) {
            
            if (xPos == Tactics.TacticPosition.AXIS) {
                posQualifier = RealWorldMapping.THR_Cross_MID_C;
            } else if (xPos == Tactics.TacticPosition.LEFT_AXIS || xPos == Tactics.TacticPosition.RIGHT_AXIS) {
                posQualifier = RealWorldMapping.THR_Cross_MID_CF;
            } else if (xPos == Tactics.TacticPosition.LEFT || xPos == Tactics.TacticPosition.RIGHT) {
                posQualifier = RealWorldMapping.THR_Cross_MID_F;
            }
            
            // surrogates = team.getNumberByTL(Constants.MIDFIELDER);
            
        } else if (yPos == Tactics.TacticLine.FORWARD) {
            
            if (xPos == Tactics.TacticPosition.LEFT_AXIS || xPos == Tactics.TacticPosition.RIGHT_AXIS) {
                posQualifier = RealWorldMapping.THR_Cross_FOR_CF;
            } else if (xPos == Tactics.TacticPosition.LEFT || xPos == Tactics.TacticPosition.RIGHT) {
                posQualifier = RealWorldMapping.THR_Cross_FOR_F;
            }
            
            // surrogates = team.getNumberByTL(Constants.FORWARD);
            
        }
        
        // Cross
        currentValue = cross.getRating(RealWorldMapping.EXP_Cross, posQualifier);
        
        // System.out.println("Cross: " + currentValue);
        
        if (currentValue != null) {
            totalValue += currentValue * (1 + RealWorldMapping.EVAL_Cross);
            statsSize += (1 + RealWorldMapping.EVAL_Cross);
        }
        
        if (yPos == Tactics.TacticLine.MIDFIELDER) {
            posQualifier = RealWorldMapping.THR_SHOTS_MID;
            // surrogates = team.getNumberByTL(Constants.MIDFIELDER);
        } else if (yPos == Tactics.TacticLine.FORWARD) {
            posQualifier = RealWorldMapping.THR_SHOTS_FOR;
            // surrogates = team.getNumberByTL(Constants.FORWARD);
        }
        
        // Shots
        currentValue = shots.getRating(1 / (RealWorldMapping.avgFinishing + RealWorldMapping.avgShooting + 1), posQualifier);
        
        // System.out.println("Shots: " + currentValue);
        
        if (currentValue != null) {
            totalValue += currentValue * (1 + RealWorldMapping.EVAL_Shots);
            statsSize += (1 + RealWorldMapping.EVAL_Shots);
        }
        
        if (yPos == Tactics.TacticLine.FORWARD) {
            
            if (xPos == Tactics.TacticPosition.LEFT_AXIS || xPos == Tactics.TacticPosition.RIGHT_AXIS) {
                posQualifier = RealWorldMapping.THR_HEADINGS_FOR_CF;
            } else if (xPos == Tactics.TacticPosition.RIGHT || xPos == Tactics.TacticPosition.LEFT) {
                posQualifier = RealWorldMapping.THR_HEADINGS_FOR_C;
            }
            
            // surrogates = team.getNumberByTL(Constants.FORWARD);
            
        }
        
        // Headings
        currentValue = headingsOnTarget.getRating(1 / (RealWorldMapping.avgFinishing + RealWorldMapping.avgShooting + 1), posQualifier);
        
        // System.out.println("Headings: " + currentValue);
        
        if (currentValue != null) {
            totalValue += currentValue * (1 + RealWorldMapping.EVAL_Shots);
            statsSize += (1 + RealWorldMapping.EVAL_Shots);
        }
        
        if (yPos == Tactics.TacticLine.DEFENDER) {
            posQualifier = RealWorldMapping.THR_Dribbling_DEF;
            // surrogates = team.getNumberByTL(Constants.DEFENDER);
        } else if (yPos == Tactics.TacticLine.MIDFIELDER) {
            posQualifier = RealWorldMapping.THR_Dribbling_MID;
            // surrogates = team.getNumberByTL(Constants.MIDFIELDER);
        } else if (yPos == Tactics.TacticLine.FORWARD) {
            posQualifier = RealWorldMapping.THR_Dribbling_FOR;
            // surrogates = team.getNumberByTL(Constants.FORWARD);
        }
        
        // Dribbling
        currentValue = dribbling.getRating(RealWorldMapping.EXP_Dribbling, posQualifier);
        
        // System.out.println("Dribbling: " + currentValue);
        
        if (currentValue != null) {
            totalValue += currentValue;
            ++statsSize;
        }
        
        if (yPos == Tactics.TacticLine.MIDFIELDER) {
            posQualifier = RealWorldMapping.THR_LongFlankPass_MID;
            // surrogates = team.getNumberByTL(Constants.MIDFIELDER);
        }
        
        // Long flank pass
        currentValue = longFlankPass.getRating(RealWorldMapping.EXP_LongFlankPass, posQualifier);
        
        // System.out.println("Long flank pass: " + currentValue);
        
        if (currentValue != null) {
            totalValue += currentValue;
            ++statsSize;
        }
        
        if (yPos == Tactics.TacticLine.MIDFIELDER) {
            posQualifier = RealWorldMapping.THR_AreaPass_MID;
            // surrogates = team.getNumberByTL(Constants.MIDFIELDER);
        } else if (yPos == Tactics.TacticLine.FORWARD) {
            posQualifier = RealWorldMapping.THR_AreaPass_FOR;
            // surrogates = team.getNumberByTL(Constants.FORWARD);
        }
        
        // Area pass
        currentValue = areaPass.getRating(RealWorldMapping.EXP_AreaPass, posQualifier);
        
        // System.out.println("Area pass: " + currentValue);
        
        if (currentValue != null) {
            totalValue += currentValue;
            ++statsSize;
        }
        
        if (yPos == Tactics.TacticLine.DEFENDER) {
            posQualifier = RealWorldMapping.THR_IndCh_DEF;
            // surrogates = team.getNumberByTL(Constants.DEFENDER);
        } else if (yPos == Tactics.TacticLine.MIDFIELDER) {
            posQualifier = RealWorldMapping.THR_IndCh_MID;
            // surrogates = team.getNumberByTL(Constants.MIDFIELDER);
        }
        
        // Individual challenges
        currentValue = personalChallenges.getRating(posQualifier);
        
        // System.out.println("Personal challenges: " + currentValue);
        
        if (currentValue != null) {
            totalValue += currentValue;
            ++statsSize;
        }
        
        // Interceptions
        currentValue = interceptions.getRating(0);
        
        // System.out.println("Interceptions: " + currentValue);
        
        if (currentValue != null) {
            totalValue += currentValue;
            ++statsSize;
        }
        
        // Saves
        currentValue = saves.getRating(0);
        
        // System.out.println("Saves: " + currentValue);
        
        if (currentValue != null) {
            totalValue += currentValue * RealWorldMapping.EVAL_SAVES;
            statsSize += RealWorldMapping.EVAL_SAVES;
        }
        
        // Concedings
        currentValue = concedings.getNegativeRating();
        
        // System.out.println("Concedings: " + currentValue);
        
        if (currentValue != null) {
            totalValue += currentValue * RealWorldMapping.EVAl_CONCEDINGS;
            statsSize += RealWorldMapping.EVAl_CONCEDINGS;
        }
        
        // Penalties missed
        currentValue = new UnitStats(this.penaltiesMissed).getNegativeRating();
        
        // System.out.println("Missed penalties: " + currentValue);
        
        if (currentValue != null) {
            totalValue += currentValue * RealWorldMapping.EVAL_PENALTIES_MISSED;
            statsSize += RealWorldMapping.EVAL_PENALTIES_MISSED;
        }
        
        // Penalties saved
        currentValue = new UnitStats(this.penaltiesSaved).getRating(0);
        
        // System.out.println("Saved penalties: " + currentValue);
        
        if (currentValue != null) {
            totalValue += currentValue * RealWorldMapping.EVAL_PENALTIES_SAVED;
            statsSize += RealWorldMapping.EVAL_PENALTIES_SAVED;
        }
        
        // Goals scored from penalties
        currentValue = new UnitStats(this.goalsScored - this.getAllShotsSucc()).getRating(0);
        
        // System.out.println("Goals scored from penalties: " + currentValue);
        
        if (currentValue != null) {
            totalValue += currentValue * RealWorldMapping.EVAL_PENALTY_GOALS;
            statsSize += RealWorldMapping.EVAL_PENALTY_GOALS;
        }
        
        return totalValue / statsSize;
        
    }
    
    /**
     * Calculate a player's rating in the match in a specific time point
     * @param team The player's team
     * @param player The player object
     * @param time The virtual time point up to which the player's rating will be calculated
     * @return The player's rating
     */
    public double getRating(Team team, Player player, int time) {
        
        // System.out.println("Calculating rating for player: " + player.getFamilyName());
        
        Double currentValue = null;
        
        double totalValue = 0;
        double statsSize = 0;
        
        Tactics.TacticLine yPos = team.getPosYByPlayer(player);
        Tactics.TacticPosition xPos = team.getPosXByPlayer(player);
        
        int posQualifier = 0;
        
        boolean unrated = true;
        
        // Gk long pass
        
        currentValue = gkLongPass.getCurrentRating(RealWorldMapping.EXP_GkLongPass, 0, time);
        
        // System.out.println("Rating after goalkeeper long pass: " + currentValue);
        
        if (currentValue != null) {
            totalValue += currentValue * (1 + RealWorldMapping.EVAL_GkLongPass);
            statsSize += (1 + RealWorldMapping.EVAL_GkLongPass);
            unrated = false;
        }
        
        // Long pass
        
        if (yPos == Tactics.TacticLine.DEFENDER) {
            posQualifier = RealWorldMapping.THR_LongPass_DEF;
        } else if (yPos == Tactics.TacticLine.MIDFIELDER) {
            posQualifier = RealWorldMapping.THR_LongPass_MID;
        }
        
        currentValue = longPass.getCurrentRating(RealWorldMapping.EXP_LongPass, adjustThresholdToTime(posQualifier, time), time);
        
        // System.out.println("Rating after long pass: " + currentValue);
        
        if (currentValue != null) {
            totalValue += currentValue * (1 + RealWorldMapping.EVAL_LongPass);
            statsSize += (1 + RealWorldMapping.EVAL_LongPass);
            unrated = false;
        }
        
        // Forward pass
        
        if (yPos == Tactics.TacticLine.DEFENDER) {
            posQualifier = RealWorldMapping.THR_ForwPass_DEF;
        } else if (yPos == Tactics.TacticLine.MIDFIELDER) {
            posQualifier = RealWorldMapping.THR_ForwPass_MID;
        }
        
        currentValue = forwardPass.getCurrentRating(RealWorldMapping.EXP_ForwardPass, adjustThresholdToTime(posQualifier, time), time);
        
        // System.out.println("Rating after forward pass: " + currentValue);
        
        if (currentValue != null) {
            totalValue += currentValue * (1 + RealWorldMapping.EVAL_ForwardPass);
            statsSize += (1 + RealWorldMapping.EVAL_ForwardPass);
            unrated = false;
        }
        
        // Flank pass
        
        if (yPos == Tactics.TacticLine.DEFENDER) {
            posQualifier = RealWorldMapping.THR_FlankPass_DEF;
        } else if (yPos == Tactics.TacticLine.MIDFIELDER) {
            posQualifier = RealWorldMapping.THR_FlankPass_MID;
        }
        
        currentValue = flankPass .getCurrentRating(RealWorldMapping.EXP_FlankPass, adjustThresholdToTime(posQualifier, time), time);
        
        // System.out.println("Rating after flank pass: " + currentValue);
        
        if (currentValue != null) {
            totalValue += currentValue * (1 + RealWorldMapping.EVAL_FlankPass);
            statsSize += (1 + RealWorldMapping.EVAL_FlankPass);
            unrated = false;
        }
        
        // Ball control
        
        if (yPos == Tactics.TacticLine.MIDFIELDER) {
            posQualifier = RealWorldMapping.THR_BallControl_MID;
        } else if (yPos == Tactics.TacticLine.FORWARD) {
            posQualifier = RealWorldMapping.THR_BallControl_FOR;
        }
        
        currentValue = ballControl.getCurrentRating(RealWorldMapping.EXP_BallControl, adjustThresholdToTime(posQualifier, time), time);
        
        // System.out.println("Rating after ball control: " + currentValue);
        
        if (currentValue != null) {
            totalValue += currentValue * (1 + RealWorldMapping.EVAL_BallControl);
            statsSize += (1 + RealWorldMapping.EVAL_BallControl);
            unrated = false;
        }
        
        // Pass
        
        if (yPos == Tactics.TacticLine.MIDFIELDER) {
            posQualifier = RealWorldMapping.THR_Pass_MID;
        }
        
        currentValue = pass.getCurrentRating(RealWorldMapping.EXP_Pass, adjustThresholdToTime(posQualifier, time), time);
        
        // System.out.println("Rating after pass: " + currentValue);
        
        if (currentValue != null) {
            totalValue += currentValue * (1 + RealWorldMapping.EVAL_Pass);
            statsSize += (1 + RealWorldMapping.EVAL_Pass);
            unrated = false;
        }
        
        // Run ball
        
        if (yPos == Tactics.TacticLine.DEFENDER) {
            posQualifier = RealWorldMapping.THR_RunBall_DEF;
        } else if (yPos == Tactics.TacticLine.MIDFIELDER) {
            posQualifier = RealWorldMapping.THR_RunBall_MID;
        }
        
        currentValue = runBall.getCurrentRating(RealWorldMapping.EXP_RunBall, adjustThresholdToTime(posQualifier, time), time);
        
        // System.out.println("Rating after run ball: " + currentValue);
        
        if (currentValue != null) {
            totalValue += currentValue * (1 + RealWorldMapping.EVAL_RunBall);
            statsSize += (1 + RealWorldMapping.EVAL_RunBall);
            unrated = false;
        }
        
        // Low cross
        
        if (yPos == Tactics.TacticLine.MIDFIELDER) {
            
            if (xPos == Tactics.TacticPosition.LEFT_AXIS || xPos == Tactics.TacticPosition.RIGHT_AXIS) {
                posQualifier = RealWorldMapping.THR_LowCross_MID_CF;
            } else if (xPos == Tactics.TacticPosition.LEFT || xPos == Tactics.TacticPosition.RIGHT) {
                posQualifier = RealWorldMapping.THR_LowCross_MID_F;
            }
            
        } else if (yPos == Tactics.TacticLine.FORWARD) {
            
            if (xPos == Tactics.TacticPosition.LEFT_AXIS || xPos == Tactics.TacticPosition.RIGHT_AXIS) {
                posQualifier = RealWorldMapping.THR_LowCross_FOR_CF;
            } else if (xPos == Tactics.TacticPosition.LEFT || xPos == Tactics.TacticPosition.RIGHT) {
                posQualifier = RealWorldMapping.THR_LowCross_FOR_F;
            }
            
        }
        
        currentValue = lowCross.getCurrentRating(RealWorldMapping.EXP_LowCross, adjustThresholdToTime(posQualifier, time), time);
        
        // System.out.println("Rating after low cross: " + currentValue);
        
        if (currentValue != null) {
            totalValue += currentValue * (1 + RealWorldMapping.EVAL_LowCross);
            statsSize += (1 + RealWorldMapping.EVAL_LowCross);
            unrated = false;
        }
        
        // Cross
        
        if (yPos == Tactics.TacticLine.DEFENDER) {
            
            if (xPos == Tactics.TacticPosition.LEFT_AXIS || xPos == Tactics.TacticPosition.RIGHT_AXIS) {
                posQualifier = RealWorldMapping.THR_Cross_DEF_CF;
            } else if (xPos == Tactics.TacticPosition.LEFT || xPos == Tactics.TacticPosition.RIGHT) {
                posQualifier = RealWorldMapping.THR_Cross_DEF_F;
            }
            
        } else if (yPos == Tactics.TacticLine.MIDFIELDER) {
            
            if (xPos == Tactics.TacticPosition.AXIS) {
                posQualifier = RealWorldMapping.THR_Cross_MID_C;
            } else if (xPos == Tactics.TacticPosition.LEFT_AXIS || xPos == Tactics.TacticPosition.RIGHT_AXIS) {
                posQualifier = RealWorldMapping.THR_Cross_MID_CF;
            } else if (xPos == Tactics.TacticPosition.LEFT || xPos == Tactics.TacticPosition.RIGHT) {
                posQualifier = RealWorldMapping.THR_Cross_MID_F;
            }
            
        } else if (yPos == Tactics.TacticLine.FORWARD) {
            
            if (xPos == Tactics.TacticPosition.LEFT_AXIS || xPos == Tactics.TacticPosition.RIGHT_AXIS) {
                posQualifier = RealWorldMapping.THR_Cross_FOR_CF;
            } else if (xPos == Tactics.TacticPosition.LEFT || xPos == Tactics.TacticPosition.RIGHT) {
                posQualifier = RealWorldMapping.THR_Cross_FOR_F;
            }
            
        }
        
        currentValue = cross.getCurrentRating(RealWorldMapping.EXP_Cross, adjustThresholdToTime(posQualifier, time), time);
        
        // System.out.println("Rating after cross: " + currentValue);
        
        if (currentValue != null) {
            totalValue += currentValue * (1 + RealWorldMapping.EVAL_Cross);
            statsSize += (1 + RealWorldMapping.EVAL_Cross);
            unrated = false;
        }
        
        // Shots
        
        if (yPos == Tactics.TacticLine.MIDFIELDER) {
            posQualifier = RealWorldMapping.THR_SHOTS_MID;
        } else if (yPos == Tactics.TacticLine.FORWARD) {
            posQualifier = RealWorldMapping.THR_SHOTS_FOR;
        }
        
        currentValue = shots.getCurrentRating(1 / (RealWorldMapping.avgFinishing + RealWorldMapping.avgShooting + 1), 
                adjustThresholdToTime(posQualifier, time), time);
        
        // System.out.println("Rating after shots: " + currentValue);
        
        if (currentValue != null) {
            totalValue += currentValue * (1 + RealWorldMapping.EVAL_Shots);
            statsSize += (1 + RealWorldMapping.EVAL_Shots);
            unrated = false;
        }
        
        // Headings
        
        if (xPos == Tactics.TacticPosition.LEFT_AXIS || xPos == Tactics.TacticPosition.RIGHT_AXIS) {
            posQualifier = RealWorldMapping.THR_HEADINGS_FOR_CF;
        } else if (xPos == Tactics.TacticPosition.LEFT || xPos == Tactics.TacticPosition.RIGHT) {
            posQualifier = RealWorldMapping.THR_HEADINGS_FOR_C;
        }
        
        currentValue = headingsOnTarget.getRating(1 / (RealWorldMapping.avgFinishing + RealWorldMapping.avgShooting + 1), 
                adjustThresholdToTime(posQualifier, time), time);
        
        // System.out.println("Rating after headings: " + currentValue);
        
        if (currentValue != null) {
            totalValue += currentValue * (1 + RealWorldMapping.EVAL_Shots);
            statsSize += (1 + RealWorldMapping.EVAL_Shots);
            unrated = false;
        }
        
        // Dribbling
        
        if (yPos == Tactics.TacticLine.DEFENDER) {
            posQualifier = RealWorldMapping.THR_Dribbling_DEF;
        } else if (yPos == Tactics.TacticLine.MIDFIELDER) {
            posQualifier = RealWorldMapping.THR_Dribbling_MID;
        } else if (yPos == Tactics.TacticLine.FORWARD) {
            posQualifier = RealWorldMapping.THR_Dribbling_FOR;
        }
        
        currentValue = dribbling.getCurrentRating(RealWorldMapping.EXP_Dribbling, adjustThresholdToTime(posQualifier, time), time);
        
        // System.out.println("Rating after dribbling: " + currentValue);
        
        if (currentValue != null) { // Dribbling evaluation factor is one
            totalValue += currentValue;
            ++statsSize;
            unrated = false;
        }
        
        // Long flank pass
        
        if (yPos == Tactics.TacticLine.MIDFIELDER) {
            posQualifier = RealWorldMapping.THR_LongFlankPass_MID;
        }
        
        currentValue = longFlankPass.getCurrentRating(RealWorldMapping.EXP_LongFlankPass, adjustThresholdToTime(posQualifier, time), time);
        
        // System.out.println("Rating after long flank pass: " + currentValue);
        
        if (currentValue != null) {
            totalValue += currentValue;
            ++statsSize;
            unrated = false;
        }
        
        // Area pass
        
        if (yPos == Tactics.TacticLine.MIDFIELDER) {
            posQualifier = RealWorldMapping.THR_AreaPass_MID;
        } else if (yPos == Tactics.TacticLine.FORWARD) {
            posQualifier = RealWorldMapping.THR_AreaPass_FOR;
        }
        
        currentValue = areaPass.getCurrentRating(RealWorldMapping.EXP_AreaPass, adjustThresholdToTime(posQualifier, time), time);
        
        // System.out.println("Rating after area pass: " + currentValue);
        
        if (currentValue != null) {
            totalValue += currentValue;
            ++statsSize;
            unrated = false;
        }
        
        // Individual challenges
        
        if (yPos == Tactics.TacticLine.DEFENDER) {
            posQualifier = RealWorldMapping.THR_IndCh_DEF;
        } else if (yPos == Tactics.TacticLine.MIDFIELDER) {
            posQualifier = RealWorldMapping.THR_IndCh_MID;
        }
        
        currentValue = personalChallenges.getCurrentRating(adjustThresholdToTime(posQualifier, time), time);
        
        // System.out.println("Rating after personal challenges: " + currentValue);
        
        if (currentValue != null) {
            totalValue += currentValue;
            ++statsSize;
            unrated = false;
        }
        
        // Interceptions
        
        currentValue = interceptions.getCurrentRating(0, time);
        
        // System.out.println("Rating after interceptions: " + currentValue);
        
        if (currentValue != null) {
            totalValue += currentValue;
            ++statsSize;
            unrated = false;
        }
        
        // Saves
        
        currentValue = saves.getCurrentRating(0, time);
        
        // System.out.println("Rating after saves: " + currentValue);
        
        if (currentValue != null) {
            totalValue += currentValue * RealWorldMapping.EVAL_SAVES;
            statsSize += RealWorldMapping.EVAL_SAVES;
            unrated = false;
        }
        
        // Concedings
        
        currentValue = concedings.getNegativeRating(time);
        
        // System.out.println("Rating after concedings: " + currentValue);
        
        if (currentValue != null) {
            totalValue += currentValue * RealWorldMapping.EVAl_CONCEDINGS;
            statsSize += RealWorldMapping.EVAl_CONCEDINGS;
            unrated = false;
        }
        
        // Penalties missed
        
        int currentPenaltiesMissed = adjustToTime(this.penaltiesMissedRecord, time).size();
        
        currentValue = new UnitStats(currentPenaltiesMissed).getNegativeRating();
        
        // System.out.println("Rating after penalties missed: " + currentValue);
        
        if (currentValue != null) {
            totalValue += currentValue * RealWorldMapping.EVAL_PENALTIES_MISSED;
            statsSize += RealWorldMapping.EVAL_PENALTIES_MISSED;
            unrated = false;
        }
        
        // Penalties saved
        
        int currentPenaltiesSaved = adjustToTime(this.penaltiesSavedRecord, time).size();
        
        currentValue = new UnitStats(currentPenaltiesSaved).getNegativeRating();
        
        // System.out.println("Rating after penalties saved: " + currentValue);
        
        if (currentValue != null) {
            totalValue += currentValue * RealWorldMapping.EVAL_PENALTIES_SAVED;
            statsSize += RealWorldMapping.EVAL_PENALTIES_SAVED;
            unrated = false;
        }
        
        // Goals scored from penalties
        
        int currentGoalsScored = adjustToTime(this.goalsRecord, time).size();
        int currentSuccShots = this.getAllShotsSucc(time);
        
        currentValue = new UnitStats(currentGoalsScored - currentSuccShots).getRating(0);
        
        // System.out.println("Rating after goals scored from penalties: " + currentValue);
        
        if (currentValue != null) {
            totalValue += currentValue * RealWorldMapping.EVAL_PENALTY_GOALS;
            statsSize += RealWorldMapping.EVAL_PENALTY_GOALS;
            unrated = false;
        }
        
        // System.out.println("Total value: " + totalValue);
        
        if (unrated) return -1;
        
        return totalValue / statsSize;
        
    }
    
    /**
     * Getter
     * @return The number of successful passes
     */
    public int getAllPassesSucc() {
        return gkLongPass.getSuccessfulAttempts() + 
                longPass.getSuccessfulAttempts() + 
                forwardPass.getSuccessfulAttempts() +
                flankPass.getSuccessfulAttempts() +
                longFlankPass.getSuccessfulAttempts() +
                pass.getSuccessfulAttempts() + 
                areaPass.getSuccessfulAttempts();
    }
    
    /**
     * Getter
     * @return The number of total passes
     */
    public int getAllPassesTotal() {
        return gkLongPass.getTotalAttempts() +
                longPass.getTotalAttempts() +
                forwardPass.getTotalAttempts() +
                flankPass.getTotalAttempts() +
                longFlankPass.getTotalAttempts() +
                pass.getTotalAttempts() +
                areaPass.getTotalAttempts();
    }
    
    /**
     * Getter
     * @return The number of successful dribbles and instances keeping the ball control under pressure 
     */
    public int getAttPersonalChallengesSucc() {
        return ballControl.getSuccessfulAttempts() + dribbling.getSuccessfulAttempts();
    }
    
    /**
     * Gettet
     * @return The number of attempted dribbles and attempts to keep the ball control under pressure
     */
    public int getAttPersonalChallengesTotal() {
        return ballControl.getTotalAttempts() + dribbling.getTotalAttempts();
    }
    
    /**
     * Getter
     * @return The number of successful crosses
     */
    public int getCrossesSucc() {
        return cross.getSuccessfulAttempts() + lowCross.getSuccessfulAttempts();
    }
    
    /**
     * Getter
     * @return The number of attempted crosses
     */
    public int getCrossesTotal() {
        return cross.getTotalAttempts() + lowCross.getTotalAttempts();
    }
    
    /**
     * Gettter
     * @return The number of successful runs with the ball
     */
    public int getRunsSucc() {
        return runBall.getSuccessfulAttempts();
    }
    
    /**
     * Getter
     * @return The number of total runs with the ball
     */
    public int getRunsTotal() {
        return runBall.getTotalAttempts();
    }
    
    /**
     * Getter
     * @return The number of defending challenges won
     */
    public int getDefChallengesWon() {
        return personalChallenges.getOccurences(); 
    }
    
    /**
     * Getter
     * @return The number of interceptions
     */
    public int getInterceptions() {
        return interceptions.getOccurences();
    }
    
    /**
     * Getter
     * @return The number of goalkeeper saves
     */
    public int getSaves() {
        return saves.getOccurences();
    }
    
    /**
     * Getter
     * @return The number of successful attempts at goal
     */
    public int getAllShotsSucc() {
        return shots.getSuccessfulAttempts() + headingsOnTarget.getSuccessfulAttempts();
    }
    
    /**
     * The number of successful attempts at goal up to a specific time
     * @param time The virtual time point of reference
     * @return The number of successful attempts at goal
     */
    public int getAllShotsSucc(int time) {
        return shots.getSuccessfulAttempts(time) + headingsOnTarget.getSuccessfulAttempts(time);
    }
    
    /**
     * Getter
     * @return The number of total attempts at goal
     */
    public int getAllShotsTotal() {
        return shots.getTotalAttempts() + headingsOnTarget.getTotalAttempts();
    }
    
    /**
     * Getter
     * @return The number of goals scored
     */
    public int getGoals() {
        return goalsScored;
    }
    
    /**
     * Add a goal to player's stats and record
     * @param time The virtual time of the goal being scored
     */
    public void addGoal(int time) {
        ++goalsScored;
        goalsRecord.add(time);
    }
    
    /**
     * Getter
     * @return The number of missed penalties
     */
    public int getPenaltiesMissed() {
        return this.penaltiesMissed;
    }
    
    /**
     * Add a missed penalty to the player's stats and record
     * @param timer The virtual time of the missed penalty
     */
    public void addPenaltiesMissed(int timer) {
        this.penaltiesMissed++;
        penaltiesMissedRecord.add(timer);
    }
    
    /**
     * Getter
     * @return The number of saved penalties
     */
    public int getPenaltiesSaved() {
        return this.penaltiesSaved;
    }
    
    /**
     * Add a saved penalty to player's stats and record
     * @param timer The virtual time of the penalty save
     */
    public void addPenaltiesSaved(int timer) {
        this.penaltiesSaved++;
        penaltiesSavedRecord.add(timer);
    }
    
    /**
     * 
     * This class is used for the player stats that have an success/total attempts element
     *
     */
    
    public class PercStats {
        
        private int successfulAttempts = 0;
        private int totalAttempts = 0;
        
        private ArrayList<Integer> successRecord = new ArrayList<Integer>();
        private ArrayList<Integer> totalRecord = new ArrayList<Integer>();
        
        /**
         * Add a successful occurence to the player's stats and record
         * @param time The virtual time of the occurence
         */
        public void registerSuccess(int time) {
            
            ++successfulAttempts;
            ++totalAttempts;
            
            successRecord.add(time);
            totalRecord.add(time);
            
        }
        
        /**
         * Add an unsuccessful attempt to the player's stats and record
         * @param time The virtual time of the attempt
         */
        public void registerFailure(int time) {
            
            ++totalAttempts;
            totalRecord.add(time);
        }
        
        public int getTotalAttempts() {
            return totalAttempts;
        }
        
        public int getSuccessfulAttempts() {
            return successfulAttempts;
        }
        
        public int getSuccessfulAttempts(int time) {
            
            return adjustToTime(this.successRecord, time).size();
            
        }
        
        public double getSuccPerc() {
            return (double) successfulAttempts / (double) totalAttempts;
        }
        
        /**
         * Get the success percentage in the stats category in reference to a specific point in virtual time
         * @param time The virtual time point of sampling the success percentage
         * @return The success percentage
         */
        public double getSuccPerc(int time) {
            
            int currentSuccessfulAttempts = adjustToTime(successRecord, time).size();
            int currentTotalAttempts = adjustToTime(totalRecord, time).size();
            
            return (double) currentSuccessfulAttempts / (double) currentTotalAttempts;
            
        }
        
        /**
         * Default function for getting the current stat's contribution to the player's rating
         * @param expectedRate The expected rate for the current statistic
         * @param posQualifier The expected number of attempts for the current stat according to the player's position 
         * @return The player's rating for the current stat category
         * @see #getRating(double, int, int)
         */
        public Double getRating(double expectedRate, int posQualifier) {
            return getRating(expectedRate, posQualifier, 1);
        }
        
        /**
         * Function for getting the current stat's contribution to the player's rating
         * @param expectedRate The expected rate for the current statistic
         * @param posQualifier The expected number of attempts for the current stat according to the player's position 
         * @param surrogates The number of players playing in the same position (line) and effectively the number of players that could have helped 
         * with the statistic. Can be used to better approximate the effect of lack of expected attempts to the team's performance. For the time being
         * it is not really taken into account as it makes things more complicated (for instance, when tactics change throughout the match) 
         * @return The player's rating for the current stat category
         */
        public Double getRating(double expectedRate, int posQualifier, int surrogates) {
            
            int initTotalAttempts = this.totalAttempts;
            
            if (posQualifier > totalAttempts && surrogates > 0) {
                totalAttempts += Math.round((posQualifier - totalAttempts) / (double) surrogates);
                // System.out.println("Total attempts corrected: " + totalAttempts);
            }
            
            if (totalAttempts == 0) return null;
            
            double confidence = 
                MathUtil.getWilsonScoreConfidence(getSuccPerc(), MathUtil.NORMAL_CONFIDENCE, totalAttempts);
            
            double result = 0;
            
            if (confidence >= expectedRate) {
            
                result = RealWorldMapping.expectedRating + 
                    ((confidence - expectedRate) / ((1 - expectedRate))) * (10.0 - RealWorldMapping.expectedRating);
                
            } else {
                
               result = (RealWorldMapping.expectedRating * confidence) / expectedRate; 
                
            }
            
            if (result > 10.0) result = 10.0;
            
            this.totalAttempts = initTotalAttempts; // After finishing with calculations bring back total attempts to their real value
            
            return result;
            
        }
        
        /**
         * Function for getting the current stat's contribution to the player's rating
         * @param expectedRate The expected rate for the current statistic
         * @param posQualifier The expected number of attempts for the current stat according to the player's position 
         * @param time The virtual time point from which the statistics are sampled
         * @return The player's rating for the current stat category
         */
        public Double getCurrentRating(double expectedRate, int posQualifier, int time) {
            
            double result = 0;
            
            int currentTotalAttempts = adjustToTime(totalRecord, time).size();
            
            if (currentTotalAttempts == 0) return null; // Before adjusting with position qualifier, because the 'pure' number will be used in the calculations
            
            if (posQualifier > currentTotalAttempts) {
                currentTotalAttempts += Math.round(posQualifier - currentTotalAttempts);
            }
            
            if (currentTotalAttempts == 0) return null;
            
            // System.out.println("Current total attempts: " + currentTotalAttempts);
            // System.out.println("Current success percentage: " + getSuccPerc(time));
            
            double confidence = 
                MathUtil.getWilsonScoreConfidence(getSuccPerc(time), MathUtil.NORMAL_CONFIDENCE, currentTotalAttempts);
            
            // System.out.println("Confidence: " + confidence);
            // System.out.println("Expected rate: " + expectedRate);
            
            if (confidence >= expectedRate) {
                
                result = RealWorldMapping.expectedRating + 
                    ((confidence - expectedRate) / ((1 - expectedRate))) * (10.0 - RealWorldMapping.expectedRating);
                
            } else {
            
                result = (RealWorldMapping.expectedRating * confidence) / expectedRate; 
                
            }
            
            if (result > 10.0) result = 10.0;
            
            return result;
            
        }
        
    }
    
    /**
     * 
     * This class is used for the player stats that are "scalar", i.e. the player is evaluated based on just their cardinality 
     *
     */
    
    public class UnitStats {
        
        private int occurences = 0;
        
        private ArrayList<Integer> occurenceRecord = new ArrayList<Integer>();
        
        /**
         * Add an occurence to the player's stats and record
         * @param time The occurence's virtual time
         */
        public void addOccurence(int time) {
            ++occurences;
            occurenceRecord.add(time);
        }
        
        public int getOccurences() {
            return occurences;
        }
        
        public UnitStats(int occurences) {
            this.occurences = occurences;
        }
        
        public UnitStats() {}
        
        /**
         * Function for getting the current stat's contribution to the player's rating
         * @param posQualifier The number of expected occurences according to the player's position
         * @return The player's rating for the current stat category 
         */
        public Double getRating(int posQualifier) {
            return getRating(posQualifier, 1);
        }
        
        /**
         * Function for getting the current stat's contribution to the player's rating taking into account the team's tactic
         * @param posQualifier The number of expected occurences according to the player's position
         * @param surrogates The number of players playing in the same position (line) and effectively the number of players that could have helped 
         * with the statistic. 
         * @return The player's rating for the current stat category
         * @see #getRating(int)
         * @see PercStats#getRating(double, int, int)
         */
        public Double getRating(int posQualifier, int surrogates) {
            
            double successPerc = 1d;
            int totalAttempts = occurences;
            
            if (occurences == 0 && posQualifier == 0) return null;
            
            if (occurences < posQualifier && surrogates > 0) {
                totalAttempts = occurences + Math.round((posQualifier - occurences) / (float) surrogates);
                successPerc = occurences / (double) totalAttempts;
                
                // System.out.println("Total attempts corrected: " + totalAttempts); 
            }
            
            if (totalAttempts == 0) return null;
            
            double confidence = MathUtil.getWilsonScoreConfidence(successPerc, MathUtil.NORMAL_CONFIDENCE, totalAttempts);
            
            return confidence * 10;
            
        }
        
        public Double getCurrentRating(int posQualifier, int time) {
            
            double successPerc = 1d;
            int currentOccurences  = adjustToTime(this.occurenceRecord, time).size();
            int totalAttempts = currentOccurences;
            
            if (currentOccurences == 0 && posQualifier == 0) return null;
            
            if (currentOccurences < posQualifier) {
                totalAttempts = posQualifier;
                successPerc = currentOccurences / (double) totalAttempts;
            }
            
            if (totalAttempts == 0) return null;
            
            double confidence = MathUtil.getWilsonScoreConfidence(successPerc, MathUtil.NORMAL_CONFIDENCE, totalAttempts);
            
            return confidence * 10;
            
        }
        
        /**
         * 
         * @return The negative rating of this statistic contributing to the total player's rating. This is used for some statistics categories
         * that have negative effect on a player's total rating, like penalties missed
         */
        public Double getNegativeRating() {
            
            if (occurences == 0) return null;
            
            double confidence = MathUtil.getWilsonScoreConfidence(0, MathUtil.NORMAL_CONFIDENCE, occurences);
            
            return confidence * 10;
            
        }
        
        /**
         * Calculates the negative rating of this statistic contributing to the total player's rating in a specific virtual time
         * @param time The virtual time point up to which the negative rating will be calculated
         * @return The negative rating
         * @see getNegativeRating()
         */
        public Double getNegativeRating(int time) {
            
            int currentOccurences = adjustToTime(this.occurenceRecord, time).size();
            
            if (currentOccurences == 0) return null;
            
            double confidence = MathUtil.getWilsonScoreConfidence(0, MathUtil.NORMAL_CONFIDENCE, currentOccurences);
            
            return confidence * 10;
            
        }
    }
}
