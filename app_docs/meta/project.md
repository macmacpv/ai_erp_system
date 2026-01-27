# Project Profile

## 1. Project Owner
*   **Name:** RepoRevolution | MineCop
*   **Role:** Product Owner / Lead Developer
*   **Technical Background:**
    *   Senior Fullstack Java Developer context.

## 2. Project Vision
*   **Project Name:** lssd_cad_erp
*   **Type:** Modular Sheriff's Department Management System (ERP, CAD, MDT)
*   **Description:**
    *   A comprehensive, modular system for managing LSSD operations.
    *   **Modules:**
        1.  **ERP (Enterprise Resource Planning):** Management of employees, resources (hiring, training, equipment, vehicles). **(MVP Priority)**
        2.  **CAD (Computer Aided Dispatch):** Dispatching and incident management.
        3.  **MDT (Mobile Data Terminal):** Officer dashboard for field data.
    *   **Structure:**
        *   **Landing Page:** Public view.
        *   **Login:** Classic credentials (login/password).
        *   **Dashboard:** Authorized access only.
    *   **Security:**
        *   **Root Account:** superadmin - initializes the system; undeletable, hidden from lists, full permissions.
        *   **ACE Perms:** complex permission system (not RBAC). Users have individual permissions + multiple permission groups (e.g., Admin Dept, Recruitment Office) + rank permission group (e.g., Sergeant).
    *   **Development Rules:**
        *   MDA/DCA must not invent features alone; strict adherence to FCA consultations.

## 3. Technical Requirements
*   **Core Stack:**
    *   **Language:** Java 21
    *   **Framework:** Spring Boot 3.5.8
    *   **UI Framework:** Vaadin Java 24.9.7
    *   **Build Tool:** Maven 4.0.0
    *   **Database:** PostgreSQL (with Flyway for migration) - separate Docker container.
*   **Infrastructure:**
    *   **Dockerization:** Full app in Docker container.
    *   **Project Structure:** `./app/` (Dockerfile context), `./docker-compose.yml`.
*   **Code Quality:**
    *   Senior Fullstack Java Developer standard.
    *   Advanced, future-proof code.
    *   No temporary solutions.

## 4. AI Interaction Guidelines
*   **Preferred Coding Style:** Senior Java, Clean Code, Solid Principles.
*   **Documentation Standards:** Markdown files, JavaDoc.
