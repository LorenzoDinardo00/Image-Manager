// src/mapper/Mapper.java
package mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface Mapper<T> {
    T fromResultSet(ResultSet rs) throws SQLException;
}