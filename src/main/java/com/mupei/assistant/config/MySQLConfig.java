package com.mupei.assistant.config;

import org.hibernate.dialect.MySQL5Dialect;

public class MySQLConfig extends MySQL5Dialect {
    @Override
    public String getTableTypeString() {
        return " ENGINE=InnoDB DEFAULT CHARSET=utf8";
    }
}