package main.Server.Model; // ENTITIES ÁNH XẠ CSDL

import org.bson.types.ObjectId;

import java.util.Date;

public class Conservation {
    private ObjectId conservationId;
    private String type;
    private Date created_at;
    private ObjectId created_by;

    public Conservation(String type, Date created_at, ObjectId created_by) {
        this.type = type;
        this.created_at = created_at;
        this.created_by = created_by;
    }

    public ObjectId getConservationId() {
        return conservationId;
    }

    public void setConservationId(ObjectId conservationId) {
        this.conservationId = conservationId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getCreate_at() {
        return created_at;
    }

    public void setCreate_at(Date create_at) {
        this.created_at = create_at;
    }

    public ObjectId getCreate_by() {
        return created_by;
    }

    public void setCreate_by(ObjectId created_by) {
        this.created_by = created_by;
    }
}
