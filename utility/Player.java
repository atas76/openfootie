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

/**
 * This class is for holding basic information about a player: shirt number in the team, first and last name
 * 
 * @author Andreas Tasoulas
 *
 */

public class Player {

    private int shirtNo;
    private String firstName;
    private String familyName;
    
    public int getShirtNo() {
        return shirtNo;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public String getFamilyName() {
        return familyName;
    }
    
    public void setShirtNo(int shirtNo) {
        this.shirtNo = shirtNo;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }
    
    public Player(int shirtNo, String firstName, String familyName) {
        this.shirtNo = shirtNo;
        this.firstName = firstName;
        this.familyName = familyName;
    }
}
