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

import java.util.Random;

/**
 * The state of the match. All the information that describe the match at any specific time are included in objects of this class. 
 * Each type of information is documented in its own right
 *  
 * @author Andreas Tasoulas
 *
 */

public class State {
    
    /**
     * The horizontal or 'X' axis has four different values: 
     * AXIS (sorry for my English - this is used as antonym of flank in my mother tongue)
     * FLANK
     * THROW_IN
     * CORNER_KICK
     * 
     * @author Andreas Tasoulas
     *
     */
    public enum X {
        AXIS,
        FLANK,
        THROW_IN,
        CORNER_KICK;
        
        public boolean matchValue(int value) {
            switch (value) {
            case Constants.AXIS:
                if (this.ordinal() == AXIS.ordinal()) return true;
                break;
            case Constants.FLANK:
                if (this.ordinal() == FLANK.ordinal()) return true;
                break;
            case Constants.THROW_IN:
                if (this.ordinal() == THROW_IN.ordinal()) return true;
                break;
            case Constants.CORNER_KICK:
                if (this.ordinal() == CORNER_KICK.ordinal()) return true;
                break;
            default:
                return false;
            }
            return false;
        }
        
        public static X getNativeValue(byte value) {
            switch (value) {
            case Constants.AXIS:
                return X.AXIS;
            case Constants.FLANK:
                return X.FLANK;
            case Constants.THROW_IN:
                return X.THROW_IN;
            case Constants.CORNER_KICK:
                return X.CORNER_KICK;
            }
            
            return null;
        }
    }
    
    /**
     * The vertical or 'Y' axis has three different values:
     * DEFENCE
     * CENTRE
     * ATTACK
     * 
     * @author Andreas Tasoulas
     *
     */
    public enum Y {
        DEFENCE,
        CENTRE,
        ATTACK;
        
        public boolean matchValue(int value) {
            switch(value) {
            case Constants.DEFENCE:
                if (this == DEFENCE) return true;
                break;
            case Constants.CENTRE:
                if (this == CENTRE) return true;
                break;
            case Constants.ATTACK:
                if (this == ATTACK) return true;
                break;
            default:
                return false;
            }
            return false;
        }
        
        public static Y getNativeValue(byte value) {
            switch(value) {
            case Constants.DEFENCE:
                return Y.DEFENCE;
            case Constants.CENTRE:
                return Y.CENTRE;
            case Constants.ATTACK:
                return Y.ATTACK;
            }
            return null;
        }
    }
    
    /**
     * Denotes whether the holder of the ball is under pressure or not
     * 
     * @author Andreas Tasoulas
     *
     */
    public enum Pressure {
        FREE,
        PRESSED;
        
        public boolean matchValue(int value) {
            switch(value) {
            case Constants.CLEAR:
                if (this == FREE) return true;
                break;
            case Constants.UNDER:
            case Constants.AVOID:
                if (this == PRESSED) return true;
                break;
            default:
                return false;
            }
            return false;
        }
        
        public static Pressure getNativeValue(byte value) {
            switch(value) {
            case Constants.UNDER:
            case Constants.AVOID:
                return Pressure.PRESSED;
            case Constants.CLEAR:
                return Pressure.FREE;
            }
            return null;
        }
    }
    
    private static Random rnd = new Random();
    
    private Team team;
    private X x;
    private Y y;
    private Pressure pressure;
    
    private Player player;
    
    private Player crosser;
    
    private int side;
    
    private boolean freeKickOpportunity;
    
    public void setFreeKickOpportunity(boolean freeKickOpportunity) {
        this.freeKickOpportunity = freeKickOpportunity;
    }
    
    public boolean isFreeKickOpportunity() {
        return this.freeKickOpportunity;
    }
    
    public boolean inFlank() {
        return (x == X.FLANK || x == X.CORNER_KICK || x == X.THROW_IN);
    }
    
    public void setPlayer(Player player) {
        this.player = player;
    }
    
    public void setCrosser(Player player) {
        this.crosser = player;
    }
    
    public void setSide(int side) {
        this.side = side;
    }
    
    public void setRandomSide() {
        this.side = rnd.nextInt(2) + 1; 
    }
    
    /**
     * Change the current side to the opposite one
     * Why do we need this? The most usual example is when the ball possession changes, so the side needs to change as well.
     * @param side The current side
     */
    public void changeSide(int side) {
        if (side == OOConstants.LEFT_SIDE) {
            this.side = OOConstants.RIGHT_SIDE; 
        } else if (side == OOConstants.RIGHT_SIDE) {
            this.side = OOConstants.LEFT_SIDE;
        }
    }
    
    public int getSide() {
        return this.side;
    }
    
    public Player getCrosser() {
        return this.crosser;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    /**
     * Accumulation of all the information needed to be included in a State variable.
     * @param team The team having possession of the ball
     * @param x The x coordinate of the ball
     * @param y The y coordinate of the ball
     * @param pressure The player/team having the ball is under pressure or not
     */
    public State(Team team, X x, Y y, Pressure pressure) {
        this.team = team;
        this.x = x;
        this.y = y;
        this.pressure = pressure;
    }
    
    public String toString() {
        return "(" + team.getName() + "," + y.toString() + "," + x.toString() + "," + pressure.toString() + ")";
    }
    
    public Team getTeam() {
        return team;
    }
    
    public X getX() {
        return x;
    }
    
    public Y getY() {
        return y;
    }
    
    public Pressure getPressure() {
        return pressure;
    }
    
    public void setPressure(Pressure pressure) {
        this.pressure = pressure;
    }
}
