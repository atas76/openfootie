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

package report;

import core.State;

/**
 * Used for documenting the transition from one state to the other through an action. This is done mostly for debugging purposes. Contrast that
 * to the 'highlight report', comprising the 'human readable' highlights and not a codified analytic report
 * 
 * @author Andreas Tasoulas
 *
 */

public class ReportObject {
    
    private State actionState;
    private State resultState;
    private byte action;
    private boolean special = false;
    private byte specialEvent;
    private String playerName;
    
    public void setPlayerName(String name) {
        this.playerName = name;
    }
    
    public String getPlayerName() {
        return this.playerName;
    }
    
    public void setActionState(State actionState) {
        this.actionState = actionState;
    }
    public State getActionState() {
        return actionState;
    }
    
    public void setResultState(State resultState) {
        this.resultState = resultState;
    }
    
    public void setSpecial(byte specialEvent) {
        this.special = true;
        this.specialEvent = specialEvent;
    }
    
    public byte getSpecial() {
        return this.specialEvent;
    }
    
    public boolean isSpecial() {
        return special;
    }
    
    public void setAction(byte action) {
        this.action = action;
    }
    
    public byte getAction() {
        return this.action;
    }

}
