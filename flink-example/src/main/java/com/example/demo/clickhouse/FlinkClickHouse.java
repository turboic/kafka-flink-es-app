package com.example.demo.clickhouse;

public class FlinkClickHouse {
    public FlinkClickHouse() {
    }

    private int id;



    private String name;

    private String version;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static FlinkClickHouse getDefault(){
        FlinkClickHouse flinkClickHouse = new FlinkClickHouse();
        flinkClickHouse.setName("kafka->flink-clickhouse");
        flinkClickHouse.setVersion(System.currentTimeMillis()+"");
        return flinkClickHouse;
    }

    public static String convertToCsv(FlinkClickHouse flinkClickHouse) {
        StringBuilder builder = new StringBuilder();
        builder.append("(");

        builder.append(flinkClickHouse.getId());
        builder.append(", ");

        builder.append("'");
        builder.append(flinkClickHouse.getName());
        builder.append("', ");

        builder.append("'");
        builder.append(flinkClickHouse.getVersion());
        builder.append("'");

        builder.append(" )");
        return builder.toString();
    }

    @Override
    public String toString() {
        return "FlinkClickHouse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
