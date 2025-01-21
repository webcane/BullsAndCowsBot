package cane.brothers.tgbot.game;

import java.sql.JDBCType;
import java.sql.SQLType;

class UUIDSQLType implements SQLType {
    @Override
    public String getName() {
        return "uuid";
    }

    @Override
    public String getVendor() {
        return "Postgres";
    }

    @Override
    public Integer getVendorTypeNumber() {
        return JDBCType.OTHER.getVendorTypeNumber();
    }
}
