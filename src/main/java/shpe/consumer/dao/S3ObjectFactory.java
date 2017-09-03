package shpe.consumer.dao;

import org.jets3t.service.model.S3Object;

public class S3ObjectFactory {

    public S3Object create(String filename, String jsonObject) {
        try {
            return new S3Object(filename, jsonObject);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Failed while creating S3Object with filename: %s", filename), e);
        }
    }
}
