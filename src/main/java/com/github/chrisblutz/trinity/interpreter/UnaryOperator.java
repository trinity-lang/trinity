package com.github.chrisblutz.trinity.interpreter;

import com.github.chrisblutz.trinity.lang.TyObject;
import com.github.chrisblutz.trinity.lang.TyRuntime;
import com.github.chrisblutz.trinity.parser.tokens.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Christopher Lutz
 */
public abstract class UnaryOperator {
    
    private static List<Token> tokens = new ArrayList<>();
    private static Map<Token, UnaryOperator> operators = new HashMap<>();
    
    private Token token;
    
    public UnaryOperator(Token token) {
        
        this.token = token;
        
        tokens.add(token);
        operators.put(token, this);
    }
    
    public Token getToken() {
        
        return token;
    }
    
    public abstract TyObject operate(TyObject value, TyRuntime runtime);
    
    public static List<Token> getOperators() {
        
        return tokens;
    }
    
    public static UnaryOperator getOperator(Token token) {
        
        return operators.get(token);
    }
}
