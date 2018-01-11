package com.sanganan.app.model;

import java.util.ArrayList;

/**
 * Created by root on 23/9/16.
 */
public class ComplainData {

    String ID;
    String RWAID;
    String CategoryID;
    String RWAResidentFlatID;
    String FlatNbr;
    String ComplaintDetails;
    String IsCommonAreaComplaint;
    String Image1;
    String Image2;
    String Image3;
    String Status;
    String DateCreated;
    String AssignedTo;
    String DateAssigned;
    String AssignedBy;
    String ComplainBy;
    String ResolvedOn;
    String ResolutionNote;
    String ResolutionAcknowledgedOn;
    String AcknowledgementNote;
    String Description;
    String Rating;

    ArrayList<Remark> remarks;

    public ArrayList<Remark> getRemarks() {
        return remarks;
    }

    public void setRemarks(ArrayList<Remark> remarks) {
        this.remarks = remarks;
    }




    public String getComplainByID() {
        return ComplainByID;
    }

    public void setComplainByID(String complainByID) {
        ComplainByID = complainByID;
    }

    String ComplainByID;

    public String getAssignToNumber() {
        return AssignToNumber;
    }

    public void setAssignToNumber(String assignToNumber) {
        AssignToNumber = assignToNumber;
    }

    String AssignToNumber;

    public String getHelperNumber() {
        return helperNumber;
    }

    public void setHelperNumber(String helperNumber) {
        this.helperNumber = helperNumber;
    }

    String helperNumber;

    public String getRating() {
        return Rating;
    }

    public void setRating(String rating) {
        Rating = rating;
    }


    public String getFlatNbr() {
        return FlatNbr;
    }

    public void setFlatNbr(String flatNbr) {
        FlatNbr = flatNbr;
    }

    public String getComplainBy() {
        return ComplainBy;
    }

    public void setComplainBy(String complainBy) {
        ComplainBy = complainBy;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getRWAID() {
        return RWAID;
    }

    public void setRWAID(String RWAID) {
        this.RWAID = RWAID;
    }

    public String getCategoryID() {
        return CategoryID;
    }

    public void setCategoryID(String categoryID) {
        CategoryID = categoryID;
    }

    public String getRWAResidentFlatID() {
        return RWAResidentFlatID;
    }

    public void setRWAResidentFlatID(String RWAResidentFlatID) {
        this.RWAResidentFlatID = RWAResidentFlatID;
    }

    public String getComplaintDetails() {
        return ComplaintDetails;
    }

    public void setComplaintDetails(String complaintDetails) {
        ComplaintDetails = complaintDetails;
    }

    public String getIsCommonAreaComplaint() {
        return IsCommonAreaComplaint;
    }

    public void setIsCommonAreaComplaint(String isCommonAreaComplaint) {
        IsCommonAreaComplaint = isCommonAreaComplaint;
    }

    public String getImage1() {
        return Image1;
    }

    public void setImage1(String image1) {
        Image1 = image1;
    }

    public String getImage2() {
        return Image2;
    }

    public void setImage2(String image2) {
        Image2 = image2;
    }

    public String getImage3() {
        return Image3;
    }

    public void setImage3(String image3) {
        Image3 = image3;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getDateCreated() {
        return DateCreated;
    }

    public void setDateCreated(String dateCreated) {
        DateCreated = dateCreated;
    }

    public String getAssignedTo() {
        return AssignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        AssignedTo = assignedTo;
    }

    public String getDateAssigned() {
        return DateAssigned;
    }

    public void setDateAssigned(String dateAssigned) {
        DateAssigned = dateAssigned;
    }

    public String getAssignedBy() {
        return AssignedBy;
    }

    public void setAssignedBy(String assignedBy) {
        AssignedBy = assignedBy;
    }

    public String getResolvedOn() {
        return ResolvedOn;
    }

    public void setResolvedOn(String resolvedOn) {
        ResolvedOn = resolvedOn;
    }

    public String getResolutionNote() {
        return ResolutionNote;
    }

    public void setResolutionNote(String resolutionNote) {
        ResolutionNote = resolutionNote;
    }

    public String getResolutionAcknowledgedOn() {
        return ResolutionAcknowledgedOn;
    }

    public void setResolutionAcknowledgedOn(String resolutionAcknowledgedOn) {
        ResolutionAcknowledgedOn = resolutionAcknowledgedOn;
    }

    public String getAcknowledgementNote() {
        return AcknowledgementNote;
    }

    public void setAcknowledgementNote(String acknowledgementNote) {
        AcknowledgementNote = acknowledgementNote;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }


}
