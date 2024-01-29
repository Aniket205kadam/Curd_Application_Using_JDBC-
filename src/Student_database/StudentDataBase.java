package Student_database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class StudentDataBase {
	public static void main(String[] args) {
		Connection connection = null;
		
		
		try {
			//connect to the database and load the Driver class
			connection = ConnectToDataBase.getConnection();
			//CrudOperation class object
			CrudOperation crudOperation = new CrudOperation();
			crudOperation.mainLoop(connection);
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		} finally {
			//close the resoures
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}

class ConnectToDataBase {
	//load and Register the Driver class
	static {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} 
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	//connect to jdbc_learing database
	public static Connection getConnection() throws SQLException {
		String url = "jdbc:mysql://localhost:3306/jdbc_learing";
		String userName = "root";
		String password = "2004";
		return DriverManager.getConnection(url, userName, password);
	}
}

class CrudOperation {
	//Scanner class object
	public Scanner scanner = new Scanner(System.in);
	
	//main loop
	public void mainLoop(Connection connection) {
		int userChoise = -1;
		
		while(!(userChoise == 6)) {
			printMenu();
			userChoise = scanner.nextInt();
			System.out.println();
			
			switch(userChoise) {
			case 1 -> {
				addStudent(connection);
			}
			case 2 -> {
				displayStudentDetails(connection);
			}
			case 3 -> {
				updateStudentDetails(connection);
			}
			case 4 -> {
				removeStudent(connection);
			}
			case 5 -> {
				displayAllStudents(connection);
			}
			case 6 -> {
				System.out.println("\nOut to the student database...");
			}
			}
		}
	}
	
	private static void printMenu() {
        System.out.println();
        System.out.println("1. Add Student");
        System.out.println("2. Display Student Details");
        System.out.println("3. Update Student Details");
        System.out.println("4. Remove Student");
        System.out.println("5. Display all students");
        System.out.println("6. Exit");
        System.out.print("Enter your choise: ");
    }
	//add student 
	private void addStudent(Connection connection) {
		System.out.print("Enter the student name: ");
		String name = scanner.next();
		
		System.out.print("Enter the student age: ");
		int age = scanner.nextInt();
		
		System.out.print("Enter the student city: ");
		String city = scanner.next();
		
		String insertQuery = "INSERT INTO students(Name, Age, City) VALUES(?, ?, ?)";
		
		PreparedStatement insertStatement = null;
		
		try {
			insertStatement = connection.prepareStatement(insertQuery);
			//put the data in the place_holder
			insertStatement.setString(1, name);
			insertStatement.setInt(2, age);
			insertStatement.setString(3, city);
			
			//execute the query
			int rowsAffected = insertStatement.executeUpdate();
			
			//check the changes are reflect into the database
			if (rowsAffected == 0) {
				System.out.println("\n" + "Student adding is Failed!");
			} 
			else {
				System.out.println("\nStudent added successfully!");
			}
			
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		} 
		finally {
			try {
				insertStatement.close();
			} 
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	//update Student Details
	private void updateStudentDetails(Connection connection) {
		System.out.print("Enter student ID to update details: ");
		int id = scanner.nextInt();
		
		//check the given id is present on the database
		String idCheckerQuery = "SELECT * FROM students WHERE id = ?";
		PreparedStatement idChecker = null;
		String updateQuery = "UPDATE students SET Name = ?, Age = ?, City = ? WHERE id = ?";
		PreparedStatement updateStatement = null;
		
		try {
			
			idChecker = connection.prepareStatement(idCheckerQuery );
			//set the id
			idChecker.setInt(1, id);
			
			//Student id is present in the database
			if(idChecker.executeQuery().next()) {
				System.out.print("Enter updated student name: ");
				String name = scanner.next();
				
				System.out.print("Enter updated student age: ");
				int age = scanner.nextInt();
				
				System.out.print("Enter updated student city: ");
				String city = scanner.next();
				
				
				updateStatement = connection.prepareStatement(updateQuery);
				
				//set the updated data
				updateStatement.setInt(4, id);
				updateStatement.setString(1, name);
				updateStatement.setInt(2, age);
				updateStatement.setString(3, city);
				
				//execute query
				int rowsAffected = updateStatement.executeUpdate();
				
				//check the update are reflect in the database
				if (rowsAffected == 0) {
					System.out.println("\nStudent details update is Failed!");
				} 
				else {
					System.out.println("\nStudent details updated successfully!");
				}
			} 
			//Student id is NOT present in the database
			else {
				System.out.println("\nStudent with ID " + id + " not found.");
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//show the details
	private void displayStudentDetails(Connection connection) {
		 System.out.print("Enter student ID to display details: ");
		 int id = scanner.nextInt();	
		 
		 try {
			 
			String displayQuery = "SELECT * FROM students WHERE id = ?";
			PreparedStatement displayStatement = connection.prepareStatement(displayQuery);
			
			//set the id in the place-holder
			displayStatement.setInt(1, id);
			
			//execute the query
			ResultSet resultSet = displayStatement.executeQuery();
			
			//if the id is present in the database
			if(resultSet.next()) {
				System.out.println("Student ID: " + resultSet.getInt("id"));
				System.out.println("Student Name: " + resultSet.getString("Name"));
				System.out.println("Student age: " + resultSet.getInt("Age"));
				System.out.println("Student city: " + resultSet.getString("City"));
			} 
			//the id is NOT present in the database
			else {
				System.out.println("\nStudent with ID " + id + " not found.");
			}
		 }
		 catch (SQLException e) {
			e.printStackTrace();
		}
		 catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//delelet the student to the database
	private void removeStudent(Connection connection) {
		System.out.print("Enter student ID to remove: ");
		int id = scanner.nextInt();
		
		//check the given id is present on the database
		String idCheckerQuery = "SELECT * FROM students WHERE id = ?";
		PreparedStatement idChecker = null;
		String deleteQuery = "DELETE FROM students WHERE id = ?";
		PreparedStatement deleteStatement = null;
				
		try {
					
			idChecker = connection.prepareStatement(idCheckerQuery );
			//set the id
			idChecker.setInt(1, id);
					
			//Student id is present in the database
			if(idChecker.executeQuery().next()) {
				deleteStatement = connection.prepareStatement(deleteQuery);
				
				//set the id in the place-holder
				deleteStatement.setInt(1, id);
				
				//execute the query
				int rowsAffected = deleteStatement.executeUpdate();
				
				//check the deletion are reflect in the database
				if(rowsAffected == 0) {
					System.out.println("\nStudent with ID " + id + " removing is Failed");
				}
				else {
					System.out.println("\nStudent with ID " + id + " removed successfully!");
				}			
			} 
			//Student id is NOT present in the database
			else {
				System.out.println("\nStudent with ID " + id + " not found.");
			}
		}  
		catch (SQLException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void displayAllStudents(Connection connection) {
		System.out.println();
		System.out.println("ID " + " Name");
		PreparedStatement displayAllStatement = null;
		String displayAllQuery = null;
		
		try {
			displayAllQuery = "SELECT * FROM students";
			displayAllStatement = connection.prepareStatement(displayAllQuery );
			
			//execute the query
			ResultSet allStudents = displayAllStatement.executeQuery();
			
			//print the table
			while(allStudents.next()) {
				System.out.println(allStudents.getInt("id") + " " + allStudents.getString("Name"));
			}
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
