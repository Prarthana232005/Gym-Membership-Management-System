
import java.util.*;
import java.time.*;

class MemberNotFoundException extends Exception {
    MemberNotFoundException(String message) {
        super(message);
    }
}

class MembershipExpiredException extends Exception {
    MembershipExpiredException(String message) {
        super(message);
    }
}

abstract class MembershipPlan {
    abstract String getName();
    abstract int getFee();
    abstract int getDuration();
}

class BasicPlan extends MembershipPlan {
    String getName() {
        return "Basic";
    }

    int getFee() {
        return 1000;
    }

    int getDuration() {
        return 1;
    }
}

class StandardPlan extends MembershipPlan {
    String getName() {
        return "Standard";
    }

    int getFee() {
        return 2500;
    }

    int getDuration() {
        return 3;
    }
}

class PremiumPlan extends MembershipPlan {
    String getName() {
        return "Premium";
    }

    int getFee() {
        return 4500;
    }

    int getDuration() {
        return 6;
    }
}

class Member {
    private int id;
    private String name;
    private int age;
    private String phone;
    private MembershipPlan plan;
    private LocalDate expiryDate;

    Member(int id, String name, int age, String phone) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.phone = phone;
    }

    int getId() {
        return id;
    }

    String getName() {
        return name;
    }

    int getAge() {
        return age;
    }

    boolean hasPlan() {
        return plan != null;
    }

    boolean isActive() {
        return plan != null && !expiryDate.isBefore(LocalDate.now());
    }

    void selectPlan(MembershipPlan plan) {
        this.plan = plan;
        expiryDate = LocalDate.now().plusMonths(plan.getDuration());
    }

    void renewPlan() {
        if (expiryDate.isBefore(LocalDate.now())) {
            expiryDate = LocalDate.now();
        }

        expiryDate = expiryDate.plusMonths(plan.getDuration());
    }

    String getDetails() {
        return "ID: " + id +
                " | Name: " + name +
                " | Age: " + age +
                " | Phone: " + phone +
                " | Plan: " + (plan == null ? "Not Selected" : plan.getName()) +
                " | Expiry: " + (expiryDate == null ? "N/A" : expiryDate);
    }
}

class Trainer {
    private int id;
    private String name;
    private String specialization;

    Trainer(int id, String name, String specialization) {
        this.id = id;
        this.name = name;
        this.specialization = specialization;
    }

    int getId() {
        return id;
    }

    String getName() {
        return name;
    }

    void display() {
        System.out.println("Trainer ID: " + id +
                " | Name: " + name +
                " | Specialization: " + specialization);
    }
}

class GymManager {
    ArrayList<Member> members = new ArrayList<>();
    ArrayList<Trainer> trainers = new ArrayList<>();
    HashMap<Integer, String> trainerAssignments = new HashMap<>();

    class Node {
        String data;
        Node next;

        Node(String data) {
            this.data = data;
            this.next = null;
        }
    }

    Node head = null;

    void addMember(Member member) {
        if (member.getAge() < 18) {
            System.out.println("Member must be at least 18 years old!");
            return;
        }

        for (Member m : members) {
            if (m.getId() == member.getId()) {
                System.out.println("Member ID already exists!");
                return;
            }
        }

        members.add(member);
        System.out.println("Member registered successfully!");
    }

    Member searchMember(int id) throws MemberNotFoundException {
        for (Member m : members) {
            if (m.getId() == id) {
                return m;
            }
        }

        throw new MemberNotFoundException("Member not found!");
    }

    void viewMembers() {
        if (members.isEmpty()) {
            System.out.println("No members registered.");
            return;
        }

        for (Member m : members) {
            System.out.println(m.getDetails());
            System.out.println("Trainer: " +
                    trainerAssignments.getOrDefault(m.getId(), "Not Assigned"));
            System.out.println("--------------------------------");
        }
    }

    void viewPlans() {
        System.out.println("\n===== MEMBERSHIP PLANS =====");
        System.out.println("1. Basic Plan    - Rs.1000  - 1 Month");
        System.out.println("2. Standard Plan - Rs.2500  - 3 Months");
        System.out.println("3. Premium Plan  - Rs.4500  - 6 Months");
    }

    void selectPlan(int id, int choice)
            throws MemberNotFoundException {
        Member m = searchMember(id);

        MembershipPlan plan;

        if (choice == 1) {
            plan = new BasicPlan();
        } else if (choice == 2) {
            plan = new StandardPlan();
        } else if (choice == 3) {
            plan = new PremiumPlan();
        } else {
            System.out.println("Invalid plan choice!");
            return;
        }

        m.selectPlan(plan);
        System.out.println(plan.getName() + " plan selected successfully!");
        System.out.println("Fee: Rs." + plan.getFee());
    }

    void renewMembership(int id)
            throws MemberNotFoundException {
        Member m = searchMember(id);

        if (!m.hasPlan()) {
            System.out.println("Please select a membership plan first!");
            return;
        }

        m.renewPlan();
        System.out.println("Membership renewed successfully!");
    }

    void markAttendance(int id)
            throws MemberNotFoundException,
            MembershipExpiredException {
        Member m = searchMember(id);

        if (!m.isActive()) {
            throw new MembershipExpiredException(
                    "Membership expired or plan not selected!"
            );
        }

        String record = m.getName() + " - " + LocalDate.now();

        Node newNode = new Node(record);

        if (head == null) {
            head = newNode;
        } else {
            Node temp = head;

            while (temp.next != null) {
                temp = temp.next;
            }

            temp.next = newNode;
        }

        System.out.println("Attendance marked successfully!");
    }

    void viewAttendance() {
        if (head == null) {
            System.out.println("No attendance records found.");
            return;
        }

        Node temp = head;

        System.out.println("\n===== ATTENDANCE RECORDS =====");

        while (temp != null) {
            System.out.println(temp.data);
            temp = temp.next;
        }
    }

    void addTrainer(Trainer trainer) {
        for (Trainer t : trainers) {
            if (t.getId() == trainer.getId()) {
                System.out.println("Trainer ID already exists!");
                return;
            }
        }

        trainers.add(trainer);
        System.out.println("Trainer added successfully!");
    }

    void allotTrainer(int memberId, int trainerId)
            throws MemberNotFoundException {
        searchMember(memberId);

        for (Trainer t : trainers) {
            if (t.getId() == trainerId) {
                trainerAssignments.put(memberId, t.getName());
                System.out.println("Trainer allotted successfully!");
                return;
            }
        }

        System.out.println("Trainer not found!");
    }

    void viewTrainers() {
        if (trainers.isEmpty()) {
            System.out.println("No trainers available.");
            return;
        }

        System.out.println("\n===== TRAINER DETAILS =====");

        for (Trainer t : trainers) {
            t.display();
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        GymManager gym = new GymManager();

        while (true) {
            System.out.println("\n===== GYM MANAGEMENT SYSTEM =====");
            System.out.println("1. Register New Member");
            System.out.println("2. View All Members");
            System.out.println("3. View Membership Plans");
            System.out.println("4. Select Membership Plan");
            System.out.println("5. Renew Membership");
            System.out.println("6. Mark Attendance");
            System.out.println("7. View Attendance");
            System.out.println("8. Add Trainer");
            System.out.println("9. Allot Trainer");
            System.out.println("10. View Trainers");
            System.out.println("11. Exit");

            System.out.print("\nEnter your choice: ");
            int choice = sc.nextInt();
            sc.nextLine();

            try {
                switch (choice) {

                    case 1:
                        System.out.print("Enter Member ID: ");
                        int id = sc.nextInt();
                        sc.nextLine();

                        System.out.print("Enter Name: ");
                        String name = sc.nextLine();

                        System.out.print("Enter Age: ");
                        int age = sc.nextInt();
                        sc.nextLine();

                        System.out.print("Enter Phone Number: ");
                        String phone = sc.nextLine();

                        gym.addMember(new Member(id, name, age, phone));
                        break;

                    case 2:
                        gym.viewMembers();
                        break;

                    case 3:
                        gym.viewPlans();
                        break;

                    case 4:
                        System.out.print("Enter Member ID: ");
                        int mid = sc.nextInt();

                        gym.viewPlans();

                        System.out.print("Choose Plan: ");
                        int plan = sc.nextInt();

                        gym.selectPlan(mid, plan);
                        break;

                    case 5:
                        System.out.print("Enter Member ID: ");
                        int rid = sc.nextInt();

                        gym.renewMembership(rid);
                        break;

                    case 6:
                        System.out.print("Enter Member ID: ");
                        int aid = sc.nextInt();

                        gym.markAttendance(aid);
                        break;

                    case 7:
                        gym.viewAttendance();
                        break;

                    case 8:
                        System.out.print("Enter Trainer ID: ");
                        int tid = sc.nextInt();
                        sc.nextLine();

                        System.out.print("Enter Trainer Name: ");
                        String tname = sc.nextLine();

                        System.out.print("Enter Specialization: ");
                        String specialization = sc.nextLine();

                        gym.addTrainer(
                                new Trainer(tid, tname, specialization)
                        );
                        break;

                    case 9:
                        System.out.print("Enter Member ID: ");
                        int memberId = sc.nextInt();

                        System.out.print("Enter Trainer ID: ");
                        int trainerId = sc.nextInt();

                        gym.allotTrainer(memberId, trainerId);
                        break;

                    case 10:
                        gym.viewTrainers();
                        break;

                    case 11:
                        System.out.println("Thank you for using Gym Management System!");
                        sc.close();
                        return;

                    default:
                        System.out.println("Invalid choice!");
                }

            } catch (MemberNotFoundException |
                     MembershipExpiredException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}
