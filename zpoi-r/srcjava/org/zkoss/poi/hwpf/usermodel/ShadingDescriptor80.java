/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */

package org.zkoss.poi.hwpf.usermodel;

import org.zkoss.poi.hwpf.model.Colorref;

import org.zkoss.poi.hwpf.model.types.SHD80AbstractType;

/**
 * The SHD80 is a substructure of the CHP and PAP, and TC for Word 97.
 */
public final class ShadingDescriptor80 extends SHD80AbstractType implements
        Cloneable
{

    public ShadingDescriptor80()
    {
    }

    public ShadingDescriptor80( byte[] buf, int offset )
    {
        super();
        fillFields( buf, offset );
    }

    public ShadingDescriptor80( short value )
    {
        super();
        field_1_value = value;
    }

    public ShadingDescriptor80 clone() throws CloneNotSupportedException
    {
        return (ShadingDescriptor80) super.clone();
    }

    public boolean isEmpty()
    {
        return field_1_value == 0;
    }

    public byte[] serialize()
    {
        byte[] result = new byte[getSize()];
        serialize( result, 0 );
        return result;
    }

    public ShadingDescriptor toShadingDescriptor()
    {
        ShadingDescriptor result = new ShadingDescriptor();
        result.setCvFore( Colorref.valueOfIco( getIcoFore() ) );
        result.setCvBack( Colorref.valueOfIco( getIcoBack() ) );
        result.setIpat( getIpat() );
        return result;
    }

    @Override
    public String toString()
    {
        if ( isEmpty() )
            return "[SHD80] EMPTY";

        return "[SHD80] (icoFore: " + getIcoFore() + "; icoBack: "
                + getIcoBack() + "; iPat: " + getIpat() + ")";
    }

}
