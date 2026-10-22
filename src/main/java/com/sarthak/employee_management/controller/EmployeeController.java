package com.sarthak.employee_management.controller;

import com.sarthak.employee_management.entity.Employee;
import com.sarthak.employee_management.repository.EmployeeRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class EmployeeController {

    private final EmployeeRepository employeeRepository;

    public EmployeeController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/employees";
    }

    @GetMapping("/employees")
    public String listEmployees(
            @RequestParam(required = false) String search,
            Model model) {

        var employees = employeeRepository.findAll();

        if (search != null && !search.isBlank()) {
            String keyword = search.trim().toLowerCase();

            employees = employees.stream()
                    .filter(employee ->
                            (employee.getName() != null &&
                             employee.getName().toLowerCase().contains(keyword))
                            ||
                            (employee.getEmail() != null &&
                             employee.getEmail().toLowerCase().contains(keyword))
                            ||
                            (employee.getDepartment() != null &&
                             employee.getDepartment().toLowerCase().contains(keyword)))
                    .toList();
        }

        model.addAttribute("employees", employees);
        model.addAttribute("search", search == null ? "" : search);
        model.addAttribute("totalEmployees", employeeRepository.count());

        return "employees";
    }

    @GetMapping("/employees/new")
    public String showCreateForm(Model model) {
        model.addAttribute("employee", new Employee());
        model.addAttribute("pageTitle", "Add New Employee");

        return "employee-form";
    }

    @PostMapping("/employees/save")
    public String saveEmployee(
            @ModelAttribute Employee employee,
            RedirectAttributes redirectAttributes) {

        try {
            employeeRepository.save(employee);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Employee saved successfully!");

        } catch (DataIntegrityViolationException exception) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "This email already exists. Please use another email.");

            return employee.getId() == null
                    ? "redirect:/employees/new"
                    : "redirect:/employees/edit/" + employee.getId();
        }

        return "redirect:/employees";
    }

    @GetMapping("/employees/edit/{id}")
    public String showEditForm(
            @PathVariable Long id,
            Model model) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invalid employee ID: " + id));

        model.addAttribute("employee", employee);
        model.addAttribute("pageTitle", "Edit Employee");

        return "employee-form";
    }

    @PostMapping("/employees/delete/{id}")
    public String deleteEmployee(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        if (employeeRepository.existsById(id)) {
            employeeRepository.deleteById(id);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Employee deleted successfully!");
        }

        return "redirect:/employees";
    }
}