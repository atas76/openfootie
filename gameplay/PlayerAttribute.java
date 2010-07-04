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

/**
 * The role of the attributes is to define a player's skillset. Each attribute is eventually associated with at least one action and its rate 
 * is the independent variable of the success of that action
 * 
 * @author Andreas Tasoulas
 *
 */

public class PlayerAttribute {
    
    private String name;
    private Integer [] optimalPositions;
    private boolean outfield;
    private boolean sideAffected = false;
    private boolean rightFoot = false;
    private boolean leftFoot = false;
    
    /**
     * Initialize attribute
     * @param name The attribute name
     * @param optimalPositions The player positions for which the attribute must be enhanced over its assigned value
     * @param outfield Whether the attribute corresponds to an outfield player or a goalie
     */
    public PlayerAttribute(String name, Integer [] optimalPositions, boolean outfield) {
        this.name = name;
        this.optimalPositions = optimalPositions;
        this.outfield = outfield;
    }
    
    /**
     * Initialize attribute
     * @param name The attribute name
     * @param optimalPositions The player positions for which the attribute must be enhanced over its assigned value
     * @param outfield Whether the attribute corresponds to an outfield player or a goalie
     * @param sideAffected Whether the attribute should be enhanced for players who play 'naturally' on the flanks  
     */
    public PlayerAttribute(String name, Integer [] optimalPositions, boolean outfield, boolean sideAffected) {
        this.name = name;
        this.optimalPositions = optimalPositions;
        this.outfield = outfield;
        this.sideAffected = sideAffected;
    }
    
    /**
     * Initialize attribute
     * @param name The attribute name
     * @param optimalPositions The player positions for which the attribute must be enhanced over its assigned value
     * @param outfield Whether the attribute corresponds to an outfield player or a goalie
     * @param sideAffected Whether the attribute should be enhanced for players who play 'naturally' on the flanks
     * @param rightFoot Whether the attribute should be enhanced for 'right-footed' players, as defined by their natural position
     * @param leftFoot Whether the attribute should be enhanced for 'left-footed' players, as defined by their natural position
     */
    public PlayerAttribute(String name, Integer [] optimalPositions, boolean outfield, boolean sideAffected, boolean rightFoot, boolean leftFoot) {
        this.name = name;
        this.optimalPositions = optimalPositions;
        this.outfield = outfield;
        this.sideAffected = sideAffected;
        this.rightFoot = rightFoot;
        this.leftFoot = leftFoot;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Integer [] getOptimalPositions() {
        return this.optimalPositions;
    }
    
    public boolean isOutfield() {
        return this.outfield;
    }
    
    public boolean isSideAffected() {
        return this.sideAffected;
    }
    
    public boolean isRightFoot() {
        return this.rightFoot;
    }
    
    public boolean isLeftFoot() {
        return this.leftFoot;
    }
}
