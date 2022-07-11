package main.school.data.abstractions;

import main.school.data.DataException;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public abstract class JDBCRepository<T extends WithId> extends WithConnection {


    public List<T> queriesForList(String sql, String sql2, List<Object> typesList) throws DataException {
        List<T> items = new ArrayList<>();
        try (
                PreparedStatement statement1 = conn.prepareStatement(sql);
                PreparedStatement statement2 = conn.prepareStatement(sql2);
        ) {
            setPreparedStatement(statement1, typesList);
            try (
                    ResultSet rs1 = statement1.executeQuery();
            ) {
                while (rs1.next()) {
                    List secondTypesList = variableForSecondQuery(rs1);
                    setPreparedStatement(statement2, secondTypesList);
                    List<String> enumList = new ArrayList<>();
                    try(
                            ResultSet rs2 = statement2.executeQuery();
                    ){
                        enumList.clear();
                        while(rs2.next()){
                            enumList.add(rs2.getString("NAME"));
                        }
                        T temp = mapItem(rs1, enumList);
                        items.add(temp);
                    }
                }
            }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return items;
    }

    public void setPreparedStatement(PreparedStatement ps, List<Object> typesToSet) throws DataException {
        int i = 1;
        for (Object o: typesToSet) {
            try {
                ps.setObject(i, o);
                i++;
            } catch (SQLException e) {
                throw new DataException(e.getMessage(), e);
            }
        }
    }

    public Optional<T> queryForObject(String sql, Object... params) throws DataException { //var args
        try(
                PreparedStatement ps = conn.prepareStatement(sql);
                ) {
            for(int i = 0; i < params.length ; i++) {
                ps.setObject(i+1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery();) {
                if(rs.next()) {
                    T entity = mapObject(rs);
                    return Optional.of(entity) ;
                }
                return Optional.empty();
            }
        } catch(SQLException e) {
            throw new DataException(e.getMessage(), e);
        }
    }

    public List<T> queryForObjects(String sql, Object... params) throws DataException {
        List<T> list = new ArrayList<>();
        try(
                PreparedStatement ps = conn.prepareStatement(sql);
        ) {
            for(int i = 0; i < params.length ; i++) {
                ps.setObject(i+1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery();) {
                while(rs.next()) {
                    list.add(mapObject(rs));
                }
                return list;
            }
        } catch(SQLException e) {
            throw new DataException(e.getMessage(), e);
        }
    }

    //public T create(String sql, String sequence, Class c, Object... params) throws DataException {
    public T create(String sql, String sequence, T entity) throws DataException{
        var isr = new IdSequenceRepo();
        isr.setConn(conn);
        try(
                PreparedStatement ps = conn.prepareStatement(sql);

        ) {
            long id = isr.getId(sequence);
            //ps.setObject(1, id);
            entity.setId(id);
            assembleCreateStatement(entity, ps);

            /*
            for (int i = 1; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            */

            ps.executeUpdate();
            return entity;


            //return implementObject(params); //metodologia 1

            //metodologia 2, usa reflection ma è fragile, vulnerabile a modifiche dirette sui costruttori
            /*
            Class[] typesForConstructor = new Class[params.length+1];
            Object[] args = new Object[params.length+1];
            typesForConstructor[0] = long.class;
            args[0] = id;
            for (int i = 0; i < params.length; i++) {
                typesForConstructor[i+1] = params[i].getClass();
                args[i+1] = params[i];
            }
            var constr = c.getConstructor(typesForConstructor);
            return (T) constr.newInstance(args);
        } catch(SQLException | NoSuchMethodException |
                InvocationTargetException | InstantiationException |
                IllegalAccessException e ) {
            throw new DataException(e.getMessage(), e);
        }

             */
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean update(String sql, Object... params) throws DataException {
        try(
                PreparedStatement ps = conn.prepareStatement(sql);
        ) {
            for(int i = 0; i < params.length ; i++) {
                ps.setObject(i+1, params[i]);
            }
            return ps.executeUpdate() > 0;
        } catch(SQLException e) {
            throw new DataException(e.getMessage(), e);
        }
    }

    public boolean delete(String sql, long id) throws DataException {
        try(
                PreparedStatement ps = conn.prepareStatement(sql);
        ) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch(SQLException e) {
            throw new DataException(e.getMessage(), e);
        }
    }

    public abstract void assembleCreateStatement(T entity, PreparedStatement ps);
    //public abstract T implementObject(Object[] params);
    public abstract List<Object> variableForSecondQuery(ResultSet rs) throws DataException;
    public abstract T mapItem(ResultSet rs, List<String> enumList) throws SQLException;
    public abstract T mapObject(ResultSet rs) throws SQLException;

}