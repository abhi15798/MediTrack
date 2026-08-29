package com.airtribe.meditrack;

import com.airtribe.meditrack.entity.*;
import com.airtribe.meditrack.exception.CancelInputException;
import com.airtribe.meditrack.exception.InvalidDataException;
import com.airtribe.meditrack.exception.SystemExitException;
import com.airtribe.meditrack.service.AppointmentService;
import com.airtribe.meditrack.service.BillService;
import com.airtribe.meditrack.service.DoctorService;
import com.airtribe.meditrack.service.PatientService;
import com.airtribe.meditrack.util.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.InputMismatchException;
import java.util.Optional;
import java.util.Scanner;

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

        Scanner scanner = new Scanner(System.in);
        int choice = 0;

        try {
            do {
                displayMainMenu();
                choice = getUserChoice(scanner);
                // Pass the scanner into the execution router to handle sub-menus
                executeMainAction(choice, scanner);
            } while (choice != 5);
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
        System.out.println(EXIT_APPLICATION);
        System.out.print("Please enter your choice" + " (1-5): ");
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
                System.out.println(THANKYOU_GOODBYE);
                break;
            default:
                System.out.println("Error: Invalid entry. Please enter a number between " + "1 and 5.");
                break;
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
            System.out.println(VIEW_DOCTORS);
            System.out.println("3. " + GO_BACK_TO_MAIN_MENU);
            System.out.print(ENTER_CHOICE + " (1-3): ");

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
                    System.out.println(doctorService.findAllDoctors());
                    break;
                case 3:
                    System.out.println(RETURNING_MAIN_MENU);
                    break;
                default:
                    System.out.println(INVALID_ENTRY + "1 and 3.");
                    break;
            }
        } while (subChoice != 3);
    }

    private static void handlePatientManagementSubMenu(Scanner scanner) {
        int subChoice = 0;

        // Sub-menu loop continues until user inputs the 'Go Back' option (3)
        do {
            System.out.println("\n" + DASHES + PATIENT_MANAG_SUBMENU + DASHES);
            System.out.println(CREATE_PATIENT);
            System.out.println(VIEW_PATIENTS);
            System.out.println("3. " + GO_BACK_TO_MAIN_MENU);
            System.out.print(ENTER_CHOICE + " (1-3): ");

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
                    System.out.println(patientService.findAllPatients());
                    break;
                case 3:
                    System.out.println(RETURNING_MAIN_MENU);
                    break;
                default:
                    System.out.println(INVALID_ENTRY + "1 and 3.");
                    break;
            }
        } while (subChoice != 3);
    }

    private static void handleAppointmentManagementSubMenu(Scanner scanner) {
        int subChoice = 0;

        // Sub-menu loop continues until user inputs the 'Go Back' option (3)
        do {
            System.out.println("\n" + DASHES + APPOINTMENT_MANAG_SUBMENU + DASHES);
            System.out.println(CREATE_APPOINTMENT);
            System.out.println(VIEW_APPOINTMENTS);
            System.out.println("3. " + GO_BACK_TO_MAIN_MENU);
            System.out.print(ENTER_CHOICE + " (1-3): ");

            subChoice = getUserChoice(scanner);
            System.out.println();

            switch (subChoice) {
                case 1:
                    scanner.nextLine();
                    try {
                        createAppointment(scanner);
                    } catch (CancelInputException e) {
                        System.out.println(e);
                    }
                    break;
                case 2:
                    System.out.println(appointmentService.findAllAppointments());
                    break;
                case 3:
                    System.out.println(RETURNING_MAIN_MENU);
                    break;
                default:
                    System.out.println(INVALID_ENTRY + "1 and 3.");
                    break;
            }
        } while (subChoice != 3);
    }

    private static void createAppointment(Scanner scanner) {
        String doctorId = null;
        do {
            System.out.print("Enter Doctor ID: ");
            doctorId = readSafeInput(scanner);
        } while (!Validator.isValidDoctorId(doctorId));

        String patientId = null;
        do {
            System.out.print("Enter Patient ID: ");
            patientId = readSafeInput(scanner);
        } while (!Validator.isValidPatientId(patientId));

        appointmentService.bookAppointment(doctorId, patientId);
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

        patientService.createPatient(patientName, contact, dateOfBirth);
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
        Specialization specialization = Specialization.GENERAL;
        do {
            System.out.println("\n" + DASHES + SPECIALIZATION_MENU + DASHES);
            System.out.println(GENERAL_SPECIALIZATION);
            System.out.println(CARDIOLOGY_SPECIALIZATION);
            System.out.println(DERMATOLOGY_SPECIALIZATION);
            System.out.println(ORTHOPEDICS_SPECIALIZATION);
            System.out.println(GENERAL_MEDICINE_SPECIALIZATION);
            System.out.println(PEDIATRICS_SPECIALIZATION);
            System.out.println("7. " + GO_BACK_TO_MAIN_MENU);
            System.out.print(ENTER_CHOICE + " (1-7): ");

            subChoice = getUserChoice(scanner);
            System.out.println();

            switch (subChoice) {
                case 1:
                    specialization = Specialization.GENERAL;
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
                    specialization = Specialization.GENERAL_MEDICINE;
                    break;
                case 6:
                    specialization = Specialization.PEDIATRICS;
                    break;
                case 7:
                    System.out.println(RETURNING_MAIN_MENU);
                    break;
                default:
                    System.out.println(INVALID_ENTRY + "1-7.");
                    break;
            }
        } while (!Validator.isValidPaymentChoice(subChoice));

        scanner.nextLine();
        double consultationCharges = 0.0;
        do {
            System.out.print("Enter Doctor's consultation charges: ");
            try {
                consultationCharges = Double.parseDouble(readSafeInput(scanner));
            } catch (NumberFormatException e) {
                System.out.println(EQUALS + INVALID_NUMBER + e + " " + EQUALS);
            }
        } while (!Validator.isValidAmount(consultationCharges));

        doctorService.createDoctor(doctorName, contact, specialization, consultationCharges);
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

//            double consultationAmount = 0.0;
//            do {
//                System.out.print("Enter consultation charges: ");
//                try {
//                    consultationAmount = Double.parseDouble(readSafeInput(scanner));
//                } catch (NumberFormatException e) {
//                    System.out.println(EQUALS + INVALID_NUMBER + e + " " + EQUALS);
//                }
//            } while (!Validator.isValidAmount(consultationAmount));

            double medicineCharges = 0.0;
            do {
                System.out.print("Enter medicine charges: ");
                try {
                    medicineCharges = Double.parseDouble(readSafeInput(scanner));
                } catch (NumberFormatException e) {
                    System.out.println(EQUALS + INVALID_NUMBER + e + " " + EQUALS);
                }
            } while (!Validator.isValidAmount(medicineCharges));

            int discount = 0;
            do {
                System.out.print("Enter discount percentage: ");
                try {
                    discount = Integer.parseInt(readSafeInput(scanner));
                } catch (NumberFormatException e) {
                    System.out.println(EQUALS + INVALID_NUMBER + e + " " + EQUALS);
                }
            } while (!Validator.isValidDiscount(discount));

            int subChoice = 0;
            Payment paymentMethod = Payment.CASH;
            do {
                System.out.println("\n" + DASHES + PAYMENT_MENU + DASHES);
                System.out.println(UPI_PAYMENT);
                System.out.println(CASH_PAYMENT);
                System.out.println(CARD_PAYMENT);
                System.out.println("4. " + GO_BACK_TO_MAIN_MENU);
                System.out.print(ENTER_CHOICE + " (1-4): ");

                subChoice = getUserChoice(scanner);
                System.out.println();

                switch (subChoice) {
                    case 1:
                        paymentMethod = Payment.UPI;
                        break;
                    case 2:
                        paymentMethod = Payment.CASH;
                        break;
                    case 3:
                        paymentMethod = Payment.CARD;
                    case 4:
                        System.out.println(RETURNING_MAIN_MENU);
                        break;
                    default:
                        System.out.println(INVALID_ENTRY + "1 and 4.");
                        break;
                }
            } while (!Validator.isValidPaymentChoice(subChoice));

            billService.generateBill(patient.getName(), doctor.getName(), doctor.getFee(), medicineCharges,
                    LocalDate.now(), discount, paymentMethod);

        } else {
            System.out.println(EQUALS + " Appointment record for id " + appointmentId + " not found! " + EQUALS);
        }
    }

    private static void generateBill(Scanner scanner) {
//        String patientId = null;
//        Patient patient = null;
//        do {
//            System.out.print("Enter Patient id: ");
//            patientId = readSafeInput(scanner);
//            try {
//                patient = patientService.searchById(patientId);
//            } catch (InvalidDataException e) {
//                System.out.println(EQUALS + " Patient record for id " + patientId + " not found! " + EQUALS);
//                System.out.println(e);
//            }
//        } while (!Validator.isValidPatientId(patientId) || patient == null);

//        String doctorId = null;
//        Doctor doctor = null;
//        do {
//            System.out.print("Enter Doctor id: ");
//            doctorId = readSafeInput(scanner);
//            try {
//                doctor = doctorService.searchById(doctorId);
//            } catch (InvalidDataException e) {
//                System.out.println(EQUALS + " Doctor record for id " + doctorId + " not found! " + EQUALS);
//                System.out.println(e);
//            }
//        } while (!Validator.isValidDoctorId(doctorId) || doctor == null);

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

        double consultationAmount = 0.0;
        do {
            System.out.print("Enter consultation charges: ");
            try {
                consultationAmount = Double.parseDouble(readSafeInput(scanner));
            } catch (NumberFormatException e) {
                System.out.println(EQUALS + INVALID_NUMBER + e + " " + EQUALS);
            }
        } while (!Validator.isValidAmount(consultationAmount));

        double medicineCharges = 0.0;
        do {
            System.out.print("Enter medicine charges: ");
            try {
                medicineCharges = Double.parseDouble(readSafeInput(scanner));
            } catch (NumberFormatException e) {
                System.out.println(EQUALS + INVALID_NUMBER + e + " " + EQUALS);
            }
        } while (!Validator.isValidAmount(medicineCharges));

        int discount = 0;
        do {
            System.out.print("Enter discount percentage: ");
            try {
                discount = Integer.parseInt(readSafeInput(scanner));
            } catch (NumberFormatException e) {
                System.out.println(EQUALS + INVALID_NUMBER + e + " " + EQUALS);
            }
        } while (!Validator.isValidDiscount(discount));

        int subChoice = 0;
        Payment paymentMethod = Payment.CASH;
        do {
            System.out.println("\n" + DASHES + PAYMENT_MENU + DASHES);
            System.out.println(UPI_PAYMENT);
            System.out.println(CASH_PAYMENT);
            System.out.println(CARD_PAYMENT);
            System.out.println("4. " + GO_BACK_TO_MAIN_MENU);
            System.out.print(ENTER_CHOICE + " (1-4): ");

            subChoice = getUserChoice(scanner);
            System.out.println();

            switch (subChoice) {
                case 1:
                    paymentMethod = Payment.UPI;
                    break;
                case 2:
                    paymentMethod = Payment.CASH;
                    break;
                case 3:
                    paymentMethod = Payment.CARD;
                case 4:
                    System.out.println(RETURNING_MAIN_MENU);
                    break;
                default:
                    System.out.println(INVALID_ENTRY + "1 and 4.");
                    break;
            }
        } while (!Validator.isValidPaymentChoice(subChoice));

        billService.generateBill(patientName, doctorName, consultationAmount, medicineCharges,
                LocalDate.now(), discount, paymentMethod);

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
