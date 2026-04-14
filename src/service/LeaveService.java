package service;
import model.Employee;
import model.LeaveRequest;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import main.DatabaseConnection;
import java.sql.ResultSet;

public class LeaveService {

    public ArrayList<Employee> employees = new ArrayList<>();
    public ArrayList<LeaveRequest> leaveRequests = new ArrayList<>();

    public void registerEmployee(Employee emp) {
        employees.add(emp);
    }
    public void applyLeave(LeaveRequest leave) {
        leaveRequests.add(leave);
    }

    public void viewMyLeaves(Employee emp){
    boolean found = false;
    for(LeaveRequest leave : leaveRequests){
      if(leave.employeeId == emp.id){
        found = true;
        System.out.println("Leave ID: " + leave.leaveId);
        System.out.println("Leave Type: "+ leave.leaveType);
        System.out.println("From Date: "+ leave.fromDate);
        System.out.println("To Date: "+ leave.toDate);
        System.out.println("Total Leave Days: " + leave.totalDays);
        System.out.println("Reason: " + leave.reason);
        System.out.println("Status: " + leave.status);
      }
    }
    if(!found){
      System.out.println("No Leave Requests Found.");
    }
  }
  public String getEmployeeNameById(int id) {

    for (Employee emp : employees) {
        if (emp.id == id) {
            return emp.name;
        }
    }
    return "Unknown";
}
  public void viewAllLeaves(){
    if(leaveRequests.isEmpty()){
      System.out.println("No Leave Requests Found.");
      return;
    }
    for(LeaveRequest leave : leaveRequests){
      System.out.println("-------------------");
      System.out.println("Leave Id: "+ leave.leaveId);
      System.out.println("Employee ID: "+ leave.employeeId);
      System.out.println("Employee Name: "+ getEmployeeNameById(leave.employeeId));
      System.out.println("Leave Type: "+ leave.leaveType);
      System.out.println("From Date: "+ leave.fromDate);
      System.out.println("To Date: "+ leave.toDate);
      System.out.println("Total Leave Days: " + leave.totalDays);
      System.out.println("Reason: "+ leave.reason);
      System.out.println("Status: "+ leave.status);
    }
  }

  public void updateLeaveStatus(int leaveId, String status) {

    for (LeaveRequest leave : leaveRequests) {

        if (leave.leaveId == leaveId) {

            leave.status = status;
            System.out.println("Leave status updated successfully!");
            return;
        }
    }

    System.out.println("Leave ID not found.");
}
public void saveEmployeeToDB(Employee emp) {

    try {
        Connection conn = DatabaseConnection.getConnection();

        String query = "INSERT INTO employees(name, email, password, role, total_leaves, used_leaves) VALUES (?, ?, ?, ?, ?, ?)";

        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, emp.name);
        ps.setString(2, emp.email);
        ps.setString(3, emp.password);
        ps.setString(4, emp.role);
        ps.setInt(5, emp.totalLeaves);
        ps.setInt(6, emp.usedLeaves);

        ps.executeUpdate();

        System.out.println("Employee saved to database!");

    } catch (Exception e) {
        e.printStackTrace();
    }
}
public Employee loginFromDB(String email, String password) {

    try {
        Connection conn = DatabaseConnection.getConnection();

        String query = "SELECT * FROM employees WHERE email=? AND password=?";

        PreparedStatement ps = conn.prepareStatement(query);

        ps.setString(1, email);
        ps.setString(2, password);

        var rs = ps.executeQuery();

        if (rs.next()) {

            return new Employee(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("password"),
                rs.getString("role")
            );
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return null;
}

public void viewEmployeesFromDB() {

    try {
        Connection conn = DatabaseConnection.getConnection();

        String query = "SELECT * FROM employees";

        PreparedStatement ps = conn.prepareStatement(query);

        ResultSet rs = ps.executeQuery();

        boolean found = false;

        while (rs.next()) {

            found = true;

            System.out.println("----------------------");
            System.out.println("ID: " + rs.getInt("id"));
            System.out.println("Name: " + rs.getString("name"));
            System.out.println("Email: " + rs.getString("email"));
            System.out.println("Role: " + rs.getString("role"));
        }

        if (!found) {
            System.out.println("No Employees Registered!");
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
}

public void saveLeaveToDB(LeaveRequest leave) {

    try {
        Connection conn = DatabaseConnection.getConnection();

        String query = "INSERT INTO leaves(employee_id, leave_type, from_date, to_date, total_days, reason, status) VALUES (?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement ps = conn.prepareStatement(query);

        ps.setInt(1, leave.employeeId);
        ps.setString(2, leave.leaveType);
        ps.setString(3, leave.fromDate);
        ps.setString(4, leave.toDate);
        ps.setLong(5, leave.totalDays);
        ps.setString(6, leave.reason);
        ps.setString(7, "Pending");  // ✅ ALWAYS pending;

        ps.executeUpdate();

        System.out.println("Leave saved to database!");

    } catch (Exception e) {
        e.printStackTrace();
    }
}

public void viewMyLeavesFromDB(int empId) {

    try {
        Connection conn = DatabaseConnection.getConnection();

        String query = "SELECT * FROM leaves WHERE employee_id=?";

        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, empId);

        ResultSet rs = ps.executeQuery();

        boolean found = false;

        while (rs.next()) {

            found = true;

            System.out.println("----------------------");
            System.out.println("Leave ID: " + rs.getInt("leave_id"));
            System.out.println("Leave Type: " + rs.getString("leave_type"));
            System.out.println("From Date: " + rs.getString("from_date"));
            System.out.println("To Date: " + rs.getString("to_date"));
            System.out.println("Total Days: " + rs.getInt("total_days"));
            System.out.println("Reason: " + rs.getString("reason"));
            System.out.println("Status: " + rs.getString("status"));
        }

        if (!found) {
            System.out.println("No Leave Requests Found.");
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
}
public void viewAllLeavesFromDB() {

    try {
        Connection conn = DatabaseConnection.getConnection();

        String query = "SELECT l.leave_id, e.name, l.leave_type, l.from_date, l.to_date, l.total_days, l.reason, l.status " +
               "FROM leaves l JOIN employees e ON l.employee_id = e.id";

        PreparedStatement ps = conn.prepareStatement(query);

        ResultSet rs = ps.executeQuery();

        boolean found = false;

        System.out.println("-----------------------------------------------------------------------------------");
        System.out.printf("%-10s %-15s %-12s %-12s %-12s %-10s\n","Leave ID", "Name", "Type", "From", "To", "Status");
        System.out.println("-----------------------------------------------------------------------------------");

        while (rs.next()) {
            found = true;
            System.out.printf("%-10d %-15s %-12s %-12s %-12s %-10s\n",
                    rs.getInt("leave_id"),
                    rs.getString("name"),
                    rs.getString("leave_type"),
                    rs.getString("from_date"),
                    rs.getString("to_date"),
                    rs.getString("status"));
        }
        if (!found) {
            System.out.println("No Leave Requests Found.");
        }else{
            System.out.println("-----------------------------------------------------------------------------------");
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
}
public void updateLeaveStatusInDB(int leaveId, String status) {

    try {
        Connection conn = DatabaseConnection.getConnection();

        // Step 1: Update status
        String query = "UPDATE leaves SET status=? WHERE leave_id=?";
        PreparedStatement ps = conn.prepareStatement(query);

        ps.setString(1, status);
        ps.setInt(2, leaveId);

        int rows = ps.executeUpdate();

        if (rows > 0) {

            // Step 2: Get leave details
            String getLeaveQuery = "SELECT employee_id, total_days FROM leaves WHERE leave_id=?";
            PreparedStatement ps2 = conn.prepareStatement(getLeaveQuery);
            ps2.setInt(1, leaveId);

            ResultSet rs = ps2.executeQuery();

            if (rs.next() && status.equalsIgnoreCase("Approved")) {

                int empId = rs.getInt("employee_id");
                int days = rs.getInt("total_days");

                // Step 3: Update leave balance
                String updateBalance = "UPDATE employees SET used_leaves = used_leaves + ? WHERE id=?";
                PreparedStatement ps3 = conn.prepareStatement(updateBalance);

                ps3.setInt(1, days);
                ps3.setInt(2, empId);

                ps3.executeUpdate();
            }

            System.out.println("Leave status updated successfully!");

        } else {
            System.out.println("Leave ID not found.");
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
}

}
