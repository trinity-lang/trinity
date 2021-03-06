package com.github.chrisblutz.trinity.interpreter.instructions;

import com.github.chrisblutz.trinity.interpreter.Location;
import com.github.chrisblutz.trinity.interpreter.Parameters;
import com.github.chrisblutz.trinity.interpreter.actions.InterfaceMethodProcedureAction;
import com.github.chrisblutz.trinity.lang.*;
import com.github.chrisblutz.trinity.lang.errors.Errors;
import com.github.chrisblutz.trinity.lang.procedures.ProcedureAction;
import com.github.chrisblutz.trinity.lang.procedures.TyProcedure;


/**
 * @author Christopher Lutz
 */
public class MethodDeclarationInstructionSet extends InstructionSet {
    
    private String name;
    private boolean isStatic, isSecure, isFinal;
    private Parameters parameters;
    private ProcedureAction body;
    
    public MethodDeclarationInstructionSet(String name, boolean isStatic, boolean isSecure, boolean isFinal, Parameters parameters, ProcedureAction body, Location location) {
        
        super(new Instruction[0], location);
        
        this.name = name;
        this.isStatic = isStatic;
        this.isSecure = isSecure;
        this.isFinal = isFinal;
        this.parameters = parameters;
        this.body = body;
    }
    
    public String getName() {
        
        return name;
    }
    
    public boolean isStatic() {
        
        return isStatic;
    }
    
    public boolean isSecure() {
        
        return isSecure;
    }
    
    public boolean isFinal() {
        
        return isFinal;
    }
    
    public Parameters getParameters() {
        
        return parameters;
    }
    
    public ProcedureAction getBody() {
        
        return body;
    }
    
    @Override
    public TyObject evaluate(TyObject thisObj, TyRuntime runtime) {
        
        if (runtime.getCurrentUsable() instanceof TyClass && ((TyClass) runtime.getCurrentUsable()).isInterface() && getBody() != TyMethod.DEFAULT_METHOD) {
            
            Errors.throwError(Errors.Classes.SCOPE_ERROR, runtime, "Methods within interfaces cannot have a body.");
            
        } else if (runtime.getCurrentUsable() instanceof TyClass && ((TyClass) runtime.getCurrentUsable()).isInterface() && getName().contentEquals("initialize")) {
            
            Errors.throwError(Errors.Classes.SCOPE_ERROR, runtime, "Interfaces cannot declare an 'initialize' method.");
            
        } else if (runtime.getCurrentUsable() instanceof TyClass && ((TyClass) runtime.getCurrentUsable()).isInterface()) {
            
            body = new InterfaceMethodProcedureAction(getName());
        }
        
        TyProcedure methodProcedure = new TyProcedure(getBody(), getParameters().getMandatoryParameters(), getParameters().getOptionalParameters(), getParameters().getBlockParameter(), getParameters().getOverflowParameter(), true);
        methodProcedure.setContainerMethod(getName());
        
        TyMethod method = new TyMethod(getName(), isStatic(), false, isSecure(), isFinal(), runtime.getCurrentUsable(), methodProcedure);
        method.setScope(runtime.getCurrentScope());
        method.setImports(runtime.getImports());
        
        if (runtime.getCurrentUsable() != null) {
            
            runtime.getCurrentUsable().addMethod(method);
            
        } else {
            
            FileMethodRegistry.register(getLocation().getFilePath(), method);
        }
        
        return TyObject.NONE;
    }
}
