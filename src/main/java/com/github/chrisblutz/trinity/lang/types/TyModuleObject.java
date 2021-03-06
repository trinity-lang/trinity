package com.github.chrisblutz.trinity.lang.types;

import com.github.chrisblutz.trinity.lang.ClassRegistry;
import com.github.chrisblutz.trinity.lang.TyModule;
import com.github.chrisblutz.trinity.lang.TyObject;
import com.github.chrisblutz.trinity.natives.NativeReferences;


/**
 * @author Christopher Lutz
 */
public class TyModuleObject extends TyObject {
    
    private TyModule internal;
    
    public TyModuleObject(TyModule internal) {
        
        super(ClassRegistry.forName(NativeReferences.Classes.MODULE, true));
        
        this.internal = internal;
    }
    
    public TyModule getInternal() {
        
        return internal;
    }
}
