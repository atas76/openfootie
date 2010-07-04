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

import java.util.ArrayList;

/**
 * Utility class for gathering all the player attributes supported by the match engine
 * 
 * @author Andreas Tasoulas
 *
 */

public class PlayerAttributes {
    
    private static ArrayList<PlayerAttribute> all = null;
    
    /**
     * Getter
     * @return All player attributes supported by the match engine
     */
    public static ArrayList<PlayerAttribute> getAll() {
        if (all == null) init();
        return all;
    }
    
    /**
     * Initialization of the player attributes. The hardcoded attributes are loaded on the class
     *
     */
    private static void init() {
        
        Integer [] midfield = {3};
        Integer [] defmidfield = {2,3};
        Integer [] goalkeeper = {1};
        Integer [] forward = {4};
        Integer [] midforward = {3,4};
        
        all = new ArrayList<PlayerAttribute>();
        
        all.add(new PlayerAttribute("Passing", midfield, true));
        all.add(new PlayerAttribute("Teamwork", null, true));
        all.add(new PlayerAttribute("BallControl", midfield, true));
        all.add(new PlayerAttribute("ThrowIn", null, true));
        all.add(new PlayerAttribute("Dribbling", midfield, true));
        all.add(new PlayerAttribute("Crossing", midfield, true, true));
        all.add(new PlayerAttribute("ZonalMarking", midfield, true));
        all.add(new PlayerAttribute("ManMarking", defmidfield, true));
        all.add(new PlayerAttribute("RushingOut", goalkeeper, false));
        all.add(new PlayerAttribute("Handling", goalkeeper, false));
        all.add(new PlayerAttribute("Shooting", forward, true));
        all.add(new PlayerAttribute("Pace", midforward, true, true));
        all.add(new PlayerAttribute("Heading", forward, true));
        all.add(new PlayerAttribute("RightFoot", null, true, false, true, false));
        all.add(new PlayerAttribute("LeftFoot", null, true, false, false, true));
        
    }

}
