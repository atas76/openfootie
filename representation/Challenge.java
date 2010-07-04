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
 * A challenge is a situation in a match where the ball possesion is not clear between teams (like two players jumping for a header or ball is in the
 * air for too much time)
 * 
 * @author Andreas Tasoulas
 *
 */

public class Challenge extends Outcome {
    
    public byte startTeam;
    public byte startY;
    public byte endTeam;
    public byte endY;
    public byte endX;
    public byte challengeEnding;
    public byte type;

    /**
     * Initialization
     * @param startTeam The team that had the ball possession before the challenge started
     * @param startY The Y-axis coordinate the time that the challenge started
     * @param endTeam The team having the ball possession in the end of a challenge
     * @param endY The Y-axis coordinate the time of the challenge end
     * @param endX The X-axis coordinate the time of the challenge end
     * @param challengeEnding How the challenge ended (e.g. a throw-in awarded)
     * @param type The type of challenge, e.g an aerial challenge or a challenge from a defender save
     */
    public Challenge(byte startTeam, byte startY, byte endTeam, byte endY, byte endX, byte challengeEnding, byte type) {
        this.startTeam = startTeam;
        this.startY = startY;
        this.endTeam = endTeam;
        this.endY = endY;
        this.endX = endX;
        this.challengeEnding = challengeEnding;
        this.type = type;
        this.condition = -1;
    }
    
    public String toString() {
        return "Challenge: " + startTeam + "," + startY + "," + endTeam + "," + endY + "," + endX + "," + challengeEnding + "," + type;
    }
    
    public boolean equals(Outcome outcome) {
        return (outcome instanceof Challenge);
    }
    
    public int hashCode() {
        return this.condition;
    }

}
