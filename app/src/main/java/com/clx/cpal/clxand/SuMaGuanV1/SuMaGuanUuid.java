package com.clx.cpal.clxand.SuMaGuanV1;

import java.util.UUID;

public class SuMaGuanUuid {

    private static class SuMaGuanHolder {
        private  static SuMaGuanUuid instance = new SuMaGuanUuid();
    }


    private SuMaGuanUuid(){

    }

    public static SuMaGuanUuid getInstance(){
        return SuMaGuanHolder.instance;
    }


    ////////////////////////////////////


    /**
     * 检查字符串是否是合法的uuid,
     *
     * @param uuidStr
     * @return 是返回true,不是返回false
     */
    public static boolean checkIsUuid(String uuidStr) {
        try {
            UUID.fromString(uuidStr).toString();
        } catch (Exception e) {
            //e.printStackTrace();
            return false;
        }
        return true;
    }


    String uuidstr = "";



    boolean setUUID(String uuid){

        boolean res = checkIsUuid(uuid);

        if (res) {
            this.uuidstr = uuid;
        }

        return res;
    }



    String getUUID(){
        return uuidstr;
    }

}
