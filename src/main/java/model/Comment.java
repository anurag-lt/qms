package model;

import java.sql.Timestamp;

/**
 * Represents a comment associated with a deviation.
 */
public class Comment {

    private int id;
    private String content;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private int deviationsId;

    /**
     * Gets the ID of the comment.
     *
     * @return the comment ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the ID of the comment.
     *
     * @param id the comment ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the content of the comment.
     *
     * @return the comment content
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the content of the comment.
     *
     * @param content the comment content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Gets the timestamp when the comment was created.
     *
     * @return the creation timestamp
     */
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the timestamp when the comment was created.
     *
     * @param createdAt the creation timestamp
     */
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the timestamp when the comment was last updated.
     *
     * @return the last updated timestamp
     */
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the timestamp when the comment was last updated.
     *
     * @param updatedAt the last updated timestamp
     */
    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Gets the ID of the deviation associated with the comment.
     *
     * @return the deviation ID
     */
    public int getDeviationsId() {
        return deviationsId;
    }

    /**
     * Sets the ID of the deviation associated with the comment.
     *
     * @param deviationsId the deviation ID
     */
    public void setDeviationsId(int deviationsId) {
        this.deviationsId = deviationsId;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", deviationsId=" + deviationsId +
                '}';
    }
}