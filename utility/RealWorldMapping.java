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

package utility;

/**
 * This class the comrpises all the constants related to mapping the model of the football match engine with the 'real world'. For instance,
 * we give the probability of a penalty kick becoming a goal to be 80%. These statistics will be adjusted as more samples will be taken from more
 * football matches and of course, I don't claim to be scientific or accurate, but rather 'realistic enough'. Somehow, the values of these constants 
 * are responsible for the 'realism' of the football match numbers resulting from the connection of the probability model events
 * 
 * @author Andreas Tasoulas
 *
 */

public class RealWorldMapping {
    
    public final static double expectedRating = 6.5;
    
    // Shooting outcome
    public final static int GOAL = 0;
    public final static int SHOT_ON = 1;
    public final static int SHOT_OFF = 2;
    
    public final static double [] AreaShotStats = {44.55, 23.86, 7.4};
    public final static double [] CrossStats = {30.69, 18.35, 12.99};
    public final static double [] LongShotStats = {13.8, 35.78, 43.52};
    
    //
    
    public final static double defaultTLCardinality = 3.3;
    
    public final static double avgFinishing = 4.15d;
    public final static double avgShooting = 3.81d;
    
    // Scoring weights
    private final static double DEF_SCORING_WEIGHT = 1d;
    private final static double MID_SCORING_WEIGHT = 10d;
    private final static double FOR_SCORING_WEIGHT = 18d;
    
    public static double getScoringWeight(Tactics.TacticLine currentPosition) {
        if (currentPosition.equals(Tactics.TacticLine.GK))
            return 0d;
        else if (currentPosition.equals(Tactics.TacticLine.DEFENDER))
            return DEF_SCORING_WEIGHT;
        else if (currentPosition.equals(Tactics.TacticLine.MIDFIELDER))
            return MID_SCORING_WEIGHT;
        else if (currentPosition.equals(Tactics.TacticLine.FORWARD))
            return FOR_SCORING_WEIGHT;
        
        return 0d;
    }
    
    private final static double MID_REBOUNDING_WEIGHT = 0.5;
    private final static double FOR_REBOUNDING_WEIGHT = 1;
    
    public static double getReboundingWeight(Tactics.TacticLine currentPosition) {
        if (currentPosition.equals(Tactics.TacticLine.MIDFIELDER))
            return 0.5;
        else if (currentPosition.equals(Tactics.TacticLine.FORWARD))
            return 1;
        else
            return 0d;
    }
    
    // Shots on outcomes probabilistic model
    public final static int AFTER_SHOT_DEFENDER = 0;
    public final static int AFTER_SHOT_GK = 1;
    public final static int AFTER_SHOT_CORNER_KICK = 2;
    public final static int AFTER_SHOT_FORWARD = 3;
    public final static int AFTER_SHOT_DEFENDER_SAVE_THROW_IN = 4;
    public final static int AFTER_SHOT_THROW_IN = 5;
    public final static int AFTER_SHOT_POST_GOAL_KICK = 6;
    public final static int AFTER_SHOT_POST_DEFENDER = 7;
    public final static int AFTER_SHOT_POST_FORWARD = 8;
    public final static int AFTER_SHOT_POST_GK = 9;
    public final static int AFTER_SHOT_DEFENDER_SAVE_FORWARD = 10;
    
    public final static double [] ShotOnStats = {16.51, 57.8, 11.01, 4.59, 0.92, 2.75, 0.92, 2.75, 0.92, 0.92, 0.92};
    
    public final static double EVAL_Shots = 4;
    
    //
    
    public final static double SHOT_OFF_GOAL_KICK = 0.8;
    
    // Nominal value
    public final static double PENALTY_AWARD_FACTOR = 0.0125d;
    
    // Testing value
    // public final static double PENALTY_AWARD_FACTOR = 0.4d;
    
    // public final static double [] PenaltyShotStats = {0.6, 0.2, 0.2}; // Testing values
    public final static double [] PenaltyShotStats = {0.8, 0.1, 0.1}; // Normal values
    public final static int PENALTY_GOAL = 0;
    public final static int PENALTY_SAVE_DEFENDER = 1;
    public final static int PENALTY_GOAL_KICK = 2;
    
    public final static double avgSkill = 3;
    
    public final static double cleanOppRatio = 0.79;
    
    // Passing action
    public final static double PassInterception = 0.2;
    public final static double PassMarking = 0.2;
    public final static double SUCC_Pass = 0.6;
    
    public final static double EVAL_Pass = 0.33;
    
    // Gk Long Pass action
    public final static double UF_GkLongPass = 0.33;
    public final static double SUCC_GkLongPass = 0.67;
    
    public final static double EVAL_GkLongPass = -0.05; 
    
    // Long Pass action
    public final static double UF_LongPass = 0.33;
    public final static double SUCC_LongPass = 0.67;
    public final static double SUCC_CH_LongPass = 0.2;
    
    public final static double EVAL_LongPass = -0.4;
    
    // Forward pass action
    public final static double UF_ForwardPass = 0.05;
    public final static double ForwardPassMarking = 0.05;
    public final static double ForwardPassGk = 0.05; // Position bound
    public final static double ForwardPassGkCentre = 0.1;
    public final static double ForwardPassInterception = 0.1;
    public final static double SUCC_ForwardPass = 0.73;
    public final static double SUCC_CH_ForwardPass = 0.64;
    
    public final static double EVAL_ForwardPass = 0.71;
    
    // Combination action
    public final static double UF_Combination = 0.33;
    public final static double SUCC_Combination = 0.67;
    
    // Flank pass action
    public final static double UF_FlankPass = 0.04;
    public final static double SUCC_FlankPass = 0.96;
    
    public final static double EVAL_FlankPass = 0.62;
    
    // Flank pass space transitions
    public final static double FlankPass_D2C = 0.92;
    public final static double FlankPass_D2D = 0.08;
    //
    public final static double FlankPass_C2C = 0.17;
    public final static double FlankPass_C2A = 0.83;
    
    // Run ball action
    public final static double RunBallMarking = 0.25;
    public final static double SUCC_RunBall = 0.75;
    
    public final static double EVAL_RunBall = 0.5;
    
    // Run ball space transitions
    public final static double RunBall_D2C = 0.67;
    public final static double RunBall_D2D = 0.33;
    
    // Long throw in action
    public final static double UF_LongThrowIn = 0.17;
    public final static double SUCC_LongThrowIn = 0.83;
    
    // Dribbling action
    public final static double UF_Dribbling = 0.2;
    public final static double DribblingManMarking = 0.4;
    public final static double SUCC_Dribbling = 0.4;
    
    // Long flank pass action
    public final static double UF_LongFlankPass = 0.2;
    public final static double LongFlankPassInterception = 0.2;
    public final static double SUCC_LongFlankPass = 0.6;
    
    // Area pass action
    public final static double UF_AreaPass = 0.4;
    public final static double AreaPassGk = 0.2;
    public final static double SUCC_AreaPass = 0.4;
    
    // Ball control action
    public final static double BallControlManMarking = 0.25;
    public final static double SUCC_BallControl = 0.75;
    
    public final static double EVAL_BallControl = -0.17;
    
    // Cross action
    public final static double UF_Cross = 0.16;
    public final static double CrossGoalScoring = 0.08;
    public final static double CrossGk = 0.08;
    public final static double SUCC_Cross = 0.68;
    
    public final static double EVAL_Cross = -0.1;
    
    // Low cross action
    public final static double UF_LowCross = 0.5;
    public final static double SUCC_LowCross = 0.5;
    
    public final static double EVAL_LowCross = -0.25;
    
    // Special evaluation section
    public final static double EVAL_SAVES = 2.0;
    public final static double EVAl_CONCEDINGS = 1.0;
    public final static double EVAL_PENALTIES_MISSED = 3.2;
    public final static double EVAL_PENALTIES_SAVED = 4.0;
    public final static double EVAL_PENALTY_GOALS = 0.4;
    
    public final static double EXP_GkLongPass = 0.64;
    public final static double EXP_LongPass = 0.56;
    public final static double EXP_ForwardPass = 0.59;
    public final static double EXP_FlankPass = 0.72;
    public final static double EXP_RunBall = 0.52;
    public final static double EXP_Dribbling = 0.46;
    public final static double EXP_LongFlankPass = 0.53;
    public final static double EXP_Cross = 0.59;
    public final static double EXP_LowCross = 0.5;
    public final static double EXP_Pass = 0.53;
    public final static double EXP_AreaPass = 0.47;
    public final static double EXP_BallControl = 0.58;
    
    // Action thresholds for player evaluation
    public final static int THR_LongPass_DEF = 1;
    public final static int THR_LongPass_MID = 2;
    
    public final static int THR_ForwPass_DEF = 1;
    public final static int THR_ForwPass_MID = 3;
    
    public final static int THR_FlankPass_DEF = 2;
    public final static int THR_FlankPass_MID = 3;
    
    public final static int THR_BallControl_MID = 1;
    public final static int THR_BallControl_FOR = 1;
    
    public final static int THR_Dribbling_DEF = 1;
    public final static int THR_Dribbling_MID = 4;
    public final static int THR_Dribbling_FOR = 1;
    
    public final static int THR_LongFlankPass_MID = 1;
    
    public final static int THR_Pass_MID = 1;
    
    public final static int THR_AreaPass_MID = 1;
    public final static int THR_AreaPass_FOR = 1;
    
    public final static int THR_RunBall_DEF = 1;
    public final static int THR_RunBall_MID = 1;
    
    public final static int THR_LowCross_MID_CF = 1;
    public final static int THR_LowCross_MID_F = 1;
    public final static int THR_LowCross_FOR_CF = 2;
    public final static int THR_LowCross_FOR_F = 1;
    
    public final static int THR_Cross_DEF_CF = 1;
    public final static int THR_Cross_DEF_F = 2;
    public final static int THR_Cross_MID_C = 1;
    public final static int THR_Cross_MID_CF = 6;
    public final static int THR_Cross_MID_F = 6;
    public final static int THR_Cross_FOR_CF = 5;
    public final static int THR_Cross_FOR_F = 6;
    
    public final static int THR_SHOTS_MID = 1;
    public final static int THR_SHOTS_FOR = 2;
    
    public final static int THR_HEADINGS_FOR_CF = 1;
    public final static int THR_HEADINGS_FOR_C = 1;
    
    public final static int THR_IndCh_DEF = 1;
    public final static int THR_IndCh_MID = 1;
}
