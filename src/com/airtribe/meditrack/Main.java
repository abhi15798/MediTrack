package com.airtribe.meditrack;

import com.airtribe.meditrack.entity.*;
import com.airtribe.meditrack.exception.CancelInputException;
import com.airtribe.meditrack.exception.EntityNotFoundException;
import com.airtribe.meditrack.exception.InvalidDataException;
import com.airtribe.meditrack.exception.SystemExitException;
import com.airtribe.meditrack.interfaces.BillingStrategy;
import com.airtribe.meditrack.interfaces.PaymentStrategy;
import com.airtribe.meditrack.service.AppointmentService;
import com.airtribe.meditrack.service.BillService;
import com.airtribe.meditrack.service.DoctorService;
import com.airtribe.meditrack.service.PatientService;
import com.airtribe.meditrack.util.*;

import javax.print.Doc;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.airtribe.meditrack.constants.AppConstants.*;
import static com.airtribe.meditrack.constants.MenuOptions.*;

public class Main {

    private static final PatientService patientService = new PatientService();
    private static final DoctorService doctorService = new DoctorService();
    private static final BillService billService = new BillService();
    private static final AppointmentService appointmentService = new AppointmentService(doctorService, patientService);

    public static void main(String[] args) {

        System.out.println("MediTrack Application started successfully!");
        System.out.println();

        Doctor doctor1 = doctorService.createDoctor("Dr. Abhi","9876545677", Specialization.CARDIOLOGY,500.00);
        Doctor doctor2 = doctorService.createDoctor("Dr. Iyer", "9123456780", Specialization.GENERAL_PHYSICIAN, 650.00);
        Doctor doctor3 = doctorService.createDoctor("Dr. Karthik", "9874444441", Specialization.DERMATOLOGY, 600.00);
        Doctor doctor4 = doctorService.createDoctor("Dr. Lakshmi", "9874444442", Specialization.ORTHOPEDICS, 600.00);
        Doctor doctor5 = doctorService.createDoctor("Dr. Rohini", "9874444443", Specialization.PEDIATRICS, 550.00);

        System.out.println("created : "+ doctor1.getId() +","+ doctor2.getId());

        Patient patient1 = patientService.createPatient("Zoom","9876543456", LocalDate.of(1990, 5, 14));
        Patient patient2 = patientService.createPatient("Mike","9876354535", LocalDate.of(2000, 5, 14));

        System.out.println("created Patient : "+ patient1.getId() +","+ patient2.getId());

        for (Doctor d: doctorService.getAllDoctors()){
            System.out.println("Doctor name "+ d.getName() + " Specialization "+ d.getSpecialization());
        }

        // 1. Define the list of doctor IDs
        List<String> doctorIds = Arrays.asList(doctor1.getId(), doctor2.getId(), doctor3.getId(), doctor4.getId(),
                doctor5.getId());

        // 2. Define working hours limits
        LocalTime workingStart = LocalTime.of(10, 0); // 10:00 AM
        LocalTime workingEnd = LocalTime.of(17, 0);   // 05:00 PM

        // 3. Get the current hour's timestamp
        LocalDateTime currentHourStart = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);

        // 4. Determine the dynamic loop start time based on your 10:00 AM logic
        LocalDateTime dynamicStart;
        if (currentHourStart.toLocalTime().isBefore(workingStart)) {
            // Current time is before 10 AM: start from the current hour today
            dynamicStart = currentHourStart;
        } else {
            // Current time is 10 AM or later: jump straight to 10 AM tomorrow
            dynamicStart = currentHourStart.plusDays(1).with(workingStart);
        }

        // 5. Calculate the end boundary (exactly 2 days from our dynamic start point)
        LocalDateTime endLimit = dynamicStart.plusDays(2);

        // 6. Run the loops for each doctor
        for (String doctorId : doctorIds) {
            LocalDateTime slotStart = dynamicStart;

            while (slotStart.isBefore(endLimit)) {
                LocalDateTime slotEnd = slotStart.plusMinutes(30);

                LocalTime slotStartTime = slotStart.toLocalTime();
                LocalTime slotEndTime = slotEnd.toLocalTime();

                // FIX: Verify the slot stays within the same calendar day AND fits inside 10:00 AM - 5:00 PM
                boolean isSameDay = slotStart.toLocalDate().equals(slotEnd.toLocalDate());
                boolean startsAfterOrAtWorkStart = !slotStartTime.isBefore(workingStart);
                boolean endsBeforeOrAtWorkEnd = !slotEndTime.isAfter(workingEnd) && !slotEndTime.equals(LocalTime.MIDNIGHT);

                if (isSameDay && startsAfterOrAtWorkStart && endsBeforeOrAtWorkEnd) {
                    doctorService.addAvailability(doctorId, slotStart, slotEnd);
                }

                slotStart = slotEnd;
            }
        }

//        doctorService.addAvailability("DOC-0001", LocalDateTime.of(2026, 9, 9, 10, 0),LocalDateTime.of(2026, 9, 9, 10, 30));
//        doctorService.addAvailability("DOC-0001", LocalDateTime.of(2026, 9, 9, 10, 30),LocalDateTime.of(2026, 9, 9, 11, 00));

//        for(TimeSlot ts1 : doctor1.getTimeslots()) {
//            System.out.println("Available slots: "+ ts1.getStartTime() + "Slot id= "+ ts1.getId());
//        }
//
        Appointment bookedAppointment = appointmentService.bookAppointment("DOC-0001","PAT-0002","TS-0001");
        System.out.println("Appointment Booked :" + bookedAppointment.getPatientId());

        Scanner scanner = new Scanner(System.in);
        int choice = 0;

        try {
            do {
                displayMainMenu();
                choice = getUserChoice(scanner);
                // Pass the scanner into the execution router to handle sub-menus
                executeMainAction(choice, scanner);
            } while (choice != 6);
        } catch (SystemExitException e) {
            System.out.println(e);
        } finally {
            scanner.close();
            System.out.println("Program terminated safely.");
        }

    }

    private static int getUserChoice(Scanner scanner) {
        try {
            return scanner.nextInt();
        } catch (InputMismatchException e) {
            scanner.nextLine();
            return -1;
        }
    }

    private static void displayMainMenu() {
        System.out.println(EQUALS);
        System.out.println(MAIN_MENU);
        System.out.println(EQUALS);
        System.out.println(BILLING_MANAG_MENU);
        System.out.println(DOCTOR_MANAG_MENU);
        System.out.println(PATIENT_MANAG_MENU);
        System.out.println(APPOINTMENT_MANAG_MENU);
        System.out.println(AI_ASSISTANT);
        System.out.println(EXIT_APPLICATION);
        System.out.print("Please enter your choice" + " (1-6): ");
    }

    private static void executeMainAction(int choice, Scanner scanner) {
        System.out.println();
        switch (choice) {
            case 1:
                handleBillingManagementSubMenu(scanner);
                break;
            case 2:
                handleDoctorManagementSubMenu(scanner);
                break;
            case 3:
                handlePatientManagementSubMenu(scanner);
                break;
            case 4:
                handleAppointmentManagementSubMenu(scanner);
                break;
            case 5:
                getAIHelp();
                break;
            case 6:
                System.out.println(THANKYOU_GOODBYE);
                break;
            default:
                System.out.println("Error: Invalid entry. Please enter a number between " + "1 and 6.");
                break;
        }
    }

    private static void getAIHelp() {
        System.out.println("Welcome to the AI Assistant!");
        System.out.println("Please describe your symptoms or medical concerns in detail.");
        System.out.println("The AI will analyze your input and suggest a suitable doctor specialization.");
        System.out.println("Type 'exit' to return to the main menu.");

        Scanner scanner = new Scanner(System.in);
        String userInput = null;

        do {
            System.out.print("\nEnter your symptoms or concerns: ");
            userInput = readSafeInput(scanner);
        } while (!Validator.isNotBlank(userInput));

        AIHelper aiHelper = AIHelper.getInstance();
        List<String> validSpecialities = Arrays.stream(Specialization.values())
                .map(Specialization::getDisplayName)
                .toList();

        String suggestedSpecialization = aiHelper.classifySpecialty(userInput, validSpecialities);
        System.out.println(suggestedSpecialization);

        try {
            System.out.println();
            List<Doctor> recommendedDoctors = doctorService.filterBySpecialization(Specialization.fromDisplayName(suggestedSpecialization));
            if (recommendedDoctors == null || recommendedDoctors.isEmpty()) {
                System.out.println("Sorry! No doctors available in the suggested specialization at this moment.");
                return;
            }

            boolean hasAvailableSlots = recommendedDoctors.stream()
                    .anyMatch(doctor -> doctor.getTimeslots() != null && !doctor.getTimeslots().isEmpty() &&
                            doctor.getTimeslots().stream().anyMatch(ts -> ts.getStatus() == SlotStatus.AVAILABLE));

            if (!hasAvailableSlots) {
                System.out.println("Sorry! No time slots available for doctors under this specialization at this moment.");
                return;
            }

            System.out.println("Available doctors in the suggested specialization: ");
            for (Doctor doctor : recommendedDoctors) {
                if (doctor.getTimeslots() != null && !doctor.getTimeslots().isEmpty() && doctor.getTimeslots().stream().anyMatch(ts -> ts.getStatus() == SlotStatus.AVAILABLE)) {
                    System.out.println(doctor.getName());
                    System.out.println();
                    System.out.println(EQUALS + " Available time slots: " + EQUALS);
                    for (TimeSlot timeSlot : doctor.getTimeslots()) {
                        if (timeSlot.getStatus() == SlotStatus.AVAILABLE) {
                            System.out.println(timeSlot.getStartTime() + " - " + timeSlot.getEndTime());
                        }
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            System.out.println(EQUALS + "Error: " + e.getMessage() + " " + EQUALS);
        }

    }

    private static void handleBillingManagementSubMenu(Scanner scanner) {
        int subChoice = 0;

        // Sub-menu loop continues until user inputs the 'Go Back' option (3)
        do {
            System.out.println("\n" + DASHES + BILLING_MANAG_SUBMENU + DASHES);
            System.out.println(GENERATE_BILL);
            System.out.println(GENERATE_BILL_APPNT_ID);
            System.out.println(VIEW_BILL_SUMMARY);
            System.out.println("4. " + GO_BACK_TO_MAIN_MENU);
            System.out.print(ENTER_CHOICE + " (1-4): ");

            subChoice = getUserChoice(scanner);
            System.out.println();

            switch (subChoice) {
                case 1:
                    scanner.nextLine();
                    try {
                        generateBill(scanner);
                    } catch (CancelInputException e) {
                        System.out.println(e);
                    }
                    break;
                case 2:
                    scanner.nextLine();
                    try {
                        generateBillFromApntId(scanner);
                    } catch (CancelInputException e) {
                        System.out.println(e);
                    }
                    break;
                case 3:
                    scanner.nextLine();
                    viewBillSummary(scanner);
                    break;
                case 4:
                    System.out.println(RETURNING_MAIN_MENU);
                    break;
                default:
                    System.out.println(INVALID_ENTRY + "1 and 4.");
                    break;
            }
        } while (subChoice != 4);
    }

    private static void handleDoctorManagementSubMenu(Scanner scanner) {
        int subChoice = 0;

        // Sub-menu loop continues until user inputs the 'Go Back' option (3)
        do {
            System.out.println("\n" + DASHES + DOCTOR_MANAG_SUBMENU + DASHES);
            System.out.println(CREATE_DOCTOR);
            System.out.println(SEARCH_DOCTOR_BY_ID);
            System.out.println(SEARCH_DOCTOR_BY_NAME);
            System.out.println(VIEW_DOCTORS);
            System.out.println(UPDATE_DOCTOR_FEE);
            System.out.println(UPDATE_DOCTOR_CONTACT);
            System.out.println(DELETE_DOCTOR);
            System.out.println("8. " + GO_BACK_TO_MAIN_MENU);
            System.out.print(ENTER_CHOICE + " (1-8): ");

            subChoice = getUserChoice(scanner);
            System.out.println();

            switch (subChoice) {
                case 1:
                    scanner.nextLine();
                    try {
                        createDoctor(scanner);
                    } catch (CancelInputException e) {
                        System.out.println(e);
                    }
                    break;
                case 2:
                    scanner.nextLine();
                    try {
                        searchDoctorById(scanner);
                    } catch (CancelInputException | EntityNotFoundException e) {
                        System.out.println(e);
                    }
                    break;
                case 3:
                    scanner.nextLine();
                    try {
                        searchDoctorByName(scanner);
                    } catch (CancelInputException | EntityNotFoundException e) {
                        System.out.println(e);
                    }
                    break;
                case 4:
                    try {
                        viewAllDoctors();
                    } catch (EntityNotFoundException e) {
                        System.out.println(e);
                    }
                    break;
                case 5:
                    scanner.nextLine();
                    try {
                        updateDoctorFee(scanner);
                    } catch (CancelInputException | InvalidDataException e) {
                        System.out.println(e);
                    }
                    break;
                case 6:
                    scanner.nextLine();
                    try {
                        updateDoctorContact(scanner);
                    } catch (CancelInputException | InvalidDataException e) {
                        System.out.println(e);
                    }
                    break;
                case 7:
                    scanner.nextLine();
                    try {
                        deleteDoctor(scanner);
                    } catch (CancelInputException | InvalidDataException e) {
                        System.out.println(e);
                    }
                    break;
                case 8:
                    System.out.println(RETURNING_MAIN_MENU);
                    break;
                default:
                    System.out.println(INVALID_ENTRY + "1 and 8.");
                    break;
            }
        } while (subChoice != 8);
    }

    private static void viewAllDoctors() {
        List<Doctor> doctors = doctorService.findAllDoctors();
        if (doctors.isEmpty()) {
            throw new EntityNotFoundException("No doctors found.");
        } else {
            System.out.println();
            System.out.println(EQUALS + "Doctor Details" + EQUALS);
            for (Doctor doctor : doctors) {
                System.out.println("Id: " + doctor.getId() + ", Name: " + doctor.getName() + ", Specialization: " + doctor.getSpecialization() + ", Fee: " + doctor.getFee() + ", Contact: " + doctor.getContact());
            }
        }
    }

    private static void deleteDoctor(Scanner scanner) {
        String doctorId = null;
        do {
            System.out.print("Enter Doctor Id: ");
            doctorId = readSafeInput(scanner);
        } while (!Validator.isValidDoctorId(doctorId));
        doctorService.deleteDoctor(doctorId);
        System.out.println();
        System.out.println(EQUALS + "Doctor deletion successful" + EQUALS);
    }

    private static void updateDoctorContact(Scanner scanner) {
        String doctorId = null;
        do {
            System.out.print("Enter Doctor Id: ");
            doctorId = readSafeInput(scanner);
        } while (!Validator.isValidDoctorId(doctorId));

        String newContact = null;
        do {
            System.out.print("Enter new contact number: ");
            newContact = readSafeInput(scanner);
        } while (!Validator.isValidContact(newContact));
        doctorService.updateContact(doctorId, newContact);
        System.out.println();
        System.out.println(EQUALS + "Doctor's contact information updated successfully." + EQUALS);
    }

    private static void updateDoctorFee(Scanner scanner) {
        String doctorId = null;
        do {
            System.out.print("Enter Doctor Id: ");
            doctorId = readSafeInput(scanner);
        } while (!Validator.isValidDoctorId(doctorId));

        String newFeeStr = null;
        do {
            System.out.print("Enter new consultation fee: ");
            newFeeStr = readSafeInput(scanner);
        } while (!Validator.isValidAmount(newFeeStr));
        double newFee = Double.parseDouble(newFeeStr);
        doctorService.updateFee(doctorId, newFee);
        System.out.println();
        System.out.println(EQUALS + "Doctor's consultation fee updated successfully." + EQUALS);
    }

    private static void searchDoctorByName(Scanner scanner) {
        String doctorName = null;
        do {
            System.out.print("Enter Doctor Name: ");
            doctorName = readSafeInput(scanner);
        } while (!Validator.isValidName(doctorName));
        List<Doctor> doctors = doctorService.search("Dr. " + doctorName);
        if (doctors != null && !doctors.isEmpty()) {
            System.out.println();
            System.out.println(EQUALS + "Doctor Details" + EQUALS);
            for (Doctor doctor : doctors) {
                System.out.println("Id: " + doctor.getId() + ", Name: " + doctor.getName() + ", Specialization: " + doctor.getSpecialization() + ", Fee: " + doctor.getFee() + ", Contact: " + doctor.getContact());
            }
        } else {
            throw new EntityNotFoundException(" Doctor with Name " + doctorName + " not found ");
        }
    }

    private static void searchDoctorById(Scanner scanner) {
        String doctorId = null;
        do {
            System.out.print("Enter Doctor Id: ");
            doctorId = readSafeInput(scanner);
        } while (!Validator.isValidDoctorId(doctorId));
        Doctor doctor = doctorService.searchById(doctorId);
        System.out.println();
        System.out.println(EQUALS + "Doctor Details" + EQUALS);
        System.out.println("Id: " + doctor.getId() + ", Name: " + doctor.getName() + ", Specialization: " + doctor.getSpecialization() + ", Fee: " + doctor.getFee() + ", Contact: " + doctor.getContact());
    }

    private static void handlePatientManagementSubMenu(Scanner scanner) {
        int subChoice = 0;

        // Sub-menu loop continues until user inputs the 'Go Back' option (3)
        do {
            System.out.println("\n" + DASHES + PATIENT_MANAG_SUBMENU + DASHES);
            System.out.println(CREATE_PATIENT);
            System.out.println(SEARCH_PATIENT_BY_ID);
            System.out.println(SEARCH_PATIENT_BY_NAME);
            System.out.println(VIEW_PATIENTS);
            System.out.println(UPDATE_PATIENT_CONTACT);
            System.out.println(DELETE_PATIENT);
            System.out.println("7. " + GO_BACK_TO_MAIN_MENU);
            System.out.print(ENTER_CHOICE + " (1-7): ");

            subChoice = getUserChoice(scanner);
            System.out.println();

            switch (subChoice) {
                case 1:
                    scanner.nextLine();
                    try {
                        createPatient(scanner);
                    } catch (CancelInputException e) {
                        System.out.println(e);
                    }
                    break;
                case 2:
                    scanner.nextLine();
                    try {
                        searchPatientById(scanner);
                    } catch (CancelInputException | InvalidDataException e) {
                        System.out.println(e);
                    }
                    break;
                case 3:
                    scanner.nextLine();
                    try {
                        searchPatientByName(scanner);
                    } catch (CancelInputException | EntityNotFoundException e) {
                        System.out.println(e);
                    }
                    break;
                case 4:
                    try {
                        viewAllPatients();
                    } catch (EntityNotFoundException e) {
                        System.out.println(e);
                    }
                    break;
                case 5:
                    scanner.nextLine();
                    try {
                        updatePatientContact(scanner);
                    } catch (CancelInputException | InvalidDataException e) {
                        System.out.println(e);
                    }
                    break;
                case 6:
                    scanner.nextLine();
                    try {
                        deletePatient(scanner);
                    } catch (CancelInputException | InvalidDataException e) {
                        System.out.println(e);
                    }
                    break;
                case 7:
                    System.out.println(RETURNING_MAIN_MENU);
                    break;
                default:
                    System.out.println(INVALID_ENTRY + "1 and 7.");
                    break;
            }
        } while (subChoice != 7);
    }

    private static void deletePatient(Scanner scanner) {
        String patientId = null;
        do {
            System.out.print("Enter Patient Id: ");
            patientId = readSafeInput(scanner);
        } while (!Validator.isValidPatientId(patientId));
        patientService.deletePatient(patientId);
        System.out.println();
        System.out.println(EQUALS + "Patient deletion successful" + EQUALS);
    }

    private static void updatePatientContact(Scanner scanner) {
        String patientId = null;
        do {
            System.out.print("Enter Patient Id: ");
            patientId = readSafeInput(scanner);
        } while (!Validator.isValidPatientId(patientId));

        String newContact = null;
        do {
            System.out.print("Enter new contact number: ");
            newContact = readSafeInput(scanner);
        } while (!Validator.isValidContact(newContact));
        patientService.updateContact(patientId, newContact);
        System.out.println();
        System.out.println(EQUALS + "Patient's contact information updated successfully" + EQUALS);
    }

    private static void viewAllPatients() {
        List<Patient> patients = patientService.findAllPatients();
        if (patients.isEmpty()) {
            throw new EntityNotFoundException("No patients found.");
        } else {
            System.out.println();
            System.out.println(EQUALS + "Patient Details" + EQUALS);
            for (Patient patient : patients) {
                System.out.println(patient);
            }
        }
    }

    private static void searchPatientByName(Scanner scanner) {
        String patientName = null;
        do {
            System.out.print("Enter Patient Name: ");
            patientName = readSafeInput(scanner);
        } while (!Validator.isValidName(patientName));
        List<Patient> patients = patientService.search(patientName);
        if (patients != null && !patients.isEmpty()) {
            System.out.println();
            System.out.println(EQUALS + "Patient Details" + EQUALS);
            System.out.println(patients);
        } else {
            throw new EntityNotFoundException(" Patient with Name " + patientName + " not found ");
        }
    }

    private static void searchPatientById(Scanner scanner) {
        String patientId = null;
        do {
            System.out.print("Enter Patient Id: ");
            patientId = readSafeInput(scanner);
        } while (!Validator.isValidPatientId(patientId));
        Patient patient = patientService.searchById(patientId);
        System.out.println();
        System.out.println(EQUALS + "Patient Details" + EQUALS);
        System.out.println(patient);
    }

    private static void handleAppointmentManagementSubMenu(Scanner scanner) {
        int subChoice = 0;

        // Sub-menu loop continues until user inputs the 'Go Back' option (3)
        do {
            System.out.println("\n" + DASHES + APPOINTMENT_MANAG_SUBMENU + DASHES);
            System.out.println(CREATE_APPOINTMENT);
            System.out.println(VIEW_APPOINTMENT);
            System.out.println(VIEW_APPOINTMENTS);
            System.out.println(CANCEL_APPOINTMENT);
            System.out.println(VIEW_DOCTOR_AVAILABILITY);
            System.out.println("6. " + GO_BACK_TO_MAIN_MENU);
            System.out.print(ENTER_CHOICE + " (1-6): ");

            subChoice = getUserChoice(scanner);
            System.out.println();

            switch (subChoice) {
                case 1:
                    scanner.nextLine();
                    try {
                        createAppointment(scanner);
                    } catch (CancelInputException | InvalidDataException | IllegalStateException e) {
                        System.out.println(e);
                    }
                    break;
                case 2:
                    scanner.nextLine();
                    try {
                        searchAppointmentById(scanner);
                    } catch (CancelInputException | EntityNotFoundException e) {
                        System.out.println(e);
                    }
                    break;
                case 3:
                    try {
                        viewAllAppointments();
                    } catch (EntityNotFoundException e) {
                        System.out.println(e);
                    }
                    break;
                case 4:
                    scanner.nextLine();
                    try {
                        cancelAppointment(scanner);
                    } catch (CancelInputException | InvalidDataException e) {
                        System.out.println(e);
                    }
                    break;
                case 5:
                    scanner.nextLine();
                    viewDoctorAvailability(scanner);
                    break;
                case 6:
                    System.out.println(RETURNING_MAIN_MENU);
                    break;
                default:
                    System.out.println(INVALID_ENTRY + "1 and 6.");
                    break;
            }
        } while (subChoice != 6);
    }

    private static void cancelAppointment(Scanner scanner) {
        String aptId = null;
        do {
            System.out.print("Enter Appointment Id: ");
            aptId = readSafeInput(scanner);
        } while (!Validator.isValidAppointmentId(aptId));
        appointmentService.cancelAppointment(aptId);
        System.out.println();
        System.out.println(EQUALS + "Appointment with ID " + aptId + " has been successfully canceled." + EQUALS);
    }

    private static void searchAppointmentById(Scanner scanner) {
        String aptId = null;
        do {
            System.out.print("Enter Appointment Id: ");
            aptId = readSafeInput(scanner);
        } while (!Validator.isValidAppointmentId(aptId));

        String finalAptId = aptId;
        Optional<Appointment> optionalAppointment = appointmentService.searchById(aptId);
        System.out.println();
        System.out.println(EQUALS + "Appointment Details" + EQUALS);
        optionalAppointment.ifPresentOrElse(System.out::println, () -> {
                    throw new EntityNotFoundException(" Appointment with ID " + finalAptId + " not found ");
                }
        );
    }

    private static void viewAllAppointments() {
        List<Appointment> appointments = appointmentService.findAllAppointments();
        if (appointments.isEmpty()) {
            throw new EntityNotFoundException("No appointments found.");
        } else {
            System.out.println();
            System.out.println(EQUALS + "Appointment Details" + EQUALS);
            for (Appointment appointment : appointments) {
                System.out.println(appointment);
            }
        }
    }

    private static void viewDoctorAvailability(Scanner scanner) {
        String doctorId = null;
        do {
            System.out.print("Enter Doctor Id to check availability for: ");
            doctorId = readSafeInput(scanner);
        } while (!Validator.isValidDoctorId(doctorId));
        checkDoctorAvailability(doctorId);
    }

    private static void checkDoctorAvailability(String doctorId) {
        Doctor doctor = doctorService.searchById(doctorId);
        if (doctor != null) {
            System.out.println();
            System.out.println("Available time slots for " + doctor.getName() + ": ");
            System.out.println(DASHES + DASHES + DASHES + DASHES + DASHES + DASHES + DASHES + DASHES + DASHES + DASHES + DASHES + DASHES + DASHES);
            int i = 1;
            for (int j = 0; j < doctor.getTimeslots().size(); j++) {
                TimeSlot timeSlot = doctor.getTimeslots().get(j);
                System.out.print(timeSlot + "   |   ");
                if (i % 3 == 0) {
                    System.out.println();
                    System.out.println(DASHES + DASHES + DASHES + DASHES + DASHES + DASHES + DASHES + DASHES + DASHES + DASHES + DASHES + DASHES + DASHES);
                }
                i++;
            }
            System.out.println();
        } else {
            throw new EntityNotFoundException(" Doctor with ID " + doctorId + " not found ");
        }
    }

    private static void createAppointment(Scanner scanner) {
        String patientId = null;
        do {
            System.out.print("Enter Patient ID: ");
            patientId = readSafeInput(scanner);
        } while (!Validator.isValidPatientId(patientId));

        String doctorId = null;
        do {
            System.out.print("Enter Doctor ID: ");
            doctorId = readSafeInput(scanner);
        } while (!Validator.isValidDoctorId(doctorId));

        checkDoctorAvailability(doctorId);

        String timeSlotId = null;
        do {
            System.out.println();
            System.out.print("Please enter Time Slot ID from above available slots: ");
            timeSlotId = readSafeInput(scanner);
        } while (!Validator.isValidTimeslotId(timeSlotId));

        Appointment appointment = appointmentService.bookAppointment(doctorId, patientId, timeSlotId);
        System.out.println();
        System.out.println("Appointment booked successfully! Appointment ID: " + appointment.getId());
    }

    private static void createPatient(Scanner scanner) {
        String patientName = null;
        do {
            System.out.print("Enter Patient's Name: ");
            patientName = readSafeInput(scanner);
        } while (!Validator.isValidName(patientName));

        String contact = null;
        do {
            System.out.print("Enter Patient's Contact Number: ");
            contact = readSafeInput(scanner);
        } while (!Validator.isValidContact(contact));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-uuuu")
                .withResolverStyle(ResolverStyle.STRICT);
        LocalDate dateOfBirth = null;

        // Loop until valid data is supplied
        while (dateOfBirth == null) {
            System.out.print("Enter Date of Birth in dd-MM-yyy format: ");
            String userInput = readSafeInput(scanner);

            try {
                // Attempt to parse the input string
                dateOfBirth = LocalDate.parse(userInput, formatter);
            } catch (DateTimeParseException e) {
                // Handle invalid formats or wrong dates (e.g., 30-02-2026)
                System.out.println(EQUALS + " Error: Invalid date or incorrect format. Please try again in this format: dd-MM-yyyy, example: 28-12-2026 " + EQUALS);
            }
        }

        Patient patient = patientService.createPatient(patientName, contact, dateOfBirth);
        System.out.println();
        System.out.println("Patient record created successfully! Patient ID: " + patient.getId());
    }

    private static void createDoctor(Scanner scanner) {
        String doctorName = null;
        do {
            System.out.print("Enter Doctor's Name: ");
            doctorName = readSafeInput(scanner);
        } while (!Validator.isValidName(doctorName));

        String contact = null;
        do {
            System.out.print("Enter Doctor's Contact Number: ");
            contact = readSafeInput(scanner);
        } while (!Validator.isValidContact(contact));

        int subChoice = 0;
        Specialization specialization = Specialization.GENERAL_PHYSICIAN;
        do {
            System.out.println("\n" + DASHES + SPECIALIZATION_MENU + DASHES);
            System.out.println(GENERAL_SPECIALIZATION);
            System.out.println(CARDIOLOGY_SPECIALIZATION);
            System.out.println(DERMATOLOGY_SPECIALIZATION);
            System.out.println(ORTHOPEDICS_SPECIALIZATION);
            System.out.println(PEDIATRICS_SPECIALIZATION);
            System.out.println("6. " + GO_BACK_TO_MAIN_MENU);
            System.out.print(ENTER_CHOICE + " (1-6): ");

            subChoice = getUserChoice(scanner);
            System.out.println();

            switch (subChoice) {
                case 1:
                    specialization = Specialization.GENERAL_PHYSICIAN;
                    break;
                case 2:
                    specialization = Specialization.CARDIOLOGY;
                    break;
                case 3:
                    specialization = Specialization.DERMATOLOGY;
                    break;
                case 4:
                    specialization = Specialization.ORTHOPEDICS;
                    break;
                case 5:
                    specialization = Specialization.PEDIATRICS;
                    break;
                case 6:
                    System.out.println(RETURNING_MAIN_MENU);
                    break;
                default:
                    System.out.println(INVALID_ENTRY + "1-6.");
                    break;
            }
        } while (!Validator.isValidPaymentChoice(subChoice));

        scanner.nextLine();
        String consultationAmountStr = null;
        do {
            System.out.print("Enter consultation charges: ");
            consultationAmountStr = readSafeInput(scanner);
        } while (!Validator.isValidAmount(consultationAmountStr));
        double consultationAmount = Double.parseDouble(consultationAmountStr);

        Doctor doctor = doctorService.createDoctor("Dr. " + doctorName, contact, specialization, consultationAmount);
        System.out.println();
        System.out.println("Doctor added successfully! Doctor ID: " + doctor.getId());
    }

    private static void viewBillSummary(Scanner scanner) {
        String billId = null;
        do {
            System.out.print("Enter Bill Id: ");
            billId = readSafeInput(scanner);
        } while (!Validator.isValidBillId(billId));
        Optional<Bill> optionalBill = billService.getBill(billId);
        System.out.println();
        optionalBill.ifPresentOrElse(
                bill -> bill.toSummary().printBillSummary(),
                () -> System.out.println("Bill record not found for entered Bill Id.")
        );
    }

    private static void generateBillFromApntId(Scanner scanner) {
        String appointmentId = null;
        Appointment appointment = null;
        do {
            System.out.print("Enter Appointment id: ");
            appointmentId = readSafeInput(scanner);
            try {
                Optional<Appointment> optionalAppointment = appointmentService.searchById(appointmentId);
                appointment = optionalAppointment.orElse(null);
            } catch (InvalidDataException e) {
                System.out.println(EQUALS + " Appointment record for id " + appointmentId + " not found! " + EQUALS);
                System.out.println(e);
            }
        } while (!Validator.isValidAppointmentId(appointmentId));

        if (appointment != null) {
            String patientId = appointment.getPatientId();
            Patient patient = patientService.searchById(patientId);

            String doctorId = appointment.getDoctorId();
            Doctor doctor = doctorService.searchById(doctorId);

            String medicineChargesStr = null;
            do {
                System.out.print("Enter medicine charges: ");
                medicineChargesStr = readSafeInput(scanner);
            } while (!Validator.isValidAmount(medicineChargesStr));
            double medicineCharges = Double.parseDouble(medicineChargesStr);

            String discountStr = null;
            do {
                System.out.print("Enter discount percentage: ");
                discountStr = readSafeInput(scanner);
            } while (!Validator.isValidDiscount(discountStr));
            double discount = Double.parseDouble(discountStr);
            BillingStrategy billingStrategy = discount > 0 ? new DiscountBillingStrategy(discount) : new StandardBillingStrategy();

            int subChoice = 0;
            PaymentStrategy paymentStrategy = new CashPaymentStrategy();
            do {
                System.out.println("\n" + DASHES + PAYMENT_MENU + DASHES);
                System.out.println(UPI_PAYMENT);
                System.out.println(CASH_PAYMENT);
                System.out.println(CARD_PAYMENT);
                System.out.println("4. " + GO_BACK_TO_MAIN_MENU);
                System.out.print(ENTER_CHOICE + " (1-4): ");
                subChoice = getUserChoice(scanner);
            } while (!Validator.isValidPaymentChoice(subChoice));

            switch (subChoice) {
                case 1:
                    paymentStrategy = new UPIPaymentStrategy();
                    break;
                case 2:
                    paymentStrategy = new CashPaymentStrategy();
                    break;
                case 3:
                    paymentStrategy = new CardPaymentStrategy();
                    break;
                case 4:
                    System.out.println(RETURNING_MAIN_MENU);
                    break;
                default:
                    System.out.println(INVALID_ENTRY + "1 and 4.");
                    break;
            }

            billService.generateBill(patient.getName(), doctor.getName(), doctor.getFee(), medicineCharges,
                    LocalDateTime.now(), billingStrategy, paymentStrategy);

        } else {
            System.out.println(EQUALS + " Appointment record for id " + appointmentId + " not found! " + EQUALS);
        }
    }

    private static void generateBill(Scanner scanner) {

        String patientName = null;
        do {
            System.out.print("Enter Patient's Name: ");
            patientName = readSafeInput(scanner);
        } while (!Validator.isValidFirstName(patientName));

        String doctorName = null;
        do {
            System.out.print("Enter Doctor's Name: ");
            doctorName = readSafeInput(scanner);
        } while (!Validator.isValidFirstName(doctorName));

        String consultationAmountStr = null;
        do {
            System.out.print("Enter consultation charges: ");
            consultationAmountStr = readSafeInput(scanner);
        } while (!Validator.isValidAmount(consultationAmountStr));
        double consultationAmount = Double.parseDouble(consultationAmountStr);

        String medicineChargesStr = null;
        do {
            System.out.print("Enter medicine charges: ");
            medicineChargesStr = readSafeInput(scanner);
        } while (!Validator.isValidAmount(medicineChargesStr));
        double medicineCharges = Double.parseDouble(medicineChargesStr);

        String discountStr = null;
        do {
            System.out.print("Enter discount percentage: ");
            discountStr = readSafeInput(scanner);
        } while (!Validator.isValidDiscount(discountStr));
        double discount = Double.parseDouble(discountStr);
        BillingStrategy billingStrategy = discount > 0 ? new DiscountBillingStrategy(discount) : new StandardBillingStrategy();

        int subChoice = 0;
        PaymentStrategy paymentStrategy = new CashPaymentStrategy();
        do {
            System.out.println("\n" + DASHES + PAYMENT_MENU + DASHES);
            System.out.println(UPI_PAYMENT);
            System.out.println(CASH_PAYMENT);
            System.out.println(CARD_PAYMENT);
            System.out.println("4. " + GO_BACK_TO_MAIN_MENU);
            System.out.print(ENTER_CHOICE + " (1-4): ");
            subChoice = getUserChoice(scanner);
        } while (!Validator.isValidPaymentChoice(subChoice));

        switch (subChoice) {
            case 1:
                paymentStrategy = new UPIPaymentStrategy();
                break;
            case 2:
                paymentStrategy = new CashPaymentStrategy();
                break;
            case 3:
                paymentStrategy = new CardPaymentStrategy();
                break;
            case 4:
                System.out.println(RETURNING_MAIN_MENU);
                break;
            default:
                System.out.println(INVALID_ENTRY + "1 and 4.");
                break;
        }

        billService.generateBill(patientName, doctorName, consultationAmount, medicineCharges,
                LocalDateTime.now(), billingStrategy, paymentStrategy);

    }

    private static String readSafeInput(Scanner scanner) {
        String input = scanner.nextLine().trim();

        // Globally check for exit strings (case-insensitive)
        if (input.equalsIgnoreCase("exit")) {
            throw new SystemExitException("System exit triggered by user.");
        }
        if (input.equalsIgnoreCase("cancel")) {
            throw new CancelInputException("Aborted, returning to sub-menu");
        }

        return input;
    }
}
