// Package: edu.ccrm.cli
package edu.ccrm.cli;

import edu.ccrm.config.AppConfig;
import edu.ccrm.domain.*;
import edu.ccrm.exception.*;
import edu.ccrm.service.*;

import java.util.*;
import java.util.Scanner;

// Simple Console UI for Campus Course Records Manager
public class MainCLI {
    private final StudentService studentService = new StudentService();
    private final CourseService courseService = new CourseService();
    private final EnrollmentService enrollmentService = new EnrollmentService();
    private final Scanner scanner = new Scanner(System.in);

    public void start() {
        System.out.println("Welcome to Campus Course Records Manager (CCRM)");

        boolean running = true;
        while (running) {
            showMainMenu();
            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> manageStudents();
                    case "2" -> manageCourses();
                    case "3" -> manageEnrollments();
                    case "4" -> exportData();
                    case "0" -> {
                        running = false;
                        System.out.println("Exiting program. Goodbye!");
                    }
                    default -> System.out.println("Invalid option, try again.");
                }
            } catch (CCRMException | RuntimeException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void showMainMenu() {
        System.out.println("\nMain Menu:");
        System.out.println("1. Manage Students");
        System.out.println("2. Manage Courses");
        System.out.println("3. Enrollments and Grades");
        System.out.println("4. Export Data");
        System.out.println("0. Quit");
        System.out.print("Select an option: ");
    }

    private void manageStudents() throws CCRMException {
        System.out.println("\nStudent Management:");
        System.out.println("1. Add Student");
        System.out.println("2. List Students");
        System.out.println("3. Deactivate Student");
        System.out.println("0. Back");
        System.out.print("Choice: ");
        String opt = scanner.nextLine().trim();
        switch (opt) {
            case "1" -> addStudent();
            case "2" -> listStudents();
            case "3" -> deactivateStudent();
            case "0" -> {}
            default -> System.out.println("Invalid option");
        }
    }

    private void addStudent() throws InvalidDataException {
        System.out.print("Enter student ID: ");
        String id = scanner.nextLine().trim();
        System.out.print("Enter student RegNo: ");
        String regNo = scanner.nextLine().trim();
        System.out.print("Enter full name: ");
        String fullName = scanner.nextLine().trim();
        System.out.print("Enter email: ");
        String email = scanner.nextLine().trim();

        Student s = studentService.createStudent(id, regNo, fullName, email);
        System.out.println("Created student: " + s.getProfile());
    }

    private void listStudents() {
        System.out.println("All Students:");
        for (Student s : studentService.listAllStudents()) {
            System.out.println(s.getProfile());
        }
    }

    private void deactivateStudent() throws EntityNotFoundException {
        System.out.print("Enter student RegNo to deactivate: ");
        String regNo = scanner.nextLine().trim();
        studentService.deactivateStudent(regNo);
        System.out.println("Student deactivated: " + regNo);
    }

    private void manageCourses() throws CCRMException {
        System.out.println("\nCourse Management:");
        System.out.println("1. Add Course");
        System.out.println("2. List Courses");
        System.out.println("0. Back");
        System.out.print("Choice: ");
        String opt = scanner.nextLine().trim();
        switch (opt) {
            case "1" -> addCourse();
            case "2" -> listCourses();
            case "0" -> {}
            default -> System.out.println("Invalid option");
        }
    }

    private void addCourse() throws InvalidDataException {
        System.out.print("Code: ");
        String code = scanner.nextLine().trim();
        System.out.print("Title: ");
        String title = scanner.nextLine().trim();
        System.out.print("Credits (int): ");
        int credits = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Instructor ID: ");
        String instructorId = scanner.nextLine().trim();
        // Instructor could be fetched from separate storage or created; for brevity create dummy
        Instructor inst = new Instructor(instructorId, "Dummy Instructor", "instr@example.com", "Dept", LocalDate.now());
        System.out.print("Semester (SPRING, SUMMER, FALL): ");
        Course.Semester semester = Course.Semester.valueOf(scanner.nextLine().trim().toUpperCase());
        System.out.print("Department: ");
        String department = scanner.nextLine().trim();

        Course c = courseService.createCourse(code, title, credits, inst, semester, department);
        System.out.println("Created course: " + c.toString());
    }

    private void listCourses() {
        System.out.println("All Courses:");
        for (Course c : courseService.listAllCourses()) {
            System.out.println(c.toString());
        }
    }

    private void manageEnrollments() throws CCRMException {
        System.out.println("\nEnrollments and Grades:");
        System.out.println("1. Enroll Student");
        System.out.println("2. Assign Grade");
        System.out.println("3. Show Student Transcript");
        System.out.println("0. Back");
        System.out.print("Choice: ");
        String opt = scanner.nextLine().trim();
        switch (opt) {
            case "1" -> enrollStudent();
            case "2" -> assignGrade();
            case "3" -> showTranscript();
            case "0" -> {}
            default -> System.out.println("Invalid option");
        }
    }

    private void enrollStudent() throws EntityNotFoundException {
        System.out.print("Student RegNo: ");
        String regNo = scanner.nextLine().trim();
        System.out.print("Course Code: ");
        String courseCode = scanner.nextLine().trim();
        Student student = studentService.getStudentByRegNo(regNo);
        Course course = courseService.getCourse(courseCode);
        Enrollment e = enrollmentService.enrollStudent(student, course);
        System.out.println("Enrolled: " + e.toString());
    }

    private void assignGrade() throws EntityNotFoundException {
        System.out.print("Student RegNo: ");
        String regNo = scanner.nextLine().trim();
        System.out.print("Course Code: ");
        String courseCode = scanner.nextLine().trim();
        System.out.print("Grade (S, A, B, C, D, F): ");
        Grade grade = Grade.valueOf(scanner.nextLine().trim().toUpperCase());
        Student student = studentService.getStudentByRegNo(regNo);
        Course course = courseService.getCourse(courseCode);
        enrollmentService.assignGrade(student, course, grade);
        System.out.println("Assigned grade " + grade + " for student " + regNo + " in course " + courseCode);
    }

    private void showTranscript() throws EntityNotFoundException {
        System.out.print("Student RegNo: ");
        String regNo = scanner.nextLine().trim();
        Student student = studentService.getStudentByRegNo(regNo);
        System.out.println("Transcript for " + student.getFullName());
        List<Enrollment> enrollments = enrollmentService.getEnrollmentsByStudent(student);
        for (Enrollment e : enrollments) {
            System.out.printf("Course: %s, Grade: %s\n", e.getCourse().getTitle(),
                              e.getGrade() == null ? "N/A" : e.getGrade().name());
        }
        System.out.printf("GPA: %.2f\n", enrollmentService.computeGPA(student));
    }

    private void exportData() {
        try {
            edu.ccrm.io.FileUtil.exportStudentsCSV(studentService.listAllStudents());
            System.out.println("Exported students data to CSV");
        } catch (Exception e) {
            System.out.println("Export failed: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        MainCLI cli = new MainCLI();
        cli.start();
    }
}
