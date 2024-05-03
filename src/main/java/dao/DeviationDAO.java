package dao;


import model.*;
import java.util.logging.Logger;
import utils.DatabaseUtility;
import java.sql.*;
import java.util.logging.*;
import java.util.*;import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.json.JSONObject;
import java.util.Date;


public class DeviationDAO {


/**
 * Initiate CFT Review: This action allows the user to initiate the CFT review process for a deviation, changing its status from 'department_review_complete' to 'ongoing_cft_review' in the system. This facilitates the formal CFT assessment phase of the deviation management workflow.
 * @param id
 * @param status
 * @param reviewerComments
 * @param reviewDecision
 * @param justificationForReturning
 * @return
 */
public boolean initiateCFTReview(int id, DeviationStatus status, String reviewerComments, boolean reviewDecision, String justificationForReturning) {
    Connection connection = DatabaseUtility.connect();
    try {
        PreparedStatement statement = connection.prepareStatement("UPDATE deviations SET status = ? WHERE id = ?");
        statement.setObject(1, status, Types.OTHER);
        statement.setInt(2, id);
        int rowsUpdated = statement.executeUpdate();

        if (rowsUpdated > 0) {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "CFT review initiated for deviation: " + id);
            return true;
        } else {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Failed to initiate CFT review for deviation: " + id);
            return false;
        }
    } catch (SQLException ex) {
        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error during CFT review initiation", ex);
        return false;
    } finally {
        DatabaseUtility.disconnect(connection);
    }
}

/**
 * Escalates a deviation with the given ID to QA review.
 *
 * @param id The ID of the deviation to escalate.
 * @param status The current status of the deviation.
 * @return true if the escalation was successful, false otherwise.
 * @throws SQLException If a database error occurs.
 */
public boolean escalateToQAReview(int id, Enums.DeviationStatus status) throws SQLException {
    if (status != Enums.DeviationStatus.DEPARTMENT_REVIEW_COMPLETED && status != Enums.DeviationStatus.CFT_REVIEW_COMPLETE) {
        Logger.getLogger(this.getClass().getName()).severe("Invalid deviation status for escalation to QA review: " + status);
        return false;
    }

    String sql = "UPDATE deviations SET status = ? WHERE id = ?";
    Connection connection = DatabaseUtility.connect();
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, Enums.DeviationStatus.PENDING_QA_REVIEW.name());
        statement.setInt(2, id);
        return statement.executeUpdate() > 0;
    } finally {
        DatabaseUtility.disconnect(connection);
    }
}

/**
 * Initiate Department Review: This action allows the user to initiate the department review process for a deviation, changing its status from 'pending_department_review' to 'ongoing_department_review' in the system. This facilitates the formal departmental assessment phase of the deviation management workflow.
 * @param comments
 * @param id
 * @param status
 * @param decision__selection
 * @param justification
 * @return
 */
public boolean initiateDepartmentReview(String comments, int id, Enums.DeviationStatus status, boolean decision__selection, String justification) {
    Connection connection = DatabaseUtility.connect();
    try {
        String sql = "UPDATE deviations SET status = ?, remarks = ? WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, status.name());
        preparedStatement.setString(2, comments);
        preparedStatement.setInt(3, id);
        int rowsAffected = preparedStatement.executeUpdate();
        if (rowsAffected > 0) {
            Deviation deviation = new Deviation();
            deviation.setId(id);
            deviation.setStatus(status);
            deviation.setRemarks(comments);
            // Additional logic for handling decision__selection and justification
            return true;
        } else {
            Logger.getLogger(this.getClass().getName()).severe("Failed to initiate department review for deviation ID: " + id);
            return false;
        }
    } catch (SQLException e) {
        Logger.getLogger(this.getClass().getName()).severe("Error during initiateDepartmentReview: " + e.getMessage());
        return false;
    } finally {
        DatabaseUtility.disconnect(connection);
    }
}

/**
 * Links the outcome of an investigation to a specific deviation, updating the deviation's details with the investigation's findings, risk assessment, and any suggested corrective actions.
 * @param conclusion The overall conclusion of the investigation.
 * @param findings The key findings discovered during the investigation.
 * @param deviations_id The ID of the deviation to which the investigation outcome is linked.
 * @param risk_assessment The updated risk assessment based on the investigation's findings.
 * @return A boolean indicating the success of linking the investigation outcome to the deviation.
 */
public boolean linkInvestigationOutcome(String conclusion, String findings, int deviations_id, String risk_assessment) {
    Connection connection = DatabaseUtility.connect();
    try {
        PreparedStatement statement = connection.prepareStatement("UPDATE deviations SET conclusion = ?, findings = ?, risk_assessment = ? WHERE id = ?");
        statement.setString(1, conclusion);
        statement.setString(2, findings);
        statement.setString(3, risk_assessment);
        statement.setInt(4, deviations_id);
        int rowsUpdated = statement.executeUpdate();
        return rowsUpdated > 0;
    } catch (SQLException e) {
        Logger.getLogger(this.getClass().getName()).severe("Error linking investigation outcome: " + e.getMessage());
        return false;
    } finally {
        DatabaseUtility.disconnect(connection);
    }
}

/**
 * Schedules a reminder for upcoming reviews or actions that need to be taken on a deviation.
 */
public void setReviewReminder() {
    try (Connection connection = DatabaseUtility.connect()) {
        // Logic to fetch deviations with upcoming reviews
        // ...

        // Schedule reminders for each deviation using ScheduledExecutorService
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        for (Deviation deviation : deviationsWithUpcomingReviews) {
            LocalDateTime reviewDateTime = getReviewDateTime(deviation);
            long delay = reviewDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - System.currentTimeMillis();

            scheduler.schedule(() -> {
                // Send notification or perform other reminder actions
                // ...
            }, delay, TimeUnit.MILLISECONDS);
        }
    } catch (SQLException e) {
        Logger.getLogger(this.getClass().getName()).severe("Error setting review reminders: " + e.getMessage());
    }
}


/**
 * Completes the department review of a deviation, transitioning its status based on the reviewer's decision.
 * The possible resulting statuses are:
 * - 'complete_department_review': Advances the deviation to the next review stage.
 * - 'deviation_returned': Returns the deviation to the initiator for re-initiation.
 * - 'deviation_dropped': Drops the deviation, requiring justification.
 * Additionally, captures the reviewer's comments.
 *
 * @param deviationId The ID of the deviation undergoing review.
 * @param newStatus The new status of the deviation ('complete_department_review', 'deviation_returned', or 'deviation_dropped').
 * @param reviewerComments Comments provided by the reviewer regarding the deviation.
 * @return True if the operation succeeds, False otherwise.
 */
public boolean completeDepartmentReview(int deviationId, Enums.DeviationStatus newStatus, String reviewerComments) {
    Connection connection = DatabaseUtility.connect();
    try {
        String sql = "UPDATE deviations SET status = ? WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, newStatus.name());
        statement.setInt(2, deviationId);
        int rowsUpdated = statement.executeUpdate();

        if (rowsUpdated > 0) {
            // Add reviewer comments to deviation_remarks
            sql = "INSERT INTO deviation_remarks (deviations_id, content, created_at) VALUES (?, ?, ?)";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, deviationId);
            statement.setString(2, reviewerComments);
            statement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now(ZoneId.of("UTC"))));
            statement.executeUpdate();
            return true;
        }
    } catch (SQLException ex) {
        Logger.getLogger(this.getClass().getName()).severe("Error completing department review: " + ex.getMessage());
    } finally {
        DatabaseUtility.disconnect(connection);
    }
    return false;
}


/**
 * Assigns a CFT reviewer to a deviation and updates the deviation's status.
 *
 * @param userId The ID of the user performing the action.
 * @param deviationId The ID of the deviation.
 * @param departmentId The ID of the selected CFT department.
 * @param crossFunctionalAssessmentRequired Whether a CFT assessment is required.
 * @param cftDepartmentSelection The ID of the selected CFT department.
 * @param userSelection The ID of the selected CFT reviewer.
 * @return A boolean indicating whether the operation was successful.
 * @throws SQLException If a database error occurs.
 */
public boolean assignCFTReviewer(int userId, int deviationId, int departmentId, boolean crossFunctionalAssessmentRequired, int cftDepartmentSelection, int userSelection) throws SQLException {
    Connection connection = DatabaseUtility.connect();
    try {
        connection.setAutoCommit(false);
        String updateDeviationSql = "UPDATE deviations SET status = ? WHERE id = ?";
        PreparedStatement updateDeviationStmt = connection.prepareStatement(updateDeviationSql);
        if (crossFunctionalAssessmentRequired) {
            updateDeviationStmt.setString(1, Enums.DeviationStatus.PENDING_CFT_REVIEW.name());
        } else {
            updateDeviationStmt.setString(1, Enums.DeviationStatus.DEPARTMENT_REVIEW_COMPLETED.name());
        }
        updateDeviationStmt.setInt(2, deviationId);
        int rowsUpdated = updateDeviationStmt.executeUpdate();

        if (crossFunctionalAssessmentRequired) {
            String insertCFTReviewSql = "INSERT INTO cft_reviews (deviation_id, department_id, reviewer_id) VALUES (?, ?, ?)";
            PreparedStatement insertCFTReviewStmt = connection.prepareStatement(insertCFTReviewSql);
            insertCFTReviewStmt.setInt(1, deviationId);
            insertCFTReviewStmt.setInt(2, cftDepartmentSelection);
            insertCFTReviewStmt.setInt(3, userSelection);
            insertCFTReviewStmt.executeUpdate();
        }

        connection.commit();
        return rowsUpdated > 0;
    } catch (SQLException e) {
        connection.rollback();
        Logger.getLogger(this.getClass().getName()).severe("Error assigning CFT reviewer: " + e.getMessage());
        throw e;
    } finally {
        DatabaseUtility.disconnect(connection);
    }
}

/**
 * Retry Department Review: Enables the record to be sent back to the department review phase after adjustments or additional information has been added following a CFT or QA review. This action ensures that deviations deemed incompletely reviewed or requiring more details can be re-evaluated at the department level.
 * @param status
 * @param timestamp
 * @param remarks
 * @param deviations_id
 * @return
 */
public boolean retryDepartmentReview(Enums.DeviationStatus status, Timestamp timestamp, String remarks, int deviations_id) {
    boolean isSuccessful = false;
    Connection connection = DatabaseUtility.connect();
    try {
        String query = "UPDATE deviations SET status = ?, remarks = ? WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setObject(1, status);
        statement.setString(2, remarks);
        statement.setInt(3, deviations_id);
        int rowsAffected = statement.executeUpdate();
        if (rowsAffected > 0) {
            isSuccessful = true;
            DeviationHistory history = new DeviationHistory();
            history.setDeviations_id(deviations_id);
            history.setTimestamp(timestamp);
            history.setDescription("Deviation sent back to department review.");
            history.setAction_type(Enums.ActionType.UPDATE);
            addDeviationHistory(history);
        }
    } catch (SQLException e) {
        Logger.getLogger(this.getClass().getName()).severe("Error during retryDepartmentReview: " + e.getMessage());
    } finally {
        DatabaseUtility.disconnect(connection);
    }
    return isSuccessful;
}

/**
 * Generate Compliance Report: Compiles and generates reports detailing the deviation handling process including all related reviews, approvals, and corrective actions for compliance and audit purposes. Facilitates adherence to industry regulations and internal standards.
 * @param approval_date
 * @param end_date
 * @param deviations_id
 * @param completion_date
 * @param start_date
 * @return JSONObject
 */
public JSONObject generateComplianceReport(Date approval_date, Date end_date, int deviations_id, Date completion_date, Date start_date) throws SQLException {
    Connection connection = DatabaseUtility.connect();
    JSONObject response = new JSONObject();
    try {
        // Implement your logic here
        // Use prepared statements for database operations
        // Be sure to handle exceptions and close resources
        // Example:
        // String query = "SELECT * FROM approvals WHERE approval_date = ?";
        // PreparedStatement statement = connection.prepareStatement(query);
        // statement.setDate(1, new java.sql.Date(approval_date.getTime()));
        // ResultSet result = statement.executeQuery();
        // ...
    } catch (SQLException e) {
        Logger.getLogger(this.getClass().getName()).severe("Error generating compliance report: " + e.getMessage());
        response.put("error", e.getMessage());
    } finally {
        DatabaseUtility.disconnect(connection);
    }
    return response;
}

/**
 * Captures the outcome of an investigation, updating the related deviation record with the investigation's findings, risk assessment, and remediation actions.
 * @param remediationActionTaken The actions taken to address the deviation based on the investigation.
 * @param riskAssessment The updated risk assessment after the investigation.
 * @param investigationId The ID of the investigation associated with the deviation.
 * @return A boolean indicating whether the update was successful.
 */
public boolean captureInvestigationOutcome(String remediationActionTaken, String riskAssessment, int investigationId) {
    boolean isUpdated = false;
    Connection connection = DatabaseUtility.connect();
    try (PreparedStatement statement = connection.prepareStatement("UPDATE deviations SET risk_assessment = ?, remarks = ? WHERE id = (SELECT deviations_id FROM investigations WHERE id = ?)")) {
        statement.setString(1, riskAssessment);
        statement.setString(2, remediationActionTaken);
        statement.setInt(3, investigationId);
        int rowsUpdated = statement.executeUpdate();
        isUpdated = rowsUpdated > 0;
    } catch (SQLException ex) {
        Logger.getLogger(this.getClass().getName()).severe("Error updating deviation with investigation outcome: " + ex.getMessage());
    } finally {
        DatabaseUtility.disconnect(connection);
    }
    return isUpdated;
}


/**
 * This method records approvals during different stages of the deviation handling process, including department-level, CFT review, and QA closures. It inserts a new record in the 'approvals' table linked to the deviation.
 * @param approval_date Date of approval
 * @param approval_status Status of the approval (approved, rejected, pending)
 * @param approver ID of the approver
 * @param approver_role Role of the approver
 * @param approval_comments Comments provided by the approver
 * @param approver_name Name of the approver
 * @param deviations_id ID of the deviation being approved
 * @return The ID of the newly created approval record or -1 if an error occurs
 */
public int recordApproval(Date approval_date, Enums.ApprovalStatus approval_status, int approver, String approver_role, String approval_comments, String approver_name, int deviations_id) {
    int approvalId = -1;
    String sql = "INSERT INTO approvals (approval_date, approval_status, approver, approver_role, approval_comments, approver_name, deviations_id) VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";

    try (Connection connection = DatabaseUtility.connect();
         PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setDate(1, new java.sql.Date(approval_date.getTime()));
        statement.setObject(2, approval_status, Types.OTHER);
        statement.setInt(3, approver);
        statement.setString(4, approver_role);
        statement.setString(5, approval_comments);
        statement.setString(6, approver_name);
        statement.setInt(7, deviations_id);

        try (ResultSet rs = statement.executeQuery()) {
            if (rs.next()) {
                approvalId = rs.getInt("id");
            }
        }
    } catch (SQLException ex) {
        Logger.getLogger(this.getClass().getName()).severe("Error recording approval: " + ex.getMessage());
    }

    return approvalId;
}


/**
 * Creates a new deviation record in the database.
 *
 * @param timeOfIdentification The time the deviation was identified.
 * @param dateOfOccurrence The date the deviation occurred.
 * @param description A description of the deviation.
 * @param riskAssessment The risk assessment for the deviation.
 * @param standardProcedure The standard procedure that was deviated from.
 * @param remarks Any additional remarks about the deviation.
 * @param deviationType The type of deviation.
 * @param productSelection The ID of the product involved in the deviation (if applicable).
 * @param material The ID of the material involved in the deviation (if applicable).
 * @param equipment The ID of the equipment involved in the deviation (if applicable).
 * @param batchId The ID of the batch involved in the deviation (if applicable).
 * @param materialLotNumber The lot number of the material involved in the deviation (if applicable).
 * @param reasonOrRootCauseForDeviation The reason or root cause for the deviation.
 * @param impactOnBatchesInvolved Whether the deviation impacted any batches.
 * @param immediateActions Any immediate actions that were taken.
 * @param justificationForDelay Justification for any delay in reporting the deviation.
 * @return The ID of the newly created deviation record.
 * @throws SQLException If there is an error creating the deviation record.
 */
public int createDeviation(Timestamp timeOfIdentification, Date dateOfOccurrence, String description, String riskAssessment, String standardProcedure, String remarks, Enums.DeviationType deviationType, int productSelection, int material, int equipment, int batchId, String materialLotNumber, String reasonOrRootCauseForDeviation, boolean impactOnBatchesInvolved, String immediateActions, String justificationForDelay) throws SQLException {
    Connection connection = DatabaseUtility.connect();
    try {
        String sql = "INSERT INTO deviations (time_of_identification, date_of_occurrence, description, risk_assessment, standard_procedure, remarks, deviation_type, product_id, material_id, equipment_id, batch_id, material_lot_number, reason_or_root_cause_for_deviation, impact_on_batches_involved, immediate_actions, justification_for_delay) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        statement.setTimestamp(1, timeOfIdentification);
        statement.setDate(2, new java.sql.Date(dateOfOccurrence.getTime()));
        statement.setString(3, description);
        statement.setString(4, riskAssessment);
        statement.setString(5, standardProcedure);
        statement.setString(6, remarks);
        statement.setString(7, deviationType.name());
        statement.setInt(8, productSelection);
        statement.setInt(9, material);
        statement.setInt(10, equipment);
        statement.setInt(11, batchId);
        statement.setString(12, materialLotNumber);
        statement.setString(13, reasonOrRootCauseForDeviation);
        statement.setBoolean(14, impactOnBatchesInvolved);
        statement.setString(15, immediateActions);
        statement.setString(16, justificationForDelay);

        int rowsInserted = statement.executeUpdate();
        if (rowsInserted > 0) {
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            } else {
                throw new SQLException("Creating deviation failed, no ID obtained.");
            }
        } else {
            throw new SQLException("Creating deviation failed, no rows affected.");
        }
    } finally {
        DatabaseUtility.disconnect(connection);
    }
}

/**
 * This method updates a deviation record in the 'deviations' table with the provided details.
 * @param id the unique identifier of the deviation record to update
 * @param description the updated description of the deviation
 * @param riskAssessment the updated risk assessment of the deviation
 * @param standardProcedure the updated standard procedure associated with the deviation
 * @param remediationActionTaken the updated remediation action taken for the deviation
 * @return a boolean indicating whether the update was successful or not
 */
public boolean updateDeviationDetails(int id, String description, String riskAssessment, String standardProcedure, String remediationActionTaken) {
    boolean isUpdated = false;
    String sql = "UPDATE deviations SET description = ?, risk_assessment = ?, standard_procedure = ?, remarks = ? WHERE id = ?";

    try (Connection connection = DatabaseUtility.connect();
            PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, description);
        statement.setString(2, riskAssessment);
        statement.setString(3, standardProcedure);
        statement.setString(4, remediationActionTaken);
        statement.setInt(5, id);

        int rowsAffected = statement.executeUpdate();
        isUpdated = rowsAffected > 0;
    } catch (SQLException ex) {
        Logger.getLogger(this.getClass().getName()).severe("Error updating deviation details: " + ex.getMessage());
    }

    return isUpdated;
}


/**
 * This method is used to close a deviation by setting its status to 'closed'.
 * @param status The new status to set for the deviation (should be 'closed')
 * @param id The ID of the deviation to close
 * @return boolean True if the update was successful, false otherwise
 */
public boolean closeDeviation(Enums.DeviationHandlingStatus status, int id) {
    boolean isUpdated = false;
    String sql = "UPDATE deviations SET status = ? WHERE id = ?";
    try (Connection connection = DatabaseUtility.connect();
            PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, status.name());
        statement.setInt(2, id);
        int rowsAffected = statement.executeUpdate();
        if (rowsAffected > 0) {
            isUpdated = true;
        }
    } catch (SQLException ex) {
        Logger.getLogger(this.getClass().getName()).severe("Error while closing deviation: " + ex.getMessage());
    }
    return isUpdated;
}

/**
 * Updates a deviation record with details specific to the 'Review by Closer Department' stage.
 *
 * @param deviationNumber The unique identifier of the deviation.
 * @param status          The updated status of the deviation.
 * @param description     A description of the review findings or actions taken.
 * @param riskAssessment  An updated risk assessment based on the review.
 * @param remarks         Additional comments or observations from the closer department.
 * @return A boolean indicating whether the update was successful.
 * @throws SQLException If a database error occurs during the update process.
 */
public boolean reviewByCloserDepartment(String deviationNumber, Enums.DeviationStatus status, String description, String riskAssessment, String remarks) throws SQLException {
    Connection connection = DatabaseUtility.connect();
    try {
        String sql = "UPDATE deviations SET status = ?, description = ?, risk_assessment = ?, remarks = ? WHERE deviation_number = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, status.name());
        statement.setString(2, description);
        statement.setString(3, riskAssessment);
        statement.setString(4, remarks);
        statement.setString(5, deviationNumber);

        int rowsUpdated = statement.executeUpdate();
        return rowsUpdated > 0;
    } finally {
        DatabaseUtility.disconnect(connection);
    }
}
}
