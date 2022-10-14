package com.yer.library.model.customtypes;

import com.yer.library.model.Location;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;

public class LocationType implements UserType {
    @Override
    public int[] sqlTypes() {
        return new int[]{Types.SMALLINT, Types.SMALLINT, Types.SMALLINT};
    }

    @Override
    public Class returnedClass() {
        return Location.class;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        return Objects.equals(x, y);
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        assert x != null;
        return x.hashCode();
    }

    @Override
    public Object nullSafeGet(ResultSet resultSet, String[] names, SharedSessionContractImplementor session, Object owner) throws HibernateException, SQLException {
        if (resultSet.wasNull()) {
            return null;
        }
        Short floor = resultSet.getShort(names[0]);
        Short bookcase = resultSet.getShort(names[1]);
        Short plank = resultSet.getShort(names[2]);

        return new Location(floor, bookcase, plank);
    }

    @Override
    public void nullSafeSet(PreparedStatement preparedStatement, Object value, int index, SharedSessionContractImplementor session) throws HibernateException, SQLException {
        if (Objects.isNull(value)) {
            preparedStatement.setNull(index, Types.SMALLINT);
            preparedStatement.setNull(index + 1, Types.SMALLINT);
            preparedStatement.setNull(index + 2, Types.SMALLINT);
        } else {
            Location location = (Location) value;
            preparedStatement.setInt(index, location.getFloor());
            preparedStatement.setInt(index + 1, location.getBookcase());
            preparedStatement.setInt(index + 2, location.getShelve());
        }
    }

    private static Location nullSafeToLocation(Object value) {
        if (value != null) {
            Location location = (Location) value;
            return new Location(location.getFloor(), location.getBookcase(), location.getShelve());
        }
        return null;
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return nullSafeToLocation(value);
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) deepCopy(value);
    }

    @Override
    public Object assemble(Serializable serializable, Object cached) throws HibernateException {
        return deepCopy(cached);
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return deepCopy(original);
    }
}
