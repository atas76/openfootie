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
 * An object carrying information about a shot coming from a cross. The crosser name is specific to this event signal
 * 
 * @author Andreas Tasoulas
 *
 */

public class CrossShot extends Shot {
    
    private String crosser;
    
    public CrossShot(int timerStart, int basicOutcome, int detailedOutcome, String shooter, String crosser, String teamName) {
        super(timerStart, basicOutcome, detailedOutcome, shooter, teamName);
        this.crosser = crosser;
    }
    
    public String getCrosser() {
        return this.crosser;
    }
    
    public String toString() {
        return super.toString() + "(Cross by " + crosser + ")";
    }

}
