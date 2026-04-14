package model;
public class LeaveRequest {
  public int leaveId;
  public int employeeId;
  public String leaveType;
  public String fromDate;
  public String toDate;
  public long totalDays;
  public String reason;
  public String status;

  public LeaveRequest(int leaveId, int employeeId,String leaveType, String fromDate, String toDate, long totalDays, String reason, String status){
    this.leaveId = leaveId;
    this.employeeId = employeeId;
    this.leaveType = leaveType;
    this.fromDate = fromDate;
    this.toDate = toDate;
    this.totalDays = totalDays;
    this.reason = reason;
    this.status = "Pending";
  }
  
}
