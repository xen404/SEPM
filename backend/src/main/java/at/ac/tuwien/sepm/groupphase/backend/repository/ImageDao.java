package at.ac.tuwien.sepm.groupphase.backend.repository;

import java.time.LocalDateTime;


public interface ImageDao {

    /**
     *
     * @param imageEncodedBase64 a String value which represents encoded in base64 image
     * @param name a String value which defines name of the saved image file
     * @param createdAt a LocalDateTime value which stores time of image creation
     * @return a String value which represents path to the saved image
     */
    String saveImage(String imageEncodedBase64, String name, LocalDateTime createdAt);


    /**
     *
     * @param  path a String value which represents path to the saved image
     * @return a String value which represents encoded in base64 image
     */
    String getImage(String path);
}
