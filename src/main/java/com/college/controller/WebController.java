package com.college.controller;

import com.college.entity.Event;
import com.college.entity.Coordinator;
import com.college.entity.Student;
import com.college.service.EventService;
import com.college.service.CoordinatorService;
import com.college.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.college.entity.EventRegistration;
import com.college.entity.Payment;
import com.college.entity.Certificate;
import com.college.repository.EventRegistrationRepository;
import com.college.repository.StudentRepository;
import com.college.repository.PaymentRepository;
import com.college.repository.CertificateRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;
import java.util.List;

import java.util.concurrent.ThreadLocalRandom;

@Controller
@RequestMapping("/web")
public class WebController {

    @Autowired
    private EventService eventService;

    @Autowired
    private CoordinatorService coordinatorService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private EventRegistrationRepository registrationRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private CertificateRepository certificateRepository;

    /** Generate a random positive 5-digit ID that is not already in use */
    private int uniqueEventId() {
        int id;
        do {
            id = ThreadLocalRandom.current().nextInt(10000, 99999);
        } while (eventService.getEventById(id) != null);
        return id;
    }

    private int uniqueCoordinatorId() {
        int id;
        do {
            id = ThreadLocalRandom.current().nextInt(1000, 9999);
        } while (coordinatorService.getCoordinatorById(id) != null);
        return id;
    }

    // -------- Home --------
    @GetMapping
    public String index() {
        return "index";
    }

    // -------- Events --------
    @GetMapping("/events")
    public String viewEvents(Model model, Principal principal) {
        model.addAttribute("events", eventService.getAllEvents());
        
        if (principal != null) {
            Student s = studentRepository.findByEmail(principal.getName()).orElse(null);
            if (s != null && s.getRole().equals("PARTICIPANT")) {
                List<Integer> registeredEventIds = registrationRepository.findByStudent(s)
                        .stream().map(reg -> reg.getEvent().getEventId()).toList();
                model.addAttribute("registeredIds", registeredEventIds);
            }
        }
        return "view-events";
    }

    @GetMapping("/events/add")
    public String addEventForm() {
        return "add-event";
    }

    @PostMapping("/events/add")
    public String addEventSubmit(
            @RequestParam("name") String name,
            @RequestParam("type") String type,
            @RequestParam("date") String date,
            @RequestParam("time") String time,
            @RequestParam("venue") String venue,
            @RequestParam("fee") Integer fee,
            RedirectAttributes ra) {
        Event e = new Event();
        e.setEventId(uniqueEventId());
        e.setName(name.trim());
        e.setType(type.trim());
        e.setDate(date.trim());
        e.setTime(time.trim());
        e.setVenue(venue.trim());
        e.setFee(fee);
        eventService.saveEvent(e);
        ra.addFlashAttribute("success", "Event '" + name + "' added successfully!");
        return "redirect:/web/events";
    }

    @GetMapping("/events/delete/{id}")
    public String deleteEvent(@PathVariable("id") Integer id, RedirectAttributes ra) {
        Event e = eventService.getEventById(id);
        if (e != null) {
            eventService.deleteEvent(id);
            ra.addFlashAttribute("success", "Event deleted successfully.");
        } else {
            ra.addFlashAttribute("error", "Event not found.");
        }
        return "redirect:/web/events";
    }

    @GetMapping("/events/edit/{id}")
    public String editEventForm(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        Event e = eventService.getEventById(id);
        if (e == null) {
            ra.addFlashAttribute("error", "Event not found.");
            return "redirect:/web/events";
        }
        model.addAttribute("event", e);
        return "edit-event";
    }

    @PostMapping("/events/edit/{id}")
    public String editEventSubmit(
            @PathVariable("id") Integer id,
            @RequestParam("name") String name,
            @RequestParam("type") String type,
            @RequestParam("date") String date,
            @RequestParam("time") String time,
            @RequestParam("venue") String venue,
            @RequestParam("fee") Integer fee,
            RedirectAttributes ra) {
        Event e = eventService.getEventById(id);
        if (e != null) {
            e.setName(name.trim());
            e.setType(type.trim());
            e.setDate(date.trim());
            e.setTime(time.trim());
            e.setVenue(venue.trim());
            e.setFee(fee);
            eventService.saveEvent(e);
            ra.addFlashAttribute("success", "Event updated successfully.");
        } else {
            ra.addFlashAttribute("error", "Event not found.");
        }
        return "redirect:/web/events";
    }

    // -------- Coordinators --------
    @GetMapping("/coordinators")
    public String viewCoordinators(Model model) {
        model.addAttribute("coordinators", coordinatorService.getAllCoordinators());
        return "view-coordinators";
    }

    @GetMapping("/coordinators/add")
    public String addCoordinatorForm() {
        return "add-coordinator";
    }

    @PostMapping("/coordinators/add")
    public String addCoordinatorSubmit(
            @RequestParam("name") String name,
            @RequestParam("department") String department,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            RedirectAttributes ra) {
        Coordinator c = new Coordinator();
        c.setCoordinatorId(uniqueCoordinatorId());
        c.setName(name.trim());
        c.setDepartment(department.trim());
        c.setEmail(email.trim());
        c.setPhone(phone.trim());
        coordinatorService.saveCoordinator(c);
        ra.addFlashAttribute("success", "Coordinator '" + name + "' added successfully!");
        return "redirect:/web/coordinators";
    }

    @GetMapping("/coordinators/edit/{id}")
    public String editCoordinatorForm(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        Coordinator c = coordinatorService.getCoordinatorById(id);
        if (c == null) {
            ra.addFlashAttribute("error", "Coordinator not found.");
            return "redirect:/web/coordinators";
        }
        model.addAttribute("coordinator", c);
        return "edit-coordinator";
    }

    @PostMapping("/coordinators/edit/{id}")
    public String editCoordinatorSubmit(
            @PathVariable("id") Integer id,
            @RequestParam("name") String name,
            @RequestParam("department") String department,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            RedirectAttributes ra) {
        Coordinator c = coordinatorService.getCoordinatorById(id);
        if (c != null) {
            c.setName(name.trim());
            c.setDepartment(department.trim());
            c.setEmail(email.trim());
            c.setPhone(phone.trim());
            coordinatorService.saveCoordinator(c);
            ra.addFlashAttribute("success", "Coordinator updated successfully.");
        } else {
            ra.addFlashAttribute("error", "Coordinator not found.");
        }
        return "redirect:/web/coordinators";
    }

    @GetMapping("/coordinators/delete/{id}")
    public String deleteCoordinator(@PathVariable("id") Integer id, RedirectAttributes ra) {
        Coordinator c = coordinatorService.getCoordinatorById(id);
        if (c != null) {
            coordinatorService.deleteCoordinator(id);
            ra.addFlashAttribute("success", "Coordinator deleted successfully.");
        } else {
            ra.addFlashAttribute("error", "Coordinator not found.");
        }
        return "redirect:/web/coordinators";
    }

    // -------- Students --------
    @GetMapping("/students")
    public String viewStudents(Model model) {
        model.addAttribute("students", studentService.getAllStudents());
        return "view-students";
    }

    @GetMapping("/students/add")
    public String addStudentForm() {
        return "add-student";
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/students/add")
    public String addStudentSubmit(
            @RequestParam("name") String name,
            @RequestParam("rollNo") String rollNo,
            @RequestParam("department") String department,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            RedirectAttributes ra) {
        Student s = new Student();
        s.setId(ThreadLocalRandom.current().nextInt(100000, 999999));
        s.setName(name.trim());
        s.setRollNo(rollNo.trim());
        s.setDepartment(department.trim());
        s.setEmail(email.trim());
        s.setPassword(passwordEncoder.encode(password.trim()));
        studentService.saveStudent(s);
        ra.addFlashAttribute("success", "Student '" + name + "' registered successfully!");
        return "redirect:/web/students";
    }

    @GetMapping("/students/edit/{id}")
    public String editStudentForm(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        Student s = studentService.getStudentById(id);
        if (s == null) {
            ra.addFlashAttribute("error", "Student not found.");
            return "redirect:/web/students";
        }
        model.addAttribute("student", s);
        return "edit-student";
    }

    @PostMapping("/students/edit/{id}")
    public String editStudentSubmit(
            @PathVariable("id") Integer id,
            @RequestParam("name") String name,
            @RequestParam("rollNo") String rollNo,
            @RequestParam("department") String department,
            @RequestParam("email") String email,
            RedirectAttributes ra) {
        Student s = studentService.getStudentById(id);
        if (s != null) {
            s.setName(name.trim());
            s.setRollNo(rollNo.trim());
            s.setDepartment(department.trim());
            s.setEmail(email.trim());
            studentService.saveStudent(s);
            ra.addFlashAttribute("success", "Student record updated.");
        } else {
            ra.addFlashAttribute("error", "Student not found.");
        }
        return "redirect:/web/students";
    }

    @GetMapping("/students/delete/{id}")
    public String deleteStudent(@PathVariable("id") Integer id, RedirectAttributes ra) {
        studentService.deleteStudent(id);
        ra.addFlashAttribute("success", "Student removed successfully.");
        return "redirect:/web/students";
    }

    // -------- Assignments --------
    @GetMapping("/assignments")
    public String viewAssignments(Model model) {
        model.addAttribute("events", eventService.getAllEvents());
        model.addAttribute("coordinators", coordinatorService.getAllCoordinators());
        model.addAttribute("assignments", coordinatorService.getCoordinatorAssignments());
        return "assign-coordinator";
    }

    @PostMapping("/assignments/add")
    public String addAssignment(
            @RequestParam("coordinatorId") Integer coordinatorId,
            @RequestParam("eventId") Integer eventId,
            RedirectAttributes ra) {
        coordinatorService.assignCoordinatorToEvent(coordinatorId, eventId);
        ra.addFlashAttribute("success", "Assignment created successfully.");
        return "redirect:/web/assignments";
    }

    // -------- Participant Actions --------
    @GetMapping("/my-events")
    public String viewMyEvents(Model model, Principal principal) {
        Student s = studentRepository.findByEmail(principal.getName()).orElse(null);
        if (s != null) {
            model.addAttribute("registrations", registrationRepository.findByStudent(s));
            model.addAttribute("student", s);
        }
        return "my-events";
    }

    @GetMapping("/events/join/{id}")
    public String joinEventForm(@PathVariable("id") Integer id, Model model, Principal principal, RedirectAttributes ra) {
        Event e = eventService.getEventById(id);
        Student s = studentRepository.findByEmail(principal.getName()).orElse(null);
        
        if (e == null || s == null) return "redirect:/web/events";

        // Check if already registered
        if (registrationRepository.findByStudentAndEvent_EventId(s, id).isPresent()) {
            ra.addFlashAttribute("error", "You are already registered for this event.");
            return "redirect:/web/my-events";
        }

        model.addAttribute("event", e);
        model.addAttribute("student", s);
        return "register-event";
    }

    @PostMapping("/events/join/{id}")
    public String joinEventSubmit(@PathVariable("id") Integer id, Principal principal, RedirectAttributes ra) {
        Event e = eventService.getEventById(id);
        Student s = studentRepository.findByEmail(principal.getName()).orElse(null);

        if (e != null && s != null) {
            // 1. Create Registration
            EventRegistration reg = new EventRegistration();
            reg.setStudent(s);
            reg.setEvent(e);
            reg.setStatus("PAID");
            registrationRepository.save(reg);

            // 2. Create Payment Record (New!)
            Payment p = new Payment();
            p.setStudent(s);
            p.setEvent(e);
            p.setAmount(e.getFee());
            p.setPaymentStatus("SUCCESS");
            p.setPaymentType("ONLINE");
            paymentRepository.save(p);

            ra.addFlashAttribute("success", "Successfully registered for " + e.getName());
        }
        return "redirect:/web/my-events";
    }

    // -------- Admin Certificate Actions --------
    @GetMapping("/admin/registrations")
    public String viewAllRegistrations(Model model) {
        model.addAttribute("registrations", registrationRepository.findAll());
        return "admin-registrations";
    }

    @PostMapping("/admin/certify/{regId}")
    public String issueCertificate(
            @PathVariable("regId") Long regId,
            @RequestParam("type") String type,
            RedirectAttributes ra) {
        EventRegistration reg = registrationRepository.findById(regId).orElse(null);
        if (reg != null) {
            // 1. Update Registration
            reg.setCertificateType(type);
            reg.setStatus("COMPLETED");
            registrationRepository.save(reg);

            // 2. Create Certificate Record (New!)
            Certificate cert = new Certificate();
            cert.setStudent(reg.getStudent());
            cert.setEvent(reg.getEvent());
            cert.setType(type);
            certificateRepository.save(cert);

            ra.addFlashAttribute("success", "Certificate issued: " + type + " for " + reg.getStudent().getName());
        }
        return "redirect:/web/admin/registrations";
    }

    // -------- API Test Results --------
    @GetMapping("/api-tests")
    public String viewApiTests(Model model) {
        model.addAttribute("executionTime", java.time.LocalDateTime.now());
        return "api-tests";
    }
}
