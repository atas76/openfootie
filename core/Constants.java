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

/**
 * 
 * @author Andreas Tasoulas
 *
 */

public class Constants {
    
    public static final int REC_SIZE = 19;
    
    public static final int X = 1;
    public static final int Y = 0;
    public static final int PRESSURE = 2;
    public static final int AREA = 3;
    public static final int ACTION = 4;
    public static final int RESULT_DESC = 5;
    public static final int CHALLENGE_Y = 6;
    public static final int CHALLENGE_TEAM = 7;
    public static final int CHALLENGE_ENDING = 8;
    public static final int RES_Y = 9;
    public static final int RES_X = 10;
    public static final int RES_PRESSURE = 11;
    public static final int RES_AREA = 12;
    public static final int RES_TEAM = 13;
    public static final int OTHER_RES = 14;
    public static final int BALL_POSSESSION_CHANGE = 15;
    public static final int CHALLENGE_TYPE = 16;
    public static final int ROW_ID = 17;
    public static final int SPECIAL_EVENT = 18;
    
    // RESULT_DESC
    public static final byte CONDITION = 1;
    public static final byte CHALLENGE = 2;
    public static final byte OTHER = 3;
    
    //  Player positions' indices
    public final static int GK = 1;
    public final static int DEFENDER = 2;
    public final static int MIDFIELDER = 3;
    public final static int FORWARD = 4;
    public final static int RIGHT_INDEX = 5;
    public final static int LEFT_INDEX = 6;
    public final static int CENTRAL_INDEX = 7;
    
    // Player positions
    public final static int GK_POS = 1;
    public final static int DEF_POS = 2;
    public final static int MID_POS = 4;
    public final static int FOR_POS = 8;
    public final static int RIGHT = 16;
    public final static int LEFT = 32;
    public final static int CENTRAL = 64;
    
    // Y, CHALLENGE_Y, RES_Y
    public static final byte DEFENCE = 1;
    public static final byte CENTRE = 2;
    public static final byte ATTACK = 3;
    
    // X, RES_X
    public static final byte AXIS = 1;
    public static final byte FLANK = 2;
    public static final byte THROW_IN = 3;
    public static final byte CORNER_KICK = 4;
    
    public static final String [] actionDescription = {null, "Move Forward", "Long Pass from GK", "Long Pass", "Back Pass", "High Pass", 
        "Dribbling", "Long Shot", "Forward Pass", "Pass", "Pass Combination", "Pass to flank", "Ball control", "Long throw in", 
        "Pass to GK", "Run with Ball", "Easy pass", "High dribble", "Counter attack", "Pass forward under pressing", 
        "Long pass to flank", "Long cross", "First touch pass", "Strong pass", "Cross", "Low cross", "Move back", "Low area cross",
        "Shot from penalty area", "Run to flank with ball", "Long pass to the area", "Delay", "Pass forward to flank", "Pass to area",
        "Throw in back", "Change of flank", "Pass forward long in flank", "Kick Away", "Move forward in flank", "Loose forward pass",
        "Throw in", "High flank pass", "Lame ball control", "Lame pass", "High area pass"};
    
    // ACTION
    public static final byte MoveForward = 1;
    public static final byte GkLongPass = 2;
    public static final byte LongPass = 3;
    public static final byte BackPass = 4;
    public static final byte HighPass = 5;
    public static final byte Dribbling = 6;
    public static final byte LongShot = 7;
    public static final byte ForwardPass = 8;
    public static final byte Pass = 9;
    public static final byte Combination = 10;
    public static final byte FlankPass = 11;
    public static final byte BallControl = 12;
    public static final byte LongThrowIn = 13;
    public static final byte GkPass = 14;
    public static final byte RunBall = 15;
    public static final byte EasyPass = 16;
    public static final byte HighDribble = 17;
    public static final byte CounterAttack = 18;
    public static final byte ForwardCirculation = 19;
    public static final byte LongFlankPass = 20;
    public static final byte LongCross = 21;
    public static final byte FirstTouchPass = 22;
    public static final byte StrongPass = 23;
    public static final byte Cross = 24;
    public static final byte LowCross = 25;
    public static final byte MoveBack = 26;
    public static final byte LowAreaCross = 27;
    public static final byte AreaShot = 28;
    public static final byte RunBallFlank = 29;
    public static final byte LongAreaPass = 30;
    public static final byte CirculationDelay = 31;
    public static final byte FlankForwardPass = 32;
    public static final byte AreaPass = 33;
    public static final byte BackThrowIn = 34;
    public static final byte LongOppositeFlankPass = 35;
    public static final byte LongFlankForwardPass = 36;
    public static final byte KickAway = 37;
    public static final byte MoveFlankForward = 38;
    public static final byte LooseForwardPass = 39;
    public static final byte ThrowInPass = 40;
    public static final byte HighFlankPass = 41;
    public static final byte LameControl = 42;
    public static final byte LamePass = 43;
    public static final byte HighAreaPass = 44;
 
    // PRESSURE, RES_PRESSURE
    public static final byte CLEAR = 1;
    public static final byte UNDER = 2;
    public static final byte AVOID = 3;
    
    // AREA, RES_AREA
    public static final byte ON_AREA = 0;
    public static final byte OFF_AREA = 1;
    
    // CHALLENGE_TEAM, RES_TEAM
    public static final byte OWN_TEAM = 1;
    public static final byte OPP_TEAM = 0;
    
    // CHALLENGE_ENDING, OTHER_RES
    public static final byte RES_Offside = 1;
    public static final byte RES_Teammate = 2;
    public static final byte RES_Foul_Tackling = 3;
    public static final byte RES_BackPass = 4;
    public static final byte RES_ShotOff = 5;
    public static final byte RES_Tackling = 6;
    public static final byte RES_Advantage = 8;
    public static final byte RES_Opponent = 10;
    public static final byte RES_GoalKick = 11;
    public static final byte RES_Dominance = 12;
    public static final byte RES_GkFoul = 13;
    public static final byte RES_ThrowIn = 14;
    public static final byte RES_ShotOffCorner = 15;
    public static final byte RES_OffensiveFoul = 16;
    public static final byte RES_Goal = 17;
    public static final byte RES_ShotOnDefender = 18;
    public static final byte RES_HangingPass = 20;
    public static final byte RES_Rebounding = 22;
    public static final byte RES_Foul = 24;
    public static final byte RES_Dribble = 26;
    public static final byte RES_LooseBall = 28;
    public static final byte RES_StrongRushedPass = 30;
    public static final byte RES_StrongManChallenge = 32;
    public static final byte RES_FlankPass = 34;
    public static final byte RES_DefenderSave = 36;
    public static final byte RES_CutOpponent = 38;
    public static final byte RES_BallControl = 40;
    public static final byte RES_InstantChallenge = 42;
    public static final byte RES_StrongHeadingChallenge = 44;
    public static final byte RES_TacklingPassing = 46;
    public static final byte RES_ManChallenge = 48;
    public static final byte RES_FirstTouchChallenge = 50;
    public static final byte RES_PassTooStrong = 52;
    public static final byte RES_SmartPass = 54;
    public static final byte RES_Combination = 56;
    public static final byte RES_WrongPass = 58;
    public static final byte RES_KickAwayLow = 60;
    
    // BALL POSSESSION CHANGE
    public static final byte BPC_Normal = 1;
    public static final byte BPC_PassInterception = 2;
    public static final byte BPC_Gk = 3;
    public static final byte BPC_ManChallengeLost = 4;
    public static final byte BPC_LooseBall = 5;
    public static final byte BPC_LostBallControl = 6;
    public static final byte BPC_BouncingOff = 7;
    
    // CHALLENGE TYPE
    public static final byte CH_Normal = 1;
    public static final byte CH_Aerial = 2;
    public static final byte CH_EasyHeading = 3;
    public static final byte CH_BouncingOff = 4;
    public static final byte CH_KickAway = 5;
    public static final byte CH_DefenderSave = 6;
}
