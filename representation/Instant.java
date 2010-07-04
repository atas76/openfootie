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
 * An "instant" corresponds to a record of the probability model file. It describes the transition from one state to the next. This is done
 * in one time step of the virtual timing, hence the name
 * 
 * @author Andreas Tasoulas
 *
 */

public class Instant {
    
    public byte Y;
    public byte X;
    public byte Pressure;
    public byte Action;
    public byte rowId; // for debugging purposes
    
    public ResultState outcomeState;
    
    public Outcome outcome;
    
    public Instant(byte Y, byte X, byte Pressure, byte Action) {
        this.Y = Y;
        this.X = X;
        this.Pressure = Pressure;
        this.Action = Action;
    }
    
    public String toString() {
        
        String initState = rowId + " Y: " + Y + " X: " + X + " Pressure: " + Pressure + " Action: " + Action;
        String resultState = outcomeState.toString();
        String details = outcome.toString();
        
        return initState + " -> " + resultState + "(" + details + ")";
        
    }
    
    public boolean equals(Instant instant) {
        return (Y == instant.Y && X == instant.X && Pressure == instant.Pressure && Action == instant.Action);
    }
    
    public boolean equalsExceptPressure(Instant instant) {
        return (Y == instant.Y && X == instant.X && Action == instant.Action);
    }
    
    public int hashCode() {
        return Y * X * Pressure * Action;
    }

}
