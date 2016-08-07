package com.sundayliu.xposed.hijacklogin;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class HijackLogin implements IXposedHookLoadPackage{

    @Override
    public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.sundayliu.demo.login")){
            return;
        }
        
        XposedBridge.log("[hijack]app:" + lpparam.packageName);
        
        XposedHelpers.findAndHookMethod(
                "com.sundayliu.demo.login.MainActivity", 
                lpparam.classLoader,
                "isValidInfo", 
                String.class, 
                String.class,
                new XC_MethodHook(){
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable{
                        XposedBridge.log("Hijack before...");
                        XposedBridge.log("param 1 = " + param.args[0]);
                        XposedBridge.log("param 2 = " + param.args[1]);
                        
                        param.args[0] = "Login";
                        param.args[1] = "Login";
                    }
                    
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable{
                        XposedBridge.log("Hijack after...");
                        XposedBridge.log("param 1 = " + param.args[0]);
                        XposedBridge.log("param 2 = " + param.args[1]);
                    }
                });
        
    }
    
}
