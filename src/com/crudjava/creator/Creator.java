package com.crudjava.creator;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Scanner;

import com.crudjava.controllers.ControllerManager;
import com.crudjava.persistance.Dao;
import com.crudjava.persistance.DatabaseConnection;

public class Creator<T, typeId> {

    private ControllerManager<T, typeId> controllerManager;
    private HashMap<String,String> configHashMap;

    public Creator() {
        this.controllerManager = new ControllerManager<T,typeId>();
    }
    
    public  T createObject() {
		return null;
	}

    @SuppressWarnings({ "unchecked" })
	public Creator(String configFile){
        configHashMap = new HashMap<>();
        this.controllerManager = new ControllerManager<T,typeId>();
        
        try {
            Scanner scanner = new Scanner(new File(configFile));
            while (scanner.hasNext())
            {
                String[] arr = scanner.next().split("=");
                if(arr[0].equals("pass") && arr.length == 1) {
                    configHashMap.put(arr[0],"");
                    System.out.println( "1 : " +  arr[0]);
                }else {
                    configHashMap.put(arr[0],arr[1]);
                    System.out.println("2 : " + arr[0] +"   " + arr[1]);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        
     
    }

 
    public void init(){

        try {
           /* DatabaseConnection con = */
        	DatabaseConnection.getInstance(this.configHashMap.get("url"), this.configHashMap.get("name"), this.configHashMap.get("pass"), this.configHashMap.get("driver"));
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }


    public ControllerManager<T, typeId> getControllerManager() {
        return controllerManager;
    }

    public void setControllerManager(ControllerManager<T, typeId> controllerManager) {
        this.controllerManager = controllerManager;
    }
}
