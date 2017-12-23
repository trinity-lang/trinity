package com.github.chrisblutz.trinity.lang.types;

import com.github.chrisblutz.trinity.lang.ClassRegistry;
import com.github.chrisblutz.trinity.lang.TyObject;
import com.github.chrisblutz.trinity.lang.errors.Errors;
import com.github.chrisblutz.trinity.lang.natives.NativeUtilities;
import com.github.chrisblutz.trinity.natives.TrinityNatives;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;


/**
 * @author Christopher Lutz
 */
public class TyMap extends TyObject {
    
    private static int fastStorage = -1, orderedStorage = -1, comparisonStorage = -1;
    
    private Map<TyObject, TyObject> internal;
    private int storageType;
    
    public TyMap(Map<TyObject, TyObject> internal, int storageType) {
        
        super(ClassRegistry.forName(TrinityNatives.Classes.MAP));
        
        this.internal = internal;
        this.storageType = storageType;
    }
    
    public Map<TyObject, TyObject> getInternal() {
        
        return internal;
    }
    
    public int getStorageType() {
        
        return storageType;
    }
    
    public void setStorageType(int storageType) {
        
        Map<TyObject, TyObject> newMap = getMapForStorageType(storageType);
        
        newMap.putAll(internal);
        
        internal = newMap;
    }
    
    public int size() {
        
        return getInternal().size();
    }
    
    public static int getFastStorage() {
        
        if (fastStorage == -1) {
            
            fastStorage = TrinityNatives.toInt(ClassRegistry.getClass(TrinityNatives.Classes.MAP).getField("FAST_STORAGE", false, NONE).getValue());
        }
        
        return fastStorage;
    }
    
    public static int getOrderedStorage() {
        
        if (orderedStorage == -1) {
            
            orderedStorage = TrinityNatives.toInt(ClassRegistry.getClass(TrinityNatives.Classes.MAP).getField("ORDERED_STORAGE", false, NONE).getValue());
        }
        
        return orderedStorage;
    }
    
    public static int getComparisonStorage() {
        
        if (comparisonStorage == -1) {
            
            comparisonStorage = TrinityNatives.toInt(ClassRegistry.getClass(TrinityNatives.Classes.MAP).getField("COMPARISON_STORAGE", false, NONE).getValue());
        }
        
        return comparisonStorage;
    }
    
    public static Map<TyObject, TyObject> getMapForStorageType(int storageType) {
        
        if (storageType == getFastStorage()) {
            
            return new HashMap<>();
            
        } else if (storageType == getOrderedStorage()) {
            
            return new LinkedHashMap<>();
            
        } else if (storageType == getComparisonStorage()) {
            
            return new TreeMap<>(NativeUtilities.getComparator());
            
        } else {
            
            Errors.throwError(Errors.Classes.ARGUMENT_ERROR, "Storage type " + storageType + " not valid.");
            return new HashMap<>();
        }
    }
}