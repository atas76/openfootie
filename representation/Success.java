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

package representation;

/**
 * An outcome class representing the success of an action as an outcome 
 * 
 * @author Andreas Tasoulas
 *
 */

public class Success extends Outcome {
    
    public Success(byte condition) {
        this.condition = condition;
    }
    
    public String toString() {
        return "Success: " + condition;
    }
    
    public boolean equals(Outcome outcome) {
        return (outcome instanceof Success && this.condition == outcome.condition);
    }
    
    public int hashCode() {
        return this.condition;
    }

}
