/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.openbusiness.opta.dbs;

//import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.openbusiness.opta.Destination;
import com.openbusiness.gen.Location;
//import com.openbusiness.gen.DeliveryOrder;

//@XStreamAlias("VrpTimeWindowedCustomer")
public class DBSBranch extends Destination {

    // Times are multiplied by 1000 to avoid floating point arithmetic rounding errors
    private int milliReadyTime;
    private int milliDueTime;
    private int milliServiceDuration;

    // Shadow variable
    private Integer milliArrivalTime;

    // Branch Information
    private String m_name;
    private String m_address;
    private String m_postcode;
    private String m_t;
    private String m_f;
    private String m_hours_weekdays;
    private String m_hours_saturday;

    public DBSBranch(){}
    
    public DBSBranch( String name,
    		      String address,
		      String postCode,
		      String t,
		      String f,
		      String hoursWeek,
		      String hoursSat,
		      Location loc,
		      double vol,
		      double weight )
    {
       this( loc, vol, weight,0,0 );
       
       m_name = name;
       m_address = address;
       m_postcode = postCode;
       m_t = t;
       m_f = f;
       m_hours_weekdays = hoursWeek;
       m_hours_saturday = hoursSat;
    }

    public DBSBranch( Location loc,
    		      double vol,
		      double weight, 
    		      int milliReadyTime, 
		      int milliDueTime )
    {
       super(loc,vol,weight);
       this.milliReadyTime = milliReadyTime;
       this.milliDueTime = milliDueTime;
    }

    public int getMilliReadyTime() {
        return milliReadyTime;
    }

    public void setMilliReadyTime(int milliReadyTime) {
        this.milliReadyTime = milliReadyTime;
    }

    public int getMilliDueTime() {
        return milliDueTime;
    }

    public void setMilliDueTime(int milliDueTime) {
        this.milliDueTime = milliDueTime;
    }

    public int getMilliServiceDuration() {
        return milliServiceDuration;
    }

    public void setMilliServiceDuration(int milliServiceDuration) {
        this.milliServiceDuration = milliServiceDuration;
    }

    public Integer getMilliArrivalTime() {
        return milliArrivalTime;
    }

    public void setMilliArrivalTime(Integer milliArrivalTime) {
        this.milliArrivalTime = milliArrivalTime;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public String getTimeWindowLabel() {
        return milliReadyTime + "-" + milliDueTime;
    }

    public Integer getDepartureTime() {
        if (milliArrivalTime == null) {
            return null;
        }
        return Math.max(milliArrivalTime, milliReadyTime) + milliServiceDuration;
    }

    public boolean isArrivalBeforeReadyTime() {
        return milliArrivalTime != null
                && milliArrivalTime < milliReadyTime;
    }

    public boolean isArrivalAfterDueTime() {
        return milliArrivalTime != null
                && milliDueTime < milliArrivalTime;
    }

    @Override
    public DBSBranch getNextDestination() {
        return (DBSBranch) super.getNextDestination();
    }

}
