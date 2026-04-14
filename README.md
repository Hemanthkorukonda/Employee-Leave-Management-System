# 📌 Employee Leave Management System

The **Employee Leave Management System** is a Java-based application that automates the process of managing employee leave requests. It eliminates manual tracking by providing a structured system where employees can apply for leave and administrators can approve or reject requests efficiently.

---

## 🚀 Features
- 👤 Add and manage employees  
- 📝 Apply for leave requests  
- ✅ Approve / ❌ Reject leave applications  
- 📊 Automatic leave balance update  
- 🔍 View employee leave details  
- 🗄️ MySQL database integration  

---

## 🛠️ Tech Stack
- Java  
- JDBC  
- MySQL  
- SQL  

---

## ⚙️ How It Works
1. Employee is added to the system  
2. Employee applies for leave  
3. Leave request is stored in database  
4. Admin reviews the request  
5. Leave is approved/rejected  
6. Leave balance is updated automatically  

---

## 📂 Project Structure
Employee-Leave-Management-System/
│
├── src/
│   ├── main/        → Main classes & database connection  
│   ├── model/       → Entity classes (Employee, LeaveRequest)  
│   ├── service/     → Business logic (LeaveService)  
│
├── lib/             → MySQL connector  
└── README.md  

---

## 🗄️ Database Design

### Employees Table
- id (Primary Key)  
- name  
- leave_balance  

### Leaves Table
- id (Primary Key)  
- employee_id (Foreign Key)  
- leave_type  
- start_date  
- end_date  
- status  

---

## ⚠️ Current Limitations
- Console-based (no UI)  
- Basic validation only  
- No authentication system  

---

## 🔮 Future Enhancements
- Web-based UI using Spring Boot  
- Role-based login (Admin / Employee)  
- Email notifications  
- Dashboard with analytics  

---

## 👨‍💻 Author
Hemanth
