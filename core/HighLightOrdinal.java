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
 * This is used as an index of the match highlights. It comprises the virtual time in which the highlight take place along with its sequence 
 * in all highlights with the same virtual time.
 * 
 * @author Andreas Tasoulas
 * 
 */

public class HighLightOrdinal implements Comparable {
    
    private int time;
    private int sequence;
    
    public HighLightOrdinal(int time, int sequence) {
        this.time = time;
        this.sequence = sequence;
    }
    
    public int getTime() {
        return this.time;
    }
    
    public int getSequence() {
        return this.sequence;
    }
    
    public boolean isSimultaneous(int time) {
        return (this.time == time);
    }

    public int compareTo(Object A) {
        
        HighLightOrdinal a = (HighLightOrdinal) A;
        
        if (this.time < a.time) {
            return -1;
        } else if (this.time > a.time) {
            return 1;
        } else if (a.time == this.time) {
            
            if (this.sequence < a.sequence) {
                return -1;
            } else if (this.sequence > a.sequence) {
                return 1;
            }
            
        }

        return 0;
     
    }
    
    public String toString() {
        return "t: " + this.time + " s: " + this.sequence;
    }

}
