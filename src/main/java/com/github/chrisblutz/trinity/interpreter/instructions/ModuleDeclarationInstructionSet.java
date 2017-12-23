package com.github.chrisblutz.trinity.interpreter.instructions;

import com.github.chrisblutz.trinity.interpreter.Location;
import com.github.chrisblutz.trinity.lang.ModuleRegistry;
import com.github.chrisblutz.trinity.lang.TyModule;
import com.github.chrisblutz.trinity.lang.TyObject;
import com.github.chrisblutz.trinity.lang.TyRuntime;
import com.github.chrisblutz.trinity.lang.procedures.ProcedureAction;


/**
 * @author Christopher Lutz
 */
public class ModuleDeclarationInstructionSet extends InstructionSet {
    
    private String name;
    private ProcedureAction body;
    
    public ModuleDeclarationInstructionSet(String name, ProcedureAction body, Location location) {
        
        super(new Instruction[0], location);
        
        this.name = name;
        this.body = body;
    }
    
    public String getName() {
        
        return name;
    }
    
    public ProcedureAction getBody() {
        
        return body;
    }
    
    @Override
    public TyObject evaluate(TyObject thisObj, TyRuntime runtime) {
        
        String priorModuleName = "";
        if (runtime.getCurrentModule() != null) {
            
            priorModuleName = runtime.getCurrentModule().getFullName() + ".";
        }
        
        TyModule module = ModuleRegistry.forName(priorModuleName + getName());
        if (runtime.getCurrentModule() != null) {
            
            runtime.getCurrentModule().addInternalModule(module);
        }
        
        TyRuntime newRuntime = runtime.cloneWithImports();
        newRuntime.setCurrentUsable(module);
        
        getBody().onAction(newRuntime, TyObject.NONE);
        newRuntime.disposeVariablesInto(runtime);
        
        return TyObject.NONE;
    }
}
