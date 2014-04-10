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
import com.openbusiness.opta.dbs.DBSBranch;
import com.openbusiness.gen.Location;
//import com.openbusiness.gen.DeliveryOrder;

// Nothing special
//@XStreamAlias("VrpTimeWindowedCustomer")
public class DBSAfternoonBranch extends DBSBranch {
  public DBSAfternoonBranch( Location l, double v, double w, int a, int d )
  {
    super(l,v,w,a,d);
  }
}
