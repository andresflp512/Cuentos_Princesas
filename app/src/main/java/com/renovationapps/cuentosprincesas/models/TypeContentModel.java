package com.renovationapps.cuentosprincesas.models;

public class TypeContentModel {
    private String content_type_id;
    private String name_content_type;

    public TypeContentModel(String content_type_id, String name_content_type) {
        this.content_type_id = content_type_id;
        this.name_content_type = name_content_type;
    }

    public String getContent_type_id() {
        return content_type_id;
    }

    public String getName_content_type() {
        return name_content_type;
    }

    public void setContent_type_id(String content_type_id) {
        this.content_type_id = content_type_id;
    }

    public void setName_content_type(String name_content_type) {
        this.name_content_type = name_content_type;
    }

    @Override
    public String toString() {
        return "TypeContentModel{" +
                "content_type_id='" + content_type_id + '\'' +
                ", name_content_type='" + name_content_type + '\'' +
                '}';
    }
}
