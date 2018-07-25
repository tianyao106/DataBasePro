package com.winfo.gdmsaec.app.databasepro.bean;

import com.winfo.gdmsaec.app.databasepro.annotations.DbField;
import com.winfo.gdmsaec.app.databasepro.annotations.DbTable;

@DbTable("tb_user")
public class User {

    @DbField("_id")
    private Integer id;

    @DbField("name")
    private String name;

    @DbField("pwd")
    private String password;

    public User(Integer id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }
}
