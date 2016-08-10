package com.sundayliu.xposed.hijacklogin;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class HijackLogin implements IXposedHookLoadPackage{

    @Override
    public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
        if ((lpparam.packageName.startsWith("com.android")
                || lpparam.packageName.startsWith("com.google.android"))
                ){
            return;
        }
        
        if (!(lpparam.packageName.startsWith("com.sundayliu") 
                || lpparam.packageName.startsWith("com.tencent") )){
            return ;
        }
        
        if (lpparam.packageName.equals("com.tencent.android.qqdownloader")){
            return;
        }
        
        if (lpparam.packageName.contains(":")){
            return;
        }
        
        XposedBridge.log("[hijack]" + lpparam.packageName);
        int pid = android.os.Process.myPid();
        XposedBridge.log("Current Pid:" + pid);
        
        if (lpparam.packageName.equals("com.sundayliu.demo.login"))
        {
            // com.sundayliu.demo
            pid = android.os.Process.myPid();
            XposedBridge.log("Current Pid:" + pid);
            XposedBridge.log("loadLibrary ...");
            System.load("/data/local/tmp/lib/libhack_login.so");
            
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
//            try{
//                // System.loadLibrary("hack_login");
//               //  getApplicationContext();
//                XposedBridge.log("loadLibrary ...");
//                System.load("/data/local/tmp/lib/libhack_login.so");
//            }
//            catch(Exception e){
//                XposedBridge.log("exception:" + e.getMessage());
//            }
            
        }
        else if (lpparam.packageName.startsWith("com.tencent")){
            // tencent app
            XposedBridge.log("[hijack] tencent game ...");
            if (lpparam.packageName.equals("com.tencent.clover")
                    || lpparam.packageName.equals("com.tencent.tmgp.sgame")){
                XposedBridge.log("loadLibrary ...");
                System.load("/data/local/tmp/lib/libtgamaster.so");
            }
            
        }
        
    }
    
}
