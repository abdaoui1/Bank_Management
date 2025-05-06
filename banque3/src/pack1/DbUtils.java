package pack1;
import java.sql.ResultSet;
import java.sql.SQLException;

class DbUtils {
    public static javax.swing.table.TableModel resultSetToTableModel(ResultSet rs) throws SQLException {
        java.sql.ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        
        String[] columnNames = new String[columnCount];
        for (int i = 1; i <= columnCount; i++) {
            columnNames[i-1] = metaData.getColumnName(i);
        }
        
        java.util.List<Object[]> data = new java.util.ArrayList<>();
        while (rs.next()) {
            Object[] row = new Object[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                row[i-1] = rs.getObject(i);
            }
            data.add(row);
        }
        
        return new javax.swing.table.DefaultTableModel(
            data.toArray(new Object[0][]), columnNames) {
               
                private static final long serialVersionUID = 9045759079655855549L;

                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
    }
}
