package com.delong.factory.model.db;
import com.raizlabs.android.dbflow.annotation.Database;

@Database(name = AppDataBase.NAME ,version = AppDataBase.VERSION)
public class AppDataBase {
    public static  final String NAME = "AppDataBase";
    public  static  final int VERSION = 2;
}
