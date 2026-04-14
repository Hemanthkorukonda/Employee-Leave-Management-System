package main;
import java.util.*;

import model.Employee;
import model.LeaveRequest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import service.LeaveService;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class LeaveManagementSystem {
  static Scanner sc = new Scanner(System.in);
  static LeaveService service = new LeaveService();
  static int idCounter = 1001;
  static ArrayList<LeaveRequest> leaveRequests = new ArrayList<>();
  static int leaveCounter = 1;

  public static void registerEmployee(){
    int id = idCounter++;

    System.out.print("Enter Name: ");
    String name = sc.nextLine();

    System.out.print("Enter email: ");
    String email = sc.nextLine();

    for(Employee emp : service.employees){
      if(emp.email.equals(email)){
        System.out.println("Email already registered!");
        return;
      }
    }

    System.out.print("Enter Password: ");
    String password = sc.nextLine();

    if(password.length()< 6){
      System.out.println("Password must be atleast 6 characters!");
      return;
    }
    System.out.print("Enter Role (Employee / Admin): ");
    String role = sc.nextLine();
    if(!role.equals("Employee") && !role.equals("Admin")){
      System.out.println("Invalid Role!");
      return;
    }

    Employee employee = new Employee(id, name, email, password, role);
    service.registerEmployee(employee);
    service.saveEmployeeToDB(employee);
    if(role.equals("Employee")){
      System.out.println("Employee Registered Successfully! Your ID is: "+ id);
    }else{
      System.out.println("Admin Registered Successfully! Your ID is: "+ id);
    }
    
  }

  public static void adminMenu(){
    while(true){
      System.out.println("\n ---Admin Menu---");
      System.out.println("1. View All Leave Requests");
      System.out.println("2. Approve/Reject Leaves");
      System.out.println("3. Logout");

      System.out.print("choose Option: ");
      int choice = sc.nextInt();
      sc.nextLine();

      switch(choice){
        case 1:
          service.viewAllLeavesFromDB();
          break;
        case 2:
          System.out.print("Enter Leave ID: ");
          int leaveId = sc.nextInt();
          sc.nextLine();
          System.out.print("Enter status (Approved / Rejected): ");
          String status = sc.nextLine();
          service.updateLeaveStatusInDB(leaveId, status);
          break;
        case 3:
          return;
        default:
          System.out.println("Invalid choice!");
      }
    }
  }
  
  public static void adminLogin() {

    System.out.print("Enter Admin Email: ");
    String email = sc.nextLine();

    System.out.print("Enter Password: ");
    String password = sc.nextLine();
    Employee emp = service.loginFromDB(email, password);

    if(emp != null && emp.role.equals("Admin")) {

    System.out.println("Admin Login Successful!");
    adminMenu();

  } else {
    System.out.println("Invalid Admin Credentials!");
  }
}

public static void employeeLogin() {

    System.out.print("Enter Email: ");
    String email = sc.nextLine();

    System.out.print("Enter Password: ");
    String password = sc.nextLine();

  Employee emp = service.loginFromDB(email, password);

  if(emp != null && emp.role.equals("Employee")) {

      System.out.println("Login Successful! Welcome " + emp.name);
      employeeMenu(emp);

  } else {
      System.out.println("Invalid Employee Credentials!");
  }

}

  public static void login() {

    System.out.println("Login As:");
    System.out.println("1. Admin");
    System.out.println("2. Employee");

    System.out.print("Choose option: ");
    int choice = sc.nextInt();
    sc.nextLine();

    switch (choice) {

        case 1:
            adminLogin();
            break;

        case 2:
            employeeLogin();
            break;

        default:
            System.out.println("Invalid choice");
    }
}
  
 public static void applyLeave(Employee emp) {
    //Leave type Validation.
    String leaveType;
    while (true) {
        System.out.print("Enter Leave Type (SICK / CASUAL / PAID): ");
        leaveType = sc.nextLine().toUpperCase();
        if (leaveType.equals("SICK") ||leaveType.equals("CASUAL") ||leaveType.equals("PAID")) {
            break;
        }

        System.out.println("Invalid Leave Type! Please enter SICK, CASUAL, or PAID.");
    }

   //Date Validation 
  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

  LocalDate fromDate;
  LocalDate toDate;

  while (true) {

    try {

        System.out.print("Enter From Date (dd-MM-yyyy): ");
        fromDate = LocalDate.parse(sc.nextLine(), formatter);

        System.out.print("Enter To Date (dd-MM-yyyy): ");
        toDate = LocalDate.parse(sc.nextLine(), formatter);

        if (toDate.isBefore(fromDate)) {
            System.out.println("Invalid date! To Date cannot be before From Date.");
            continue;
        }

        break;

    } catch (Exception e) {
        System.out.println("Invalid date format! Please use dd-MM-yyyy.");
    }
}
  //To know no.of days easily
  long totalDays = ChronoUnit.DAYS.between(fromDate, toDate) + 1;
  System.out.println("Total Leave Days: " + totalDays);

  int remaining = emp.totalLeaves - emp.usedLeaves;

    if (totalDays > remaining) {
        System.out.println("Not enough leave balance!");
        return;
    }
  
    System.out.print("Enter Reason: ");
    String reason = sc.nextLine();

    LeaveRequest leave = new LeaveRequest(leaveCounter++,emp.id,leaveType,fromDate.format(formatter),toDate.format(formatter),totalDays,reason,"Pending");

    service.applyLeave(leave);
    service.saveLeaveToDB(leave);

    System.out.println("Leave Applied Successfully! Status: Pending");
}
  
  public static void employeeMenu(Employee emp){
    while(true){
      System.out.println("\n ---Employee Menu---");
      System.out.println("1. Apply Leave");
      System.out.println("2. View My Leaves");
      System.out.println("3. View Leave Balance");
      System.out.println("4. Logout");

      System.out.print("Choose option: ");
      int choice = sc.nextInt();
      sc.nextLine();

      switch(choice){
        case 1:
          applyLeave(emp);
          break;
        case 2:
          service.viewMyLeavesFromDB(emp.id);
          break;
        case 3:
          viewLeaveBalance(emp.id);
          break;
        case 4:
          return;
        default:
          System.out.println("Invalid Choice!");
          
      }
    }
  }

public static void viewLeaveBalance(int empId) {

    try {
        Connection conn = DatabaseConnection.getConnection();

        String query = "SELECT total_leaves, used_leaves FROM employees WHERE id=?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, empId);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            int total = rs.getInt("total_leaves");
            int used = rs.getInt("used_leaves");
            int remaining = total - used;

            System.out.println("Total Leaves: " + total);
            System.out.println("Used Leaves: " + used);
            System.out.println("Remaining Leaves: " + remaining);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
}
  public static void main(String[] args) { 
    Connection conn = DatabaseConnection.getConnection();

    if (conn != null) {
        System.out.println("Connected Successfully!");
    } else {
        System.out.println("Connection Failed!");
    }
    while(true){
      System.out.println("\n--- Employee Management System---");
      System.out.println("1. Register Employee");
      System.out.println("2. Login");
      System.out.println("3. View Employees");
      System.out.println("4. Exit");

      System.out.print("Choose option: ");
      int choice = sc.nextInt();
      sc.nextLine();

      switch(choice){
        case 1:
          registerEmployee();
          break;
        case 2:
          login();
          break;
        case 3:
          service.viewEmployeesFromDB();
          break;
        case 4:
          System.exit(0);
        default:
          System.out.println("Invalid choice");  
      }
    }
  }
}
