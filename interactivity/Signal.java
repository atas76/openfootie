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
 * The signal class which is the superclass of all the specific signal classes, used in the description of events to external callers.
 * The basic piece of information common to all signal types is the virtual time of the event
 * 
 * @author Andreas Tasoulas
 *
 */

public class Signal {
    
    private int time;
    
    public Signal(int time) {
        this.time = time;
    }
    
    public int getTime() {
        return this.time;
    }
    
    public void setTime(int time) {
        this.time = time;
    }

}
