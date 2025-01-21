package cane.brothers.tgbot.game;

import java.sql.JDBCType;
import java.sql.SQLType;

class GuessNumberSQLType implements SQLType {
    @Override
    public String getName() {
        return "Integer[]";
    }

    @Override
    public String getVendor() {
        return "Postgres";
    }

    @Override
    public Integer getVendorTypeNumber() {
        return JDBCType.ARRAY.getVendorTypeNumber();
    }
}
