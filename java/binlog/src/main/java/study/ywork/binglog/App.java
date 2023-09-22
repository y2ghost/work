package study.ywork.binglog;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.alibaba.fastjson2.JSON;
import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.BinaryLogClient.EventListener;
import com.github.shyiko.mysql.binlog.BinaryLogFileReader;
import com.github.shyiko.mysql.binlog.event.DeleteRowsEventData;
import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.EventData;
import com.github.shyiko.mysql.binlog.event.EventType;
import com.github.shyiko.mysql.binlog.event.TableMapEventData;
import com.github.shyiko.mysql.binlog.event.UpdateRowsEventData;
import com.github.shyiko.mysql.binlog.event.WriteRowsEventData;
import com.github.shyiko.mysql.binlog.event.deserialization.EventDeserializer;
import com.github.shyiko.mysql.binlog.event.deserialization.ByteArrayEventDataDeserializer;
import com.github.shyiko.mysql.binlog.event.deserialization.NullEventDataDeserializer;
import com.github.shyiko.mysql.binlog.jmx.BinaryLogClientStatistics;
import com.github.shyiko.mysql.binlog.network.SSLMode;

/**
 * 获取列名的SQL语句 select TABLE_SCHEMA, TABLE_NAME, COLUMN_NAME, ORDINAL_POSITION,
 * COLUMN_DEFAULT, IS_NULLABLE, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH,
 * CHARACTER_OCTET_LENGTH, NUMERIC_PRECISION, NUMERIC_SCALE, CHARACTER_SET_NAME,
 * COLLATION_NAME from INFORMATION_SCHEMA.COLUMNS;
 */
public class App {
    // 监听的数据库表
    private static List<String> LISTEN_TABLE_LIST = Arrays.asList("user", "role");
    // 维护表
    private static Map<Long, String> TABLE_MAP = new HashMap<>();

    public static void main(String[] args) {
        // readBinlogFile("./binlog.data");
        // readBinlogServer();
        // getColumnNames();
        listeningExample();
    }

    public static void readBinlogFile(String fileName) {
        File binlogFile = new File(fileName);
        EventDeserializer eventDeserializer = new EventDeserializer();
        eventDeserializer.setCompatibilityMode(EventDeserializer.CompatibilityMode.DATE_AND_TIME_AS_LONG,
                EventDeserializer.CompatibilityMode.CHAR_AND_BINARY_AS_BYTE_ARRAY);

        BinaryLogFileReader reader;
        try {
            reader = new BinaryLogFileReader(binlogFile, eventDeserializer);
            for (Event event; (event = reader.readEvent()) != null;) {
                System.out.println(event.toString());
            }

            reader.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void readBinlogServer() {
        BinaryLogClient client = new BinaryLogClient("localhost", 3306, "root", "rootroot");
        EventDeserializer eventDeserializer = new EventDeserializer();
        eventDeserializer.setCompatibilityMode(EventDeserializer.CompatibilityMode.DATE_AND_TIME_AS_LONG,
                EventDeserializer.CompatibilityMode.CHAR_AND_BINARY_AS_BYTE_ARRAY);
        // 可以自定义反序列化的处理类
        eventDeserializer.setEventDataDeserializer(EventType.EXT_DELETE_ROWS, new ByteArrayEventDataDeserializer());
        eventDeserializer.setEventDataDeserializer(EventType.EXT_WRITE_ROWS, new NullEventDataDeserializer());
        client.setEventDeserializer(eventDeserializer);

        client.registerEventListener(new EventListener() {
            @Override
            public void onEvent(Event event) {
                System.out.println(event.toString());
            }
        });

        try {
            MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
            ObjectName objectName = new ObjectName("mysql.binlog:type=BinaryLogClient");
            mBeanServer.registerMBean(client, objectName);
            BinaryLogClientStatistics stats = new BinaryLogClientStatistics(client);
            ObjectName statsObjectName = new ObjectName("mysql.binlog:type=BinaryLogClientStatistics");
            mBeanServer.registerMBean(stats, statsObjectName);
            client.connect();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    // 示例如何SSL连接
    public static void readBinlogSSLServer() {
        System.setProperty("javax.net.ssl.trustStore", "/path/to/truststore.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "truststore.password");
        System.setProperty("javax.net.ssl.keyStore", "/path/to/keystore.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "keystore.password");
        BinaryLogClient client = new BinaryLogClient("localhost", 3306, "root", "rootroot");
        client.setSSLMode(SSLMode.VERIFY_IDENTITY);
    }

    // 获取数据表字段名称
    public static void getColumnNames() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            String url = "jdbc:mariadb://localhost:3306/testdb";
            Connection connection = DriverManager.getConnection(url, "root", "rootroot");
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tableResultSet = metaData.getTables(null, "public", null, new String[] { "TABLE" });

            while (tableResultSet.next()) {
                String tableName = tableResultSet.getString("TABLE_NAME");
                ResultSet columnResultSet = metaData.getColumns(null, "public", tableName, null);

                while (columnResultSet.next()) {
                    String columnName = columnResultSet.getString("COLUMN_NAME");
                    System.out.println(String.format("%s: %s", tableName, columnName));
                }

                columnResultSet.close();
            }

            tableResultSet.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static void listeningExample() {
        BinaryLogClient client = new BinaryLogClient("localhost", 3306, "root", "rootroot");
        client.registerEventListener(App::fireEvent);

        try {
            client.connect();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private static void fireEvent(Event event) {
        EventData data = event.getData();
        if (null == data) {
            return;
        }

        if (data instanceof TableMapEventData) {
            TableMapEventData tableMapEventData = (TableMapEventData) data;
            TABLE_MAP.put(tableMapEventData.getTableId(), tableMapEventData.getTable());
        }

        if (data instanceof WriteRowsEventData) {
            WriteRowsEventData writeRowsEventData = (WriteRowsEventData) data;
            String tableName = TABLE_MAP.get(writeRowsEventData.getTableId());
            if (tableName != null && LISTEN_TABLE_LIST.contains(tableName)) {
                for (Serializable[] row : writeRowsEventData.getRows()) {
                    System.out.println(JSON.toJSONString(row));
                }
            }
        } else if (data instanceof DeleteRowsEventData) {
            DeleteRowsEventData deleteRowsEventData = (DeleteRowsEventData) data;
            String tableName = TABLE_MAP.get(deleteRowsEventData.getTableId());
            if (tableName != null && LISTEN_TABLE_LIST.contains(tableName)) {
                for (Serializable[] row : deleteRowsEventData.getRows()) {
                    System.out.println(JSON.toJSONString(row));
                }
            }
        } else if (data instanceof UpdateRowsEventData) {
            UpdateRowsEventData updateRowsEventData = (UpdateRowsEventData) data;
            String tableName = TABLE_MAP.get(updateRowsEventData.getTableId());
            if (tableName != null && LISTEN_TABLE_LIST.contains(tableName)) {
                for (Map.Entry<Serializable[], Serializable[]> row : updateRowsEventData.getRows()) {
                    // 修改之前的数据
                    System.out.println(JSON.toJSONString(row.getKey()));
                    // 修改之后的数据
                    System.out.println(JSON.toJSONString(row.getValue()));
                }
            }
        }
    }
}
