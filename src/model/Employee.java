package model;
public class Employee {
  public int id;
  public String name;
  public String email;
  public String password;
  public String role;
  public int totalLeaves;
  public int usedLeaves;

  public Employee(int id, String name, String email, String password, String role){
    this.id = id;
    this.name = name;
    this.email = email;
    this.password = password;
    this.role = role;
    this.totalLeaves = 20;
    this.usedLeaves = 0;
  }

}
