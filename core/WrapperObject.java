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
 * Wrapper class used for reading the probability model file 
 * 
 * @author Andreas Tasoulas
 *
 */

public class WrapperObject {
    
    private byte [] currentRow = new byte[Constants.REC_SIZE];
    
    public void setCurrentRow(byte [] currentRow) {
        this.currentRow = currentRow;
    }
    
    public byte [] getCurrentRow() {
        return this.currentRow;
    }
    
    public WrapperObject(byte [] currentRow) {
        setCurrentRow(currentRow);
    }    
}
