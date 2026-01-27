# Technical Vision & Roadmap

## 1. Architectural Overview
* **Pattern:** Modular Monolith (Reactive).
    * Single deployable unit (Spring Boot / Docker).
    * Distinct modules (ERP, CAD, MDT, Core) communicating via Java Interfaces.
* **Real-Time Core (Global):**
    * **Technology:** Vaadin `@Push` (WebSocket) enabled globally.
    * **Mechanism:** **Broadcaster / Event Bus Pattern**. Logic: *Action -> Database Commit -> ApplicationEvent -> Broadcaster -> All subscribed UIs update*.
    * **Scope:** Changes in ERP (Roster, Inventory) and Core (Permissions) are propagated instantly to all active clients, just like CAD incidents.
* **Frontend Approach:** Server-Side Rendering with **Vaadin Flow**.
* **Database:** Single PostgreSQL instance with logical separation.

## 2. Core Modules & Features

### 2.1. Core Module (The "Kernel")
* **Responsibility:** Authentication, Authorization (ACE), Navigation, Logging, Audit, Global Configuration.
* **The "ACE" Permission System (Real-Time):**
    * **Logic:** `Effective_Permissions = Union(Rank_Permissions, Group_Permissions[], User_Specific_Permissions)`.
    * **Dynamic UI Updates:** If an admin revokes a permission, the corresponding UI elements (buttons, menu items) must disappear from the target user's screen **immediately** without a page refresh.
    * **Superadmin:** Hardcoded `root` account.
* **UI Structure:**
    * `MainLayout`: Sidebar navigation (updates in real-time).
    * `LandingView`: Public info.
    * `LoginView`: Standard credentials.

### 2.2. ERP Module (MVP Priority & Real-Time)
* **Focus:** Human Resources & Logistics.
* **Real-Time Collaboration:** Multiple admins working on the roster or inventory see each other's changes instantly.
* **Sub-components:**
    * **Personnel File:** Personal info, service history.
    * **Roster Management:** Live hierarchy visualization.
    * **Training:** Certifications.
    * **Asset Management:** Vehicles, weaponry (Inventory). Item availability updates instantly across the department.

### 2.3. CAD Module (Phase 2)
* **Focus:** Real-time Dispatching.
* **Key Features:**
    * Call creation and management.
    * Unit status tracking (10-8, 10-7).
    * Live map integration (optional future feature).

### 2.4. MDT Module (Phase 2)
* **Focus:** Field Officer Interface.
* **Key Features:**
    * Simplified, touch-friendly UI.
    * Lookup System.
    * Report writing.

## 3. Data Model Strategy (PostgreSQL)

### Identity & Access (Core)
* `users`: id, username, password_hash, rank_id, is_active.
* `ranks`: id, name, weight (hierarchy).
* `permission_groups`: id, name, type.
* `permissions`: id, node_string (e.g., `erp.hiring.create`).
* `user_groups`: link table.
* `rank_permissions`: link table.
* `group_permissions`: link table.
* `user_permissions`: link table.

### ERP Data
* `employees`: link to user, badge_number, hire_date.
* `assets`: id, type, serial_number, assigned_to (user_id).

## 4. Technology Stack Specifications
* **Java:** 21 (LTS).
* **Spring Boot:** 3.5.8.
* **Vaadin:** 24.9.7 (Flow) with **Push** enabled.
* **DB:** PostgreSQL 16+ (Dockerized).
* **Migration:** Flyway.
* **Build:** Maven 4.0.0.

## 5. Development Roadmap

### Phase 1: The Foundation (Current)
1.  **Skeleton:** Maven setup, Multi-module package structure.
2.  **Real-Time Infrastructure:** Implement `Broadcaster` service and Event Listeners.
3.  **Security Core:** ACE Permission Evaluator.
4.  **Base UI:** Landing Page, Login, Main Dashboard.

### Phase 2: ERP (MVP Target)
1.  **User Management:** Real-time CRUD.
2.  **Permissions UI:** Real-time ACE configuration.
3.  **Roster View:** Live list of employees.

### Phase 3: CAD & MDT (Future)
1.  Incident Management.
2.  Status updates.

## 6. Design & UX Guidelines (DCA Scope)
* **Theme:** "Professional, Dark Mode preferred (LSSD style). Human Pilot add styles.css after project setup.".
* **Responsiveness:** Desktop/Tablet/Mobile (ERP/CAD/MDT).
* **Feedback:** UI must clearly indicate "Live" status or updates via toasts/notifications.