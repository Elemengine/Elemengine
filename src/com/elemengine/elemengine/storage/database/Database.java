package com.elemengine.elemengine.storage.database;

import java.sql.Connection;

public interface Database {

    Connection connect();
    
}
