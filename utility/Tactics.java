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

import utility.exceptions.TacticsException;

/**
 * Basic utility class to represent match tactics
 * 
 * @author Andreas Tasoulas
 *
 */

public class Tactics {
    
    public static enum TacticLine {
        GK, DEFENDER, MIDFIELDER, FORWARD
    }
    
    public static enum TacticPosition {
        LEFT, RIGHT, AXIS, LEFT_AXIS, RIGHT_AXIS
    }
    
    private int defenders;
    private int midfielders;
    private int forwards;
    
    public Tactics(int defenders, int midfielders, int forwards) throws TacticsException {
        if (defenders + midfielders + forwards != 10) {
            throw new TacticsException();
        } else {
            this.defenders = defenders;
            this.midfielders = midfielders;
            this.forwards = forwards;
        }
    }
    
    public int getDefenders() {
        return defenders;
    }
    
    public int getMidfielders() {
        return midfielders;
    }
    
    public int getForwards() {
        return forwards;
    }
}
