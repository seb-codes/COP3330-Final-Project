import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

//TODO add everyone's names to the header
//**
//  COP3330 Spring 2023 Final Project 
//  Created by Ian Ragan & Sebastian Steele
// */

public class Main {

	public static ArrayList<Course> readLecFile(String filePath) {

		ArrayList<Course> courseArray = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			String line;

			while ((line = br.readLine()) != null) {
				String[] array = line.split(",");

				String lastItem = array[array.length - 1].toLowerCase();

				// System.out.println(lastItem);

				if (lastItem.equals("online")) {
					// System.out.println("This class is online");
					courseArray.add(new Course(array[0], array[1], array[2], array[3], array[4]));
				}

				// idea, always grab the last full course before hand, then keep and store labs
				// in needed

				if (lastItem.equals("yes")) {
					// System.out.println("This class has a labs");
					courseArray.add(new Course(array[0], array[1], array[2], array[3], array[4], array[5], array[6]));

				}

				if (lastItem.equals("no")) {
					// System.out.println("This class has no labs");
					courseArray.add(new Course(array[0], array[1], array[2], array[3], array[4], array[5], array[6]));
				}

				if (array.length == 2) {
					// System.out.println("This is a lab");
					Course tmpCourse = courseArray.get(courseArray.size() - 1);
					tmpCourse.addLab(new Lab(array[0], array[1]));
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return courseArray;
	}

	public static Scanner scanner = null;
	public static String stringInput = null;
	public static boolean runProgram = true;
	public static boolean fileChanged = false;
	public static int menuOption = 0;
	public static String temp = null;
	public static ArrayList<Person> personList = new ArrayList<>();
	ArrayList<Course> courseArray = readLecFile(stringInput);

	public static void main(String[] args) throws FileNotFoundException {

		System.out.print("Enter the absolute path of the file: ");

		scanner = new Scanner(System.in);

		stringInput = scanner.nextLine();
		File file = new File(stringInput);
		while (!file.exists()) {
			System.out.print("Sorry no such file.\nTry again: ");
			stringInput = scanner.nextLine();
			file = new File(stringInput);
		}

		System.out.println("File Found! Let’s proceed...");
		System.out.println("*****************************************");

		// if we are confident with this let's move it out of main and into the global
		// section
		ArrayList<Course> courseArray = readLecFile(stringInput);

//		 for (int i = 0; i < courseArray.size(); i++) {
//	            System.out.println(courseArray.get(i).toString());
//	        }

		while (runProgram) {
			System.out.println("Choose one of these options:");
			System.out.println("   1- Add a new Faculty to the schedule");
			System.out.println("   2- Enroll a Student to a Lecture");
			System.out.println("   3- Print the schedule of a Faculty");
			System.out.println("   4- Print the schedule of an TA");
			System.out.println("   5- Print the schedule of a Student");
			System.out.println("   6- Delete a Lecture");
			System.out.println("   7- Exit");
			System.out.print("	Enter your choice: ");
//			This makes sure the program wont crash due to a 
//			input mismatch and that the input is within the 1 to 7 range
			menuSelect();
			while (menuOption < 1 || menuOption > 7 || temp.length() > 1) {
				System.out.print("Invalid input! Please choose a repsonce between 1 and 7");
				menuSelect();
			}

			switch (menuOption) {
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
				idCheck(menuOption, courseArray);
				break;
			case 6:
				deleteLecture(courseArray);
				break;
			case 7:
				exitProgram(courseArray);
				break;
			}

		}
		scanner.close();
	}

	public static void menuSelect() {
		temp = scanner.nextLine();
		menuOption = temp.charAt(0);
		menuOption -= 48;
	}

	public static void addFac(ArrayList<Course> courseArray) {
		Faculty p = null;
		Boolean exists = false;
		String name = null;
		String rank = null;
		String office = null;
		String numLecturesString = null;
		Course c = null;
		ArrayList<Lab> labList = null;
		String studentType = null;

		for (int i = 0; i < personList.size(); i++) {
			if (personList.get(i).getUcfId() == Integer.valueOf(stringInput) && personList.get(i) instanceof Faculty) {
				exists = true;
				p = (Faculty) personList.get(i);
				name = p.getName();
				rank = p.getRank();
				office = p.getOffice();
				System.out.println("	Record found/Name: " + name);
			}
		}

		if (!exists) {
			System.out.print("	Enter name: ");
			name = scanner.nextLine();
			System.out.print("	Enter rank: ");
			rank = scanner.nextLine();
			System.out.print("	Enter office: ");
			office = scanner.nextLine();

			p = new Faculty(name, Integer.valueOf(stringInput), rank, office);
			personList.add(p);

		}

		// need to make sure numLecuteres = number of lectures entered by user

		System.out.print("	Enter how many lectures: ");
		numLecturesString = scanner.nextLine();

		// int numLecturesInt = Integer.parseInt(numLecturesString);

		System.out.print("	Enter the codes separated by spaces: ");
		String lectures = scanner.nextLine();

		String[] codes = lectures.split(" ");

		// delete duplicate codes
		Set<String> codeSet = new HashSet<>(Arrays.asList(codes));
		String[] uniqueCodes = codeSet.toArray(new String[0]);

		for (String code : uniqueCodes) {
			if (code.length() == 5 && code.matches("\\d+")) {
				// System.out.println(code);
			}
		}

		for (String lecture : uniqueCodes) {
			for (int i = 0; i < courseArray.size(); i++) {
				if (courseArray.get(i).getCourseNumber().equalsIgnoreCase(lecture)) {
					c = courseArray.get(i);
					if (c.getProfessor() != null) {
						System.out.println("Class already has a professor");
						break;
					} else {
						String num = c.getCourseNumber();
						String prefix = c.getPrefix();
						String courseName = c.getCourseName();
						System.out.printf("	[%s/%s/%s] Added!\n", num, prefix, courseName);

						if (c.getHasLab().equalsIgnoreCase("yes")) {
							System.out.printf("	[%s/%s/%s] has these labs:\n", num, prefix, courseName);
							for (int j = 0; j < c.getLabList().size(); j++) {
								System.out.printf("		%s\n", c.getLabList().get(j));
							}
						}

						p.addLecture(courseArray.get(i));
						c.setProfessor(name);
					}

					if (c.getHasLab().equalsIgnoreCase("yes")) {
						labList = c.getLabList();

						for (Lab l : labList) {
							String labId = l.getCourseNumber();
							boolean idExist = false;

							boolean isFac = false;
							String tmpId = null;

							boolean validId = false;
							while (!validId) {
								System.out.printf("	Enter the TA’s id for %s: ", c.getCourseNumber());
								int idInt = scanner.nextInt();
								scanner.nextLine(); // consume the newline character

								// Convert the int to a String and check if it is 7 digits and all numbers
								tmpId = Integer.toString(idInt);
								if (!tmpId.matches("[0-9]{7}")) {
									System.out.println("Invalid ID. Please enter a 7-digit numeric ID.");
									continue;
								}

								// Check if the ID belongs to a faculty object
								boolean isFaculty = false;
								for (Person tmpPerson : personList) {
									if (tmpPerson instanceof Faculty
											&& Integer.toString(((Faculty) tmpPerson).getUcfId()).equals(tmpId)) {
										isFaculty = true;
										break;
									}
								}

								if (!isFaculty) {
									validId = true;
								} else {
									System.out.println("ID belongs to a faculty member. Please enter a different ID.");
								}
							}

							String taId = tmpId;

							for (Person t : personList) {
								int id = t.getUcfId();
								String idString = Integer.toString(id);

								if (taId.equalsIgnoreCase(idString)) {
									idExist = true;

									if (t instanceof Student) {
										studentType = "student";
										ArrayList<Course> taking = ((Student) t).getCoursesTaken();
										for (Course ct : taking) {
											if (ct.getCourseNumber().equalsIgnoreCase(labId)) {
												System.out.println("TA is taking course as a Student");
												// ask for a new TA here
												break;
											}
										}
										System.out.println("Student is not taking course, converting to TA");
										System.out.println(" TA’s supervisor’s name: ");
										String advisor = scanner.nextLine();

										System.out.println(" Degree Seeking: ");
										String expectedDegree = scanner.nextLine();

										TA tmpTA = new TA(t.getName(), id, advisor, expectedDegree);

										personList.remove(t);
										personList.add(tmpTA);

										tmpTA.getLabsSupervised().add(l);
										break;
									} else if (t instanceof TA) {
										studentType = "ta";
										ArrayList<Course> taking = ((TA) t).getCoursesTaken();
										ArrayList<Lab> labs = ((TA) t).getLabsSupervised();

										for (Course ct : taking) {
											if (ct.getCourseNumber().equalsIgnoreCase(labId)) {
												System.out.println("TA is taking course as a Student");
												// ask for a new TA here
												break;
											}
										}

										for (Lab ls : labs) {
											if (ls.getCourseNumber().equalsIgnoreCase(labId)) {
												System.out.println("TA is already registered for this lab");
												break;
											}
										}

										((TA) t).getLabsSupervised().add(l);
										break;
									} else {
										continue;
									}

								}

							}
							if (!idExist) {
								// add a new TA
								System.out.print("	Name of TA: ");
								name = scanner.nextLine();

								System.out.print(" TA’s supervisor’s name: ");
								String advisor = scanner.nextLine();

								System.out.print(" Degree Seeking: ");
								String expectedDegree = scanner.nextLine();

								TA tmpTA = new TA(name, Integer.parseInt(taId), advisor, expectedDegree);
								tmpTA.getLabsSupervised().add(l);

								personList.add(tmpTA);

							}
						}

					}
				}
			}
		}

	}

	public static Course enrollingCourse = null;
	public static Lab tmpLab = null;
	public static String gradLevel = null;
	public static String labId = null;
	public static String crns = null;
	public static String name = null;

	public static void enrollStudent(ArrayList<Course> courseArray) {
		Person p = null;

		Boolean exists = false;

		for (int i = 0; i < personList.size(); i++) {
			if (personList.get(i).getUcfId() == Integer.valueOf(stringInput)) {
				exists = true;
				p = personList.get(i);
				name = p.getName();
				System.out.println("	Record found/Name: " + name);
			}
		}

		if (!exists) {
			System.out.print("	Enter name: ");
			name = scanner.nextLine();
		}

		System.out.print("	Which lecture to enroll: [" + name + "] in? ");
		// validate this is a 5 digit value
		// edit id validator to take in a number of digits and the string you want to
		// validate like this (int num_of_digits, String validation_string)
		crns = scanner.nextLine();

		// validate if CRN is an available class
		boolean classExists = false;

		for (int i = 0; i < courseArray.size(); i++) {
			if (courseArray.get(i).getCourseNumber().equalsIgnoreCase(crns)) {
				enrollingCourse = courseArray.get(i);
				classExists = true;
				break;
			}
		}

		if (classExists) {
			// System.out.println(" The class exists");
		} else {
			System.out.println("	The class does not exist, exiting");
			return;
		}

		// doing work if student is a TA
		// we assume that is p is not a TA or Null then we will create a Student object
		// for the new person
		if (p instanceof TA) {
			System.out.println("TA ENROLLING SECTION.");
			ArrayList<Lab> tmpLabList = ((TA) p).getLabsSupervised();
			for (Lab lab : tmpLabList) {
				if (lab.getCourseNumber().equalsIgnoreCase(crns)) {
					System.out.println("	This person is a TA for this course");
					return;
				}
			}

			// check if student is already registered in course or lab
			boolean enrolled = false;
			Map<Integer, Course> tmpCourseMap = ((TA) p).getCourseMap();
			Map<Integer, Lab> tmpLabMap = ((TA) p).getLabMap();
			for (Map.Entry<Integer, Course> entry : tmpCourseMap.entrySet()) {
				Course value = entry.getValue();
				String tmpCrn = value.getCourseNumber();
				if (tmpCrn.equalsIgnoreCase(crns)) {
					enrolled = true;
					break;
				}
			}

			if (enrolled) {
				System.out.println("	The TA is already enrolled");
				return;
			} else {
				System.out.println("	The TA is not enrolled, Enrolling TA in to class");
			}

			// checking if class has a Lab and picking lab to enroll in

			if (enrollingCourse.getHasLab().equalsIgnoreCase("yes")) {
				ArrayList<Lab> courseLabList = enrollingCourse.getLabList();
				int randomIndex = new Random().nextInt(courseLabList.size());
				tmpLab = courseLabList.get(randomIndex);
				labId = tmpLab.getCourseNumber();

			} else {
				System.out.println("	Course has no labs to enroll in");
			}

			// enrolling TA into the class
			int nextKey = Collections.max(tmpCourseMap.keySet()) + 1;
			tmpCourseMap.put(nextKey, enrollingCourse);
			tmpLabMap.put(nextKey, tmpLab);

			if (labId != null) {
				System.out.print("[" + name + "] is added to lab :" + labId);
			} else {
				System.out.print("[" + name + "] is added to lecture :" + crns);
			}

			System.out.print("TA Enrolled!");

		}

		if (p instanceof Student) {
			System.out.println("	STUDENT ENROLLING SECTION.");
			// check if student is already registered in course or lab
			boolean enrolled = false;
			Map<Integer, Course> tmpCourseMap = ((Student) p).getCourseMap();
			Map<Integer, Lab> tmpLabMap = ((Student) p).getLabMap();
			for (Map.Entry<Integer, Course> entry : tmpCourseMap.entrySet()) {
				Course value = entry.getValue();
				String tmpCrn = value.getCourseNumber();
				if (tmpCrn.equalsIgnoreCase(crns)) {
					enrolled = true;
					break;
				}
			}

			if (enrolled) {
				System.out.println("	The Student is already enrolled");
				return;
			} else {
				System.out.println("	The Student is not enrolled, Enrolling Student in to class");
			}

			// checking if class has a Lab and picking lab to enroll in

			randomLab();

			// enrolling TA into the class
			int nextKey = Collections.max(tmpCourseMap.keySet()) + 1;
			tmpCourseMap.put(nextKey, enrollingCourse);
			tmpLabMap.put(nextKey, tmpLab);

		} else {
			randomLab();

			System.out.print("	Is student Grad or Undergrad: ");
			gradLevel = scanner.nextLine();
			// need to validate this input

			p = new Student(name, Integer.valueOf(stringInput), gradLevel);
			personList.add(p);
			Map<Integer, Course> newCourseMap = ((Student) p).getCourseMap();
			Map<Integer, Lab> newLabMap = ((Student) p).getLabMap();

			int nextKey = 1;

			newCourseMap.put(nextKey, enrollingCourse);
			newLabMap.put(nextKey, tmpLab);

		}

		System.out.println("\n	Student Enrolled!\n");
		System.out.println("*****************************************\n");

	}

	public static void randomLab() {
		if (enrollingCourse.getHasLab().equalsIgnoreCase("yes")) {

			String prefix = enrollingCourse.getPrefix();
			String courseName = enrollingCourse.getCourseName();
			System.out.printf("	[%s/%s] has these labs: \n", prefix, courseName);

			for (int i = 0; i < enrollingCourse.getLabList().size(); i++) {
				System.out.printf("		%s\n", enrollingCourse.getLabList().get(i));
			}
			System.out.println();

			ArrayList<Lab> courseLabList = enrollingCourse.getLabList();
			int randomIndex = new Random().nextInt(courseLabList.size());
			tmpLab = courseLabList.get(randomIndex);
			labId = tmpLab.getCourseNumber();

			if (labId != null) {
				System.out.println("	[" + name + "] is added to lab : " + labId);
			} else {
				System.out.println("	[" + name + "] is added to lecture : " + crns);
			}

		} else {
			System.out.println("	Course has no labs to enroll in");
		}
	}

	public static void noRecordsFound(ArrayList<Course> courseArray) {
		System.out.println("No records to be found");
		System.out.println("Moving to Student Enrollment");
		enrollStudent(courseArray);
	}

	public static void printFaculty(ArrayList<Course> courseArray) {
		if (personList.isEmpty()) {
			noRecordsFound(courseArray);

		} else {
			for (Person p : personList) {
				if (p instanceof Faculty) {

					if (p.getUcfId() == Integer.valueOf(stringInput)) {
						System.out.println("\nRecord Found:");
						System.out.printf("	%s is teaching the following lectures:\n", p.getName());

						for (int i = 0; i < ((Faculty) p).getLecturesTaught().size(); i++) {
							String prefix = ((Faculty) p).getLecturesTaught().get(i).getPrefix();
							String name = ((Faculty) p).getLecturesTaught().get(i).getCourseName();
							String mode = ((Faculty) p).getLecturesTaught().get(i).getCourseType();
							String number = ((Faculty) p).getLecturesTaught().get(i).getCourseNumber();
							System.out.printf("	 [%s/%s/%s]", prefix, name, mode);

							if (((Faculty) p).getLecturesTaught().get(i).getHasLab().equals("yes")) {
								System.out.printf("[%s/%s/%s] with labs", number, prefix, name);
								System.out.println(((Faculty) p).getLecturesTaught().get(i).getLabList());
								for (int j = 0; j < ((Faculty) p).getLecturesTaught().get(j).getLabList().size(); j++) {
									System.out.printf("		%s\n",
											((Faculty) p).getLecturesTaught().get(j).getLabList());
								}
							}
						}

						System.out.println("\n\n*****************************************\n");

					} else {
						noRecordsFound(courseArray);
					}
				}
			}
		}
	}

	public static void printTA(ArrayList<Course> courseArray) {
		if (personList.isEmpty()) {
			noRecordsFound(courseArray);

		} else {
			for (Person p : personList) {
				if (p instanceof TA) {

					if (p.getUcfId() == Integer.valueOf(stringInput)) {
						System.out.println("\nRecord Found:");
						System.out.printf("	%s\n", p.getName());
						System.out.printf("	Enrolled in the following lectures\n");
						for (Entry<Integer, Course> entry : ((TA) p).getCourseMap().entrySet()) {
							String prefix = entry.getValue().getPrefix();
							String name = entry.getValue().getCourseName();

							if (entry.getValue().getHasLab().equalsIgnoreCase("no")) {
								System.out.printf("	 [%s/%s]\n", prefix, name);
							}

							if (entry.getValue().getHasLab().equalsIgnoreCase("yes")) {
								System.out.printf("	 [%s/%s]", prefix, name);
								for (Entry<Integer, Lab> entry2 : ((TA) p).getLabMap().entrySet()) {
									if (entry2.getValue() != null) {

										if (entry.getKey() == entry2.getKey()) {
											String labName = entry2.getValue().getCourseNumber();
											System.out.printf("/[Lab: %s]\n", labName);
										}
									}
								}
							}
						}
						System.out.println("\n\n*****************************************\n");

					} else {
						noRecordsFound(courseArray);
					}
				}
			}
		}
	}

	public static void printStudent(ArrayList<Course> courseArray) {
		if (personList.isEmpty()) {
			noRecordsFound(courseArray);

		} else {
			for (Person p : personList) {
				if (p instanceof Student) {
					if (p.getUcfId() == Integer.valueOf(stringInput)) {
						System.out.println("\nRecord Found:");
						System.out.printf("	%s\n", p.getName());
						System.out.printf("	Enrolled in the following lectures\n");
						for (Entry<Integer, Course> entry : ((Student) p).getCourseMap().entrySet()) {
							String prefix = entry.getValue().getPrefix();
							String name = entry.getValue().getCourseName();

							if (entry.getValue().getHasLab().equalsIgnoreCase("no")) {
								System.out.printf("	 [%s/%s]\n", prefix, name);
							}

							if (entry.getValue().getHasLab().equalsIgnoreCase("yes")) {
								System.out.printf("	 [%s/%s]", prefix, name);
								for (Entry<Integer, Lab> entry2 : ((Student) p).getLabMap().entrySet()) {
									if (entry2.getValue() != null) {

										if (entry.getKey() == entry2.getKey()) {
											String labName = entry2.getValue().getCourseNumber();
											System.out.printf("/[Lab: %s]\n", labName);
										}
									}
								}
							}
						}
						System.out.println("\n\n*****************************************\n");

					} else {
						noRecordsFound(courseArray);
					}
				}
			}
		}
	}

	public static void deleteLecture(ArrayList<Course> courseArray) {

		System.out.print("Enter the crn of the lecture to delete: ");
		stringInput = scanner.next();
		scanner.nextLine();

		for (int i = 0; i < courseArray.size(); i++) {
			if (courseArray.get(i).getCourseNumber().equals(stringInput)) {
				courseArray.get(i).deletionPrint();
				System.out.print(" Deleted\n");

				for (Person p : personList) {
					if (p instanceof Student) {

						Iterator<Entry<Integer, Course>> iterator = ((Student) p).getCourseMap().entrySet().iterator();
						while (iterator.hasNext()) {
							if (iterator.next().getValue().equals(courseArray.get(i))) {
								iterator.remove();
							}

						}

					}
					if (p instanceof TA) {

						Iterator<Entry<Integer, Course>> iterator = ((TA) p).getCourseMap().entrySet().iterator();
						while (iterator.hasNext()) {
							if (iterator.next().getValue().equals(courseArray.get(i))) {
								iterator.remove();
							}

						}

					}
					if (p instanceof Faculty) {

						for (int j = 0; i < ((Faculty) p).getLecturesTaught().size(); j++) {
							if (((Faculty) p).getLecturesTaught().get(j).getCourseNumber().equals(stringInput)) {
								((Faculty) p).getLecturesTaught().get(j).deletionPrint();
								System.out.print(" Deleted\n");
								((Faculty) p).getLecturesTaught().remove(((Faculty) p).getLecturesTaught().get(j));
							}
						}

					}
				}
				courseArray.remove(courseArray.get(i));
				fileChanged = true;

			}
		}

	}

	public static void idCheck(int option, ArrayList<Course> courseArray) {
		System.out.print("	Enter UCF id: ");

		stringInput = scanner.nextLine();

		try {
			if (stringInput.length() == 7 && Pattern.matches("^[0-9]*$", stringInput)) {
				switch (option) {
				case 1:
					addFac(courseArray);
					break;
				case 2:
					// need to hand in course array
					enrollStudent(courseArray);
					break;
				case 3:
					printFaculty(courseArray);
					break;
				case 4:
					printTA(courseArray);
				case 5:
					printStudent(courseArray);
					break;
				}
			} else {
				throw new IdException(stringInput);
			}
		} catch (IdException e) {
			System.out.println(">>>>>>>>>>>Sorry incorrect format. (Ids are 7 digits)");
		}

	}

	public static void exitProgram(ArrayList<Course> courseArray) {

		if (fileChanged) {
			System.out.print("You have made a deletion of at least one lecture. Would you like to\n "
					+ "print the copy of lec.txt? Enter y/Y for Yes or n/N for No: ");
			stringInput = scanner.nextLine();
			while (runProgram) {
				if (stringInput.equalsIgnoreCase("y") || stringInput.equalsIgnoreCase("n")) {

					if (stringInput.equalsIgnoreCase("y")) {
						printFile(courseArray);
					}

					runProgram = false;
				} else {
					System.out.print("Is that a yes or no? Enter y/Y for Yes or n/N for No: ");
					stringInput = scanner.nextLine();
				}
			}
		} else {
			runProgram = false;
		}
		System.out.print("Bye!");
	}

	public static void printFile(ArrayList<Course> courseArray) {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter("lec1.txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		for (Course c : courseArray) {
			writer.println(c);
		}
		writer.close();
	}
}

class IdException extends Exception {
	public IdException(String message) {
		super(message);
	}
}

class Person {
	private String name;
	private int ucfId;

	public Person(String name, int ucfId) {
		this.name = name;
		this.ucfId = ucfId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getUcfId() {
		return ucfId;
	}

	public void setUcfId(int ucfId) {
		this.ucfId = ucfId;
	}
}

class Faculty extends Person {
	private String rank;
	private ArrayList<Course> lecturesTaught;
	private String office;

	public Faculty(String name, int ucfId, String rank, String office) {
		super(name, ucfId);
		this.rank = rank;
		this.office = office;
		;
		this.lecturesTaught = new ArrayList<>();
	}

	public String getOffice() {
		return office;
	}

	public void setOffice(String office) {
		this.office = office;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public ArrayList<Course> getLecturesTaught() {
		return lecturesTaught;
	}

	public void setLecturesTaught(ArrayList<Course> lecturesTaught) {
		this.lecturesTaught = lecturesTaught;
	}

	public void addLecture(Course lecture) {
		this.lecturesTaught.add(lecture);
	}

	public void removeLecture(Course lecture) {
		this.lecturesTaught.remove(lecture);
	}
}

class TA extends Person {
	private ArrayList<Lab> labsSupervised;
	private String advisor;
	private String expectedDegree;
	private ArrayList<Course> coursesTaken;
	Map<Integer, Course> courseMap;
	Map<Integer, Lab> labMap;

	public TA(String name, int ucfId, String advisor, String expectedDegree) {
		super(name, ucfId);
		this.labsSupervised = new ArrayList<>();
		this.advisor = advisor;
		this.expectedDegree = expectedDegree;
		this.coursesTaken = new ArrayList<>();
		this.courseMap = new HashMap<>();
		this.labMap = new HashMap<>();
	}

	// Getters and setters for attributes

	public ArrayList<Lab> getLabsSupervised() {
		return labsSupervised;
	}

	public void setLabsSupervised(ArrayList<Lab> labsSupervised) {
		this.labsSupervised = labsSupervised;
	}

	public ArrayList<Course> getCoursesTaken() {
		return coursesTaken;
	}

	public void setCoursesTaken(ArrayList<Course> coursesTaken) {
		this.coursesTaken = coursesTaken;
	}

	public void addLab(Lab lab) {
		this.labsSupervised.add(lab);
	}

	public void removeLab(Lab lab) {
		this.labsSupervised.remove(lab);
	}

	public void addCourse(Course course) {
		this.coursesTaken.add(course);
	}

	public void removeCourse(Course course) {
		this.coursesTaken.remove(course);
	}

	public String getAdvisor() {
		return advisor;
	}

	public void setAdvisor(String advisor) {
		this.advisor = advisor;
	}

	public String getExpectedDegree() {
		return expectedDegree;
	}

	public void setExpectedDegree(String expectedDegree) {
		this.expectedDegree = expectedDegree;
	}

	public Map<Integer, Course> getCourseMap() {
		return courseMap;
	}

	public void setCourseMap(Map<Integer, Course> courseMap) {
		this.courseMap = courseMap;
	}

	public Map<Integer, Lab> getLabMap() {
		return labMap;
	}

	public void setLabMap(Map<Integer, Lab> labMap) {
		this.labMap = labMap;
	}
}

class Student extends Person {
	private String studentType;
	private ArrayList<Course> coursesTaken;
	Map<Integer, Course> courseMap;
	Map<Integer, Lab> labMap;

	public Student(String name, int ucfId, String studentType) {
		super(name, ucfId);
		this.studentType = studentType;
		this.coursesTaken = new ArrayList<>();
		this.courseMap = new HashMap<>();
		this.labMap = new HashMap<>();
	}

	// Getters and setters for attributes

	public String getStudentType() {
		return studentType;
	}

	public void setStudentType(String studentType) {
		this.studentType = studentType;
	}

	public ArrayList<Course> getCoursesTaken() {
		return coursesTaken;
	}

	public void setCoursesTaken(ArrayList<Course> coursesTaken) {
		this.coursesTaken = coursesTaken;
	}

	public Map<Integer, Course> getCourseMap() {
		return courseMap;
	}

	public void setCourseMap(Map<Integer, Course> courseMap) {
		this.courseMap = courseMap;
	}

	public Map<Integer, Lab> getLabMap() {
		return labMap;
	}

	public void setLabMap(Map<Integer, Lab> labMap) {
		this.labMap = labMap;
	}

	public void addCourse(Course course) {
		this.coursesTaken.add(course);
	}

	public void removeCourse(Course course) {
		this.coursesTaken.remove(course);
	}

	@Override
	public String toString() {
		return String.format("\n\n\nStudent Info: %s - %d - %s\n\n", this.getName(), this.getUcfId(), this.studentType);
	}
}

class Lab {
	private String courseNumber;
	private String courseLocation;
	private String ta;

	public Lab(String courseNumber, String courseLocation) {
		this.courseNumber = courseNumber;
		this.courseLocation = courseLocation;
		this.ta = null;
	}

	public String getTa() {
		return ta;
	}

	public void setTa(String ta) {
		this.ta = ta;
	}

	public String getCourseNumber() {
		return courseNumber;
	}

	public void setCourseNumber(String courseNumber) {
		this.courseNumber = courseNumber;
	}

	public String getCourseLocation() {
		return courseLocation;
	}

	public void setCourseLocation(String courseLocation) {
		this.courseLocation = courseLocation;
	}

	@Override
	public String toString() {
		return String.format("%s,%s", this.getCourseNumber(), this.getCourseLocation());
	}
}

class Course extends Lab {
	private String prefix;
	private String courseName;
	private String gradType;
	private String courseType;
	private String hasLab;
	private ArrayList<Lab> labList;
	private String professor;

	public Course(String courseNumber, String prefix, String courseName, String gradType, String courseType,
			String courseLocation, String hasLab) {
		super(courseNumber, courseLocation);
		this.prefix = prefix;
		this.courseName = courseName;
		this.gradType = gradType;
		this.courseType = courseType;
		this.hasLab = hasLab;
		this.labList = new ArrayList<>();
		this.professor = null;
	}

	public Course(String courseNumber, String prefix, String courseName, String gradType, String courseType) {
		super(courseNumber, "no location");
		this.prefix = prefix;
		this.courseName = courseName;
		this.gradType = gradType;
		this.courseType = courseType;
		this.labList = new ArrayList<>();
		this.hasLab = "no";
		this.professor = null;
	}

	public String getProfessor() {
		return professor;
	}

	public void setProfessor(String professor) {
		this.professor = professor;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getGradType() {
		return gradType;
	}

	public void setGradType(String gradType) {
		this.gradType = gradType;
	}

	public String getCourseType() {
		return courseType;
	}

	public void setCourseType(String courseType) {
		this.courseType = courseType;
	}

	public String getHasLab() {
		return hasLab;
	}

	public void setHasLab(String hasLab) {
		this.hasLab = hasLab;
	}

	public ArrayList<Lab> getLabList() {
		return labList;
	}

	public void setLabList(ArrayList<Lab> labList) {
		this.labList = labList;
	}

	public void addLab(Lab lab) {
		this.labList.add(lab);
	}

	public void removeLab(Lab lab) {
		this.labList.remove(lab);
	}

	public void deletionPrint() {
		System.out.print(String.format("[%s/%s/%s]", this.getCourseNumber(), this.getPrefix(), this.getCourseName()));
	}

	@Override
	public String toString() {
		if (this.courseType.equalsIgnoreCase("online")) {
			return String.format("%s,%s,%s,%s,%s", getCourseNumber(), this.prefix, this.courseName, this.gradType,
					this.courseType);
		} else {
			if (this.hasLab.equalsIgnoreCase("yes")) {
				StringBuilder sb = new StringBuilder();
				sb.append(String.format("%s,%s,%s,%s,%s,%s,%s", getCourseNumber(), this.prefix, this.courseName,
						this.gradType, this.courseType, getCourseLocation(), this.hasLab));
				for (int i = 0; i < labList.size(); i++) {
					sb.append("\n");
					sb.append(labList.get(i).toString());
				}
				return sb.toString();
			} else {
				return String.format("%s,%s,%s,%s,%s,%s,%s", getCourseNumber(), this.prefix, this.courseName,
						this.gradType, this.courseType, getCourseLocation(), this.hasLab);
			}
		}
	}
}
