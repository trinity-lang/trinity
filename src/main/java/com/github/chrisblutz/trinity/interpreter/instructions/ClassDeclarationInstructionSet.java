package com.github.chrisblutz.trinity.interpreter.instructions;

import com.github.chrisblutz.trinity.interpreter.Location;
import com.github.chrisblutz.trinity.lang.*;
import com.github.chrisblutz.trinity.lang.errors.Errors;
import com.github.chrisblutz.trinity.lang.procedures.ProcedureAction;
import com.github.chrisblutz.trinity.natives.NativeStorage;
import com.github.chrisblutz.trinity.utils.ArrayUtils;


/**
 * @author Christopher Lutz
 */
public class ClassDeclarationInstructionSet extends InstructionSet {
    
    private String name;
    private String superclass;
    private String[] superinterfaces;
    private boolean isFinal;
    private ProcedureAction body;
    
    public ClassDeclarationInstructionSet(String name, String superclass, String[] superinterfaces, boolean isFinal, ProcedureAction body, Location location) {
        
        super(new Instruction[0], location);
        
        this.name = name;
        this.superclass = superclass;
        this.superinterfaces = superinterfaces;
        this.isFinal = isFinal;
        this.body = body;
    }
    
    public String getName() {
        
        return name;
    }
    
    public String getSuperclass() {
        
        return superclass;
    }
    
    public String[] getSuperinterfaces() {
        
        return superinterfaces;
    }
    
    public boolean isFinal() {
        
        return isFinal;
    }
    
    public ProcedureAction getBody() {
        
        return body;
    }
    
    @Override
    public TyObject evaluate(TyObject thisObj, TyRuntime runtime) {
        
        String priorName = "";
        if (runtime.getCurrentUsable() != null) {
            
            priorName = runtime.getCurrentUsable().getFullName() + ".";
        }
        
        TyClass tyClass = ClassRegistry.forName(priorName + getName(), isFinal());
        tyClass.setParent(runtime.getCurrentUsable());
        if (getSuperclass() != null) {
            
            TyClass superclass;
            if (runtime.getCurrentUsable() != null && runtime.getCurrentUsable().hasInternalClass(getSuperclass())) {
                
                superclass = runtime.getCurrentUsable().getInternalClass(getSuperclass());
                
            } else if (runtime.hasImports() && runtime.hasImportedClass(getSuperclass())) {
                
                superclass = runtime.getImportedClass(getSuperclass());
                
            } else if (runtime.getCurrentModule() != null && runtime.getCurrentModule().hasInternalClass(getSuperclass())) {
                
                superclass = runtime.getCurrentModule().getInternalClass(getSuperclass());
                
            } else if (ModuleRegistry.getModule("Trinity").hasInternalClass(getSuperclass())) {
                
                superclass = ModuleRegistry.getModule("Trinity").getInternalClass(getSuperclass());
                
            } else {
                
                superclass = ClassRegistry.getClass(getSuperclass());
            }
            
            if (superclass.isFinal()) {
                
                Errors.throwError(Errors.Classes.INHERITANCE_ERROR, "Final class '" + superclass.getFullName() + "' cannot be subclassed.");
            }
            
            tyClass.setSuperclass(superclass);
        }
        TyClass[] superinterfaces = new TyClass[getSuperinterfaces().length];
        for (int i = 0; i < getSuperinterfaces().length; i++) {
            
            String superinterface = getSuperinterfaces()[i];
            TyClass superinterfaceClass;
            if (runtime.getCurrentUsable() != null && runtime.getCurrentUsable().hasInternalClass(superinterface)) {
                
                superinterfaceClass = runtime.getCurrentUsable().getInternalClass(superinterface);
                
            } else if (runtime.getCurrentModule() != null && runtime.getCurrentModule().hasInternalClass(superinterface)) {
                
                superinterfaceClass = runtime.getCurrentModule().getInternalClass(superinterface);
                
            } else if (ModuleRegistry.getModule("Trinity").hasInternalClass(superinterface)) {
                
                superinterfaceClass = ModuleRegistry.getModule("Trinity").getInternalClass(superinterface);
                
            } else {
                
                superinterfaceClass = ClassRegistry.getClass(getSuperinterfaces()[i]);
            }
            
            superinterfaces[i] = superinterfaceClass;
        }
        
        if (superinterfaces.length > 0) {
            
            TyClass[] combined = ArrayUtils.combine(tyClass.getSuperinterfaces(), superinterfaces);
            tyClass.setSuperinterfaces(combined);
        }
        
        if (runtime.getCurrentUsable() != null) {
            
            runtime.getCurrentUsable().addInternalClass(tyClass);
        }
        
        if (getBody() != null) {
            
            TyRuntime newRuntime = runtime.clone();
            newRuntime.setCurrentUsable(tyClass);
            newRuntime.setStaticScope(true);
            newRuntime.setStaticScopeObject(NativeStorage.getClassObject(tyClass));
            
            getBody().onAction(newRuntime, TyObject.NONE);
            newRuntime.disposeVariablesInto(runtime);
        }
        
        return TyObject.NONE;
    }
}
