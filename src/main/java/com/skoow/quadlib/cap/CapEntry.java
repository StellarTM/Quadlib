package com.skoow.quadlib.cap;

import com.skoow.quadlib.utilities.func.Prov;
import com.skoow.quadlib.utilities.struct.CapProvider;
import net.minecraftforge.common.capabilities.Capability;

public class CapEntry<T> {
    public Prov<CapProvider> prov;
    public Prov<Capability<T>> instance;
    public String modId;
    public String capId;
    public int id;
}
